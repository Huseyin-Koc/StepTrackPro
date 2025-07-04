package com.example.myapplication2;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class CreateProfileActivity extends AppCompatActivity {

    private ImageView profileImageView;
    private EditText nameEditText, emailEditText, passwordEditText, heightEditText, weightEditText, ageEditText;
    private Button saveButton;
    private Uri profileImageUri;

    // ----------------------------------------
    // 1. OnCreate
    // ----------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        // Runtime izni (Android 6.0 ve sonrası) kontrolü (Eğer gerekiyorsa)
        checkStoragePermission();

        profileImageView = findViewById(R.id.profileImage);
        nameEditText = findViewById(R.id.editName);
        emailEditText = findViewById(R.id.editEmail);
        passwordEditText = findViewById(R.id.editPassword);
        heightEditText = findViewById(R.id.editHeight);
        weightEditText = findViewById(R.id.editWeight);
        ageEditText = findViewById(R.id.editAge);
        saveButton = findViewById(R.id.saveProfileButton);

        // Geri butonu
        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> onBackPressed());

        // ----------------------------------------
        // 2. Galeriden resim seçme launcheri
        // ----------------------------------------
        ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        // Galeriden gelen orijinal URI
                        Uri selectedUri = result.getData().getData();

                        // Resmi uygulama dizinine kopyalayalım:
                        Uri newUri = copyImageToAppDir(selectedUri);
                        if (newUri != null) {
                            profileImageUri = newUri;
                            profileImageView.setImageURI(profileImageUri);
                            Log.d("CreateProfile", "Copied Image URI: " + profileImageUri);
                        } else {
                            Log.e("CreateProfile", "Failed to copy image");
                        }
                    }
                }
        );

        // Resim tıklanınca galeri aç
        profileImageView.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            pickImageLauncher.launch(intent);
        });

        // Profil kaydet butonu
        saveButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String height = heightEditText.getText().toString().trim();
            String weight = weightEditText.getText().toString().trim();
            String age = ageEditText.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(CreateProfileActivity.this, "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show();
            } else {
                // SharedPreferences'a kaydet
                SharedPreferences.Editor editor = getSharedPreferences("UserPrefs", MODE_PRIVATE).edit();
                editor.putString("name", name);
                editor.putString("email", email);
                editor.putString("password", password);
                editor.putString("height", height);
                editor.putString("weight", weight);
                editor.putString("age", age);
                // Kopyaladığımız URI'yi kaydet
                if (profileImageUri != null) {
                    editor.putString("profileImageUri", profileImageUri.toString());
                }
                editor.apply();

                Toast.makeText(CreateProfileActivity.this, "Profil oluşturuldu", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    // ----------------------------------------
    // 3. Resmi Uygulama Dizinine Kopyalama Metodu
    // ----------------------------------------
    private Uri copyImageToAppDir(Uri sourceUri) {
        if (sourceUri == null) return null;

        try {
            // 3.1. Uygulamanın özel dizini (Pictures)
            File targetDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            if (targetDir == null) {
                Log.e("CreateProfile", "Target directory is null.");
                return null;
            }

            // 3.2. Kaydedilecek dosya ismi (örnek: profile_<timestamp>.jpg)
            String fileName = "profile_" + System.currentTimeMillis() + ".jpg";
            File outFile = new File(targetDir, fileName);

            // 3.3. InputStream / OutputStream oluşturup kopyalamayı gerçekleştiriyoruz
            try (InputStream inStream = getContentResolver().openInputStream(sourceUri);
                 FileOutputStream outStream = new FileOutputStream(outFile)) {

                byte[] buf = new byte[4096];
                int len;
                while ((len = inStream.read(buf)) != -1) {
                    outStream.write(buf, 0, len);
                }
            }

            // 3.4. Yeni oluşturduğumuz dosyanın URI'sini döndürüyoruz
            return Uri.fromFile(outFile);

        } catch (IOException e) {
            e.printStackTrace();
            Log.e("CreateProfile", "Error copying image: " + e.getMessage());
            return null;
        }
    }

    // ----------------------------------------
    // 4. (Opsiyonel) Storage Permission Kontrolü
    // ----------------------------------------
    private void checkStoragePermission() {
        // Android 13 öncesi için READ_EXTERNAL_STORAGE izni gerekli olabilir
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 123);
            }
        } else {

        }
    }
}
