package com.zegocloud.uikit.prebuilt.videoconference.internal;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.zegocloud.uikit.components.audiovideo.ZegoSwitchAudioOutputButton;
import com.zegocloud.uikit.components.audiovideo.ZegoSwitchCameraButton;
import com.zegocloud.uikit.components.audiovideo.ZegoToggleCameraButton;
import com.zegocloud.uikit.components.audiovideo.ZegoToggleMicrophoneButton;
import com.zegocloud.uikit.components.common.ZegoMemberListItemProvider;
import com.zegocloud.uikit.prebuilt.videoconference.R;
import com.zegocloud.uikit.prebuilt.videoconference.ZegoUIKitPrebuiltVideoConferenceFragment.LeaveVideoConferenceListener;
import com.zegocloud.uikit.prebuilt.videoconference.config.ZegoLeaveConfirmDialogInfo;
import com.zegocloud.uikit.prebuilt.videoconference.config.ZegoMemberListConfig;
import com.zegocloud.uikit.prebuilt.videoconference.config.ZegoMenuBarButtonName;
import com.zegocloud.uikit.prebuilt.videoconference.config.ZegoMenuBarStyle;
import com.zegocloud.uikit.prebuilt.videoconference.config.ZegoTopMenuBarConfig;
import com.zegocloud.uikit.utils.Utils;
import java.util.ArrayList;
import java.util.List;

public class TopMenuBar extends FrameLayout {

    private Runnable runnable;
    private ZegoLeaveConfirmDialogInfo confirmDialogInfo;
    private LeaveVideoConferenceListener leaveVideoConferenceListener;
    private ZegoMemberListItemProvider memberListItemProvider;
    private final int maxViewCount = 3;
    private final List<View> showList = new ArrayList<>();
    private ZegoConferenceMemberList memberList;
    private LinearLayout contentView;
    private TextView titleView;
    private String title;
    private ZegoMemberListConfig memberListConfig;

    public TopMenuBar(Context context) {
        super(context);
        initView();
    }

    public TopMenuBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public TopMenuBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private ZegoTopMenuBarConfig menuBarConfig;
    private static final long HIDE_DELAY_TIME = 5000;

    private void initView() {
        int paddingEnd = Utils.dp2px(8f, getResources().getDisplayMetrics());
        int paddingStart = Utils.dp2px(6f, getResources().getDisplayMetrics());
        setPadding(paddingStart, 0, paddingEnd, 0);

        contentView = new LinearLayout(getContext());
        contentView.setOrientation(LinearLayout.HORIZONTAL);
        contentView.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
        addView(contentView);

        titleView = new TextView(getContext());
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER_VERTICAL;
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        titleView.setTextColor(Color.WHITE);
        if (title != null) {
            titleView.setText(title);
        }
        addView(titleView, layoutParams);
        runnable = () -> setVisibility(View.GONE);
    }

    @NonNull
    private LayoutParams getChildLayoutParams() {
        int childWidth = Utils.dp2px(35f, getResources().getDisplayMetrics());
        int childHeight = Utils.dp2px(35f, getResources().getDisplayMetrics());
        int childMarginEnd = Utils.dp2px(6f, getResources().getDisplayMetrics());
        LayoutParams params = new LayoutParams(childWidth, childHeight);
        params.setMarginEnd(childMarginEnd);
        return params;
    }

    public void setConfig(ZegoTopMenuBarConfig config) {
        menuBarConfig = config;
        applyMenuBarStyle(config.style);
        applyMenuBarButtons(config.buttons);
        getHandler().postDelayed(runnable, HIDE_DELAY_TIME);
    }

    private void applyMenuBarButtons(List<ZegoMenuBarButtonName> buttons) {
        showList.clear();
        if (buttons.size() > maxViewCount) {
            buttons = buttons.subList(0, maxViewCount);
        }
        List<View> menuBarViews = getMenuBarViews(buttons);
        showList.addAll(menuBarViews);
        notifyListChanged();
    }

