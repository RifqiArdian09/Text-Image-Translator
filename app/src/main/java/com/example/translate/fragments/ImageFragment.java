package com.example.translate.fragments;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.translate.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ImageFragment extends Fragment {
    private ImageView ivImagePreview;
    private Spinner spinnerLanguage;
    private Button btnTranslate;
    private ImageButton btnCopy;
    private TextView tvResult;
    private Bitmap selectedImage;
    private GenerativeModelFutures visionModel;

    private final String[] languages = {
            "Bahasa Indonesia", "Bahasa Inggris", "Bahasa China",
            "Bahasa Arab", "Bahasa Jepang", "Bahasa Korea",
            "Bahasa Prancis", "Bahasa Jerman","Bahasa Belanda", "Bahasa Filipina","Bahasa Jawa",
    };

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    try {
                        selectedImage = MediaStore.Images.Media.getBitmap(
                                requireActivity().getContentResolver(), imageUri);
                        ivImagePreview.setImageBitmap(selectedImage);
                        btnTranslate.setVisibility(View.VISIBLE);
                    } catch (IOException e) {
                        e.printStackTrace();
                        showSnackbar("Gagal memuat gambar");
                    }
                }
            });

    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Bundle extras = result.getData().getExtras();
                    selectedImage = (Bitmap) extras.get("data");
                    ivImagePreview.setImageBitmap(selectedImage);
                    btnTranslate.setVisibility(View.VISIBLE);
                }
            });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image, container, false);

        GenerativeModel gm = new GenerativeModel(
                "gemini-1.5-flash",
                "Replace Your API"
        );
        visionModel = GenerativeModelFutures.from(gm);

        ivImagePreview = view.findViewById(R.id.ivImagePreview);
        spinnerLanguage = view.findViewById(R.id.spinnerLanguage);
        btnTranslate = view.findViewById(R.id.btnTranslate);
        btnCopy = view.findViewById(R.id.btnCopy);
        tvResult = view.findViewById(R.id.tvResult);

        btnTranslate.setVisibility(View.GONE);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                languages
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLanguage.setAdapter(adapter);

        ivImagePreview.setOnClickListener(v -> showImagePickerDialog());

        btnTranslate.setOnClickListener(v -> {
            String selectedLang = spinnerLanguage.getSelectedItem().toString();

            if (selectedImage == null) {
                showSnackbar(getString(R.string.error_empty));
            } else {
                translateImage(selectedLang);
            }
        });

        btnCopy.setOnClickListener(v -> copyTextToClipboard());

        return view;
    }

    private void showImagePickerDialog() {
        String[] options = {getString(R.string.gallery), getString(R.string.camera)};

        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.choose_image_source)
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        openGallery();
                    } else {
                        openCamera();
                    }
                })
                .show();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        galleryLauncher.launch(intent);
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
            cameraLauncher.launch(intent);
        } else {
            showSnackbar("Kamera tidak tersedia");
        }
    }

    private void translateImage(String language) {
        if (selectedImage == null) return;

        tvResult.setVisibility(View.VISIBLE);
        tvResult.setText("Sedang menganalisis dan menerjemahkan gambar...");
        btnCopy.setVisibility(View.GONE);
        btnTranslate.setEnabled(false);

        String prompt = "Analisis gambar ini dan lakukan hal berikut:\n" +
                "1. Identifikasi dan ekstrak semua teks yang terlihat dalam gambar\n" +
                "2. Jika ada teks, terjemahkan ke dalam " + language + "\n" +
                "3. Jika tidak ada teks, berikan deskripsi singkat tentang gambar dalam " + language + "\n" +
                "4. Berikan hanya hasil terjemahan atau deskripsi tanpa penjelasan tambahan";

        Content content = new Content.Builder()
                .addText(prompt)
                .addImage(selectedImage)
                .build();

        ListenableFuture<GenerateContentResponse> response = visionModel.generateContent(content);
        Executor executor = Executors.newSingleThreadExecutor();

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                requireActivity().runOnUiThread(() -> {
                    btnTranslate.setEnabled(true);
                    String translatedText = result.getText();
                    if (translatedText != null && !translatedText.trim().isEmpty()) {
                        tvResult.setText(translatedText.trim());
                        btnCopy.setVisibility(View.VISIBLE);
                    } else {
                        tvResult.setText("Tidak ada teks yang dapat diidentifikasi dalam gambar.");
                        btnCopy.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onFailure(@NonNull Throwable t) {
                requireActivity().runOnUiThread(() -> {
                    btnTranslate.setEnabled(true);
                    String errorMessage = "Gagal menerjemahkan gambar: " + t.getMessage();
                    showSnackbar(errorMessage);
                    tvResult.setText("Terjadi kesalahan. Silakan coba lagi.");
                    btnCopy.setVisibility(View.GONE);
                });
            }
        }, executor);
    }

    private void copyTextToClipboard() {
        if (tvResult.getText() != null && !tvResult.getText().toString().isEmpty()) {
            ClipboardManager clipboard = (ClipboardManager) requireActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Translation", tvResult.getText());
            clipboard.setPrimaryClip(clip);

            // Add animation feedback
            btnCopy.animate()
                    .scaleX(1.2f)
                    .scaleY(1.2f)
                    .setDuration(100)
                    .withEndAction(() -> btnCopy.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(100)
                            .start())
                    .start();

            showSnackbar(getString(R.string.text_copied));
        } else {
            showSnackbar("Tidak ada teks untuk disalin");
        }
    }

    private void showSnackbar(String message) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show();
    }
}