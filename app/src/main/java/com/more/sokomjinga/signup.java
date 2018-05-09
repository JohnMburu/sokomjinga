package com.more.sokomjinga;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;

import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class signup extends AppCompatActivity {


    FirebaseAuth mAuth;
    EditText txt_mobile_no;
    Button btn_Verify;

    String VerificationCode;
    String number;

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    ProgressDialog dialog;
    private ProgressDialog mProgress;
    CountryCodePicker ccp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        txt_mobile_no = (EditText)findViewById(R.id.txt_mobile_no);
        btn_Verify = (Button)findViewById(R.id.btn_Verify);

        ccp = (CountryCodePicker)findViewById(R.id.ccpicker) ;
        ccp.registerCarrierNumberEditText(txt_mobile_no);

        mProgress = new ProgressDialog(this);




        //initialize firebase database
        mAuth= FirebaseAuth.getInstance();
        mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithMobile(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(getApplicationContext(),"Verification Error",Toast.LENGTH_SHORT).show();
                mProgress.dismiss();

            }

            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(verificationId, forceResendingToken);
                mVerificationId = verificationId;
                mResendToken = forceResendingToken;
                Toast.makeText(getApplicationContext(),"Code sent to: "+number,Toast.LENGTH_SHORT).show();
            }
        };

    }


    public void sendCode(View view){
        number = ccp.getFullNumberWithPlus();
        //setupVerificationCallbacks();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,60, TimeUnit.SECONDS,this,mCallBack
        );
        txt_mobile_no.setEnabled(false);
        btn_Verify.setEnabled(false);
        mProgress.setMessage("Authenticating User...");
        mProgress.show();
    }


    public void signInWithMobile(PhoneAuthCredential credential){
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser user = task.getResult().getUser();
                            finish();
                            //login
                            Intent intent = new Intent(signup.this, splash.class);
                            startActivity(intent);
                            mProgress.dismiss();
                        }

                    }
                });
    }

    public void VerifyPhoneNumber(String input_code,String VerifyCode){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(input_code,VerifyCode);
        signInWithMobile(credential);
    }

}
