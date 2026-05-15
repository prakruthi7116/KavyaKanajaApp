📖 Kavya Kanaja (ಕಾವ್ಯ ಕನಜ)
Bridging the Heritage Gap for the Gen-Z Generation
Kavya Kanaja (Poetry Granary) is a "Literary Revival" Android application designed to make classical Kannada poetry accessible, understandable, and engaging for the modern generation. It acts as a digital bridge, using modern technology to preserve and promote one of the world's oldest living languages.
🚀 The Vision
Karnataka has a rich literary history spanning centuries, yet classical verses (especially in "Old Kannada") are becoming increasingly difficult for the younger generation to access or understand. Kavya Kanaja solves this by providing a distraction-free, "Duolingo-style" learning experience for classical literature.

✨ Key Features

📜 Poem of the Day: A curated, famous Kannada verse presented every 24 hours on an elegant, parchment-style digital card.

💡 Interactive Word Meanings: Modern "Tap-to-Learn" technology where difficult traditional words are underlined; tapping them reveals their modern Kannada and English meanings.

🎙️ Listen & Learn: Integrated Audio Recitation using Android's Text-to-Speech engine to help users master the rhythm and pronunciation of classical poetry.

🏛️ Poet's Corner: A dedicated biographical grid featuring portraits and life stories of Jnanpith awardees and legendary authors.

📚 Library Archive: A searchable database of 50+ classical poems categorized by era (Halegannada, Vachana, Dasa Sahitya, Navodaya) and theme.

🛠️ Tech Stack
Language: Kotlin - 100% Type-safe and modern logic.

UI Framework: Jetpack Compose - Declarative UI for a fluid, reactive experience.

Design System: Material 3 with a custom "Modern Heritage" theme (Navy Blue & Soft Cream).

Media: Android TTS (Text-to-Speech) API for localized Kannada recitations.

Data Management: Local JSON Repository for efficient, offline-first content delivery.

Image Loading: Coil - For high-performance rendering of historical poet portraits.

🏗️ Architecture
The app follows the Modern Android Architecture patterns:

Presentation Layer: Composable-based UI driven by reactive state.

Data Layer: A clean repository pattern parsing local JSON assets to ensure high speed and zero latency.

Theme Engine: A centralized Material3 design system ensuring visual consistency across all components.

📂 Project Structure
Java
Kavya Kanaja/
├── app/│   ├── src/main/
│   │   ├── java/com/kavyakanaja/app/MainActivity.kt  <-- Core Logic & UI
│   │   ├── res/
│   │   │   ├── drawable/                             <-- Historical Poet Portraits
│   │   │   └── raw/poetry_database.json              <-- 50+ Poems Repository
│   └── build.gradle.kts                              <-- Dependency Management
└── README.md

⚙️ Build Readiness & Installation

Clone this repository.

Open the project in Android Studio (Ladybug or newer).

Ensure you have JDK 17 configured in your Gradle settings.

Connect an Android device or Emulator (API 24+ recommended).

Click Run ▶.
Note: For the audio feature, ensure a Kannada TTS engine (like Google TTS) is installed in your device settings.
 
🎯 Impact Goals

Cultural Renaissance: Reconnecting youth with their linguistic roots through an "aesthetic" and "cool" medium.

Educational Enrichment: Providing a supplementary tool for students studying the Kannada literature curriculum.

Soft Power: Preserving and promoting the beauty of classical Kannada in the global digital landscape.

👤 Author
•
Project Lead:Prakruthi S
•
Category: National Pride / Cultural Preservation
•
Project Title ID: 75
