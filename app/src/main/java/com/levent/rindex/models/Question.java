package com.levent.rindex.models;

import java.io.Serializable;

public class Question implements Serializable {
    public int id;
    public String body;
    public int placeId;
    public int userId;
    public String date;
}