    private List<View> getMenuBarViews(List<ZegoMenuBarButtonName> buttons) {
        List<View> viewList = new ArrayList<>();
        if (buttons != null && buttons.size() > 0) {
            for (int i = 0; i < buttons.size(); i++) {
                ZegoMenuBarButtonName buttonName = buttons.get(i);
                View view = getViewFromType(buttonName);
                viewList.add(view);
            }
        }
        return viewList;
    }

    private View getViewFromType(ZegoMenuBarButtonName menuBar) {
        View view = null;
        switch (menuBar) {
            case TOGGLE_CAMERA_BUTTON:
                view = new ZegoToggleCameraButton(getContext());
                break;
            case TOGGLE_MICROPHONE_BUTTON:
                view = new ZegoToggleMicrophoneButton(getContext());
                break;
            case SWITCH_CAMERA_BUTTON:
                view = new ZegoSwitchCameraButton(getContext());
                break;
            case LEAVE_BUTTON:
                view = new ZegoLeaveConferenceButton(getContext());
                if (confirmDialogInfo != null) {
                    ((ZegoLeaveConferenceButton) view).setHangUpConfirmInfo(confirmDialogInfo);
                }
                if (leaveVideoConferenceListener != null) {
                    ((ZegoLeaveConferenceButton) view).setLeaveListener(leaveVideoConferenceListener);
                }
                break;
            case SWITCH_AUDIO_OUTPUT_BUTTON:
                view = new ZegoSwitchAudioOutputButton(getContext());
                break;
            case SHOW_MEMBER_LIST_BUTTON:
                view = new ImageView(getContext());
                ((ImageView) view).setImageResource(R.drawable.icon_member_normal);
                view.setOnClickListener(v -> {
                    memberList = new ZegoConferenceMemberList(getContext());
                    memberList.setMemberListItemViewProvider(memberListItemProvider);
                    memberList.setMemberListConfig(memberListConfig);
                    memberList.show();
                });
                break;
        }
        if (view != null) {
            view.setTag(menuBar);
        }
        return view;
    }

    public void addButtons(List<View> viewList) {
        int length = maxViewCount - showList.size();
        if (length > 0) {
            if (viewList.size() > length) {
                viewList = viewList.subList(0, length);
            }
            showList.addAll(viewList);
        }
        notifyListChanged();
    }

    private void notifyListChanged() {
        contentView.removeAllViews();
        for (View view : showList) {
            contentView.addView(view, getChildLayoutParams());
        }
    }

    private void applyMenuBarStyle(ZegoMenuBarStyle style) {
        if (style == ZegoMenuBarStyle.LIGHT) {
            setBackground(null);
        } else {
            setBackgroundColor(Color.parseColor("#171821"));
        }
    }

    public void setTitleText(String title) {
        this.title = title;
        if (titleView != null && title != null) {
            titleView.setText(title);
        }
    }

    public void setOutSideClicked() {
        if (getVisibility() == View.VISIBLE) {
            if (menuBarConfig.hideByClick) {
                setVisibility(View.GONE);
            }
        } else {
            setVisibility(View.VISIBLE);
            if (menuBarConfig.hideAutomatically) {
                getHandler().removeCallbacks(runnable);
                getHandler().postDelayed(runnable, HIDE_DELAY_TIME);
            }
        }
    }

    public void setLeaveConfirmDialogInfo(ZegoLeaveConfirmDialogInfo leaveConfirmDialogInfo) {
        this.confirmDialogInfo = leaveConfirmDialogInfo;
    }

    public void setLeaveVideoConferenceListener(LeaveVideoConferenceListener leaveVideoConferenceListener) {
        this.leaveVideoConferenceListener = leaveVideoConferenceListener;
    }

    public void setMemberListItemViewProvider(ZegoMemberListItemProvider memberListItemProvider) {
        this.memberListItemProvider = memberListItemProvider;
    }

    public void setMemberListConfig(ZegoMemberListConfig memberListConfig) {
        this.memberListConfig = memberListConfig;
    }
}
