package ca.uwaterloo.ewslee.boardcast;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.content.Intent;
import android.widget.Button;

import com.google.android.gms.nearby.Nearby;

/**
 * Created by Elgin on 3/2/2018.
 */

public class MainMenuActivity extends AppCompatActivity {
    protected void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.main_menu);

        configureHostButton();
        configureJoinButton();
        configurePollButton();
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
    private void configureJoinButton(){
        Button hostBtn = (Button) findViewById(R.id.joinBtn);
        hostBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainMenuActivity.this,JoinSessionActivity.class));
            }
        });
    }

    private void configurePollButton(){
        Button hostBtn = (Button) findViewById(R.id.createPollBtn);
        hostBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainMenuActivity.this,tempActivity.class));
            }
        });
    }
}
