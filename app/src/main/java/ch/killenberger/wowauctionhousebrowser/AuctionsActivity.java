package ch.killenberger.wowauctionhousebrowser;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import ch.killenberger.wowauctionhousebrowser.model.item.ItemClass;
import ch.killenberger.wowauctionhousebrowser.model.item.ItemSubClass;
import ch.killenberger.wowauctionhousebrowser.service.AuctionHouseService;
import ch.killenberger.wowauctionhousebrowser.sqlite.DatabaseHelper;
import ch.killenberger.wowauctionhousebrowser.ui.AuctionsAdapter;

public class AuctionsActivity extends AppCompatActivity {
    private static final String ALL_FILTER_STRING = "All";

    private RecyclerView      recyclerView;
    private Spinner           classSpinner;
    private Spinner           subClassSpinner;
    private Button            filterButton;
    private TextInputEditText nameSearchInput;

    private ArrayAdapter<ItemClass>    classAdapter;
    private ArrayAdapter<ItemSubClass> subClassAdapter;

    private ItemClass iClass;
    private ItemSubClass iSubClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auctions);

        this.recyclerView    = findViewById(R.id.acutionsRecyclerView);
        this.classSpinner    = findViewById(R.id.classSpinner);
        this.subClassSpinner = findViewById(R.id.subClassSpinner);
        this.filterButton    = findViewById(R.id.auctionFilterButton);
        this.nameSearchInput = findViewById(R.id.filterItemNameInput);

        // SETUP ADAPTERS

        final List<ItemClass> classes = loadClasses();
        this.classAdapter    = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, classes);
        this.subClassAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<>());

        this.classSpinner.setAdapter(classAdapter);
        this.classSpinner.setOnItemSelectedListener(createClassSelectedListener());

        this.subClassSpinner.setAdapter(subClassAdapter);
        this.subClassSpinner.setOnItemSelectedListener(createSubClassSelectedListener());

        this.filterButton.setOnClickListener(createFilterClickListener());

        new AuctionHouseService(this, recyclerView).execute();
    }

    private List<ItemClass> loadClasses() {
        final DatabaseHelper db = new DatabaseHelper(this);

        final List<ItemClass> classes = new ArrayList<>();
        classes.add(new ItemClass(-1, ALL_FILTER_STRING));
        classes.addAll(db.getItemClasses());

        db.close();

        return classes;
    }

    private List<ItemSubClass> loadSubClassesByParent(final int id) {
        final DatabaseHelper db = new DatabaseHelper(this);

        final List<ItemSubClass> subClasses = db.getSubClassesByParentId(id);

        db.close();

        return subClasses;
    }

    private AdapterView.OnItemSelectedListener createClassSelectedListener() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                iClass = (ItemClass) parent.getItemAtPosition(position);

                subClassAdapter.clear();
                subClassAdapter.add(new ItemSubClass(iClass.getId(), -1, ALL_FILTER_STRING));
                subClassAdapter.addAll(loadSubClassesByParent(iClass.getId()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
    }

    private AdapterView.OnItemSelectedListener createSubClassSelectedListener() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                iSubClass = (ItemSubClass) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
    }

    private View.OnClickListener createFilterClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuctionsAdapter auctionsAdapter = (AuctionsAdapter) recyclerView.getAdapter();
                final String name = nameSearchInput.getText().toString();

                auctionsAdapter.filter(name, iClass.getId(), iSubClass.getId());
            }
        };
    }
}