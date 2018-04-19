package com.uactiv.fragment;

import com.uactiv.model.IFavGroupNearModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pratikb on 10-04-2018.
 */

public class Mydetailss {
    String idgroup,groupname,iduser;
    private List<IFavGroupNearModel.Members> members;

    public String getIdgroup() {
        return idgroup;
    }

    public void setIdgroup(String idgroup) {
        this.idgroup = idgroup;
    }

    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    public String getIduser() {
        return iduser;
    }

    public void setIduser(String iduser) {
        this.iduser = iduser;
    }

    public List<IFavGroupNearModel.Members> getMembers() {
        return members;
    }

    public void setMembers(List<IFavGroupNearModel.Members> members) {
        this.members = members;
    }
}
