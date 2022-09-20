package com.zegocloud.uikit.prebuilt.videoconference.invite;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.zegocloud.uikit.components.invite.ZegoInvitationType;
import com.zegocloud.uikit.components.invite.ZegoStartInvitationButton;
import com.zegocloud.uikit.prebuilt.videoconference.invite.internal.VideoConferenceInvitation;
import com.zegocloud.uikit.prebuilt.videoconference.invite.internal.Constants;
import com.zegocloud.uikit.prebuilt.videoconference.invite.internal.InvitationServiceImpl;
import com.zegocloud.uikit.prebuilt.videoconference.invite.internal.VideoConferenceInviteActivity;
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;
import com.zegocloud.uikit.service.internal.UIKitCore;
import com.zegocloud.uikit.service.internal.UIKitCoreUser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ZegoStartVideoConferenceInvitationButton extends ZegoStartInvitationButton {

    private boolean isVideoCall = false;

    public ZegoStartVideoConferenceInvitationButton(@NonNull Context context) {
        super(context);
    }

    public ZegoStartVideoConferenceInvitationButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initView() {
        super.initView();
    }

    public String generateCallID() {
        String callID = null;
        String userID = InvitationServiceImpl.getInstance().userID;
        if (userID != null) {
            callID = "call_" + userID;
        }
        return callID;
    }

    public void setIsVideoCall(boolean isVideoCall) {
        this.isVideoCall = isVideoCall;
        if (isVideoCall) {
            setType(ZegoInvitationType.VIDEO_CALL);
        } else {
            setType(ZegoInvitationType.VOICE_CALL);
        }
    }

    @Override
    public void setType(int type) {
        throw new UnsupportedOperationException("unSupport operation");
    }

    @Override
    protected void invokedWhenClick() {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        String roomID = generateCallID();
        try {
            jsonObject.put("call_id", roomID);
            for (ZegoUIKitUser invitee : invitees) {
                JSONObject tmp = new JSONObject();
                tmp.put("user_id", invitee.userID);
                tmp.put("user_name", invitee.userName);
                jsonArray.put(tmp);
            }
            jsonObject.put("invitees", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        data = jsonObject.toString();
        UIKitCoreUser localCoreUser = UIKitCore.getInstance().getLocalCoreUser();
        if (localCoreUser != null) {
            VideoConferenceInvitation invitation = new VideoConferenceInvitation(roomID, type, invitees, timeout,
                localCoreUser.getUIKitUser());
            VideoConferenceInviteActivity.startActivity(getContext(), invitation, true);
        } else {
            Log.e(Constants.TAG, "please call ZegoUIKit.login(String,String) first");
        }
        super.invokedWhenClick();
        InvitationServiceImpl.getInstance().setCallState(InvitationServiceImpl.OUTGOING);
    }
}
