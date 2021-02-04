package com.example.ecommerceapp;

public class ProductDetails {
    String Name,Description,Image,Pid;
    Double Price;

    public ProductDetails(String name, String description, String image, Double price,String id) {
        Name = name;
        Description = description;
        Image = image;
        Price = price;
        Pid=id;
    }

    public String getPid() {
        return Pid;
    }

    public void setPid(String pid) {
        Pid = pid;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public Double getPrice() {
        return Price;
    }

    public void setPrice(Double price) {
        Price = price;
    }
}
