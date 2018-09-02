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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
            btnDiagLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showProgres();
                    getFirebaseAuth().signInWithEmailAndPassword(etEmail.getText().toString(),etPassword.getText().toString())
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            hideProgress();
                            if (task.isSuccessful()) {
                                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(i);
                            } else {
                                Toast.makeText(LoginActivity.this, getString(R.string.login_activity_authentication_failed_message),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
            builder.setView(view);
            builder.show();
        } else if (btnRegister == v) {

            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            View view = getLayoutInflater().inflate(R.layout.dialog_login,null);
            Button btnDiagLogin = view.findViewById(R.id.btnDiagLogin);
            final EditText etEmail = view.findViewById(R.id.etEmail);
            final EditText etPassword = view.findViewById(R.id.etPassword);
            btnDiagLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showProgres();
                    getFirebaseAuth().createUserWithEmailAndPassword(etEmail.getText().toString(), etPassword.getText().toString())
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
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
                    });
                }
            });
            builder.setView(view);
            builder.show();
        }

    }
}
