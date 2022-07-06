package com.example.imagecapture;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.OutputStream;
import java.net.URI;

public class MainActivity extends AppCompatActivity {
    ActivityResultLauncher<Intent>activityResultLauncher;
    Button capturebutton;
    Button savebutton;
    ImageView image;
    Bitmap bitmap;
    Uri uri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)== PackageManager.PERMISSION_DENIED  || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},100);
        }
        capturebutton=findViewById(R.id.capturebutton);
        savebutton=findViewById(R.id.savebutton);
        image=findViewById(R.id.image);
        activityResultLauncher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode()==RESULT_OK && result.getData()!=null){
                    Bundle bundle=result.getData().getExtras();
                    bitmap=(Bitmap)bundle.get("data");
                    image.setImageBitmap(bitmap);
                }
            }
        });
        capturebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent intent =new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(intent.resolveActivity(getPackageManager())!=null) {
                    activityResultLauncher.launch(intent);
                }
                else{
                    Toast.makeText(MainActivity.this, "You do not have proper application for doing this operation or not proper permissions", Toast.LENGTH_SHORT).show();
                }
            }
        });
        ContentResolver contentResolver = getContentResolver();
        savebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in=new Intent(Intent.ACTION_SEND);
                in.setType("image/jpeg");
                ContentValues cv=new ContentValues();
                cv.put(MediaStore.Images.Media.TITLE,"Capture Image");
                cv.put(MediaStore.Images.Media.MIME_TYPE,"image/jpeg");

                try {
                    uri=contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,cv);
                    OutputStream outputStream = getContentResolver().openOutputStream(uri);
                    outputStream.close();
                }
                catch (Exception e){

                }
                in.putExtra(Intent.EXTRA_STREAM,uri);
                startActivity(Intent.createChooser(in,"Share image"));
            }
        });
    }
}