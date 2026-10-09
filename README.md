# Yujoy 🎉

A mobile app for **Yeditepe University** students to discover and join club events, manage club memberships, and track reservations.

---

## Features

- **Authentication** — Sign up with your university email (`@std.yeditepe.edu.tr`), email verification, forgot password
- **Events** — Browse all upcoming events, view details, capacity tracking with progress bar
- **Reservations** — View events you've registered for in one place
- **Clubs** — Discover university clubs, view club details and members
- **Profile** — Edit your profile, change password, view history
- **Role-based access**
  - `MEMBER` — Browse events & clubs, manage profile
  - `PRESIDENT` — Create and manage events, edit club info
  - `ADMIN` — Create clubs, manage platform

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Architecture | MVVM |
| Backend | Firebase (Auth, Firestore, Storage) |
| Navigation | Navigation Compose |
| Image Loading | Coil |

---

## Project Structure

```
app/src/main/java/com/yujoy/
├── models/          # Data classes (User, Event, Club, ...)
├── repositories/    # Firebase data layer
├── viewmodels/      # MVVM ViewModels
├── ui/
│   ├── screens/     # All Compose screens
│   ├── components/  # Reusable UI components
│   └── theme/       # Colors, typography, theme
└── MainActivity.kt
```

---

## Setup

1. Clone the repo
2. Open in Android Studio
3. Add your `google-services.json` from [Firebase Console](https://console.firebase.google.com) to the `app/` directory
4. Build & run

---

## Screenshots

| Events | Reservations | Clubs | Profile |
|--------|-------------|-------|---------|
| ![Events](screenshots/main_screen.png) | ![Reservations](screenshots/reservations.png) | ![Clubs](screenshots/clubs.png) | ![Profile](screenshots/profile.png) |

---

## Developed By

Ediz Koşar — Information Systems and Technologies, Yeditepe University
