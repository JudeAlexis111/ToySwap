package com.hackathon.toyswap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.asynclayoutinflater.view.AsyncLayoutInflater;
import androidx.databinding.DataBindingUtil;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import com.google.firebase.ml.vision.label.FirebaseVisionLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionLabelDetector;
import com.google.firebase.ml.vision.label.FirebaseVisionLabelDetectorOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hackathon.toyswap.databinding.ActivityMainBinding;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.util.List;

import static java.lang.Math.round;

public class Listings extends AppCompatActivity {

    private static final int PERMISSION_CODE = 1000;
    public static final int REQUEST_CODE = 102;
    public Bitmap image;
    //private View Mview;
    public LinearLayout linearLayout;
    public LinearLayout linearLayoutLeft;

    public ImageView imageView;
    public Context mContext;
    public EditText name;
    public EditText tags;
    public TextView titles;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;

    private SectionsStatePagerAdapter mSectionsStatePagerAdapter;
    private ViewPager mViewPager;

    public Listings() {
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listings);

        nextAction(this);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();

        final StorageReference mStorageRef = FirebaseStorage.getInstance().getReference("media");

        mSectionsStatePagerAdapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.tabLayout);
        titles = (TextView) findViewById(R.id.textView5);

        setupViewPager(mViewPager);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position == 0){
                    titles.setText("Listing");
                }
                if(position == 1){
                    titles.setText("My Items");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        Log.e("init", String.valueOf(mViewPager.getCurrentItem()));

        linearLayout = findViewById(R.id.listId);
        imageView = findViewById(R.id.imageView3);
        //linearLayoutLeft = findViewById(R.id.listIdg);
        mContext = this;

        /*

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                linearLayout.removeAllViews();
                //linearLayoutLeft.removeAllViews();

                int index = 0;
                for(final DataSnapshot ds : dataSnapshot.getChildren()) {
                    final String Pname = ds.getKey();
                    final String desc = ds.child("description").getValue(String.class);
                    Log.e("value",Pname);

                    LinearLayout layout2 = new LinearLayout(mContext);
                    layout2.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    layout2.setPadding(2,5,5,25);
                    layout2.setBackgroundResource(R.drawable.listing_cards);
                    layout2.setOrientation(LinearLayout.VERTICAL);

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    linearLayout.setPadding(25,0,0,0);
                    linearLayout.setOrientation(LinearLayout.VERTICAL);
                    //linearLayoutLeft.setOrientation(LinearLayout.VERTICAL);

                    final ImageView iv = new ImageView(mContext);
                    iv.setLayoutParams(new ViewGroup.LayoutParams(490,490));
                    iv.setPadding(0,0,0,25);

                    final StorageReference dateRef = FirebaseStorage.getInstance().getReference("media/" + ds.child("media").getValue(String.class));
                    dateRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.with(mContext).load(uri).into(iv);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Log.e("msg","hello");
                        }
                    });

                    Log.e("msg","bellow");
                    //String imageUri = "https://i.imgur.com/tGbaZCY.jpg";


                    TextView textView = new TextView(mContext);

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

                    TextView text = new TextView(mContext);
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

                            AlertDialog.Builder mBuilder = new AlertDialog.Builder(Listings.this);
                            View mView = getLayoutInflater().inflate(R.layout.trade_popup, null);
                            mBuilder.setView(mView);

                            TextView title = mView.findViewById(R.id.textView4);
                            TextView description = mView.findViewById(R.id.description);
                            Button trade = mView.findViewById(R.id.trade);
                            Button cancel = mView.findViewById(R.id.cancelM);

                            final ImageView picture = mView.findViewById(R.id.pictures);

                            final AlertDialog dialog = mBuilder.create();

                            title.setText(Pname);
                            description.setText(desc);

                            StorageReference dateRef = FirebaseStorage.getInstance().getReference("media/" + ds.child("media").getValue(String.class));
                            dateRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Picasso.with(mContext).load(uri).into(picture);
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

                                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(Listings.this);
                                    View bView = getLayoutInflater().inflate(R.layout.new_item_popup, null);

                                    mBuilder.setView(bView);
                                    final AlertDialog bialog = mBuilder.create();

                                    Button submit = bView.findViewById(R.id.button2);
                                    Button cancel = bView.findViewById(R.id.button3);
                                    ImageButton takePhoto = bView.findViewById(R.id.imageButton);

                                    name = bView.findViewById(R.id.editText6);
                                    tags = bView.findViewById(R.id.editText54);
                                    final EditText description = bView.findViewById(R.id.editText4);

                                    submit.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            final FirebaseDatabase database = FirebaseDatabase.getInstance();

                                            if(image == null){
                                                Toast.makeText(getApplicationContext(),"Include a Picture", Toast.LENGTH_SHORT).show();
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

                                                        myRef = database.getReference(name.getText().toString() + "/description");

                                                        myRef.setValue(description.getText().toString());

                                                        myRef = database.getReference(name.getText().toString() + "/media");
                                                        myRef.setValue(key);

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
                                            dialog.dismiss();
                                        }
                                    });

                                    takePhoto.setOnClickListener(new View.OnClickListener() {
                                        @RequiresApi(api = Build.VERSION_CODES.M)
                                        @Override
                                        public void onClick(View v) {
                                            if(checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){
                                                String[] permission = {Manifest.permission.CAMERA};

                                                requestPermissions(permission, PERMISSION_CODE);
                                            }

                                            else{
                                                openCamera();

                                                //name.setText("thing");

                                            }
                                        }
                                    });

                                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


                                    bialog.show();

                                    //dialog.dismiss();
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

                        LinearLayout layout3 = new LinearLayout(mContext);
                        layout3.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        layout3.setId(index);
                        layout3.setOrientation(LinearLayout.HORIZONTAL);

                        Space space = new Space(mContext);
                        space.setLayoutParams(new LinearLayout.LayoutParams(30, ViewGroup.LayoutParams.MATCH_PARENT));

                        layout3.addView(layout2);
                        layout3.addView(space);

                        linearLayout.addView(layout3);
                    }

                    else{
                        Space space = new Space(mContext);
                        space.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 30));

                        LinearLayout layout3 = findViewById(index-1);
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

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        }); */

    }

    public void setupViewPager (ViewPager viewPager){
        SectionsStatePagerAdapter adapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new FragmentListing(), "Listing");
        adapter.addFragment(new FragmentItemView(), "Your Items");
        viewPager.setAdapter(adapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void revealButton(Context context){

        final View view = ((View) ((Activity)context).findViewById(R.id.crumpleView));

        view.setVisibility(View.VISIBLE);

        int x =view.getWidth();
        int y = view.getHeight();

        int startX = (int) (getFinalWidth() / 2 + 456);
        int startY = (int) (getFinalWidth() / 2 + 1608);

        float radius = Math.max(x,y) * 1.2f;

        Animator reveal = ViewAnimationUtils.createCircularReveal(view, startX, startY, radius, 0);
        reveal.setDuration(350);

        reveal.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                view.setVisibility(View.INVISIBLE);

                //finish();
            }
        });

        reveal.start();
    }

    public void LaunchSettings(View view){
        Log.e("Hello","Hi");

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(Listings.this);
        View mView = getLayoutInflater().inflate(R.layout.user_info_options, null);

        FrameLayout cancel = mView.findViewById(R.id.signOutBtn);
        FrameLayout signOut = mView.findViewById(R.id.CancelButton);
        final TextView textView = mView.findViewById(R.id.textView4);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userId = fAuth.getCurrentUser().getUid();

        mBuilder.setView(mView);

        final AlertDialog dialog = mBuilder.create();


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
                dialog.dismiss();
            }
        });

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        final DocumentReference documentReference = fStore.collection("users").document(userId);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    textView.setText("Hello, " + document.getString("fullName"));
                }
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();
    }

    public void AddItem(View view){

        Log.e("Hello","Hi");

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(Listings.this);
        View mView = getLayoutInflater().inflate(R.layout.new_item_popup, null);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();

        //dialog.dismiss();

        Button submit = mView.findViewById(R.id.button2);
        Button cancel = mView.findViewById(R.id.button3);
        ImageButton takePhoto = mView.findViewById(R.id.imageButton);

        name = mView.findViewById(R.id.editText6);
        tags = mView.findViewById(R.id.editText54);
        final EditText description = mView.findViewById(R.id.editText4);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FirebaseDatabase database = FirebaseDatabase.getInstance();

                if(image == null){
                    Toast.makeText(getApplicationContext(),"Include a Picture", Toast.LENGTH_SHORT).show();
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
                    final String uniqueName = myKef.push().getKey();

                    fAuth = FirebaseAuth.getInstance();
                    userId = fAuth.getCurrentUser().getUid();

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

                            myRef = database.getReference(uniqueName + "/user");
                            myRef.setValue(userId);

                            myRef = database.getReference(uniqueName + "/description");
                            myRef.setValue(description.getText().toString());

                            myRef = database.getReference(uniqueName + "/tags");
                            myRef.setValue(tags.getText().toString());

                            myRef = database.getReference(uniqueName + "/media");
                            myRef.setValue(key);

                            myRef = database.getReference(uniqueName + "/name");
                            myRef.setValue(name.getText().toString());

                        }
                    });

                }

                dialog.dismiss();

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        takePhoto.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if(checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){
                    String[] permission = {Manifest.permission.CAMERA};

                    requestPermissions(permission, PERMISSION_CODE);
                }

                else{
                    openCamera(REQUEST_CODE);

                    //name.setText("thing");

                }
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();

    }

    private void nextAction(final Context context){

        new Handler().postDelayed(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void run() {
                revealButton(context);
            }
        }, 500);
    }

    private int getFinalWidth(){
        return (int) getResources().getDimension(R.dimen.get_width);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case PERMISSION_CODE:{
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    openCamera(REQUEST_CODE);
                }

                else{
                    Toast.makeText(this,"Permission denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void openCamera(Integer requestCode) {
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(camera, requestCode);
    }

    public void SetTitle(String TitleText){
        titles.setText(TitleText);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.e("Called", String.valueOf(requestCode));
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
            image = (Bitmap) data.getExtras().get("data");

            Bitmap bitmap = image;

            FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);

            FirebaseVisionCloudLabelDetector detector = FirebaseVision.getInstance()
                    .getVisionCloudLabelDetector();

            Task<List<FirebaseVisionCloudLabel>> result =
                    detector.detectInImage(image)
                            .addOnSuccessListener(
                                    new OnSuccessListener<List<FirebaseVisionCloudLabel>>() {
                                        @Override
                                        public void onSuccess(List<FirebaseVisionCloudLabel> labels) {
                                            // Task completed successfully
                                            // ...
                                            String taggedInfo = "";
                                            for (FirebaseVisionCloudLabel label: labels) {
                                                String text = label.getLabel();
                                                String entityId = label.getEntityId();
                                                float confidence = label.getConfidence();

                                                //Log.e("image", text);
                                                //Log.e("image", String.valueOf(confidence));

                                                taggedInfo = taggedInfo + "#" + text + " ";
                                            }

                                            //View mView = getLayoutInflater().inflate(R.layout.new_item_popup, null);

                                            name.setText(labels.get(0).getLabel());
                                            tags.setText(taggedInfo);

                                            Log.e("image",labels.get(0).getLabel());
                                        }
                                    })
                            .addOnFailureListener(
                                    new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Task failed with an exception
                                            // ...
                                        }
                                    });

        }

        if (requestCode == 65639) {
            FragmentListing.image = (Bitmap) data.getExtras().get("data");

            Bitmap bitmap = FragmentListing.image;

            FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);

            FirebaseVisionCloudLabelDetector detector = FirebaseVision.getInstance()
                    .getVisionCloudLabelDetector();

            Task<List<FirebaseVisionCloudLabel>> result =
                    detector.detectInImage(image)
                            .addOnSuccessListener(
                                    new OnSuccessListener<List<FirebaseVisionCloudLabel>>() {
                                        @Override
                                        public void onSuccess(List<FirebaseVisionCloudLabel> labels) {
                                            // Task completed successfully
                                            // ...
                                            String taggedInfo = "";
                                            for (FirebaseVisionCloudLabel label: labels) {
                                                String text = label.getLabel();
                                                String entityId = label.getEntityId();
                                                float confidence = label.getConfidence();

                                                //Log.e("image", text);
                                                //Log.e("image", String.valueOf(confidence));

                                                taggedInfo = taggedInfo + "#" + text + " ";
                                            }

                                            //View mView = getLayoutInflater().inflate(R.layout.new_item_popup, null);

                                            FragmentListing.name.setText(labels.get(0).getLabel());
                                            FragmentListing.tags.setText(taggedInfo);

                                            Log.e("image",labels.get(0).getLabel());
                                        }
                                    })
                            .addOnFailureListener(
                                    new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Task failed with an exception
                                            // ...
                                        }
                                    });

        }
    }


}
