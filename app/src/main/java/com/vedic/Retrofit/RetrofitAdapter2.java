package com.vedic.Retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.vedic.Utils.Constants.BASE_URL;

public class RetrofitAdapter2 {

    public static APIServices mAPIService = null;

    public static APIServices callWithoutToken(){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mAPIService = retrofit.create(APIServices.class);

        return mAPIService;

    }
}
