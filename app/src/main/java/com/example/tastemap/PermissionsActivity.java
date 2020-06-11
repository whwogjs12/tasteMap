package com.example.tastemap;
import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class PermissionsActivity extends AppCompatActivity
{
    //권한 획득을 위한 화면
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //퍼미션 상태 확인
            if (!hasPermissions(PERMISSIONS)) {
                //퍼미션 허가 안되어있다면 사용자에게 요청
                requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }else{
                Intent intent = new Intent(this, MainActivity.class); //실행 화면
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        }
    }

    static final int PERMISSIONS_REQUEST_CODE = 1000;
    String[] PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
            ,Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE};

    private boolean hasPermissions(String[] permissions) {
        int result;

        //스트링 배열에 있는 퍼미션들의 허가 상태 여부 확인
        for (String perms : permissions){

            result = ContextCompat.checkSelfPermission(this, perms);

            if (result == PackageManager.PERMISSION_DENIED){
                //허가 안된 퍼미션 발견
                return false;
            }
        }
        //모든 퍼미션이 허가되었음
        return true;
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch(requestCode){

            case PERMISSIONS_REQUEST_CODE:

                if (grantResults.length > 0) {
                    boolean fineLocationPermissionAccepted = grantResults[0]
                            == PackageManager.PERMISSION_GRANTED;
                    boolean coarseLocationPermissionAccepted = grantResults[1]
                            == PackageManager.PERMISSION_GRANTED;
                    boolean internetPermissionAccepted = grantResults[2]
                            == PackageManager.PERMISSION_GRANTED;
                    boolean networkAccessPermissionAccepted = grantResults[3]
                            == PackageManager.PERMISSION_GRANTED;

                    if (!fineLocationPermissionAccepted || !coarseLocationPermissionAccepted ||
                            !internetPermissionAccepted || !networkAccessPermissionAccepted)
                        showDialogForPermission("앱을 실행하려면 퍼미션을 허가하셔야합니다.");
                    else
                    {
                        Intent intent = new Intent(this,MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
                break;
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    private void showDialogForPermission(String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder( this);
        builder.setTitle("권한 설정 알림");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id){
                requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        });
        builder.create().show();
    }
}
