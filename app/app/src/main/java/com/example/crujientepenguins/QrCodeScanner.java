package com.example.crujientepenguins;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.crujientepenguins.pojos.Message;
import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QrCodeScanner extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView scannerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        scannerView = new ZXingScannerView(this);

        setContentView(scannerView);
    }

    @Override
    protected void onResume() {
        super.onResume();

        scannerView.setResultHandler(this);
        scannerView.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();

        scannerView.stopCamera();
    }

    @Override
    public void handleResult(Result result) {
//        Toast.makeText(this, result.getText(), Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("auth_token", getIntent().getExtras().getString("auth_token"));

        Api.getInstance().collectOrder(
                getIntent().getExtras().getString("auth_token"),
                result.getText(),
                new Callback<Message>() {
                    @Override
                    public void onResponse(Call<Message> call, Response<Message> response) {
                        Toast.makeText(getApplicationContext(),
                                response.body().getMessage(),
                                Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                    }

                    @Override
                    public void onFailure(Call<Message> call, Throwable t) {
                        t.printStackTrace();
                    }
                }
        );
    }


}
