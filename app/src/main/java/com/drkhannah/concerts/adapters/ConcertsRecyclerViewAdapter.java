package com.drkhannah.concerts.adapters;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.drkhannah.concerts.MainActivity;
import com.drkhannah.concerts.R;
import com.drkhannah.concerts.data.ConcertsContract;
import com.squareup.picasso.Picasso;

/**
 * Created by dhannah on 11/14/16.
 */

public class ConcertsRecyclerViewAdapter extends RecyclerView.Adapter<ConcertsRecyclerViewAdapter.ViewHolder> {

    private static final String LOG_TAG = ConcertsRecyclerViewAdapter.class.getSimpleName();

    private Context mContext;
    private Cursor mCursor;

    //constants for which layout to use for Recycler view list items
    private static final int VIEW_TYPE_WITH_IMAGE = 0;
    private static final int VIEW_TYPE_NO_IMAGE = 1;

    //constructor
    public ConcertsRecyclerViewAdapter(Context context, Cursor cursor) {
        mContext = context;
        mCursor = cursor;
    }

    //This ViewHolder object will be used in onBindViewHolder
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is a Concert object
        private TextView mConcertTitleView;
        private TextView mConcertFormattedDateView;
        private TextView mConcertTicketStatusView;
        private ImageView mArtistImageView;

        //ViewHolder constructor
        ViewHolder(View view) {
            super(view);
            mConcertTitleView = (TextView) view.findViewById(R.id.concert_title);
            mConcertFormattedDateView = (TextView) view.findViewById(R.id.concert_formatted_date);
            mConcertTicketStatusView = (TextView) view.findViewById(R.id.concert_ticket_status);
            mArtistImageView = (ImageView) view.findViewById(R.id.artist_image);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            //get a record from mCursor using the adapter position
            mCursor.moveToPosition(getAdapterPosition());
            final String artistName = mCursor.getString(mCursor.getColumnIndexOrThrow(ConcertsContract.ArtistEntry.COLUMN_ARTIST_NAME));
            final String concertDate = mCursor.getString(mCursor.getColumnIndexOrThrow(ConcertsContract.ConcertEntry.COLUMN_FORMATTED_DATE_TIME));
            //handle item selection back in MainActivity
            ((ConcertsRecyclerViewAdapterItemClick) mContext).onConcertsRecyclerViewItemClick(ConcertsContract.ConcertEntry.buildConcertForArtistWithDate(artistName, concertDate));
        }
    }

    //get an item's view type
    @Override
    public int getItemViewType(int position) {
        return (position == 0 && !((MainActivity) mContext).isTwoPane()) ? VIEW_TYPE_WITH_IMAGE : VIEW_TYPE_NO_IMAGE;
    }

    // create a new ViewHolder object that uses one of two layout resources:
    // recyclerview_item_view_with_image if it is the first item in the list
    // recyclerview_item_view_no_image for every other item in the list
    @Override
    public ConcertsRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (parent instanceof RecyclerView) {
            int layoutId = -1;
            switch (viewType) {
                case VIEW_TYPE_WITH_IMAGE: {
                    layoutId = R.layout.recyclerview_item_view_with_image;
                    break;
                }
                case VIEW_TYPE_NO_IMAGE: {
                    layoutId = R.layout.recyclerview_item_view_no_image;
                    break;
                }
            }
            View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
            return new ViewHolder(view);
        } else {
            throw new RuntimeException("NOT BOUND TO RECYCLERVIEW");
        }
    }


    //bind data to a ViewHolder object
    @Override
    public void onBindViewHolder(ConcertsRecyclerViewAdapter.ViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        final String imageUrl = mCursor.getString(mCursor.getColumnIndexOrThrow(ConcertsContract.ArtistEntry.COLUMN_ARTIST_IMAGE));
        final String title = mCursor.getString(mCursor.getColumnIndexOrThrow(ConcertsContract.ConcertEntry.COLUMN_TTILE));
        final String date = mCursor.getString(mCursor.getColumnIndexOrThrow(ConcertsContract.ConcertEntry.COLUMN_FORMATTED_DATE_TIME));
        final String ticketStatus = mCursor.getString(mCursor.getColumnIndexOrThrow(ConcertsContract.ConcertEntry.COLUMN_TICKET_STATUS));
        switch (getItemViewType(position)) {
            case VIEW_TYPE_WITH_IMAGE: {
                Picasso.with(mContext)
                        .load(imageUrl)
                        .placeholder(R.drawable.artist_placeholder_img)
                        .error(R.drawable.artist_placeholder_img)
                        .into(holder.mArtistImageView);
            }
        }
        holder.mConcertTitleView.setText(title);
        holder.mConcertFormattedDateView.setText(date);
        holder.mConcertTicketStatusView.setText(ticketStatus);
    }

    public void swapCursor(Cursor newCursor){
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    public Cursor getCursor() {
        return mCursor;
    }

    //returns a count of items in the adapter
    @Override
    public int getItemCount() {
        return (null != mCursor ? mCursor.getCount() : 0);
    }

    //interface to communicate the selected concert back to MainActivity
    public interface ConcertsRecyclerViewAdapterItemClick {
        void onConcertsRecyclerViewItemClick(Uri concertUri);
    }
}
