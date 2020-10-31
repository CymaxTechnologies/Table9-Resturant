package com.example.resturantappadmin;

import java.util.ArrayList;

public class OrderHistory {
    String user_id,resturant_id,resturant_name,table,time,date,payment_method,rating,order_id;
    ArrayList<Cuisine> cuisines=new ArrayList<>();

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    ArrayList<Integer> count=new ArrayList<>();
    ArrayList<String> prices=new ArrayList<>();
    float total=0;

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getResturant_id() {
        return resturant_id;
    }

    public void setResturant_id(String resturant_id) {
        this.resturant_id = resturant_id;
    }

    public String getResturant_name() {
        return resturant_name;
    }

    public void setResturant_name(String resturant_name) {
        this.resturant_name = resturant_name;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPayment_method() {
        return payment_method;
    }

    public void setPayment_method(String payment_method) {
        this.payment_method = payment_method;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public ArrayList<Cuisine> getCuisines() {
        return cuisines;
    }

    public void setCuisines(ArrayList<Cuisine> cuisines) {
        this.cuisines = cuisines;
    }

    public ArrayList<Integer> getCount() {
        return count;
    }

    public void setCount(ArrayList<Integer> count) {
        this.count = count;
    }

    public ArrayList<String> getPrices() {
        return prices;
    }

    public void setPrices(ArrayList<String> prices) {
        this.prices = prices;
    }
}
