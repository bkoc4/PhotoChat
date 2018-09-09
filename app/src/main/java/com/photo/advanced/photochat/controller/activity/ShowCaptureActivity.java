package com.photo.advanced.photochat.controller.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.photo.advanced.photochat.R;
import com.photo.advanced.photochat.controller.fragment.ConversationsFragment;
import com.photo.advanced.photochat.helper.DataHelper;
import com.photo.advanced.photochat.helper.Security.SecurityHelper;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import butterknife.BindView;

public class ShowCaptureActivity extends BaseActivity implements View.OnClickListener {

    public static final int REQUEST_USER_ID = 1;

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

        if (intent.hasExtra(ConversationsFragment.EXTRA_OPEN_CHAT_IMAGE)) {
            fabSend.setVisibility(View.GONE);
            DataHelper.getInstance().getUserCollection()
                    .document(DataHelper.getInstance().getUserAuth().getUid())
                    .collection("received")
                    .document(getIntent().getStringExtra(ConversationsFragment.EXTRA_OPEN_CHAT_IMAGE))
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull final Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {

                                final long TWO_MEGABYTE = 2 * 1024 * 1024;
                                DataHelper.getInstance().getStorage()
                                        .child("images/" + task.getResult().get("imageUrl"))
                                        .getBytes(TWO_MEGABYTE)
                                        .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                            @Override
                                            public void onSuccess(final byte[] bytes) {

                                                final SecurityHelper securityHelper = new SecurityHelper();

                                                DataHelper.getInstance().getUserCollection()
                                                        .document(task.getResult().getString("fromUserId"))
                                                        .get()
                                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                if (task.isSuccessful()) {
                                                                    PublicKey publicKey = securityHelper.fromStringToPublicKey((String) task.getResult().get("keyX"), (String) task.getResult().get("keyY"));
                                                                    SecretKey secretKey = securityHelper.generateSharedSecret(publicKey);
                                                                    byte[] decryptedData = securityHelper.decryptData(secretKey.getEncoded(), bytes);

                                                                    Bitmap bitmap = BitmapFactory.decodeByteArray(decryptedData, 0, decryptedData.length);
                                                                    ivCapture.setImageBitmap(rotate(bitmap));
                                                                }
                                                            }
                                                        });


                                            }
                                        });


                            } else {
                                Toast.makeText(getApplicationContext(), "Bir hata oluştu",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            return;
        } else {

            imageBytes = MainActivity.lastBytes; //intent.getByteArrayExtra(MainActivity.EXTRA_CAPTURED_IMAGE_BYTES);

            Bitmap decodedBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            Bitmap rotateBitmap = rotate(decodedBitmap);
            ivCapture.setImageBitmap(rotateBitmap);
        }

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

            Intent i = new Intent(this, ListUserActivity.class);
            startActivityForResult(i,REQUEST_USER_ID);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (REQUEST_USER_ID == requestCode) {
            if (RESULT_OK == resultCode) {

                showProgres();
                final SecurityHelper securityHelper = new SecurityHelper();

                DataHelper.getInstance().getUserCollection()
                        .document(data.getStringExtra(ListUserActivity.EXTRA_USER_ID))
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            PublicKey publicKey = securityHelper.fromStringToPublicKey((String) task.getResult().get("keyX"), (String) task.getResult().get("keyY"));
                            SecretKey secretKey = securityHelper.generateSharedSecret(publicKey);
                            byte[] encryptedData = securityHelper.encryptData(secretKey.getEncoded(), imageBytes);

                            System.out.println("Burak - image uploaded");

                            long time = System.currentTimeMillis();
                            final String fileName = "file" + time;
                            StorageReference imagesRef = DataHelper.getInstance().getStorage().child("images/" + fileName);
                            imagesRef.putBytes(encryptedData)
                                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            Map<String, Object> message = new HashMap<>();
                                            message.put("fromUserId", DataHelper.getInstance().getUserAuth().getCurrentUser().getUid());
                                            message.put("date", System.currentTimeMillis());
                                            message.put("imageUrl", fileName);
                                            message.put("isRead", false);

                                            DataHelper.getInstance().getUserCollection()
                                                    .document(data.getStringExtra(ListUserActivity.EXTRA_USER_ID))
                                                    .collection("received")
                                                    .add(message)
                                                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentReference> task) {
                                                            hideProgress();
                                                            setResult(RESULT_OK);
                                                            finish();
                                                        }
                                                    });

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    hideProgress();
                                    Toast.makeText(getApplicationContext(), "Bir hata oluştu", Toast.LENGTH_SHORT).show();
                                    setResult(RESULT_OK);
                                    finish();
                                }
                            });
                        }
                    }
                });
            }
        }
    }
}
