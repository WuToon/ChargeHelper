package com.zhangwenl1993163.chargehelper.model;

import java.util.Date;

/**
 * Created by Administrator on 2018/2/3.
 */
public class Record {
    private Integer id;
    private Integer processCardNumber;
    private String modelName;
    private Double modelPrice;
    private Integer qulifiedNumber;
    private Date addTime;
    private Date modifyTime;
    private String comment;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getProcessCardNumber() {
        return processCardNumber;
    }

    public void setProcessCardNumber(Integer processCardNumber) {
        this.processCardNumber = processCardNumber;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public Double getModelPrice() {
        return modelPrice;
    }

    public void setModelPrice(Double modelPrice) {
        this.modelPrice = modelPrice;
    }

    public Integer getQulifiedNumber() {
        return qulifiedNumber;
    }

    public void setQulifiedNumber(Integer qulifiedNumber) {
        this.qulifiedNumber = qulifiedNumber;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "Record{" +
                "id=" + id +
                ", processCardNumber=" + processCardNumber +
                ", modelName='" + modelName + '\'' +
                ", modelPrice=" + modelPrice +
                ", qulifiedNumber=" + qulifiedNumber +
                ", addTime=" + addTime +
                ", modifyTime=" + modifyTime +
                ", comment='" + comment + '\'' +
                '}';
    }
}
