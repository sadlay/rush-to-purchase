package com.lay.rushtopurchase.entity;

import org.apache.ibatis.type.Alias;

import java.io.Serializable;

/**
 * @Description:
 * @Author: lay
 * @Date: Created in 15:08 2018/11/23
 * @Modified By:IntelliJ IDEA
 */
//mybatis别名
@Alias("product")
public class Product implements Serializable {

    private static final long serialVersionUID = 2011540733511145050L;

    private Long id;
    private String productName;
    private int stock;
    private double price;
    private int version;
    private String note;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
