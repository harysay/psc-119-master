package com.kominfo.harysay.psc;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.kominfo.harysay.psc.handler.GpsTracker;
import com.kominfo.harysay.psc.handler.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentDashboard extends Fragment implements AdapterView.OnItemSelectedListener {

    Button mTelepon, mWhatsapp, mLayananKesehatan;
    Spinner spinnerJenisLaporan;
    String pesan = "ambulance";
    SessionManager session;
    String nama,latitud,longitut,telepon, nik;
    com.github.nkzawa.socketio.client.Socket socket;
    TextView mLabelLatitude, mLabelLongitude,klikviewmap;
    private GpsTracker gpsTracker;
    double latitude,longitude;
    String socketId;

    public FragmentDashboard() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        session = new SessionManager(getActivity().getApplication());
        HashMap<String, String> user = session.getUserDetails();
        nama = user.get(SessionManager.KEY_NAME);
        telepon = user.get(SessionManager.KEY_telepon);
        nik = user.get(SessionManager.KEY_NIK);
        gpsTracker = new GpsTracker(getActivity());
        klikviewmap = view.findViewById(R.id.viewMap);
        mLabelLatitude = view.findViewById(R.id.labellatitude);
        mLabelLongitude = view.findViewById(R.id.labellongitude);
        spinnerJenisLaporan = (Spinner) view.findViewById(R.id.spinnerJenisLaporan);
        mTelepon = view.findViewById(R.id.imgTelepon);
        mWhatsapp=view.findViewById(R.id.iImgWhatsapp);
        mLayananKesehatan=view.findViewById(R.id.imgLayananKesehatan);
        if(gpsTracker.canGetLocation()){
            latitude = gpsTracker.getLatitude();
            longitude = gpsTracker.getLongitude();
            mLabelLatitude.setText(String.valueOf(latitude));
            mLabelLongitude.setText(String.valueOf(longitude));
        }else{
            gpsTracker.showSettingsAlert();
        }
        spinnerJenisLaporan.setOnItemSelectedListener(this);
        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("ambulance");
        categories.add("police");
        categories.add("damkar");
        categories.add("bpbd");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinnerJenisLaporan.setAdapter(dataAdapter);
        try {
            if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        try{
            //socket= IO.socket("http://10.28.11.23:3000");
            socket= IO.socket("http://development.kebumenkab.go.id:4000");
        }
        catch (URISyntaxException e){
            Log.d("error","onCreate"+e.toString());
        }
        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                socketId = socket.id();
//                JSONObject jsonObject = new JSONObject();
//                try {
//                    jsonObject.put("telepon", telepon);
//                    jsonObject.put("socketid", String.valueOf(socketId));
//                    jsonObject.put("nomoridentitas", String.valueOf(nik));
//                    jsonObject.put("nama", nama);
//                    jsonObject.put("longitude", String.valueOf(longitude));
//                    jsonObject.put("latitude", String.valueOf(latitude));
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                socket.emit("pesan", jsonObject);

            }
        });
        socket.connect();
        //socketId = socket.connect().id();
        //socket.on("pesan",handling);

        mTelepon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getActivity(), "anda mengeklik telepon", Toast.LENGTH_SHORT).show();
                //klikTelepon();
                Intent intent= new Intent(getActivity().getApplication(), CallCenterActivity.class);
                startActivity(intent);
            }
        });
//
//        mLaporKecelakaan.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //klikLaporKecelakaan();
//                Intent intent= new Intent(getActivity().getApplication(), LaporKecelakaanActivity.class);
//                startActivity(intent);
//            }
//        });
//        mLaporKebakaran.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //klikLaporKebakaran();
//                Intent intent= new Intent(getActivity().getApplication(), LaporKebakaranActivity.class);
//                startActivity(intent);
//            }
//        });
        mWhatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    String number = "6289637319180";
                    String pesan = "saya ingin melaporkan....";
                    String url = "https://wa.me/"+number+"?text="+ pesan;
                    Intent i= new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }

        });

        klikviewmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getActivity().getApplication(), ViewMapsActivity.class);
                startActivity(intent);
            }
        });

//        mLaporbencanaAlam.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent= new Intent(getActivity().getApplication(), LaporBencanaAlamActivity.class);
//                startActivity(intent);
//            }
//        });
//
//        mLaporKejahatan.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent= new Intent(getActivity().getApplication(), LaporBencanaAlamActivity.class);
//                startActivity(intent);
//            }
//        });
//
//        mLaporKesehatan.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent= new Intent(getActivity().getApplication(), LaporBencanaAlamActivity.class);
//                startActivity(intent);
//            }
//        });
//
//        mKetersediaanKamar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String url = "https://spgdt.kebumenkab.go.id/";
//                Intent i = new Intent(Intent.ACTION_VIEW);
//                i.setData(Uri.parse(url));
//                startActivity(i);
//            }
//        });

        mLayananKesehatan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //tambahpesan(pesan,"me");
                pesan = String.valueOf(spinnerJenisLaporan.getSelectedItem());
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("telepon", telepon);
                    jsonObject.put("socketid", String.valueOf(socketId));
                    jsonObject.put("nomoridentitas", String.valueOf(nik));
                    jsonObject.put("nama", nama);
                    jsonObject.put("message", pesan);
                    jsonObject.put("longitude", String.valueOf(longitude));
                    jsonObject.put("latitude", String.valueOf(latitude));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                socket.emit("pesan", jsonObject);
                //socket.emit("pesan",pesan);
                Toast.makeText(getActivity(), "Laporan Anda berhasil dikirim", Toast.LENGTH_LONG).show();
            }
        });
        return view;
    }

    public void getLocation(View view){

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();
        // Showing selected spinner item
        Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

//    private Emitter.Listener handling=new Emitter.Listener() {
//        @Override
//        public void call(final Object... args) {
//            getActivity().runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    socket.emit("pesan",pesan);
//                    //tambahpesan(args[0].toString(),null);
//                }
//            });
//        }
//    };



}
