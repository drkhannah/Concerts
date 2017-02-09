package com.drkhannah.concerts;


import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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

import com.drkhannah.concerts.data.ConcertsContract;
import com.squareup.picasso.Picasso;

public class ConcertDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int CONCERT_DETAIL_LOADER_ID = 2;
    private static final String ARG_CONCERT_URI = "concert_uri";

    private Uri mConcertUri;

    //views
    private TextView mTitleTextView;
    private TextView mTicketUrlTextView;
    private TextView mVenueNameTextView;
    private ImageView mArtistImageView;
    private String mGeo;

    private String mArtistName;
    private String mArtistImageURL;
    private String mTitle;
    private String mDate;
    private String mLocation;
    private String mTicketUrl;
    private String mTicketType;
    private String mTicketStatus;
    private String mDescription;
    private String mArtistWebsiteURL;
    private String mVenueName;
    private String mVenuePlace;
    private String mVenueCity;
    private String mVenueRegion;
    private String mVenueCountry;
    private String mVenueLatitude;
    private String mVenueLongitude;

    // projection for our concert list loader
    final String[] CONCERTS_DETAIL_PROJECTION = new String[]{
            ConcertsContract.ArtistEntry.COLUMN_ARTIST_NAME,
            ConcertsContract.ArtistEntry.COLUMN_ARTIST_IMAGE,
            ConcertsContract.ArtistEntry.COLUMN_ARTIST_WEBSITE,
            ConcertsContract.ConcertEntry.COLUMN_TTILE,
            ConcertsContract.ConcertEntry.COLUMN_FORMATTED_DATE_TIME,
            ConcertsContract.ConcertEntry.COLUMN_FORMATTED_LOCATION,
            ConcertsContract.ConcertEntry.COLUMN_TICKET_URL,
            ConcertsContract.ConcertEntry.COLUMN_TICKET_TYPE,
            ConcertsContract.ConcertEntry.COLUMN_TICKET_STATUS,
            ConcertsContract.ConcertEntry.COLUMN_DESCRIPTION,
            ConcertsContract.ConcertEntry.COLUMN_VENUE_NAME,
            ConcertsContract.ConcertEntry.COLUMN_VENUE_PLACE,
            ConcertsContract.ConcertEntry.COLUMN_VENUE_CITY,
            ConcertsContract.ConcertEntry.COLUMN_VENUE_REGION,
            ConcertsContract.ConcertEntry.COLUMN_VENUE_COUNTRY,
            ConcertsContract.ConcertEntry.COLUMN_VENUE_COUNTRY,
            ConcertsContract.ConcertEntry.COLUMN_VENUE_LATITUDE,
            ConcertsContract.ConcertEntry.COLUMN_VENUE_LONGITUDE,

    };

    public ConcertDetailFragment() {
        // Required empty public constructor
    }

    //use this to create a new instance of this fragment and pass it initialization arguments
    public static ConcertDetailFragment newInstance(Uri concertUri) {
        ConcertDetailFragment fragment = new ConcertDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_CONCERT_URI, concertUri);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //get the fragment arguments
        if (getArguments() != null) {
            mConcertUri = getArguments().getParcelable(ARG_CONCERT_URI);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //allows us to add a menu to the App Bar from this fragment
        setHasOptionsMenu(true);
        getLoaderManager().initLoader(CONCERT_DETAIL_LOADER_ID, null, this);
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
                .load(mArtistImageURL)
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
        TextView venueRegionTextView = (TextView) rootView.findViewById(R.id.concert_detail_venue_region);
        TextView venueCountryTextView = (TextView) rootView.findViewById(R.id.concert_detail_venue_country);

        //Populate Views with Concert object data
        mTitleTextView.setText(mTitle);
        formattedDateTextView.setText(mDate);
        formattedLocationTextView.setText(mLocation);
        mTicketUrlTextView.setText(mTicketUrl);
        ticketTypeTextView.setText(mTicketType);
        ticketStatusTextView.setText(mTicketStatus);
        descriptionTextView.setText(mDescription);
        artistNameTextView.setText(mArtistName);

        artistWebsiteTextView.setText(mArtistWebsiteURL);
        mVenueNameTextView.setText(mVenueName);
        venuePlaceTextView.setText(mVenuePlace);
        venueCityTextView.setText(mVenueCity);
        venueRegionTextView.setText(mVenueRegion);
        venueCountryTextView.setText(mVenueCountry);
        //create a Geo code
        mGeo = new StringBuilder()
                .append("geo:")
                .append(mVenueLatitude)
                .append(",")
                .append(mVenueLongitude)
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //called when a new Loader needs to be created.
        if (null != mConcertUri) {
            return new CursorLoader(getActivity(), mConcertUri, CONCERTS_DETAIL_PROJECTION, null, null, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor.getCount() > 0 && cursor.moveToFirst()) {
            mArtistName = cursor.getString(cursor.getColumnIndexOrThrow(ConcertsContract.ArtistEntry.COLUMN_ARTIST_NAME));
            mArtistImageURL = cursor.getString(cursor.getColumnIndexOrThrow(ConcertsContract.ArtistEntry.COLUMN_ARTIST_IMAGE));
            mArtistWebsiteURL = cursor.getString(cursor.getColumnIndexOrThrow(ConcertsContract.ArtistEntry.COLUMN_ARTIST_WEBSITE));
            mTitle = cursor.getString(cursor.getColumnIndexOrThrow(ConcertsContract.ConcertEntry.COLUMN_ARTIST_IMAGE));
            mDate = cursor.getString(cursor.getColumnIndexOrThrow(ConcertsContract.ConcertEntry.COLUMN_ARTIST_IMAGE));
            mLocation = cursor.getString(cursor.getColumnIndexOrThrow(ConcertsContract.ConcertEntry.COLUMN_ARTIST_IMAGE));
            mTicketUrl = cursor.getString(cursor.getColumnIndexOrThrow(ConcertsContract.ConcertEntry.COLUMN_ARTIST_IMAGE));
            mTicketType = cursor.getString(cursor.getColumnIndexOrThrow(ConcertsContract.ConcertEntry.COLUMN_ARTIST_IMAGE));
            mTicketStatus = cursor.getString(cursor.getColumnIndexOrThrow(ConcertsContract.ConcertEntry.COLUMN_ARTIST_IMAGE));
            mDescription = cursor.getString(cursor.getColumnIndexOrThrow(ConcertsContract.ConcertEntry.COLUMN_ARTIST_IMAGE));
            mVenueName = cursor.getString(cursor.getColumnIndexOrThrow(ConcertsContract.ConcertEntry.COLUMN_ARTIST_IMAGE));
            mVenuePlace = cursor.getString(cursor.getColumnIndexOrThrow(ConcertsContract.ConcertEntry.COLUMN_ARTIST_IMAGE));
            mVenueCity = cursor.getString(cursor.getColumnIndexOrThrow(ConcertsContract.ConcertEntry.COLUMN_ARTIST_IMAGE));
            mVenueRegion = cursor.getString(cursor.getColumnIndexOrThrow(ConcertsContract.ConcertEntry.COLUMN_ARTIST_IMAGE));
            mVenueCountry = cursor.getString(cursor.getColumnIndexOrThrow(ConcertsContract.ConcertEntry.COLUMN_ARTIST_IMAGE));
            mVenueLatitude = cursor.getString(cursor.getColumnIndexOrThrow(ConcertsContract.ConcertEntry.COLUMN_ARTIST_IMAGE));
            mVenueLongitude = cursor.getString(cursor.getColumnIndexOrThrow(ConcertsContract.ConcertEntry.COLUMN_ARTIST_IMAGE));
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }
}
