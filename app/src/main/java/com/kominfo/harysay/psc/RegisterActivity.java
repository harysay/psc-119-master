package com.kominfo.harysay.psc;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

public class RegisterActivity extends AppCompatActivity {

    public static final int REQUEST_GET_SINGLE_FILE = 101;
    public static final int REQUEST_CAPTURE_IMAGE = 102;

    EditText mName, mEmail, mUsername, mPassword, mConfirmPassword;
    Button btnDaftar;
    LinearLayout btnLogin;
    TextView btnTambahFoto;
    ImageView imgTambahFoto;
    User mUser;
    byte[] selectedImageByte = null;

    private UserRepository mUserRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        bindView();
        mUserRepo = new UserRepository(this);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mNama = mName.getText().toString();
                String mMail = mEmail.getText().toString();
                String mUname = mUsername.getText().toString();
                String mPass = mPassword.getText().toString();
                String mConfPass = mConfirmPassword.getText().toString();

                if (TextUtils.isEmpty(mNama)){
                    pesan("Nama tidak boleh kosong");
                    return;
                }

                if (TextUtils.isEmpty(mMail)){
                    pesan("Email tidak boleh kosong");
                    return;
                }

                if (TextUtils.isEmpty(mUname)){
                    pesan("Username tidak boleh kosong");
                    return;
                }

                if (TextUtils.isEmpty(mPass)){
                    pesan("Password tidak boleh kosong");
                    return;
                }

                if (TextUtils.isEmpty(mConfPass)){
                    pesan("Confirm password tidak boleh kosong");
                    return;
                }

                if (!mPass.equals(mConfPass)){
                    pesan("Password tidak sama");
                    return;
                }

                daftarAkun(mNama, mMail, mUname, mPass);
            }
        });

        btnTambahFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prepareStorage();
            }
        });

        imgTambahFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prepareStorage();
            }
        });

    }

    private void daftarAkun(String mNama, String mMail, String mUname, String mPass) {
        mUser = new User();
        mUser.setEmail(mMail);
        mUser.setUsername(mUname);
        mUser.setName(mNama);
        mUser.setPassword(mPass);
        if (selectedImageByte != null){
            mUser.setProfile_picture("");
        }

        mUserRepo.insertUserTask(mUser);

        pesan("Anda berhasil mendaftarkan akun");
        finish();

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

                        imgTambahFoto.setImageBitmap(bitmap);

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

                        imgTambahFoto.setImageBitmap(bitmap);

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
        mName = findViewById(R.id.register_name_edittext);
        mEmail = findViewById(R.id.register_email_edittext);
        mUsername = findViewById(R.id.register_username_edittext);
        mPassword = findViewById(R.id.register_password_edittext);
        mConfirmPassword = findViewById(R.id.register_confirm_password_edittext);
        btnDaftar = findViewById(R.id.register_daftar_button);
        btnLogin = findViewById(R.id.register_daftar_text);
        btnTambahFoto = findViewById(R.id.register_tambah_foto_text);
        imgTambahFoto = findViewById(R.id.register_image_profile);
    }

    private void pesan(String pesan){
        Toast.makeText(this, pesan, Toast.LENGTH_SHORT).show();
    }
}
