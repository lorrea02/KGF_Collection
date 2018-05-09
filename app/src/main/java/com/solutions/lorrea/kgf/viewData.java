package com.solutions.lorrea.kgf;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class viewData extends AppCompatActivity{
    Calendar calendar = Calendar.getInstance();
    String year = "" + calendar.get(Calendar.YEAR);
    String month = "" + (calendar.get(Calendar.MONTH) + 1);
    String date = "" + calendar.get(Calendar.DATE);
    String userID, user, tCode;
    int ctr = 0;
    TextInputLayout tiAccNum, tiAccName;
    static TextView tvDate;
    static String selectedDate = "";
    String accNum, accName;
    String queryDate;
    String mDate, mAmount, mMethod, mCheckNum, mTime, mBillMonth, mBookID, mAccNum, mAccName;
    int totalMatch = 0;
    String arr[][] = new String[1000][9];
    DecimalFormat df = new DecimalFormat("#,##0.00");

    ListView listView;
    List<String> output = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_data);
        findViewById(R.id.mainLayout).requestFocus();
        getSupportActionBar().setHomeButtonEnabled(true);

        Intent iin = getIntent();
        Bundle b = iin.getExtras();
        if(b != null){
            userID = (String) b.get("userID");
            user = (String) b.get("user");
            tCode = (String) b.get("tCode");
        }



        //initialization
        tvDate = (TextView) findViewById(R.id.tvDate);
        tiAccNum = (TextInputLayout) findViewById(R.id.tiAccNum);
        tiAccName = (TextInputLayout) findViewById(R.id.tiAccName);
        listView = (ListView) findViewById(R.id.listView);


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
            Intent logoutIntent = new Intent(viewData.this,MainActivity.class);
            Toast.makeText(viewData.this,"Successfully logged out.", Toast.LENGTH_LONG).show();
            startActivity(logoutIntent);
            finish();
        }
        if(id == R.id.menuRecord){
            Intent viewIntent = new Intent(viewData.this,viewData.class);
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
//            Intent emailIntent = new Intent(record.this,emailCSV.class);
//            startActivity(emailIntent);
//            finish();

//
        }
        if(id == R.id.menuReport){
            Intent reportIntent = new Intent(viewData.this,report.class);
            reportIntent.putExtra("userID", userID);
            reportIntent.putExtra("user", user);
            reportIntent.putExtra("tCode", tCode);
            startActivity(reportIntent);
        }

        return super.onOptionsItemSelected(item);
    }



    //start coding here
    public void showDialog(View view){
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, year, month, day);
            dialog.getDatePicker().setMaxDate(c.getTimeInMillis());
            return  dialog;
        }


        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            String mMonth = "" + (month+1);
            String mDay = "" + dayOfMonth;
            if(mMonth.length()<2)
                mMonth = "0" + mMonth;
            if(mDay.length()<2)
                mDay = "0" + mDay;
            tvDate.setText("Date: "+year+""+mMonth+""+mDay);
            selectedDate = ""+year+""+mMonth+""+mDay;
        }
    }

    private boolean validateAcc(){
        accNum = tiAccNum.getEditText().getText().toString().trim();
        accName = tiAccName.getEditText().getText().toString().trim();
        if(accNum.length() != 7 && accNum.length() != 0){
            tiAccNum.setErrorEnabled(true);
            tiAccNum.setError("Must have 0 or 7 characters");
            return false;
        }
        else{
            tiAccNum.setError(null);
            tiAccNum.setErrorEnabled(false);
            return true;
        }
    }

    public void searchRecord(View view) throws IOException{
        if(!validateAcc()){
            return;
        }
        if(selectedDate.isEmpty()){
            Toast.makeText(viewData.this, "Please select a date first.", Toast.LENGTH_LONG).show();
        }
        else
            queryRecord();
    }


    private List<searchList> recordList = new ArrayList<>();
    public void queryRecord() throws IOException{
        recordList.clear();
        queryDate = selectedDate;
        File dir = new File(Environment.getExternalStorageDirectory(), "KGF");
        if (!dir.exists() || !dir.isDirectory())
            dir.mkdirs();
        String path = dir.getAbsolutePath();
        String uri= path+"/collection_"+ queryDate +".txt";

        File collectionTXT = new File(uri);

        if(!collectionTXT.exists()){
            Toast.makeText(viewData.this, "Collection file does not exist.", Toast.LENGTH_LONG).show();
        }
        else{
            File file = new File(uri);
            FileInputStream is = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is, Charset.forName("UTF-8"))
            );
            String line = "";
            while ( (line=reader.readLine()) != null) {
                String token[] = line.split(",");
                searchList SList = new searchList();

                if(token[1].length() > 0 && token.length >= 11)
                    SList.setmDate(token[1]);

                if(token[2].length() > 0 && token.length >= 11)
                    SList.setAccNum(token[2]);

                if(token[3].length() > 0 && token.length >= 11)
                    SList.setAmount(token[3]);

                if(token[4].length() > 0 && token.length >= 11)
                    SList.setMethod(token[4]);

                if(token[5].length() > 0 && token.length >= 11)
                    SList.setChecknum(token[5]);

                if(token[6].length() > 0 && token.length >= 11)
                    SList.settCode(token[6]);

                if(token[7].length() > 0 && token.length >= 11)
                    SList.setmTime(token[7]);

                if(token[8].length() > 0 && token.length >= 11)
                    SList.setBillMonth(token[8]);

                if(token[9].length() > 0 && token.length >= 11)
                    SList.setAccName(token[9]);
                if(token[10].length() > 0 && token.length >= 11)
                    SList.setBookID(token[10]);
                recordList.add(SList);
            }
        }
        ctr = 0;
        output.clear();
        totalMatch = 0;
        if(accNum.isEmpty() && accName.isEmpty()){
            for(int i = 0,j=0; i<recordList.size(); i++){
                    //mDate, mAmount, mMethod, mCheckNum, mTime, mBillMonth, mBookID, mAccNum;
                mAccNum = recordList.get(i).getAccNum();
                mAmount = recordList.get(i).getAmount();
                mDate = recordList.get(i).getmDate();
                mMethod = recordList.get(i).getMethod();
                mCheckNum = recordList.get(i).getChecknum();
                mTime = recordList.get(i).getmTime();
                mBillMonth = recordList.get(i).getBillMonth();
                mBookID = recordList.get(i).getBookID();
                mAccName = recordList.get(i).getAccName();
                String first = "", second ="";
                int amountlength = mAmount.length();
                first = mAmount.substring(0,amountlength-2);
                second = mAmount.substring(amountlength-2,amountlength);
                mAmount = first + "." + second;
                tiAccNum.setErrorEnabled(false);
                tiAccNum.setError(null);

                arr[j][0] = mAccNum;
                arr[j][1] = mBookID;
                arr[j][2] = mBillMonth;
                arr[j][3] = mAmount;
                arr[j][4] = mMethod;
                arr[j][5] = mCheckNum;
                arr[j][6] = mDate;
                arr[j][7] = mTime;
                arr[j][8] = mAccName;
                totalMatch++;
                j++;
                ctr++;
            }
            listView.setAdapter(null);
            CustomAdapter customAdapter = new CustomAdapter();
            listView.setAdapter(customAdapter);
        }
        else if(!accName.isEmpty() && !accNum.isEmpty()){
            for(int i = 0,j=0; i<recordList.size(); i++){
                if(recordList.get(i).getAccName().toLowerCase().contains(accName.toLowerCase()) &&
                        recordList.get(i).getAccNum().equals(accNum)){
                    //mDate, mAmount, mMethod, mCheckNum, mTime, mBillMonth, mBookID, mAccNum;
                    mAccNum = recordList.get(i).getAccNum();
                    mAmount = recordList.get(i).getAmount();
                    mDate = recordList.get(i).getmDate();
                    mMethod = recordList.get(i).getMethod();
                    mCheckNum = recordList.get(i).getChecknum();
                    mTime = recordList.get(i).getmTime();
                    mBillMonth = recordList.get(i).getBillMonth();
                    mBookID = recordList.get(i).getBookID();
                    mAccName = recordList.get(i).getAccName();
                    String first = "", second ="";
                    int amountlength = mAmount.length();
                    first = mAmount.substring(0,amountlength-2);
                    second = mAmount.substring(amountlength-2,amountlength);
                    mAmount = first + "." + second;
                    tiAccNum.setErrorEnabled(false);
                    tiAccNum.setError(null);

                    arr[j][0] = mAccNum;
                    arr[j][1] = mBookID;
                    arr[j][2] = mBillMonth;
                    arr[j][3] = mAmount;
                    arr[j][4] = mMethod;
                    arr[j][5] = mCheckNum;
                    arr[j][6] = mDate;
                    arr[j][7] = mTime;
                    arr[j][8] = mAccName;
                    totalMatch++;
                    j++;
                }
                ctr++;
            }
            if(totalMatch == 0){
                tiAccName.setError("Account not found");
                tiAccName.setErrorEnabled(true);
                tiAccNum.setError("Account number not found");
                tiAccNum.setErrorEnabled(true);
                listView.setAdapter(null);
            }
            else{
                tiAccName.setError(null);
                tiAccName.setErrorEnabled(false);
                tiAccNum.setError(null);
                tiAccNum.setErrorEnabled(false);
                listView.setAdapter(null);
                CustomAdapter customAdapter = new CustomAdapter();
                listView.setAdapter(customAdapter);
            }
        }

        else if(!accName.isEmpty()){
            for(int i = 0,j=0; i<recordList.size(); i++){
                if(recordList.get(i).getAccName().toLowerCase().contains(accName.toLowerCase())){
                    //mDate, mAmount, mMethod, mCheckNum, mTime, mBillMonth, mBookID, mAccNum;
                    mAccNum = recordList.get(i).getAccNum();
                    mAmount = recordList.get(i).getAmount();
                    mDate = recordList.get(i).getmDate();
                    mMethod = recordList.get(i).getMethod();
                    mCheckNum = recordList.get(i).getChecknum();
                    mTime = recordList.get(i).getmTime();
                    mBillMonth = recordList.get(i).getBillMonth();
                    mBookID = recordList.get(i).getBookID();
                    mAccName = recordList.get(i).getAccName();
                    String first = "", second ="";
                    int amountlength = mAmount.length();
                    first = mAmount.substring(0,amountlength-2);
                    second = mAmount.substring(amountlength-2,amountlength);
                    mAmount = first + "." + second;
                    tiAccNum.setErrorEnabled(false);
                    tiAccNum.setError(null);

                    arr[j][0] = mAccNum;
                    arr[j][1] = mBookID;
                    arr[j][2] = mBillMonth;
                    arr[j][3] = mAmount;
                    arr[j][4] = mMethod;
                    arr[j][5] = mCheckNum;
                    arr[j][6] = mDate;
                    arr[j][7] = mTime;
                    arr[j][8] = mAccName;
                    totalMatch++;
                    j++;
                }
                ctr++;
            }
            if(totalMatch == 0){
                tiAccName.setError("Account not found");
                tiAccName.setErrorEnabled(true);
                listView.setAdapter(null);
            }
            else{
                tiAccName.setError(null);
                tiAccName.setErrorEnabled(false);
                listView.setAdapter(null);
                CustomAdapter customAdapter = new CustomAdapter();
                listView.setAdapter(customAdapter);
            }
        }

        else{
            for(int i = 0,j=0; i<recordList.size(); i++){
                if(recordList.get(i).getAccNum().equals(accNum)){

                    //mDate, mAmount, mMethod, mCheckNum, mTime, mBillMonth, mBookID, mAccNum;
                    mAccNum = recordList.get(i).getAccNum();
                    mAmount = recordList.get(i).getAmount();
                    mDate = recordList.get(i).getmDate();
                    mMethod = recordList.get(i).getMethod();
                    mCheckNum = recordList.get(i).getChecknum();
                    mTime = recordList.get(i).getmTime();
                    mBillMonth = recordList.get(i).getBillMonth();
                    mBookID = recordList.get(i).getBookID();
                    mAccName = recordList.get(i).getAccName();
                    String first = "", second ="";
                    int amountlength = mAmount.length();
                    first = mAmount.substring(0,amountlength-2);
                    second = mAmount.substring(amountlength-2,amountlength);
                    mAmount = first + "." + second;
                    tiAccNum.setErrorEnabled(false);
                    tiAccNum.setError(null);

                    arr[j][0] = mAccNum;
                    arr[j][1] = mBookID;
                    arr[j][2] = mBillMonth;
                    arr[j][3] = mAmount;
                    arr[j][4] = mMethod;
                    arr[j][5] = mCheckNum;
                    arr[j][6] = mDate;
                    arr[j][7] = mTime;
                    arr[j][8] = mAccName;
                    totalMatch++;
                    j++;
                }
                ctr++;
            }
            if(totalMatch == 0){
                tiAccNum.setError("Account number not found");
                tiAccNum.setErrorEnabled(true);
                listView.setAdapter(null);
            }
            else{
                tiAccNum.setError(null);
                tiAccNum.setErrorEnabled(false);
                listView.setAdapter(null);
                CustomAdapter customAdapter = new CustomAdapter();
                listView.setAdapter(customAdapter);
            }
        }

