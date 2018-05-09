package com.solutions.lorrea.kgf;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class report extends AppCompatActivity {
    String userID, user, tCode;
    String strCash, strCheck, strCollection;
    Double cash = 0.00, check = 0.00, collection = 0.00, totalCash = 0.00, totalCheck = 0.00;
    int transNum;
    TextView tvDate, tvTransNum, tvCash, tvCheck, tvCollection;
    Calendar calendar = Calendar.getInstance();
    String year = "" + calendar.get(Calendar.YEAR);
    String month = "" + (calendar.get(Calendar.MONTH) + 1);
    String date = "" + calendar.get(Calendar.DATE);
    DecimalFormat df = new DecimalFormat("#,##0.00");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        getSupportActionBar().setHomeButtonEnabled(true);

        Intent iin = getIntent();
        Bundle b = iin.getExtras();
        if(b != null){
            userID = (String) b.get("userID");
            user = (String) b.get("user");
            tCode = (String) b.get("tCode");
        }

        tvDate = (TextView) findViewById(R.id.tvDate);
        tvTransNum = (TextView) findViewById(R.id.tvTransNum);
        tvCash = (TextView) findViewById(R.id.tvCash);
        tvCheck = (TextView) findViewById(R.id.tvCheck);
        tvCollection = (TextView) findViewById(R.id.tvCollection);
        try {
            getReportData();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            Intent logoutIntent = new Intent(report.this,MainActivity.class);
            Toast.makeText(report.this,"Successfully logged out.", Toast.LENGTH_LONG).show();
            startActivity(logoutIntent);
            finish();
        }
        if(id == R.id.menuRecord){
            Intent viewIntent = new Intent(report.this,viewData.class);
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
            String to[] = {"asd@gmail.com"};
            emailIntent .putExtra(Intent.EXTRA_EMAIL, to);
            emailIntent .putExtra(Intent.EXTRA_STREAM, path);
            emailIntent .putExtra(Intent.EXTRA_SUBJECT, "" + dateToday + " Collection");
            startActivity(Intent.createChooser(emailIntent , "Send email..."));
        }

        return super.onOptionsItemSelected(item);
    }

    public void returnRecords(View view){
        Intent backToRecords = new Intent(report.this,record.class);
        backToRecords.putExtra("userID", userID);
        backToRecords.putExtra("user", user);
        backToRecords.putExtra("tCode", tCode);
        startActivity(backToRecords);
        finish();
    }

    private List<CollectionList> collectionList = new ArrayList<>();

    public void getReportData() throws IOException{
        if(month.length() < 2)
            month = "0" + month;
        if(date.length() < 2)
            date = "0" + date;
        String dateToday = year+month+date;
        File dir = new File(Environment.getExternalStorageDirectory(), "KGF");
        if (!dir.exists() || !dir.isDirectory())
            dir.mkdirs();
        String path = dir.getAbsolutePath();
        String uri= path+"/collection_"+ dateToday +".txt";

        File collectionTXT = new File(uri);
        if(!collectionTXT.exists()){
            Toast.makeText(report.this,"Collection file not found!", Toast.LENGTH_SHORT).show();
            totalCash = 0.00;
            totalCheck = 0.00;
            transNum = 0;
            collection = 0.00;
            String textCash = "₱ 0.00" ;
            tvCash.setText(textCash);
            tvCheck.setText("₱ 0.00");
            tvCollection.setText("₱ 0.00");
            tvTransNum.setText("" + transNum);
            Date today = Calendar.getInstance().getTime();
            SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");
            String dateNow = formatter.format(today);
            tvDate.setText(dateNow);
        }
        else{
            File file = new File(uri);
            FileInputStream is = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is, Charset.forName("UTF-8"))
            );
            String line = "";
            while ( (line=reader.readLine()) != null){
                String token[] = line.split(",");
//                private String 0accNum, 1accName, 2amount, 3bookID, 4billMonth, 5paymentType, 6checknum, 7userID;
                CollectionList collections = new CollectionList();
                //amount
                if(token[3].length() > 0 && token.length >= 11)
                    collections.setAmount(token[3]);
                else
                    collections.setAmount("0");

                //type
                if(token[4].equalsIgnoreCase("CS") && token.length >= 11)
                    collections.setPaymentType("cash");
                else if(token[4].equalsIgnoreCase("CK") && token.length >= 11)
                    collections.setPaymentType("check");
                collectionList.add(collections);
            }


            for(int i = 0; i < collectionList.size();i++)
            {
                if(collectionList.get(i).getPaymentType().equalsIgnoreCase("cash"))
                {
                    strCash = collectionList.get(i).getAmount();
                    strCash = strCash.replace(".", "");
                    int cashlength = strCash.length();
                    String first = "", second ="";
                    first = strCash.substring(0,cashlength-2);
                    second = strCash.substring(cashlength-2,cashlength);
                    strCash = first + "." + second;
                    cash = Double.parseDouble(strCash);
                    totalCash = totalCash + cash;
                    collection = collection + cash;
                }
                else if(collectionList.get(i).getPaymentType().equalsIgnoreCase("check"))
                {

                    strCheck = collectionList.get(i).getAmount();
                    strCheck = strCheck.replace(".", "");
                    int checklength = strCheck.length();
                    String first = "", second ="";
                    first = strCheck.substring(0,checklength-2);
                    second = strCheck.substring(checklength-2,checklength);
                    strCheck = first + "." + second;
                    check = Double.parseDouble(strCheck);
                    totalCheck = totalCheck + check;
                    collection = collection + check;
                }
                transNum++;
            }
            tvCash.setText("₱ " + df.format(totalCash));
            tvCheck.setText("₱ " + df.format(totalCheck));
            tvCollection.setText("₱ " + df.format(collection));
            tvTransNum.setText("" + transNum);
            Date today = Calendar.getInstance().getTime();
            SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");
            String dateNow = formatter.format(today);
            tvDate.setText(dateNow);
        }

    }


}
