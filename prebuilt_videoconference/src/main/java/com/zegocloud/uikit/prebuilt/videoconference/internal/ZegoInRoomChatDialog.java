package com.zegocloud.uikit.prebuilt.videoconference.internal;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import com.zegocloud.uikit.components.chat.ZegoInRoomChatItemViewProvider;
import com.zegocloud.uikit.prebuilt.videoconference.R;
import com.zegocloud.uikit.prebuilt.videoconference.databinding.LayoutInroomChatBinding;

public class ZegoInRoomChatDialog extends Dialog {

    private LayoutInroomChatBinding binding;
    private ZegoInRoomChatItemViewProvider inRoomChatItemViewProvider;
    private boolean isKeyboardVisible = false;
    private boolean dismissWhenBackPressed = false;
    private OnGlobalLayoutListener globalLayoutListener;

    public ZegoInRoomChatDialog(@NonNull Context context) {
        super(context, R.style.TransparentDialog);
        //        super(context);
        Window window = getWindow();
        window.requestFeature(Window.FEATURE_NO_TITLE);
    }

    public ZegoInRoomChatDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        Window window = getWindow();
        window.requestFeature(Window.FEATURE_NO_TITLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = LayoutInroomChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Window window = getWindow();
        LayoutParams lp = window.getAttributes();
        lp.width = LayoutParams.MATCH_PARENT;
        lp.height = LayoutParams.WRAP_CONTENT;
        lp.dimAmount = 0.5f;
        lp.gravity = Gravity.BOTTOM;
        window.setAttributes(lp);
        setCanceledOnTouchOutside(true);
        window.setBackgroundDrawable(new ColorDrawable());

        int mode = LayoutParams.SOFT_INPUT_ADJUST_RESIZE;
        window.setSoftInputMode(mode);

        binding.inroomChatDown.setOnClickListener(v -> {
            dismiss();
        });
        binding.inroomChatInput.setPlaceHolder(getContext().getString(R.string.chat_hint));

        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        int height = (int) (displayMetrics.heightPixels * 0.85f);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-1, height);
        binding.inroomChatLayout.setLayoutParams(params);

        binding.inroomChatView.setInRoomChatItemViewProvider(inRoomChatItemViewProvider);

        globalLayoutListener = new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (binding.getRoot().getHeight() == height) {
                    isKeyboardVisible = false;
                } else {
                    isKeyboardVisible = true;
                }
            }
        };
        binding.getRoot().getViewTreeObserver().addOnGlobalLayoutListener(globalLayoutListener);
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                binding.getRoot().getViewTreeObserver().removeOnGlobalLayoutListener(globalLayoutListener);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_ESCAPE) {
            dismissWhenBackPressed = !isKeyboardVisible;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, @NonNull KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_ESCAPE) {
            if (dismissWhenBackPressed) {
                dismiss();
                return false;
            } else {
                return true;
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    public void setInRoomChatItemViewProvider(ZegoInRoomChatItemViewProvider inRoomChatItemViewProvider) {
        this.inRoomChatItemViewProvider = inRoomChatItemViewProvider;
    }
}
