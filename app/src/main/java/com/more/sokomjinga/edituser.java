package com.more.sokomjinga;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class edituser extends AppCompatActivity {

    private ImageView profile_image;
    private EditText user_email,user_town,user_name;
    private Button user_update_button;
    private Button Editprofilepicbutton;
    private static final int CAMERA_REQUEST_CODE =1;

    private ProgressDialog mProgress;

    FirebaseAuth mAuth;
    private StorageReference mStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edituser);

        user_email = (EditText) findViewById(R.id.user_email);
        user_town = (EditText) findViewById(R.id.user_town);
        user_name = (EditText) findViewById(R.id.user_name);
        user_update_button = (Button) findViewById(R.id.user_update_button);
        profile_image = (ImageView) findViewById(R.id.profile_image);
        Editprofilepicbutton = (Button) findViewById(R.id.Editprofilepicbutton);

        mProgress = new ProgressDialog(this);


        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();

        user_update_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user_id = mAuth.getCurrentUser().getUid();
                try {
                    String email = user_email.getText().toString();
                    String town = user_town.getText().toString();
                    String name = user_name.getText().toString();

                    DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
                    Map newPost = new HashMap();
                    newPost.put("email",email);
                    newPost.put("town",town);
                    newPost.put("name",name);
                    current_user_db.setValue(newPost);

                    Toast.makeText(getApplicationContext(),"User Details saved Successfully", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(edituser.this, commuter.class);
                    startActivity(intent);
                    finish();


                } catch (Exception e) {
            e.printStackTrace();
        }

            }
        });



        Editprofilepicbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,CAMERA_REQUEST_CODE);


            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==CAMERA_REQUEST_CODE && resultCode== RESULT_OK){
            mProgress.setMessage("Uploading Image...");
            mProgress.show();

             Uri imageUri = data.getData();
            StorageReference filepath = mStorage.child("Photo").child(imageUri.getLastPathSegment());
            filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mProgress.dismiss();



                    Uri downloadUri = taskSnapshot.getDownloadUrl();
                   // Picasso.with(edituser.this)






                    Toast.makeText(getApplicationContext(),"Upload Successful!",Toast.LENGTH_SHORT).show();

                }
            });
        }

    }
}
