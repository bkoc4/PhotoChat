package com.photo.advanced.photochat.controller.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import com.photo.advanced.photochat.R;
import com.photo.advanced.photochat.adapter.MainViewAdapter;
import com.photo.advanced.photochat.view.SnapTabsView;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import butterknife.BindView;

public class MainActivity extends BaseActivity implements ViewPager.OnPageChangeListener, SurfaceHolder.Callback, SnapTabsView.OnClickListener {

    @BindView(R.id.viewPager) ViewPager viewPager;
    @BindView(R.id.backgroundView) View backgroundView;
    @BindView(R.id.snapTabsView) SnapTabsView snapTabsView;

    public final int CAMERA_REQUEST_CODE = 1;
    public final int WRITE_STORAGE_REQUEST_CODE = 2;

    @BindView(R.id.svCamera)
    SurfaceView svCamera;

    private SurfaceHolder shCamera;
    private Camera camera;
    private Camera.PictureCallback jpegCallback;
    private byte[] lastBytes;

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }
    @Override
    public void initView(Bundle savedInstanceState) {
        viewPager.setAdapter(new MainViewAdapter(getSupportFragmentManager()));
        viewPager.setCurrentItem(1);
        viewPager.addOnPageChangeListener(this);
        snapTabsView.setupWithViewPager(viewPager);
        snapTabsView.setOnClickListener(this);
        shCamera = svCamera.getHolder();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        } else {
            shCamera.addCallback(this);
        }

        jpegCallback = new Camera.PictureCallback(){
            @Override
            public void onPictureTaken(byte[] bytes, Camera camera) {

                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    lastBytes = bytes;
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_STORAGE_REQUEST_CODE);
                } else {
                    Bitmap decodedBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    Bitmap rotateBitmap = rotate(decodedBitmap);
                    String fileLocation = SaveImageToStorage(rotateBitmap);
                    if (fileLocation != null) {
                        //Intent intent = new Intent(this, ShowCaptureActivity.class);
                        //startActivity(intent);
                        camera.startPreview();
                        return;
                    }
                }
            }
        };
    }

    public String SaveImageToStorage(Bitmap bitmap){
        String fileName = "imageToSend2";
        try{
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            FileOutputStream fo = openFileOutput(fileName, Context.MODE_PRIVATE);
            fo.write(bytes.toByteArray());
            fo.close();
        }catch(Exception e){
            e.printStackTrace();
            fileName = null;
        }
        return fileName;
    }

    private Bitmap rotate(Bitmap decodedBitmap) {
        int w = decodedBitmap.getWidth();
        int h = decodedBitmap.getHeight();

        Matrix matrix = new Matrix();
        matrix.setRotate(90);

        return Bitmap.createBitmap(decodedBitmap, 0, 0, w, h, matrix, true);

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (position == 0) {
            backgroundView.setBackgroundColor(ContextCompat.getColor(this, R.color.light_blue));
            backgroundView.setAlpha(1 - positionOffset);
        }
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    shCamera.addCallback(this);
                    if (shCamera.getSurface() != null) {
                        surfaceCreated(shCamera);
                    }
                } else {
                    Toast.makeText(this, "Please provide camera permission", Toast.LENGTH_LONG).show();
                }
                break;
            case WRITE_STORAGE_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Bitmap decodedBitmap = BitmapFactory.decodeByteArray(lastBytes, 0, lastBytes.length);
                    Bitmap rotateBitmap = rotate(decodedBitmap);
                    String fileLocation = SaveImageToStorage(rotateBitmap);
                    if (fileLocation != null) {
                        //Intent intent = new Intent(this, ShowCaptureActivity.class);
                        //startActivity(intent);
                        camera.startPreview();
                        return;
                    }
                } else {
                    Toast.makeText(this, "Please provide camera permission", Toast.LENGTH_LONG).show();
                }
        }
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        camera = Camera.open();

        Camera.Parameters parameters;
        parameters = camera.getParameters();

        camera.setDisplayOrientation(90);
        parameters.setPreviewFrameRate(30);
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);

        Camera.Size bestSize = null;
        List<Camera.Size> sizeList = camera.getParameters().getSupportedPreviewSizes();
        bestSize = sizeList.get(0);
        for (int i = 1; i < sizeList.size(); i++) {
            if ((sizeList.get(i).width * sizeList.get(i).height) > (bestSize.width * bestSize.height)) {
                bestSize = sizeList.get(i);
            }
        }

        parameters.setPictureSize(bestSize.width, bestSize.height);

        camera.setParameters(parameters);

        try {
            camera.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
        }

        camera.startPreview();

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void onTakePhotoClick(View view) {
        camera.takePicture(null, null, jpegCallback);
    }

    @Override
    public void onTakePhotoLongClick(View view) {

    }
}
