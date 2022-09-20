package com.zegocloud.uikit.prebuilt.videoconference.invite;

import com.zegocloud.uikit.prebuilt.videoconference.ZegoUIKitPrebuiltVideoConferenceConfig;

public interface ZegoUIKitPrebuiltVideoConferenceConfigProvider {

    ZegoUIKitPrebuiltVideoConferenceConfig requireConfig(ZegoVideoConferenceInvitationData invitationData);
}