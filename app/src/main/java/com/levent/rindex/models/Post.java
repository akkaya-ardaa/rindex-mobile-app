package com.levent.rindex.models;

import java.io.Serializable;

public class Post implements Serializable {
    public int id;
    public String description;
    public int userId;
    public int placeId;
    public String imageUrl;
}
