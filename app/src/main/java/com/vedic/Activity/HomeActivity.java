package com.vedic.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.vedic.Adapters.FetchListAdapter;
import com.vedic.Models.ResponseBody;
import com.vedic.Models.User;
import com.vedic.R;
import com.vedic.Retrofit.RetrofitAdapter;
import com.vedic.Utils.Constants;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class HomeActivity extends BaseActivity {

    private FetchListAdapter fetchListAdapter;
    private RecyclerView fetchListRecylerView;
    private List<User> userList;
    private Button search;
    private EditText searchEditText;
    private FloatingActionButton insertFab;
    private String name,email,username;
    private Long contact = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        fetchListRecylerView = findViewById(R.id.fetchList);
        search = findViewById(R.id.searchButton);
        searchEditText = findViewById(R.id.search);
        insertFab = findViewById(R.id.insertFab);

        fetchListRecylerView.setLayoutManager(new LinearLayoutManager(this));
        userList = new ArrayList<>();
        fetchListAdapter =  new FetchListAdapter(this,userList);
        fetchListRecylerView.setAdapter(fetchListAdapter);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate();
            }
        });

        insertFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(HomeActivity.this, FormActivity.class);
                i.putExtra(Constants.CALL_FROM,Constants.ADD_USER);
                startActivity(i);
            }
        });

    }

    private void validate(){

        if(searchEditText.getText().toString().trim().equals("")){
            Toast.makeText(getApplicationContext(),"Search field can't be Empty",Toast.LENGTH_LONG).show();
            return;
        }
        final String searchString = searchEditText.getText().toString().trim();

        boolean isEmail = Patterns.EMAIL_ADDRESS.matcher(searchString).matches();

        boolean isContact = Patterns.PHONE.matcher(searchString).matches();

        if(isEmail){
            email=searchString;
            callFetchData();
        }else if(isContact){
            contact = Long.parseLong(searchString);
            callFetchData();
        }else {
            final CharSequence[] items = {getResources().getString(R.string.name), getResources().getString(R.string.username)};

            final AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Search by - ");
            alert.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(items[which].equals(getResources().getString(R.string.username))){
                        username = searchString;
                        callFetchData();
                        dialog.dismiss();
                    }
                    if(items[which].equals(getResources().getString(R.string.name))){
                        name = searchString;
                        callFetchData();
                        dialog.dismiss();
                    }
                }
            });
            AlertDialog dialog = alert.create();
            dialog.show();
        }

    }

    private void callFetchData(){
        Log.e("VEDIC:","parameters:"+name+"//"+username+"//"+email+"//"+contact);

        Call<ResponseBody> call = RetrofitAdapter.getInstance().fetchData(name,username,email,contact);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if(response.body()!=null && response.body().getMetadata().getResponseCode() == 200){

                    Toast.makeText(getApplicationContext(),
                            response.body().getMetadata().getResponseText(),
                            Toast.LENGTH_SHORT).show();
                    userList.clear();
                    userList.addAll(response.body().getData());
                    fetchListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Log error here since request failed\
                Log.e("ERROR:",t.toString());
                Toast.makeText(getApplicationContext(),t.toString(),Toast.LENGTH_LONG).show();
            }
        });
        name="";email="";username="";contact=null;
    }
}
