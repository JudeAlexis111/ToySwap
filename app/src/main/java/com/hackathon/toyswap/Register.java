package com.hackathon.toyswap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.compat.Place;
import com.google.android.libraries.places.compat.ui.PlaceAutocompleteFragment;
import com.google.android.libraries.places.compat.ui.PlaceSelectionListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hackathon.toyswap.databinding.ActivityRegisterBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    EditText name, email, password, confirmPassword;
    FrameLayout register;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    LatLng locationCoordniate;
    CharSequence locationName;

    String userId;

    private ActivityRegisterBinding mBinding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        name = findViewById(R.id.curvedName);
        email = findViewById(R.id.curvedUsername);
        password = findViewById(R.id.curvedPassword);
        confirmPassword = findViewById(R.id.curvedConfirm);

        register = findViewById(R.id.registerBtn);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                locationCoordniate = place.getLatLng();
                locationName = place.getName();
            }

            @Override
            public void onError(Status status) {

            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String mEmail = email.getText().toString().trim();
                final String mName = name.getText().toString();
                String mPassword = password.getText().toString().trim();
                String mConfirm = confirmPassword.getText().toString().trim();

                if(TextUtils.isEmpty(mEmail)){
                    email.setError("Email is Required");
                    return;
                }

                if(TextUtils.isEmpty(mPassword)){
                    password.setError("Password is Required");
                    return;
                }

                if(mPassword.contentEquals(mConfirm) == false){
                    password.setError("Password must match");
                    return;
                }

                if(locationCoordniate == null || locationName == null){
                    Toast.makeText(Register.this,"Missing Address",Toast.LENGTH_LONG).show();
                    return;
                }



                fAuth.createUserWithEmailAndPassword(mEmail, mPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            userId = fAuth.getCurrentUser().getUid();

                            DocumentReference documentReference = fStore.collection("users").document(userId);
                            ArrayList list = new ArrayList<Long>();

                            list.add(locationCoordniate.latitude);
                            list.add(locationCoordniate.longitude);

                            Map<String,Object> user = new HashMap<>();
                            user.put("fullName", mName);
                            user.put("email", mEmail);
                            user.put("addressName", locationName);
                            user.put("addressCoordinates", list);

                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("Login","Profile created successfully");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("Login",e.toString());
                                }
                            });

                            startActivity(new Intent(getApplicationContext(), Listings.class));
                            finish();
                        }

                        else{
                            Toast.makeText(Register.this, "Error !" + task.getException().getMessage(), Toast.LENGTH_LONG);
                        }

                    }
                });
            }
        });

    }


}
