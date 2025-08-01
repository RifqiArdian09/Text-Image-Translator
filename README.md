# 📲 Aplikasi Penerjemah Teks & Gambar 

Aplikasi Android berbasis **Java** untuk menerjemahkan teks dan gambar antar berbagai bahasa menggunakan teknologi **AI Gemini dari Google**.

---

## ✨ Fitur Unggulan

- 🔤 **Penerjemah Teks**  
  Terjemahkan teks dengan cepat.

- 🖼️ **Penerjemah Gambar**  
  Ekstrak teks dari gambar dan langsung terjemahkan secara otomatis.

- 🧭 **Antarmuka User-Friendly**  
  Desain material modern, intuitif, dan mudah digunakan.



---

## 📋 Persyaratan

- Android Studio (versi terbaru)
- JDK 17 atau lebih tinggi
- Perangkat/emulator Android dengan API level **24+**
- API Key dari **Google Cloud**

---

## 🛠️ Panduan Instalasi

1. **Clone repository**:

    ```bash
    https://github.com/RifqiArdian09/Text-Image-Translator.git
    cd translate
    ```

2. **Atur API Key Anda**:

    * Buka file 
    ```
    app/src/main/java/com/example/translate/fragments/TextFragment.java.
    ```
    * Buka file
    ```
    app/src/main/java/com/example/translate/fragments/ImageFragment.java.
    ```
    - Cari baris berikut di kedua file:
    ```
    "gemini-1.5-flash", // Updated from "gemini-pro"
     "///" // Replace with your API key
    ```
Ganti "///" dengan API Key Google Gemini Anda.

3. **Buka project** di Android Studio dan jalankan ke emulator atau perangkat.

---

## 🔑 Cara Mendapatkan API Key Gemini

1. Kunjungi [Google AI Studio](https://makersuite.google.com/)
2. Buat atau gunakan project yang ada.
3. Generate API Key di bagian **"Get API Key"**.
4. Aktifkan layanan **"Generative Language API"** di [Google Cloud Console](https://console.cloud.google.com/).

---

## 🏗️ Struktur Kode Java

```text
/app
├── src/main
│   ├── java/com/example/translate
│   │   ├── MainActivity.java         # Aktivitas utama
│   │   ├── adapters
│   │   │   └── ViewPagerAdapter.java # Pengatur tab & fragment
│   │   └── fragments
│   │       ├── TextFragment.java     # Logika terjemah teks
│   │       └── ImageFragment.java    # Logika terjemah gambar
│   ├── res
│   │   ├── layout
│   │   │   ├── activity_main.xml
│   │   │   ├── fragment_image.xml
│   │   │   └── fragment_text.xml
│   └── AndroidManifest.xml
