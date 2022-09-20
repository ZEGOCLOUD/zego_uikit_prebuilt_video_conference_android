package com.zegocloud.uikit.prebuilt.videoconference.invite;

import com.zegocloud.uikit.service.defines.ZegoUIKitUser;
import java.io.Serializable;
import java.util.List;

public class ZegoVideoConferenceInvitationData implements Serializable {

    public String conferenceID;
    public int type;
    public List<ZegoUIKitUser> invitees;
    public ZegoUIKitUser inviter;
}
