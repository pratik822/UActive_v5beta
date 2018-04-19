package com.uactiv.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by nirmal on 8/2/2016.
 */
public class InviteListsModel extends BaseModel implements Serializable {
    public Details details;

    public Details getDetails() {
        return details;
    }

    public void setDetails(Details details) {
        this.details = details;
    }

    public class Details implements Serializable {
        public ArrayList<IFavGroupNearModel> favourites;
        public ArrayList<IFavGroupNearModel> group;
        //public ArrayList<IFavGroupNearModel> near_by;

        NearBy near_by=new NearBy();

        @SerializedName("near_by_search")
        public ArrayList<IFavGroupNearModel> near_by_search;

        public ArrayList<IFavGroupNearModel> getFavourites() {
            return favourites;
        }

        public void setFavourites(ArrayList<IFavGroupNearModel> favourites) {
            this.favourites = favourites;
        }

        public ArrayList<IFavGroupNearModel> getGroup() {
            return group;
        }

        public void setGroup(ArrayList<IFavGroupNearModel> group) {
            this.group = group;
        }

       /* public ArrayList<IFavGroupNearModel> getNear_by() {
            return near_by;
        }

        public void setNear_by(ArrayList<IFavGroupNearModel> near_by) {
            this.near_by = near_by;
        }*/

        public NearBy getNearBy() {
            return near_by;
        }

        public void setNearBy(NearBy nearBy) {
            this.near_by = nearBy;
        }

        public ArrayList<IFavGroupNearModel> getNearBySearch() {
            return near_by_search;
        }

        public void setNearBySearch(ArrayList<IFavGroupNearModel> nearBySearch) {
            this.near_by_search = nearBySearch;
        }
    }
}
