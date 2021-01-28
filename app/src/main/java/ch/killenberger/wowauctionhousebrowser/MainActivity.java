package ch.killenberger.wowauctionhousebrowser;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;

import androidx.navigation.ui.AppBarConfiguration;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import ch.killenberger.wowauctionhousebrowser.enums.Region;
import ch.killenberger.wowauctionhousebrowser.model.ApplicationSettings;
import ch.killenberger.wowauctionhousebrowser.model.Realm;
import ch.killenberger.wowauctionhousebrowser.model.UserSettings;
import ch.killenberger.wowauctionhousebrowser.service.ConnectedRealmService;
import ch.killenberger.wowauctionhousebrowser.service.ItemClassService;
import ch.killenberger.wowauctionhousebrowser.service.ItemService;
import ch.killenberger.wowauctionhousebrowser.service.ItemSubClassService;
import ch.killenberger.wowauctionhousebrowser.service.OAuth2Service;
import ch.killenberger.wowauctionhousebrowser.service.RealmService;
import ch.killenberger.wowauctionhousebrowser.sqlite.DatabaseHelper;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private AutoCompleteTextView realmInput;
    private Spinner              regionSpinner;
    private Button searchButton;

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

        try {
            ApplicationSettings.getInstance().setAccessToken(new OAuth2Service().execute().get());
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        this.regionSpinner = findViewById(R.id.regionSpinner);
        this.realmInput    = findViewById(R.id.realmInput);
        this.searchButton  = findViewById(R.id.searchButton);
        this.adapter  = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, realms);

        realmInput.setAdapter(adapter);

        this.regionSpinner.setAdapter(new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, Region.values()));
        this.regionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
        });

        this.searchButton.setOnClickListener(new View.OnClickListener() {
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
        });

/*        DatabaseHelper db = new DatabaseHelper(getApplicationContext());
        db.resetDatabase();
        db.close();

        try {
            fetchData();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }*/
    }

    private void fetchData() throws ExecutionException, InterruptedException {
        fetchItemClasses();
        fetchItemSubClasses();
        fetchItems();
        System.out.println("Finished importing data!");
    }

    private void fetchItemClasses() {
        System.out.println("Creating Item Classes...");
        new ItemClassService().execute();
    }

    private void fetchItemSubClasses() throws ExecutionException, InterruptedException {
        System.out.println("Creating Item SubClasses...");
        new ItemSubClassService().execute();
    }

    public void fetchItems() throws ExecutionException, InterruptedException {
        System.out.println("Creating Items...");
        new ItemService().execute();
    }
}