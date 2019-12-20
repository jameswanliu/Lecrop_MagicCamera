package com.seu.magiccamera;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import com.seu.magiccamera.activity.CameraActivity;


public class MainActivity extends AppCompatActivity {


    private final static int CAMERA_REQUEST = 100;
    private final static int ALBUM_REQUEST = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PermissionChecker.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
                        == PermissionChecker.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA},
                            CAMERA_REQUEST);
                } else {
                    startActivity(new Intent(MainActivity.this, CameraActivity.class));
                }
            }
        });


        findViewById(R.id.btn_camerax).setOnClickListener((v)->{
            if (PermissionChecker.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
                    == PermissionChecker.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA},
                        CAMERA_REQUEST);
            } else {
                startActivity(new Intent(MainActivity.this, CameraActivity.class));
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (grantResults.length != 1 || grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startActivity(new Intent(this, CameraActivity.class));
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}
