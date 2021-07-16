package com.example.pets;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.pets.data.PetContract.PetEntry;
import com.example.pets.data.PetDbHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, android.app.LoaderManager.LoaderCallbacks<D> {

    private static final int PET_LOADER = 0;

    PetCursorAdapter mCursonAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // Find the Listview which will be populated with the pet data
        ListView petListView = (ListView) findViewById(R.id.list);

        // Find and set EmptyView on the ListView, so that it only shows when the list has 0 item.
        View emptyView = findViewById(R.id.empty_view);
        petListView.setEmptyView(emptyView);

        // Setup an adapter to create a list item for each row of pet data in the cursor
        // There is no pet data yet (until the loader finishes) so pass in nul for the cursor
        mCursonAdapter = new PetCursorAdapter(this, null);
        petListView.setAdapter(mCursonAdapter);

        // Kick off the loader
        getLoaderManager().initLoader(PET_LOADER, null, this);
    }

    /**
     * Helper method to insert hardcoded pet data into the database. For debugging purposes only.
     */
    private void insertPet(){

        // Create a ContentValues object where column names are the keys,
        // and Toto's pet attributes are the values.
        ContentValues values = new ContentValues();
        values.put(PetEntry.COLUMN_PET_NAME, "Toto");
        values.put(PetEntry.COLUMN_PET_BREED, "Terrier");
        values.put(PetEntry.COLUMN_PET_GENDER, PetEntry.GENDER_MALE);
        values.put(PetEntry.COLUMN_PET_WEIGHT, 7);

        // Insert a new row for Toto into the provider using the ContentResolver.
        // Use the {@link PetEntry#CONTENT_URI} to indicate that we want to insert
        // into the pets database table.
        // Receive the new content URI that will allow us to access Toto's data in the future.
        Uri newUri = getContentResolver().insert(PetEntry.CONTENT_URI, values);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertPet();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Do nothing for now
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @NonNull
    @org.jetbrains.annotations.NotNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable @org.jetbrains.annotations.Nullable Bundle args) {
        // Define a projection that specifies the columns from the table we care about
        String[] projection = {
                PetEntry._ID,
                PetEntry.COLUMN_PET_NAME,
                PetEntry.COLUMN_PET_BREED
        };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                PetEntry.CONTENT_URI,          // Provider content uri to query
                projection,                    // Columns to include in  the resulting cursor
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull @org.jetbrains.annotations.NotNull Loader<Cursor> loader, Cursor data) {
        // Update {@link PetCursorAdapter} with this new cursor containing updated pet data
        mCursonAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull @org.jetbrains.annotations.NotNull Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        mCursonAdapter.swapCursor(null);
    }
}