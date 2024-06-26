package com.zegocloud.uikit.prebuilt.videoconference;

import android.Manifest.permission;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.RequestCallback;
import com.zegocloud.uikit.ZegoUIKit;
import com.zegocloud.uikit.components.audiovideo.ZegoBaseAudioVideoForegroundView;
import com.zegocloud.uikit.components.audiovideo.ZegoForegroundViewProvider;
import com.zegocloud.uikit.components.audiovideocontainer.ZegoAudioVideoComparator;
import com.zegocloud.uikit.components.audiovideocontainer.ZegoAudioVideoViewConfig;
import com.zegocloud.uikit.components.audiovideocontainer.ZegoLayoutGalleryConfig;
import com.zegocloud.uikit.components.chat.ZegoInRoomChatItemViewProvider;
import com.zegocloud.uikit.components.memberlist.ZegoMemberListItemViewProvider;
import com.zegocloud.uikit.components.notice.ZegoInRoomNotificationItemViewProvider;
import com.zegocloud.uikit.components.notice.ZegoInRoomNotificationViewConfig;
import com.zegocloud.uikit.prebuilt.videoconference.config.ZegoLeaveConfirmDialogInfo;
import com.zegocloud.uikit.prebuilt.videoconference.databinding.VideoconferenceFragmentVideoconferenceBinding;
import com.zegocloud.uikit.prebuilt.videoconference.internal.ConferenceConfigGlobal;
import com.zegocloud.uikit.prebuilt.videoconference.internal.ZegoAudioVideoForegroundView;
import com.zegocloud.uikit.prebuilt.videoconference.internal.ZegoScreenShareForegroundView;
import com.zegocloud.uikit.service.defines.ZegoScenario;
import com.zegocloud.uikit.service.defines.ZegoUIKitCallback;
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;
import im.zego.zegoexpress.constants.ZegoOrientation;
import im.zego.zegoexpress.constants.ZegoVideoConfigPreset;
import im.zego.zegoexpress.entity.ZegoVideoConfig;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ZegoUIKitPrebuiltVideoConferenceFragment extends Fragment {

    private VideoconferenceFragmentVideoconferenceBinding binding;
    private List<View> bottomMenuBarBtns = new ArrayList<>();
    private List<View> topMenuBarBtns = new ArrayList<>();
    private OnBackPressedCallback onBackPressedCallback;
    private LeaveVideoConferenceListener leaveVideoConferenceListener;
    private BroadcastReceiver configurationChangeReceiver;
    private IntentFilter configurationChangeFilter;

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

    public static ZegoUIKitPrebuiltVideoConferenceFragment newInstanceWithToken(long appID, String token,
        @NonNull String userID, @NonNull String userName, @NonNull String conferenceID,
        ZegoUIKitPrebuiltVideoConferenceConfig config) {
        ZegoUIKitPrebuiltVideoConferenceFragment fragment = new ZegoUIKitPrebuiltVideoConferenceFragment();
        Bundle bundle = new Bundle();
        bundle.putLong("appID", appID);
        bundle.putString("appToken", token);
        bundle.putString("conferenceID", conferenceID);
        bundle.putString("userID", userID);
        bundle.putString("userName", userName);
        ConferenceConfigGlobal.getInstance().setConfig(config);
        fragment.setArguments(bundle);
        return fragment;
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
        String token = arguments.getString("appToken");

        if (appID != 0) {
            ZegoUIKit.init(application, appID, appSign, ZegoScenario.GENERAL);
            if (!TextUtils.isEmpty(token)) {
                ZegoUIKit.renewToken(token);
            }
            ZegoUIKit.login(userID, userName);
        }
        if (config.leaveConfirmDialogInfo != null) {
            if (TextUtils.isEmpty(config.leaveConfirmDialogInfo.title)) {
                config.leaveConfirmDialogInfo.title = getString(R.string.videoconference_leave_title);
            }
            if (TextUtils.isEmpty(config.leaveConfirmDialogInfo.message)) {
                config.leaveConfirmDialogInfo.message = getString(R.string.videoconference_leave_message);
            }
            if (TextUtils.isEmpty(config.leaveConfirmDialogInfo.cancelButtonName)) {
                config.leaveConfirmDialogInfo.cancelButtonName = getString(R.string.videoconference_leava_cancel);
            }
            if (TextUtils.isEmpty(config.leaveConfirmDialogInfo.confirmButtonName)) {
                config.leaveConfirmDialogInfo.confirmButtonName = getString(R.string.videoconference_leave_confirm);
            }
        }

        configurationChangeFilter = new IntentFilter();
        configurationChangeFilter.addAction("android.intent.action.CONFIGURATION_CHANGED");

        configurationChangeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                ZegoOrientation orientation = ZegoOrientation.ORIENTATION_0;

                if (Surface.ROTATION_0 == requireActivity().getWindowManager().getDefaultDisplay().getRotation()) {
                    orientation = ZegoOrientation.ORIENTATION_0;
                } else if (Surface.ROTATION_180 == requireActivity().getWindowManager().getDefaultDisplay()
                    .getRotation()) {
                    orientation = ZegoOrientation.ORIENTATION_180;
                } else if (Surface.ROTATION_270 == requireActivity().getWindowManager().getDefaultDisplay()
                    .getRotation()) {
                    orientation = ZegoOrientation.ORIENTATION_270;
                } else if (Surface.ROTATION_90 == requireActivity().getWindowManager().getDefaultDisplay()
                    .getRotation()) {
                    orientation = ZegoOrientation.ORIENTATION_90;
                }
                ZegoUIKit.setAppOrientation(orientation);
            }
        };

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
        binding = VideoconferenceFragmentVideoconferenceBinding.inflate(inflater, container, false);
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
        requireActivity().registerReceiver(configurationChangeReceiver, configurationChangeFilter);

        String userID = getArguments().getString("userID");
        ZegoUIKitPrebuiltVideoConferenceConfig config = ConferenceConfigGlobal.getInstance().getConfig();

        applyMenuBarConfig(config);

        applyNotificationViewConfig(config);

        ZegoUIKit.setAudioOutputToSpeaker(config.useSpeakerWhenJoining);

        applyAudioVideoViewConfig(config);

        requestPermissionIfNeeded((allGranted, grantedList, deniedList) -> {
            if (config.turnOnCameraWhenJoining) {
                if (grantedList.contains(permission.CAMERA)) {
                    ZegoUIKit.turnCameraOn(userID, true);
                }
            } else {
                ZegoUIKit.turnCameraOn(userID, false);
            }
            if (config.turnOnMicrophoneWhenJoining) {
                if (grantedList.contains(permission.RECORD_AUDIO)) {
                    ZegoUIKit.turnMicrophoneOn(userID, true);
                }
            } else {
                ZegoUIKit.turnMicrophoneOn(userID, false);
            }
        });
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
            binding.avcontainer.setAudioVideoForegroundViewProvider(new ZegoForegroundViewProvider() {
                @Override
                public ZegoBaseAudioVideoForegroundView getForegroundView(ViewGroup parent, ZegoUIKitUser uiKitUser) {
                    ZegoAudioVideoForegroundView foregroundView = new ZegoAudioVideoForegroundView(getContext(),
                        uiKitUser.userID);
                    foregroundView.showMicrophoneView(config.audioVideoViewConfig.showMicrophoneStateOnView);
                    foregroundView.showCameraView(config.audioVideoViewConfig.showCameraStateOnView);
                    foregroundView.showUserNameView(config.audioVideoViewConfig.showUserNameOnView);
                    return foregroundView;
                }
            });
        } else {
            binding.avcontainer.setAudioVideoForegroundViewProvider(foregroundProvider);
        }

        if (config.avatarViewProvider != null) {
            binding.avcontainer.setAvatarViewProvider(config.avatarViewProvider);
        }

        binding.avcontainer.setLayout(config.layout);
        binding.avcontainer.setAudioVideoComparator(new ZegoAudioVideoComparator() {
            @Override
            public List<ZegoUIKitUser> sortAudioVideo(List<ZegoUIKitUser> userList) {
                List<ZegoUIKitUser> sortUsers = new ArrayList<>();
                ZegoUIKitUser self = ZegoUIKit.getLocalUser();
                userList.remove(self);
                Collections.reverse(userList);
                sortUsers.add(self);
                sortUsers.addAll(userList);
                return sortUsers;
            }
        });

        binding.avcontainer.setScreenShareForegroundViewProvider((parent, uiKitUser) -> {
            ZegoScreenShareForegroundView foregroundView = new ZegoScreenShareForegroundView(parent, uiKitUser.userID);
            foregroundView.setParentContainer(binding.avcontainer);

            if (config.layout.config instanceof ZegoLayoutGalleryConfig) {
                ZegoLayoutGalleryConfig galleryConfig = (ZegoLayoutGalleryConfig) config.layout.config;
                foregroundView.setToggleButtonRules(galleryConfig.showScreenSharingFullscreenModeToggleButtonRules);
            }

            return foregroundView;
        });

        ZegoAudioVideoViewConfig audioVideoViewConfig = new ZegoAudioVideoViewConfig();
        audioVideoViewConfig.showSoundWavesInAudioMode = config.audioVideoViewConfig.showSoundWavesInAudioMode;
        audioVideoViewConfig.useVideoViewAspectFill = config.audioVideoViewConfig.useVideoViewAspectFill;
        binding.avcontainer.setAudioVideoConfig(audioVideoViewConfig);

        if (config.videoConfig != null) {
            ZegoVideoConfigPreset zegoVideoConfigPreset = ZegoVideoConfigPreset.getZegoVideoConfigPreset(
                config.videoConfig.resolution.value());
            ZegoUIKit.setVideoConfig(new ZegoVideoConfig(zegoVideoConfigPreset));
        }
    }

    private void requestPermissionIfNeeded(RequestCallback requestCallback) {
        List<String> permissions = Arrays.asList(permission.CAMERA, permission.RECORD_AUDIO);

        boolean allGranted = true;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(getContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                allGranted = false;
            }
        }
        if (allGranted) {
            requestCallback.onResult(true, permissions, new ArrayList<>());
            return;
        }

        PermissionX.init(requireActivity()).permissions(permissions).onExplainRequestReason((scope, deniedList) -> {
            String message = "";
            if (deniedList.size() == 1) {
                if (deniedList.contains(permission.CAMERA)) {
                    message = getContext().getString(R.string.videoconference_permission_explain_camera);
                } else if (deniedList.contains(permission.RECORD_AUDIO)) {
                    message = getContext().getString(R.string.videoconference_permission_explain_mic);
                }
            } else {
                message = getContext().getString(R.string.videoconference_permission_explain_camera_mic);
            }
            scope.showRequestReasonDialog(deniedList, message, getString(R.string.videoconference_ok));
        }).onForwardToSettings((scope, deniedList) -> {
            String message = "";
            if (deniedList.size() == 1) {
                if (deniedList.contains(permission.CAMERA)) {
                    message = getContext().getString(R.string.videoconference_settings_camera);
                } else if (deniedList.contains(permission.RECORD_AUDIO)) {
                    message = getContext().getString(R.string.videoconference_settings_mic);
                }
            } else {
                message = getContext().getString(R.string.videoconference_settings_camera_mic);
            }
            scope.showForwardToSettingsDialog(deniedList, message, getString(R.string.videoconference_settings),
                getString(R.string.videoconference_cancel));
        }).request(new RequestCallback() {
            @Override
            public void onResult(boolean allGranted, @NonNull List<String> grantedList,
                @NonNull List<String> deniedList) {
                if (requestCallback != null) {
                    requestCallback.onResult(allGranted, grantedList, deniedList);
                }
            }
        });
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

        binding.topMenuBar.setScreenShareVideoConfig(config.screenSharingVideoConfig);
        binding.bottomMenuBar.setScreenShareVideoConfig(config.screenSharingVideoConfig);

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
            config.topMenuBarConfig.title = getString(R.string.videoconference_top_bar_title);
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
        if (configurationChangeReceiver != null) {
            requireActivity().unregisterReceiver(configurationChangeReceiver);
            configurationChangeReceiver = null;
        }
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