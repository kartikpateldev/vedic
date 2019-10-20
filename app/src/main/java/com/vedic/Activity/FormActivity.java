package com.vedic.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.vedic.Models.InsertResponseBody;
import com.vedic.Models.ResponseBody;
import com.vedic.Models.User;
import com.vedic.R;
import com.vedic.Retrofit.RetrofitAdapter;
import com.vedic.Utils.CommonUtils;
import com.vedic.Utils.Constants;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

public class FormActivity extends BaseActivity {

    private TextView heading;
    private TextInputEditText nameEditText,emailEditText,usernameEditText,contactEditText;
    private String name,email,username;
    private Long contact;
    private Button button;
    private ImageView profilePic, cameraIcon;
    private final int MY_PERMISSIONS = 0x113;
    private final int REQUEST_CAMERA = 0x114;
    private final int PICK_IMAGE = 0x115;
    private Bitmap selectedBitmap;
    private Context context;
    private String photoString, mCurrentPhotoPath="", imageURL="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        context = this;

        heading = findViewById(R.id.heading);
        cameraIcon = findViewById(R.id.cameraIcon);
        profilePic = findViewById(R.id.profilePic);
        button = findViewById(R.id.button);

        nameEditText = findViewById(R.id.name);
        nameEditText.setTag(Constants.NAME_TAG);

        usernameEditText = findViewById(R.id.username);
        usernameEditText.setTag(Constants.USERNAME_TAG);

        emailEditText = findViewById(R.id.email);
        emailEditText.setTag(Constants.EMAIL_TAG);

        contactEditText = findViewById(R.id.contact);
        contactEditText.setTag(Constants.CONTACT_TAG);

        Glide.with(this)
                .load(getDrawable(R.drawable.camera))
                .apply(RequestOptions.circleCropTransform())
                .into(cameraIcon);

        if(getIntent().getStringExtra(Constants.CALL_FROM).equals(Constants.VIEW_USER)){
            User user = (User) getIntent().getSerializableExtra(Constants.USER_DATA);
            populateUserData(user);

        }else {

            Glide.with(this)
                    .load(getDrawable(R.drawable.default_profile_pic))
                    .apply(RequestOptions.circleCropTransform())
                    .into(profilePic);
        }

