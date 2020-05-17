package com.hackathon.toyswap;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.cloud.label.FirebaseVisionCloudLabel;
import com.google.firebase.ml.vision.cloud.label.FirebaseVisionCloudLabelDetector;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.List;

import javax.annotation.Nullable;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import static java.lang.Math.round;

public class FragmentListing extends Fragment {

    private static final String TAG = "Fragment1";

    private static final int PERMISSION_CODE = 1000;
    public static final int REQUEST_CODE = 103;

    private LinearLayout linearLayout;
    public static EditText name;
    public static EditText tags;
    public static Bitmap image;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_listings, container, false);

        linearLayout = (LinearLayout) view.findViewById(R.id.listId);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();

        final StorageReference mStorageRef = FirebaseStorage.getInstance().getReference("media");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                linearLayout.removeAllViews();
                //linearLayoutLeft.removeAllViews();

                int index = 0;
                for(final DataSnapshot ds : dataSnapshot.getChildren()) {
                    final String Pname = ds.child("name").getValue(String.class);
                    final String Pid = ds.getKey();
                    final String taggs = ds.child("tags").getValue(String.class);
                    final String desc = ds.child("description").getValue(String.class);

                    try {
                        Log.e("value",Pname);
                    }
                    catch(Exception e) {
                        return;
                    }


                    LinearLayout layout2 = new LinearLayout(getActivity());
                    layout2.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    layout2.setPadding(2,5,5,25);
                    layout2.setBackgroundResource(R.drawable.listing_cards);
                    layout2.setOrientation(LinearLayout.VERTICAL);

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    linearLayout.setPadding(25,0,0,0);
                    linearLayout.setOrientation(LinearLayout.VERTICAL);
                    //linearLayoutLeft.setOrientation(LinearLayout.VERTICAL);

                    final ImageView iv = new ImageView(getActivity());
                    iv.setLayoutParams(new ViewGroup.LayoutParams(490,490));
                    iv.setPadding(0,0,0,25);

                    final StorageReference dateRef = FirebaseStorage.getInstance().getReference("media/" + ds.child("media").getValue(String.class));
                    dateRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.with(getActivity()).load(uri).into(iv);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Log.e("msg","hello");
                        }
                    });

                    Log.e("msg","bellow");
                    //String imageUri = "https://i.imgur.com/tGbaZCY.jpg";


                    TextView textView = new TextView(getActivity());

                    if(Pname.length() <= 6) {
                        textView.setTextSize(35);
                    }
                    else{
                        textView.setTextSize(round((Pname.length() * -2.2)  + 53));
                    }

                    textView.setText(Pname);
                    textView.setTextColor(Color.WHITE);
                    textView.setPadding(45,0,0,0);
                    textView.setLayoutParams(params);

                    TextView text = new TextView(getActivity());
                    text.setText(desc);
                    text.setTextSize(15);
                    text.setPadding(45,0,0,0);
                    text.setLayoutParams(params);

                    layout2.addView(textView);
                    //layout2.addView(text);
                    layout2.addView(iv);
                    layout2.setClickable(true);

                    layout2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.e("Hello","Hi");

                            AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
                            View mView = getLayoutInflater().inflate(R.layout.trade_popup, null);
                            mBuilder.setView(mView);

                            FirebaseFirestore fStore = FirebaseFirestore.getInstance();

                            TextView title = mView.findViewById(R.id.textView4);
                            final TextView names = mView.findViewById(R.id.textView34);
                            TextView description = mView.findViewById(R.id.description);
                            TextView tagss = mView.findViewById(R.id.tags);
                            Button trade = mView.findViewById(R.id.trade);
                            Button cancel = mView.findViewById(R.id.cancelM);

                            final DocumentReference documentReference = fStore.collection("users").document(ds.child("user").getValue(String.class));
                            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()){
                                        DocumentSnapshot document = task.getResult();
                                        names.setText("By " + document.getString("fullName"));
                                    }
                                }
                            });


                            final ImageView picture = mView.findViewById(R.id.pictures);

                            final AlertDialog dialog = mBuilder.create();


                            title.setText(Pname);
                            description.setText(desc);
                            tagss.setText("Tags: " + taggs);

                            StorageReference dateRef = FirebaseStorage.getInstance().getReference("media/" + ds.child("media").getValue(String.class));
                            dateRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Picasso.with(getActivity()).load(uri).into(picture);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    Log.e("msg","hello");
                                }
                            });

                            trade.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Log.e("Hello","Hi");

                                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
                                    View mView = getLayoutInflater().inflate(R.layout.new_item_popup, null);

                                    mBuilder.setView(mView);
                                    final AlertDialog bialog = mBuilder.create();

                                    //dialog.dismiss();

                                    Button submit = mView.findViewById(R.id.button2);
                                    Button cancel = mView.findViewById(R.id.button3);
                                    ImageButton takePhoto = mView.findViewById(R.id.imageButton);

                                    name = mView.findViewById(R.id.editText6);
                                    tags = mView.findViewById(R.id.editText54);

                                    TextView purpose = mView.findViewById(R.id.textView3);
                                    final EditText description = mView.findViewById(R.id.editText4);

                                    purpose.setText("Trade Item");
                                    submit.setText("Trade");

                                    submit.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            final FirebaseDatabase database = FirebaseDatabase.getInstance();

                                            if(image == null){
                                                Toast.makeText(getContext(),"Include a Picture", Toast.LENGTH_SHORT).show();
                                                return;
                                            }

                                            if(TextUtils.isEmpty(name.getText())){
                                                name.setError("Name is required");
                                                return;
                                            }

                                            if(TextUtils.isEmpty(tags.getText())){
                                                tags.setError("Tags are required");
                                                return;
                                            }

                                            if(TextUtils.isEmpty(description.getText())){
                                                description.setError("Description is required");
                                                return;
                                            }

                                            if(name.getText().length() > 15){
                                                name.setError("Must be less than 16 Chars.");
                                                return;
                                            }

                                            if(image != null){

                                                DatabaseReference myKef = database.getReference();

                                                final String key = myKef.push().getKey();
                                                final String uniqueTradeId = myKef.push().getKey();
                                                FirebaseAuth fAuth = FirebaseAuth.getInstance();
                                                final String userId = fAuth.getCurrentUser().getUid();

                                                StorageReference mStorageRef = FirebaseStorage.getInstance().getReference("media/" + key);

                                                Bitmap bitmap = image;

                                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                                byte[] data = baos.toByteArray();

                                                UploadTask uploadTask = mStorageRef.putBytes(data);
                                                uploadTask.addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception exception) {
                                                        // Handle unsuccessful uploads
                                                    }
                                                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                    @Override
                                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                                                        // ...

                                                        DatabaseReference myRef = database.getReference();

                                                        myRef = database.getReference(Pid + "/requests/" + uniqueTradeId + "/user");
                                                        myRef.setValue(userId);

                                                        myRef = database.getReference(Pid + "/requests/" + uniqueTradeId + "/description");
                                                        myRef.setValue(description.getText().toString());

                                                        myRef = database.getReference(Pid + "/requests/" + uniqueTradeId + "/tags");
                                                        myRef.setValue(tags.getText().toString());

                                                        myRef = database.getReference(Pid + "/requests/" + uniqueTradeId + "/media");
                                                        myRef.setValue(key);

                                                        myRef = database.getReference(Pid + "/requests/" + uniqueTradeId + "/name");
                                                        myRef.setValue(name.getText().toString());

                                                    }
                                                });

                                            }

                                            bialog.dismiss();
                                            dialog.dismiss();


                                        }
                                    });

                                    cancel.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            bialog.dismiss();
                                        }
                                    });

                                    takePhoto.setOnClickListener(new View.OnClickListener() {
                                        @RequiresApi(api = Build.VERSION_CODES.M)
                                        @Override
                                        public void onClick(View v) {
                                            openCamera(REQUEST_CODE);
                                        }
                                    });

                                    bialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                                    bialog.show();
                                }
                            });

                            cancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });

                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                            dialog.show();


                        }
                    });



                    if (index % 2 == 0){

                        LinearLayout layout3 = new LinearLayout(getActivity());
                        layout3.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        layout3.setId(index);
                        layout3.setOrientation(LinearLayout.HORIZONTAL);

                        Space space = new Space(getActivity());
                        space.setLayoutParams(new LinearLayout.LayoutParams(30, ViewGroup.LayoutParams.MATCH_PARENT));

                        layout3.addView(layout2);
                        layout3.addView(space);

                        linearLayout.addView(layout3);
                    }

                    else{
                        Space space = new Space(getActivity());
                        space.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 30));

                        LinearLayout layout3 = (LinearLayout) view.findViewById(index-1);
                        layout3.addView(layout2);

                        linearLayout.addView(space);
                    }

                    //else{
                    //    linearLayoutLeft.addView(textView);
                    //    linearLayoutLeft.addView(text);
                    //    linearLayoutLeft.addView(iv);
                    //}

                    index = index + 1;

                    //linearLayout.addView(layout2);


                }

            }

            public void openCamera(Integer requestCode) {
                Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(camera, requestCode);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });



        return view;
    }
}
