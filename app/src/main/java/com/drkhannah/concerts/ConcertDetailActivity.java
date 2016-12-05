package com.drkhannah.concerts;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.drkhannah.concerts.models.Concert;
import com.squareup.picasso.Picasso;

/**
 * Created by dhannah on 11/21/16.
 */

public class ConcertDetailActivity extends AppCompatActivity {

    TextView mTitleTextView;
    TextView mTicketUrlTextView;
    TextView mVenueNameTextView;
    String mGeo;

    ShareActionProvider mShareActionProvider;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_concert_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //get the Intent and its Extras that started this Activity
        Intent receivedIntent = getIntent();
        Concert concert = (Concert) receivedIntent.getParcelableExtra(getString(R.string.extra_concert));

        //get references to View's in activity_concert_detail
        mTitleTextView = (TextView) findViewById(R.id.concert_detail_title);
        TextView formattedDateTextView = (TextView) findViewById(R.id.concert_detail_formatted_date);
        TextView formattedLocationTextView = (TextView) findViewById(R.id.concert_detail_formatted_location);
        mTicketUrlTextView = (TextView) findViewById(R.id.concert_detail_ticket_url);
        TextView ticketTypeTextView = (TextView) findViewById(R.id.concert_detail_ticket_type);
        TextView ticketStatusTextView = (TextView) findViewById(R.id.concert_detail_ticket_status);
        TextView descriptionTextView = (TextView) findViewById(R.id.concert_detail_description);
        TextView artistNameTextView = (TextView) findViewById(R.id.concert_detail_artist_name);
        ImageView artistImageView = (ImageView) findViewById(R.id.concert_detail_artist_image);
        TextView artistWebsiteTextView = (TextView) findViewById(R.id.concert_detail_artist_website);
        mVenueNameTextView = (TextView) findViewById(R.id.concert_detail_venue_name);
        TextView venuePlaceTextView = (TextView) findViewById(R.id.concert_detail_venue_place);
        TextView venueCityTextView = (TextView) findViewById(R.id.concert_detail_venue_city);
        TextView venueCountryTextView = (TextView) findViewById(R.id.concert_detail_venue_country);

        //Populate Views with Concert object data
        mTitleTextView.setText(concert.getTitle());
        formattedDateTextView.setText(concert.getFormattedDateTime());
        formattedLocationTextView.setText(concert.getFormattedLocation());
        mTicketUrlTextView.setText(concert.getTicketURL());
        ticketTypeTextView.setText(concert.getTicketType());
        ticketStatusTextView.setText(concert.getTicketStatus());
        descriptionTextView.setText(concert.getDescription());
        artistNameTextView.setText(concert.getArtistName());

        Picasso.with(this)
                .load(concert.getArtistImage())
                .into(artistImageView);

        artistWebsiteTextView.setText(concert.getArtistWebsite());
        mVenueNameTextView.setText(concert.getVenueName());
        venuePlaceTextView.setText(concert.getVenuePlace());
        venueCityTextView.setText(concert.getVenueCity());
        venueCountryTextView.setText(concert.getVenueCountry());

        //create a Geo code
        StringBuilder geoString = new StringBuilder()
                .append("geo:")
                .append(concert.getVenueLatitude())
                .append(",")
                .append(concert.getVenueLongitude())
                .append("?");
        mGeo = geoString.toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflate detail_menu layout
        getMenuInflater().inflate(R.menu.menu_detail, menu);

        //Get the Share Action Button's ShareActionProvider
        MenuItem shareItem = menu.findItem(R.id.action_share);
        ShareActionProvider shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);

        //set the Intent of the shareActionProvider
        if (shareActionProvider != null) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, mTitleTextView.getText() + "\n" + mTicketUrlTextView.getText());
            shareActionProvider.setShareIntent(shareIntent);
        }
        return true;
    }

    //respond to Toolbar Actions
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_map:
                // User chose the "Show Map" item
                if (mGeo != null) {
                    //implicit intent to open up the concert's location in a map app
                    Uri gmmIntentUri = Uri.parse(mGeo).buildUpon()
                            .appendQueryParameter("q", mVenueNameTextView.getText().toString())
                            .build();
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);

                    //see if there is an app on the device with an Activity that can handle this Intent
                    if (mapIntent.resolveActivity(this.getPackageManager()) != null) {
                        startActivity(mapIntent);
                    }
                }
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }
}
