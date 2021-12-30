package ch.killenberger.wowauctionhousebrowser;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
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
import ch.killenberger.wowauctionhousebrowser.service.ItemClassService;
import ch.killenberger.wowauctionhousebrowser.service.ItemClassUpdateService;
import ch.killenberger.wowauctionhousebrowser.service.ItemMediaService;
import ch.killenberger.wowauctionhousebrowser.service.ItemMediaUpdateService;
import ch.killenberger.wowauctionhousebrowser.service.ItemService;
import ch.killenberger.wowauctionhousebrowser.service.ItemSubClassService;
import ch.killenberger.wowauctionhousebrowser.service.ItemSubClassUpdateService;
import ch.killenberger.wowauctionhousebrowser.service.ItemUpdateService;
import ch.killenberger.wowauctionhousebrowser.service.OAuth2Service;
import ch.killenberger.wowauctionhousebrowser.service.RealmService;
import ch.killenberger.wowauctionhousebrowser.sqlite.DatabaseHelper;
import ch.killenberger.wowauctionhousebrowser.util.AlertUtil;

public class MainActivity extends AppCompatActivity {
    private static final String DB_DIR  = "databases";
    private static final String DB_NAME = "WOWAUCTIONBROWSER";

    private AutoCompleteTextView realmInput;
    private Spinner              regionSpinner;
    private Button searchButton;
    private Button downloadImagesButton;

    private UserSettings        userSettings        = UserSettings.getInstance();
    private ApplicationSettings applicationSettings = ApplicationSettings.getInstance();
    private List<Realm>         realms              = new ArrayList<>();

    private boolean initialization = true;

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

        this.regionSpinner         = findViewById(R.id.regionSpinner);
        this.realmInput            = findViewById(R.id.realmInput);
        this.searchButton          = findViewById(R.id.searchButton);
        this.downloadImagesButton  = findViewById(R.id.downloadImagesButton);

        // SETUP ADAPTERS
        this.realmInput.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, realms));
        this.regionSpinner.setAdapter(new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, Region.values()));

        // SETUP LISTENERS
        this.regionSpinner.setOnItemSelectedListener(createItemSelectedListener());
        this.searchButton.setOnClickListener(createSearchOnClickListener());
        this.downloadImagesButton.setOnClickListener(createDownloadImagesOnClickListener());

        // CREATE API ACCESS TOKEN
        try {
            new OAuth2Service(this).execute().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        // NOTIFY USER IF AUTHENTICATION HAS FAILED
        if(ApplicationSettings.getInstance().getAccessToken() == null) {
            AlertUtil.createAlertDialog(this, "Authorization", "Unable to authorize against the Blizzard API.\nPlease restart the application!", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                    System.exit(0);
                }
            });
        } else {
            try {
                fetchDataIfNecessary();
            } catch (ExecutionException | InterruptedException e) {
                return;
            }
        }

        this.initialization = false;
    }

    private void fetchDataIfNecessary() throws ExecutionException, InterruptedException {
        final boolean itemClassUpdateRequired = new ItemClassUpdateService(this).execute().get();
        if(itemClassUpdateRequired) {
            fetchItemClasses();
        }

        final boolean itemSubClassUpdateRequired = new ItemSubClassUpdateService(this).execute().get();
        if(itemSubClassUpdateRequired) {
            fetchItemSubClasses();
        }

        final boolean itemUpdateRequired = new ItemUpdateService(this).execute().get();
        if(itemUpdateRequired) {
            fetchItems();
        }
    }

    private void fetchItemClasses() {
        System.out.println("Fetching Item Classes...");
        new ItemClassService(this).execute();
    }

    private void fetchItemSubClasses() {
        System.out.println("Fetching Item SubClasses...");
        new ItemSubClassService(this).execute();
    }

    private void fetchItems() {
        System.out.println("Fetching Items...");
        new ItemService(this).execute();
    }

    private void fetchItemMedia() {
        System.out.println("Fetching Item Media...");
        new ItemMediaService(this).execute();
    }

    private AdapterView.OnItemSelectedListener createItemSelectedListener() {
       return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(initialization) {
                    return;
                }

                userSettings.setRegion((Region) parent.getItemAtPosition(position));

                try {
                    realms = new RealmService(MainActivity.this).execute().get();
                    realmInput.setAdapter(new ArrayAdapter<Realm>(MainActivity.this, android.R.layout.simple_dropdown_item_1line, realms));
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
    }

    private View.OnClickListener createSearchOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String selectedRealm = realmInput.getText().toString();

                for(Realm r : realms) {
                    if(r.getName().equals(selectedRealm)) {
                        userSettings.setRealm(r);

                        try {
                            userSettings.setConnectedRealmId(new ConnectedRealmService(MainActivity.this).execute().get());

                            Intent intent = new Intent(getBaseContext(), AuctionsActivity.class);
                            startActivity(intent);
                        } catch (ExecutionException | InterruptedException e) {
                            AlertUtil.createAlertDialog(MainActivity.this, "Oops", getString(R.string.connection_failed_error_msg));
                        }

                        return;
                    }
                }

                AlertUtil.createAlertDialog(MainActivity.this, "Invalid Realm", "You've entered an invalid realm.\nPlease select a realm from the dropdown.");
            }
        };
    }

    private View.OnClickListener createDownloadImagesOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final boolean itemMediaUpdateRequired = new ItemMediaUpdateService().execute().get();
                if(itemMediaUpdateRequired) {
                    fetchItemMedia();
                }
            }
        };
    }

    public void checkPermissions() {
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
        final File dbFolder = new File(appDataPath + "/" + DB_DIR);
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