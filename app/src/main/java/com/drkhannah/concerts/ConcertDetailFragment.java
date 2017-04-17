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
import android.widget.Toast;

import com.drkhannah.concerts.data.ConcertsContract;
import com.squareup.picasso.Picasso;

public class ConcertDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int CONCERT_DETAIL_LOADER_ID = 1;
    private static final String ARG_CONCERT_URI = "concert_uri";

    private Uri mConcertUri;

    //views
    private TextView mTitleTextView;
    private TextView mTicketUrlTextView;
    private TextView mVenueNameTextView;
    private ImageView mArtistImageView;
    private TextView mArtistNameTextView;
    private TextView mArtistWebsiteTextView;
    private TextView mFormattedDateTextView;
    private TextView mFormattedLocationTextView;
    private TextView mTicketTypeTextView;
    private TextView mTicketStatusTextView;
    private TextView mDescriptionTextView;
    private TextView mVenuePlaceTextView;
    private TextView mVenueCityTextView;
    private TextView mVenueRegionTextView;
    private TextView mVenueCountryTextView;


    private String mGeo;

    private ImageView mTicketsIconImageView;

    private View mBuyTicketsCardView;

    // projection for our concert list loader
    final String[] CONCERTS_DETAIL_PROJECTION = new String[]{
            ConcertsContract.ArtistEntry.COLUMN_ARTIST_NAME,
            ConcertsContract.ArtistEntry.COLUMN_ARTIST_IMAGE,
            ConcertsContract.ArtistEntry.COLUMN_ARTIST_WEBSITE,
            ConcertsContract.ConcertEntry.COLUMN_TITLE,
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
            //the concert_detail_artist_image ImageView is in the rootView
            mArtistImageView = (ImageView) rootView.findViewById(R.id.concert_detail_artist_image);
        }

        //get references to View's in activity_concert_detail
        mArtistNameTextView = (TextView) rootView.findViewById(R.id.concert_detail_artist_name);
        mArtistWebsiteTextView = (TextView) rootView.findViewById(R.id.concert_detail_artist_website);
        mTitleTextView = (TextView) rootView.findViewById(R.id.concert_detail_title);
        mFormattedDateTextView = (TextView) rootView.findViewById(R.id.concert_detail_formatted_date);
        mFormattedLocationTextView = (TextView) rootView.findViewById(R.id.concert_detail_formatted_location);
        mTicketUrlTextView = (TextView) rootView.findViewById(R.id.concert_detail_ticket_url);
        mTicketsIconImageView = (ImageView) rootView.findViewById(R.id.tickets_icon);
        mBuyTicketsCardView = rootView.findViewById(R.id.buy_tickets_cardview);
        mTicketTypeTextView = (TextView) rootView.findViewById(R.id.concert_detail_ticket_type);
        mTicketStatusTextView = (TextView) rootView.findViewById(R.id.concert_detail_ticket_status);
        mDescriptionTextView = (TextView) rootView.findViewById(R.id.concert_detail_description);
        mVenueNameTextView = (TextView) rootView.findViewById(R.id.concert_detail_venue_name);
        mVenuePlaceTextView = (TextView) rootView.findViewById(R.id.concert_detail_venue_place);
        mVenueCityTextView = (TextView) rootView.findViewById(R.id.concert_detail_venue_city);
        mVenueRegionTextView = (TextView) rootView.findViewById(R.id.concert_detail_venue_region);
        mVenueCountryTextView = (TextView) rootView.findViewById(R.id.concert_detail_venue_country);

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
            case R.id.action_settings:
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
                return true;
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
            } else {
                Toast.makeText(getActivity(), "There is no Map applicaiton on this device", Toast.LENGTH_SHORT).show();
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
        if (cursor.moveToFirst()) {

            Picasso.with(getActivity())
                    .load(cursor.getString(cursor.getColumnIndexOrThrow(ConcertsContract.ArtistEntry.COLUMN_ARTIST_IMAGE)))
                    .placeholder(R.drawable.artist_placeholder_img)
                    .error(R.drawable.artist_placeholder_img)
                    .into(mArtistImageView);

            mArtistNameTextView.setText(cursor.getString(cursor.getColumnIndexOrThrow(ConcertsContract.ArtistEntry.COLUMN_ARTIST_NAME)));
            mArtistWebsiteTextView.setText(cursor.getString(cursor.getColumnIndexOrThrow(ConcertsContract.ArtistEntry.COLUMN_ARTIST_WEBSITE)));
            mTitleTextView.setText(cursor.getString(cursor.getColumnIndexOrThrow(ConcertsContract.ConcertEntry.COLUMN_TITLE)));
            mFormattedDateTextView.setText(cursor.getString(cursor.getColumnIndexOrThrow(ConcertsContract.ConcertEntry.COLUMN_FORMATTED_DATE_TIME)));
            mFormattedLocationTextView.setText(cursor.getString(cursor.getColumnIndexOrThrow(ConcertsContract.ConcertEntry.COLUMN_FORMATTED_LOCATION)));

            //set text for available or unavailable tickets, and ticket availability icons
            String ticketStatus = cursor.getString(cursor.getColumnIndexOrThrow(ConcertsContract.ConcertEntry.COLUMN_TICKET_STATUS));
            if (ticketStatus.equalsIgnoreCase("available")) {
                mTicketUrlTextView.setText(R.string.tickets_available_buy_now);
                mTicketsIconImageView.setImageResource(R.drawable.tickets_available);

                //set buy tickets link url
                final String url = cursor.getString(cursor.getColumnIndexOrThrow(ConcertsContract.ConcertEntry.COLUMN_TICKET_URL));
                mBuyTicketsCardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(myIntent);
                    }
                });
            } else {
                mTicketUrlTextView.setText(R.string.tickets_sold_out);
                mTicketsIconImageView.setImageResource(R.drawable.tickets_unavailable);
            }

            mTicketTypeTextView.setText(cursor.getString(cursor.getColumnIndexOrThrow(ConcertsContract.ConcertEntry.COLUMN_TICKET_TYPE)));
            mTicketStatusTextView.setText(cursor.getString(cursor.getColumnIndexOrThrow(ConcertsContract.ConcertEntry.COLUMN_TICKET_STATUS)));
            mDescriptionTextView.setText(cursor.getString(cursor.getColumnIndexOrThrow(ConcertsContract.ConcertEntry.COLUMN_DESCRIPTION)));
            mVenueNameTextView.setText(cursor.getString(cursor.getColumnIndexOrThrow(ConcertsContract.ConcertEntry.COLUMN_VENUE_NAME)));
            mVenuePlaceTextView.setText(cursor.getString(cursor.getColumnIndexOrThrow(ConcertsContract.ConcertEntry.COLUMN_VENUE_PLACE)));
            mVenueCityTextView.setText(cursor.getString(cursor.getColumnIndexOrThrow(ConcertsContract.ConcertEntry.COLUMN_VENUE_CITY)));
            mVenueRegionTextView.setText(cursor.getString(cursor.getColumnIndexOrThrow(ConcertsContract.ConcertEntry.COLUMN_VENUE_REGION)));
            mVenueCountryTextView.setText(cursor.getString(cursor.getColumnIndexOrThrow(ConcertsContract.ConcertEntry.COLUMN_VENUE_COUNTRY)));

            //create a Geo code
            mGeo = new StringBuilder()
                    .append("geo:")
                    .append(cursor.getString(cursor.getColumnIndexOrThrow(ConcertsContract.ConcertEntry.COLUMN_VENUE_LATITUDE)))
                    .append(",")
                    .append(cursor.getString(cursor.getColumnIndexOrThrow(ConcertsContract.ConcertEntry.COLUMN_VENUE_LONGITUDE)))
                    .append("?")
                    .toString();
        } else {
            Picasso.with(getActivity())
                    .load(R.drawable.artist_placeholder_img)
                    .into(mArtistImageView);

            mArtistNameTextView.setText(R.string.no_artist_name_available);
            mArtistWebsiteTextView.setText(R.string.no_artist_website_available);
            mTitleTextView.setText(R.string.no_title_available);
            mFormattedDateTextView.setText(R.string.no_date_available);
            mFormattedLocationTextView.setText(R.string.no_location_available);
            mTicketUrlTextView.setText(R.string.no_ticket_url_available);
            mTicketTypeTextView.setText(R.string.no_ticket_type_available);
            mTicketStatusTextView.setText(R.string.no_ticket_status_available);
            mDescriptionTextView.setText(R.string.no_description_available);
            mVenueNameTextView.setText(R.string.no_venue_name_available);
            mVenuePlaceTextView.setText(R.string.no_venue_place_available);
            mVenueCityTextView.setText(R.string.no_venue_city_available);
            mVenueRegionTextView.setText(R.string.no_venue_region_available);
            mVenueCountryTextView.setText(R.string.no_venue_country_available);
            mGeo = null;
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        Picasso.with(getActivity())
                .load(R.drawable.artist_placeholder_img)
                .into(mArtistImageView);

        mArtistNameTextView.setText(R.string.no_artist_name_available);
        mArtistWebsiteTextView.setText(R.string.no_artist_website_available);
        mTitleTextView.setText(R.string.no_title_available);
        mFormattedDateTextView.setText(R.string.no_date_available);
        mFormattedLocationTextView.setText(R.string.no_location_available);
        mTicketUrlTextView.setText(R.string.no_ticket_url_available);
        mTicketTypeTextView.setText(R.string.no_ticket_type_available);
        mTicketStatusTextView.setText(R.string.no_ticket_status_available);
        mDescriptionTextView.setText(R.string.no_description_available);
        mVenueNameTextView.setText(R.string.no_venue_name_available);
        mVenuePlaceTextView.setText(R.string.no_venue_place_available);
        mVenueCityTextView.setText(R.string.no_venue_city_available);
        mVenueRegionTextView.setText(R.string.no_venue_region_available);
        mVenueCountryTextView.setText(R.string.no_venue_country_available);
        mGeo = null;
    }
}
