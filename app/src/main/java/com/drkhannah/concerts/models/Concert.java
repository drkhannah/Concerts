package com.drkhannah.concerts.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by dhannah on 11/9/16.
 */

public class Concert implements Parcelable {

    private String mTitle;
    private String mFormattedDateTime;
    private String mFormattedLocation;
    private String mTicketURL;
    private String mTicketType;
    private String mTicketStatus;
    private String mDescription;
    private String mArtistName;
    private String mArtistImage;
    private String mArtistWebsite;
    private String mVenueName;
    private String mVenuePlace;
    private String mVenueCity;
    private String mVenueCountry;
    private String mVenueLongitude;
    private String mVenueLatitude;

    public Concert(String title,
                   String formattedDateTime,
                   String formattedLocation,
                   String ticketURL,
                   String ticketType,
                   String ticketStatus,
                   String description,
                   String artistName,
                   String artistImage,
                   String artistWebsite,
                   String venueName,
                   String venuePlace,
                   String venueCity,
                   String venueCountry,
                   String venueLongitude,
                   String venueLatitude) {
        mTitle = title;
        mFormattedDateTime = formattedDateTime;
        mFormattedLocation = formattedLocation;
        mTicketURL = ticketURL;
        mTicketType = ticketType;
        mTicketStatus = ticketStatus;
        mDescription = description;
        mArtistName = artistName;
        mArtistImage = artistImage;
        mArtistWebsite = artistWebsite;
        mVenueName = venueName;
        mVenuePlace = venuePlace;
        mVenueCity = venueCity;
        mVenueCountry = venueCountry;
        mVenueLongitude = venueLongitude;
        mVenueLatitude = venueLatitude;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getFormattedDateTime() {
        return mFormattedDateTime;
    }

    public String getFormattedLocation() {
        return mFormattedLocation;
    }

    public String getTicketURL() {
        return mTicketURL;
    }

    public String getTicketType() {
        return mTicketType;
    }

    public String getTicketStatus() {
        return mTicketStatus;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getArtistName() {
        return mArtistName;
    }

    public String getArtistImage() {
        return mArtistImage;
    }

    public String getArtistWebsite() {
        return mArtistWebsite;
    }

    public String getVenueName() {
        return mVenueName;
    }

    public String getVenuePlace() {
        return mVenuePlace;
    }

    public String getVenueCity() {
        return mVenueCity;
    }

    public String getVenueCountry() {
        return mVenueCountry;
    }

    public String getVenueLongitude() {
        return mVenueLongitude;
    }

    public String getVenueLatitude() {
        return mVenueLatitude;
    }

    @Override
    public String toString() {
        return "Concert{" +
                "mTitle='" + mTitle + '\'' +
                ", mFormattedDateTime='" + mFormattedDateTime + '\'' +
                ", mFormattedLocation='" + mFormattedLocation + '\'' +
                ", mTicketURL='" + mTicketURL + '\'' +
                ", mTicketType='" + mTicketType + '\'' +
                ", mTicketStatus='" + mTicketStatus + '\'' +
                ", mDescription='" + mDescription + '\'' +
                ", mArtistName='" + mArtistName + '\'' +
                ", mArtistImage='" + mArtistImage + '\'' +
                ", mArtistWebsite='" + mArtistWebsite + '\'' +
                ", mVenueName='" + mVenueName + '\'' +
                ", mVenuePlace='" + mVenuePlace + '\'' +
                ", mVenueCity='" + mVenueCity + '\'' +
                ", mVenueCountry='" + mVenueCountry + '\'' +
                ", mVenueLongitude='" + mVenueLongitude + '\'' +
                ", mVenueLatitude='" + mVenueLatitude + '\'' +
                '}';
    }

    //Everything below deals with the Parcelable interface

    //Concert constructor that receives a Parcel object
    protected Concert(Parcel in) {
        mTitle = in.readString();
        mFormattedDateTime = in.readString();
        mFormattedLocation = in.readString();
        mTicketURL = in.readString();
        mTicketType = in.readString();
        mTicketStatus = in.readString();
        mDescription = in.readString();
        mArtistName = in.readString();
        mArtistImage = in.readString();
        mArtistWebsite = in.readString();
        mVenueName = in.readString();
        mVenuePlace = in.readString();
        mVenueCity = in.readString();
        mVenueCountry = in.readString();
        mVenueLongitude = in.readString();
        mVenueLatitude = in.readString();
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Creator<Concert> CREATOR = new Creator<Concert>() {
        @Override
        public Concert createFromParcel(Parcel in) {
            return new Concert(in);
        }

        @Override
        public Concert[] newArray(int size) {
            return new Concert[size];
        }
    };

    /**
     * Describe the kinds of special objects contained in this Parcelable
     * instance's marshaled representation. For example, if the object will
     * include a file descriptor in the output of {@link #writeToParcel(Parcel, int)},
     * the return value of this method must include the
     * {@link #CONTENTS_FILE_DESCRIPTOR} bit.
     *
     * @return a bitmask indicating the set of special object types marshaled
     * by this Parcelable object instance.
     * @see #CONTENTS_FILE_DESCRIPTOR
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeString(mFormattedDateTime);
        dest.writeString(mFormattedLocation);
        dest.writeString(mTicketURL);
        dest.writeString(mTicketType);
        dest.writeString(mTicketStatus);
        dest.writeString(mDescription);
        dest.writeString(mArtistName);
        dest.writeString(mArtistImage);
        dest.writeString(mArtistWebsite);
        dest.writeString(mVenueName);
        dest.writeString(mVenuePlace);
        dest.writeString(mVenueCity);
        dest.writeString(mVenueCountry);
        dest.writeString(mVenueLongitude);
        dest.writeString(mVenueLatitude);
    }
}
