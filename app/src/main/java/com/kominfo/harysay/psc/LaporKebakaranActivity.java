package com.kominfo.harysay.psc;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.listener.multi.BaseMultiplePermissionsListener;
import com.kominfo.harysay.psc.model.User;
import com.kominfo.harysay.psc.persistence.UserRepository;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

public class LaporKebakaranActivity extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap map;
    EditText mlongitudekebakaran,mlatitudekebakaran;
    private UiSettings mUiSettings;

    public static final int REQUEST_GET_SINGLE_FILE = 101;
    public static final int REQUEST_CAPTURE_IMAGE = 102;

    User mUser;
    byte[] selectedImageByte = null;

    private UserRepository mUserRepo;
    ImageView mLogoKameraKebakaran;
    ImageView mFotoKebakaran;

    Button mBtnLaporKebakaran;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lapor_kebakaran);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragmentKebakaran);
        mlongitudekebakaran = (EditText) findViewById(R.id.longitudeKebakaran);
        mlatitudekebakaran =(EditText) findViewById(R.id.latitudeKebakaran);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarlaporkebakaran);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mapFragment.getMapAsync(this);




        bindView();
        mUserRepo = new UserRepository(this);


        mLogoKameraKebakaran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prepareStorage();
            }
        });

        mFotoKebakaran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prepareStorage();
            }
        });

        mBtnLaporKebakaran=(Button)findViewById(R.id.btnLaporKebakaran);

        mBtnLaporKebakaran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

    }





    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        mUiSettings = map.getUiSettings();
        mUiSettings.setZoomControlsEnabled(true);
//        LatLng WestJava = new LatLng(-6.738758, 106.625219);
//        map.addMarker(new MarkerOptions().position(WestJava).title("WestJava"));
//        map.moveCamera(CameraUpdateFactory.newLatLng(WestJava));
        // Check if we were successful in obtaining the map.
        if (map != null) {


            map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {

                @Override
                public void onMyLocationChange(Location arg0) {
                    // TODO Auto-generated method stub

                    map.addMarker(new MarkerOptions().position(new LatLng(arg0.getLatitude(), arg0.getLongitude())).title("It's Me!"));
                    mlongitudekebakaran.setText(Double.toString(arg0.getLongitude()));
                    mlatitudekebakaran.setText(Double.toString(arg0.getLatitude()));
                }
            });

        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        map.setMyLocationEnabled(true);
//        mlongitudekecelakaan.setText(Double.toString(map.getMyLocation().getLongitude()));
//        mlatitudekecelakaan.setText(Double.toString( map.getMyLocation().getLatitude()));



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_GET_SINGLE_FILE:
                if (resultCode == Activity.RESULT_OK && data != null){
                    Uri uri = data.getData();
                    try{
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                                getContentResolver(), uri
                        );

                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, outputStream);

                        selectedImageByte = outputStream.toByteArray();

                        mFotoKebakaran.setImageBitmap(bitmap);

                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
                break;

            case REQUEST_CAPTURE_IMAGE:
                if (resultCode == Activity.RESULT_OK && data != null){
                    try{
                        Bitmap bitmap = (Bitmap) data.getExtras().get("data");

                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, outputStream);

                        selectedImageByte = outputStream.toByteArray();

                        mFotoKebakaran.setImageBitmap(bitmap);

                    }catch (NullPointerException e){
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    private void prepareStorage(){
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA
                )
                .withListener(new BaseMultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        super.onPermissionsChecked(report);
                        showDialog();
                    }
                }).check();
    }

    private void showDialog(){
        AlertDialog.Builder mDialog = new AlertDialog.Builder(this);
        mDialog.setTitle("Choose Option");
        mDialog.setPositiveButton("Camera", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                openCamera();
            }
        });
        mDialog.setNegativeButton("Gallery", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                openGallery();
            }
        });
        mDialog.create().show();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(
                Intent.createChooser(intent, "Select Picture"),
                REQUEST_GET_SINGLE_FILE
        );
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            startActivityForResult(takePictureIntent, REQUEST_CAPTURE_IMAGE);
        }

    }

    private void bindView() {

        mLogoKameraKebakaran = findViewById(R.id.tambahfotokebakaran);
        mFotoKebakaran=findViewById(R.id.fotoKebakaran);

    }

    private void pesan(String pesan){
        Toast.makeText(this, pesan, Toast.LENGTH_SHORT).show();
    }
}
