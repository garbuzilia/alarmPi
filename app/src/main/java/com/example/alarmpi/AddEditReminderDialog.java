package com.example.alarmpi;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddEditReminderDialog extends DialogFragment {

    private static final String TAG = "ReminderDialog";

    public interface OnReminderSavedListener {
        void onReminderAdded(Reminder reminder);
        void onReminderUpdated(int position, Reminder reminder);
    }

    private OnReminderSavedListener listener;
    private EditText titleEditText, descriptionEditText;
    private TextView dateTextView, timeTextView;
    private Calendar selectedCalendar;
    private int editingPosition = -1;
    private Reminder existingReminder;

    private static final String ARG_POSITION = "position";
    private static final String ARG_REMINDER = "reminder";

    public static AddEditReminderDialog newInstance() {
        return new AddEditReminderDialog();
    }

    public static AddEditReminderDialog newInstance(int position, Reminder reminder) {
        AddEditReminderDialog dialog = new AddEditReminderDialog();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        args.putSerializable(ARG_REMINDER, reminder);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (OnReminderSavedListener) context;
            Log.d(TAG, "Listener attached successfully");
        } catch (ClassCastException e) {
            Log.e(TAG, "Activity must implement OnReminderSavedListener", e);
            throw new ClassCastException(context.toString()
                    + " must implement OnReminderSavedListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        try {
            selectedCalendar = Calendar.getInstance();

            if (getArguments() != null) {
                editingPosition = getArguments().getInt(ARG_POSITION, -1);
                existingReminder = (Reminder) getArguments().getSerializable(ARG_REMINDER);
                if (existingReminder != null && existingReminder.getDateTime() != null) {
                    selectedCalendar.setTime(existingReminder.getDateTime());
                    Log.d(TAG, "Editing existing reminder at position: " + editingPosition);
                }
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
            LayoutInflater inflater = requireActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.dialog_add_edit_reminder, null);

            // Инициализация элементов
            initViews(view);

            // Установка данных
            populateData();

            // Установка обработчиков
            setClickListeners(view);

            builder.setView(view);
            return builder.create();

        } catch (Exception e) {
            Log.e(TAG, "Error creating dialog", e);
            Toast.makeText(getContext(), "Ошибка создания диалога", Toast.LENGTH_SHORT).show();
            return super.onCreateDialog(savedInstanceState);
        }
    }

    private void initViews(View view) {
        titleEditText = view.findViewById(R.id.titleEditText);
        descriptionEditText = view.findViewById(R.id.descriptionEditText);
        dateTextView = view.findViewById(R.id.dateTextView);
        timeTextView = view.findViewById(R.id.timeTextView);
    }

    private void populateData() {
        // Установка текущей даты и времени по умолчанию
        updateDateTimeDisplay();

        // Если редактируем существующее напоминание
        if (existingReminder != null) {
            titleEditText.setText(existingReminder.getTitle());
            descriptionEditText.setText(existingReminder.getDescription());
            if (existingReminder.getDateTime() != null) {
                selectedCalendar.setTime(existingReminder.getDateTime());
                updateDateTimeDisplay();
            }
        }
    }

    private void setClickListeners(View view) {
        Button dateButton = view.findViewById(R.id.dateButton);
        Button timeButton = view.findViewById(R.id.timeButton);
        Button cancelButton = view.findViewById(R.id.cancelButton);
        Button saveButton = view.findViewById(R.id.saveButton);

        dateButton.setOnClickListener(v -> showDatePickerDialog());
        timeButton.setOnClickListener(v -> showTimePickerDialog());
        cancelButton.setOnClickListener(v -> dismiss());
        saveButton.setOnClickListener(v -> saveReminder());
    }

    private void showDatePickerDialog() {
        try {
            int year = selectedCalendar.get(Calendar.YEAR);
            int month = selectedCalendar.get(Calendar.MONTH);
            int day = selectedCalendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    requireContext(),
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        selectedCalendar.set(Calendar.YEAR, selectedYear);
                        selectedCalendar.set(Calendar.MONTH, selectedMonth);
                        selectedCalendar.set(Calendar.DAY_OF_MONTH, selectedDay);
                        updateDateTimeDisplay();
                    },
                    year, month, day
            );

            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            datePickerDialog.show();

        } catch (Exception e) {
            Log.e(TAG, "Error showing date picker", e);
            Toast.makeText(getContext(), "Ошибка выбора даты", Toast.LENGTH_SHORT).show();
        }
    }

    private void showTimePickerDialog() {
        try {
            int hour = selectedCalendar.get(Calendar.HOUR_OF_DAY);
            int minute = selectedCalendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    requireContext(),
                    (view, selectedHour, selectedMinute) -> {
                        selectedCalendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                        selectedCalendar.set(Calendar.MINUTE, selectedMinute);
                        selectedCalendar.set(Calendar.SECOND, 0);
                        updateDateTimeDisplay();
                    },
                    hour, minute, true
            );

            timePickerDialog.show();

        } catch (Exception e) {
            Log.e(TAG, "Error showing time picker", e);
            Toast.makeText(getContext(), "Ошибка выбора времени", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateDateTimeDisplay() {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

            Date currentDate = selectedCalendar.getTime();
            dateTextView.setText(dateFormat.format(currentDate));
            timeTextView.setText(timeFormat.format(currentDate));

        } catch (Exception e) {
            Log.e(TAG, "Error updating date/time display", e);
        }
    }

    private void saveReminder() {
        try {
            String title = titleEditText.getText().toString().trim();
            String description = descriptionEditText.getText().toString().trim();

            if (title.isEmpty()) {
                Toast.makeText(requireContext(), "Введите заголовок", Toast.LENGTH_SHORT).show();
                return;
            }

            Date dateTime = selectedCalendar.getTime();
            Date currentTime = new Date();

            // Проверка, что дата не в прошлом (с допуском в 1 минуту)
            if (dateTime.before(new Date(currentTime.getTime() - 60000))) {
                Toast.makeText(requireContext(),
                        "Выберите будущую дату и время", Toast.LENGTH_SHORT).show();
                return;
            }

            Reminder reminder = new Reminder(title, description, dateTime);

            if (listener != null) {
                if (editingPosition == -1) {
                    listener.onReminderAdded(reminder);
                } else {
                    listener.onReminderUpdated(editingPosition, reminder);
                }
            } else {
                Log.e(TAG, "Listener is null!");
                Toast.makeText(requireContext(), "Ошибка сохранения", Toast.LENGTH_SHORT).show();
            }

            dismiss();

        } catch (Exception e) {
            Log.e(TAG, "Error saving reminder", e);
            Toast.makeText(requireContext(), "Ошибка сохранения", Toast.LENGTH_SHORT).show();
        }
    }
}