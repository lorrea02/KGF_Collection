package com.solutions.lorrea.kgf;

import android.content.Intent;
import android.os.Environment;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    TextInputLayout tiUser, tiPass;
    String username, password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setHomeButtonEnabled(true);
        findViewById(R.id.mainLayout).requestFocus();
        File dir = new File(Environment.getExternalStorageDirectory(), "KGF");
        if (!dir.exists() || !dir.isDirectory())
            dir.mkdirs();

        tiUser = (TextInputLayout) findViewById(R.id.input_username);
        tiPass = (TextInputLayout) findViewById(R.id.input_password);
    }

    private boolean validateUsername(){
        username = tiUser.getEditText().getText().toString().trim();
        if(username.isEmpty()){
            tiUser.setError("Field cannot be empty");
            return false;
        }
        else if(username.length() > tiUser.getCounterMaxLength()){
            tiUser.setError("Exceeded maximum number of characters");
            return false;
        }
        else{
            tiUser.setError(null);
            return true;
        }
    }

    private boolean validatePassword(){
        password = tiPass.getEditText().getText().toString();
        if(password.isEmpty()){
            tiPass.setError("Field cannot be empty");
            return false;
        }
        else{
            tiPass.setError(null);
            return true;
        }
    }

    public void confirmLogin(View view) throws IOException{
        if(!validatePassword() | !validateUsername()){
            return;
        }
        userLogin();
    }

    private List<UsersList> usersList = new ArrayList<>();
    private void userLogin() throws IOException{
        File dir = new File(Environment.getExternalStorageDirectory(), "KGF");
        if (!dir.exists() || !dir.isDirectory())
            dir.mkdirs();
        String path = dir.getAbsolutePath();
        String uri= path+"/users.csv";

        File usersCSV = new File(uri);
        if(!usersCSV.exists()){
            Toast.makeText(MainActivity.this,"users.csv file not found!", Toast.LENGTH_SHORT).show();
        }
        else{
            File file = new File(uri);
            FileInputStream is = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is, Charset.forName("UTF-8"))
            );

            String line = "";
            reader.readLine();
            while ( (line=reader.readLine()) != null){
                String token[] = line.split(",");

                UsersList users = new UsersList();
                if(token[0].length() > 0 && token.length >= 4)
                    users.setUserID(token[0]);
                else
                    users.setUserID("-");

                if(token[1].length() > 0 && token.length >= 4)
                    users.setUser(token[1]);
                else
                    users.setUser("-");
                if(token[2].length() > 0 && token.length >= 4)
                    users.setPassword(token[2]);
                else
                    users.setPassword("-");
                if(token[3].length() > 0 && token.length >= 4)
                    users.setTellerCode(token[3]);
                else
                    users.setTellerCode("0");
                usersList.add(users);
            }

            username = tiUser.getEditText().getText().toString();
            password = tiPass.getEditText().getText().toString();
            String tempUser = "";
            String tempTellerCode = "";
            int ctr = 0;
            for(int i = 0; i < usersList.size();i++)
            {
                if(username.equals(usersList.get(i).getUserID()) && password.equals(usersList.get(i).getPassword()))
                {
                    tempUser = "" + usersList.get(i).getUser();
                    tempTellerCode = "" + usersList.get(i).getTellerCode();
                    break;
                }
            }
            if(tempUser.isEmpty())
                Toast.makeText(MainActivity.this,"Login Failed", Toast.LENGTH_SHORT ).show();
            else{
                tiUser.getEditText().setText("");
                tiPass.getEditText().setText("");
                Toast.makeText(MainActivity.this, "Welcome " + tempUser, Toast.LENGTH_LONG).show();
                Intent loginIntent = new Intent(MainActivity.this,serviceStarto.class);
                String strID = username;
                String strName = tempUser;
                String tCode = tempTellerCode;
                loginIntent.putExtra("userID", strID);
                loginIntent.putExtra("user", strName);
                loginIntent.putExtra("tCode", tCode);
                startActivity(loginIntent);
                finish();
            }
            
        }

    }

}
