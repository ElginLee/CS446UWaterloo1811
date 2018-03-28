package ca.uwaterloo.ewslee.boardcast;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.util.ArrayList;

import controllers.GradebookDAO;
import controllers.GradebookDBC;
import controllers.UserDAO;
import controllers.UserDBC;
import de.codecrafters.tableview.*;
import de.codecrafters.tableview.toolkit.*;
/**
 * Created by Elgin on 24/3/2018.
 */

public class DrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String[][] DATA_TO_SHOW = { { "This", "is", "a", "test" },
            { "and", "a", "second", "test" } };
    private static final String[] TABLE_HEADERS = { "Quiz", "Score" };

    TableView<String[]> tableView;
    PieChart mPieChart;
    TextView content_textView;
    LinearLayout registration;
    TextInputLayout email;
    TextInputLayout userid;
    TextInputLayout password;
    private String loginUser = "";
    DrawerLayout drawer;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_main);

        tableView = (TableView<String[]>) findViewById(R.id.tableView);
        mPieChart = (PieChart) findViewById(R.id.contentPieChart);
        content_textView = (TextView) findViewById(R.id.content_textView);
        registration = (LinearLayout) findViewById(R.id.registration_layout);
        email  = (TextInputLayout) findViewById(R.id.email_layout);
        userid  = (TextInputLayout) findViewById(R.id.userid_layout);
        password  = (TextInputLayout) findViewById(R.id.password_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        NavigationView navigationViewRight = (NavigationView) findViewById(R.id.nav_view_right);
        navigationViewRight.setNavigationItemSelectedListener(this);
        navigationView.setNavigationItemSelectedListener(this);

        configureDisplayID();

        GradebookDAO gdbc = new GradebookDBC();
        ArrayList<String[]> grades = gdbc.getRecent(getIntent().getStringExtra("userid"));
        String[][] gradeTableInput = new String[grades.size()][2];
        int i = 0;
        int pass = 0;
        int fail = 0;
        for (String[] str : grades) {
            gradeTableInput[i][0] = str[0];
            gradeTableInput[i][1] = str[1];
            String[] parts = str[1].split("/");
            if(Integer.parseInt(parts[0]) > (Integer.parseInt(parts[1]) / 2)){
                pass++;
            }else{
                fail++;
            }
            i++;
        }


        if(!getIntent().getStringExtra("userid").equals("anonymous")) {
            tableView.setDataAdapter(new SimpleTableDataAdapter(this, gradeTableInput));
            tableView.setHeaderAdapter(new SimpleTableHeaderAdapter(this, TABLE_HEADERS));
            int colorEvenRows = getResources().getColor(R.color.white);
            int colorOddRows = getResources().getColor(R.color.gray);
            tableView.setDataRowBackgroundProvider(TableDataRowBackgroundProviders.alternatingRowColors(colorEvenRows, colorOddRows));

            mPieChart.addPieSlice(new PieModel("Pass", pass, Color.parseColor("#80ff80")));
            mPieChart.addPieSlice(new PieModel("Fail", fail, Color.parseColor("#FF5733")));
            content_textView.setVisibility(View.INVISIBLE);
            registration.setVisibility(View.INVISIBLE);
        }else{
            content_textView.setText("Register to save results to cloud!");
            tableView.setVisibility(View.INVISIBLE);
            mPieChart.setVisibility(View.INVISIBLE);

            Button register = (Button) findViewById(R.id.register_button);

            register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserDAO userRegister = new UserDBC();
                    userRegister.insertUser(userid.getEditText().getText().toString(), password.getEditText().getText().toString(),email.getEditText().getText().toString());
                    Intent intent = new Intent(DrawerActivity.this, Login.class);
                    intent.putExtra("userid", "anonymous");
                    startActivity(intent);
                }
            });


        }


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_right_menu) {
            if (drawer.isDrawerOpen(GravityCompat.END)) {
                drawer.closeDrawer(GravityCompat.END);
            } else {
                drawer.openDrawer(GravityCompat.END);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_host) {
            Intent intent = new Intent(DrawerActivity.this,HostSessionActivity.class);
            intent.putExtra("userid", loginUser);
            startActivity(intent);
        } else if (id == R.id.nav_join) {
            Intent intent = new Intent(DrawerActivity.this, JoinSessionActivity.class);
            intent.putExtra("userid", loginUser);
            startActivity(intent);
        } else if (id == R.id.nav_edit) {
            Intent intent = new Intent(DrawerActivity.this, QuestionCreationActivity.class);
            intent.putExtra("userid", loginUser);
            startActivity(intent);
        } else if (id == R.id.nav_manage) {
            Intent intent = new Intent(DrawerActivity.this, GradebookActivity.class);
            intent.putExtra("userid", loginUser);
            startActivity(intent);
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void configureDisplayID(){
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView userid = (TextView)headerView.findViewById(R.id.nameLabel);
        loginUser = getIntent().getStringExtra("userid");
        userid.setText(loginUser);
    }
}