//        else{
//            for(int i = 0; i < 8; i++){
//                output.add(arr[0][i]);
//            }
//        }



    }


    class CustomAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return totalMatch;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.adapter_view_layout,null);
            TextView tvlAccNo = (TextView)convertView.findViewById(R.id.tvlAccNo);
            TextView tvlBookID = (TextView)convertView.findViewById(R.id.tvlBookID);
            TextView tvlBillMonth = (TextView)convertView.findViewById(R.id.tvlBillMonth);
            TextView tvlAmount = (TextView)convertView.findViewById(R.id.tvlAmount);
            TextView tvlPayment = (TextView)convertView.findViewById(R.id.tvlPayment);
            TextView tvlCheckNo = (TextView)convertView.findViewById(R.id.tvlCheckNo);
            TextView tvlAccName = (TextView)convertView.findViewById(R.id.tvlAccName);
            Button btDel = (Button)convertView.findViewById(R.id.btlDelete);
            Button btView = (Button)convertView.findViewById(R.id.btlView);


            final String accNumReport = arr[position][0];

            btDel.setOnClickListener(new View.OnClickListener() {
                public void onClick(final View v) {
                    AlertDialog.Builder alertDlg = new AlertDialog.Builder(viewData.this);
                    alertDlg.setMessage("Are you sure you want to delete this record? Acc Num: " + accNumReport);
                    alertDlg.setCancelable(false);
                    alertDlg.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                deleteRecord(v, arr[position][0], arr[position][2]);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    alertDlg.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(viewData.this, "Wag delete daw.", Toast.LENGTH_LONG).show();
                        }
                    });

                    alertDlg.show();
                }
            });

            btView.setOnClickListener(new View.OnClickListener() {
                public void onClick(final View v) {
                  Intent intentEntry = new Intent(viewData.this, viewEntry.class);
                    intentEntry.putExtra("userID", userID);
                    intentEntry.putExtra("user", user);
                    intentEntry.putExtra("tCode", tCode);
                    intentEntry.putExtra("accNum", arr[position][0]);
                    intentEntry.putExtra("accName", arr[position][8]);
                    intentEntry.putExtra("bookID", arr[position][1]);
                    intentEntry.putExtra("billMonth", arr[position][2]);
                    intentEntry.putExtra("amount", arr[position][3]);
                    intentEntry.putExtra("paymentType", arr[position][4]);
                    intentEntry.putExtra("checkNum", arr[position][5]);
                  startActivity(intentEntry);
                /*
                arr[j][0] = mAccNum;
                    arr[j][1] = mBookID;
                    arr[j][2] = mBillMonth;
                    arr[j][3] = mAmount;
                    arr[j][4] = mMethod;
                    arr[j][5] = mCheckNum;
                    arr[j][6] = mDate;
                    arr[j][7] = mTime;
                    arr[j][8] = mAccName;
                 */

                }
            });

            tvlAccNo.setText("Account No. : " + arr[position][0]);
            tvlBookID.setText("Book ID : " + arr[position][1]);
            tvlBillMonth.setText("Bill Month : " + arr[position][2]);
            tvlAmount.setText("Amount : ₱ " + df.format(Double.parseDouble(arr[position][3])));
            tvlPayment.setText("Payment : " + arr[position][4]);
            tvlCheckNo.setText("Check No. : " + arr[position][5]);
            tvlAccName.setText("Account Name : " + arr[position][8]);
        /*
        arr[i][0] = mAccNum;
                arr[i][1] = mBookID;
                arr[i][2] = mBillMonth;
                arr[i][3] = mAmount;
                arr[i][4] = mMethod;
                arr[i][5] = mCheckNum;
                arr[i][6] = mDate;
                arr[i][7] = mTime;
         */
            return convertView;
        }

        public void deleteRecord(View view, String x, String y) throws IOException{
//            Toast.makeText(viewData.this, x, Toast.LENGTH_LONG).show();
            File dir = new File(Environment.getExternalStorageDirectory(), "KGF");
            if (!dir.exists() || !dir.isDirectory())
                dir.mkdirs();
            String path = dir.getAbsolutePath();
            String uri= path+"/collection_"+ selectedDate +".txt";
            File file = new File(uri);
            FileInputStream is = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is, Charset.forName("UTF-8"))
            );
            String line = "";
            String appendText = "";
            while ( (line=reader.readLine()) != null) {
                String token[] = line.split(",");
                if(!(token[2].equals(x) && token[8].equals(y))){
                    appendText = appendText + "" + line + "\n";
                }
            }
            String uri2 = path+"/collection_"+ selectedDate +"_bak.txt";
            File replacement = new File(uri2);
            FileOutputStream fileinput = new FileOutputStream(replacement, true);
            PrintStream printstream = new PrintStream(fileinput);
            printstream.print("");
            fileinput.close();
            OutputStreamWriter file_writer = new OutputStreamWriter(new FileOutputStream(replacement,true));
            BufferedWriter buffered_writer = new BufferedWriter(file_writer);
            buffered_writer.write(appendText);
            buffered_writer.close();
            //delete
            file.delete();
            //rename
            replacement.renameTo(file);
            //
            listView.setAdapter(null);
            Toast.makeText(viewData.this, "Record deleted", Toast.LENGTH_LONG).show();


        }
    }


}
