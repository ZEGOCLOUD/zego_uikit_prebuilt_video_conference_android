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
import com.permissionx.guolindev.callback.RequestCallback;
import com.zegocloud.uikit.ZegoUIKit;
import com.zegocloud.uikit.components.audiovideo.ZegoForegroundViewProvider;
import com.zegocloud.uikit.components.audiovideocontainer.ZegoAudioVideoComparator;
import com.zegocloud.uikit.components.audiovideocontainer.ZegoAudioVideoViewConfig;
import com.zegocloud.uikit.components.chat.ZegoInRoomChatItemViewProvider;
import com.zegocloud.uikit.components.notice.ZegoInRoomNotificationItemViewProvider;
import com.zegocloud.uikit.components.notice.ZegoInRoomNotificationViewConfig;
import com.zegocloud.uikit.components.memberlist.ZegoMemberListItemViewProvider;
import com.zegocloud.uikit.prebuilt.videoconference.config.ZegoLeaveConfirmDialogInfo;
import com.zegocloud.uikit.prebuilt.videoconference.databinding.FragmentVideoconferenceBinding;
import com.zegocloud.uikit.prebuilt.videoconference.internal.ConferenceConfigGlobal;
import com.zegocloud.uikit.prebuilt.videoconference.internal.ZegoVideoForegroundView;
import com.zegocloud.uikit.service.defines.ZegoScenario;
import com.zegocloud.uikit.service.defines.ZegoUIKitCallback;
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

        ZegoUIKit.setAudioOutputToSpeaker(config.useSpeakerWhenJoining);

        applyAudioVideoViewConfig(config);

        requestPermissionIfNeeded((allGranted, grantedList, deniedList) -> {
            if (grantedList.contains(permission.CAMERA)) {
                if (config.turnOnCameraWhenJoining) {
                    ZegoUIKit.turnCameraOn(userID, true);
                }
            }
            if (grantedList.contains(permission.RECORD_AUDIO)) {
                if (config.turnOnMicrophoneWhenJoining) {
                    ZegoUIKit.turnMicrophoneOn(userID, true);
                }
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
            binding.avcontainer.setForegroundViewProvider(new ZegoForegroundViewProvider() {
                @Override
                public View getForegroundView(ViewGroup parent, ZegoUIKitUser uiKitUser) {
                    ZegoVideoForegroundView foregroundView = new ZegoVideoForegroundView(getContext(), uiKitUser);
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
        ZegoAudioVideoViewConfig audioVideoViewConfig = new ZegoAudioVideoViewConfig();
        audioVideoViewConfig.showSoundWavesInAudioMode = config.audioVideoViewConfig.showSoundWavesInAudioMode;
        audioVideoViewConfig.useVideoViewAspectFill = config.audioVideoViewConfig.useVideoViewAspectFill;
        binding.avcontainer.setAudioVideoConfig(audioVideoViewConfig);
    }

    private void requestPermissionIfNeeded(RequestCallback requestCallback) {
        List<String> permissions = Arrays.asList(permission.CAMERA, permission.RECORD_AUDIO);
        PermissionX.init(requireActivity()).permissions(permissions).onExplainRequestReason((scope, deniedList) -> {
            String message = "";
            if (deniedList.size() == 1) {
                if (deniedList.contains(permission.CAMERA)) {
                    message = getContext().getString(R.string.permission_explain_camera);
                } else if (deniedList.contains(permission.RECORD_AUDIO)) {
                    message = getContext().getString(R.string.permission_explain_mic);
                }
            } else {
                message = getContext().getString(R.string.permission_explain_camera_mic);
            }
            scope.showRequestReasonDialog(deniedList, message, getString(R.string.ok));
        }).onForwardToSettings((scope, deniedList) -> {
            String message = "";
            if (deniedList.size() == 1) {
                if (deniedList.contains(permission.CAMERA)) {
                    message = getContext().getString(R.string.settings_camera);
                } else if (deniedList.contains(permission.RECORD_AUDIO)) {
                    message = getContext().getString(R.string.settings_mic);
                }
            } else {
                message = getContext().getString(R.string.settings_camera_mic);
            }
            scope.showForwardToSettingsDialog(deniedList, message, getString(R.string.settings),
                getString(R.string.cancel));
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