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
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.photo.advanced.photochat.R;
import com.photo.advanced.photochat.adapter.MainViewAdapter;
import com.photo.advanced.photochat.helper.Security.SecurityHelper;
import com.photo.advanced.photochat.view.SnapTabsView;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import butterknife.BindView;

public class MainActivity extends BaseActivity implements ViewPager.OnPageChangeListener, SurfaceHolder.Callback, SnapTabsView.OnClickListener {

    @BindView(R.id.viewPager) ViewPager viewPager;
    @BindView(R.id.backgroundView) View backgroundView;
    @BindView(R.id.snapTabsView) SnapTabsView snapTabsView;

    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int WRITE_STORAGE_REQUEST_CODE = 2;

    public static final String EXTRA_CAPTURED_IMAGE_BYTES = "extra.captured_image_bytes";

    @BindView(R.id.svCamera)
    SurfaceView svCamera;

    private SurfaceHolder shCamera;
    private Camera camera;
    private Camera.PictureCallback jpegCallback;
    public static byte[] lastBytes;

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

        String plainText = "abcde";
        System.out.println("Original plaintext message: " + plainText);


        try {

            // Initialize two key pairs
            SecurityHelper to = new SecurityHelper();
            SecurityHelper from = new SecurityHelper();

            Log.d("Burak", "Private Key To : " + Base64.encodeToString(to.getKeyPair().getPrivate().getEncoded(), Base64.URL_SAFE | Base64.NO_WRAP));
            Log.d("Burak", "Private Key From : " +  Base64.encodeToString(from.getKeyPair().getPrivate().getEncoded(), Base64.URL_SAFE | Base64.NO_WRAP));
            Log.d("Burak", "Public Key To : " +  Base64.encodeToString(to.getKeyPair().getPublic().getEncoded(), Base64.URL_SAFE | Base64.NO_WRAP));
            Log.d("Burak", "Public Key From : " +  Base64.encodeToString(from.getKeyPair().getPublic().getEncoded(), Base64.URL_SAFE | Base64.NO_WRAP));

            // Create two AES secret keys to encrypt/decrypt the message
            SecretKey secretKeyA = from.generateSharedSecret(to.getKeyPair().getPublic());
            SecretKey secretKeyB = to.generateSharedSecret(from.getKeyPair().getPublic());

            System.out.println("Burak : SharedA : " +  Base64.encodeToString(secretKeyA.getEncoded(), Base64.URL_SAFE | Base64.NO_WRAP));
            System.out.println("Burak : Sharedb : " +  Base64.encodeToString(secretKeyB.getEncoded(), Base64.URL_SAFE | Base64.NO_WRAP));

            String encryptedData = to.encryptData(secretKeyA.getEncoded(),plainText.getBytes());
            Log.d("Burak","Encrypted Data : " + encryptedData);

            byte[] decryptedData = to.decryptData(secretKeyB.getEncoded(),encryptedData);
            Log.d("Burak","Decrypted Data : " +  Base64.encodeToString(decryptedData, Base64.URL_SAFE | Base64.NO_WRAP));


            /*byte[] fromCreatedAESKey = SecurityHelper.generateNewAESKey();
            String[] encryptedAESKey = from.encryptAESKey(fromCreatedAESKey, to.getKeyPair().getPublic());


            Log.d("Burak","AES Key : " + Arrays.toString(fromCreatedAESKey));
            Log.d("Burak","Encrypted AES Key for To : " + encryptedAESKey[1]);
            Log.d("Burak","Encrypted AES Key for from : " + encryptedAESKey[0]);

*/

/*
            // Encrypt the message using 'secretKeyA'
            String cipherText = SecurityHelper.encryptString(secretKeyA, plainText);
            System.out.println("Encrypted cipher text: " + cipherText);

            // Decrypt the message using 'secretKeyB'
            String decryptedPlainText = SecurityHelper.decryptString(secretKeyB, cipherText);
            System.out.println("Decrypted cipher text: " + decryptedPlainText);
            */
        }  catch (Exception e) {
            e.printStackTrace();
        }

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        } else {
            shCamera.addCallback(this);
        }

        jpegCallback = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] bytes, Camera camera) {

                System.out.println("Burak size : " + bytes.length);
                lastBytes = bytes;
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_STORAGE_REQUEST_CODE);
                } else {
                    Intent intent = new Intent(MainActivity.this, ShowCaptureActivity.class);
                    startActivity(intent);
                    camera.startPreview();
                }
            }
        };
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


                    Intent intent = new Intent(MainActivity.this, ShowCaptureActivity.class);
                    //intent.putExtra(EXTRA_CAPTURED_IMAGE_BYTES, lastBytes);
                    startActivity(intent);
                    camera.startPreview();
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
