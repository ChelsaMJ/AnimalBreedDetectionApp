# Chimera – AI Animal Breed Detection App

Chimera is an Android application that uses Google Gemini AI to detect animal breeds from images and provide intelligent recommendations. It combines Firebase Authentication, modern UI, and powerful AI processing to deliver a smooth user experience.

---

## Features

### AI Breed Detection
- Upload an image of any animal.
- Gemini Vision AI identifies the breed.
- Returns breed details, characteristics, and insights.
- Results displayed in a clean, dedicated result screen.

### AI Recommendations
- Explore page generates recommendations powered by AI.
- Shows similar breeds, suggestions, and insights.
- Includes a separate recommendation result screen.

### User Authentication
- Email/Password Login & Signup using Firebase.
- Secure user management.
- Profile page with user details and logout.

### Smooth Navigation
- Bottom navigation bar with:
  - Detect
  - Explore
  - Profile

---

## Tech Stack

- **Kotlin**
- **Firebase Authentication**
- **Google Gemini AI API**
- **Retrofit & OkHttp**
- **Glide**
- **Material Design Components**
- **Android Jetpack (ViewModel, LiveData, Navigation)**

---

## How to Run the App

1. Clone the repository:
   ```
   git clone https://github.com/yourusername/Chimera.git
   Open the project in Android Studio.
   ```

2. Add your Gemini API Key in Constants.kt.

3. Add your google-services.json for Firebase setup.

4. Let Gradle sync.

5. Run the app on an emulator or a physical device.

---

## Requirements

Android Studio Hedgehog or newer

Minimum SDK: 24+

Gemini API key

Firebase project with Authentication enabled
