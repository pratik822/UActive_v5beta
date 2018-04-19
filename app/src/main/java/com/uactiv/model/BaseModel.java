package com.uactiv.model;

import java.io.Serializable;

/**
 * Created by nirmal on 8/1/2016.
 */
public class BaseModel implements Serializable{
    String result;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
