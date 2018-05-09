package com.solutions.lorrea.kgf;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;



import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class paymentOption extends AppCompatActivity {
    RadioButton rbCash, rbCheck;
    RadioGroup rgOption;
    String userID, user, accNum, accName, amount, bookID, billMonth, selOption, checknum, tCode;
    LinearLayout checkLayout, cashLayout;
    TextView tvCashDue, tvCashChange, tvCheckDue;
    EditText etCheckAmt, etCheckNum, etCashAmt;
    Double changeAmt, amtPaid;
    CheckBox cbConfirm;
    Button btnProcess;
    DecimalFormat df = new DecimalFormat("#,##0.00");
    Calendar calendar = Calendar.getInstance();
    String year = "" + calendar.get(Calendar.YEAR);
    String month = "" + (calendar.get(Calendar.MONTH) + 1);
    String date = "" + calendar.get(Calendar.DATE);




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_option);
        getSupportActionBar().setHomeButtonEnabled(true);
        rbCash = (RadioButton) findViewById(R.id.rbCash);
        rbCheck = (RadioButton) findViewById(R.id.rbCheck);
        rgOption = (RadioGroup) findViewById(R.id.rgOption);
        RadioButton checkedRadioButton = (RadioButton)rgOption.findViewById(rgOption.getCheckedRadioButtonId());
        checkLayout = (LinearLayout) findViewById(R.id.checkLayout);
        cashLayout = (LinearLayout) findViewById(R.id.cashLayout);
        tvCashChange = (TextView) findViewById(R.id.tvCashChange);
        tvCheckDue = (TextView) findViewById(R.id.tvCheckDue);
        tvCashDue = (TextView) findViewById(R.id.tvCashDue);
        etCheckAmt = (EditText) findViewById(R.id.etCheckAmt);
        etCheckNum = (EditText) findViewById(R.id.etCheckNum);
        etCashAmt = (EditText) findViewById(R.id.etCashAmt);
        cbConfirm = (CheckBox) findViewById(R.id.cbConfirm);
        btnProcess = (Button) findViewById(R.id.btnProcess);



        Intent iin= getIntent();
        Bundle b = iin.getExtras();
         if(b != null){
            tCode = (String) b.get("tCode");
            userID = (String) b.get("userID");
            user = (String) b.get("user");
            accNum = (String) b.get("accNum");
            accName = (String) b.get("accName");
            amount = (String) b.get("amount");
            bookID = (String) b.get("bookID");
            billMonth = (String) b.get("billMonth");
            tvCheckDue.setText("₱ "+ df.format(Double.parseDouble(amount)));
            tvCashDue.setText("₱ "+ df.format(Double.parseDouble(amount)));
        }
        tvCashChange.setText("₱ 0.00");

         etCashAmt.setOnKeyListener(new View.OnKeyListener() {
             @Override
             public boolean onKey(View v, int keyCode, KeyEvent event) {
                 if(event.getAction() == KeyEvent.ACTION_UP){
                     amtPaid = Double.parseDouble(etCashAmt.getText().toString());
                     changeAmt = amtPaid - (Double.parseDouble(amount) + 10.00);
                     if(changeAmt >= 0) {
//                         Toast.makeText(paymentOption.this,"" + changeAmt, Toast.LENGTH_SHORT).show();
                         tvCashChange.setText("₱ " + df.format(changeAmt));
                     }
//                     else{
//                         tvCashChange.setText("₱ 0.00");
//                     }
                 }
                 return false;
             }
         });

