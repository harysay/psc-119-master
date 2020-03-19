package com.kominfo.harysay.psc;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
//import android.location.LocationListener;
import com.google.android.gms.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.listener.multi.BaseMultiplePermissionsListener;
import com.kominfo.harysay.psc.model.User;
import com.kominfo.harysay.psc.persistence.UserRepository;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class LaporBencanaAlamActivity extends AppCompatActivity implements OnMapReadyCallback,
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleMap.OnMarkerDragListener,
        GoogleMap.OnMapLongClickListener,
        GoogleApiClient.OnConnectionFailedListener {

    GoogleMap map;
    EditText mlongitudebencanaalam,mlatitudebencanaalam;
    TextView tvTime;
    private UiSettings mUiSettings;

    final String TAG = "GPS";
    private long UPDATE_INTERVAL = 2 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */
    static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    GoogleApiClient gac;
    LocationRequest locationRequest;
    public static final int REQUEST_GET_SINGLE_FILE = 101;
    public static final int REQUEST_CAPTURE_IMAGE = 102;

    User mUser;
    byte[] selectedImageByte = null;

    private UserRepository mUserRepo;
    ImageView mLogoKameraBencanaAlam;
    ImageView mFotoBencanaAlam;
    private GoogleMap mMap;
    private double longitude;
    private double latitude;
    Button mBtnLaporBencanaAlam;
    LocationManager locationManager;
    LocationListener locationListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lapor_bencana_alam);
        bindView();
        isGooglePlayServicesAvailable();

        if(!isLocationEnabled())
            showAlert();

        locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        gac = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mUserRepo = new UserRepository(this);

        mLogoKameraBencanaAlam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prepareStorage();
            }
        });

        mFotoBencanaAlam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prepareStorage();
            }
        });

        mBtnLaporBencanaAlam=(Button)findViewById(R.id.btnLaporBencanaAlam);

        mBtnLaporBencanaAlam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

    }





    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        Intent intent = getIntent();
        if (intent.getIntExtra("Place Number",0) == 0 ){

            // Zoom into users location
            locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    centreMapOnLocation(location,"Your Location");
                }
            };

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0, (android.location.LocationListener) locationListener);
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                centreMapOnLocation(lastKnownLocation,"Your Location");
            } else {

                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            }
        }



//        map = googleMap;
//        mUiSettings = map.getUiSettings();
//        mUiSettings.setZoomControlsEnabled(true);
////        LatLng WestJava = new LatLng(-6.738758, 106.625219);
////        map.addMarker(new MarkerOptions().position(WestJava).title("WestJava"));
////        map.moveCamera(CameraUpdateFactory.newLatLng(WestJava));
//        // Check if we were successful in obtaining the map.
//        if (map != null) {
//            map.addMarker(new MarkerOptions().position(new LatLng(arg0.getLatitude(), arg0.getLongitude())).title("It's Me!"));
//            mlongitudebencanaalam.setText(Double.toString(arg0.getLongitude()));
//            mlatitudebencanaalam.setText(Double.toString(arg0.getLatitude()));
//
////            map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
////
////                @Override
////                public void onMyLocationChange(Location arg0) {
////                    // TODO Auto-generated method stub
////
////                    map.addMarker(new MarkerOptions().position(new LatLng(arg0.getLatitude(), arg0.getLongitude())).title("It's Me!"));
////                    mlongitudebencanaalam.setText(Double.toString(arg0.getLongitude()));
////                    mlatitudebencanaalam.setText(Double.toString(arg0.getLatitude()));
////                }
////            });
//
//        }
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        map.setMyLocationEnabled(true);
////        mlongitudekecelakaan.setText(Double.toString(map.getMyLocation().getLongitude()));
////        mlatitudekecelakaan.setText(Double.toString( map.getMyLocation().getLatitude()));
//


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

                        mFotoBencanaAlam.setImageBitmap(bitmap);

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

                        mFotoBencanaAlam.setImageBitmap(bitmap);

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

    public void centreMapOnLocation(Location location, String title){

        LatLng userLocation = new LatLng(location.getLatitude(),location.getLongitude());
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(userLocation).title(title));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,12));

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

        mlongitudebencanaalam = (EditText) findViewById(R.id.longitudeBencanaAlam);
        mlatitudebencanaalam =(EditText) findViewById(R.id.latitudeBencanaAlam);
        tvTime = (TextView) findViewById(R.id.tvTime);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarlaporbencanaalam);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mLogoKameraBencanaAlam = findViewById(R.id.tambahfotobencanaalam);
        mFotoBencanaAlam=findViewById(R.id.fotoBencanaAlam);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragmentKebakaran);
        mapFragment.getMapAsync(this);
    }

    private void pesan(String pesan){
        Toast.makeText(this, pesan, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            updateUI(location);
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(LaporBencanaAlamActivity.this, "Permission was granted!", Toast.LENGTH_LONG).show();

                    try{
                        LocationServices.FusedLocationApi.requestLocationUpdates(
                                gac, locationRequest, this);
                    } catch (SecurityException e) {
                        Toast.makeText(LaporBencanaAlamActivity.this, "SecurityException:\n" + e.toString(),
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(LaporBencanaAlamActivity.this, "Permission denied!", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(LaporBencanaAlamActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

            return;
        }
        Log.d(TAG, "onConnected");
        Location ll = LocationServices.FusedLocationApi.getLastLocation(gac);
        Log.d(TAG, "LastLocation: " + (ll == null ? "NO LastLocation" : ll.toString()));
        LocationServices.FusedLocationApi.requestLocationUpdates(gac, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStart() {
        gac.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        gac.disconnect();
        super.onStop();
    }

    private void moveMap() {
        /**
         * Creating the latlng object to store lat, long coordinates
         * adding marker to map
         * move the camera with animation
         */
        LatLng latLng = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .draggable(true)
                .title("Posisi Anda"));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setOnMarkerDragListener(this);
        mMap.setOnMapLongClickListener(this);
    }

    private void updateUI(Location loc) {
        Log.d(TAG, "updateUI");
        mlongitudebencanaalam.setText(Double.toString(loc.getLongitude()));
        mlatitudebencanaalam.setText(Double.toString(loc.getLatitude()));
        tvTime.setText(DateFormat.getTimeInstance().format(loc.getTime()));

    }

    private boolean isLocationEnabled() {
        LocationManager locationManager =
                (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
    private boolean isGooglePlayServicesAvailable() {
        final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.d(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        Log.d(TAG, "This device is supported.");
        return true;
    }

    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                        "use this app")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                    }
                });
        dialog.show();
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        mMap.addMarker(new MarkerOptions().position(latLng).draggable(true));
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        Toast.makeText(LaporBencanaAlamActivity.this, "onMarkerDragStart", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        Toast.makeText(LaporBencanaAlamActivity.this, "onMarkerDrag", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        // getting the Co-ordinates
        latitude = marker.getPosition().latitude;
        longitude = marker.getPosition().longitude;

        //move to current position
        moveMap();
    }
}
