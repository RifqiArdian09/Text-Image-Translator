package com.example.translate.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
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

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class TextFragment extends Fragment {
    private EditText etTextInput;
    private Spinner spinnerLanguage;
    private Button btnTranslate;
    private TextView tvResult;
    private GenerativeModelFutures model;

    private final String[] languages = {
            "Bahasa Indonesia", "Bahasa Inggris", "Bahasa China",
            "Bahasa Arab", "Bahasa Jepang", "Bahasa Korea",
            "Bahasa Prancis", "Bahasa Jerman", "Bahasa Belanda","Bahasa Philipina","Bahasa Jawa",

    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_text, container, false);

        // Updated model name - using current Gemini 1.5 Flash
        GenerativeModel gm = new GenerativeModel(
                "gemini-1.5-flash", // Updated from "gemini-pro"
                "///" // Replace with your API key
        );
        model = GenerativeModelFutures.from(gm);

        etTextInput = view.findViewById(R.id.etTextInput);
        spinnerLanguage = view.findViewById(R.id.spinnerLanguage);
        btnTranslate = view.findViewById(R.id.btnTranslate);
        tvResult = view.findViewById(R.id.tvResult);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                languages
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLanguage.setAdapter(adapter);

        btnTranslate.setOnClickListener(v -> {
            String text = etTextInput.getText().toString().trim();
            String selectedLang = spinnerLanguage.getSelectedItem().toString();

            if (text.isEmpty()) {
                showSnackbar(getString(R.string.error_empty));
            } else {
                translateText(text, selectedLang);
            }
        });

        return view;
    }

    private void translateText(String text, String language) {
        tvResult.setVisibility(View.VISIBLE);
        tvResult.setText("Sedang menerjemahkan...");
        btnTranslate.setEnabled(false); // Disable button during translation

        // Improved prompt for better translation accuracy
        String prompt = "Terjemahkan teks berikut ke dalam " + language + ". " +
                "Berikan hanya hasil terjemahan tanpa penjelasan tambahan:\n\n" + text;

        Content content = new Content.Builder()
                .addText(prompt)
                .build();

        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
        Executor executor = Executors.newSingleThreadExecutor();

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                requireActivity().runOnUiThread(() -> {
                    btnTranslate.setEnabled(true); // Re-enable button
                    String translatedText = result.getText();
                    if (translatedText != null && !translatedText.trim().isEmpty()) {
                        tvResult.setText(translatedText.trim());
                    } else {
                        tvResult.setText("Tidak ada hasil terjemahan.");
                    }
                });
            }

            @Override
            public void onFailure(@NonNull Throwable t) {
                requireActivity().runOnUiThread(() -> {
                    btnTranslate.setEnabled(true); // Re-enable button
                    String errorMessage = "Gagal menerjemahkan: " + t.getMessage();
                    showSnackbar(errorMessage);
                    tvResult.setText("Terjadi kesalahan. Silakan coba lagi.");
                });
            }
        }, executor);
    }

    private void showSnackbar(String message) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show();
    }
}