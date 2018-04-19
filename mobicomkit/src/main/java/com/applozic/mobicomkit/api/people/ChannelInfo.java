package com.applozic.mobicomkit.api.people;

import com.applozic.mobicommons.json.JsonMarker;
import com.applozic.mobicommons.people.channel.ChannelMetadata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sunil on 29/1/16.
 */
public class ChannelInfo extends JsonMarker {

    private String clientGroupId;
    private String groupName;
    private List<String> groupMemberList;
    private String imageUrl;

    private Map<String, String> metadata;
    private ChannelMetadata channelMetadata;


    public ChannelInfo() {
    }

    public ChannelInfo(String groupName, List<String> groupMemberList) {
        this.groupName = groupName;
        this.groupMemberList = groupMemberList;
    }

    public ChannelInfo(String groupName, List<String> groupMemberList, String imageLink) {
        this.groupName = groupName;
        this.groupMemberList = groupMemberList;
        this.imageUrl = imageLink;
    }

    public String getClientGroupId() {
        return clientGroupId;
    }

    public void setClientGroupId(String clientGroupId) {
        this.clientGroupId = clientGroupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<String> getGroupMemberList() {
        return groupMemberList;
    }

    public void setGroupMemberList(List<String> groupMemberList) {
        this.groupMemberList = groupMemberList;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public ChannelMetadata getChannelMetadata() {
        return channelMetadata;
    }

    public void setChannelMetadata(ChannelMetadata channelMetadata) {
        this.channelMetadata = channelMetadata;
        if (channelMetadata != null) {
            if (metadata == null) {
                metadata = new HashMap<String, String>();
            }
            metadata.put(ChannelMetadata.CREATE_GROUP_MESSAGE, channelMetadata.getCreateGroupMessage());
            metadata.put(ChannelMetadata.ADD_MEMBER_MESSAGE, channelMetadata.getAddMemberMessage());
            metadata.put(ChannelMetadata.GROUP_NAME_CHANGE_MESSAGE, channelMetadata.getGroupNameChangeMessage());
            metadata.put(ChannelMetadata.GROUP_ICON_CHANGE_MESSAGE, channelMetadata.getGroupIconChangeMessage());
            metadata.put(ChannelMetadata.GROUP_LEFT_MESSAGE, channelMetadata.getGroupLeftMessage());
            metadata.put(ChannelMetadata.JOIN_MEMBER_MESSAGE, channelMetadata.getJoinMemberMessage());
            metadata.put(ChannelMetadata.DELETED_GROUP_MESSAGE, channelMetadata.getDeletedGroupMessage());
            metadata.put(ChannelMetadata.REMOVE_MEMBER_MESSAGE, channelMetadata.getRemoveMemberMessage());
            metadata.put(ChannelMetadata.HIDE_METADATA_NOTIFICATION, channelMetadata.getHideMetaDataNotification() + "");

        }
    }


    @Override
    public String toString() {
        return "ChannelInfo{" +
                "groupName='" + groupName + '\'' +
                ", groupMemberList=" + groupMemberList +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
