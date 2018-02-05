package com.zhangwenl1993163.chargehelper.model;

/**
 * Created by Administrator on 2018/2/3.
 */
public class Product {
    private Integer id;
    private String modelName;
    private Double modelPrice;
    private Long addTimeStamp;
    private Long modifyTimeStamp;

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

    public Double getModelPrice() {
        return modelPrice;
    }

    public void setModelPrice(Double modelPrice) {
        this.modelPrice = modelPrice;
    }

    public Long getAddTimeStamp() {
        return addTimeStamp;
    }

    public void setAddTimeStamp(Long addTimeStamp) {
        this.addTimeStamp = addTimeStamp;
    }

    public Long getModifyTimeStamp() {
        return modifyTimeStamp;
    }

    public void setModifyTimeStamp(Long modifyTimeStamp) {
        this.modifyTimeStamp = modifyTimeStamp;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", modelName='" + modelName + '\'' +
                ", modelPrice=" + modelPrice +
                ", addTimeStamp=" + addTimeStamp +
                ", modifyTimeStamp=" + modifyTimeStamp +
                '}';
    }
}
