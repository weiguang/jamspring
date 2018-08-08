package com.okayjam.jamspring.model;

/**
 * @description: ${description}
 * @author: Chen wei guang <weiguangchen@sf-express.com>
 * @create: 2018/08/08 14:41
 **/
public class DemoModel {
    private Integer id;
    private String key;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    private String value;

}
