package com.drkhannah.concerts.adapters;

import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.drkhannah.concerts.MainActivity;
import com.drkhannah.concerts.R;
import com.drkhannah.concerts.data.ConcertsContract;
import com.squareup.picasso.Picasso;

import java.util.Stack;

/**
 * Created by dhannah on 11/14/16.
 */

public class ConcertsRecyclerViewAdapter extends RecyclerView.Adapter<ConcertsRecyclerViewAdapter.ViewHolder> {

    private static final String LOG_TAG = ConcertsRecyclerViewAdapter.class.getSimpleName();

    private Context mContext;
    private Cursor mCursor;

    static private Stack<Integer> sSelectionHistory = new Stack<>();

    //constants for which layout to use for Recycler view list items
    private static final int VIEW_TYPE_WITH_IMAGE = 0;
    private static final int VIEW_TYPE_NO_IMAGE = 1;

    //constructor
    public ConcertsRecyclerViewAdapter(Context context, Cursor cursor) {
        mContext = context;
        mCursor = cursor;
    }

    //This ViewHolder object will be used in onBindViewHolder
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is a Concert object
        private TextView mConcertTitleView;
        private TextView mConcertFormattedDateView;
        private TextView mConcertTicketStatusView;
        public ImageView mArtistImageView;
        private TextView mLocation;
        public ImageView mTicketsIconImageView;
        private LinearLayout mItemBackground;


        //ViewHolder constructor
        ViewHolder(View view) {
            super(view);
            mConcertTitleView = (TextView) view.findViewById(R.id.concert_title);
            mConcertFormattedDateView = (TextView) view.findViewById(R.id.concert_formatted_date);
            mConcertTicketStatusView = (TextView) view.findViewById(R.id.concert_ticket_status);
            mArtistImageView = (ImageView) view.findViewById(R.id.artist_image);
            mLocation = (TextView) view.findViewById(R.id.concert_formatted_location);
            mTicketsIconImageView = (ImageView) view.findViewById(R.id.tickets_icon);
            mItemBackground = (LinearLayout) view.findViewById(R.id.recyclerview_item_background);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            //set selected item
            if (((MainActivity) mContext).isTwoPane()) {
                notifyItemChanged(sSelectionHistory.peek());
                sSelectionHistory.push(getLayoutPosition());
                notifyItemChanged(sSelectionHistory.peek());
            } else {
                sSelectionHistory.push(getLayoutPosition());
                showSelectedItemDetails(this);
            }
        }
    }

    //show details of selected item
    private void showSelectedItemDetails(ViewHolder viewHolder) {
        //get a record from mCursor using the selected position position
        mCursor.moveToPosition(sSelectionHistory.peek());
        final String artistName = mCursor.getString(mCursor.getColumnIndexOrThrow(ConcertsContract.ArtistEntry.COLUMN_ARTIST_NAME));
        final String concertDate = mCursor.getString(mCursor.getColumnIndexOrThrow(ConcertsContract.ConcertEntry.COLUMN_FORMATTED_DATE_TIME));
        //handle item selection back in MainActivity
        ((ConcertsRecyclerViewAdapterItemClick) mContext).onConcertsRecyclerViewItemClick(ConcertsContract.ConcertEntry.buildConcertForArtistWithDate(artistName, concertDate), viewHolder);
    }

    //get selected position
    public int getSelectedPos() {
        return (!sSelectionHistory.empty()) ? sSelectionHistory.peek() : 0;
    }

    //pop selection off top of stack
    public void popSelectionHistory() {
        if (!sSelectionHistory.empty()) {
            notifyItemChanged(sSelectionHistory.peek());
            sSelectionHistory.pop();
            if (!sSelectionHistory.empty()) {
                notifyItemChanged(sSelectionHistory.peek());
            }
        }
    }

    //get an item's view type
    @Override
    public int getItemViewType(int position) {
        //get device orientation
        int orientation = mContext.getResources().getConfiguration().orientation;
        return (position == 0 && !((MainActivity) mContext).isTwoPane() && orientation == Configuration.ORIENTATION_PORTRAIT) ? VIEW_TYPE_WITH_IMAGE : VIEW_TYPE_NO_IMAGE;
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
        final String title = mCursor.getString(mCursor.getColumnIndexOrThrow(ConcertsContract.ConcertEntry.COLUMN_TITLE));
        final String location = mCursor.getString(mCursor.getColumnIndexOrThrow(ConcertsContract.ConcertEntry.COLUMN_FORMATTED_LOCATION));
        final String date = mCursor.getString(mCursor.getColumnIndexOrThrow(ConcertsContract.ConcertEntry.COLUMN_FORMATTED_DATE_TIME));
        final String ticketStatus = mCursor.getString(mCursor.getColumnIndexOrThrow(ConcertsContract.ConcertEntry.COLUMN_TICKET_STATUS));

        //set ticket icon type
        if (ticketStatus.equalsIgnoreCase("available")) {
            holder.mTicketsIconImageView.setImageResource(R.drawable.tickets_available);
        } else {
            holder.mTicketsIconImageView.setImageResource(R.drawable.tickets_unavailable);
        }

        switch (getItemViewType(position)) {
            case VIEW_TYPE_WITH_IMAGE: {
                holder.mConcertTitleView.setText(title);
                Picasso.with(mContext)
                        .load(imageUrl)
                        .placeholder(R.drawable.artist_placeholder_img)
                        .error(R.drawable.artist_placeholder_img)
                        .into(holder.mArtistImageView);
                break;
            }
            case VIEW_TYPE_NO_IMAGE: {
                holder.mLocation.setText(location);
                break;
            }
        }
        holder.mConcertFormattedDateView.setText(date);
        holder.mConcertTicketStatusView.setText(ticketStatus);

        //set selected item only in TwoPane Tablet UI
        if (((MainActivity) mContext).isTwoPane() && !sSelectionHistory.empty()) {
            holder.mItemBackground.setSelected(sSelectionHistory.peek() == position);
            if (sSelectionHistory.peek() == position) {
                showSelectedItemDetails(holder);
            }
        }
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
        if (((MainActivity) mContext).isTwoPane() && sSelectionHistory.empty()) {
            sSelectionHistory.push(0);
        }
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
        void onConcertsRecyclerViewItemClick(Uri concertUri, ConcertsRecyclerViewAdapter.ViewHolder viewHolder);
    }
}
