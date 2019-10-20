package com.vedic.Retrofit;

import com.vedic.Models.InsertResponseBody;
import com.vedic.Models.ResponseBody;
import com.vedic.Utils.Constants;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface APIServices {

    @FormUrlEncoded
    @POST(Constants.FETCH_URL)
    Call<ResponseBody> fetchData(@Field(Constants.NAME_KEY) String name,
                                 @Field(Constants.USERNAME_KEY) String username,
                                 @Field(Constants.EMAIL_KEY) String email,
                                 @Field(Constants.CONTACT_KEY) Long contact);

//    @FormUrlEncoded
//    @POST(Constants.UPDATE_URL)
//    Call<ResponseBody> updateData1(@Field("user_id") String userId, String a,
//                                  @Field("name") String name,
//                                  @Field("username") String username,
//                                  @Field("email") String email,
//                                  @Field("contact") Long contact,
//                                  @Field("image_url") String imageUrl);

    @FormUrlEncoded
    @POST(Constants.UPDATE_URL)
    Call<ResponseBody> updateData(@FieldMap Map<String, String> fields);

    @FormUrlEncoded
    @POST(Constants.INSERT_URL)
    Call<InsertResponseBody> insertData(@Field(Constants.NAME_KEY) String name,
                                        @Field(Constants.USERNAME_KEY) String username,
                                        @Field(Constants.EMAIL_KEY) String email,
                                        @Field(Constants.CONTACT_KEY) Long contact,
                                        @Field(Constants.IMAGE_URL_KEY) String imageUrl);

    @Multipart
    @POST(Constants.UPLOAD_URL)
    Call<ResponseBody> uploadImage(@Part MultipartBody.Part file);

}