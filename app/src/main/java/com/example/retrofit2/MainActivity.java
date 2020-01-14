package com.example.retrofit2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.retrofit2.Interfaces.InternetConnectionListener;
import com.example.retrofit2.Models.AccountInfo;
import com.example.retrofit2.Utils.App;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements InternetConnectionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((App)getApplication()).setmInternetConnectionListener(this);

        getAccountInfo();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ((App)getApplication()).removeInternetConnectionListener();
    }

    private void getAccountInfo(){

        ((App)getApplication()).getApiService().getAccountInfo("id")
                .enqueue(new Callback<AccountInfo>() {
                    @Override
                    public void onResponse(Call<AccountInfo> call, Response<AccountInfo> response) {

                        // return to UI thread
                        // display AccountInfo on UI

                    }

                    @Override
                    public void onFailure(Call<AccountInfo> call, Throwable t) {

                        // skip for now
                    }
                });
    }

    @Override
    public void onInternetUnavailable() {

        // hide content UI
        // show No Internet Connection UI
    }

    @Override
    public void onCacheUnavailable() {

        // no content to show
        // hide content UI
        // show a full covered UI saying 'No Internet'
        // or 'No Content Available'
    }
}
