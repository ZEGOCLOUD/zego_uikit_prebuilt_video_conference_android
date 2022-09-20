package com.zegocloud.uikit.prebuilt.videoconference.invite.internal;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import com.zegocloud.uikit.ZegoUIKit;
import com.zegocloud.uikit.prebuilt.videoconference.R;
import com.zegocloud.uikit.prebuilt.videoconference.ZegoUIKitPrebuiltVideoConferenceConfig;
import com.zegocloud.uikit.prebuilt.videoconference.ZegoUIKitPrebuiltVideoConferenceFragment;
import com.zegocloud.uikit.prebuilt.videoconference.invite.ZegoVideoConferenceInvitationData;
import com.zegocloud.uikit.service.defines.ZegoInvitationListener;
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;
import java.util.List;

public class VideoConferenceInviteActivity extends AppCompatActivity {

    private VideoConferenceInvitation zegoInvitation;

    public static void startActivity(Context context, VideoConferenceInvitation invitation, boolean waitingPage) {
        Log.d(Constants.TAG, "startActivity() called with: context = [" + context + "], invitation = [" + invitation
            + "], waitingPage = [" + waitingPage + "]");
        Intent intent = new Intent(context, VideoConferenceInviteActivity.class);
        intent.putExtra("invitation", invitation);
        intent.putExtra("waitingPage", waitingPage);
        context.startActivity(intent);
    }

    private ZegoInvitationListener invitationListener = new ZegoInvitationListener() {
        @Override
        public void onInvitationReceived(ZegoUIKitUser inviter, int type, String data) {

        }

        @Override
        public void onInvitationTimeout(ZegoUIKitUser inviter, String data) {
            finish();
        }

        @Override
        public void onInvitationResponseTimeout(List<ZegoUIKitUser> invitees, String data) {
            finish();
        }

        @Override
        public void onInvitationAccepted(ZegoUIKitUser invitee, String data) {
            showCallFragment();
        }

        @Override
        public void onInvitationRefused(ZegoUIKitUser invitee, String data) {
            finish();
        }

        @Override
        public void onInvitationCanceled(ZegoUIKitUser inviter, String data) {
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prebuilt_call);

        zegoInvitation = (VideoConferenceInvitation) getIntent().getParcelableExtra("invitation");
        boolean waitingPage = getIntent().getBooleanExtra("waitingPage", true);
        if (waitingPage) {
            showWaitingFragment();
        } else {
            showCallFragment();
        }
    }

    private void showCallFragment() {
        ZegoVideoConferenceInvitationData invitationData = VideoConferenceInvitation.convertToZegoCallInvitationData(zegoInvitation);
        ZegoUIKitPrebuiltVideoConferenceConfig config;
        InvitationServiceImpl service = InvitationServiceImpl.getInstance();
        if (service.getProvider() != null) {
            config = service.getProvider().requireConfig(invitationData);
        } else {
            config = new ZegoUIKitPrebuiltVideoConferenceConfig();
        }
        ZegoUIKitPrebuiltVideoConferenceFragment fragment = ZegoUIKitPrebuiltVideoConferenceFragment.newInstance(invitationData,
            config);
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.call_fragment_container, fragment)
            .commitNow();
    }

    private void showWaitingFragment() {
        VideoConferenceWaitingFragment fragment = VideoConferenceWaitingFragment.newInstance(zegoInvitation);
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.call_fragment_container, fragment)
            .commitNow();
        ZegoUIKit.addInvitationListener(invitationListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ZegoUIKit.removeInvitationListener(invitationListener);
    }
}