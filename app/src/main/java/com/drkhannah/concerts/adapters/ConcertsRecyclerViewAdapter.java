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

import java.util.List;

/**
 * Created by dhannah on 11/14/16.
 */

public class ConcertsRecyclerViewAdapter extends RecyclerView.Adapter<ConcertsRecyclerViewAdapter.ViewHolder> {

    private static final String LOG_TAG = ConcertsRecyclerViewAdapter.class.getSimpleName();

    //constants for which layout to use for Recycler view list items
    private static final int VIEW_TYPE_WITH_IMAGE = 0;
    private static final int VIEW_TYPE_NO_IMAGE = 1;

    private List<Concert> mConcertList;
    private Context mContext;


    //constructor
    public ConcertsRecyclerViewAdapter(Context context,  List<Concert> concertList) {
        mContext = context;
        mConcertList = concertList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is a Concert object
        public TextView mConcertTitleView;
        public TextView mConcertFormattedDateView;
        public TextView mConcertFormattedLocationView;
        public ImageView mArtistImageView;

        //ViewHolder constructor
        public ViewHolder(View view) {
            super(view);
            mConcertTitleView = (TextView) view.findViewById(R.id.concert_title);
            mConcertFormattedDateView = (TextView) view.findViewById(R.id.concert_formatted_date);
            mConcertFormattedLocationView = (TextView) view.findViewById(R.id.concert_formatted_location);
            mArtistImageView = (ImageView) view.findViewById(R.id.artist_image);
            view.setOnClickListener(this);
        }

        //called when a list item is clicked
        @Override
        public void onClick(View view) {
            //start the detail activity
        }
    }


    @Override
    public ConcertsRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (parent instanceof RecyclerView) {
            int layoutId = -1;
            switch (viewType) {
                case VIEW_TYPE_WITH_IMAGE: {
                    layoutId = R.layout.recycler_view_list_item_with_image;
                    break;
                }
                case VIEW_TYPE_NO_IMAGE: {
                    layoutId = R.layout.recycler_view_list_item_no_image;
                }
            }
            View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
            view.setFocusable(true);
            return new ViewHolder(view);
        } else {
            throw new RuntimeException("NOT BOUND TO RECYCLERVIEW");
        }
    }


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
        holder.mConcertFormattedLocationView.setText(concert.getFormattedLocation());
    }


    @Override
    public int getItemCount() {
        return (mConcertList.size() > 0) ? mConcertList.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0) ? VIEW_TYPE_WITH_IMAGE : VIEW_TYPE_NO_IMAGE;
    }

    public void updateData(List<Concert> updatedConcerts) {
        mConcertList = updatedConcerts;
        notifyDataSetChanged();
    }
}
