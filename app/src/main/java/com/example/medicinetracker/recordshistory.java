package com.example.medicinetracker;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.w3c.dom.Text;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class recordshistory extends AppCompatActivity {
    Connectionclass connectionclass;
    Connection con;

    String name, str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recordshistory);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        connectionclass = new Connectionclass();
        connect();

    }

    public void connect() {
        // Create an ExecutorService for background task execution
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                // Establish the database connection
                con = connectionclass.CONN();

                // Check if connection is null
                if (con == null) {
                    runOnUiThread(() -> Toast.makeText(recordshistory.this, "Error in connecting with the database", Toast.LENGTH_SHORT).show());
                    return;
                }

                // Define the query to retrieve the required data
                String query = "SELECT * FROM patientrecords_db.records_tbl";
                PreparedStatement stmt = con.prepareStatement(query);
                ResultSet rs = stmt.executeQuery();

                // Create a StringBuilder to store the data
                StringBuilder bStr = new StringBuilder();
                while (rs.next()) {

                    // Append data to the StringBuilder for logging or further use
                    bStr.append("Took ").append(rs.getString("medicine")).append(" at ").append(rs.getString("time")).append("\n").append("\n");
                    String datas = bStr.toString();
                    // Update UI with the retrieved data
                    runOnUiThread(() -> {
                        TextView data = findViewById(R.id.Medicine);

                        data.setText(datas);
                        data.setMovementMethod(new ScrollingMovementMethod());
                    });
                }

                // Clean up resources
                rs.close();
                stmt.close();
                con.close();

                // Show success message
                runOnUiThread(() -> Toast.makeText(recordshistory.this, "Data retrieved successfully", Toast.LENGTH_SHORT).show());

            } catch (SQLException e) {
                // Handle SQL exceptions and show error message
                runOnUiThread(() -> Toast.makeText(recordshistory.this, "SQL Exception: " + e.getMessage(), Toast.LENGTH_LONG).show());
                e.printStackTrace();
            } catch (Exception e) {
                // Handle general exceptions and show error message
                runOnUiThread(() -> Toast.makeText(recordshistory.this, "Exception: " + e.getMessage(), Toast.LENGTH_LONG).show());
                e.printStackTrace();
            }
        });
        // Shutdown the executor service
        executorService.shutdown();
    }
}

