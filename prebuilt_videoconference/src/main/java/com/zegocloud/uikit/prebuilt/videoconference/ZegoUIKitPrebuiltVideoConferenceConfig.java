package com.zegocloud.uikit.prebuilt.videoconference;

import com.zegocloud.uikit.components.audiovideocontainer.ZegoLayout;
import com.zegocloud.uikit.components.common.ZegoPresetResolution;
import com.zegocloud.uikit.components.notice.ZegoInRoomNotificationViewConfig;
import com.zegocloud.uikit.prebuilt.videoconference.config.ZegoBottomMenuBarConfig;
import com.zegocloud.uikit.prebuilt.videoconference.config.ZegoLeaveConfirmDialogInfo;
import com.zegocloud.uikit.prebuilt.videoconference.config.ZegoMemberListConfig;
import com.zegocloud.uikit.prebuilt.videoconference.config.ZegoPrebuiltAudioVideoViewConfig;
import com.zegocloud.uikit.prebuilt.videoconference.config.ZegoPrebuiltVideoConfig;
import com.zegocloud.uikit.prebuilt.videoconference.config.ZegoTopMenuBarConfig;
import java.io.Serializable;

public class ZegoUIKitPrebuiltVideoConferenceConfig implements Serializable {

    public boolean turnOnCameraWhenJoining = true;
    public boolean turnOnMicrophoneWhenJoining = true;
    public boolean useSpeakerWhenJoining = true;
    public ZegoPrebuiltAudioVideoViewConfig audioVideoViewConfig = new ZegoPrebuiltAudioVideoViewConfig();
    public ZegoLayout layout = new ZegoLayout();
    public ZegoBottomMenuBarConfig bottomMenuBarConfig = new ZegoBottomMenuBarConfig();
    public ZegoTopMenuBarConfig topMenuBarConfig = new ZegoTopMenuBarConfig();
    public ZegoLeaveConfirmDialogInfo leaveConfirmDialogInfo;
    public ZegoInRoomNotificationViewConfig inRoomNotificationViewConfig = new ZegoInRoomNotificationViewConfig();

    public ZegoMemberListConfig memberListConfig = new ZegoMemberListConfig();
    public ZegoPrebuiltVideoConfig screenSharingVideoConfig = new ZegoPrebuiltVideoConfig(
        ZegoPresetResolution.PRESET_540P);
    public ZegoPrebuiltVideoConfig videoConfig = new ZegoPrebuiltVideoConfig(ZegoPresetResolution.PRESET_360P);

}
