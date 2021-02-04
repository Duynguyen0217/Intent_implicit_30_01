package com.example.intent_implicit_30_01;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.URI;

public class MainActivity extends AppCompatActivity {
    int REQUEST_CODE_CAMERA = 123;
    int REQUEST_CODE_GALLERY = 234;
    Button mBtncamera, mBtnGallery;
    ImageView mImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtncamera = findViewById(R.id.buttonCamera);
        mBtnGallery = findViewById(R.id.buttonGallery);
        mImg = findViewById(R.id.imageview);


        //sms: hầu như ko được đưa lên Store
        //contact: đc sử dụng ít trên store

        //GPS: định vị vị trí đang đứng
        //open file pdf
        //chọn nhiều hình từ Gallery

        mBtncamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){
                   //code tạo hộp thoại để chọn deny or allow
                    ActivityCompat.requestPermissions(MainActivity.this ,
                            new String[]{Manifest.permission.CAMERA},
                            REQUEST_CODE_CAMERA);
                }else {
                    Intent intent = new Intent();
                    intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent , REQUEST_CODE_CAMERA);
                }
            }
        });

        mBtnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                    //READ_EXTERNAL_STORAGE : đọc dữ liệu từ vùng nhớ
                    ActivityCompat.requestPermissions(MainActivity.this ,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_CODE_GALLERY);
                }else {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent , REQUEST_CODE_GALLERY);
                }

            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //code giúp cho camera chọn camera nào trong app để thực hiện
        if(requestCode == REQUEST_CODE_CAMERA){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent();
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent , REQUEST_CODE_CAMERA);
            }
        }

        if(requestCode == REQUEST_CODE_GALLERY){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent , REQUEST_CODE_GALLERY);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE_CAMERA && resultCode == RESULT_OK){
            //code để lấy hình từ camera
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            mImg.setImageBitmap(bitmap);
        }
        if(requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK){
            //URI : lấy dữ liệu từ thiết bị , nhưng 1 số thiết bị ko hỗ trợ URI
       //     Uri uri = data.getData();
        //    mImg.setImageURI(uri);

            try {
                //getContentResolver : lấy thông tin từ thiết bị, nó là 1 hàm con của contentprovider
                //chuyển đổi URI sang dạng stream
                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                //decodeStream : dịch từ stream sang dạng bitmap
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                mImg.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
