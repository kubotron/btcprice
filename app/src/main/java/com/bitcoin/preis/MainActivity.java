package com.bitcoin.preis;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bitcoin.preis.model.PriceResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.DateFormat;
import java.util.Date;

import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements Callback<PriceResponse> {

    private static final String TAG = MainActivity.class.getSimpleName();

    private final String CURRENCY_CODE = "CHF";
    private final String BITCOIN_CODE = "BTC";
    private final String BASE_URL = "https://api.coinbase.com/v2/";

    private TextView price;
    private TextView error;
    private TextView dateTime;
    private Button refreshButton;

    private CoinbaseApi coinbaseApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        price = findViewById(R.id.bitcoin_price_view);
        error = findViewById(R.id.error);
        dateTime = findViewById(R.id.last_updated);
        refreshButton = findViewById(R.id.refresh_button);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestPrice();
    }

    @Override
    public void onResponse(Call<PriceResponse> call, Response<PriceResponse> response) {
        Log.d(TAG, "got response from: " + call.request().url());
        updatePrice(response);
    }

    @Override
    public void onFailure(Call<PriceResponse> call, Throwable t) {
        Log.e(TAG, "request to " + call.request().url() + " failed", t);
        showError();
    }

    private void init() {
        initApi();
        initButton();
    }

    private void initApi() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        coinbaseApi = retrofit.create(CoinbaseApi.class);
    }

    private void initButton() {
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "will refresh price");
                price.setText(R.string.loading_hint);
                dateTime.setVisibility(View.GONE);
                requestPrice();
            }
        });
    }

    private void requestPrice() {
        if (coinbaseApi == null) {
            Log.e(TAG, "requesting price from unitialized API");
            return;
        }
        Call<PriceResponse> call = coinbaseApi.getPrice(CURRENCY_CODE);
        call.enqueue(this);
    }

    private void updatePrice(Response<PriceResponse> response) {
        if (response == null || response.body() == null || response.body().data == null) {
            Log.e(TAG, "unexpected response");
            showError();
            return;
        }
        hideError();
        String amountInChf = Float.toString(response.body().data.amount);
        price.setText(amountInChf + " " + CURRENCY_CODE + "/" + BITCOIN_CODE);

        String currentDateTime = DateFormat.getDateTimeInstance().format(new Date());
        String lastUpdated = getResources().getString(R.string.last_updated_hint) + " "
                + " " + currentDateTime;
        dateTime.setText(lastUpdated);
        dateTime.setVisibility(View.VISIBLE);

        Log.d(TAG, "updated price: " + amountInChf + CURRENCY_CODE + " at " + currentDateTime);
    }

    private void showError() {
        error.setVisibility(View.VISIBLE);
    }

    private void hideError() {
        error.setVisibility(View.GONE);
    }
}
