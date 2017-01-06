package com.drkhannah.concerts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by dhannah on 1/5/17.
 */

public class ArtistSearchActivity extends AppCompatActivity {

    EditText mSearchArtistEditText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mSearchArtistEditText = (EditText) findViewById(R.id.search_artist_edittext);

        mSearchArtistEditText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    returnResult();
                    return true;
                }
                return false;
            }
        });

    }

    private void returnResult() {
        String artistToSearch = mSearchArtistEditText.getText().toString();
        if (!TextUtils.isEmpty(artistToSearch)) {
            Intent returnIntent = new Intent(this, MainActivity.class);
            returnIntent.putExtra(getString(R.string.artist_to_search), artistToSearch);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        } else {
            Intent returnIntent = new Intent(this, MainActivity.class);
            setResult(Activity.RESULT_CANCELED, returnIntent);
            finish();
        }
    }
}
