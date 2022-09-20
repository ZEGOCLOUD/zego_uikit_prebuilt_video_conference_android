package com.zegocloud.uikit.prebuilt.videoconference.internal;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.zegocloud.uikit.prebuilt.videoconference.ZegoUIKitPrebuiltVideoConferenceConfig;

public class VideoConferenceViewModel extends ViewModel {

    private MutableLiveData<ZegoUIKitPrebuiltVideoConferenceConfig> configLiveData = new MutableLiveData<>();

    public MutableLiveData<ZegoUIKitPrebuiltVideoConferenceConfig> getConfigLiveData() {
        return configLiveData;
    }
}