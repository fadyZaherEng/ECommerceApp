package com.example.ecommerceapp;

public class adminViewClass {
    String nawem,quan;
    Double price;

    public adminViewClass(String nawem, String quan, Double price) {
        this.nawem = nawem;
        this.quan = quan;
        this.price = price;
    }

    public String getNawem() {
        return nawem;
    }

    public void setNawem(String nawem) {
        this.nawem = nawem;
    }

    public String getQuan() {
        return quan;
    }

    public void setQuan(String quan) {
        this.quan = quan;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
