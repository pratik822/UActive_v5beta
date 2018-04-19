package com.uactiv.model;


public class NavDrawerItem {
    int sliderimg;
    private String title;

    public NavDrawerItem() {

    }

    public NavDrawerItem(String title, int sliderimg) {

        this.title = title;
        this.sliderimg = sliderimg;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public int getSliderimg() {
        return sliderimg;
    }

    public void setSliderimg(int sliderimg) {
        this.sliderimg = sliderimg;
    }
}
