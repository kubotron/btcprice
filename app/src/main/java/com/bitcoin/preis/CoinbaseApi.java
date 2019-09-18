package com.bitcoin.preis;

import com.bitcoin.preis.model.PriceResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CoinbaseApi {

    @GET("prices/spot")
    Call<PriceResponse> getPrice(@Query("currency") String currency);

}
