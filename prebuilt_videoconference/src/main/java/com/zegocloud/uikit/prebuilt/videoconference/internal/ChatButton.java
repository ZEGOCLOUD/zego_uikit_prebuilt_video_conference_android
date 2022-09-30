package com.zegocloud.uikit.prebuilt.videoconference.internal;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

public class ChatButton extends AppCompatImageView {

    public ChatButton(@NonNull Context context) {
        super(context);
        iniView();
    }

    public ChatButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        iniView();
    }

    public ChatButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        iniView();
    }

    private void iniView() {
        if (getContext() instanceof Activity) {
            setOnClickListener(v -> {
            });
        }
    }
}
