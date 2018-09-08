package com.photo.advanced.photochat.controller.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.photo.advanced.photochat.R;
import com.photo.advanced.photochat.helper.Security.SecurityHelper;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import butterknife.BindView;

public class ShowCaptureActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.fabSend) FloatingActionButton fabSend;
    @BindView(R.id.ivCapture) ImageView ivCapture;

    private byte[] imageBytes;

    @Override
    public int getLayoutId() {
        return R.layout.activity_show_capture;
    }

    @Override
    public void initView(Bundle savedInstanceState) {

        Intent intent = getIntent();

       // if (intent.hasExtra(MainActivity.EXTRA_CAPTURED_IMAGE_BYTES)) {

            imageBytes = MainActivity.lastBytes; //intent.getByteArrayExtra(MainActivity.EXTRA_CAPTURED_IMAGE_BYTES);

            Bitmap decodedBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            Bitmap rotateBitmap = rotate(decodedBitmap);
            //String fileLocation = SaveImageToStorage(rotateBitmap);
            ivCapture.setImageBitmap(rotateBitmap);
       /* } else {
            setResult(RESULT_CANCELED);
            //finish();
        }*/

        fabSend.setOnClickListener(this);

    }

    public String SaveImageToStorage(Bitmap bitmap) {
        String fileName = "imageToSend2";
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            FileOutputStream fo = openFileOutput(fileName, Context.MODE_PRIVATE);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (Exception e) {
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
    public void onClick(View v) {
        if (fabSend == v) {
            if (imageBytes == null) {
                setResult(RESULT_CANCELED);
                finish();
                return;
            }
            showProgres();
            try {

                SecurityHelper to = new SecurityHelper();
                SecurityHelper from = new SecurityHelper();

                Log.d("Burak", "Private Key To : " + Base64.encodeToString(to.getKeyPair().getPrivate().getEncoded(), Base64.URL_SAFE | Base64.NO_WRAP));
                Log.d("Burak", "Private Key From : " + Base64.encodeToString(from.getKeyPair().getPrivate().getEncoded(), Base64.URL_SAFE | Base64.NO_WRAP));
                Log.d("Burak", "Public Key To : " + Base64.encodeToString(to.getKeyPair().getPublic().getEncoded(), Base64.URL_SAFE | Base64.NO_WRAP));
                Log.d("Burak", "Public Key From : " + Base64.encodeToString(from.getKeyPair().getPublic().getEncoded(), Base64.URL_SAFE | Base64.NO_WRAP));

                // Create two AES secret keys to encrypt/decrypt the message
                SecretKey secretKeyA = from.generateSharedSecret(to.getKeyPair().getPublic());
                SecretKey secretKeyB = to.generateSharedSecret(from.getKeyPair().getPublic());

                System.out.println("Burak : SharedA : " + Base64.encodeToString(secretKeyA.getEncoded(), Base64.URL_SAFE | Base64.NO_WRAP));
                System.out.println("Burak : Sharedb : " + Base64.encodeToString(secretKeyB.getEncoded(), Base64.URL_SAFE | Base64.NO_WRAP));

                String encryptedData = to.encryptData(secretKeyA.getEncoded(), imageBytes);
                Log.d("Burak", "Encrypted Data : " + encryptedData);

                byte[] decryptedData = to.decryptData(secretKeyB.getEncoded(), encryptedData);
                Log.d("Burak", "Decrypted Data : " + Base64.encodeToString(decryptedData, Base64.URL_SAFE | Base64.NO_WRAP));


                StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                StorageReference riversRef = storageRef.child("images2/" + System.currentTimeMillis());

                riversRef.putBytes(encryptedData.getBytes()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
/*
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                                .setTimestampsInSnapshotsEnabled(true)
                                .build();
                        db.setFirestoreSettings(settings);

                        db.collection("Users").whe
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (DocumentSnapshot document : task.getResult()) {
                                                document.
                                                Log.d(TAG, document.getId() + " => " + document.getData());
                                            }
                                        } else {
                                            Log.w(TAG, "Error getting documents.", task.getException());
                                        }
                                    }
                                });

                        Map<String, Object> user = new HashMap<>();
                        user.put("userId", "Ada");
                        user.put("last", "Lovelace");
                        user.put("born", 1815);

                        db.collection("Users").add(user).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {

                            }
                        });
*/
                        hideProgress();
                        setResult(RESULT_OK);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        hideProgress();
                        Toast.makeText(getApplicationContext(), "Bir hata oluştu",Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
