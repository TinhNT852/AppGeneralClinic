package com.example.appgeneralclinic.Account;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.example.appgeneralclinic.Account.NguoiDung;
import com.example.appgeneralclinic.R;
public class Register extends AppCompatActivity {
    EditText editTextEmail, editTextPassword, editTextName;
    Button buttonReg;
    private FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView textView;
    ImageView imageView;
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent=new Intent(getApplicationContext(), Account.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth= FirebaseAuth.getInstance();
        editTextName=findViewById(R.id.namereg);
        editTextEmail=findViewById(R.id.emailreg);
        editTextPassword=findViewById(R.id.passwordreg);
        buttonReg=findViewById(R.id.btn_register);
        textView=findViewById(R.id.loginNow);
        progressBar=findViewById(R.id.progressBar);
        ImageView showPasswordImageView = findViewById(R.id.showPasswordButton);

        showPasswordImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isPasswordVisible =
                        editTextPassword.getTransformationMethod() == null;

                editTextPassword.setTransformationMethod(
                        isPasswordVisible
                                ? new PasswordTransformationMethod()
                                : null);

                showPasswordImageView.setImageResource(
                        isPasswordVisible
                                ? R.mipmap.show_pwd
                                : R.mipmap.hide_pwd);
            }
        });
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),Login.class);
                startActivity(intent);
                finish();
            }
        });

        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email, password, name, repass;
                name = String.valueOf(editTextName.getText());
                email = String.valueOf(editTextEmail.getText());
                password = String.valueOf(editTextPassword.getText());
                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(Register.this, "Vui lòng điền họ tên", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(email)) {
                    Toast.makeText(Register.this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(password)) {
                    Toast.makeText(Register.this, "Vui lòng nhập mật khẩu", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser firebaseUser = mAuth.getCurrentUser();

                                        NguoiDung nguoiDung = new NguoiDung(name);

                                        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Register User");
                                        referenceProfile.child(firebaseUser.getUid()).setValue(nguoiDung).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    progressBar.setVisibility(View.GONE);
                                                    Toast.makeText(Register.this, "Đăng ký thành công.", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(Register.this, Login.class);
                                                    startActivity(intent);
                                                    finish();
                                                } else {
                                                    progressBar.setVisibility(View.GONE);
                                                    Toast.makeText(Register.this, "Đăng ký không thành công, hãy thử lại", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(Register.this, "Đăng ký thất bại, hãy thử lại", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                }
            }
        });
    }
}
