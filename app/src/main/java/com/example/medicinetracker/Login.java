package com.example.medicinetracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Login extends AppCompatActivity {
    EditText username;
    EditText password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        username = findViewById(R.id.editTextText);
        password = findViewById(R.id.editTextTextPassword);

    }

    public void login(View view) {
    String USERNAME = username.getText().toString();
    String PASSWORD = password.getText().toString();

        if (PASSWORD.equals("patient123") && USERNAME.equals("patient")) {

            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);
        } else if (PASSWORD.equals("facultypassword") && USERNAME.equals("faculty@apc")) {

        } else {
            runOnUiThread(() -> {
                Toast.makeText(Login.this, "Incorrect Email or Password", Toast.LENGTH_SHORT).show();
    });
        }
}
}