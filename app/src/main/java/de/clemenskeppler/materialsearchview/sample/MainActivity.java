package de.clemenskeppler.materialsearchview.sample;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.clemenskeppler.materialsearchview.MaterialSearchView;

public class MainActivity extends AppCompatActivity {

  MaterialSearchView materialSearchView;
  SearchView searchView;
  RecyclerView results;
  SearchAdapter searchAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    materialSearchView = findViewById(R.id.materialSearchView);
    searchView = materialSearchView.getSearchView();
    results = materialSearchView.getSearchResults();
    ArrayList<String> previousSearches = new ArrayList<>(3);
    previousSearches.add("Green");
    previousSearches.add("Red");
    previousSearches.add("Blue");
    searchAdapter = new SearchAdapter(this, previousSearches);
    results.setAdapter(searchAdapter);
    results.setLayoutManager(new LinearLayoutManager(this));
    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
      @Override public boolean onQueryTextSubmit(String query) {
        searchAdapter.add(query);
        return true;
      }

      @Override public boolean onQueryTextChange(String newText) {
        return false;
      }
    });
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.action_filter_search) {
      materialSearchView.show();
      materialSearchView.show();
      materialSearchView.show();
      materialSearchView.show();
      materialSearchView.show();
      materialSearchView.show();
      materialSearchView.show();
      materialSearchView.show();
      materialSearchView.show();
      return true;
    } else {
      return super.onOptionsItemSelected(item);
    }
  }
}
