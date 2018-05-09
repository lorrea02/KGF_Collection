package com.solutions.lorrea.kgf;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

public class printing extends AppCompatActivity {
    TextView myLabel;
    Button sendButton, findButton;
    // will enable user to enter any text to be printed

    // android built in classes for bluetooth operations

    String userID, user, accNum, accName, amount, bookID, billMonth, amountPaid, amountDue, method,checkNum, tCode;
    Double changeAmt;
    DecimalFormat df = new DecimalFormat("#,##0.00");
    Messenger mMessenger  = null;
    boolean isBind = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_printing);
        myLabel = (TextView) findViewById(R.id.myLabel);
        sendButton = (Button) findViewById(R.id.btSend);





        Intent iin= getIntent();
        Bundle b = iin.getExtras();
        if(b != null){
            userID = (String) b.get("userID");
            user = (String) b.get("user");
            tCode = (String) b.get("tCode");
            method = (String) b.get("method");
            accNum = (String) b.get("accNum");
            accName = (String) b.get("accName");
            amount = (String) b.get("amount");
            bookID = (String) b.get("bookID");
            billMonth = (String) b.get("billMonth");
            changeAmt = (Double) b.get("changeAmt");
            amountPaid = (String) b.get("amountPaid");
            amountDue = (String) b.get("amountDue");
            checkNum = (String) b.get("checkNum");
        }

        // send data typed by the user to be printed
        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Date today = Calendar.getInstance().getTime();
                SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy hh:mm a");
                String dateNow = formatter.format(today);
                String headerTxt = centerTxt("KGFajardo Payment Center") + "\n" +
                    centerTxt("Tin No. : 220-658-835-002") + "\n" +
                    centerTxt("Contact No. : 0918-377-5635") + "\n\n" +
                    centerTxt("PAYMENT SLIP") + "\n" +
                    "________________________________" + "\n\n" +
                    "" + accName + "\n" +
                    "Acc. No.: " + accNum +"\n" +
                    "Book ID: " + bookID +"\n" +
                    "Bill Month: " + billMonth +"\n" +
                    "________________________________" + "\n\n";
//
                String paymentTxt = "";
                    if(method.equalsIgnoreCase("Cash")){
                        paymentTxt = centerTxt("CASH PAYMENT") + "\n\n\n" +
                                amtAlign("Amount Due:", "PHP "+ df.format(Double.parseDouble(amountDue))) + "\n" +
                                amtAlign("Transaction Charge:", "PHP "+ df.format(10.00)) + "\n" +
                                amtAlign("Amount Tendered:", "PHP "+ df.format(Double.parseDouble(amountPaid))) + "\n" +
                                amtAlign("Change:", "PHP "+ df.format(Double.parseDouble("" + changeAmt))) + "\n\n" +
                                "Date / Time" + "\n" +
                                "" + dateNow +"\n\n\n" +
                                centerTxt("" + user) +"\n" +
                                centerTxt("Collection Officer")+"\n\n\n\n\n";
                    }
                    else if(method.equalsIgnoreCase("Check")){
                        paymentTxt = centerTxt("CHECK PAYMENT") + "\n\n\n" +
                            amtAlign("Amount Due:", "PHP "+ df.format(Double.parseDouble(amountDue))) + "\n" +
                            amtAlign("Transaction Charge:", "PHP "+ df.format(10.00)) + "\n" +
                            amtAlign("Amount Tendered:", "PHP "+ df.format(Double.parseDouble(amountPaid))) + "\n" +
                            "Check Number: "+ checkNum + "\n\n" +
                            "Date / Time" + "\n" +
                            "" + dateNow +"\n\n\n" +
                            centerTxt("" + user) +"\n" +
                            centerTxt("Collection Officer")+"\n\n\n\n\n";
                    }
                if(isBind){
                    ArrayList message = new ArrayList<>();
                    message.add("" + headerTxt + paymentTxt);
                    Message msg = Message.obtain();
                    msg.obj = message;
                    try {
                        mMessenger.send(msg);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    Toast.makeText(printing.this,"Bind muna", Toast.LENGTH_LONG).show();
                }
            }
        });

        bindService();
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMessenger = new Messenger(service);
            isBind = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mMessenger = null;
            isBind = false;
        }
    };

    @Override
    protected void onStop() {
        unbindService(mConnection);
        isBind = false;
        mMessenger = null;
        super.onStop();
    }

    public void bindService(){
        Intent intent = new Intent(printing.this,MyService.class);
        bindService(intent,mConnection, Context.BIND_AUTO_CREATE);
    }







