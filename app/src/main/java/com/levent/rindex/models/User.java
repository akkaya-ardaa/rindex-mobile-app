package com.levent.rindex.models;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class User implements Serializable {
    public int id;
    public String firstName ;
    public String lastName;
    public String mail;
    public String username;
    public String hashedPassword;
    public String biography;
    public String dateOfBirth;
    public boolean active;
    public String photoUrl;
}
