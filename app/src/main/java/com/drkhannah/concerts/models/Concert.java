package com.drkhannah.concerts.models;

/**
 * Created by dhannah on 11/9/16.
 */

public class Concert {
    private String mTitle;
    private String mFormattedDateTime;
    private String mFormattedLocation;
    private String mTicketURL;
    private String mTicketType;
    private String mTicketStatus;
    private String mOnSaleDateTime;
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
                   String onSaleDateTime,
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
        mOnSaleDateTime = onSaleDateTime;
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

    public String getOnSaleDateTime() {
        return mOnSaleDateTime;
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
                ", mOnSaleDateTime='" + mOnSaleDateTime + '\'' +
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
}
