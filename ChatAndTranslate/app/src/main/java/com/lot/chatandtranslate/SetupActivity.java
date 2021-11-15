package com.lot.chatandtranslate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 101;
    CircleImageView profileImg;
    EditText input_name, input_age, input_city, input_job;
    Button saveBtn;

    Uri imgUri;

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference mRef;
    StorageReference sRef;

    ProgressDialog mLoadingBar;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        profileImg = findViewById(R.id.profile_image);
        input_name = findViewById(R.id.input_username);
        input_age = findViewById(R.id.input_age);
        input_city = findViewById(R.id.input_city);
        input_job = findViewById(R.id.input_job);
        saveBtn = findViewById(R.id.btn_save);
        toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(" ");
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mRef = FirebaseDatabase.getInstance().getReference().child("Users");
        sRef = FirebaseStorage.getInstance().getReference().child("ProfileImages");
        mLoadingBar = new ProgressDialog(this);

        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveDate();
            }
        });
    }

    private void SaveDate() {
        String username = input_name.getText().toString();
        String age = input_age.getText().toString();
        String city = input_city.getText().toString();
        String job = input_job.getText().toString();

        if(username.isEmpty()) {
            showError(input_name, "이름을 적어주세요.");
        } else if(city.isEmpty()) {
            showError(input_city, "거주지역을 적어주세요.");
        } else if(job.isEmpty()) {
            showError(input_job, "직업을 적어주세요.");
        } else {
            mLoadingBar.setTitle("저장중...");
            mLoadingBar.setCanceledOnTouchOutside(false);
            mLoadingBar.show();
            sRef.child(mUser.getUid()).putFile(imgUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()) {
                        sRef.child(mUser.getUid()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                HashMap map = new HashMap();
                                map.put("username", username);
                                map.put("age", age);
                                map.put("city", city);
                                map.put("job", job);
                                map.put("profileImage", uri.toString());
                                map.put("status", "offline");

                                mRef.child(mUser.getUid()).updateChildren(map).addOnSuccessListener(new OnSuccessListener() {
                                    @Override
                                    public void onSuccess(Object o) {
                                        Intent intent = new Intent(SetupActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        mLoadingBar.dismiss();
                                        Toast.makeText(SetupActivity.this, "저장 완료", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull @NotNull Exception e) {
                                        mLoadingBar.dismiss();
                                        Toast.makeText(SetupActivity.this, "저장 실패", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }
                }
            });
        }
    }

    private void showError(EditText field, String s) {
        field.setError(s);
        field.requestFocus();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            imgUri = data.getData();
            profileImg.setImageURI(imgUri);
        }
    }
}