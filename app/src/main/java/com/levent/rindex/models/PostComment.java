package com.levent.rindex.models;

import java.io.Serializable;

public class PostComment implements Serializable {
    public int id;
    public String body;
    public int userId;
    public int postId;
}
