package com.example.alarmpi;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements AddEditReminderDialog.OnReminderSavedListener {

    private static final String TAG = "MainActivity";

    private RecyclerView recyclerView;
    private ReminderAdapter adapter;
    private List<Reminder> reminderList;
    private Button deleteButton, addButton, editButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate started");

        try {
            // Инициализация RecyclerView
            recyclerView = findViewById(R.id.remindersRecyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            // Инициализация списка
            reminderList = new ArrayList<>();
            adapter = new ReminderAdapter(reminderList, this);
            recyclerView.setAdapter(adapter);

            // Находим кнопки
            deleteButton = findViewById(R.id.deleteButton);
            addButton = findViewById(R.id.addButton);
            editButton = findViewById(R.id.editButton);

            // Устанавливаем обработчики
            setupButtonListeners();

            // Добавляем слушатель выбора элемента
            adapter.setOnItemClickListener(new ReminderAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    updateButtonStates(true);
                    Log.d(TAG, "Item clicked at position: " + position);
                }

                @Override
                public void onSwitchChanged(int position, boolean isChecked) {
                    if (position >= 0 && position < reminderList.size()) {
                        reminderList.get(position).setActive(isChecked);
                        String state = isChecked ? "включено" : "выключено";
                        Toast.makeText(MainActivity.this,
                                "Напоминание " + state,
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });

            // Добавляем тестовые данные
            addTestData();

            // Изначально кнопки удаления и редактирования выключены
            updateButtonStates(false);

            Log.d(TAG, "onCreate completed successfully");

        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate", e);
            Toast.makeText(this, "Ошибка инициализации приложения", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupButtonListeners() {
        deleteButton.setOnClickListener(v -> {
            try {
                deleteSelectedReminder();
            } catch (Exception e) {
                Log.e(TAG, "Error deleting reminder", e);
                Toast.makeText(this, "Ошибка удаления", Toast.LENGTH_SHORT).show();
            }
        });

        addButton.setOnClickListener(v -> {
            try {
                showAddDialog();
            } catch (Exception e) {
                Log.e(TAG, "Error showing add dialog", e);
                Toast.makeText(this, "Ошибка открытия диалога", Toast.LENGTH_SHORT).show();
            }
        });

        editButton.setOnClickListener(v -> {
            try {
                showEditDialog();
            } catch (Exception e) {
                Log.e(TAG, "Error showing edit dialog", e);
                Toast.makeText(this, "Ошибка открытия диалога", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateButtonStates(boolean hasSelection) {
        deleteButton.setEnabled(hasSelection);
        editButton.setEnabled(hasSelection);
    }

    private void deleteSelectedReminder() {
        int position = adapter.getSelectedPosition();
        if (position != -1 && position < reminderList.size()) {
            reminderList.remove(position);
            adapter.notifyItemRemoved(position);
            adapter.setSelectedPosition(-1);
            updateButtonStates(false);
            Toast.makeText(this, "Напоминание удалено", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Сначала выберите напоминание", Toast.LENGTH_SHORT).show();
        }
    }

    private void showAddDialog() {
        try {
            AddEditReminderDialog dialog = AddEditReminderDialog.newInstance();
            dialog.show(getSupportFragmentManager(), "add_reminder_dialog");
            Log.d(TAG, "Add dialog shown");
        } catch (Exception e) {
            Log.e(TAG, "Error showing add dialog", e);
            Toast.makeText(this, "Ошибка открытия диалога", Toast.LENGTH_SHORT).show();
        }
    }

    private void showEditDialog() {
        try {
            int position = adapter.getSelectedPosition();
            if (position != -1 && position < reminderList.size()) {
                Reminder reminder = reminderList.get(position);
                AddEditReminderDialog dialog = AddEditReminderDialog.newInstance(position, reminder);
                dialog.show(getSupportFragmentManager(), "edit_reminder_dialog");
                Log.d(TAG, "Edit dialog shown for position: " + position);
            } else {
                Toast.makeText(this, "Сначала выберите напоминание", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error showing edit dialog", e);
            Toast.makeText(this, "Ошибка открытия диалога", Toast.LENGTH_SHORT).show();
        }
    }

    // Реализация методов интерфейса
    @Override
    public void onReminderAdded(Reminder reminder) {
        try {
            reminderList.add(reminder);
            adapter.notifyItemInserted(reminderList.size() - 1);
            Toast.makeText(this, "Напоминание добавлено", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Reminder added: " + reminder.getTitle());
        } catch (Exception e) {
            Log.e(TAG, "Error adding reminder", e);
            Toast.makeText(this, "Ошибка добавления напоминания", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onReminderUpdated(int position, Reminder reminder) {
        try {
            if (position >= 0 && position < reminderList.size()) {
                // Сохраняем состояние активности
                boolean wasActive = reminderList.get(position).isActive();
                reminder.setActive(wasActive);

                reminderList.set(position, reminder);
                adapter.notifyItemChanged(position);
                Toast.makeText(this, "Напоминание обновлено", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Reminder updated at position: " + position);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating reminder", e);
            Toast.makeText(this, "Ошибка обновления напоминания", Toast.LENGTH_SHORT).show();
        }
    }

    private void addTestData() {
        try {
            Calendar cal1 = Calendar.getInstance();
            cal1.add(Calendar.DAY_OF_YEAR, 1);
            cal1.set(Calendar.HOUR_OF_DAY, 10);
            cal1.set(Calendar.MINUTE, 30);

            reminderList.add(new Reminder(
                    "Вынести мусор",
                    "Скоро приедут родители, нужно сделать уборку",
                    cal1.getTime()
            ));

            Calendar cal2 = Calendar.getInstance();
            cal2.add(Calendar.DAY_OF_YEAR, 2);
            cal2.set(Calendar.HOUR_OF_DAY, 15);
            cal2.set(Calendar.MINUTE, 0);

            reminderList.add(new Reminder(
                    "Погулять с собакой",
                    "Она очень хочет погулять",
                    cal2.getTime()
            ));

            adapter.notifyDataSetChanged();
            Log.d(TAG, "Test data added");

        } catch (Exception e) {
            Log.e(TAG, "Error adding test data", e);
        }
    }
}