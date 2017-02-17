package com.drkhannah.concerts;

import android.content.SearchRecentSuggestionsProvider;

/**
 * Created by dhannah on 2/17/17.
 */

public class ArtistSuggestionsProvider extends SearchRecentSuggestionsProvider {
    public final static String AUTHORITY = "com.drkhannah.concerts.ArtistSuggestionsProvider";
    public final static int MODE = DATABASE_MODE_QUERIES;

    public ArtistSuggestionsProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }
}
