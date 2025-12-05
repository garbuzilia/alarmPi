package com.example.alarmpi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ViewHolder> {

    private List<Reminder> reminders;
    private Context context;
    private OnItemClickListener listener;
    private int selectedPosition = -1; // -1 означает "ничего не выбрано"

    // Интерфейс для обработки кликов
    public interface OnItemClickListener {
        void onItemClick(int position);
        void onSwitchChanged(int position, boolean isChecked);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    // Конструктор адаптера
    public ReminderAdapter(List<Reminder> reminders, Context context) {
        this.reminders = reminders;
        this.context = context;
    }

    // Создание ViewHolder (вызывается для каждой новой карточки)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reminder, parent, false);
        return new ViewHolder(view);
    }

    // Наполнение данными (самая важная часть!)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Reminder reminder = reminders.get(position);

        // 1. Устанавливаем текст в элементы
        holder.titleTextView.setText(reminder.getTitle());
        holder.descriptionTextView.setText(reminder.getDescription());

        // 2. Форматируем дату и время
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
        String formattedDate = sdf.format(reminder.getDateTime());
        holder.dateTimeTextView.setText(formattedDate);

        // 3. Устанавливаем состояние переключателя
        holder.reminderSwitch.setChecked(reminder.isActive());

        // 4. Выделяем выбранный элемент
        if (position == selectedPosition) {
            // Зеленый фон для выбранного
            holder.cardView.setCardBackgroundColor(
                    ContextCompat.getColor(context, R.color.selected_color)
            );
        } else {
            // Обычный фон
            holder.cardView.setCardBackgroundColor(
                    ContextCompat.getColor(context, R.color.card_background)
            );
        }

        // 5. Обработчик клика по карточке
        holder.cardView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(position);
            }
            // Обновляем выделение
            setSelectedPosition(position);
        });

        // 6. Обработчик изменения Switch
        holder.reminderSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            reminder.setActive(isChecked);
            if (listener != null) {
                listener.onSwitchChanged(position, isChecked);
            }
        });
    }

    @Override
    public int getItemCount() {
        return reminders.size();
    }

    // Метод для обновления выделенной позиции
    public void setSelectedPosition(int position) {
        int previousSelected = selectedPosition;
        selectedPosition = position;

        // Обновляем только изменившиеся элементы (оптимизация)
        if (previousSelected != -1) {
            notifyItemChanged(previousSelected);
        }
        if (selectedPosition != -1) {
            notifyItemChanged(selectedPosition);
        }
    }

    // Получить выбранную позицию
    public int getSelectedPosition() {
        return selectedPosition;
    }

    // Получить выбранное напоминание
    public Reminder getSelectedReminder() {
        if (selectedPosition != -1 && selectedPosition < reminders.size()) {
            return reminders.get(selectedPosition);
        }
        return null;
    }

    // Добавить новое напоминание
    public void addReminder(Reminder reminder) {
        reminders.add(reminder);
        notifyItemInserted(reminders.size() - 1);
    }

    // Удалить напоминание
    public void removeReminder(int position) {
        reminders.remove(position);
        notifyItemRemoved(position);
        if (position == selectedPosition) {
            selectedPosition = -1;
        }
    }

    // Обновить напоминание
    public void updateReminder(int position, Reminder reminder) {
        reminders.set(position, reminder);
        notifyItemChanged(position);
    }

    // Класс ViewHolder - хранит ссылки на элементы одной карточки
    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView titleTextView;
        TextView descriptionTextView;
        TextView dateTimeTextView;
        Switch reminderSwitch;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Находим все элементы карточки
            cardView = itemView.findViewById(R.id.reminderCard);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            dateTimeTextView = itemView.findViewById(R.id.dateTimeTextView);
            reminderSwitch = itemView.findViewById(R.id.reminderSwitch);
        }
    }
}