//


        rgOption.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton checkedRadioButton = (RadioButton)group.findViewById(checkedId);
                boolean isChecked = checkedRadioButton.isChecked();
                if (isChecked)
                {
                    // Changes the textview's text to "Checked: example radiobutton text"
                    if(checkedRadioButton.getText().toString().equalsIgnoreCase("Cash")){
                        selOption = "Cash";
                        cashLayout.setVisibility(View.VISIBLE);
                        checkLayout.setVisibility(View.GONE);
                        cbConfirm.setVisibility(View.VISIBLE);
                        cbConfirm.setChecked(false);
                        btnProcess.setVisibility(View.VISIBLE);
                        etCheckAmt.setText("");
                        etCheckNum.setText("");
                    }
                    else if(checkedRadioButton.getText().toString().equalsIgnoreCase("Check")){
                        selOption = "Check";
                        checkLayout.setVisibility(View.VISIBLE);
                        cashLayout.setVisibility(View.GONE);
                        cbConfirm.setVisibility(View.VISIBLE);
                        cbConfirm.setChecked(false);
                        etCashAmt.setText("");
                        tvCashChange.setText("₱ 0.00");
                        btnProcess.setVisibility(View.VISIBLE);
                    }
                }
            }
        });


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
            Intent logoutIntent = new Intent(paymentOption.this,MainActivity.class);
            Toast.makeText(paymentOption.this,"Successfully logged out.", Toast.LENGTH_LONG).show();
            startActivity(logoutIntent);
            finish();
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
            String to[] = {"asd@gmail.com"};
            emailIntent .putExtra(Intent.EXTRA_EMAIL, to);
            emailIntent .putExtra(Intent.EXTRA_STREAM, path);
            emailIntent .putExtra(Intent.EXTRA_SUBJECT, "" + dateToday + " Collection");
            startActivity(Intent.createChooser(emailIntent , "Send email..."));
        }
        if(id == R.id.menuReport){
            Intent reportIntent = new Intent(paymentOption.this,report.class);
            reportIntent.putExtra("userID", userID);
            reportIntent.putExtra("tCode", tCode);
            reportIntent.putExtra("user", user);
            startActivity(reportIntent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void processPayment(View view) throws IOException{
        if(selOption.equalsIgnoreCase("Cash")){
            //do cash transaction
//            Toast.makeText(paymentOption.this,""+changeAmt,Toast.LENGTH_SHORT).show();
            if(etCashAmt.getText().toString().isEmpty()){
                Toast.makeText(paymentOption.this,"Please specify the amount paid",Toast.LENGTH_LONG).show();
            }
            else if(changeAmt < 0){
                Toast.makeText(paymentOption.this,"Unable to process insufficient payment",Toast.LENGTH_LONG).show();
            }
            else if(!cbConfirm.isChecked()){
                Toast.makeText(paymentOption.this,"Please confirm the details first",Toast.LENGTH_LONG).show();
            }
            else{
                while(month.length() < 2){
                    month = "0" + month;
                }
                while(date.length() < 2){
                    date = "0" + date;
                }
                String dateToday = year + month + date;
                BufferedWriter bw = null;
                File dir = new File(Environment.getExternalStorageDirectory(), "KGF");
                if (!dir.exists() || !dir.isDirectory())
                    dir.mkdirs();
                String path = dir.getAbsolutePath();
                String uri= path+"/collection_" + dateToday + ".txt";
                File collectionTXT = new File(uri);
                if (!collectionTXT.exists()) {
                    collectionTXT.createNewFile();
                }

                File file = new File(uri);
                FileOutputStream fileinput = new FileOutputStream(file, true);
                PrintStream printstream = new PrintStream(fileinput);
                printstream.print("");
                fileinput.close();

                OutputStreamWriter file_writer = new OutputStreamWriter(new FileOutputStream(file,true));
                BufferedWriter buffered_writer = new BufferedWriter(file_writer);
                String amountAppend = df.format(Double.parseDouble(amount));
//                String txtToAppend = accNum + "," + accName.replace(",", "") + "," + amountAppend.replace
//                        (".","").replace(",","") + "," + bookID + "," + billMonth + "," +"cash,," + userID + "\n";
                Date today = Calendar.getInstance().getTime();
                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
                SimpleDateFormat formatter2 = new SimpleDateFormat("hh:mm:ss");
                String dateNow = formatter.format(today);
                String timeNow = formatter2.format(today);
                String txtToAppend = "0," + dateNow + "," + accNum + "," + amountAppend.replace(".","").replace(",","") + "," + "CS,0," +
                    tCode + "," + timeNow + "," + billMonth + "," + accName + "," + bookID + "\n";
                buffered_writer.write(txtToAppend);
                buffered_writer.close();
                Toast.makeText(paymentOption.this, "Collections Updated", Toast.LENGTH_SHORT).show();
                Intent printingIntent = new Intent(paymentOption.this,printing.class);
                printingIntent.putExtra("method", "Cash");
                printingIntent.putExtra("tCode", tCode);
                printingIntent.putExtra("user", user);
                printingIntent.putExtra("userID", userID);
                printingIntent.putExtra("accName", accName);
                printingIntent.putExtra("accNum", accNum);
                printingIntent.putExtra("billMonth", billMonth);
                printingIntent.putExtra("amountDue", amount);
                printingIntent.putExtra("amountPaid", etCashAmt.getText().toString());
                printingIntent.putExtra("bookID", bookID);
                printingIntent.putExtra("checkNum", etCheckNum.getText().toString());
                printingIntent.putExtra("changeAmt", changeAmt);
                startActivity(printingIntent);
                finish();

            }
        }
        else if(selOption.equalsIgnoreCase("Check")){
            if(etCheckAmt.getText().toString().isEmpty()){Toast.makeText(paymentOption.this,"Please specify the check amount",Toast.LENGTH_LONG).show();

            }
            else if(etCheckNum.getText().toString().isEmpty()){
                Toast.makeText(paymentOption.this,"Please specify the check number",Toast.LENGTH_LONG).show();
            }
            else if(!cbConfirm.isChecked()){
                Toast.makeText(paymentOption.this,"Please confirm the details first",Toast.LENGTH_LONG).show();
            }
            else{
                while(month.length() < 2){
                    month = "0" + month;
                }
                while(date.length() < 2){
                    date = "0" + date;
                }
                String dateToday = year + month + date;
                BufferedWriter bw = null;
                File dir = new File(Environment.getExternalStorageDirectory(), "KGF");
                if (!dir.exists() || !dir.isDirectory())
                    dir.mkdirs();
                String path = dir.getAbsolutePath();
                String uri= path+"/collection_" + dateToday + ".txt";
                File collectionTXT = new File(uri);
                if (!collectionTXT.exists()) {
                    collectionTXT.createNewFile();
                }

                File file = new File(uri);
                FileOutputStream fileinput = new FileOutputStream(file, true);
                PrintStream printstream = new PrintStream(fileinput);
                printstream.print("");
                fileinput.close();

                OutputStreamWriter file_writer = new OutputStreamWriter(new FileOutputStream(file,true));
                BufferedWriter buffered_writer = new BufferedWriter(file_writer);
                String amountAppend = df.format(Double.parseDouble(amount));
//                String txtToAppend = accNum + "," + accName.replace(",", "") + "," +
//                        amountAppend.replace(".","").replace(",","") + "," + bookID +
//                        "," + billMonth + "," +"check," + etCheckNum.getText().toString() +"," + userID + "\n";
                Date today = Calendar.getInstance().getTime();
                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
                SimpleDateFormat formatter2 = new SimpleDateFormat("hh:mm:ss");
                String dateNow = formatter.format(today);
                String timeNow = formatter2.format(today);
                String txtToAppend = "0," + dateNow + "," + accNum + "," + amountAppend.replace(".","").replace(",","") + "," +
                        "CK," + etCheckNum.getText().toString() + "," + tCode + "," + timeNow + "," + billMonth + "," + accName + "," + bookID + "\n";
                buffered_writer.write(txtToAppend);
                buffered_writer.close();
                Toast.makeText(paymentOption.this, "Collections Updated", Toast.LENGTH_SHORT).show();
                Intent printingIntent = new Intent(paymentOption.this,printing.class);
                printingIntent.putExtra("method", "Check");
                printingIntent.putExtra("tCode", tCode);
                printingIntent.putExtra("user", user);
                printingIntent.putExtra("userID", userID);
                printingIntent.putExtra("accName", accName);
                printingIntent.putExtra("accNum", accNum);
                printingIntent.putExtra("billMonth", billMonth);
                printingIntent.putExtra("amountDue", amount);
                printingIntent.putExtra("amountPaid", etCheckAmt.getText().toString());
                printingIntent.putExtra("bookID", bookID);
                printingIntent.putExtra("changeAmt", changeAmt);
                printingIntent.putExtra("checkNum", etCheckNum.getText().toString());
                startActivity(printingIntent);
                finish();
            }
        }
    }






}
