package com.zegocloud.uikit.prebuilt.videoconference.internal;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.zegocloud.uikit.components.common.ZegoMemberListItemProvider;
import com.zegocloud.uikit.prebuilt.videoconference.R;
import com.zegocloud.uikit.prebuilt.videoconference.config.ZegoMemberListConfig;
import com.zegocloud.uikit.prebuilt.videoconference.databinding.LayoutMemberlistBinding;

public class ZegoConferenceMemberList extends BottomSheetDialog {

    private LayoutMemberlistBinding binding;
    private ZegoMemberListItemProvider memberListItemProvider;
    private ZegoMemberListConfig memberListConfig;

    public ZegoConferenceMemberList(@NonNull Context context) {
        super(context, R.style.TransparentDialog);
        //        super(context);
        Window window = getWindow();
        window.requestFeature(Window.FEATURE_NO_TITLE);
    }

    public ZegoConferenceMemberList(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        Window window = getWindow();
        window.requestFeature(Window.FEATURE_NO_TITLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = LayoutMemberlistBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = LayoutParams.MATCH_PARENT;
        lp.height = LayoutParams.WRAP_CONTENT;
        lp.dimAmount = 0.1f;
        lp.gravity = Gravity.BOTTOM;
        window.setAttributes(lp);
        setCanceledOnTouchOutside(true);
        window.setBackgroundDrawable(new ColorDrawable());

        binding.memberlistDown.setOnClickListener(v -> {
            dismiss();
        });
        if (memberListItemProvider != null) {
            binding.memberlist.setItemViewProvider(memberListItemProvider);
        }
        if (memberListConfig != null) {
            binding.memberlist.setShowCameraState(memberListConfig.showCameraState);
            binding.memberlist.setShowMicrophoneState(memberListConfig.showMicrophoneState);
        }

        // both need setPeekHeight & setLayoutParams
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        int height = (int) (displayMetrics.heightPixels * 0.85f);
        getBehavior().setPeekHeight(height);
        CoordinatorLayout.LayoutParams params = new CoordinatorLayout.LayoutParams(-1, height);
        binding.memberlistLayout.setLayoutParams(params);
    }

    @Override
    public void show() {
        super.show();
        try {
            // hack bg color of the BottomSheetDialog
            ViewGroup parent = getDelegate().findViewById(com.google.android.material.R.id.design_bottom_sheet);
            parent.setBackgroundColor(Color.TRANSPARENT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, @NonNull KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_ESCAPE) {
            dismiss();
            return true;
        }
        return false;
    }

    public void setMemberListItemViewProvider(ZegoMemberListItemProvider memberListItemProvider) {
        this.memberListItemProvider = memberListItemProvider;
    }

    public void setMemberListConfig(ZegoMemberListConfig memberListConfig) {
        this.memberListConfig = memberListConfig;
    }
}