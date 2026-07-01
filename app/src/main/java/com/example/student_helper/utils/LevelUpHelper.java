package com.example.student_helper.utils;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import com.example.student_helper.R;
import com.google.android.material.button.MaterialButton;

public class LevelUpHelper {

    public static void showLevelUpDialog(Context context, int newLevel) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        View rootView = LayoutInflater.from(context).inflate(R.layout.dialog_level_up, null);
        dialog.setContentView(rootView);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.setCancelable(true);

        TextView tvNewLevel = rootView.findViewById(R.id.tvNewLevel);
        tvNewLevel.setText("Dosegao si Nivo " + newLevel + "! 🚀");

        MaterialButton btnContinue = rootView.findViewById(R.id.btnLevelUpContinue);
        btnContinue.setOnClickListener(v -> dialog.dismiss());

        dialog.show();

        rootView.setScaleX(0f);
        rootView.setScaleY(0f);
        rootView.setAlpha(0f);

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(rootView, "scaleX", 0f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(rootView, "scaleY", 0f, 1f);
        ObjectAnimator alpha  = ObjectAnimator.ofFloat(rootView, "alpha", 0f, 1f);
        scaleX.setDuration(400);
        scaleY.setDuration(400);
        alpha.setDuration(400);
        scaleX.setInterpolator(new OvershootInterpolator());
        scaleY.setInterpolator(new OvershootInterpolator());
        scaleX.start();
        scaleY.start();
        alpha.start();

        TextView tvConfetti = rootView.findViewById(R.id.tvConfetti);
        ObjectAnimator confettiBounce = ObjectAnimator.ofFloat(tvConfetti, "translationY", 0f, -18f, 0f);
        confettiBounce.setDuration(900);
        confettiBounce.setStartDelay(400);
        confettiBounce.setRepeatCount(ObjectAnimator.INFINITE);
        confettiBounce.start();

        dialog.setOnDismissListener(d -> confettiBounce.cancel());
    }
}