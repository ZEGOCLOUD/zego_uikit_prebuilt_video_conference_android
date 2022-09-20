package com.zegocloud.uikit.prebuilt.videoconference.invite;

import android.app.Application;
import com.zegocloud.uikit.prebuilt.videoconference.invite.internal.InvitationServiceImpl;

public class ZegoUIKitPrebuiltVideoConferenceInvitationService {

    public static void init(Application application, long appID, String appSign, String userID, String userName) {
        InvitationServiceImpl.getInstance().init(application, appID, appSign, userID, userName);
    }

    public static void logout() {
        InvitationServiceImpl.getInstance().unInit();
    }

    public static void setPrebuiltCallConfigProvider(ZegoUIKitPrebuiltVideoConferenceConfigProvider provider) {
        InvitationServiceImpl.getInstance().setPrebuiltConfigProvider(provider);
    }
}
