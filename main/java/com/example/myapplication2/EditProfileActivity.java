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
import android.widget.TextView;
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

public class EditProfileActivity extends AppCompatActivity {

    private ImageView backButton, profileImage;
    private TextView textName, textEmail;
    private EditText editHeight, editWeight, editAge;
    private Button updateProfileButton;

    private Uri profileImageUri;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        backButton          = findViewById(R.id.backButton);
        profileImage        = findViewById(R.id.profileImage);
        textName            = findViewById(R.id.textName);
        textEmail           = findViewById(R.id.textEmail);
        editHeight          = findViewById(R.id.editHeight);
        editWeight          = findViewById(R.id.editWeight);
        editAge             = findViewById(R.id.editAge);
        updateProfileButton = findViewById(R.id.updateProfileButton);

        // Geri butonu
        backButton.setOnClickListener(v -> onBackPressed());

        // Depolama izni (eğer lazım ise)
        checkStoragePermission();

        // SharedPreferences'tan mevcut değerleri al
        prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String currentName    = prefs.getString("name", "");
        String currentEmail   = prefs.getString("email", "");
        String currentHeight  = prefs.getString("height", "");
        String currentWeight  = prefs.getString("weight", "");
        String currentAge     = prefs.getString("age", "");
        String uriStr         = prefs.getString("profileImageUri", null);

        // Ad ve Email TextView'e setle (değiştirilemez)
        textName.setText("name: " + currentName);
        textEmail.setText("email: " + currentEmail);

        // Boy, Kilo, Yaş EditText’lere setle (düzenlenebilecek)
        editHeight.setText(currentHeight);
        editWeight.setText(currentWeight);
        editAge.setText(currentAge);

        // Mevcut resim URI
        if (uriStr != null) {
            profileImageUri = Uri.parse(uriStr);
            try {
                profileImage.setImageURI(profileImageUri);
            } catch (Exception e) {
                profileImage.setImageResource(R.drawable.ic_android_black_24dp);
            }
        } else {
            profileImage.setImageResource(R.drawable.ic_android_black_24dp);
        }

        // Galeriden resim seçme iş akışı
        ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedUri = result.getData().getData();
                        // Seçilen resmi uygulama dizinine kopyala
                        Uri newUri = copyImageToAppDir(selectedUri);
                        if (newUri != null) {
                            profileImageUri = newUri;
                            profileImage.setImageURI(profileImageUri);
                        } else {
                            Toast.makeText(EditProfileActivity.this, "Resim kopyalanamadı", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        // Profil resmine tıklayınca galeri aç
        profileImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            pickImageLauncher.launch(intent);
        });

        // Güncelle Butonu
        updateProfileButton.setOnClickListener(v -> {
            // Kullanıcının girdiği (yeni) değerleri al
            String newHeight = editHeight.getText().toString().trim();
            String newWeight = editWeight.getText().toString().trim();
            String newAge    = editAge.getText().toString().trim();

            // Boşluk kontrolü (opsiyonel)
            if (newHeight.isEmpty() || newWeight.isEmpty() || newAge.isEmpty()) {
                Toast.makeText(EditProfileActivity.this,
                        "Boy, kilo ve yaş boş bırakılamaz!",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            // SharedPreferences'e sadece Boy, Kilo, Yaş ve varsa yeni resim URI'sini kaydediyoruz
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("height", newHeight);
            editor.putString("weight", newWeight);
            editor.putString("age", newAge);

            if (profileImageUri != null) {
                editor.putString("profileImageUri", profileImageUri.toString());
            }
            editor.apply();

            Toast.makeText(EditProfileActivity.this, "Profil güncellendi", Toast.LENGTH_SHORT).show();
            finish(); // Activity kapat -> UserProfileActivity'ye dön
        });
    }

    /**
     * Galeriden seçilen resmi uygulama dizinine kopyalama
     */
    private Uri copyImageToAppDir(Uri sourceUri) {
        if (sourceUri == null) return null;

        try {
            File targetDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            if (targetDir == null) {
                Log.e("EditProfileActivity", "Target directory is null.");
                return null;
            }
            String fileName = "profile_" + System.currentTimeMillis() + ".jpg";
            File outFile = new File(targetDir, fileName);

            try (InputStream inStream = getContentResolver().openInputStream(sourceUri);
                 FileOutputStream outStream = new FileOutputStream(outFile)) {

                byte[] buf = new byte[4096];
                int len;
                while ((len = inStream.read(buf)) != -1) {
                    outStream.write(buf, 0, len);
                }
            }
            return Uri.fromFile(outFile);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Android 6.0 (API 23) sonrası için depolama izni
     */
    private void checkStoragePermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 123);
            }
        } else {
            // Android 13 ve sonrası READ_MEDIA_IMAGES izni
            // if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
            //         != PackageManager.PERMISSION_GRANTED) {
            //     ActivityCompat.requestPermissions(this,
            //             new String[]{Manifest.permission.READ_MEDIA_IMAGES}, 123);
            // }
        }
    }
}
