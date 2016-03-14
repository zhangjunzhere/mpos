package com.itertk.app.mpos.dbhelper;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table ADVERTISING.
 */
public class Advertising {

    private long adv_id;
    private Long order;
    /** Not-null value. */
    private String value;

    public Advertising() {
    }

    public Advertising(long adv_id) {
        this.adv_id = adv_id;
    }

    public Advertising(long adv_id, Long order, String value) {
        this.adv_id = adv_id;
        this.order = order;
        this.value = value;
    }

    public long getAdv_id() {
        return adv_id;
    }

    public void setAdv_id(long adv_id) {
        this.adv_id = adv_id;
    }

    public Long getOrder() {
        return order;
    }

    public void setOrder(Long order) {
        this.order = order;
    }

    /** Not-null value. */
    public String getValue() {
        return value;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setValue(String value) {
        this.value = value;
    }

}
