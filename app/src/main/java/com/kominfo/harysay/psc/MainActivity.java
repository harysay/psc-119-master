package com.kominfo.harysay.psc;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.kominfo.harysay.psc.handler.CheckNetwork;
import com.kominfo.harysay.psc.handler.SessionManager;
import com.kominfo.harysay.psc.intro.MyIntro;
import com.kominfo.harysay.psc.model.User;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


public class MainActivity extends AppCompatActivity{
    private Context mContext;
    private User mInitUser;
    public boolean isFirstStart;
    TextView mName, mEmail;
    ImageView mImageViewProfileDrawer;
    FragmentManager fm;
    FragmentTransaction ft;
    SessionManager session;
    String nik,nama,jenisKelamin,email,alamat,telepon,tokenNilai,urlImag;
    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        session = new SessionManager(getApplication());
        mContext = getApplicationContext();
        if (CheckNetwork.isInternetAvailable(MainActivity.this))
        {
            if (!session.isLoggedIn()) {
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //  Intro App Initialize SharedPreferences
                        SharedPreferences getSharedPreferences = PreferenceManager
                                .getDefaultSharedPreferences(getBaseContext());

                        //  Create a new boolean and preference and set it to true
                        isFirstStart = getSharedPreferences.getBoolean("firstStart", true);

                        //  Check either activity or app is open very first time or not and do action
                        if (isFirstStart) {

                            //  Launch application introduction screen
                            Intent i = new Intent(MainActivity.this, MyIntro.class);
                            startActivity(i);
                            SharedPreferences.Editor e = getSharedPreferences.edit();
                            e.putBoolean("firstStart", false);
                            e.apply();
                        }
                    }
                });
                t.start();
                // user is not logged in redirect him to Login Activity
                Intent i = new Intent(MainActivity.this, LoginActivity.class);

                // Closing all the Activities
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                // Add new Flag to start new Activity
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                // Staring Login Activity
                startActivity(i);
                finish();

            } else {
                setContentView(R.layout.activity_main);
                Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);
                // get user data from session
                HashMap<String, String> user = session.getUserDetails();

                nik = user.get(SessionManager.KEY_NIK);
                nama = user.get(SessionManager.KEY_NAME);
                urlImag = user.get(SessionManager.KEY_URLIMAGE);
                jenisKelamin = user.get(SessionManager.KEY_JK);
                email = user.get(SessionManager.KEY_email);
                alamat = user.get(SessionManager.KEY_alamat);
                telepon = user.get(SessionManager.KEY_telepon);
                tokenNilai = user.get(SessionManager.KEY_TOKEN);
//                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//                NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//
//                View hView = navigationView.getHeaderView(0);
//                ImageView imgvw = (ImageView)hView.findViewById(R.id.imageUserLogin);
//                TextView lblName = (TextView) hView.findViewById(R.id.main_nav_account_name);
//                TextView lblEmail = (TextView) hView.findViewById(R.id.main_nav_account_email);

                setUpView();
            }
        }else {
            try {
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();

                alertDialog.setTitle("Tidak ada koneksi internet!");
                alertDialog.setMessage("Cek koneksi internet Anda dan ulangi lagi");
                alertDialog.setIcon(android.R.drawable.stat_sys_warning);
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int n) {
                        dialog.cancel();
                    }
                });
                alertDialog.show();
            } catch (Exception e) {
                //Log.d(Constants.TAG, "Show Dialog: "+e.getMessage());
            }
        }




//        setContentView(R.layout.activity_main);
//
//        setUpView();
//
//        if(getIntent().hasExtra("user_login")) {
//            mInitUser = getIntent().getParcelableExtra("user_login");
//
//            mName.setText(mInitUser.getName());
//            mEmail.setText(mInitUser.getEmail());
//        }

    }

    private void setUpView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
