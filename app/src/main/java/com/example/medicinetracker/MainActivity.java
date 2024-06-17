package com.example.medicinetracker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MainActivity extends AppCompatActivity {

    Connectionclass connectionclass;
    Connection con;

    String name, str;
    private static final String TAG = "MainActivity";
    private static final int REQUEST_EXACT_ALARM_PERMISSION = 100;
    private ImageView picImageView;
    private TextView nameofpill, quantityofpill, time, texttimes, textoftime, date, textofdate;
    private static final int REQUEST_SETUP = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check if the exact alarm permission is granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (!alarmManager.canScheduleExactAlarms()) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivityForResult(intent, REQUEST_EXACT_ALARM_PERMISSION);
            } else {
                // If permission is already granted, set an example alarm (default values)
                setAlarm("10:00 PM", 0);
            }
        } else {
            // For versions below S, set an example alarm (default values)
            setAlarm("10:00 PM", 0);
        }

        Button mondayButton = findViewById(R.id.Monday);
        Button tuesdayButton = findViewById(R.id.Tuesday);
        Button wednesdayButton = findViewById(R.id.Wednesday);
        Button settingButton = findViewById(R.id.setting);
        Button addButton = findViewById(R.id.add);

        updateButtonDates(mondayButton, tuesdayButton, wednesdayButton);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Setup.class);
                startActivityForResult(intent, REQUEST_SETUP);
            }
        });

        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Setting.class);
                startActivity(intent);
            }
        });
        connectionclass = new Connectionclass();
        connect();
    }
    public void connect() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                con = connectionclass.CONN();

                if (con == null) {
                    str = "Error in connecting with the database";
                } else {
                    str = "Connected successfully";
                }
            } catch (Exception e) {
                str = "Exception: " + e.getMessage();
            }

            runOnUiThread(() -> {
                Toast.makeText(MainActivity.this, str, Toast.LENGTH_SHORT).show();
            });
        });
    }
    private void updateButtonDates(Button... buttons) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("d\nEEE", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();

        int currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        for (int i = 0; i < buttons.length; i++) {
            String dateText = dateFormat.format(calendar.getTime());
            buttons[i].setText(dateText);

            if (calendar.get(Calendar.DAY_OF_WEEK) == currentDayOfWeek) {
                buttons[i].setBackgroundResource(R.drawable.highlight_color);
            }

            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    private void setAlarm(String timeText, int requestCode) {
        // Use the correct format for 24-hour time
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();

        try {
            // Parse the time from the timeText (which comes from the TextView)
            Date date = timeFormat.parse(timeText);
            if (date != null) {
                // Log the parsed time for debugging
                Log.d(TAG, "Parsed time: " + date);

                // Set the calendar to the parsed time
                Calendar alarmTime = Calendar.getInstance();
                alarmTime.setTime(date);

                // Set the calendar to today's date with the parsed time
                calendar.set(Calendar.HOUR_OF_DAY, alarmTime.get(Calendar.HOUR_OF_DAY));
                calendar.set(Calendar.MINUTE, alarmTime.get(Calendar.MINUTE));
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);

                // Log the initial alarm time set on the calendar
                Log.d(TAG, "Initial alarm time set to: " + calendar.getTime());

                // If the alarm time is before the current time, set it for the next day
                if (calendar.before(Calendar.getInstance())) {
                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                }

                // Log the final alarm time after adjustment
                Log.d(TAG, "Final alarm time set to: " + calendar.getTime());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error parsing time: " + timeText, e);
        }

        // Set up the alarm
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_SETUP && resultCode == RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                String selectedImageName = extras.getString("selectedImageName");
                int selectedImageId = extras.getInt("selectedImageId");
                String times = extras.getString("texttimes");
                String timesOfDay = extras.getString("textoftime");
                String timesOfDate = extras.getString("textofdate");
                String mgInput = extras.getString("inputmg");

                Log.d(TAG, "Received time from Setup: " + timesOfDay);

                // Inflate the medication item view
                View newMedicationView = getLayoutInflater().inflate(R.layout.medication_item, null);

                ImageView picImageView = newMedicationView.findViewById(R.id.pic);
                TextView nameofpill = newMedicationView.findViewById(R.id.nameofpill);
                TextView quantityofpill = newMedicationView.findViewById(R.id.quantityofpill);
                TextView time = newMedicationView.findViewById(R.id.time);
                TextView date = newMedicationView.findViewById(R.id.date);
                ImageView statusCircle = newMedicationView.findViewById(R.id.statuscircle); // Reference to status circle

                if (nameofpill != null) {
                    picImageView.setImageResource(selectedImageId);
                    nameofpill.setText(selectedImageName);
                    quantityofpill.setText(times);
                    time.setText(timesOfDay);
                    date.setText(timesOfDate);
                } else {
                    Log.e(TAG, "TextView nameofpill is null");
                    Toast.makeText(this, "Initialization error", Toast.LENGTH_SHORT).show();
                    return;
                }

                LinearLayout bottomSection = findViewById(R.id.bottom_section);
                bottomSection.addView(newMedicationView);
                setAlarm(time.getText().toString(), bottomSection.getChildCount());

                Button taken = newMedicationView.findViewById(R.id.takenBTN);
                if (taken != null) {
                    taken.setOnClickListener(v -> {
                        String name = "patient";
                        String med = nameofpill.getText().toString();
                        String status = "taken";

                        // Get current date and time
                        LocalDateTime now = LocalDateTime.now();
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a");
                        String oras = now.format(formatter);

                        new Thread(() -> {
                            Connection con = null;
                            PreparedStatement stmt = null;
                            try {
                                con = connectionclass.CONN();
                                if (con != null) {
                                    String sql = "INSERT INTO records_tbl (name, medicine, status, time) VALUES (?, ?, ?, ?)";
                                    stmt = con.prepareStatement(sql);
                                    stmt.setString(1, name);
                                    stmt.setString(2, med);
                                    stmt.setString(3, status);
                                    stmt.setString(4, oras);

                                    int rowsInserted = stmt.executeUpdate();
                                    if (rowsInserted > 0) {
                                        runOnUiThread(() -> {
                                            Toast.makeText(MainActivity.this, "Medication recorded as taken", Toast.LENGTH_SHORT).show();

                                            // Change the status circle color to green
                                            statusCircle.setColorFilter(Color.parseColor("#00FF00"), PorterDuff.Mode.SRC_IN);

                                            // Schedule a task to revert the status circle color back to red after 1 hour
                                            statusCircle.postDelayed(() -> {
                                                runOnUiThread(() -> statusCircle.setColorFilter(Color.parseColor("#FF0000"), PorterDuff.Mode.SRC_IN));
                                            }, 3600000); // 1 hour in milliseconds
                                        });
                                    }
                                } else {
                                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Database connection failed", Toast.LENGTH_SHORT).show());
                                }
                            } catch (SQLException e) {
                                e.printStackTrace();
                                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Failed to record medication", Toast.LENGTH_SHORT).show());
                            } finally {
                                try {
                                    if (stmt != null) stmt.close();
                                    if (con != null) con.close();
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    });
                } else {
                    Log.e(TAG, "Button taken is null");
                    Toast.makeText(this, "Initialization error", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "No data received", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_EXACT_ALARM_PERMISSION) {
            if (resultCode == RESULT_OK) {
                setAlarm("10:00 PM", 0);
            } else {
                Toast.makeText(this, "Exact alarm permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

}



