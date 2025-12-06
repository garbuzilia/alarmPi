package com.example.alarmpi;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class ServerSettingsDialog extends DialogFragment {

    private EditText serverUrlEditText;
    private TextView connectionStatusTextView;
    private Button testConnectionButton;
    private ApiService apiService;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_server_settings, null);

        // Инициализация ApiService
        apiService = new ApiService(requireContext());

        // Находим элементы
        serverUrlEditText = view.findViewById(R.id.serverUrlEditText);
        connectionStatusTextView = view.findViewById(R.id.connectionStatusTextView);
        testConnectionButton = view.findViewById(R.id.testConnectionButton);
        Button cancelButton = view.findViewById(R.id.cancelButton);
        Button saveButton = view.findViewById(R.id.saveButton);

        // Загружаем текущие настройки
        serverUrlEditText.setText(apiService.getServerUrl());

        // Обработчик кнопки тестирования
        testConnectionButton.setOnClickListener(v -> testConnection());

        // Обработчик отмены
        cancelButton.setOnClickListener(v -> dismiss());

        // Обработчик сохранения
        saveButton.setOnClickListener(v -> saveSettings());

        builder.setView(view);
        return builder.create();
    }

    private void testConnection() {
        String url = serverUrlEditText.getText().toString().trim();
        if (url.isEmpty()) {
            Toast.makeText(getContext(), "Введите URL сервера", Toast.LENGTH_SHORT).show();
            return;
        }

        connectionStatusTextView.setText("Проверка соединения...");
        testConnectionButton.setEnabled(false);

        // Временно устанавливаем URL для теста
        ApiService testApiService = new ApiService(requireContext());
        testApiService.setServerUrl(url);

        testApiService.testConnection(new ApiService.ConnectionTestCallback() {
            @Override
            public void onConnectionResult(boolean success, String message) {
                connectionStatusTextView.setText(message);
                testConnectionButton.setEnabled(true);

                if (success) {
                    connectionStatusTextView.setTextColor(
                            getResources().getColor(android.R.color.holo_green_dark));
                } else {
                    connectionStatusTextView.setTextColor(
                            getResources().getColor(android.R.color.holo_red_dark));
                }
            }
        });
    }

    private void saveSettings() {
        String url = serverUrlEditText.getText().toString().trim();

        if (url.isEmpty()) {
            Toast.makeText(getContext(), "Введите URL сервера", Toast.LENGTH_SHORT).show();
            return;
        }

        // Сохраняем настройки
        apiService.setServerUrl(url);
        Toast.makeText(getContext(), "Настройки сохранены", Toast.LENGTH_SHORT).show();
        dismiss();
    }
}