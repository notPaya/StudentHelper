# 📱 Student Helper

> Android aplikacija za organizaciju studentskog života: raspored, ispiti, bilješke i study timer.

---

## 📖 O aplikaciji

**Student Helper** je Android mobilna aplikacija razvijena projekat u sklopu predmeta **Razvoj mobilnih aplikacija**.

Aplikacija pomaže studentima da:
- Organizuju raspored predavanja
- Prate rokove ispita s odbrojavanjem
- Vode bilješke po predmetima
- Uče efikasnije pomoću Pomodoro Study Timera
- Ostanu motivirani kroz XP i Streak sistem

---

## ✨ Funkcionalnosti

| Funkcionalnost | Opis |
|---|---|
| 🏠 **Home** | Pregled dana, streak, XP nivo, sljedeći ispit |
| 📅 **Raspored** | Dodavanje predavanja po danima (Pon–Pet) |
| 📝 **Ispiti** | Lista ispita s odbrojavanjem i notifikacijama |
| 📓 **Bilješke** | Bilješke u bojama, staggered grid prikaz |
| ⏱️ **Study Timer** | Pomodoro timer s podešavanjem trajanja (5–60 min) |
| 🔥 **Streak sistem** | Dnevni streak za konzistentnost |
| ⭐ **XP sistem** | Skupljanje XP poena za svaku akciju |
| 🌙 **Dark/Light tema** | Promjena teme s jednim klikom |
| 👤 **Profil** | Slika profila i ime korisnika |
| 🎉 **Onboarding** | Ekran dobrodošlice pri prvom pokretanju |

---

## 🛠️ Tehnologije

- **Jezik:** Java
- **Min SDK:** API 29 (Android 10)
- **Target SDK:** API 34 (Android 14)
- **Arhitektura:** MVVM (Model-View-ViewModel)
- **Baza podataka:** Room (SQLite)
- **UI:** Material Design 3
- **Navigation:** Navigation Component + Bottom Navigation

### Biblioteke
```
androidx.room:room-runtime:2.6.1
androidx.navigation:navigation-fragment:2.7.7
androidx.lifecycle:lifecycle-viewmodel:2.7.0
androidx.lifecycle:lifecycle-livedata:2.7.0
com.google.android.material:material:1.11.0
```

---

## 🗃️ Struktura projekta

```
app/src/main/java/com/example/student_helper/
├── MainActivity.java
├── SplashActivity.java
├── OnboardingActivity.java
├── database/
│   ├── AppDatabase.java
│   ├── entity/
│   │   ├── ScheduleItem.java
│   │   ├── Exam.java
│   │   └── Note.java
│   └── dao/
│       ├── ScheduleDao.java
│       ├── ExamDao.java
│       └── NoteDao.java
├── ui/
│   ├── home/HomeFragment.java + HomeViewModel.java
│   ├── schedule/ScheduleFragment.java + ScheduleViewModel.java + ScheduleAdapter.java
│   ├── exams/ExamsFragment.java + ExamsViewModel.java + ExamsAdapter.java
│   ├── notes/NotesFragment.java + NotesViewModel.java + NotesAdapter.java
│   └── timer/TimerFragment.java
└── utils/
    ├── XPManager.java
    ├── NotificationHelper.java
    └── NotificationReceiver.java
```

---

## 🎮 XP Sistem

| Akcija | XP |
|---|---|
| Otvoriš app (dnevni streak) | +5 XP |
| Dodaš predavanje | +10 XP |
| Dodaš bilješku | +10 XP |
| Dodaš ispit | +15 XP |
| Završiš sesiju učenja | +50 XP |

**Formula:** `Nivo = (ukupni XP / 100) + 1`

---

## 👨‍💻 Autor

**Danis Pajić**

Predmet: Razvoj mobilnih aplikacija

Akademska godina: 2025/2026

---

## 📄 Licenca

Projekat je razvijen u obrazovne svrhe.