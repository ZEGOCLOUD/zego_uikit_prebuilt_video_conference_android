package com.zegocloud.uikit.prebuilt.videoconference.internal;

import android.content.Context;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import com.zegocloud.uikit.components.audiovideo.ZegoSwitchAudioOutputButton;
import com.zegocloud.uikit.components.audiovideo.ZegoSwitchCameraButton;
import com.zegocloud.uikit.components.audiovideo.ZegoToggleCameraButton;
import com.zegocloud.uikit.components.audiovideo.ZegoToggleMicrophoneButton;
import com.zegocloud.uikit.components.chat.ZegoInRoomChatItemViewProvider;
import com.zegocloud.uikit.components.memberlist.ZegoMemberListItemViewProvider;
import com.zegocloud.uikit.prebuilt.videoconference.R;
import com.zegocloud.uikit.prebuilt.videoconference.ZegoUIKitPrebuiltVideoConferenceFragment.LeaveVideoConferenceListener;
import com.zegocloud.uikit.prebuilt.videoconference.config.ZegoBottomMenuBarConfig;
import com.zegocloud.uikit.prebuilt.videoconference.config.ZegoLeaveConfirmDialogInfo;
import com.zegocloud.uikit.prebuilt.videoconference.config.ZegoMemberListConfig;
import com.zegocloud.uikit.prebuilt.videoconference.config.ZegoMenuBarButtonName;
import com.zegocloud.uikit.prebuilt.videoconference.config.ZegoMenuBarStyle;
import com.zegocloud.uikit.utils.Utils;
import java.util.ArrayList;
import java.util.List;

public class BottomMenuBar extends LinearLayout {

    private float downY;
    private float currentY;
    private List<View> showList = new ArrayList<>();
    private List<View> hideList = new ArrayList<>();
    private MoreDialog moreDialog;
    private Runnable runnable;
    private static final long HIDE_DELAY_TIME = 5000;
    private ZegoBottomMenuBarConfig menuBarConfig;

    public BottomMenuBar(@NonNull Context context) {
        super(context);
        initView();
    }

