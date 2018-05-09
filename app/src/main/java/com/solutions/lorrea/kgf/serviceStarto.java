package com.solutions.lorrea.kgf;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class serviceStarto extends AppCompatActivity {
    String userID, user, tCode;
    BluetoothAdapter mBluetoothAdapter;
    Button btSkip, btConnect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_service);

        //turning off bluetooth
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.disable();
            Intent enableBluetooth = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 0);
        }
        else{
            Intent enableBluetooth = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 0);
        }




        //getting Intent data
        Intent iin= getIntent();
        Bundle b = iin.getExtras();
        if(b != null){
            userID = (String) b.get("userID");
            user = (String) b.get("user");
            tCode = (String) b.get("tCode");
        }

        //initialize buttons
        btSkip = (Button) findViewById(R.id.btSkip);
        btConnect = (Button) findViewById(R.id.btConnect);

        btSkip.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent skipCon = new Intent(serviceStarto.this, record.class);
                skipCon.putExtra("userID", userID);
                skipCon.putExtra("user", user);
                skipCon.putExtra("tCode", tCode);
                startActivity(skipCon);
                finish();
            }
        });

        btConnect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
               Toast.makeText(serviceStarto.this,"Connecting...", Toast.LENGTH_LONG).show();
                try{
                    stopService(new Intent(serviceStarto.this, MyService.class));
                }
                catch (Exception e){

                }
                startService(new Intent(serviceStarto.this, MyService.class));
            }
        });
    }


    //Broadcast
    private BroadcastReceiver mReceiver;
    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter intentFilter = new IntentFilter("com.solutions.lorrea.kgf");
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String msg_for_me = intent.getStringExtra("some_msg");
                //log our message value
                if(msg_for_me.equalsIgnoreCase("connected")) {
                    Toast.makeText(serviceStarto.this, "Printer successfully connected", Toast.LENGTH_LONG).show();
                    Intent goToRecord = new Intent(serviceStarto.this,record.class);
                    goToRecord.putExtra("userID", userID);
                    goToRecord.putExtra("user", user);
                    goToRecord.putExtra("tCode", tCode);
                    startActivity(goToRecord);

                }

            }
        };
        this.registerReceiver(mReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.unregisterReceiver(this.mReceiver);
    }

}
