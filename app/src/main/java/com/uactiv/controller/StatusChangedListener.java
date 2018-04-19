package com.uactiv.controller;

import java.io.Serializable;

/**
 * Created by moorthy on 12/14/2015.
 */
public interface StatusChangedListener extends Serializable {
     static final long serialVersionUID = -7060210544600464481L;
    public void onStatusChanged(Object model, int position);


}
