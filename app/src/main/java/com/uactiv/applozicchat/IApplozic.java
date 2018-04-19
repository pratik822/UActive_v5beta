package com.uactiv.applozicchat;

import com.applozic.mobicommons.people.channel.Channel;

/**
 * Created by nirmal on 11/24/2016.
 */
public interface IApplozic {

    void getGroupId(String groupId);

    void getChannel(Channel mChannel);

    void initFailure(String error_msg);
}
