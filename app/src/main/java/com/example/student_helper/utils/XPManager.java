package com.example.student_helper.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class XPManager {

    private static final String PREFS_NAME = "xp_prefs";
    private static final String KEY_XP = "xp";
    private static final String KEY_STREAK = "streak";
    private static final String KEY_LAST_DATE = "last_date";

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static int getXP(Context context) {
        return getPrefs(context).getInt(KEY_XP, 0);
    }

    public static int getStreak(Context context) {
        return getPrefs(context).getInt(KEY_STREAK, 0);
    }

    public static int getLevel(Context context) {
        return (getXP(context) / 100) + 1;
    }

    public static int getXPInLevel(Context context) {
        return getXP(context) % 100;
    }

    public static void addXP(Context context, int amount) {
        SharedPreferences prefs = getPrefs(context);
        int current = prefs.getInt(KEY_XP, 0);
        prefs.edit().putInt(KEY_XP, current + amount).apply();
    }

    public static void checkAndUpdateStreak(Context context) {
        SharedPreferences prefs = getPrefs(context);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        String today = sdf.format(new Date());
        String lastDate = prefs.getString(KEY_LAST_DATE, "");
        int streak = prefs.getInt(KEY_STREAK, 0);

        if (today.equals(lastDate)) return; // Već uračunato danas

        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DAY_OF_YEAR, -1);
        String yesterdayStr = sdf.format(yesterday.getTime());

        int newStreak = yesterdayStr.equals(lastDate) ? streak + 1 : 1;
        int currentXP = prefs.getInt(KEY_XP, 0);

        prefs.edit()
                .putString(KEY_LAST_DATE, today)
                .putInt(KEY_STREAK, newStreak)
                .putInt(KEY_XP, currentXP + 5) // +5 XP za dnevni streak
                .apply();
    }
}