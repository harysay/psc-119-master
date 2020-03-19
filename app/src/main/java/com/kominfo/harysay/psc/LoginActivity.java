package com.kominfo.harysay.psc;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.OkHttpResponseAndParsedRequestListener;
import com.androidnetworking.interfaces.OkHttpResponseListener;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.kominfo.harysay.psc.handler.MyApplication;
import com.kominfo.harysay.psc.handler.Serve;
import com.kominfo.harysay.psc.handler.SessionManager;
import com.kominfo.harysay.psc.model.User;
import com.kominfo.harysay.psc.persistence.UserRepository;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    public static final String TAG = LoginActivity.class.getSimpleName();
    EditText mUsername, mPassword;
    TextView btnLupaPassword;
    Button btnLogin;
    LinearLayout btnDaftar;
    String tokenlogin, namauser, nikget, jeniskelamin, emailuser,alamatusr, teleponusr,urlimage;
    boolean status;
    private ProgressDialog progressBar;
    SessionManager session;

    private ArrayList<User> mUsers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        session = new SessionManager(getApplicationContext());
        mUsername = (EditText) findViewById(R.id.login_username_edittext);
        mPassword = (EditText) findViewById(R.id.login_password_edittext);
        btnLogin = (Button) findViewById(R.id.login_login_button);
        bindView();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String uName = mUsername.getText().toString();
                String uPass = mPassword.getText().toString();

                if (TextUtils.isEmpty(uName)){
                    pesan("Username tidak boleh kosong");
                    return;
                }
                if (TextUtils.isEmpty(uPass)){
                    pesan("Password tidak boleh kosong");
                    return;
                }
                loginAuth(v);
            }
        });

        btnLupaPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pesan("Lupa password");
            }
        });

        btnDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }

    private void bindView() {
        mUsername = findViewById(R.id.login_username_edittext);
        mPassword = findViewById(R.id.login_password_edittext);
        btnLupaPassword = findViewById(R.id.login_lupa_password_text);
        btnDaftar = findViewById(R.id.login_daftar_text);
    }

    private void pesan(String pesan){
        Toast.makeText(this, pesan, Toast.LENGTH_SHORT).show();
    }

    private void loginAuth(View viewku) {
        progressBar = new ProgressDialog(viewku.getContext());//Create new object of progress bar type
        progressBar.setCancelable(false);//Progress bar cannot be cancelled by pressing any wher on screen
        progressBar.setMessage("Verifikasi user,...");//Title shown in the progress bar
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);//Style of the progress bar
        progressBar.setProgress(0);//attributes
        progressBar.setMax(100);//attributes
        progressBar.show();//show the progress bar
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = Serve.URL+"auth/login";
        //String url = "http://development.kebumenkab.go.id:3000/api/auth/login";
        JSONObject jsonBody = new JSONObject();
        final String requestBody = jsonBody.toString();
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    //menaruh data JSON kkedalam variabel JSON Object
                    JSONObject jsonPost = new JSONObject(response.toString());
                    status = jsonPost.getBoolean("success");
                    tokenlogin = jsonPost.getString("msg");

                    Log.e("coba", jsonPost.toString());

                    if(status==true){
                        getDetailUser(tokenlogin);
                        progressBar.cancel();//Progress bar will be cancelled (hide from screen) when this run function will execute after 3.5seconds
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }else if(status==false) {
                        progressBar.cancel();//Progress bar will be cancelled (hide from screen) when this run function will execute after 3.5seconds
                        Toast.makeText(LoginActivity.this, tokenlogin, Toast.LENGTH_SHORT).show();
//                        finish();
//                        startActivity(getIntent());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    progressBar.cancel();//Progress bar will be cancelled (hide from screen) when this run function will execute after 3.5seconds
                    Log.e("coba", e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error Response", error.toString());
                progressBar.cancel();//Progress bar will be cancelled (hide from screen) when this run function will execute after 3.5seconds
                Toast.makeText(LoginActivity.this, "Kesalahan server silahkan hubungi admin", Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                String user =mUsername.getText().toString();
                String pss = mPassword.getText().toString();
                params.put("nomorIdentitas", user);
                params.put("password", pss);
                return params;
            }
        };
        Log.d("kembalianLogin", stringRequest.toString());
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public void getDetailUser(final String userToken){
        String user =mUsername.getText().toString();
        AndroidNetworking.get(Serve.URL+"user/nomor-identitas/"+user)
                .setTag("test")
                .addHeaders("Authorization", userToken)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("tag-response",response.toString());
//                        if (response == null) {
//                            Toast.makeText(LoginActivity.this, "Couldn't fetch the store items! Pleas try again.", Toast.LENGTH_LONG).show();
//                            return;
//                        }else {
                            try {
                                JSONObject jsonPost = new JSONObject(response.toString());
                                String status = jsonPost.getString("success");
                                JSONObject getMsg = jsonPost.getJSONObject("msg");
                                nikget = getMsg.getString("nomorIdentitas");
                                namauser = getMsg.getString("nama");
                                jeniskelamin = getMsg.getString("jk");
                                emailuser = getMsg.getString("email");
                                alamatusr = getMsg.getString("alamat");
                                teleponusr = getMsg.getString("telepon");
                                urlimage = "https://simpeg.kebumenkab.go.id/web/personal2019/foto/500104555.jpg";
                                session.createLoginSession(nikget, namauser, jeniskelamin, emailuser, userToken, urlimage, alamatusr, teleponusr);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        //}
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.i("tag-error",anError.getResponse().toString());
                    }
                });
    }
//    private void getDetailUser(final String userToken) {
//        String user =mUsername.getText().toString();
//        String url = Serve.URL+"user/nik/"+user;
//        StringRequest req = new StringRequest(Request.Method.GET,url, new Response.Listener<String>() {
//
//            @Override
//            public void onResponse(String response) {
//                Log.d(TAG, response.toString());
//                try {
//                    JSONObject jsonPost = new JSONObject(response);
//                    nikget = jsonPost.getString("nik");
//                    namauser = jsonPost.getString("nama");
//                    jeniskelamin = jsonPost.getString("jk");
//                    emailuser = jsonPost.getString("email");
//                    alamatusr = jsonPost.getString("alamat");
//                    teleponusr = jsonPost.getString("telepon");
//                    urlimage = "https://simpeg.kebumenkab.go.id/web/personal2019/foto/500104555.jpg";
//                    session.createLoginSession(nikget,namauser,jeniskelamin,emailuser,userToken,urlimage,alamatusr,teleponusr);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                VolleyLog.d("getuserdetail", "Error: " + error.getMessage());
//                Log.e(TAG, "Site Info Error: " + error.getMessage());
//                Toast.makeText(getApplicationContext(),"You are Unauthorized", Toast.LENGTH_SHORT).show();
////                try {
////                    activity.onRequestServed(null,code);
////                } catch (JSONException e) {
////                    e.printStackTrace();
////                }
//            }
//        }) {
//            /**
//             * Passing some request headers
//             */
//            @Override
//            public Map getHeaders() throws AuthFailureError {
//                HashMap headers = new HashMap();
//                headers.put("Content-Type", "application/json");
//                headers.put("Authorization", userToken);
//                return headers;
//            }
//        };
//        // Adding the request to the queue along with a unique string tag
//        //queue.add(req,headers);
//        MyApplication.getInstance().addToRequestQueue(req, "headerRequest");
//    }
}

