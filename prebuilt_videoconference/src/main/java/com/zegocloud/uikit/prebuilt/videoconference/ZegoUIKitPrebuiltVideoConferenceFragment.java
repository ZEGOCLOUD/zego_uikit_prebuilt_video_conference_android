package com.zegocloud.uikit.prebuilt.videoconference;

import android.Manifest.permission;
import android.app.Application;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.permissionx.guolindev.PermissionX;
import com.zegocloud.uikit.ZegoUIKit;
import com.zegocloud.uikit.components.audiovideo.ZegoForegroundViewProvider;
import com.zegocloud.uikit.components.audiovideocontainer.ZegoAudioVideoViewConfig;
import com.zegocloud.uikit.components.common.ZegoInRoomChatItemViewProvider;
import com.zegocloud.uikit.components.common.ZegoInRoomNotificationItemViewProvider;
import com.zegocloud.uikit.components.common.ZegoInRoomNotificationViewConfig;
import com.zegocloud.uikit.components.common.ZegoMemberListItemViewProvider;
import com.zegocloud.uikit.prebuilt.videoconference.config.ZegoLeaveConfirmDialogInfo;
import com.zegocloud.uikit.prebuilt.videoconference.databinding.FragmentVideoconferenceBinding;
import com.zegocloud.uikit.prebuilt.videoconference.internal.ConferenceConfigGlobal;
import com.zegocloud.uikit.prebuilt.videoconference.internal.ZegoVideoForegroundView;
import com.zegocloud.uikit.service.defines.ZegoScenario;
import com.zegocloud.uikit.service.defines.ZegoUIKitCallback;
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;
import java.util.ArrayList;
import java.util.List;

public class ZegoUIKitPrebuiltVideoConferenceFragment extends Fragment {

    private FragmentVideoconferenceBinding binding;
    private List<View> bottomMenuBarBtns = new ArrayList<>();
    private List<View> topMenuBarBtns = new ArrayList<>();
    private OnBackPressedCallback onBackPressedCallback;
    private LeaveVideoConferenceListener leaveVideoConferenceListener;

