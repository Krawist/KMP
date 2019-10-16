package com.example.kmp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.kmp.Helper.Helper;
import com.example.kmp.R;

public class PermissionActivity extends AppCompatActivity {

    private Button grantPermisionsResult;
    public static final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 10;
    public static final String[] PERMISSION_ARRAY = {Manifest.permission.READ_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);

        grantPermisionsResult = findViewById(R.id.button_grant_permission);

        grantPermisionsResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(ActivityCompat.checkSelfPermission(PermissionActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED) {
                   startActivity(new Intent(PermissionActivity.this, MainActivity.class));
                   finish();
               }else{
                   ActivityCompat.requestPermissions(PermissionActivity.this, PERMISSION_ARRAY,READ_EXTERNAL_STORAGE_REQUEST_CODE);
               }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode== READ_EXTERNAL_STORAGE_REQUEST_CODE  ){
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivity(new Intent(PermissionActivity.this, MainActivity.class));
                finish();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
