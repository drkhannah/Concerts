package com.drkhannah.concerts.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.drkhannah.concerts.R;
import com.drkhannah.concerts.models.Concert;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dhannah on 11/14/16.
 */

public class ConcertsRecyclerViewAdapter extends RecyclerView.Adapter<ConcertsRecyclerViewAdapter.ViewHolder> {

    private static final String LOG_TAG = ConcertsRecyclerViewAdapter.class.getSimpleName();

    //constants for which layout to use for Recycler view list items
    private static final int VIEW_TYPE_WITH_IMAGE = 0;
    private static final int VIEW_TYPE_NO_IMAGE = 1;

    private List<Concert> mConcertList = new ArrayList<>();
    private Context mContext;

    //constructor
    public ConcertsRecyclerViewAdapter(Context context) {
        mContext = context;
    }

    //This ViewHolder object will be used in onBindViewHolder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is a Concert object
        public TextView mConcertTitleView;
        public TextView mConcertFormattedDateView;
        public TextView mConcertTicketStatusView;
        public ImageView mArtistImageView;

        //ViewHolder constructor
        public ViewHolder(View view) {
            super(view);
            mConcertTitleView = (TextView) view.findViewById(R.id.concert_title);
            mConcertFormattedDateView = (TextView) view.findViewById(R.id.concert_formatted_date);
            mConcertTicketStatusView = (TextView) view.findViewById(R.id.concert_ticket_status);
            mArtistImageView = (ImageView) view.findViewById(R.id.artist_image);
        }
    }

    //get an item's view type
    @Override
    public int getItemViewType(int position) {
        return (position == 0) ? VIEW_TYPE_WITH_IMAGE : VIEW_TYPE_NO_IMAGE;
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
        Concert concert = mConcertList.get(position);
        switch (getItemViewType(position)) {
            case VIEW_TYPE_WITH_IMAGE: {
                Picasso.with(mContext).load(concert.getArtistImage()).into(holder.mArtistImageView);
            }
        }
        holder.mConcertTitleView.setText(concert.getTitle());
        holder.mConcertFormattedDateView.setText(concert.getFormattedDateTime());
        holder.mConcertTicketStatusView.setText(concert.getTicketStatus());
    }


    //returns a count of items in the adapter
    @Override
    public int getItemCount() {
        return (mConcertList.size() > 0) ? mConcertList.size() : 0;
    }

    //update the adapters data
    public void updateData(List<Concert> updatedConcerts) {
        mConcertList = updatedConcerts;
        notifyDataSetChanged();
    }
}
