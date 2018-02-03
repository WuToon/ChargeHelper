package com.zhangwenl1993163.chargehelper.model;

import java.util.Date;

/**
 * Created by Administrator on 2018/2/3.
 */
public class Product {
    private Integer id;
    private String modelName;
    private String modelPrice;
    private Date addTime;
    private Date modifyTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getModelPrice() {
        return modelPrice;
    }

    public void setModelPrice(String modelPrice) {
        this.modelPrice = modelPrice;
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", modelName='" + modelName + '\'' +
                ", modelPrice='" + modelPrice + '\'' +
                ", addTime=" + addTime +
                ", modifyTime=" + modifyTime +
                '}';
    }
}
