package com.itertk.app.mpos.dbhelper;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table RETAIL.
 */
public class Retail {

    private long id;
    private String price;

    public Retail() {
    }

    public Retail(long id) {
        this.id = id;
    }

    public Retail(long id, String price) {
        this.id = id;
        this.price = price;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

}