package com.uactiv.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by nirmal on 8/1/2016.
 */
public class IGroupModel implements Serializable {
    private String idgroup;
    private String groupname;
    private String iduser;
    private String status;
    private String created_on;
    private ArrayList<Members> members;

    public String getCreated_on() {
        return created_on;
    }

    public void setCreated_on(String created_on) {
        this.created_on = created_on;
    }

    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    public String getIdgroup() {
        return idgroup;
    }

    public void setIdgroup(String idgroup) {
        this.idgroup = idgroup;
    }

    public String getIduser() {
        return iduser;
    }

    public void setIduser(String iduser) {
        this.iduser = iduser;
    }

    public ArrayList<Members> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<Members> members) {
        this.members = members;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public class Members implements Serializable {
        private String iduser;
        private String firstname;
        private String lastname;
        private String image;
        private String skills;

        public String getFirstname() {
            return firstname;
        }

        public void setFirstname(String firstname) {
            this.firstname = firstname;
        }

        public String getIduser() {
            return iduser;
        }

        public void setIduser(String iduser) {
            this.iduser = iduser;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getLastname() {
            return lastname;
        }

        public void setLastname(String lastname) {
            this.lastname = lastname;
        }

        public String getSkills() {
            return skills;
        }

        public void setSkills(String skills) {
            this.skills = skills;
        }
    }
}
