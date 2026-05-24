package com.example.student_helper.ui.timer;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.student_helper.R;
import com.example.student_helper.utils.XPManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.slider.Slider;

import java.util.Locale;

public class TimerFragment extends Fragment {

    private static final int BREAK_SECONDS  = 5 * 60;
    private static final int TOTAL_SESSIONS = 4;

    private int WORK_SECONDS_CUSTOM = 25 * 60;

    private CountDownTimer countDownTimer;
    private boolean isRunning = false;
    private boolean isBreak   = false;
    private int timeLeftSeconds   = WORK_SECONDS_CUSTOM;
    private int sessionsCompleted = 0;

    private CircularProgressIndicator progressTimer;
    private TextView tvTime, tvMode, tvSessions, tvMotivation, tvSelectedMinutes;
    private MaterialButton btnStartPause, btnReset;
    private Slider sliderMinutes;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_timer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressTimer     = view.findViewById(R.id.progressTimer);
        tvTime            = view.findViewById(R.id.tvTime);
        tvMode            = view.findViewById(R.id.tvMode);
        tvSessions        = view.findViewById(R.id.tvSessions);
        tvMotivation      = view.findViewById(R.id.tvMotivation);
        btnStartPause     = view.findViewById(R.id.btnStartPause);
        btnReset          = view.findViewById(R.id.btnReset);
        sliderMinutes     = view.findViewById(R.id.sliderMinutes);
        tvSelectedMinutes = view.findViewById(R.id.tvSelectedMinutes);

        // Postavi početnu vrijednost iz slidera
        int initialMinutes = (int) sliderMinutes.getValue();
        WORK_SECONDS_CUSTOM = initialMinutes * 60;
        timeLeftSeconds = WORK_SECONDS_CUSTOM;
        tvSelectedMinutes.setText(initialMinutes + " min");

        // Listener za slider
        sliderMinutes.addOnChangeListener((slider, value, fromUser) -> {
            int minutes = (int) value;
            tvSelectedMinutes.setText(minutes + " min");
            if (!isRunning && !isBreak) {
                WORK_SECONDS_CUSTOM = minutes * 60;
                timeLeftSeconds = WORK_SECONDS_CUSTOM;
                updateUI();
            }
        });

        updateUI();

        btnStartPause.setOnClickListener(v -> {
            if (isRunning) pauseTimer();
            else startTimer();
        });

        btnReset.setOnClickListener(v -> resetTimer());
    }

    private void startTimer() {
        isRunning = true;
        sliderMinutes.setEnabled(false);
        btnStartPause.setText("⏸ Pauziraj");
        tvMotivation.setText("Fokusiraj se! Možeš ti to! 💪");

        countDownTimer = new CountDownTimer(timeLeftSeconds * 1000L, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftSeconds = (int) (millisUntilFinished / 1000);
                updateUI();
            }

            @Override
            public void onFinish() {
                timeLeftSeconds = 0;
                updateUI();
                onTimerFinished();
            }
        }.start();
    }

    private void pauseTimer() {
        isRunning = false;
        if (countDownTimer != null) countDownTimer.cancel();
        btnStartPause.setText("▶ Nastavi");
        tvMotivation.setText("Pauzirano. Pritisni Nastavi kad si spreman.");
    }

    private void resetTimer() {
        isRunning = false;
        isBreak   = false;
        if (countDownTimer != null) countDownTimer.cancel();
        WORK_SECONDS_CUSTOM = (int) sliderMinutes.getValue() * 60;
        timeLeftSeconds = WORK_SECONDS_CUSTOM;
        btnStartPause.setText("▶ Pokreni");
        tvMotivation.setText("Pritisni Start i počni učiti!");
        sliderMinutes.setEnabled(true);
        updateUI();
    }

    private void onTimerFinished() {
        isRunning = false;

        if (!isBreak) {
            sessionsCompleted++;
            XPManager.addXP(requireContext(), 50);
            isBreak = true;
            timeLeftSeconds = BREAK_SECONDS;
            btnStartPause.setText("▶ Pokreni pauzu");
            tvMotivation.setText("🎉 +50 XP! Bravo! Odmori se malo ☕");
        } else {
            isBreak = false;
            timeLeftSeconds = WORK_SECONDS_CUSTOM;

            if (sessionsCompleted >= TOTAL_SESSIONS) {
                sessionsCompleted = 0;
                tvMotivation.setText("🏆 Završio si 4 sesije! Fenomenalno!");
            } else {
                tvMotivation.setText("Pauza gotova! Spreman za sljedeću sesiju? 💪");
            }
            btnStartPause.setText("▶ Pokreni");
        }
        updateUI();
    }

    private void updateUI() {
        int min = timeLeftSeconds / 60;
        int sec = timeLeftSeconds % 60;
        tvTime.setText(String.format(Locale.getDefault(), "%02d:%02d", min, sec));

        int maxTime = isBreak ? BREAK_SECONDS : WORK_SECONDS_CUSTOM;
        progressTimer.setMax(maxTime);
        progressTimer.setProgress(timeLeftSeconds);

        if (isBreak) {
            tvMode.setText("☕ Pauza");
            tvMode.setTextColor(requireContext().getColor(R.color.examSoon));
        } else {
            tvMode.setText("🎯 Učenje");
            tvMode.setTextColor(requireContext().getColor(R.color.colorPrimary));
        }

        tvSessions.setText("Sesija: " + sessionsCompleted + " / " + TOTAL_SESSIONS);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (countDownTimer != null) countDownTimer.cancel();
    }
}