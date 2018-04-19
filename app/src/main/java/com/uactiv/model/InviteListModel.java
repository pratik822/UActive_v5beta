package com.uactiv.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by nirmal on 8/1/2016.
 */
public class InviteListModel extends BaseModel implements Serializable {
    public Details details;

    public Details getDetails() {
        return details;
    }

    public void setDetails(Details details) {
        this.details = details;
    }

    public class Details implements Serializable {
        public ArrayList<IFavouritesModel> favourites;
        public ArrayList<IGroupModel> group;
        public ArrayList<INearByModel> near_by;

        public ArrayList<IFavouritesModel> getFavourites() {
            return favourites;
        }

        public void setFavourites(ArrayList<IFavouritesModel> favourites) {
            this.favourites = favourites;
        }

        public ArrayList<IGroupModel> getGroup() {
            return group;
        }

        public void setGroup(ArrayList<IGroupModel> group) {
            this.group = group;
        }

        public ArrayList<INearByModel> getNear_by() {
            return near_by;
        }

        public void setNear_by(ArrayList<INearByModel> near_by) {
            this.near_by = near_by;
        }
    }
}
