package com.example.ecommerceapp;

public class CartProductDetails
{
 private String PName,Pid,PPrice,quantity,discount;

    public CartProductDetails() {
    }

    public CartProductDetails(String PName, String pid, String PPrice, String quantity, String discount) {
        this.PName = PName;
        this.Pid = pid;
        this.PPrice = PPrice;
        this.quantity = quantity;
        this.discount = discount;
    }

    public String getPName() {
        return PName;
    }

    public void setPName(String PName) {
        this.PName = PName;
    }

    public String getPid() {
        return Pid;
    }

    public void setPid(String pid) {
        Pid = pid;
    }

    public String getPPrice() {
        return PPrice;
    }

    public void setPPrice(String PPrice) {
        this.PPrice = PPrice;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }
}
