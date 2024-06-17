package com.example.medicinetracker;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Selection extends AppCompatActivity {

    private static final String TAG = "Selecting";
    private Button capsuleImage, saveButton;
    private TextView time1, time3,time2;
    private EditText inputmg;
    private ImageView selectedImageView;
    private Button backButton;
    private final int[] imageIds = {
            R.drawable.donepezil,
            R.drawable.galantamine,
            R.drawable.rivastigmine,
            R.drawable.lecanemab,
            R.drawable.memantine,
    };

    private final String[] imageNames = {
            "Donepezil",
            "Galantamine",
            "Rivastigmine",
            "Lecanemab",
            "Memantine",
    };

    private int selectedPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selecting_med);

        Button backButton = findViewById(R.id.backButton);


        capsuleImage = findViewById(R.id.capsuleImage);
        time1 = findViewById(R.id.texttimes);
        time3 = findViewById(R.id.textoftime);
        inputmg = findViewById(R.id.inputmg);
        time2 = findViewById(R.id.textofdate);
        selectedImageView = findViewById(R.id.selectedImageView);
        saveButton = findViewById(R.id.Save);

        Intent intent = getIntent();
        String selectedType = intent.getStringExtra("selectedType");
        String selectedTime = intent.getStringExtra("selectedTime");
        String selectedDate = intent.getStringExtra("selectedDate");
        String quantity = intent.getStringExtra("quantity");
        if (selectedType != null) {
            time1.setText(quantity);
        }

        if (quantity != null) {
            time3.setText(selectedTime);
        }

        if (quantity != null) {
            time2.setText(selectedDate);
        }



        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Selection.this, Setup.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });

        capsuleImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "capsuleImage clicked");
                showImagePickerDialog();
            }
        });

        // Set click listener for the saveButton
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSelectedImageAndName();
            }
        });



    }

    private void showImagePickerDialog() {
        Log.d(TAG, "showImagePickerDialog called");
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.img_selection);

        GridView gridView = dialog.findViewById(R.id.imageGrid);
        if (gridView == null) {
            Log.e(TAG, "GridView is null. Check your layout file.");
            return;
        }

        ImageAdapter adapter = new ImageAdapter(this, imageIds, imageNames);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "Image selected at position: " + position);
                capsuleImage.setText(imageNames[position]);
                selectedImageView.setImageResource(imageIds[position]);
                selectedPosition = position; // Save the selected position
                dialog.dismiss();
            }
        });

        Log.d(TAG, "Showing dialog");
        dialog.show();
    }

    private void saveSelectedImageAndName() {
        if (selectedPosition != -1) { // Check if an item is selected
            // Get the selected image name
            String selectedImageName = imageNames[selectedPosition];

            // Get the selected image ID
            int selectedImageId = imageIds[selectedPosition];

            String times = time1.getText().toString();
            String timesOfDay = time3.getText().toString();
            String timesOfDate = time2.getText().toString();
            String mgInput = inputmg.getText().toString();

            Intent resultIntent = new Intent();
            resultIntent.putExtra("selectedImageName", selectedImageName);
            resultIntent.putExtra("selectedImageId", selectedImageId);
            resultIntent.putExtra("texttimes", times);
            resultIntent.putExtra("textoftime", timesOfDay);
            resultIntent.putExtra("textofdate", timesOfDate);
            resultIntent.putExtra("inputmg", mgInput);
            setResult(RESULT_OK, resultIntent);
            finish();
        }
    }

}

