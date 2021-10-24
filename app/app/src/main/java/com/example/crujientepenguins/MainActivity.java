package com.example.crujientepenguins;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.crujientepenguins.databinding.ActivityMainBinding;
import com.example.crujientepenguins.pojos.LoginProfile;
import com.example.crujientepenguins.pojos.LoginToken;
import com.example.crujientepenguins.pojos.PointsAvailable;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    protected LoginToken sessionToken;

    private LoginProfile login = new LoginProfile("test", "test");

    private UserService service;
    private UserServicePoints pointsService;
//    private UserRepo repo = new UserRepo()
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hitApiCall();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        getPointsAvailable();


        //noinspection SimplifiableIfStatement

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void hitApiCall() {
        Api.getInstance().login(login, new Callback<LoginToken>() {
            @Override
            public void onResponse
                    (@NonNull Call <LoginToken> call, @NonNull Response <LoginToken> response){
                sessionToken = response.body();
                System.out.println(sessionToken.getToken());
                Toast.makeText(getApplicationContext(), "Logged In", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure (@NonNull Call <LoginToken> call, @NonNull Throwable t){
//                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getPointsAvailable() {
        Api.getInstance().getPoints(sessionToken.getToken(), new Callback<PointsAvailable>() {
            @Override
            public void onResponse
                    (@NonNull Call <PointsAvailable> call, @NonNull Response <PointsAvailable> response){
                PointsAvailable points = response.body();
                System.out.println(response.body());
                Toast.makeText(getApplicationContext(), points.getPointsAvailable(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure (@NonNull Call <PointsAvailable> call, @NonNull Throwable t){
//                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

}