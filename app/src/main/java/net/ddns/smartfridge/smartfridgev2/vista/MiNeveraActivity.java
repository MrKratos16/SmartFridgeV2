package net.ddns.smartfridge.smartfridgev2.vista;

import android.app.SearchManager;
import android.content.Context;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.Toast;

import net.ddns.smartfridge.smartfridgev2.R;
import net.ddns.smartfridge.smartfridgev2.modelo.CustomRecyclerViewAdapter;
import net.ddns.smartfridge.smartfridgev2.persistencia.AlimentoDB;

public class MiNeveraActivity extends AppCompatActivity {
    private AlimentoDB alimentoDB;
    private Cursor cursor;
    private RecyclerView rvMiNevera;
    private CustomRecyclerViewAdapter recyclerViewAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mi_nevera);

        alimentoDB = new AlimentoDB(this);
        cursor = alimentoDB.getAlimentos();

        iniciarRecyclerView();


    }

    private void iniciarRecyclerView(){
        rvMiNevera = (RecyclerView)findViewById(R.id.rvMiNevera);
        layoutManager = new LinearLayoutManager(this);
        recyclerViewAdapter = new CustomRecyclerViewAdapter(cursor, this);

        rvMiNevera.setLayoutManager(layoutManager);
        rvMiNevera.setAdapter(recyclerViewAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.menuSearch).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconified(true);
        searchView.onActionViewCollapsed();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                recyclerViewAdapter.filter(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                    recyclerViewAdapter.filter(s);
                return false;
            }
        });
        return true;
    }
}
