package de.kaiwidmaier.suggestamovie.activities;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import de.kaiwidmaier.suggestamovie.R;
import de.kaiwidmaier.suggestamovie.rest.ResultType;
import de.kaiwidmaier.suggestamovie.utils.NetworkUtils;

public class SearchActivity extends AppCompatActivity {

  EditText editText;
  FloatingActionButton fabSearch;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_search);

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    editText = findViewById(R.id.edittext_search);
    fabSearch = findViewById(R.id.fab_search);
    fabSearch.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (editText.getText().length() > 0) {
          if (NetworkUtils.isNetworkAvailable(SearchActivity.this)) {
            Intent intent = new Intent(SearchActivity.this, ResultActivity.class);
            intent.putExtra("resultType", ResultType.SEARCH);
            intent.putExtra("searchString", editText.getText().toString());
            intent.putExtra("resultTitle", getString(R.string.search));
            intent.putExtra("resultDescr", String.format(getString(R.string.search_descr), editText.getText().toString()));
            startActivity(intent);
          } else {
            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), getString(R.string.unable_connect), Snackbar.LENGTH_SHORT);
            snackbar.show();
          }
        } else {
          Snackbar.make(findViewById(android.R.id.content), getString(R.string.input_empty), Snackbar.LENGTH_SHORT).show();
        }
      }
    });
  }

  public boolean onOptionsItemSelected(MenuItem item){
    onBackPressed();
    return true;
  }

}
