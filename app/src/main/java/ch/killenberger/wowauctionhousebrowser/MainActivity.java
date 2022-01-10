package ch.killenberger.wowauctionhousebrowser;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
    private AutoCompleteTextView realmInput;

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

        while(!checkPermissions()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("Waiting for permissions");
        }

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        dbHelper.initializeDataBase();

        final Spinner regionSpinner = findViewById(R.id.regionSpinner);
        final Button  searchButton  = findViewById(R.id.searchButton);
        this.realmInput             = findViewById(R.id.realmInput);

        // SETUP ADAPTERS
        this.realmInput.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, realms));
        regionSpinner.setAdapter(new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, Region.values()));

        // SETUP LISTENERS
        regionSpinner.setOnItemSelectedListener(createItemSelectedListener());
        searchButton.setOnClickListener(createSearchOnClickListener());

        // CREATE API ACCESS TOKEN
        try {
            new OAuth2Service(this).execute().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        // NOTIFY USER IF AUTHENTICATION HAS FAILED
        if(ApplicationSettings.getInstance().getAccessToken() == null) {
            AlertUtil.createAlertDialog(this, "Authorization", "Unable to authorize against the Blizzard API.\nPlease restart the application!", (dialog, which) -> {
                finish();
                System.exit(0);
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

        final boolean itemMediaUpdateRequired = new ItemMediaUpdateService().execute().get();
        if(itemMediaUpdateRequired) {
            fetchItemMedia();
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
        return v -> {
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
        };
    }

    private boolean checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

            return false;
        }

        return true;
    }
}