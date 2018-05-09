package com.solutions.lorrea.kgf;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;
import java.util.Calendar;

public class record extends AppCompatActivity {
    String userID, user, accNum, accName, amount, bookID, billMonth,tCode;
    TextInputLayout tiAccNum, tiAccName, tiAmt, tiBookID, tiBillMonth;
    Calendar calendar = Calendar.getInstance();
    String year = "" + calendar.get(Calendar.YEAR);
    String month = "" + (calendar.get(Calendar.MONTH) + 1);
    String date = "" + calendar.get(Calendar.DATE);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        findViewById(R.id.mainLayout).requestFocus();
        Intent iin= getIntent();
        Bundle b = iin.getExtras();

        if(b != null){
            userID = (String) b.get("userID");
            user = (String) b.get("user");
            tCode = (String) b.get("tCode");
        }

        tiAccNum = (TextInputLayout) findViewById(R.id.tiAccNum);
        tiAccName = (TextInputLayout) findViewById(R.id.tiAccName);
        tiAmt = (TextInputLayout) findViewById(R.id.tiAmt);
        tiBookID = (TextInputLayout) findViewById(R.id.tiBookID);
        tiBillMonth = (TextInputLayout) findViewById(R.id.tiBillMonth);






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
            Intent logoutIntent = new Intent(record.this,MainActivity.class);
            Toast.makeText(record.this,"Successfully logged out.", Toast.LENGTH_LONG).show();
            startActivity(logoutIntent);
            finish();
        }
        if(id == R.id.menuRecord){
            Intent viewIntent = new Intent(record.this,viewData.class);
            viewIntent.putExtra("userID", userID);
            viewIntent.putExtra("user", user);
            viewIntent.putExtra("tCode", tCode);
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
//            Intent emailIntent = new Intent(record.this,emailCSV.class);
//            startActivity(emailIntent);
//            finish();

//
        }
        if(id == R.id.menuReport){
            Intent reportIntent = new Intent(record.this,report.class);
            reportIntent.putExtra("userID", userID);
            reportIntent.putExtra("user", user);
            reportIntent.putExtra("tCode", tCode);
            startActivity(reportIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean validateAccNum(){
        accNum = tiAccNum.getEditText().getText().toString().trim();
        if(accNum.isEmpty()){
            tiAccNum.setError("Field cannot be empty");
            tiAccNum.setErrorEnabled(true);
            return false;
        }
        else if(accNum.length() != tiAccNum.getCounterMaxLength()){
            tiAccNum.setError("Field should have 7 characters");
            tiAccNum.setErrorEnabled(true);
            return false;
        }
        else{
            tiAccNum.setError(null);
            tiAccNum.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateAccName(){
        accName = tiAccName.getEditText().getText().toString().trim();
        if(accName.isEmpty()){
            tiAccName.setError("Field cannot be empty");
            tiAccName.setErrorEnabled(true);
            return false;
        }
        else{
            tiAccName.setError(null);
            tiAccName.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateAmt(){
        amount = tiAmt.getEditText().getText().toString().trim();
        if(amount.isEmpty()){
            tiAmt.setError("Field cannot be empty");
            tiAmt.setErrorEnabled(true);
            return false;
        }
        else{
            tiAmt.setError(null);
            tiAmt.setErrorEnabled(false);
            return true;
        }
    }

//    private boolean validatebookID(){
//        bookID = tiBookID.getEditText().getText().toString().trim();
//        if(bookID.isEmpty()){
//            tiBookID.setError("Field cannot be empty");
//            tiBookID.setErrorEnabled(true);
//            return false;
//        }
//        else{
//            tiBookID.setError(null);
//            tiBookID.setErrorEnabled(false);
//            return true;
//        }
//    }

    private boolean validateBillMonth(){
        billMonth = tiBillMonth.getEditText().getText().toString().trim();
        if(billMonth.isEmpty()){
            tiBillMonth.setError("Field cannot be empty");
            tiBillMonth.setErrorEnabled(true);
            return false;
        }
        else{
            tiBillMonth.setError(null);
            tiBillMonth.setErrorEnabled(false);
            return true;
        }
    }

    public void confirmCollection(View view){
        bookID = tiBookID.getEditText().getText().toString().trim();
        if(bookID.isEmpty()){
            bookID = "0";
        }
        if(!validateAccName() | !validateAccNum() | !validateAmt() | !validateBillMonth()){
            return;
        }
        proceedPayment();
    }

    private void proceedPayment(){
        Intent proceedPayment = new Intent(record.this,paymentOption.class);
        proceedPayment.putExtra("userID", userID);
        proceedPayment.putExtra("user", user);
        proceedPayment.putExtra("accNum", accNum);
        proceedPayment.putExtra("accName", accName);
        proceedPayment.putExtra("amount", amount);
        proceedPayment.putExtra("bookID", bookID);
        proceedPayment.putExtra("billMonth", billMonth);
        proceedPayment.putExtra("tCode", tCode);
        startActivity(proceedPayment);
    }
}
