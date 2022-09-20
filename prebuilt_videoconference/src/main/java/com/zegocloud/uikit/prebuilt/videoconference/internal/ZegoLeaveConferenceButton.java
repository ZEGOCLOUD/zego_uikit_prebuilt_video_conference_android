package com.zegocloud.uikit.prebuilt.videoconference.internal;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.zegocloud.uikit.components.audiovideo.ZegoLeaveButton;
import com.zegocloud.uikit.prebuilt.videoconference.config.ZegoLeaveConfirmDialogInfo;
import com.zegocloud.uikit.prebuilt.videoconference.ZegoUIKitPrebuiltVideoConferenceFragment.LeaveVideoConferenceListener;

public class ZegoLeaveConferenceButton extends ZegoLeaveButton {

    private ZegoLeaveConfirmDialogInfo hangUpConfirmDialogInfo;
    private LeaveVideoConferenceListener leaveVideoConferenceListener;

    public ZegoLeaveConferenceButton(@NonNull Context context) {
        super(context);
    }

    public ZegoLeaveConferenceButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setHangUpConfirmInfo(ZegoLeaveConfirmDialogInfo info) {
        hangUpConfirmDialogInfo = info;
    }

    @Override
    public void invokedWhenClick() {
        boolean isActivity = getContext() instanceof Activity;
        if (isActivity && hangUpConfirmDialogInfo != null) {
            showQuitDialog(hangUpConfirmDialogInfo);
        } else {
            if (leaveVideoConferenceListener != null) {
                leaveVideoConferenceListener.onLeaveConference();
            }
        }
    }

    public void setLeaveListener(LeaveVideoConferenceListener listener) {
        this.leaveVideoConferenceListener = listener;
    }

    private void showQuitDialog(ZegoLeaveConfirmDialogInfo dialogInfo) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
        builder.setTitle(dialogInfo.title);
        builder.setMessage(dialogInfo.message);
        builder.setPositiveButton(dialogInfo.confirmButtonName, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (leaveVideoConferenceListener != null) {
                    leaveVideoConferenceListener.onLeaveConference();
                }
            }
        });
        builder.setNegativeButton(dialogInfo.cancelButtonName, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

}
