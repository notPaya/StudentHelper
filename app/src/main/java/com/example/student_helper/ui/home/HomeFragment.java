package com.example.student_helper.ui.home;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.student_helper.R;
import com.example.student_helper.database.entity.ScheduleItem;
import com.example.student_helper.databinding.FragmentHomeBinding;
import com.example.student_helper.utils.XPManager;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel viewModel;
    private ActivityResultLauncher<String> pickImageLauncher;

    private final List<String> quotes = Arrays.asList(
            "\"Svaki ekspert je bio početnik.\"",
            "\"Učenje je jedina stvar čiji se um nikada ne zasiti.\"",
            "\"Što više znaš, to manje znaš.\"",
            "\"Uspjeh je zbir malih napora ponavljanih svaki dan.\"",
            "\"Obrazovanje je najmoćnije oružje kojim možeš promijeniti svijet.\""
    );

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Registruj image picker
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {

                        // Kopira sliku u interno skladište
                        try {
                            InputStream inputStream = requireContext()
                                    .getContentResolver().openInputStream(uri);
                            File outputFile = new File(
                                    requireContext().getFilesDir(), "profile_pic.jpg");
                            FileOutputStream outputStream = new FileOutputStream(outputFile);

                            byte[] buffer = new byte[1024];
                            int length;
                            while ((length = inputStream.read(buffer)) > 0) {
                                outputStream.write(buffer, 0, length);
                            }
                            outputStream.close();
                            inputStream.close();

                            // Sačuva putanju
                            SharedPreferences prefs = requireContext()
                                    .getSharedPreferences("profile", Context.MODE_PRIVATE);
                            prefs.edit().putString("profile_image",
                                    outputFile.getAbsolutePath()).apply();

                            // Prikaže sliku
                            Bitmap bitmap = BitmapFactory.decodeFile(
                                    outputFile.getAbsolutePath());
                            binding.ivProfile.setImageBitmap(bitmap);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // Pozdrav
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (hour < 12)      binding.tvGreeting.setText("Dobro jutro! 👋");
        else if (hour < 18) binding.tvGreeting.setText("Dobar dan! 👋");
        else                binding.tvGreeting.setText("Dobra večer! 👋");

        // Datum
        SimpleDateFormat sdf = new SimpleDateFormat(
                "EEEE, dd. MMMM yyyy.", new Locale("bs", "BA"));
        String date = sdf.format(new Date());
        binding.tvDate.setText(date.substring(0, 1).toUpperCase() + date.substring(1));

        // Citati
        binding.tvQuote.setText(quotes.get(new Random().nextInt(quotes.size())));

        // Statistike
        updateStats();

        // Učitaj profil
        loadProfile();

        // Klik na sliku → otvori galeriju
        binding.layoutProfileImage.setOnClickListener(v ->
                pickImageLauncher.launch("image/*"));

        // Klik na ime → dialog za promjenu imena
        binding.tvName.setOnClickListener(v -> showEditNameDialog());

        // Dark mode switch
        SharedPreferences settingsPrefs = requireContext()
                .getSharedPreferences("settings", Context.MODE_PRIVATE);
        boolean isDark = settingsPrefs.getBoolean("dark_mode", false);
        binding.switchDarkMode.setChecked(isDark);
        binding.switchDarkMode.setOnCheckedChangeListener((btn, isChecked) -> {
            settingsPrefs.edit().putBoolean("dark_mode", isChecked).apply();
            AppCompatDelegate.setDefaultNightMode(
                    isChecked ? AppCompatDelegate.MODE_NIGHT_YES
                            : AppCompatDelegate.MODE_NIGHT_NO
            );
        });

        // Pregled za danasnje predmete
        viewModel.getTodayClasses().observe(getViewLifecycleOwner(), classes -> {
            binding.layoutTodayClasses.removeAllViews();
            if (classes == null || classes.isEmpty()) {
                TextView tv = new TextView(requireContext());
                tv.setText(getString(R.string.no_classes_today));
                tv.setTextColor(requireContext().getColor(R.color.colorTextSecondary));
                tv.setTextSize(14f);
                binding.layoutTodayClasses.addView(tv);
            } else {
                for (ScheduleItem item : classes) {
                    TextView tv = new TextView(requireContext());
                    String text = "⏰ " + item.startTime + " – " + item.subject;
                    if (!item.room.isEmpty()) text += "  📍" + item.room;
                    tv.setText(text);
                    tv.setTextSize(14f);
                    tv.setPadding(0, 6, 0, 6);
                    tv.setTextColor(requireContext().getColor(R.color.colorOnBackground));
                    binding.layoutTodayClasses.addView(tv);
                }
            }
        });

        // Observer za sljedeci ispit
        viewModel.getNextExam().observe(getViewLifecycleOwner(), exam -> {
            if (exam == null) {
                binding.tvNextExamName.setText("Nema nadolazećih ispita 🎉");
                binding.tvNextExamDays.setVisibility(View.GONE);
            } else {
                long daysLeft = TimeUnit.MILLISECONDS.toDays(
                        exam.examDate - System.currentTimeMillis());
                binding.tvNextExamName.setText(exam.subject);
                binding.tvNextExamDays.setVisibility(View.VISIBLE);
                if (daysLeft <= 0)      binding.tvNextExamDays.setText("Danas! ⚠️");
                else if (daysLeft == 1) binding.tvNextExamDays.setText("Sutra! 😰");
                else binding.tvNextExamDays.setText("Za " + daysLeft + " dana");
            }
        });

        viewModel.loadData();
    }

    private void loadProfile() {
        SharedPreferences prefs = requireContext()
                .getSharedPreferences("profile", Context.MODE_PRIVATE);

        // Učitaj ime
        String savedName = prefs.getString("profile_name", null);
        if (savedName != null && !savedName.isEmpty()) {
            binding.tvName.setText(savedName);
        }

        // Učitaj sliku
        String imagePath = prefs.getString("profile_image", null);
        if (imagePath != null) {
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                binding.ivProfile.setImageBitmap(bitmap);
            }
        }
    }

    private void showEditNameDialog() {
        SharedPreferences prefs = requireContext()
                .getSharedPreferences("profile", Context.MODE_PRIVATE);
        String currentName = prefs.getString("profile_name", "");

        // Input polje za ime
        EditText input = new EditText(requireContext());
        input.setHint("Upiši svoje ime...");
        input.setText(currentName);
        input.setPadding(40, 20, 40, 20);

        new AlertDialog.Builder(requireContext())
                .setTitle("✏️ Promijeni ime")
                .setView(input)
                .setPositiveButton("Sačuvaj", (dialog, which) -> {
                    String newName = input.getText().toString().trim();
                    if (!newName.isEmpty()) {
                        prefs.edit().putString("profile_name", newName).apply();
                        binding.tvName.setText(newName);
                    }
                })
                .setNegativeButton("Odustani", null)
                .show();
    }

    private void updateStats() {
        binding.tvStreak.setText(XPManager.getStreak(requireContext()) + " dana");
        binding.tvLevel.setText("Nivo " + XPManager.getLevel(requireContext()));
        binding.xpProgressBar.setProgress(XPManager.getXPInLevel(requireContext()));
        binding.tvXP.setText(XPManager.getXP(requireContext()) + " XP ukupno");
    }

    @Override
    public void onResume() {
        super.onResume();
        updateStats();
        viewModel.loadData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}