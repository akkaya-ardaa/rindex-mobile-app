package com.levent.rindex.models;

import java.io.Serializable;

public class Answer implements Serializable {
    public int id;
    public String body;
    public int questionId;
    public int userId;
    public String date;
    public boolean starred;
}
