package com.example.medicinetracker;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Calendar;

public class Setup extends AppCompatActivity {

    private static final int REQUEST_CODE_POST_NOTIFICATIONS = 1001;
    private TextView select1, select2, select3, text3, text4, text6, text5;
    private Switch notificationsSwitch;
    private Spinner reminderTimesSpinner;
    private String selectedFrequency;
    private static final int REQUEST_SELECTION = 1;
    private Calendar alarmCalendar;
    private AlarmManager alarmManager;
    private PendingIntent alarmIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_medicine);

        // Initialize views
        select1 = findViewById(R.id.select1);
        select2 = findViewById(R.id.select2);
        select3 = findViewById(R.id.select3);
        text3 = findViewById(R.id.text3);
        text5 = findViewById(R.id.text5);
        text4 = findViewById(R.id.text4);
        text6 = findViewById(R.id.text6);
        notificationsSwitch = findViewById(R.id.notificationsSwitch);
        reminderTimesSpinner = findViewById(R.id.reminderTimesSpinner);
        Button backButton = findViewById(R.id.backButton);
        Button proceed = findViewById(R.id.proceed);

        // Initialize alarm calendar and manager
        alarmCalendar = Calendar.getInstance();
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        createNotificationChannel();

        // Set up the spinner for reminder frequency selection
        reminderTimesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedFrequency = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedFrequency = "Once";
            }
        });

        notificationsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Log.d("Setup", "notificationsSwitch changed to: " + isChecked);
            if (isChecked) {
                text3.setVisibility(View.VISIBLE);
                text4.setVisibility(View.VISIBLE);
                text5.setVisibility(View.VISIBLE);
                text6.setVisibility(View.VISIBLE);

                String selectedTime = text4.getText().toString();
                String selectedDate = text6.getText().toString();
                Log.d("Setup", "Selected time: " + selectedTime + ", Selected date: " + selectedDate);

                if (isValidTime(selectedTime) && isValidDate(selectedDate)) {
                    int requestCode = (int) (System.currentTimeMillis() & 0xfffffff);
                    Log.d("Setup", "Setting alarm with requestCode: " + requestCode);

                    // Call the setAlarm method with selected time, date, and requestCode
                    setAlarm(selectedTime, selectedDate, requestCode);
                } else {
                    Toast.makeText(Setup.this, "Please select a valid time and date", Toast.LENGTH_SHORT).show();
                }
            } else {
                text3.setVisibility(View.GONE);
                text4.setVisibility(View.GONE);
                text5.setVisibility(View.GONE);
                text6.setVisibility(View.GONE);
                Log.d("Setup", "Canceling alarm");

                // Handle alarm cancellation if necessary
            }
        });

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(Setup.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        select1.setOnClickListener(v -> updateSelection(select1));
        select2.setOnClickListener(v -> updateSelection(select2));
        select3.setOnClickListener(v -> updateSelection(select3));
        text4.setOnClickListener(v -> showTimePickerDialog());
        text6.setOnClickListener(v -> showDatePickerDialog());

        proceed.setOnClickListener(v -> {
            Intent intent = new Intent(Setup.this, Selection.class);

            String selectedType = getSelectedType();
            String selectedTime = text4.getText().toString();
            String selectedDate = text6.getText().toString();
            String selectorText = getSelectedSelectorText();
            String quantity = ((EditText) findViewById(R.id.quantity)).getText().toString();

            intent.putExtra("selectedType", selectedType);
            intent.putExtra("selectedDate", selectedDate);
            intent.putExtra("selectedTime", selectedTime);
            intent.putExtra("selectorText", selectorText);
            intent.putExtra("quantity", quantity);
            startActivityForResult(intent, REQUEST_SELECTION);
        });
    }

    private boolean isValidTime(String time) {
        return time.matches("\\d{2}:\\d{2}");
    }

    private boolean isValidDate(String date) {
        return date.matches("\\d{2}/\\d{2}/\\d{4}");
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "MedicineTrackerChannel";
            String description = "Channel for Medicine Tracker Alarm";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("medicinetracker_channel", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, REQUEST_CODE_POST_NOTIFICATIONS);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_POST_NOTIFICATIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with notifications
            } else {
                // Permission denied, handle accordingly
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private String getSelectedSelectorText() {
        if (select1.getBackground() != null) return select1.getText().toString();
        if (select2.getBackground() != null) return select2.getText().toString();
        if (select3.getBackground() != null) return select3.getText().toString();
        return "";
    }

    private void showTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        @SuppressLint("DefaultLocale") TimePickerDialog timePickerDialog = new TimePickerDialog(Setup.this,
                (view, hourOfDay, minute1) -> {
                    text4.setText(String.format("%02d:%02d", hourOfDay, minute1));
                    alarmCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    alarmCalendar.set(Calendar.MINUTE, minute1);
                }, hour, minute, true);
        timePickerDialog.show();
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        @SuppressLint("DefaultLocale") DatePickerDialog datePickerDialog = new DatePickerDialog(Setup.this,
                (view, year1, month1, dayOfMonth) -> {
                    text6.setText(String.format("%02d/%02d/%04d", dayOfMonth, month1 + 1, year1));
                    alarmCalendar.set(Calendar.YEAR, year1);
                    alarmCalendar.set(Calendar.MONTH, month1);
                    alarmCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void updateSelection(TextView selectedView) {
        select1.setBackgroundColor(Color.TRANSPARENT);
        select2.setBackgroundColor(Color.TRANSPARENT);
        select3.setBackgroundColor(Color.TRANSPARENT);
        selectedView.setBackgroundColor(Color.YELLOW);
    }

    private String getSelectedType() {
        if (select1.getBackground() != null) return "Tablet";
        if (select2.getBackground() != null) return "Capsules";
        if (select3.getBackground() != null) return "Liquid";
        return "";
    }

    @SuppressLint("ScheduleExactAlarm")
    private void setAlarm(String time, String date, int requestCode) {
        try {
            String[] timeParts = time.split(":");
            String[] dateParts = date.split("/");

            int hour = Integer.parseInt(timeParts[0]);
            int minute = Integer.parseInt(timeParts[1]);
            int day = Integer.parseInt(dateParts[0]);
            int month = Integer.parseInt(dateParts[1]) - 1; // Month is 0-based in Calendar
            int year = Integer.parseInt(dateParts[2]);

            alarmCalendar.set(Calendar.HOUR_OF_DAY, hour);
            alarmCalendar.set(Calendar.MINUTE, minute);
            alarmCalendar.set(Calendar.SECOND, 0);
            alarmCalendar.set(Calendar.MILLISECOND, 0);
            alarmCalendar.set(Calendar.DAY_OF_MONTH, day);
            alarmCalendar.set(Calendar.MONTH, month);
            alarmCalendar.set(Calendar.YEAR, year);

            Log.d("Setup", "Alarm set for: " + alarmCalendar.getTime());

            Intent intent = new Intent(this, AlarmReceiver.class);
            alarmIntent = PendingIntent.getBroadcast(this, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            switch (selectedFrequency) {
                case "Once":
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmCalendar.getTimeInMillis(), alarmIntent);
                    } else {
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmCalendar.getTimeInMillis(), alarmIntent);
                    }
                    break;
                case "Everyday":
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmCalendar.getTimeInMillis(),
                            AlarmManager.INTERVAL_DAY, alarmIntent);
                    break;
                case "Every week":
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmCalendar.getTimeInMillis(),
                            AlarmManager.INTERVAL_DAY * 7, alarmIntent);
                    break;
                case "Every month":
                    // Monthly interval calculation
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmCalendar.getTimeInMillis(),
                            AlarmManager.INTERVAL_DAY * 30, alarmIntent);
                    break;
                case "Twice":
                    // Set alarm for twice a day (e.g., morning and evening)
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmCalendar.getTimeInMillis(),
                            AlarmManager.INTERVAL_HALF_DAY, alarmIntent);
                    break;
                case "Thrice":
                    // Set alarm for thrice a day (e.g., morning, afternoon, and evening)
                    long interval = AlarmManager.INTERVAL_HALF_DAY / 2;
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmCalendar.getTimeInMillis(),
                            interval, alarmIntent);
                    break;
            }

            Toast.makeText(this, "Alarm set for " + time + " on " + date, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("Setup", "Error setting alarm: ", e);
            Toast.makeText(this, "Error setting alarm. Please check the time and date format.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SELECTION && resultCode == RESULT_OK && data != null) {
            // Get the returned data from Selection activity
            Bundle extras = data.getExtras();
            if (extras != null) {
                Intent resultIntent = new Intent();
                resultIntent.putExtras(extras);
                setResult(RESULT_OK, resultIntent);
                finish();  // Finish Setup activity and return to MainActivity with result
            } else {
                Toast.makeText(this, "No data received", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
