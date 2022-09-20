package com.zegocloud.uikit.prebuilt.videoconference.invite.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.zegocloud.uikit.prebuilt.videoconference.invite.ZegoVideoConferenceInvitationData;
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;
import java.util.List;

public class VideoConferenceInvitation implements Parcelable {

    public String roomID;
    public int type;
    public List<ZegoUIKitUser> invitees;
    public int timeout;
    public ZegoUIKitUser inviteUser;

    public VideoConferenceInvitation(String roomID, int type, List<ZegoUIKitUser> invitees, int timeout,
        ZegoUIKitUser inviteUser) {
        this.roomID = roomID;
        this.type = type;
        this.invitees = invitees;
        this.timeout = timeout;
        this.inviteUser = inviteUser;
    }

    public static ZegoVideoConferenceInvitationData convertToZegoCallInvitationData(
        VideoConferenceInvitation zegoInvitation) {
        ZegoVideoConferenceInvitationData invitationData = new ZegoVideoConferenceInvitationData();
        invitationData.invitees = zegoInvitation.invitees;
        invitationData.inviter = zegoInvitation.inviteUser;
        invitationData.type = zegoInvitation.type;
        invitationData.conferenceID = zegoInvitation.roomID;
        return invitationData;
    }

    public static VideoConferenceInvitation getFromZegoCallInvitationData(ZegoVideoConferenceInvitationData zegoInvitation) {
        VideoConferenceInvitation invitationData = new VideoConferenceInvitation(zegoInvitation.conferenceID, zegoInvitation.type,
            zegoInvitation.invitees, 60, zegoInvitation.inviter);
        return invitationData;
    }


    protected VideoConferenceInvitation(Parcel in) {
        roomID = in.readString();
        type = in.readInt();
        invitees = in.createTypedArrayList(ZegoUIKitUser.CREATOR);
        timeout = in.readInt();
        inviteUser = in.readParcelable(ZegoUIKitUser.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(roomID);
        dest.writeInt(type);
        dest.writeTypedList(invitees);
        dest.writeInt(timeout);
        dest.writeParcelable(inviteUser, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<VideoConferenceInvitation> CREATOR = new Creator<VideoConferenceInvitation>() {
        @Override
        public VideoConferenceInvitation createFromParcel(Parcel in) {
            return new VideoConferenceInvitation(in);
        }

        @Override
        public VideoConferenceInvitation[] newArray(int size) {
            return new VideoConferenceInvitation[size];
        }
    };
}
