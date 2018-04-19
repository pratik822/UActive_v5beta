package com.uactiv.model;

import java.util.List;

/**
 * Created by pratikb on 09-11-2017.
 */
public class FevoriteMain {
    String result;
    List<IFavouritesModel>details;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public List<IFavouritesModel> getDetails() {
        return details;
    }

    public void setDetails(List<IFavouritesModel> details) {
        this.details = details;
    }
}
