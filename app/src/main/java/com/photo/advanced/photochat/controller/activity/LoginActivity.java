package com.photo.advanced.photochat.controller.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.photo.advanced.photochat.R;

import butterknife.BindView;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.btnLogin) Button btnLogin;
    @BindView(R.id.btnRegister) Button btnRegister;

    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        getFirebaseAuth().addAuthStateListener(mAuthListener);
    }

    @Override
    public void initView(Bundle savedInstanceState) {

        btnLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (btnLogin == v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            View view = getLayoutInflater().inflate(R.layout.dialog_login,null);
            Button btnDiagLogin = view.findViewById(R.id.btnDiagLogin);
            final EditText etEmail = view.findViewById(R.id.etEmail);
            final EditText etPassword = view.findViewById(R.id.etPassword);
            etEmail.setText("kocburak1994@gmail.com");
            etPassword.setText("qweqwe");
            btnDiagLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showProgres();
                    getFirebaseAuth().signInWithEmailAndPassword(etEmail.getText().toString(),etPassword.getText().toString())
                            .addOnCompleteListener(LoginActivity.this, loginCompleteListener);
                }
            });
            builder.setView(view);
            builder.show();
        } else if (btnRegister == v) {

            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            View view = getLayoutInflater().inflate(R.layout.dialog_register,null);
            Button btnDiagLogin = view.findViewById(R.id.btnDiagRegister);
            final EditText etEmail = view.findViewById(R.id.etEmail);
            final EditText etPassword = view.findViewById(R.id.etPassword);
            final EditText etPasswordAgain = view.findViewById(R.id.etPasswordAgain);
            btnDiagLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!etPassword.getText().toString().isEmpty() &&
                            !etPasswordAgain.getText().toString().isEmpty() &&
                            etPassword.getText().toString().equals(etPasswordAgain.getText().toString())) {
                        showProgres();
                        System.out.println("Burak pass :" + etPassword.getText().toString() + " email : " +etEmail.getText().toString());
                        getFirebaseAuth().createUserWithEmailAndPassword(etEmail.getText().toString(), etPassword.getText().toString())
                                .addOnCompleteListener(LoginActivity.this, registerCompleteListener);
                    } else {
                        Toast.makeText(LoginActivity.this, getString(R.string.login_activity_create_user_password_mismatch_message),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
            builder.setView(view);
            builder.show();
        }

    }

    OnCompleteListener registerCompleteListener = new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            hideProgress();
            if (task.isSuccessful()) {
                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(i);
            } else {
                Toast.makeText(LoginActivity.this, getString(R.string.login_activity_create_user_failed_message),
                        Toast.LENGTH_SHORT).show();
            }
        }
    };

    OnCompleteListener loginCompleteListener = new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {
                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
            } else {
                Toast.makeText(LoginActivity.this, getString(R.string.login_activity_authentication_failed_message),
                        Toast.LENGTH_SHORT).show();
            }
            hideProgress();
        }

    };
   FirebaseAuth.AuthStateListener mAuthListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                // User is signed in
                Log.d("Burak", "onAuthStateChanged:signed_in:" + user.getUid());
            } else {
                // User is signed out
                Log.d("Burak", "onAuthStateChanged:signed_out");
            }
            // ...
        }
    };
}
