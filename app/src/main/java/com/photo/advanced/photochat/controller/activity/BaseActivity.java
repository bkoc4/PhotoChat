package com.photo.advanced.photochat.controller.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.photo.advanced.photochat.helper.PreferencesHelper;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity {

    private FirebaseAuth fbUserAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getLayoutId() != -1) {
            setContentView(getLayoutId());
            ButterKnife.bind(this);
        }

        fbUserAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        PreferencesHelper.initialize(this);
        initView(savedInstanceState);

    }

    public abstract void initView(Bundle savedInstanceState);

    public FirebaseAuth getFirebaseAuth() {
        return fbUserAuth;
    }

    public void showProgres() {
        progressDialog.show();
    }

    public void hideProgress() {
        progressDialog.dismiss();
    }

    public int getLayoutId() {
        return -1;
    }
}
