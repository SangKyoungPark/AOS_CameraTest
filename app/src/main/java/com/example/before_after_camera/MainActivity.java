package com.example.before_after_camera;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    ImageView temp_img;
    private Button save_btn;
    private Button add_left_btn, add_right_btn;
    private Button camera_left_btn, camera_right_btn;
    private Button album_left_btn, album_right_btn;
    private ImageView img_left, img_right;
    private TextView before_text, after_text;

    final static int TAKE_PICTURE_LEFT = 0;
    final static int TAKE_PICTURE_RIGHT = 1;
    final String TAG = getClass().getSimpleName();

    String mCurrentPhotoPath;
    static final int REQUEST_TAKE_PHOTO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // κΆν νμ©
        checkPermissionCamera();

        // layout matching
        add_left_btn = (Button) findViewById(R.id.add_left_btn);
        add_right_btn = (Button) findViewById(R.id.add_right_btn);
        camera_left_btn = (Button) findViewById(R.id.camera_left_btn);
        camera_right_btn = (Button) findViewById(R.id.camera_right_btn);
        album_left_btn = (Button) findViewById(R.id.album_left_btn);
        album_right_btn = (Button) findViewById(R.id.album_right_btn);
        img_left = (ImageView) findViewById(R.id.left_img);
        img_right = (ImageView) findViewById(R.id.right_img);
        before_text = (TextView) findViewById(R.id.before_text);
        after_text = (TextView) findViewById(R.id.after_text);
        save_btn = (Button) findViewById(R.id.save_btn);

        // click Listener
        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //μ μ²΄νλ©΄
                View rootView = getWindow().getDecorView();

                File screenShot = ScreenShot(rootView);
                if (screenShot != null) {
                    //κ°€λ¬λ¦¬μ μΆκ°
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(screenShot)));
                }
                showToast("κ°€λ¬λ¦¬μ μ μ₯");
            }
        });
        //μΆκ° λ²νΌ ν΄λ¦­ μ
        add_left_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_left_btn.setVisibility(View.INVISIBLE);
                camera_left_btn.setVisibility(View.VISIBLE);
                album_left_btn.setVisibility(View.VISIBLE);
            }
        });
        add_right_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_right_btn.setVisibility(View.INVISIBLE);
                camera_right_btn.setVisibility(View.VISIBLE);
                album_right_btn.setVisibility(View.VISIBLE);
            }
        });
        //μΉ΄λ©λΌ ν΄λ¦­ μ
        camera_left_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // μΉ΄λ©λΌ μ±μ μ¬λ μμ€
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityIfNeeded(cameraIntent, TAKE_PICTURE_LEFT);
            }
        });
        camera_right_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // μΉ΄λ©λΌ μ±μ μ¬λ μμ€
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityIfNeeded(cameraIntent, TAKE_PICTURE_RIGHT);
            }
        });

        // μ¨λ² ν΄λ¦­ μ
        album_left_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityIfNeeded(intent, TAKE_PICTURE_LEFT);
            }
        });
        album_right_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityIfNeeded(intent, TAKE_PICTURE_RIGHT);
                Log.d("TEG", "" + MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            }
        });
    }

    //νλ©΄ μΊ‘μ³νκΈ°
    public File ScreenShot(View view) {
        view.setDrawingCacheEnabled(true);  //νλ©΄μ λΏλ¦΄λ μΊμλ₯Ό μ¬μ©νκ² νλ€

        Bitmap screenBitmap = view.getDrawingCache();   //μΊμλ₯Ό λΉνΈλ§΅μΌλ‘ λ³ν

        String filename = "BeforeNAfter_0.jpeg";
        File file = new File(Environment.getExternalStorageDirectory() + "/Pictures", filename);  //Picturesν΄λ screenshot.png νμΌ
        Log.d("TEG", "" + Environment.getExternalStorageDirectory());
        Log.d("TEG", "" + MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(file);
            screenBitmap.compress(Bitmap.CompressFormat.PNG, 90, os);   //λΉνΈλ§΅μ PNGνμΌλ‘ λ³ν
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        view.setDrawingCacheEnabled(false);
        return file;
    }

    public void checkPermissionCamera(){
        // 6.0 λ§μ¬λ©λ‘μ° μ΄μμΌ κ²½μ°μλ κΆν μ²΄ν¬ ν κΆν μμ²­
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "κΆν μ€μ  μλ£");
            } else {
                Log.d(TAG, "κΆν μ€μ  μμ²­");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //κΆν νμ© μ,
        if (requestCode == 1) {
            int length = permissions.length;
            for (int i = 0; i < length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("MainActivity", "κΆν νμ© : " + permissions[i]);
                }
            }
        }
    }

    public void checkSelfPermission() {
        String temp = "";
        //νμΌ μ½κΈ° κΆν νμΈ
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            temp += Manifest.permission.READ_EXTERNAL_STORAGE + " ";
        }
        //νμΌ μ°κΈ° κΆν νμΈ
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            temp += Manifest.permission.WRITE_EXTERNAL_STORAGE + " ";
        }
        //κΆνμμ²­
        if (TextUtils.isEmpty(temp) == false) {
            ActivityCompat.requestPermissions(this, temp.trim().split(" "), 1);
        } else {
            showToast("κΆνμ λͺ¨λ νμ©");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == TAKE_PICTURE_LEFT) {
            // μ¬μ§
            if (resultCode == RESULT_OK && intent.hasExtra("data")) {
                Bitmap bm = (Bitmap) intent.getExtras().get("data");
                if (bm != null) {
                    img_left.setImageBitmap(bm);
                    camera_left_btn.setVisibility(View.INVISIBLE);
                    album_left_btn.setVisibility(View.INVISIBLE);
                    before_text.setVisibility(View.VISIBLE);
                }
            }
            //μ¨λ²
            else if (resultCode == RESULT_OK) {
                try {
                    InputStream is = getContentResolver().openInputStream(intent.getData());
                    Bitmap bm = BitmapFactory.decodeStream(is);
                    img_left.setImageBitmap(bm);
                    camera_left_btn.setVisibility(View.INVISIBLE);
                    album_left_btn.setVisibility(View.INVISIBLE);
                    before_text.setVisibility(View.VISIBLE);
                    is.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == 101 && resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "μ·¨μ", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == TAKE_PICTURE_RIGHT) {
            if (resultCode == RESULT_OK && intent.hasExtra("data")) {
                Bitmap bm = (Bitmap) intent.getExtras().get("data");
                if (bm != null) {
                    img_right.setImageBitmap(bm);
                    camera_right_btn.setVisibility(View.INVISIBLE);
                    album_right_btn.setVisibility(View.INVISIBLE);
                    after_text.setVisibility(View.VISIBLE);
                    save_btn.setEnabled(true);
                }
            }
            else if (resultCode == RESULT_OK) {
                try {
                    InputStream is = getContentResolver().openInputStream(intent.getData());
                    Bitmap bm = BitmapFactory.decodeStream(is);
                    img_right.setImageBitmap(bm);
                    camera_right_btn.setVisibility(View.INVISIBLE);
                    album_right_btn.setVisibility(View.INVISIBLE);
                    after_text.setVisibility(View.VISIBLE);
                    is.close();
                    save_btn.setEnabled(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == 101 && resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "μ·¨μ", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}