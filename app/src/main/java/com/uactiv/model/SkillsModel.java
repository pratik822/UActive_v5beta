package com.uactiv.model;

import java.io.Serializable;

/**
 * Created by nirmal on 8/2/2016.
 */
public class SkillsModel implements Serializable {
    private String activity;
    private String level;
    private String type;
    private String is_open;

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getIs_open() {
        return is_open;
    }

    public void setIs_open(String is_open) {
        this.is_open = is_open;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
