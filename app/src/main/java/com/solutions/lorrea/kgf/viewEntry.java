package com.solutions.lorrea.kgf;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class viewEntry extends AppCompatActivity {
    String userID, user, tCode, accNum, accName, billMonth, bookID, amount, paymentType, checkNum;
    Calendar calendar = Calendar.getInstance();
    String year = "" + calendar.get(Calendar.YEAR);
    String month = "" + (calendar.get(Calendar.MONTH) + 1);
    String date = "" + calendar.get(Calendar.DATE);
    DecimalFormat df = new DecimalFormat("#,##0.00");
    TextView tvAccNum, tvBookID, tvBillMonth, tvAmount, tvPayment, tvCheckNum, tvAccName;
    Messenger mMessenger  = null;
    boolean isBind = false;
    Button sendButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_entry);
        getSupportActionBar().setHomeButtonEnabled(true);

        sendButton = (Button) findViewById(R.id.btSend);

        Intent iin = getIntent();
        Bundle b = iin.getExtras();
        if(b != null){
            //accNum, accName, billMonth, bookID, amount, paymentType, checkNum
            userID = (String) b.get("userID");
            user = (String) b.get("user");
            tCode = (String) b.get("tCode");
            accNum = (String) b.get("accNum");
            accName = (String) b.get("accName");
            billMonth = (String) b.get("billMonth");
            bookID = (String) b.get("bookID");
            amount = (String) b.get("amount");
            paymentType = (String) b.get("paymentType");
            checkNum = (String) b.get("checkNum");
        }
        tvAccNum = (TextView) findViewById(R.id.tvAccNum);
        tvBookID = (TextView) findViewById(R.id.tvBookID);
        tvBillMonth = (TextView) findViewById(R.id.tvBillMonth);
        tvAmount = (TextView) findViewById(R.id.tvAmount);
        tvPayment = (TextView) findViewById(R.id.tvPaymentType);
        tvCheckNum = (TextView) findViewById(R.id.tvCheckNum);
        tvAccName = (TextView) findViewById(R.id.tvAccName);
        sendButton = (Button) findViewById(R.id.btSend);

        String longTerm = "";
        if(paymentType.equalsIgnoreCase("CK"))
                longTerm = "Check";
        else if(paymentType.equalsIgnoreCase("CS"))
            longTerm = "Cash";

        tvAccNum.setText("Account No. : " + accNum);
        tvBookID.setText("Book ID : " + bookID);
        tvBillMonth.setText("Bill Month : " + billMonth);
        tvAmount.setText("Amount : " + df.format(Double.parseDouble(amount)));
        tvPayment.setText("Payment Type : " + longTerm);
        tvCheckNum.setText("Check No. : " + checkNum);
        tvAccName.setText("Account Name : " + accName);


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
                if(paymentType.equalsIgnoreCase("CS")){
                    paymentTxt = centerTxt("CASH PAYMENT") + "\n\n\n" +
                            amtAlign("Amount Due:", "PHP "+ df.format(Double.parseDouble(amount))) + "\n" +
                            amtAlign("Transaction Charge:", "PHP "+ df.format(10.00)) + "\n" +
                            amtAlign("Amount Tendered:", "PHP "+ df.format(Double.parseDouble(amount) + 10.00)) + "\n" +
                            amtAlign("Change:", "PHP "+ df.format(Double.parseDouble("0.00"))) + "\n\n" +
                            "Date / Time" + "\n" +
                            "" + dateNow +"\n\n\n" +
                            centerTxt("" + user) +"\n" +
                            centerTxt("Collection Officer")+"\n\n\n\n\n";
                }
                else if(paymentType.equalsIgnoreCase("CK")){
                    paymentTxt = centerTxt("CHECK PAYMENT") + "\n\n\n" +
                            amtAlign("Amount Due:", "PHP "+ df.format(Double.parseDouble(amount))) + "\n" +
                            amtAlign("Transaction Charge:", "PHP "+ df.format(10.00)) + "\n" +
                            amtAlign("Amount Tendered:", "PHP "+ df.format(Double.parseDouble(amount) + 10.00)) + "\n" +
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
                    Toast.makeText(viewEntry.this,"Bind muna", Toast.LENGTH_LONG).show();
                }
            }
        });

        bindService();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.opt_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.menuLogout){
            Intent logoutIntent = new Intent(viewEntry.this,MainActivity.class);
            Toast.makeText(viewEntry.this,"Successfully logged out.", Toast.LENGTH_LONG).show();
            startActivity(logoutIntent);
            finish();
        }
        if(id == R.id.menuRecord){
            Intent viewIntent = new Intent(viewEntry.this,viewData.class);
            startActivity(viewIntent);
        }
        if(id == R.id.menuEmail){
            while(date.length() < 2){
                date = "0" + date;
            }
            while(month.length() < 2){
                month = "0" + month;
            }
            String dateToday = year+month+date;
            String filename="collection_" + dateToday +".txt";
            File dir = new File(Environment.getExternalStorageDirectory(), "KGF");
            File filelocation = new File(dir.getAbsolutePath(), filename);
            Uri path = Uri.fromFile(filelocation);
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent .setType("vnd.android.cursor.dir/email");
            String to[] = {"admin@batelec2.com.ph"};
            emailIntent .putExtra(Intent.EXTRA_EMAIL, to);
            emailIntent .putExtra(Intent.EXTRA_STREAM, path);
            emailIntent .putExtra(Intent.EXTRA_SUBJECT, "KGF Collection - " + dateToday);
            startActivity(Intent.createChooser(emailIntent , "Send email..."));
        }

        return super.onOptionsItemSelected(item);
    }

    public void backToSearch(View view){
        Intent returnSearch = new Intent(viewEntry.this, viewData.class);
        returnSearch.putExtra("user", user);
        returnSearch.putExtra("userID", userID);
        returnSearch.putExtra("tCode", tCode);
        startActivity(returnSearch);
        finish();
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
        Intent intent = new Intent(viewEntry.this,MyService.class);
        bindService(intent,mConnection, Context.BIND_AUTO_CREATE);
    }


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
}