    public static ZegoUIKitPrebuiltVideoConferenceFragment newInstance(long appID, @NonNull String appSign,
        @NonNull String userID, @NonNull String userName, @NonNull String conferenceID,
        @NonNull ZegoUIKitPrebuiltVideoConferenceConfig config) {
        ZegoUIKitPrebuiltVideoConferenceFragment fragment = new ZegoUIKitPrebuiltVideoConferenceFragment();
        Bundle bundle = new Bundle();
        bundle.putLong("appID", appID);
        bundle.putString("appSign", appSign);
        bundle.putString("conferenceID", conferenceID);
        bundle.putString("userID", userID);
        bundle.putString("userName", userName);
        ConferenceConfigGlobal.getInstance().setConfig(config);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static ZegoUIKitPrebuiltVideoConferenceFragment newInstance(long appID, @NonNull String appSign,
        @NonNull String userID, @NonNull String userName, @NonNull String conferenceID) {
        return newInstance(appID, appSign, conferenceID, userID, userName,
            new ZegoUIKitPrebuiltVideoConferenceConfig());
    }

    public ZegoUIKitPrebuiltVideoConferenceFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        ZegoUIKitPrebuiltVideoConferenceConfig config = ConferenceConfigGlobal.getInstance().getConfig();
        Application application = requireActivity().getApplication();
        long appID = arguments.getLong("appID");
        String appSign = arguments.getString("appSign");
        String userID = arguments.getString("userID");
        String userName = arguments.getString("userName");
        if (appID != 0) {
            ZegoUIKit.init(application, appID, appSign, ZegoScenario.GENERAL);
            ZegoUIKit.login(userID, userName);
        }
        if (config.leaveConfirmDialogInfo != null) {
            if (TextUtils.isEmpty(config.leaveConfirmDialogInfo.title)) {
                config.leaveConfirmDialogInfo.title = getString(R.string.leave_title);
            }
            if (TextUtils.isEmpty(config.leaveConfirmDialogInfo.message)) {
                config.leaveConfirmDialogInfo.message = getString(R.string.leave_message);
            }
            if (TextUtils.isEmpty(config.leaveConfirmDialogInfo.cancelButtonName)) {
                config.leaveConfirmDialogInfo.cancelButtonName = getString(R.string.leava_cancel);
            }
            if (TextUtils.isEmpty(config.leaveConfirmDialogInfo.confirmButtonName)) {
                config.leaveConfirmDialogInfo.confirmButtonName = getString(R.string.leave_confirm);
            }
        }
        onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (config.leaveConfirmDialogInfo != null) {
                    handleFragmentBackPressed(config.leaveConfirmDialogInfo);
                } else {
                    leaveRoom();
                    setEnabled(false);
                    requireActivity().onBackPressed();
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        leaveRoom();
    }

    private void handleFragmentBackPressed(ZegoLeaveConfirmDialogInfo quitInfo) {
        showQuitDialog(quitInfo.title, quitInfo.message, quitInfo.confirmButtonName, quitInfo.cancelButtonName);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        binding = FragmentVideoconferenceBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String conferenceID = getArguments().getString("conferenceID");
        if (!TextUtils.isEmpty(conferenceID)) {
            ZegoUIKit.joinRoom(conferenceID, new ZegoUIKitCallback() {
                @Override
                public void onResult(int errorCode) {
                    if (errorCode == 0) {
                        onRoomJoinSucceed();
                    } else {
                        onRoomJoinFailed();
                    }
                }
            });
        }
    }

    private void onRoomJoinFailed() {

    }

    private void onRoomJoinSucceed() {
        String userID = getArguments().getString("userID");
        ZegoUIKitPrebuiltVideoConferenceConfig config = ConferenceConfigGlobal.getInstance().getConfig();

        applyMenuBarConfig(config);

        applyNotificationViewConfig(config);

        ZegoUIKit.turnCameraOn(userID, config.turnOnCameraWhenJoining);
        ZegoUIKit.turnMicrophoneOn(userID, config.turnOnMicrophoneWhenJoining);
        ZegoUIKit.setAudioOutputToSpeaker(config.useSpeakerWhenJoining);

        applyAudioVideoViewConfig(config);

        requestPermissionIfNeeded();
    }

    private void applyNotificationViewConfig(ZegoUIKitPrebuiltVideoConferenceConfig config) {
        ZegoInRoomNotificationItemViewProvider inRoomNotificationItemViewProvider = ConferenceConfigGlobal.getInstance()
            .getInRoomNotificationItemViewProvider();
        if (inRoomNotificationItemViewProvider != null) {
            binding.inroomNotificationView.setInRoomNotificationItemViewProvider(inRoomNotificationItemViewProvider);
        }
        ZegoInRoomNotificationViewConfig notificationViewConfig = ConferenceConfigGlobal.getInstance()
            .getConfig().inRoomNotificationViewConfig;
        binding.inroomNotificationView.setInRoomNotificationViewConfig(notificationViewConfig);
    }

    private void applyAudioVideoViewConfig(ZegoUIKitPrebuiltVideoConferenceConfig config) {
        ZegoForegroundViewProvider foregroundProvider = ConferenceConfigGlobal.getInstance()
            .getVideoViewForegroundViewProvider();
        if (foregroundProvider == null) {
            binding.avcontainer.setForegroundViewProvider(new ZegoForegroundViewProvider() {
                @Override
                public View getForegroundView(ViewGroup parent, ZegoUIKitUser userInfo) {
                    ZegoVideoForegroundView foregroundView = new ZegoVideoForegroundView(getContext(), userInfo);
                    foregroundView.showMicrophone(config.audioVideoViewConfig.showMicrophoneStateOnView);
                    foregroundView.showCamera(config.audioVideoViewConfig.showCameraStateOnView);
                    foregroundView.showUserName(config.audioVideoViewConfig.showUserNameOnView);
                    return foregroundView;
                }
            });
        } else {
            binding.avcontainer.setForegroundViewProvider(foregroundProvider);
        }

        binding.avcontainer.setLayout(config.layout);
        ZegoAudioVideoViewConfig audioVideoViewConfig = new ZegoAudioVideoViewConfig();
        audioVideoViewConfig.showSoundWavesInAudioMode = config.audioVideoViewConfig.showSoundWavesInAudioMode;
        audioVideoViewConfig.useVideoViewAspectFill = config.audioVideoViewConfig.useVideoViewAspectFill;
        binding.avcontainer.setAudioVideoConfig(audioVideoViewConfig);
    }

    private void requestPermissionIfNeeded() {
        String userID = getArguments().getString("userID");
        ZegoUIKitPrebuiltVideoConferenceConfig config = ConferenceConfigGlobal.getInstance().getConfig();
        boolean permissionGranted =
            PermissionX.isGranted(getContext(), permission.CAMERA) && PermissionX.isGranted(getContext(),
                permission.RECORD_AUDIO);
        if (!permissionGranted) {
            PermissionX.init(requireActivity()).permissions(permission.CAMERA, permission.RECORD_AUDIO)
                .onExplainRequestReason((scope, deniedList) -> {
                    scope.showRequestReasonDialog(deniedList, "We require camera&microphone access to connect a call",
                        "OK", "Cancel");
                }).request((allGranted, grantedList, deniedList) -> {
                    if (allGranted) {
                        if (config.turnOnCameraWhenJoining) {
                            ZegoUIKit.turnCameraOn(userID, false);
                            ZegoUIKit.turnCameraOn(userID, true);
                        }
                        if (config.turnOnMicrophoneWhenJoining) {
                            ZegoUIKit.turnMicrophoneOn(userID, false);
                            ZegoUIKit.turnMicrophoneOn(userID, true);
                        }
                    }
                });
        }
    }

    private void applyMenuBarConfig(ZegoUIKitPrebuiltVideoConferenceConfig config) {
        ConferenceConfigGlobal.getInstance().setLeaveVideoConferenceListener(() -> {
            if (leaveVideoConferenceListener != null) {
                leaveVideoConferenceListener.onLeaveConference();
            } else {
                leaveRoom();
                requireActivity().finish();
            }
        });
        binding.bottomMenuBar.setConfig(config.bottomMenuBarConfig);
        binding.topMenuBar.setConfig(config.topMenuBarConfig);
        if (bottomMenuBarBtns.size() > 0) {
            binding.bottomMenuBar.addButtons(bottomMenuBarBtns);
        }
        if (topMenuBarBtns.size() > 0) {
            binding.topMenuBar.addButtons(topMenuBarBtns);
        }
        binding.getRoot().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.bottomMenuBar.setOutSideClicked();
                binding.topMenuBar.setOutSideClicked();
            }
        });
        if (config.topMenuBarConfig.title == null) {
            config.topMenuBarConfig.title = getString(R.string.top_bar_title);
        }
        binding.topMenuBar.setTitleText(config.topMenuBarConfig.title);
    }

    private void showQuitDialog(String title, String message, String positiveText, String negativeText) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (onBackPressedCallback != null) {
                    onBackPressedCallback.setEnabled(false);
                }
                leaveRoom();
                requireActivity().onBackPressed();
            }
        });
        builder.setNegativeButton(negativeText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    private void leaveRoom() {
        ZegoUIKit.leaveRoom();
    }

    public void addButtonToBottomMenuBar(List<View> viewList) {
        bottomMenuBarBtns.addAll(viewList);
        if (binding != null) {
            binding.bottomMenuBar.addButtons(viewList);
        }
    }

    public void addButtonToTopMenuBar(List<View> viewList) {
        topMenuBarBtns.addAll(viewList);
        if (binding != null) {
            binding.topMenuBar.addButtons(viewList);
        }
    }

    public void setForegroundViewProvider(ZegoForegroundViewProvider provider) {
        ConferenceConfigGlobal.getInstance().setVideoViewForegroundViewProvider(provider);
    }

    public void setLeaveVideoConferenceListener(LeaveVideoConferenceListener listener) {
        this.leaveVideoConferenceListener = listener;
    }


    public void setMemberListItemViewProvider(ZegoMemberListItemViewProvider memberListItemProvider) {
        ConferenceConfigGlobal.getInstance().setMemberListItemProvider(memberListItemProvider);
    }

    public void setInRoomChatItemViewProvider(ZegoInRoomChatItemViewProvider inRoomChatItemViewProvider) {
        ConferenceConfigGlobal.getInstance().setInRoomChatItemViewProvider(inRoomChatItemViewProvider);
    }

    public void setInRoomNotificationItemViewProvider(ZegoInRoomNotificationItemViewProvider provider) {
        ConferenceConfigGlobal.getInstance().setInRoomNotificationItemViewProvider(provider);
    }

    public interface LeaveVideoConferenceListener {

        void onLeaveConference();
    }
}