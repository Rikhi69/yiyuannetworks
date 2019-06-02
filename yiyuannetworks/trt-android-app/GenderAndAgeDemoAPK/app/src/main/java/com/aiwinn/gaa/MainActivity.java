package com.aiwinn.gaa;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity {

    TextView version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        version = findViewById(R.id.version);
        permissionTask();
        findViewById(R.id.tv_ageandgender).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (AppConfig.INIT_STATE) {
                startActivity(new Intent(MainActivity.this,AgeAndGenderActivity.class));
            }else {
                Toast.makeText(MainActivity.this,AppConfig.INIT_ERROR,Toast.LENGTH_SHORT).show();
            }
            }
        });
        findViewById(R.id.config).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppConfig.INIT_STATE) {
                    startActivity(new Intent(MainActivity.this,ConfigActivity.class));
                }else {
                    Toast.makeText(MainActivity.this,AppConfig.INIT_ERROR,Toast.LENGTH_SHORT).show();
                }
            }
        });
        try {
            version.setText(MainActivity.this.getPackageManager().getPackageInfo(getPackageName(),0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }


    private static final int RQ_CAMERA_STORAGE_PERMISSION = 100;

    @AfterPermissionGranted(RQ_CAMERA_STORAGE_PERMISSION)
    public void permissionTask() {
        String[] perms = { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE };
        if (EasyPermissions.hasPermissions(this, perms)) {
        } else {
            EasyPermissions.requestPermissions(this, "给相机和存储权限",
                    RQ_CAMERA_STORAGE_PERMISSION, perms);
        }

    }

}
