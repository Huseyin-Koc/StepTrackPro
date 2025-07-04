package com.example.myapplication2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class UserProfileActivity extends AppCompatActivity {

    private ImageView profileImage, backButton;
    private TextView nameTextView, emailTextView, heightTextView, weightTextView, ageTextView;
    private Button editProfileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        profileImage    = findViewById(R.id.profileImage);
        backButton      = findViewById(R.id.backButton);
        nameTextView    = findViewById(R.id.nameTextView);
        emailTextView   = findViewById(R.id.emailTextView);
        heightTextView  = findViewById(R.id.heightTextView);
        weightTextView  = findViewById(R.id.weightTextView);
        ageTextView     = findViewById(R.id.ageTextView);
        editProfileButton = findViewById(R.id.editProfileButton);

        // Geri butonu
        backButton.setOnClickListener(v -> onBackPressed());

        // DİKKAT: getString(R.string.no_name), getString(R.string.no_email), vs.
        SharedPreferences preferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String name   = preferences.getString("name", getString(R.string.no_name));
        String email  = preferences.getString("email", getString(R.string.no_email));
        String height = preferences.getString("height", getString(R.string.unknown));
        String weight = preferences.getString("weight", getString(R.string.unknown));
        String age    = preferences.getString("age", getString(R.string.unknown));
        String uriStr = preferences.getString("profileImageUri", null);

        // "Ad: " + name -> getString(R.string.name_label) + name
        // Örn. name_label = "Ad: "
        nameTextView.setText(getString(R.string.name_label) + name);
        emailTextView.setText(getString(R.string.email_label) + email);
        heightTextView.setText(getString(R.string.height_label) + height);
        weightTextView.setText(getString(R.string.weight_label) + weight);
        ageTextView.setText(getString(R.string.age_label) + age);

        // Profil resmi
        if (uriStr != null) {
            try {
                Uri imageUri = Uri.parse(uriStr);
                profileImage.setImageURI(imageUri);
            } catch (Exception e) {
                Log.e("UserProfileActivity", "Failed to load image URI: " + e.getMessage());
                profileImage.setImageResource(R.drawable.ic_android_black_24dp);
            }
        } else {
            profileImage.setImageResource(R.drawable.ic_android_black_24dp);
        }

        // Düzenle butonu
        editProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(UserProfileActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });
    }

    // onResume'da tekrar yükleme
    @Override
    protected void onResume() {
        super.onResume();
        loadProfileData();
    }

    private void loadProfileData() {
        SharedPreferences preferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String name   = preferences.getString("name", getString(R.string.no_name));
        String email  = preferences.getString("email", getString(R.string.no_email));
        String height = preferences.getString("height", getString(R.string.unknown));
        String weight = preferences.getString("weight", getString(R.string.unknown));
        String age    = preferences.getString("age", getString(R.string.unknown));
        String uriStr = preferences.getString("profileImageUri", null);

        nameTextView.setText(getString(R.string.name_label) + name);
        emailTextView.setText(getString(R.string.email_label) + email);
        heightTextView.setText(getString(R.string.height_label) + height);
        weightTextView.setText(getString(R.string.weight_label) + weight);
        ageTextView.setText(getString(R.string.age_label) + age);

        if (uriStr != null) {
            try {
                Uri imageUri = Uri.parse(uriStr);
                profileImage.setImageURI(imageUri);
            } catch (Exception e) {
                profileImage.setImageResource(R.drawable.ic_android_black_24dp);
            }
        } else {
            profileImage.setImageResource(R.drawable.ic_android_black_24dp);
        }
    }
}
