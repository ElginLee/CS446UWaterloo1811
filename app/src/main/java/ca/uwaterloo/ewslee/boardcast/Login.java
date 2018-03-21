package ca.uwaterloo.ewslee.boardcast;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    public void login(){
        userid = (EditText)findViewById(R.id.userid);
        password = (EditText)findViewById(R.id.password);
        loginbutton = (Button)findViewById(R.id.loginbutton);

        loginbutton.setOnClickListener(
                new View.OnClickListener(){
                    public void onClick(View v){
                        UserDAO udao = new UserDBC();
                        if(udao.login(userid.getText().toString(), password.getText().toString())){
                            Intent intent = new Intent(Login.this, MainMenuActivity.class);
                            startActivity(intent);
                        }
                        else{
                            Toast.makeText(Login.this, "Password is incorrect", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }
}
