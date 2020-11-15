package com.example.coordinate;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Registration extends AppCompatActivity {

    EditText mNome,mPassword,mEmail,mPhone;
    Button mRegistrationB;
    TextView mLoginB;
    FirebaseAuth fAuth;
    ProgressBar mPrograssBar;
    FirebaseFirestore fStore;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mNome = findViewById(R.id.editTextTextPersonName4);
        mPassword = findViewById(R.id.editTextTextPassword2);
        mEmail = findViewById(R.id.editTextTextEmailAddress2);
        mPhone = findViewById(R.id.editTextPhone);
        mRegistrationB = findViewById(R.id.buttonRE);
        mLoginB = findViewById(R.id.LoginB);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        mPrograssBar =findViewById(R.id.progressBar);

        if (fAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();

        }


        mRegistrationB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                final String Name = mNome.getText().toString();
                final String phone = mPhone.getText().toString();

                if(TextUtils.isEmpty(email)){
                    mEmail.setError("E-Mail richiesta");
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    mPassword.setError("Password richiesta");
                    return;
                }
                if (password.length() < 8){
                    mPassword.setError("La password deve avere almeno 8 caratteri");
                    return;
                }
                mPrograssBar.setVisibility(View.VISIBLE);

                //registrazione FIREBASE

                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(com.example.coordinate.Registration.this, "Utente Creato", Toast.LENGTH_SHORT).show();
                            userID = fAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = fStore.collection("users").document(userID);
                            Map<String, Object> user = new HashMap<>();
                            user.put("Nome",Name);
                            user.put("email",email);
                            user.put("Telefono",phone);
                            user.put("user_id",FirebaseAuth.getInstance().getUid());
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("TAG","onSuccess: is created for"+userID);
                                }
                            });
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));

                        }else {
                            Toast.makeText(com.example.coordinate.Registration.this, "Errore"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }


                    }
                });

            }
        });
        mLoginB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Login.class));

            }
        });








    }
}