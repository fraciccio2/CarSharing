package com.example.carsharing.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.carsharing.R;
import com.example.carsharing.databinding.ActivityLoginBinding;
import com.example.carsharing.services.DataBaseHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;
    DataBaseHelper dataBaseHelper;
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        setContentView(binding.getRoot());

        dataBaseHelper = new DataBaseHelper(getApplicationContext());

        checkIfAreLoggedUser();

        loginButton();
        signUpRedirect();
        forgotPassword();
    }

    private void loginButton() {
        binding.loginButton.setOnClickListener(view -> {
            String email = binding.loginEmail.getText().toString();
            String password = binding.loginPassword.getText().toString();

            if (email.equals("") || password.equals("")) {
                Toast.makeText(LoginActivity.this, getString(R.string.all_field_required_text), Toast.LENGTH_SHORT).show();
            } else {
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, getString(R.string.success_login_text), Toast.LENGTH_SHORT).show();
                                FirebaseUser user = mAuth.getCurrentUser();
                                dataBaseHelper.insertData(email, password);
                                if (user != null) {
                                    mDatabase.child("users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists() && snapshot.hasChildren()) {
                                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                startActivity(intent);
                                            } else {
                                                Intent intent = new Intent(getApplicationContext(), AddInfoActivity.class);
                                                startActivity(intent);
                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Log.println(Log.ERROR, "Errore", "");
                                        }
                                    });
                                }
                            } else {
                                Toast.makeText(LoginActivity.this, getString(R.string.email_password_wrong_text), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    private void signUpRedirect() {
        binding.signUpRedirect.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
            startActivity(intent);
        });
    }

    private void forgotPassword() {
        binding.forgotPassword.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_forgot, null);
            EditText emailBox = dialogView.findViewById(R.id.email_box);
            builder.setView(dialogView);
            AlertDialog dialog = builder.create();

            dialogView.findViewById(R.id.button_reset).setOnClickListener(view1 -> {
                String userEmail = emailBox.getText().toString();
                if (TextUtils.isEmpty(userEmail) && !dataBaseHelper.isCorrectEmail(userEmail)) {
                    Toast.makeText(LoginActivity.this, getString(R.string.email_wrong_text), Toast.LENGTH_SHORT).show();
                    return;
                }
                mAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, getString(R.string.check_email_text), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(LoginActivity.this, getString(R.string.error_forgot_password_text), Toast.LENGTH_SHORT).show();
                    }
                });
            });

            dialogView.findViewById(R.id.button_cancel).setOnClickListener(view2 -> dialog.dismiss());
            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            dialog.show();
        });
    }

    private void checkIfAreLoggedUser() {
        HashMap<String, String> map = dataBaseHelper.getEmailAndPassword();
        if (map != null) {
            mAuth.signInWithEmailAndPassword(map.get("email"), map.get("password")).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }
}