    public BottomMenuBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public BottomMenuBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        setOrientation(LinearLayout.HORIZONTAL);
        setLayoutParams(new LayoutParams(-1, -2));
        setGravity(Gravity.CENTER_HORIZONTAL);
        runnable = () -> setVisibility(View.GONE);
    }

    private void applyMenuBarButtons(List<ZegoMenuBarButtonName> zegoMenuBarButtons) {
        showList.clear();
        hideList.clear();
        List<View> menuBarViews = getMenuBarViews(zegoMenuBarButtons);
        if (zegoMenuBarButtons.size() <= menuBarConfig.maxCount) {
            showList.addAll(menuBarViews);
        } else {
            int showChildCount = menuBarConfig.maxCount - 1;
            if (showChildCount > 0) {
                showList.addAll(menuBarViews.subList(0, showChildCount));
                hideList = menuBarViews.subList(showChildCount, menuBarViews.size());
            }
            showList.add(new MoreButton(getContext()));
        }
        notifyListChanged();
    }

    private List<View> getMenuBarViews(List<ZegoMenuBarButtonName> list) {
        List<View> viewList = new ArrayList<>();
        if (list != null && list.size() > 0) {
            for (ZegoMenuBarButtonName zegoMenuBarButton : list) {
                View viewFromType = getViewFromType(zegoMenuBarButton);
                viewList.add(viewFromType);
            }
        }
        return viewList;
    }

    private LayoutParams generateChildLayoutParams() {
        int size = Utils.dp2px(48f, getResources().getDisplayMetrics());
        int marginTop = Utils.dp2px(31f, getResources().getDisplayMetrics());
        int marginBottom = Utils.dp2px(25f, getResources().getDisplayMetrics());
        LinearLayout.LayoutParams layoutParams = new LayoutParams(size, size);
        layoutParams.topMargin = marginTop;
        layoutParams.bottomMargin = marginBottom;
        return layoutParams;
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
                ZegoLeaveConfirmDialogInfo leaveConfirmDialogInfo = ConferenceConfigGlobal.getInstance()
                    .getConfig().leaveConfirmDialogInfo;
                ((ZegoLeaveConferenceButton) view).setLeaveConfirmDialogInfo(leaveConfirmDialogInfo);
                LeaveVideoConferenceListener leaveVideoConferenceListener = ConferenceConfigGlobal.getInstance()
                    .getLeaveVideoConferenceListener();
                ((ZegoLeaveConferenceButton) view).setLeaveVideoConferenceListener(leaveVideoConferenceListener);
                break;
            case SWITCH_AUDIO_OUTPUT_BUTTON:
                view = new ZegoSwitchAudioOutputButton(getContext());
                break;
            case SHOW_MEMBER_LIST_BUTTON:
                view = new ImageView(getContext());
                ((ImageView) view).setImageResource(R.drawable.icon_top_member_normal);
                view.setOnClickListener(v -> {
                    ZegoConferenceMemberList memberList = new ZegoConferenceMemberList(getContext());
                    ZegoMemberListItemViewProvider memberListItemProvider = ConferenceConfigGlobal.getInstance()
                        .getMemberListItemProvider();
                    memberList.setMemberListItemViewProvider(memberListItemProvider);
                    ZegoMemberListConfig memberListConfig = ConferenceConfigGlobal.getInstance().getConfig().memberListConfig;
                    memberList.setMemberListConfig(memberListConfig);
                    memberList.show();
                });
                break;
            case CHAT_BUTTON:
                view = new ImageView(getContext());
                ((ImageView) view).setImageResource(R.drawable.icon_chat_normal);
                view.setOnClickListener(v -> {
                    ZegoInRoomChatDialog inRoomChatDialog = new ZegoInRoomChatDialog(getContext());
                    ZegoInRoomChatItemViewProvider inRoomChatItemViewProvider = ConferenceConfigGlobal.getInstance()
                        .getInRoomChatItemViewProvider();
                    inRoomChatDialog.setInRoomChatItemViewProvider(inRoomChatItemViewProvider);
                    inRoomChatDialog.show();
                });
                break;
        }
        if (view != null) {
            view.setTag(menuBar);
        }
        return view;
    }

    public void addButtons(List<View> viewList) {
        for (View view : viewList) {
            if (showList.size() < menuBarConfig.maxCount) {
                showList.add(view);
            } else {
                View lastView = showList.get(showList.size() - 1);
                if (!(lastView instanceof MoreButton)) {
                    showList.remove(lastView);
                    showList.add(new MoreButton(getContext()));
                    hideList.add(lastView);
                }
                hideList.add(view);
            }
        }
        notifyListChanged();
    }

    private void showMoreDialog() {
        if (moreDialog == null) {
            moreDialog = new MoreDialog(getContext());
        }
        if (!moreDialog.isShowing()) {
            moreDialog.show();
        }
        moreDialog.setHideChildren(hideList);
    }

    private void notifyListChanged() {
        removeAllViews();
        for (int i = 0; i < showList.size(); i++) {
            LayoutParams params = generateChildLayoutParams();
            if (i != 0) {
                int marginStart = Utils.dp2px(23f, getResources().getDisplayMetrics());
                if (showList.size() == 2) {
                    marginStart = Utils.dp2px(79f, getResources().getDisplayMetrics());
                } else if (showList.size() == 3) {
                    marginStart = Utils.dp2px(59.5f, getResources().getDisplayMetrics());
                } else if (showList.size() == 4) {
                    marginStart = Utils.dp2px(37f, getResources().getDisplayMetrics());
                }
                params.setMarginStart(marginStart);
            }
            View view = showList.get(i);
            addView(view, params);
        }
        if (moreDialog != null) {
            moreDialog.setHideChildren(hideList);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            currentY = event.getY();
            downY = currentY;
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            float currentY = event.getY();
            int scaledTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
            if (currentY - downY < -scaledTouchSlop) {

            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            currentY = 0;
            downY = 0;
        }
        return super.onTouchEvent(event);
    }

    public void setConfig(ZegoBottomMenuBarConfig bottomMenuBarConfig) {
        this.menuBarConfig = bottomMenuBarConfig;
        applyMenuBarStyle(bottomMenuBarConfig.style);
        applyMenuBarButtons(bottomMenuBarConfig.buttons);
        getHandler().postDelayed(runnable, HIDE_DELAY_TIME);
    }

    private void applyMenuBarStyle(ZegoMenuBarStyle style) {
        if (style == ZegoMenuBarStyle.LIGHT) {
            setBackground(null);
        } else {
            setBackgroundResource(R.drawable.background_bottom_menubar);
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

    public class MoreButton extends AppCompatImageView {

        public MoreButton(@NonNull Context context) {
            super(context);
            initView();
        }

        public MoreButton(@NonNull Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
            initView();
        }

        private void initView() {
            StateListDrawable sld = new StateListDrawable();
            sld.addState(new int[]{android.R.attr.state_pressed},
                ContextCompat.getDrawable(getContext(), R.drawable.icon_more_off));
            sld.addState(new int[]{}, ContextCompat.getDrawable(getContext(), R.drawable.icon_more));
            setImageDrawable(sld);
            setOnClickListener(v -> showMoreDialog());
        }
    }
}
