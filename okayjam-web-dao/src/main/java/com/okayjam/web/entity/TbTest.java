package com.okayjam.web.entity;

import java.util.Date;
import java.io.Serializable;

/**
 * (TbTest)实体类
 *
 * @author makejava19384d14078065e2d237bbbc4e2be277
 * @since 2026-01-13 16:46:39
 */
public class TbTest implements Serializable {
    private static final long serialVersionUID = 614815002419887405L;

    private Long id;

    private String key;

    private String value;

    private Double amt;

    private Short status;

    private Date createTime;

    private Date updateTime;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public Double getAmt() {
        return amt;
    }

    public void setAmt(Double amt) {
        this.amt = amt;
    }

    public Short getStatus() {
        return status;
    }

    public void setStatus(Short status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

}
