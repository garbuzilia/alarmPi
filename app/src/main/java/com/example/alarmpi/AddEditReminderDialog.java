package com.example.alarmpi;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
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
            listener = (OnReminderSavedListener) getParentFragment();
        } catch (ClassCastException e) {
            try {
                listener = (OnReminderSavedListener) context;
            } catch (ClassCastException e2) {
                throw new ClassCastException(context.toString()
                        + " must implement OnReminderSavedListener");
            }
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        selectedCalendar = Calendar.getInstance();

        if (getArguments() != null) {
            editingPosition = getArguments().getInt(ARG_POSITION, -1);
            existingReminder = (Reminder) getArguments().getSerializable(ARG_REMINDER);
            if (existingReminder != null) {
                selectedCalendar.setTime(existingReminder.getDateTime());
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_edit_reminder, null);

        // Находим все элементы с правильными id
        TextView dialogTitleTextView = view.findViewById(R.id.dialogTitleTextView);
        titleEditText = view.findViewById(R.id.titleEditText);
        descriptionEditText = view.findViewById(R.id.descriptionEditText);
        dateTextView = view.findViewById(R.id.dateTextView);
        timeTextView = view.findViewById(R.id.timeTextView);
        Button dateButton = view.findViewById(R.id.dateButton);
        Button timeButton = view.findViewById(R.id.timeButton);
        Button cancelButton = view.findViewById(R.id.cancelButton);
        Button saveButton = view.findViewById(R.id.saveButton);

        // Устанавливаем заголовок диалога
        if (editingPosition == -1) {
            dialogTitleTextView.setText("Добавить напоминание");
        } else {
            dialogTitleTextView.setText("Изменить напоминание");
        }

        // Если редактируем существующее напоминание, заполняем поля
        if (existingReminder != null) {
            titleEditText.setText(existingReminder.getTitle());
            descriptionEditText.setText(existingReminder.getDescription());
        }

        // Обновляем отображение даты и времени
        updateDateTimeDisplay();

        // Обработчики кнопок
        dateButton.setOnClickListener(v -> showDatePickerDialog());
        timeButton.setOnClickListener(v -> showTimePickerDialog());
        cancelButton.setOnClickListener(v -> dismiss());
        saveButton.setOnClickListener(v -> saveReminder());

        builder.setView(view);
        return builder.create();
    }

    private void showDatePickerDialog() {
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
    }

    private void showTimePickerDialog() {
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
    }

    private void updateDateTimeDisplay() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        dateTextView.setText(dateFormat.format(selectedCalendar.getTime()));
        timeTextView.setText(timeFormat.format(selectedCalendar.getTime()));
    }

    private void saveReminder() {
        String title = titleEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();

        if (title.isEmpty()) {
            Toast.makeText(requireContext(), "Введите заголовок", Toast.LENGTH_SHORT).show();
            return;
        }

        Date dateTime = selectedCalendar.getTime();

        if (dateTime.before(new Date())) {
            Toast.makeText(requireContext(),
                    "Выберите будущую дату и время", Toast.LENGTH_SHORT).show();
            return;
        }

        Reminder reminder = new Reminder(title, description, dateTime);

        if (editingPosition == -1) {
            listener.onReminderAdded(reminder);
        } else {
            listener.onReminderUpdated(editingPosition, reminder);
        }

        dismiss();
    }
}