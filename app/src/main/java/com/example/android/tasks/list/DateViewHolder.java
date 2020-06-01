package com.example.android.tasks.list;

import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import com.example.android.tasks.R;
import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.FormatStyle;

class DateViewHolder extends RecyclerView.ViewHolder {

    private final TextView dateTextView;

    DateViewHolder(@NonNull View itemView) {
        super(itemView);

        dateTextView = itemView.findViewById(R.id.date_text);
    }

    void bind(@Nullable LocalDate date) {
        if (date != null) {
            String formattedDate = formatDate(date);
            dateTextView.setText(formattedDate);
        } else {
            dateTextView.setText(R.string.no_deadline);
        }
    }

    private String formatDate(@NonNull LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL);
        return formatter.format(date);
    }
}
