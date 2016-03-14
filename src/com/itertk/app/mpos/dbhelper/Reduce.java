package com.itertk.app.mpos.dbhelper;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table REDUCE.
 */
public class Reduce {

    private Long reduceId;
    /** Not-null value. */
    private String name;
    /** Not-null value. */
    private String value;
    private long type;

    public Reduce() {
    }

    public Reduce(Long reduceId) {
        this.reduceId = reduceId;
    }

    public Reduce(Long reduceId, String name, String value, long type) {
        this.reduceId = reduceId;
        this.name = name;
        this.value = value;
        this.type = type;
    }

    public Long getReduceId() {
        return reduceId;
    }

    public void setReduceId(Long reduceId) {
        this.reduceId = reduceId;
    }

    /** Not-null value. */
    public String getName() {
        return name;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setName(String name) {
        this.name = name;
    }

    /** Not-null value. */
    public String getValue() {
        return value;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setValue(String value) {
        this.value = value;
    }

    public long getType() {
        return type;
    }

    public void setType(long type) {
        this.type = type;
    }

}