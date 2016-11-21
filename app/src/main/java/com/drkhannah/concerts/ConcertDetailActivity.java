package com.drkhannah.concerts;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.drkhannah.concerts.models.Concert;
import com.squareup.picasso.Picasso;

/**
 * Created by dhannah on 11/21/16.
 */

public class ConcertDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_concert_detail);

        //get the Intent and its Extras that started this Activity
        Intent receivedIntent = getIntent();
        Concert concert = (Concert) receivedIntent.getParcelableExtra(getString(R.string.extra_concert));

        //get references to View's in activity_concert_detail
        TextView titleTextView = (TextView) findViewById(R.id.concert_detail_title);
        TextView formattedDateTextView = (TextView) findViewById(R.id.concert_detail_formatted_date);
        TextView formattedLocationTextView = (TextView) findViewById(R.id.concert_detail_formatted_location);
        TextView ticketURLTextView = (TextView) findViewById(R.id.concert_detail_ticket_url);
        TextView ticketTypeTextView = (TextView) findViewById(R.id.concert_detail_ticket_type);
        TextView ticketStatusTextView = (TextView) findViewById(R.id.concert_detail_ticket_status);
        TextView descriptionTextView = (TextView) findViewById(R.id.concert_detail_description);
        TextView artistNameTextView = (TextView) findViewById(R.id.concert_detail_artist_name);
        ImageView artistImageView = (ImageView) findViewById(R.id.concert_detail_artist_image);
        TextView artistWebsiteTextView = (TextView) findViewById(R.id.concert_detail_artist_website);
        TextView venueNameTextView = (TextView) findViewById(R.id.concert_detail_venue_name);
        TextView venuePlaceTextView = (TextView) findViewById(R.id.concert_detail_venue_place);
        TextView venueCityTextView = (TextView) findViewById(R.id.concert_detail_venue_city);
        TextView venueCountryTextView = (TextView) findViewById(R.id.concert_detail_venue_country);

        //Populate Views with Concert object data
        titleTextView.setText(concert.getTitle());
        formattedDateTextView.setText(concert.getFormattedDateTime());
        formattedLocationTextView.setText(concert.getFormattedLocation());
        ticketURLTextView.setText(concert.getTicketURL());
        ticketTypeTextView.setText(concert.getTicketType());
        ticketStatusTextView.setText(concert.getTicketStatus());
        descriptionTextView.setText(concert.getDescription());
        artistNameTextView.setText(concert.getArtistName());

        Picasso.with(this).load(concert.getArtistImage()).into(artistImageView);

        artistWebsiteTextView.setText(concert.getArtistWebsite());
        venueNameTextView.setText(concert.getVenueName());
        venuePlaceTextView.setText(concert.getVenuePlace());
        venueCityTextView.setText(concert.getVenueCity());
        venueCountryTextView.setText(concert.getVenueCountry());
    }
}
