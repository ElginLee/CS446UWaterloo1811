package ca.uwaterloo.ewslee.boardcast;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.os.StrictMode;

import controllers.UserDAO;
import controllers.UserDBC;

public class Login extends AppCompatActivity {

    private static EditText userid;
    private static EditText password;
    private static Button loginbutton;
    private static Button skipbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_main);
        login();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public void login(){
        userid = (EditText)findViewById(R.id.userid);
        password = (EditText)findViewById(R.id.password);
        loginbutton = (Button)findViewById(R.id.loginbutton);
        skipbutton = (Button)findViewById(R.id.skip);

        loginbutton.setOnClickListener(
                new View.OnClickListener(){
                    public void onClick(View v){
                        UserDAO udao = new UserDBC();
                        String uidText = userid.getText().toString();
                        if(udao.login(uidText, password.getText().toString())){
                            Intent intent = new Intent(Login.this, DrawerActivity.class);
                            intent.putExtra("userid", uidText);
                            startActivity(intent);
                            finish();
                        }
                        else{
                            Toast.makeText(Login.this, "Password is incorrect", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        skipbutton.setOnClickListener(
                new View.OnClickListener(){
                    public void onClick(View v){
                        Intent intent = new Intent(Login.this, DrawerActivity.class);
                        intent.putExtra("userid", "anonymous");
                        startActivity(intent);
                        finish();
                    }
                }
        );
    }
}
