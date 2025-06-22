# 📲 Aplikasi Penerjemah Teks & Gambar (Versi Java)

Aplikasi Android berbasis **Java** untuk menerjemahkan teks dan gambar antar berbagai bahasa menggunakan teknologi **AI Gemini dari Google**.

---

## ✨ Fitur Unggulan

- 🔤 **Penerjemah Teks**  
  Terjemahkan teks dengan cepat.

- 🖼️ **Penerjemah Gambar**  
  Ekstrak teks dari gambar dan langsung terjemahkan secara otomatis.

- 🧭 **Antarmuka User-Friendly**  
  Desain material modern, intuitif, dan mudah digunakan.

- 📡 **Dukungan Offline**  
  Fitur dasar dapat digunakan **tanpa koneksi internet**.

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
    git clone https://github.com/username/penerjemah-teks-gambar-java.git
    cd penerjemah-teks-gambar-java
    ```

2. **Atur API Key:
   -Buka file TextFragment.java
   *app/src/main/java/com/example/translator/fragments/TextFragment.java*
   -Buka file ImageFragment.java
   *app/src/main/java/com/example/translator/fragments/ImageFragment.java*

**Cari baris berikut:**
"gemini-1.5-flash", // Updated from "gemini-pro"
"///" // Replace with your API key
Ganti dengan API key Anda

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
│   ├── java/com/example/translator
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
