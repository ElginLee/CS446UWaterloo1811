package ca.uwaterloo.ewslee.boardcast;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.content.Intent;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.nearby.Nearby;

/**
 * Created by Elgin on 3/2/2018.
 */

public class MainMenuActivity extends AppCompatActivity {
    private String loginUser = "";
    protected void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.main_menu);

        configureDisplayID();
        configureHostButton();
        configureJoinButton();
        configureDrawButton();
        configurePollButton();
        configureQuestionButton();
        configureGradebookButton();
    }

    private void configureDisplayID(){
        TextView userid = (TextView)findViewById(R.id.userid);
        loginUser = getIntent().getStringExtra("userid");
        userid.setText(loginUser);
    }

    private void configureHostButton(){
        Button hostBtn = (Button) findViewById(R.id.hostBtn);
        hostBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainMenuActivity.this,HostSessionActivity.class));
            }
        });
    }
    private void configureJoinButton() {
        Button hostBtn = (Button) findViewById(R.id.joinBtn);
        hostBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainMenuActivity.this, JoinSessionActivity.class));
            }
        });
    }
    private void configurePollButton() {
        Button hostBtn = (Button) findViewById(R.id.createPollBtn);
        hostBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
            }
        });
    }
    private void configureDrawButton(){
        Button drawBtn = (Button) findViewById(R.id.draw_button);
        drawBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainMenuActivity.this,DrawingBoard.class));
            }
        });
    }
    private void configureQuestionButton(){
        Button questionBtn = (Button) findViewById(R.id.question_button);
        questionBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainMenuActivity.this, QuestionCreationActivity.class));
            }
        });
    }

    private void configureGradebookButton(){
        Button gradebookBtn = (Button) findViewById(R.id.gradebook_button);
        gradebookBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainMenuActivity.this, GradebookActivity.class);
                intent.putExtra("userid", loginUser);
                startActivity(intent);

            }
        });
        if(loginUser.equals("anonymous")) {
            gradebookBtn.setVisibility(View.GONE);
        }
    }
}
