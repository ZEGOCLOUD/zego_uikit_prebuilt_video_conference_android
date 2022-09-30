package com.zegocloud.uikit.prebuilt.videoconference.internal;

import com.zegocloud.uikit.components.audiovideo.ZegoForegroundViewProvider;
import com.zegocloud.uikit.components.common.ZegoInRoomChatItemViewProvider;
import com.zegocloud.uikit.components.common.ZegoInRoomNotificationItemViewProvider;
import com.zegocloud.uikit.components.common.ZegoMemberListItemViewProvider;
import com.zegocloud.uikit.prebuilt.videoconference.ZegoUIKitPrebuiltVideoConferenceConfig;
import com.zegocloud.uikit.prebuilt.videoconference.ZegoUIKitPrebuiltVideoConferenceFragment.LeaveVideoConferenceListener;

public class ConferenceConfigGlobal {

    private static ConferenceConfigGlobal sInstance;

    private ConferenceConfigGlobal() {
    }

    public static ConferenceConfigGlobal getInstance() {
        synchronized (ConferenceConfigGlobal.class) {
            if (sInstance == null) {
                sInstance = new ConferenceConfigGlobal();
            }
            return sInstance;
        }
    }

    private LeaveVideoConferenceListener leaveVideoConferenceListener;
    private ZegoMemberListItemViewProvider memberListItemProvider;
    private ZegoInRoomNotificationItemViewProvider inRoomNotificationItemViewProvider;
    private ZegoInRoomChatItemViewProvider inRoomChatItemViewProvider;
    private ZegoForegroundViewProvider videoViewForegroundViewProvider;
    private ZegoUIKitPrebuiltVideoConferenceConfig config;

    public LeaveVideoConferenceListener getLeaveVideoConferenceListener() {
        return leaveVideoConferenceListener;
    }

    public void setLeaveVideoConferenceListener(LeaveVideoConferenceListener leaveVideoConferenceListener) {
        this.leaveVideoConferenceListener = leaveVideoConferenceListener;
    }

    public ZegoMemberListItemViewProvider getMemberListItemProvider() {
        return memberListItemProvider;
    }

    public void setMemberListItemProvider(ZegoMemberListItemViewProvider memberListItemProvider) {
        this.memberListItemProvider = memberListItemProvider;
    }

    public ZegoInRoomNotificationItemViewProvider getInRoomNotificationItemViewProvider() {
        return inRoomNotificationItemViewProvider;
    }

    public void setInRoomNotificationItemViewProvider(
        ZegoInRoomNotificationItemViewProvider inRoomNotificationItemViewProvider) {
        this.inRoomNotificationItemViewProvider = inRoomNotificationItemViewProvider;
    }

    public ZegoInRoomChatItemViewProvider getInRoomChatItemViewProvider() {
        return inRoomChatItemViewProvider;
    }

    public void setInRoomChatItemViewProvider(ZegoInRoomChatItemViewProvider inRoomChatItemViewProvider) {
        this.inRoomChatItemViewProvider = inRoomChatItemViewProvider;
    }

    public ZegoForegroundViewProvider getVideoViewForegroundViewProvider() {
        return videoViewForegroundViewProvider;
    }

    public void setVideoViewForegroundViewProvider(ZegoForegroundViewProvider provider) {
        this.videoViewForegroundViewProvider = provider;
    }

    public ZegoUIKitPrebuiltVideoConferenceConfig getConfig() {
        return config;
    }

    public void setConfig(ZegoUIKitPrebuiltVideoConferenceConfig config) {
        this.config = config;
    }
}