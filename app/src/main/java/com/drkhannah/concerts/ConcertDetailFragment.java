package com.drkhannah.concerts;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.drkhannah.concerts.models.Concert;
import com.squareup.picasso.Picasso;

public class ConcertDetailFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_CONCERT = "concert";

    private Concert mConcert;

    private TextView mTitleTextView;
    private TextView mTicketUrlTextView;
    private TextView mVenueNameTextView;
    private ImageView mArtistImageView;
    private String mGeo;

    public ConcertDetailFragment() {
        // Required empty public constructor
    }

    //use this to create a new instance of this fragment and pass it initialization arguments
    public static ConcertDetailFragment newInstance(Concert concert) {
        ConcertDetailFragment fragment = new ConcertDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_CONCERT, concert);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //allows us to add a menu to the App Bar from this fragment
        setHasOptionsMenu(true);

        //get the fragment arguments
        if (getArguments() != null) {
            mConcert = getArguments().getParcelable(ARG_CONCERT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_concert_detail, container, false);

        if (getActivity() instanceof ConcertDetailActivity) {
            //the concert_detail_artist_image ImageView is in the ConcertDetailActivity's Toolbar
            mArtistImageView = (ImageView) getActivity().findViewById(R.id.concert_detail_artist_image);
        } else {
            mArtistImageView = (ImageView) rootView.findViewById(R.id.concert_detail_artist_image);
        }

        Picasso.with(getActivity())
                .load(mConcert.getArtistImage())
                .into(mArtistImageView);

        //get references to View's in activity_concert_detail
        mTitleTextView = (TextView) rootView.findViewById(R.id.concert_detail_title);
        TextView formattedDateTextView = (TextView) rootView.findViewById(R.id.concert_detail_formatted_date);
        TextView formattedLocationTextView = (TextView) rootView.findViewById(R.id.concert_detail_formatted_location);
        mTicketUrlTextView = (TextView) rootView.findViewById(R.id.concert_detail_ticket_url);
        TextView ticketTypeTextView = (TextView) rootView.findViewById(R.id.concert_detail_ticket_type);
        TextView ticketStatusTextView = (TextView) rootView.findViewById(R.id.concert_detail_ticket_status);
        TextView descriptionTextView = (TextView) rootView.findViewById(R.id.concert_detail_description);
        TextView artistNameTextView = (TextView) rootView.findViewById(R.id.concert_detail_artist_name);
        TextView artistWebsiteTextView = (TextView) rootView.findViewById(R.id.concert_detail_artist_website);
        mVenueNameTextView = (TextView) rootView.findViewById(R.id.concert_detail_venue_name);
        TextView venuePlaceTextView = (TextView) rootView.findViewById(R.id.concert_detail_venue_place);
        TextView venueCityTextView = (TextView) rootView.findViewById(R.id.concert_detail_venue_city);
        TextView venueCountryTextView = (TextView) rootView.findViewById(R.id.concert_detail_venue_country);

        //Populate Views with Concert object data
        mTitleTextView.setText(mConcert.getTitle());
        formattedDateTextView.setText(mConcert.getFormattedDateTime());
        formattedLocationTextView.setText(mConcert.getFormattedLocation());
        mTicketUrlTextView.setText(mConcert.getTicketURL());
        ticketTypeTextView.setText(mConcert.getTicketType());
        ticketStatusTextView.setText(mConcert.getTicketStatus());
        descriptionTextView.setText(mConcert.getDescription());
        artistNameTextView.setText(mConcert.getArtistName());

        artistWebsiteTextView.setText(mConcert.getArtistWebsite());
        mVenueNameTextView.setText(mConcert.getVenueName());
        venuePlaceTextView.setText(mConcert.getVenuePlace());
        venueCityTextView.setText(mConcert.getVenueCity());
        venueCountryTextView.setText(mConcert.getVenueCountry());

        //create a Geo code
        mGeo = new StringBuilder()
                .append("geo:")
                .append(mConcert.getVenueLatitude())
                .append(",")
                .append(mConcert.getVenueLongitude())
                .append("?")
                .toString();

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //inflate detail_menu layout
        inflater.inflate(R.menu.menu_detail, menu);
        createShareActionProvider(menu);
    }

    //respond to Toolbar Actions
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_map:
                showVenueLocationInMap();
                return true;
            case android.R.id.home:
                return super.onOptionsItemSelected(item);
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    private void createShareActionProvider(Menu menu) {
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
    }

    private void showVenueLocationInMap() {
        // User chose the "Show Map" item
        if (mGeo != null) {
            //implicit intent to open up the concert's location in a map app
            Uri gmmIntentUri = Uri.parse(mGeo).buildUpon()
                    .appendQueryParameter("q", mVenueNameTextView.getText().toString())
                    .build();
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);

            //see if there is an app on the device with an Activity that can handle this Intent
            if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivity(mapIntent);
            }
        }
    }

}