//        View headerView = navigationView.inflateHeaderView(R.layout.nav_header_main);
        View headerView = navigationView.getHeaderView(0);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        //navigationView.setNavigationItemSelectedListener(this);
        //navigationView.setCheckedItem(R.id.nav_home);

        mName = headerView.findViewById(R.id.main_nav_account_name);
        mEmail = headerView.findViewById(R.id.main_nav_account_email);
        mImageViewProfileDrawer = headerView.findViewById(R.id.imageUserLogin);
        mName.setText(nama);
        mEmail.setText(email);
        new DownloadImage().execute(urlImag);

        fm=getSupportFragmentManager();
        ft=fm.beginTransaction();
        ft.replace(R.id.contenerView, new FragmentDashboard()).commit();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
               drawer.closeDrawers();
               if (menuItem.getItemId()==R.id.nav_home){
                   FragmentTransaction fmTran=fm.beginTransaction();
                   fmTran.replace(R.id.contenerView, new FragmentDashboard()).commit();

               }
                if (menuItem.getItemId()==R.id.nav_report){
                    FragmentTransaction fmTran=fm.beginTransaction();
                    fmTran.replace(R.id.contenerView, new FragmentRiwayatLaporan()).commit();

                }
                if (menuItem.getItemId()==R.id.nav_telepon){
//                    FragmentTransaction fmTran=fm.beginTransaction();
//                    fmTran.replace(R.id.contenerView, new FragmentRiwayatLaporan()).commit();
                        //Toast.makeText(getActivity(), "anda mengeklik telepon", Toast.LENGTH_SHORT).show();
                        //klikTelepon();
                        Intent intent= new Intent(MainActivity.this.getApplication(), CallCenterActivity.class);
                        startActivity(intent);

                }
                if (menuItem.getItemId()==R.id.nav_wa){
                    String number = "6289637319180";
                    String pesan = "saya ingin melaporkan....";
                    String url = "https://wa.me/"+number+"?text="+ pesan;
                    Intent i= new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
//                    FragmentTransaction fmTran=fm.beginTransaction();
//                    fmTran.replace(R.id.contenerView, new FragmentRiwayatLaporan()).commit();

                }
                if (menuItem.getItemId()==R.id.nav_account){
                    FragmentTransaction fmTran=fm.beginTransaction();
                    fmTran.replace(R.id.contenerView, new FragmentPengaturanAkun()).commit();

                }
                if (menuItem.getItemId()==R.id.nav_about){
                    FragmentTransaction fmTran=fm.beginTransaction();
                    fmTran.replace(R.id.contenerView, new FragmentTentangAplikasi()).commit();

                }
                if (menuItem.getItemId()==R.id.nav_feedback){
                    FragmentTransaction fmTran=fm.beginTransaction();
                    fmTran.replace(R.id.contenerView, new FragmentKritikSaran()).commit();

                }
                return true;
            }
        });
    }

    // DownloadImage AsyncTask
    private class DownloadImage extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(MainActivity.this);
            // Set progressdialog message
            mProgressDialog.setMessage("Loading image...");
            mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }

        @Override
        protected Bitmap doInBackground(String... URL) {

            String imageURL = URL[0];

            Bitmap bitmap = null;
            try {
                // Download Image from URL
                InputStream input = new java.net.URL(imageURL).openStream();
                // Decode Bitmap
                bitmap = BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            // Set the bitmap into ImageView
            mImageViewProfileDrawer.setImageBitmap(result);
            // Close progressdialog
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    //@Override
//    public boolean onNavigationItemSelected(MenuItem item) {
//        // Handle navigation view item clicks here.
//        int id = item.getItemId();
//
//        if (id == R.id.nav_home) {
//            //pesan("FragmentHome");


//        } else if (id == R.id.nav_report) {
//          //  pesan("Riwayat Laporan");


//        } else if (id == R.id.nav_account) {
//            pesan("Pengaturan Akun");
//        } else if (id == R.id.nav_about) {
//            pesan("Tentang Aplikasi");
//        } else if (id == R.id.nav_feedback) {
//            pesan("Kritik & Saran");
//        } else if (id == R.id.nav_logout) {
//            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            startActivity(intent);
//            finish();
//        }
//
//        DrawerLayout drawer = findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
//        return true;
//    }

    private void pesan(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
