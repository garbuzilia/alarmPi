package com.example.alarmpi;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class ApiService {

    private static final String TAG = "ApiService";
    private static final String PREFERENCES_NAME = "ServerSettings";
    private static final String SERVER_URL_KEY = "server_url";
    private static final String DEFAULT_SERVER_URL = "http://192.168.1.100:5000/api/reminders";

    private Context context;
    private SharedPreferences preferences;

    public ApiService(Context context) {
        this.context = context;
        this.preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public void setServerUrl(String url) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(SERVER_URL_KEY, url);
        editor.apply();
    }

    public String getServerUrl() {
        return preferences.getString(SERVER_URL_KEY, DEFAULT_SERVER_URL);
    }

    // Отправка напоминания на сервер
    public void sendReminder(Reminder reminder, String action) {
        new SendReminderTask().execute(reminder, action);
    }

    // Асинхронная задача для отправки данных
    private class SendReminderTask extends AsyncTask<Object, Void, Boolean> {
        private String errorMessage = "";

        @Override
        protected Boolean doInBackground(Object... params) {
            Reminder reminder = (Reminder) params[0];
            String action = (String) params[1];

            try {
                // Формируем JSON объект
                JSONObject jsonReminder = new JSONObject();
                jsonReminder.put("id", reminder.getId());
                jsonReminder.put("title", reminder.getTitle());
                jsonReminder.put("description", reminder.getDescription());

                // Форматируем дату
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                jsonReminder.put("dateTime", sdf.format(reminder.getDateTime()));

                jsonReminder.put("isActive", reminder.isActive());
                jsonReminder.put("action", action); // "add", "update", "delete"

                String serverUrl = getServerUrl();
                Log.d(TAG, "Отправка на сервер: " + serverUrl);
                Log.d(TAG, "Данные: " + jsonReminder.toString());

                // Создаем HTTP соединение
                URL url = new URL(serverUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setRequestProperty("Accept", "application/json");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                conn.setDoOutput(true);

                // Отправляем данные
                OutputStream os = conn.getOutputStream();
                os.write(jsonReminder.toString().getBytes("UTF-8"));
                os.close();

                // Получаем ответ
                int responseCode = conn.getResponseCode();
                Log.d(TAG, "Код ответа: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    Log.d(TAG, "Ответ сервера: " + response.toString());
                    return true;
                } else {
                    errorMessage = "Ошибка сервера: " + responseCode;
                    return false;
                }

            } catch (Exception e) {
                errorMessage = "Ошибка соединения: " + e.getMessage();
                Log.e(TAG, "Ошибка отправки данных", e);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Log.d(TAG, "Данные успешно отправлены на сервер");
            } else {
                Log.e(TAG, "Ошибка отправки: " + errorMessage);
                Toast.makeText(context, "Ошибка отправки на сервер: " + errorMessage,
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    // Проверка соединения с сервером
    public void testConnection(ConnectionTestCallback callback) {
        new TestConnectionTask(callback).execute();
    }

    public interface ConnectionTestCallback {
        void onConnectionResult(boolean success, String message);
    }

    private class TestConnectionTask extends AsyncTask<Void, Void, Boolean> {
        private ConnectionTestCallback callback;
        private String message = "";

        public TestConnectionTask(ConnectionTestCallback callback) {
            this.callback = callback;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                String serverUrl = getServerUrl();
                URL url = new URL(serverUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(3000);
                conn.setReadTimeout(3000);

                int responseCode = conn.getResponseCode();
                message = "Сервер доступен. Код ответа: " + responseCode;
                return responseCode == HttpURLConnection.HTTP_OK;

            } catch (Exception e) {
                message = "Ошибка соединения: " + e.getMessage();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (callback != null) {
                callback.onConnectionResult(success, message);
            }
        }
    }
}