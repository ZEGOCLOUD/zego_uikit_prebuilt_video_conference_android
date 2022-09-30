package com.zegocloud.uikit.prebuilt.videoconference.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ZegoTopMenuBarConfig implements Serializable {

    public String title;
    public List<ZegoMenuBarButtonName> buttons = Arrays.asList(
        ZegoMenuBarButtonName.SHOW_MEMBER_LIST_BUTTON,ZegoMenuBarButtonName.SWITCH_CAMERA_BUTTON);
    public boolean hideAutomatically = true;
    public boolean hideByClick = true;
    public ZegoMenuBarStyle style = ZegoMenuBarStyle.DARK;
    public boolean isVisible = true;
}
