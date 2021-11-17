package com.lot.chatandtranslate;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;
import com.lot.chatandtranslate.Utils.Chat;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView;
    EditText inputSms;
    ImageView btnSend, btnTranslate;
    CircleImageView userProfileImageAppbar;
    TextView usernameAppbar;
    TextView status;
    String otherUserID;
    DatabaseReference mUserRef, mSmsRef;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    String otherUsername, otherUserProfileLink, otherUserCurrentStatus;
    FirebaseRecyclerOptions<Chat> options;
    FirebaseRecyclerAdapter<Chat, ChatMyViewHolder> adapter;
    String myProfileImageLink;
    String URL = "https://fcm.googleapis.com/fcm/send";
    RequestQueue requestQueue;
    String username;

    //Translate
    String[] fromLanguages = {"From", "English", "Korean", "Japanese", "Russian", "German", "French"};
    String[] toLanguages = {"To", "English", "Korean", "Japanese", "Russian", "German", "French"};
    private static final int REQUEST_PERMISSOIN_CODE = 1;
    String fromLanguageCode, toLanguageCode = "";
    TextInputEditText sourceEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        toolbar = findViewById(R.id.app_bar);
        recyclerView = findViewById(R.id.recyclerview);
        inputSms = findViewById(R.id.inputSms);
        btnSend = findViewById(R.id.btn_send);
        btnTranslate = findViewById(R.id.btn_translate);
        userProfileImageAppbar = findViewById(R.id.userProfileImageAppbar);
        usernameAppbar = findViewById(R.id.usernameAppbar);
        status = findViewById(R.id.current_status);
        setSupportActionBar(toolbar);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        otherUserID = getIntent().getStringExtra("OtherUserID");
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mSmsRef = FirebaseDatabase.getInstance().getReference().child("Message");

        requestQueue = Volley.newRequestQueue(this);

        LoadOtherUser();
        LoadMyProfile();
        btnSend.setOnClickListener(v -> {
            SendSMS();
        });

        LoadSMS();

        btnTranslate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ChatActivity.this, R.style.BottomSheetDialogTheme);
                ViewGroup bottomSheetView = (ViewGroup) LayoutInflater.from(getApplicationContext()).inflate(R.layout.activity_translate, (LinearLayout)
                findViewById(R.id.bottomSheetContainer));
                bottomSheetView.findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog.dismiss();
                    }
                });
                Spinner fromSpinner = bottomSheetView.findViewById(R.id.idFromSpinner);
                Spinner toSpinner = bottomSheetView.findViewById(R.id.idToSpinner);

                sourceEdit = bottomSheetView.findViewById(R.id.idEditSource);
                ImageView micIV = bottomSheetView.findViewById(R.id.idIVMic);
                TextView translatedTV = bottomSheetView.findViewById(R.id.translatedTV);


                fromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        fromLanguageCode = getLanguageCode(fromLanguages[position]);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                ArrayAdapter fromAdapter = new ArrayAdapter(ChatActivity.this, R.layout.spinner_item, fromLanguages);
                fromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                fromSpinner.setAdapter(fromAdapter);


                toSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        toLanguageCode = getLanguageCode(toLanguages[position]);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ArrayAdapter toAdapter = new ArrayAdapter(ChatActivity.this, R.layout.spinner_item, toLanguages);
                toAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                toSpinner.setAdapter(toAdapter);

                bottomSheetView.findViewById(R.id.idBtnTranslate).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        translatedTV.setText("");
                        if(sourceEdit.getText().toString().isEmpty()) {
                            Toast.makeText(ChatActivity.this, "번역할 메세지를 적어주세요", Toast.LENGTH_SHORT).show();
                        } else if(fromLanguageCode.isEmpty() || fromLanguageCode == null) {
                            Toast.makeText(ChatActivity.this, "From 언어를 설정해주세요", Toast.LENGTH_SHORT).show();
                        } else if(toLanguageCode.isEmpty() || toLanguageCode == null) {
                            Toast.makeText(ChatActivity.this, "To 언어를 설정해주세요", Toast.LENGTH_SHORT).show();
                        } else {
                            translateText(fromLanguageCode, toLanguageCode, sourceEdit.getText().toString(), bottomSheetDialog, translatedTV);
                        }
                    }
                });

                micIV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            if(fromLanguageCode.isEmpty() || fromLanguageCode == null) {
                                Toast.makeText(ChatActivity.this, "From 언어를 설정해주세요", Toast.LENGTH_SHORT).show();
                            } else if(toLanguageCode.isEmpty() || toLanguageCode == null) {
                                Toast.makeText(ChatActivity.this, "To 언어를 설정해주세요", Toast.LENGTH_SHORT).show();
                            } else {
                                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "번역할 메세지를 말해주세요");
                                startActivityForResult(intent, REQUEST_PERMISSOIN_CODE);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(ChatActivity.this, "요청 실패", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_PERMISSOIN_CODE) {
            if(resultCode==RESULT_OK && data!=null) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                sourceEdit.setText(result.get(0));
            }
        }
    }

    private void LoadMyProfile() {
        mUserRef.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    myProfileImageLink = snapshot.child("profileImage").getValue().toString();
                    username = snapshot.child("username").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Toast.makeText(ChatActivity.this, "요청 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void LoadSMS() {
        options = new FirebaseRecyclerOptions.Builder<Chat>().setQuery(mSmsRef.child(mUser.getUid()).child(otherUserID), Chat.class).build();
        adapter= new FirebaseRecyclerAdapter<Chat, ChatMyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull @NotNull ChatMyViewHolder holder, int position, @NonNull @NotNull Chat model) {
                if(model.getUserID().equals(mUser.getUid())) {
                    holder.firstUserText.setVisibility(View.GONE);
                    holder.firstUserProfileImage.setVisibility(View.GONE);
                    holder.secondUserText.setVisibility(View.VISIBLE);
                    holder.secondUserProfileImage.setVisibility(View.VISIBLE);

                    holder.secondUserText.setText(model.getSms());
                    Picasso.get().load(myProfileImageLink).into(holder.secondUserProfileImage);
                }
                 else {
                    holder.firstUserText.setVisibility(View.VISIBLE);
                    holder.firstUserProfileImage.setVisibility(View.VISIBLE);
                    holder.secondUserText.setVisibility(View.GONE);
                    holder.secondUserProfileImage.setVisibility(View.GONE);

                    holder.firstUserText.setText(model.getSms());
                    Picasso.get().load(otherUserProfileLink).into(holder.firstUserProfileImage);
                }
            }

            @NotNull
            @Override
            public ChatMyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view_sms,parent,false);
                return new ChatMyViewHolder(view);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    private void SendSMS() {
        final String sms = inputSms.getText().toString();
        if(sms.isEmpty()) {
            Toast.makeText(this, "보내실 내용을 입력해주세요", Toast.LENGTH_SHORT).show();
        } else {
            HashMap map = new HashMap();
            map.put("sms", sms);
            map.put("status", "unseen");
            map.put("userID", mUser.getUid());
            mSmsRef.child(otherUserID).child(mUser.getUid()).push().updateChildren(map).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull @NotNull Task task) {
                    if(task.isSuccessful()) {
                        mSmsRef.child(mUser.getUid()).child(otherUserID).push().updateChildren(map).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task task) {
                                if(task.isSuccessful()) {
                                    try {
                                        SendNotification(sms);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    inputSms.setText(null);
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    private void SendNotification(String sms) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("to", "/topics/"+otherUserID);
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("title", username+"님에게로부터 메세지가 왔습니다.");
        jsonObject1.put("body", sms);

        JSONObject jsonObject2 = new JSONObject();
        jsonObject2.put("userID", mUser.getUid());
        jsonObject2.put("type", "sms");

        jsonObject.put("notification", jsonObject1);
        jsonObject.put("data", jsonObject2);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,URL,jsonObject, response -> {

        }, error -> {

        }){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> map = new HashMap<>();
                map.put("content-type", "application/json");
                map.put("authorization", "key=AAAA5TK8qos:APA91bGLcwYfD1hjUEqY2E7Eh9_I9rLxBzBWHFLEfz2fy6D8Qvb3pGSZ2b8ndcuoWbfZ48uzTOI4Esz6AuvobpGr0JbvrKHzn4M6N06kIXkbVDQkcJ4zEZpnd2ofYFkSdPn6nPhxI5wh");
                return map;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    private void LoadOtherUser() {
        mUserRef.child(otherUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    otherUsername = snapshot.child("username").getValue().toString();
                    otherUserProfileLink = snapshot.child("profileImage").getValue().toString();
                    otherUserCurrentStatus = snapshot.child("status").getValue().toString();

                    Picasso.get().load(otherUserProfileLink).into(userProfileImageAppbar);
                    usernameAppbar.setText(otherUsername);
                    status.setText(otherUserCurrentStatus);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Toast.makeText(ChatActivity.this, "요청 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void translateText(String fromLanguageCode, String toLanguageCode, String source, BottomSheetDialog dialog, TextView translatedTV) {
        TranslatorOptions options = new TranslatorOptions.Builder().setSourceLanguage(fromLanguageCode).setTargetLanguage(toLanguageCode).build();
        Translator translator = Translation.getClient(options);
        translatedTV.setText("번역 모델 다운로드중...");
        translator.downloadModelIfNeeded().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                translator.translate(source).addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        inputSms.setText(s);
                        dialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Toast.makeText(ChatActivity.this, "요청 실패", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(ChatActivity.this, "요청 실패", Toast.LENGTH_SHORT).show();
        });
    }

    public String getLanguageCode(String language) {
        String languageCode;
        switch(language) {
            case "English":
                languageCode = TranslateLanguage.ENGLISH; //en
                break;
            case "Korean":
                languageCode = TranslateLanguage.KOREAN;
                break;
            case "Japanese":
                languageCode = TranslateLanguage.JAPANESE;
                break;
            case "Russian":
                languageCode = TranslateLanguage.RUSSIAN;
                break;
            case "German":
                languageCode = TranslateLanguage.GERMAN;
                break;
            case "French":
                languageCode = TranslateLanguage.FRENCH;
                break;
            default:
                languageCode = "";
        }
        return languageCode;
    }
}