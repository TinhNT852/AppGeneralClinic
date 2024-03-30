package com.example.appgeneralclinic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {

    TextInputEditText editTextFullName,editTextDateOfBirth,editTextComfirmPasword,editTextEmail, editTextPassword;
    Button buttonReg;

    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView textView;
    DatePickerDialog picker;

    RadioGroup radioGroupRegisterGender;
    RadioButton radioButtonRegisterGenderSelected;

    private  static final String TAG="Register Activity";
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent=new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth= FirebaseAuth.getInstance();
        editTextFullName=findViewById(R.id.namereg);
        editTextEmail=findViewById(R.id.emailreg);
        editTextPassword=findViewById(R.id.passwordreg);
        buttonReg=findViewById(R.id.btn_register);
        progressBar=findViewById(R.id.progressBar);
        textView=findViewById(R.id.loginNow);


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

                int selectedGenderId= radioGroupRegisterGender.getCheckedRadioButtonId();
                radioButtonRegisterGenderSelected=findViewById(selectedGenderId);

                progressBar=findViewById(R.id.progressBar);
                //Obtain the entered data
                String textFullName=editTextFullName.getText().toString();
                String textEmail=editTextEmail.getText().toString();
                String textPwd=editTextPassword.getText().toString();
                progressBar.setVisibility(View.VISIBLE);


                if(TextUtils.isEmpty(textFullName))
                {
                    Toast.makeText(Register.this,"Vui lòng nhập đầy đủ tên",Toast.LENGTH_LONG).show();
                    editTextFullName.setError("Cần điền đầy đủ họ và tên");
                    editTextFullName.requestFocus();
                }else if(TextUtils.isEmpty(textEmail)){
                    Toast.makeText(Register.this,"Vui lòng nhập Email",Toast.LENGTH_LONG).show();
                    editTextEmail.setError("Cần điền Email");
                    editTextEmail.requestFocus();
                } else if(!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()){
                    Toast.makeText(Register.this,"Vui lòng nhập lại Email",Toast.LENGTH_LONG).show();
                    editTextEmail.setError("Cần điền Email");
                    editTextEmail.requestFocus();

                }else if(TextUtils.isEmpty(textPwd)){
                    Toast.makeText(Register.this,"Vui lòng cài mật khẩu",Toast.LENGTH_LONG).show();
                    editTextPassword.setError("Cần có mật khẩu");
                    editTextPassword.requestFocus();
                } else if(textPwd.length()<6){
                    Toast.makeText(Register.this,"Vui lòng cài mật khẩu trên 6 ký tự ",Toast.LENGTH_LONG).show();
                    editTextPassword.setError("Mật khẩu quá yếu");
                    editTextPassword.requestFocus();
                }
            }


            private void registerUser(String textFullName, String textEmail, String textDob, String textGender, String textPwd) {
                FirebaseAuth auth=FirebaseAuth.getInstance();

                //Create User Profile
                mAuth.createUserWithEmailAndPassword(textEmail,textPwd).addOnCompleteListener(Register.this,
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(Register.this,"Tài khoản được tạo thành công",Toast.LENGTH_LONG).show();
                                    FirebaseUser firebaseUser= auth.getCurrentUser();

                                    UserProfileChangeRequest profileChangeRequest= new UserProfileChangeRequest.Builder().setDisplayName(textFullName).build();
                                    firebaseUser.updateProfile(profileChangeRequest);

                                    //Enter User Data into the Firebase Realtime Database.
                                    ReadwriteUserDetail writeuserDetails = new ReadwriteUserDetail(textDob,textGender);

                                    //Extracting User reference from Database for "Register User"
                                    DatabaseReference referenceProfile= FirebaseDatabase.getInstance().getReference("Register User");

//                                    referenceProfile.child(firebaseUser.getUid()).setValue(writeuserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                        @Override
//                                        public void onComplete(@NonNull Task<Void> task) {
//
//                                            if(task.isSuccessful()){
//                                                //Open User Profile after successful registration
//                                                Intent intent=new Intent(Register.this,UserProfile.class);
//                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_NEW_TASK);
//
//                                                startActivity(intent);
//                                                finish(); //to close Register Activity
//                                            }else{
//                                                Toast.makeText(Register.this,"User register failed,Please try again"
//                                                        ,Toast.LENGTH_LONG).show();
//
//                                            }
//                                            //Hide ProgressBar when created Successful or Failed
//                                            progressBar.setVisibility(View.GONE);
//                                        }
//                                    });
//                                    // Gửi thư xác nhận Email
//                                    firebaseUser.sendEmailVerification();

                                    //Open User Profile after successful registration
//                                    Intent intent=new Intent(Register.this,UserProfileActivity.class);
//                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK
//                                    |Intent.FLAG_ACTIVITY_NEW_TASK);
//
//                                    startActivity(intent);
//                                    finish(); to close Register Activity
                                }
                                else {
                                    try {
                                        throw task.getException();
                                    } catch (FirebaseAuthWeakPasswordException e) {
                                        editTextPassword.setError("Mật khẩu của bạn quá yếu");
                                        editTextPassword.requestFocus();
                                    } catch (FirebaseAuthInvalidCredentialsException e) {
                                        editTextPassword.setError("Email của bạn không hợp lệ hoặc đã được sử dụng");
                                        editTextPassword.requestFocus();
                                    } catch (FirebaseAuthInvalidUserException e) {
                                        editTextPassword.setError("Người dùng này đăng kí bằng với email này");
                                        editTextPassword.requestFocus();
                                    } catch (Exception e) {
                                        Log.e(TAG, e.getMessage());
                                        Toast.makeText(Register.this, e.getMessage(), Toast.LENGTH_LONG).show();

                                    }
                                    progressBar.setVisibility(View.GONE);
                                }
                            }
                        });
            }
        });






    }
}