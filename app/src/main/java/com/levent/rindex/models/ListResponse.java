package com.levent.rindex.models;

import java.util.List;

public class ListResponse<T> {
    public List<T> data;
    public String message;
    public boolean success;
}
