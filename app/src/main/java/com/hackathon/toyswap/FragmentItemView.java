package com.hackathon.toyswap;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
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
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import static java.lang.Float.parseFloat;
import static java.lang.Math.round;

public class FragmentItemView extends Fragment implements OnMapReadyCallback {

    private static final String TAG = "Fragment1";
    private GoogleMap mMap;
    private MapView mMapView;
    private LinearLayout linearLayout;
    public View mView;
    LatLng pos1;
    LatLng pos2;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_itemview, container, false);

        linearLayout = (LinearLayout) view.findViewById(R.id.myItems);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();

        final StorageReference mStorageRef = FirebaseStorage.getInstance().getReference("media");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                linearLayout.removeAllViews();
                //linearLayoutLeft.removeAllViews();


                for(final DataSnapshot ds : dataSnapshot.getChildren()) {
                    final String Pname = ds.child("name").getValue(String.class);
                    final String itemOwner = ds.child("user").getValue(String.class);
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
                    layout2.setLayoutParams(new LinearLayout.LayoutParams(300, ViewGroup.LayoutParams.WRAP_CONTENT));
                    layout2.setPadding(2,5,5,25);
                    layout2.setBackgroundResource(R.drawable.listing_cards);
                    layout2.setOrientation(LinearLayout.VERTICAL);

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    linearLayout.setPadding(25,0,0,0);
                    linearLayout.setOrientation(LinearLayout.VERTICAL);
                    //linearLayoutLeft.setOrientation(LinearLayout.VERTICAL);

                    final ImageView iv = new ImageView(getActivity());
                    iv.setLayoutParams(new ViewGroup.LayoutParams(290,290));
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

                    FirebaseFirestore fStore = FirebaseFirestore.getInstance();

                    final DocumentReference documeReference = fStore.collection("users").document(ds.child("user").getValue(String.class));
                    documeReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                DocumentSnapshot document = task.getResult();
                                List<Double> group = (List<Double>) document.get("addressCoordinates");
                                Log.e("thing", String.valueOf(group.get(0).getClass()));
                                pos2 = new LatLng(group.get(0), group.get(1));
                            }
                        }
                    });

                    TextView textView = new TextView(getActivity());

                    textView.setTextSize(45);

                    textView.setText(Pname);
                    textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    textView.setGravity(Gravity.CENTER_HORIZONTAL);
                    textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    textView.setTextColor(Color.WHITE);

                    //layout2.addView(textView);
                    //layout2.addView(text);
                    layout2.addView(iv);
                    layout2.setClickable(true);

                    FirebaseAuth fAuth = FirebaseAuth.getInstance();

                    LinearLayout layout3 = new LinearLayout(getActivity());
                    layout3.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                    layout3.setGravity(Gravity.CENTER_HORIZONTAL);
                    layout3.setOrientation(LinearLayout.HORIZONTAL);

                    LinearLayout layout4 = new LinearLayout(getActivity());
                    layout4.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    layout4.setOrientation(LinearLayout.VERTICAL);

                    View space = new View(getActivity());
                    space.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 30));

                    View spaceThree = new View(getActivity());
                    space.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 30));

                    Space spaceTwo = new Space(getActivity());
                    spaceTwo.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 70));
                    Space spaceFour = new Space(getActivity());
                    spaceFour.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 70));

                    TextView text = new TextView(getActivity());
                    text.setText("Description: " + desc);
                    text.setTextSize(15);
                    text.setPadding(15,0,0,0);
                    text.setLayoutParams(params);

                    TextView textTags = new TextView(getActivity());
                    text.setText("Tags: " + taggs);
                    text.setTextSize(15);
                    text.setPadding(15,0,0,0);
                    text.setLayoutParams(params);

                    layout4.addView(text);
                    layout4.addView(textTags);

                    //layout3.addView(spaceThree);
                    layout3.addView(layout2);
                    //layout3.addView(space);
                    //layout3.addView(layout4);

                   // linearLayout.addView(layout3);
                    linearLayout.addView(textView);
                    linearLayout.addView(spaceTwo);
                    if (ds.hasChild("requests")) {

                        Log.e("req", "Alive");

                        int index = 0;
                        for (final DataSnapshot dds : ds.child("requests").getChildren()) {
                            final String Pdname = dds.child("name").getValue(String.class);
                            final String Pidds = dds.getKey();
                            final String tagggs = dds.child("tags").getValue(String.class);
                            final String descs = dds.child("description").getValue(String.class);
                            Log.e("value", Pdname);

                            LinearLayout layout23 = new LinearLayout(getActivity());
                            layout23.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            layout23.setPadding(2, 5, 5, 25);
                            layout23.setBackgroundResource(R.drawable.listing_cards);
                            layout23.setOrientation(LinearLayout.VERTICAL);

                            linearLayout.setPadding(25, 0, 0, 0);
                            linearLayout.setOrientation(LinearLayout.VERTICAL);
                            //linearLayoutLeft.setOrientation(LinearLayout.VERTICAL);

                            final ImageView ivd = new ImageView(getActivity());
                            ivd.setLayoutParams(new ViewGroup.LayoutParams(490, 490));
                            ivd.setPadding(0, 0, 0, 25);

                            final StorageReference dateReff = FirebaseStorage.getInstance().getReference("media/" + dds.child("media").getValue(String.class));
                            dateReff.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Picasso.with(getActivity()).load(uri).into(ivd);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    Log.e("msg", "hello");
                                }
                            });

                            Log.e("msg", "bellow");
                            //String imageUri = "https://i.imgur.com/tGbaZCY.jpg";


                            TextView textView3 = new TextView(getActivity());

                            if (Pdname.length() <= 6) {
                                textView3.setTextSize(35);
                            } else {
                                textView3.setTextSize(round(( Pdname.length() * -2.2 ) + 53));
                            }

                            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                            textView3.setText(Pdname);
                            textView3.setTextColor(Color.WHITE);
                            textView3.setLayoutParams(param);

                            TextView text2 = new TextView(getActivity());
                            text2.setText(desc);
                            text2.setTextSize(15);
                            text2.setPadding(45, 0, 0, 0);
                            text2.setLayoutParams(param);

                            layout23.addView(textView3);
                            //layout2.addView(text);
                            layout23.addView(ivd);
                            layout23.setClickable(true);

                            final DocumentReference documenReference = fStore.collection("users").document(dds.child("user").getValue(String.class));
                            documenReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        List<Double> group = (List<Double>) document.get("addressCoordinates");

                                        pos1 = new LatLng(group.get(0), group.get(1));
                                    }
                                }
                            });

                            layout23.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Log.e("Hello", "Hi");

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

                                    final DocumentReference documentReference = fStore.collection("users").document(dds.child("user").getValue(String.class));
                                    documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot document = task.getResult();
                                                names.setText("By " + document.getString("fullName"));
                                            }
                                        }
                                    });

                                    final ImageView picture = mView.findViewById(R.id.pictures);

                                    final AlertDialog dialog = mBuilder.create();

                                    trade.setText("Accept");
                                    cancel.setText("Cancel");

                                    String tasg = dds.child("tags").getValue(String.class);
                                    String Fname = dds.child("name").getValue(String.class);
                                    String desk = dds.child("description").getValue(String.class);

                                    title.setText(Fname);
                                    description.setText(desk);
                                    tagss.setText("Tags: " + tasg);

                                    StorageReference dateRef = FirebaseStorage.getInstance().getReference("media/" + dds.child("media").getValue(String.class));
                                    dateRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Picasso.with(getActivity()).load(uri).into(picture);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                            Log.e("msg", "hello");
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

                            if ((ds.hasChild("status") == false || ds.child("status").getValue(String.class).equals(Pidds))) {

                                if (index % 2 == 0) {

                                    LinearLayout layout39 = new LinearLayout(getActivity());
                                    layout39.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    layout39.setId(index);
                                    layout39.setOrientation(LinearLayout.HORIZONTAL);

                                    Button accept = new Button(getActivity());
                                    accept.setLayoutParams(new LinearLayout.LayoutParams(200, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    accept.setBackgroundResource(R.drawable.sign_in_btn);
                                    accept.setTextColor(Color.WHITE);
                                    accept.setTextSize(11);
                                    accept.setText("✓");

                                    accept.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            Log.e("thing", "sound");


                                            AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());

                                            if (mView != null) {
                                                ViewGroup parent = (ViewGroup) mView.getParent();
                                                if (parent != null) {
                                                    parent.removeView(mView);
                                                }
                                            }
                                            try {
                                                mView = getLayoutInflater().inflate(R.layout.accept_popup, null);
                                            } catch (InflateException e) {
                                                //e.printStackTrace();
                                            }


                                            mBuilder.setView(mView);

                                            Button except = mView.findViewById(R.id.accepts);
                                            Button decline = mView.findViewById(R.id.decline);
                                            Spinner spinner = mView.findViewById(R.id.spinner);

                                            final AlertDialog dialog = mBuilder.create();

                                            mMapView = mView.findViewById(R.id.map);
                                            Bundle mapViewBundle = null;

                                            if (savedInstanceState != null) {
                                                mapViewBundle = savedInstanceState.getBundle("AIzaSyBgrtrEe8fveSLFTHYg5hphVlqQ0yqnJcs");
                                            }

                                            mMapView.onCreate(mapViewBundle);
                                            mMapView.getMapAsync(FragmentItemView.this);

                                            except.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                                    DatabaseReference myRef = database.getReference(Pid + "/status");
                                                    myRef.setValue(Pidds);
                                                    dialog.dismiss();
                                                }
                                            });

                                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                                            dialog.show();


                                            //MapsInitializer.initialize(getActivity());


                                        }
                                    });

                                    Space wideSpace = new Space(getActivity());
                                    wideSpace.setLayoutParams(new LinearLayout.LayoutParams(20, ViewGroup.LayoutParams.MATCH_PARENT));

                                    Button decline = new Button(getActivity());
                                    decline.setLayoutParams(new LinearLayout.LayoutParams(200, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    decline.setBackgroundResource(R.drawable.sign_in_btn);
                                    decline.setTextColor(Color.WHITE);
                                    decline.setTextSize(11);
                                    decline.setText("X");

                                    final LinearLayout buttonHolder = new LinearLayout(getActivity());
                                    buttonHolder.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    buttonHolder.setOrientation(LinearLayout.HORIZONTAL);

                                    layout23.setGravity(Gravity.CENTER);

                                    buttonHolder.addView(accept);
                                    buttonHolder.addView(wideSpace);
                                    buttonHolder.addView(decline);

                                    layout23.addView(buttonHolder);

                                    Space space7 = new Space(getActivity());
                                    space7.setLayoutParams(new LinearLayout.LayoutParams(30, ViewGroup.LayoutParams.MATCH_PARENT));

                                    layout39.addView(layout23);
                                    layout39.addView(space7);

                                    linearLayout.addView(layout39);
                                } else {

                                    Button accept = new Button(getActivity());
                                    accept.setLayoutParams(new LinearLayout.LayoutParams(200, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    accept.setBackgroundResource(R.drawable.sign_in_btn);
                                    accept.setTextColor(Color.WHITE);
                                    accept.setTextSize(11);
                                    accept.setText("✓");

                                    accept.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            Log.e("thing", "sound");


                                            AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());

                                            if (mView != null) {
                                                ViewGroup parent = (ViewGroup) mView.getParent();
                                                if (parent != null) {
                                                    parent.removeView(mView);
                                                }
                                            }
                                            try {
                                                mView = getLayoutInflater().inflate(R.layout.accept_popup, null);
                                            } catch (InflateException e) {
                                                //e.printStackTrace();
                                            }


                                            mBuilder.setView(mView);

                                            Button except = mView.findViewById(R.id.accepts);
                                            Button decline = mView.findViewById(R.id.decline);
                                            Spinner spinner = mView.findViewById(R.id.spinner);

                                            final AlertDialog dialog = mBuilder.create();

                                            mMapView = mView.findViewById(R.id.map);
                                            Bundle mapViewBundle = null;

                                            if (savedInstanceState != null) {
                                                mapViewBundle = savedInstanceState.getBundle("AIzaSyBgrtrEe8fveSLFTHYg5hphVlqQ0yqnJcs");
                                            }

                                            mMapView.onCreate(mapViewBundle);
                                            mMapView.getMapAsync(FragmentItemView.this);

                                            except.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                                    DatabaseReference myRef = database.getReference(Pid + "/status");
                                                    myRef.setValue(Pidds);
                                                    dialog.dismiss();
                                                }
                                            });

                                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                                            dialog.show();


                                            //MapsInitializer.initialize(getActivity());


                                        }
                                    });

                                    Space wideSpace = new Space(getActivity());
                                    wideSpace.setLayoutParams(new LinearLayout.LayoutParams(20, ViewGroup.LayoutParams.MATCH_PARENT));

                                    Button decline = new Button(getActivity());
                                    decline.setLayoutParams(new LinearLayout.LayoutParams(200, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    decline.setBackgroundResource(R.drawable.sign_in_btn);
                                    decline.setTextColor(Color.WHITE);
                                    decline.setTextSize(11);
                                    decline.setText("X");

                                    LinearLayout buttonHolder = new LinearLayout(getActivity());
                                    buttonHolder.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    buttonHolder.setOrientation(LinearLayout.HORIZONTAL);

                                    layout23.setGravity(Gravity.CENTER);

                                    buttonHolder.addView(accept);
                                    buttonHolder.addView(wideSpace);
                                    buttonHolder.addView(decline);

                                    layout23.addView(buttonHolder);

                                    Space space7 = new Space(getActivity());
                                    space.setLayoutParams(new LinearLayout.LayoutParams(30, ViewGroup.LayoutParams.MATCH_PARENT));

                                    LinearLayout layout39 = (LinearLayout) view.findViewById(index - 1);
                                    layout39.addView(layout23);

                                    linearLayout.addView(space7);
                                }

                                index = index + 1;

                            }

                        }
                    }

                    //else{
                    //    linearLayoutLeft.addView(textView);
                    //    linearLayoutLeft.addView(text);
                    //    linearLayoutLeft.addView(iv);
                    //}

                    //linearLayout.addView(layout2);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMapView.onResume();

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(pos1);
        builder.include(pos2);



        Log.e("value", String.valueOf(pos1));
        Log.e("value", String.valueOf(pos2));

        LatLngBounds bounds = builder.build();

        int padding = 0; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

        googleMap.animateCamera(cu);

        googleMap.addMarker(new MarkerOptions().position(pos1)
                .title("Traders House"));

        googleMap.addMarker(new MarkerOptions().position(pos2)
                .title("Your House"));

        Log.e("hello","world");


    }
}