        View.OnFocusChangeListener listener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.e("VEDIC:","in onFocusChange");
                if(!hasFocus){

                    TextInputEditText editText = (TextInputEditText) v;
                    switch (v.getTag().toString()){
                        case Constants.EMAIL_TAG:
                            if(!Patterns.EMAIL_ADDRESS.matcher(editText.getText().toString().trim()).matches()){
                                editText.setError(Constants.INVALID_EMAIL_ID);
                            }
                            break;
                        case Constants.CONTACT_TAG:
                            if(!Patterns.PHONE.matcher(editText.getText().toString().trim()).matches()){
                                editText.setError(Constants.INVALID_CONTACT_NUMBER);
                            }
                            break;
                        case Constants.NAME_TAG:
                            Log.e("VEDIC:","in name tag case");
                            if(editText.getText()== null || editText.getText().toString().trim().equals("")){
                                editText.setError(Constants.FIELD_CANT_BE_EMPTY);
                            }else {
                                if(mCurrentPhotoPath.equals("")){
                                    String name = editText.getText().toString().trim();
                                    String picInitial = name.substring(0,1);
                                    picInitial = picInitial + ((name.contains(" "))?name.charAt(name.indexOf(" ")+1):"");
                                    picInitial = picInitial.toUpperCase();
                                    Log.e("VEDIC:","picInitial: "+picInitial);
                                    TextDrawable drawable = TextDrawable.builder()
                                            .beginConfig()
                                            .textColor(ResourcesCompat.getColor(context.getResources(),R.color.avatarTextColor,null))
                                            .useFont(ResourcesCompat.getFont(context,R.font.montserrat))
                                            .fontSize(CommonUtils.dpToPx(75,context)) //size in px
                                            .bold()
                                            .toUpperCase()
                                            .endConfig()
                                            .buildRound(picInitial,
                                                    getResources().getColor(R.color.avatarBackgroundColor));
                                    profilePic.setImageDrawable(drawable);
                                }
                            }
                            break;
                        case Constants.USERNAME_TAG:
                            if(editText.getText()== null || editText.getText().toString().trim().equals("")){
                                editText.setError(Constants.FIELD_CANT_BE_EMPTY);
                            }
                            break;
                    }
                }
            }
        };

        nameEditText.setOnFocusChangeListener(listener);
        usernameEditText.setOnFocusChangeListener(listener);
        emailEditText.setOnFocusChangeListener(listener);
        contactEditText.setOnFocusChangeListener(listener);

        cameraIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestCameraPermissions();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mCurrentPhotoPath.trim().equals("")){
                    Toast.makeText(getApplicationContext(),
                            Constants.PROVIDE_PROFILE_PICTURE,
                            Toast.LENGTH_SHORT).show();
                }else if(!isvalidInput()){
                    Toast.makeText(getApplicationContext(),
                            "Complete the form.",
                            Toast.LENGTH_SHORT).show();
                }else{
                    Log.e("VEDIC:","calling upload api");
                    callUploadAPI();
                }
            }
        });

    }

    private boolean isvalidInput(){
        boolean isValid=true;
        if(nameEditText.getError() != null || nameEditText.getText().length() == 0){
            isValid=false;
        }else if(usernameEditText.getError() != null || usernameEditText.getText().length() == 0){
            isValid=false;
        }else if(emailEditText.getError() != null || emailEditText.getText().length() == 0){
            isValid=false;
        }else if(contactEditText.getError() != null || contactEditText.getText().length() == 0){
            isValid=false;
        }
        return isValid;
    }

    private void populateUserData(User user){
        heading.setText(String.format("%s's Profile",user.getName()));
        changeEditTextView(false);
        button.setText(getString(R.string.edit));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeEditTextView(true);
                button.setText(getString(R.string.update_data));
            }
        });
        
        
        nameEditText.setText(user.getName());
        usernameEditText.setText(user.getUsername());
        emailEditText.setText(user.getEmail());
        contactEditText.setText(String.format(Locale.getDefault(),"%d",user.getContact()));
        Glide.with(this)
                .load(user.getImageUrl())
                .placeholder(getDrawable(R.drawable.default_profile_pic))
                .apply(RequestOptions.circleCropTransform())
                .into(profilePic);
    }

    private void changeEditTextView(boolean enable){
        nameEditText.setEnabled(enable);
        usernameEditText.setEnabled(enable);
        emailEditText.setEnabled(enable);
        contactEditText.setEnabled(enable);
    }

    private void requestCameraPermissions(){
        String[] permissionArray = new String[2];
        int flag = 0;

        if (ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED )
        {
            permissionArray[0] = Manifest.permission.CAMERA;
            flag++;
        }
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
        {
            permissionArray[1] = Manifest.permission.WRITE_EXTERNAL_STORAGE;
            flag++;
        }

        if (flag>0)
        {
            ActivityCompat.requestPermissions(this,permissionArray, MY_PERMISSIONS);
        }
        else
        {
            showAlertDialog();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! do the
                    showAlertDialog();
                } else {
                    Toast.makeText(this, "permissions denied", Toast.LENGTH_LONG).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                break;
            }
            // other 'switch' lines to check for other
            // permissions this app might request
        }
    }

    private void showAlertDialog(){
        final CharSequence[] items = getResources().getStringArray(R.array.choose_profile_photo);
        AlertDialog.Builder builder = new AlertDialog.Builder(FormActivity.this);
//        builder.setTitle("Pick Image");
        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals(getString(R.string.take_photo))) {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    // Ensure that there's a camera activity to handle the intent
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        // Create the File where the photo should go
                        File photoFile = null;
                        try {
                            photoFile = createImageFile();
                        } catch (IOException ex) {
                            // Error occurred while creating the File
                            ex.printStackTrace();
                        }
                        // Continue only if the File was successfully created
                        if (photoFile != null) {
                            Uri photoURI = FileProvider.getUriForFile(FormActivity.this,
                                    "com.vedic.fileprovider",
                                    photoFile);
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                            startActivityForResult(takePictureIntent, REQUEST_CAMERA);
                        }
                    }
                } else if (items[item].equals(getString(R.string.choose_from_gallery))) {
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(galleryIntent, PICK_IMAGE);

                } else if (items[item].equals(getString(R.string.choose_from_uploaded_images))) {
                    dialog.dismiss();
//                    startActivity(new Intent());
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CAMERA:
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        File file = new File(mCurrentPhotoPath);
                        selectedBitmap = MediaStore.Images.Media
                                .getBitmap(getContentResolver(), Uri.fromFile(file));
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        scaleImage();
                        selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
                        photoString = Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT);

                        Glide.with(this)
                                .load(selectedBitmap)
                                .apply(RequestOptions.circleCropTransform())
                                .into(profilePic);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case PICK_IMAGE:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    try{
                        Uri picUri = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        Cursor cursor = getContentResolver().query(picUri,
                                filePathColumn, null, null, null);
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String filePath = cursor.getString(columnIndex);
                        mCurrentPhotoPath = filePath;
                        Log.e("VEDIC:","image filePath:-"+filePath);
                        cursor.close();

                        selectedBitmap = BitmapFactory.decodeFile(filePath);

                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 2;

                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
                        scaleImage();
                        photoString = Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP);
                    }catch (Exception e){
                        Log.e("VEDIC:",e.toString());
                    }

                    Glide.with(this)
                            .load(selectedBitmap)
                            .apply(RequestOptions.circleCropTransform())
                            .into(profilePic);
                }
                break;
        }
    }

    private void scaleImage() {
        //scaling bitmap to the ratio to maintain original scaling
        float width = selectedBitmap.getWidth();
        float height = selectedBitmap.getHeight();

        float widthByHeightRatio = width / height;
        float heightByWidthRatio = height / width;

        if (height > width) {
            selectedBitmap = Bitmap.createScaledBitmap(selectedBitmap, 900, (int) (heightByWidthRatio * 900), false);
        } else {
            selectedBitmap = Bitmap.createScaledBitmap(selectedBitmap, (int) (widthByHeightRatio * 900), 900, false);
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String imageFileName = "photo";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void callUploadAPI(){
        try{
            File file = new File(mCurrentPhotoPath);
            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);

            MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("image", file.getName(), requestBody);

            // for filename
//            RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());

            Call<ResponseBody> call = RetrofitAdapter.getInstance().uploadImage(fileToUpload);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                    if(response.body()!=null && response.body().getMetadata().getResponseCode() == 200){

                        ResponseBody responseBody = response.body();

//                        Toast.makeText(getApplicationContext(),
//                                responseBody.getMetadata().getResponseText()+", "+responseBody.getUrl().get(0),
//                                Toast.LENGTH_SHORT).show();

                        Set<String> stringSet = new HashSet<>();
                        stringSet.add(responseBody.getUrl().get(0));

                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(FormActivity.this);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putStringSet(Constants.UPLOADED_IMAGES, stringSet);
                        editor.apply();

                        imageURL = responseBody.getUrl().get(0);
                        Log.e("VEDIC:","calling insert api");
                        callInsertAPI();

                    }else if(response.body().getMetadata().getResponseCode() != 200){

                        Toast.makeText(getApplicationContext(),
                                response.body().getMetadata().getResponseCode()+": "+
                                        response.body().getMetadata().getResponseText(),
                                Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    // Log error here since request failed\
                    Log.e("ERROR:",t.toString());
                    Toast.makeText(getApplicationContext(),t.toString(),Toast.LENGTH_LONG).show();
                }
            });
        }catch (Exception e){

            Log.e("VEDIC:",e.toString());
        }
    }

    private void callInsertAPI(){
        name = nameEditText.getText().toString();
        username = usernameEditText.getText().toString();
        email = emailEditText.getText().toString();
        contact = Long.parseLong(contactEditText.getText().toString());
        try{
            Call<InsertResponseBody> call = RetrofitAdapter.getInstance().insertData(name,username,email,contact,imageURL);
            call.enqueue(new Callback<InsertResponseBody>() {
                @Override
                public void onResponse(Call<InsertResponseBody> call, retrofit2.Response<InsertResponseBody> response) {
                    if(response.body()!=null && response.body().getMetadata().getResponseCode() == 200){

                        InsertResponseBody responseBody = response.body();

                        Toast.makeText(getApplicationContext(),
                                responseBody.getMetadata().getResponseText(),
                                Toast.LENGTH_SHORT).show();



                    }else if(response.body().getMetadata().getResponseCode() != 200){

                        Toast.makeText(getApplicationContext(),
                                response.body().getMetadata().getResponseCode()+": "+
                                        response.body().getMetadata().getResponseText(),
                                Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<InsertResponseBody> call, Throwable t) {
                    // Log error here since request failed\
                    Log.e("ERROR:",t.toString());
                    Toast.makeText(getApplicationContext(),t.toString(),Toast.LENGTH_LONG).show();
                }
            });
        }catch (Exception e){

            Log.e("VEDIC:",e.toString());
        }
        //clearAllVariables();
    }
//    private void clearAllVariables(){
//        mCurrentPhotoPath="";
//        imageURL="";
//    }
}