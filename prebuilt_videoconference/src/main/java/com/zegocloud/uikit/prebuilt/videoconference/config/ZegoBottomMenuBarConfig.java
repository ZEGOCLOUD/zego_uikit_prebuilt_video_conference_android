package com.zegocloud.uikit.prebuilt.videoconference.config;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class ZegoBottomMenuBarConfig implements Serializable {

    public List<ZegoMenuBarButtonName> buttons = Arrays.asList(
        ZegoMenuBarButtonName.TOGGLE_CAMERA_BUTTON,
        ZegoMenuBarButtonName.TOGGLE_MICROPHONE_BUTTON,
        ZegoMenuBarButtonName.LEAVE_BUTTON,
        ZegoMenuBarButtonName.SWITCH_AUDIO_OUTPUT_BUTTON,
        ZegoMenuBarButtonName.CHAT_BUTTON
        );
    public int maxCount = 5;
    public boolean hideAutomatically = true;
    public boolean hideByClick = true;

    public ZegoMenuBarStyle style = ZegoMenuBarStyle.DARK;
}
