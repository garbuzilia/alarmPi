package com.example.alarmpi;

import android.os.Bundle;
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

    private RecyclerView recyclerView;
    private ReminderAdapter adapter;
    private List<Reminder> reminderList;
    private Button deleteButton, addButton, editButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.remindersRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        reminderList = new ArrayList<>();
        adapter = new ReminderAdapter(reminderList, this);
        recyclerView.setAdapter(adapter);

        deleteButton = findViewById(R.id.deleteButton);
        addButton = findViewById(R.id.addButton);
        editButton = findViewById(R.id.editButton);

        deleteButton.setOnClickListener(v -> deleteSelectedReminder());
        addButton.setOnClickListener(v -> showAddDialog());
        editButton.setOnClickListener(v -> showEditDialog());

        adapter.setOnItemClickListener(new ReminderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                updateButtonStates(true);
            }

            @Override
            public void onSwitchChanged(int position, boolean isChecked) {
                reminderList.get(position).setActive(isChecked);
                String state = isChecked ? "включено" : "выключено";
                Toast.makeText(MainActivity.this,
                        "Напоминание " + state,
                        Toast.LENGTH_SHORT).show();
            }
        });

        addTestData();
        updateButtonStates(false);
    }

    private void updateButtonStates(boolean hasSelection) {
        deleteButton.setEnabled(hasSelection);
        editButton.setEnabled(hasSelection);
    }

    private void deleteSelectedReminder() {
        int position = adapter.getSelectedPosition();
        if (position != -1) {
            reminderList.remove(position);
            adapter.notifyItemRemoved(position);
            adapter.setSelectedPosition(-1);
            updateButtonStates(false);
            Toast.makeText(this, "Напоминание удалено", Toast.LENGTH_SHORT).show();
        }
    }

    private void showAddDialog() {
        AddEditReminderDialog dialog = AddEditReminderDialog.newInstance();
        dialog.show(getSupportFragmentManager(), "add_reminder_dialog");
    }

    private void showEditDialog() {
        int position = adapter.getSelectedPosition();
        if (position != -1) {
            Reminder reminder = reminderList.get(position);
            AddEditReminderDialog dialog = AddEditReminderDialog.newInstance(position, reminder);
            dialog.show(getSupportFragmentManager(), "edit_reminder_dialog");
        } else {
            Toast.makeText(this, "Сначала выберите напоминание", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onReminderAdded(Reminder reminder) {
        reminderList.add(reminder);
        adapter.notifyItemInserted(reminderList.size() - 1);
        Toast.makeText(this, "Напоминание добавлено", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onReminderUpdated(int position, Reminder reminder) {
        if (position >= 0 && position < reminderList.size()) {
            boolean wasActive = reminderList.get(position).isActive();
            reminder.setActive(wasActive);

            reminderList.set(position, reminder);
            adapter.notifyItemChanged(position);
            Toast.makeText(this, "Напоминание обновлено", Toast.LENGTH_SHORT).show();
        }
    }

    private void addTestData() {
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
    }
}