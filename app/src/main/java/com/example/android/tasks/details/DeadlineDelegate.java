package com.example.android.tasks.details;

import android.app.TimePickerDialog;
import android.text.format.DateFormat;
import android.widget.Toast;
import androidx.core.util.Consumer;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import com.example.android.tasks.R;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.CalendarConstraints.Builder;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;
import org.threeten.bp.ZoneId;

class DeadlineDelegate {

    private final FragmentActivity activity;
    private final Consumer<LocalDateTime> onDeadlineChanged;

    private LocalDateTime deadline = null;

    DeadlineDelegate(FragmentActivity activity, Consumer<LocalDateTime> onDeadlineChanged) {
        this.activity = activity;
        this.onDeadlineChanged = onDeadlineChanged;
    }

    LocalDateTime getDeadline() {
        return deadline;
    }

    void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    void chooseNewDeadline() {
        CalendarConstraints constraints = new Builder()
            .setValidator(DateValidatorPointForward.now())
            .build();

        MaterialDatePicker<Long> picker = MaterialDatePicker.Builder.datePicker()
            .setCalendarConstraints(constraints)
            .build();

        picker.addOnPositiveButtonClickListener(selection -> {
            ZoneId utcZone = ZoneId.of("UTC");
            Instant selectionInstant = Instant.ofEpochMilli(selection);
            LocalDate newDate = LocalDateTime.ofInstant(selectionInstant, utcZone).toLocalDate();

            chooseDeadlineTime(newDate);
        });

        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        String tag = MaterialDatePicker.class.getName();
        picker.show(fragmentManager, tag);
    }

    private void chooseDeadlineTime(LocalDate newDate) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        boolean isSystem24Hour = DateFormat.is24HourFormat(activity);

        TimePickerDialog timePickerDialog = new TimePickerDialog(activity, (view, hourOfDay, minute) -> {
            LocalTime newTime = LocalTime.of(hourOfDay, minute);
            LocalDateTime newDeadline = LocalDateTime.of(newDate, newTime);

            if (newDeadline.isAfter(currentDateTime)) {
                deadline = newDeadline;
                onDeadlineChanged.accept(newDeadline);
            } else {
                Toast toast = Toast.makeText(activity, R.string.deadline_passed, Toast.LENGTH_SHORT);
                toast.show();
            }
        }, currentDateTime.getHour(), currentDateTime.getMinute(), isSystem24Hour);

        timePickerDialog.show();
    }
}