//    public void sendData() throws IOException {
//        try {
//
//            // the text typed by the user
//
////            12345678901234657890123456789012
////            ################################
////                           KGF
////            Address
////            Contact Number:
////            TIN
////            BIR PERMIT
////            ________________________________
////
////            Acc. Name:
////            Acc. No.:
////            Bill Month:
////            ________________________________
//
////            Amount Due:
////            Amount Tendered:
////            Change
//
////            Date / Time
//
////            Collector Name
////            Collection Officer
//            Date today = Calendar.getInstance().getTime();
//            SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy hh:mm a");
//            String dateNow = formatter.format(today);
//            String headerTxt = centerTxt("KGF") + "\n" +
//                    centerTxt("Address") + "\n" +
//                    centerTxt("TIN") + "\n" +
//                    centerTxt("BIR Permit:") +"\n\n" +
//                    centerTxt("OFFICIAL RECEIPT") + "\n" +
//                    "________________________________" + "\n\n" +
//                    "" + accName + "\n" +
//                    "Acc. No.: " + accNum +"\n" +
//                    "Book ID: " + bookID +"\n" +
//                    "Bill Month: " + billMonth +"\n" +
//                    "________________________________" + "\n\n";
//
//            String paymentTxt = "";
//            if(method.equalsIgnoreCase("Cash")){
//                paymentTxt = centerTxt("CASH PAYMENT") + "\n\n\n" +
//                        amtAlign("Amount Due:", "PHP "+ df.format(Double.parseDouble(amountDue))) + "\n" +
//                        amtAlign("Amount Tendered:", "PHP "+ df.format(Double.parseDouble(amountPaid))) + "\n" +
//                        amtAlign("Change:", "PHP "+ df.format(Double.parseDouble("" + changeAmt))) + "\n\n" +
//                        "Date / Time" + "\n" +
//                        "" + dateNow +"\n\n\n" +
//                        centerTxt("" + user) +"\n" +
//                        centerTxt("Collection Officer")+"\n\n\n\n\n";
//            }
//            else if(method.equalsIgnoreCase("Check")){
//                paymentTxt = centerTxt("CHECK PAYMENT") + "\n\n\n" +
//                        amtAlign("Amount Due:", "PHP "+ df.format(Double.parseDouble(amountDue))) + "\n" +
//                        amtAlign("Amount Tendered:", "PHP "+ df.format(Double.parseDouble(amountPaid))) + "\n" +
//                        "Check Number: "+ checkNum + "\n\n" +
//                        "Date / Time" + "\n" +
//                        "" + dateNow +"\n\n\n" +
//                        centerTxt("" + user) +"\n" +
//                        centerTxt("Collection Officer")+"\n\n\n\n\n";
//            }
//
//
//
//            String msg = headerTxt + paymentTxt;
//
//            mmOutputStream.write(msg.getBytes());
//
//            // tell the user data were sent
////             myLabel.setText("Data Sent");
//
//        } catch (NullPointerException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }




    public String centerTxt(String txt){
        if(txt.length() < 32){
            int spaces = 32 - txt.length();
            int firstHalf = spaces / 2;
            int secondHalf = spaces - firstHalf;
            for(int i = 0; i < firstHalf; i++){
                txt = " " + txt;
            }
            for(int i = 0; i < secondHalf; i++){
                txt = txt + " ";
            }
        }
        return txt;
    }

    public String amtAlign(String title, String amt){
        String txt = "";
        int total = title.length() + amt.length();
        int spaces = 32 - total;
        String strSpaces = "";
        for(int i = 0; i < spaces; i++){
            strSpaces = strSpaces + " ";
        }
        txt = title + strSpaces + amt;
        return txt;
    }

    public void newRecord(View view){
        Intent newRecord = new Intent(printing.this, record.class);
        newRecord.putExtra("user", user);
        newRecord.putExtra("userID", userID);
        newRecord.putExtra("tCode", tCode);
        startActivity(newRecord);
        finish();
    }
}
