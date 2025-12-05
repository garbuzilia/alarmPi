package com.example.alarmpi;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Button;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ReminderAdapter adapter;
    private List<Reminder> reminderList;
    private Button deleteButton, addButton, editButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Инициализируем список (пока пустой)
        reminderList = new ArrayList<>();

        // 2. Находим RecyclerView в макете
        recyclerView = findViewById(R.id.remindersRecyclerView);

        // 3. Устанавливаем LayoutManager (отвечает за размещение элементов)
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 4. Создаем адаптер и передаем ему данные
        adapter = new ReminderAdapter(reminderList, this);

        // 5. Устанавливаем адаптер в RecyclerView
        recyclerView.setAdapter(adapter);

        // 6. Находим кнопки
        deleteButton = findViewById(R.id.deleteButton);
        addButton = findViewById(R.id.addButton);
        editButton = findViewById(R.id.editButton);

        // 7. Устанавливаем слушатель кликов для адаптера
        adapter.setOnItemClickListener(new ReminderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // Включаем кнопки удаления и редактирования
                deleteButton.setEnabled(true);
                editButton.setEnabled(true);
                Toast.makeText(MainActivity.this,
                        "Выбрано: " + reminderList.get(position).getTitle(),
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSwitchChanged(int position, boolean isChecked) {
                // Обновляем состояние напоминания
                reminderList.get(position).setActive(isChecked);
                String state = isChecked ? "включено" : "выключено";
                Toast.makeText(MainActivity.this,
                        "Напоминание " + state,
                        Toast.LENGTH_SHORT).show();
            }
        });

        // 8. Обработчики для кнопок
        deleteButton.setOnClickListener(v -> {
            int selectedPosition = adapter.getSelectedPosition();
            if (selectedPosition != -1) {
                adapter.removeReminder(selectedPosition);
                deleteButton.setEnabled(false);
                editButton.setEnabled(false);
            }
        });

        addButton.setOnClickListener(v -> {
            // Создаем тестовое напоминание
            Reminder newReminder = new Reminder(
                    "Новое напоминание",
                    "Описание нового напоминания",
                    new Date()
            );
            adapter.addReminder(newReminder);
        });

        editButton.setOnClickListener(v -> {
            int selectedPosition = adapter.getSelectedPosition();
            if (selectedPosition != -1) {
                // Здесь можно открыть диалог редактирования
                Toast.makeText(this, "Редактировать позицию " + selectedPosition,
                        Toast.LENGTH_SHORT).show();
            }
        });

        // 9. Добавляем тестовые данные
        addTestData();
    }

    private void addTestData() {
        // Добавляем несколько тестовых напоминаний
        reminderList.add(new Reminder(
                "Вынести мусор",
                "Скоро приедут родители, нужно сделать уборку",
                new Date() // Здесь должна быть реальная дата
        ));

        reminderList.add(new Reminder(
                "Погулять с собакой",
                "Она очень хочет погулять",
                new Date()
        ));

        // Уведомляем адаптер, что данные изменились
        adapter.notifyDataSetChanged();
    }
}