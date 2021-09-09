package com.levent.rindex.models;

import java.io.Serializable;

public class Comment implements Serializable {
    public int id;
    public int placeId;
    public int star;
    public int userId;
    public String text;
    public String addedDate;
    public int nearbyPlaceId;
}
