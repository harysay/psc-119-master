package com.kominfo.harysay.psc;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class CallCenterActivity extends AppCompatActivity {
    Button mCall1, mCall2, mCall3, mCall4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.callcenter_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarcallcenter);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        mCall1= (Button)findViewById(R.id.btnc1);
        mCall2= (Button)findViewById(R.id.btnc2);
        mCall3= (Button)findViewById(R.id.btnc3);
        mCall4= (Button)findViewById(R.id.btnc4);

        mCall1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = "6289637319180";
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                startActivity(intent);
            }
        });

        mCall2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = "6283804043020";
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                startActivity(intent);
            }
        });
        mCall3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = "6283804043020";
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                startActivity(intent);
            }
        });

        mCall4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = "6283804043020";
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                startActivity(intent);
            }
        });
    }

}
