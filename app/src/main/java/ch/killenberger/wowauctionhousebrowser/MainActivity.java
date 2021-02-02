package ch.killenberger.wowauctionhousebrowser;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import ch.killenberger.wowauctionhousebrowser.enums.Region;
import ch.killenberger.wowauctionhousebrowser.model.ApplicationSettings;
import ch.killenberger.wowauctionhousebrowser.model.Realm;
import ch.killenberger.wowauctionhousebrowser.model.UserSettings;
import ch.killenberger.wowauctionhousebrowser.service.ConnectedRealmService;
import ch.killenberger.wowauctionhousebrowser.service.ItemClassUpdateService;
import ch.killenberger.wowauctionhousebrowser.service.ItemClassService;
import ch.killenberger.wowauctionhousebrowser.service.ItemService;
import ch.killenberger.wowauctionhousebrowser.service.ItemSubClassService;
import ch.killenberger.wowauctionhousebrowser.service.ItemSubClassUpdateService;
import ch.killenberger.wowauctionhousebrowser.service.ItemUpdateService;
import ch.killenberger.wowauctionhousebrowser.service.OAuth2Service;
import ch.killenberger.wowauctionhousebrowser.service.RealmService;
import ch.killenberger.wowauctionhousebrowser.sqlite.DatabaseHelper;

public class MainActivity extends AppCompatActivity {
    private static final String DB_DIR  = "databases";
    private static final String DB_NAME = "WOWAUCTIONBROWSER";

    private AutoCompleteTextView realmInput;
    private Spinner              regionSpinner;
    private Button searchButton;
    private Button resetButton;

    private UserSettings        userSettings        = UserSettings.getInstance();
    private ApplicationSettings applicationSettings = ApplicationSettings.getInstance();
    private List<Realm>         realms              = new ArrayList<>();
    private ArrayAdapter<Realm> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        applicationSettings.setApplicationContext(getApplicationContext());
        userSettings.setRegion(Region.US);

        checkPermissions();

        if(!databaseExists()) {
            copyDatabse();
        }

        // CREATE API ACCESS TOKEN
        try {
            ApplicationSettings.getInstance().setAccessToken(new OAuth2Service().execute().get());
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        try {
            fetchDataIfNecessary();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        this.regionSpinner = findViewById(R.id.regionSpinner);
        this.realmInput    = findViewById(R.id.realmInput);
        this.searchButton  = findViewById(R.id.searchButton);
        this.resetButton  = findViewById(R.id.resetDatabaseButton);
        this.adapter       = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, realms);

        // SETUP ADAPTERS
        this.realmInput.setAdapter(adapter);
        this.regionSpinner.setAdapter(new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, Region.values()));
        this.regionSpinner.setOnItemSelectedListener(createItemSelectedListener());

        this.searchButton.setOnClickListener(createRealmOnClickListener());
        this.resetButton.setOnClickListener(createResetDbnClickListener());
    }

    private void fetchDataIfNecessary() throws ExecutionException, InterruptedException {
        final boolean itemClassUpdateRequired = new ItemClassUpdateService().execute().get();
        if(itemClassUpdateRequired) {
            fetchItemClasses();
        }

        final boolean itemSubClassUpdateRequired = new ItemSubClassUpdateService().execute().get();
        if(itemSubClassUpdateRequired) {
            fetchItemSubClasses();
        }

        final boolean itemUpdateRequired = new ItemUpdateService().execute().get();
        if(itemUpdateRequired) {
            fetchItems();
        }
    }

    private void fetchItemClasses() throws ExecutionException, InterruptedException {
        System.out.println("Creating Item Classes...");
        new ItemClassService().execute().get();
    }

    private void fetchItemSubClasses() throws ExecutionException, InterruptedException {
        System.out.println("Creating Item SubClasses...");
        new ItemSubClassService().execute().get();
    }

    private void fetchItems() {
        System.out.println("Creating Items...");
        new ItemService(this).execute();
    }

    private AdapterView.OnItemSelectedListener createItemSelectedListener() {
       return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                userSettings.setRegion((Region) parent.getItemAtPosition(position));

                try {
                    realms = new RealmService().execute().get();

                    adapter.clear();
                    adapter.addAll(realms);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
    }

    private View.OnClickListener createRealmOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String selectedRealm = realmInput.getText().toString();

                for(Realm r : realms) {
                    if(r.getName().equals(selectedRealm)) {
                        userSettings.setRealm(r);

                        try {
                            userSettings.setConnectedRealmId(new ConnectedRealmService().execute().get());
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        break;
                    }
                }

                Intent intent = new Intent(getBaseContext(), AuctionsActivity.class);
                startActivity(intent);
            }
        };
    }

    private View.OnClickListener createResetDbnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseHelper db = new DatabaseHelper(MainActivity.this);
                db.resetDatabase();
                db.close();;

                try {
                    fetchDataIfNecessary();
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    public void checkPermissions() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }

    private boolean databaseExists() {
        return new File(this.getApplicationInfo().dataDir + "/" + DB_DIR + "/" + DB_NAME).exists();
    }

    private void copyDatabse() {
        final String appDataPath  = this.getApplicationInfo().dataDir;

        //Make sure the /databases folder exists
        File dbFolder = new File(appDataPath + "/" + DB_DIR);
        dbFolder.mkdir();//This can be called multiple times.

        File dbFilePath = new File(appDataPath + "/" + DB_DIR + "/" + DB_NAME);

        try (InputStream  is = this.getAssets().open(DB_NAME)) {
            try (OutputStream os = new FileOutputStream(dbFilePath)) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) > 0) {
                    os.write(buffer, 0, length);
                }

                os.flush();
            }
        } catch (IOException e){
            Log.e("DATABASE", "FAILED TO COPY DATABAE TO DATA DIR");
        }
    }
}