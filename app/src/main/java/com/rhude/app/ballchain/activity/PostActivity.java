package com.rhude.app.ballchain.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.kbeanie.multipicker.api.ImagePicker;
import com.kbeanie.multipicker.api.Picker;
import com.kbeanie.multipicker.api.callbacks.ImagePickerCallback;
import com.kbeanie.multipicker.api.entity.ChosenImage;
import com.rhude.app.ballchain.FileHelper;
import com.rhude.app.ballchain.R;
import com.rhude.app.ballchain.network.WordpressApi;
import com.rhude.app.ballchain.value.AppValues;

import net.steamcrafted.materialiconlib.MaterialMenuInflater;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by sean on 2018-02-15.
 */

public class PostActivity extends AppCompatActivity {
    Toolbar toolbar;
    ImageView imageView;
    EditText editText;
    View selectPhotoHolder;

    ImagePicker imagePicker;
    Uri imageUri;

    //<editor-fold desc="Lifecycle">
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        setupView();
        setupToolbar();

        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null && type.startsWith("image/")) {
            setupImage((Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM));
        } else {
            // Handle other intents, such as being started from the home screen
            setupPhotoSelect();
        }

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 54);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Picker.PICK_IMAGE_DEVICE:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    if (imagePicker == null) {
                        imagePicker = new ImagePicker(this);
                        imagePicker.setImagePickerCallback(imageCallback);
                    }
                    imagePicker.submit(data);
                }
                break;
        }
    }
    //</editor-fold>

    //<editor-fold desc="Option Menu">
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MaterialMenuInflater.with(this)
                .setDefaultColorResource(android.R.color.white)
                .inflate(R.menu.menu_post, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_send:
                postImage();
                break;
            case android.R.id.home:
                finish();
                break;
        }

        return true;
    }
    //</editor-fold>

    private void setupView() {
        toolbar = findViewById(R.id.toolbar);
        imageView = findViewById(R.id.imageView);
        editText = findViewById(R.id.editText);
        selectPhotoHolder = findViewById(R.id.selectImageHolder);

        selectPhotoHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imagePicker = new ImagePicker(PostActivity.this);
                imagePicker.setImagePickerCallback(imageCallback);
                imagePicker.shouldGenerateMetadata(false);
                imagePicker.shouldGenerateThumbnails(false);
                imagePicker.pickImage();
            }
        });
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        getSupportActionBar().setTitle("Post Image");
    }

    void setupImage(Uri imageUri) {
        Log.i("setupImage", String.format("imageUri:  %s", imageUri));
        if (imageUri != null) {
            this.imageUri = imageUri;
            // Update UI to reflect image being shared
            imageView.setImageURI(imageUri);
            selectPhotoHolder.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
        } else {
            setupPhotoSelect();
        }
    }

    void setupPhotoSelect() {
        selectPhotoHolder.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.GONE);
    }

    void postImage() {
        if (imageUri == null) {
            Toast.makeText(this, "You must select a photo first", Toast.LENGTH_LONG).show();
            return;
        }

        String realPath = FileHelper.getImagePathFromInputStreamUri(this, imageUri);
        File file = new File(realPath);

        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        // finally, execute the request
        AppValues.getInstance().setAuthorization("testuser", "abba123abba");
        WordpressApi.getInstance().getService().uploadFile(body).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                //This block happens if the request was successful
                String error = WordpressApi.checkResponseForError(response);
                if (error != null) {
                    onFailure(call, new Throwable(error));
                    return;
                }
                Toast.makeText(PostActivity.this, "Uploaded Photo :)", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(PostActivity.this, "Failed to upload photo :(", Toast.LENGTH_LONG).show();

            }
        });

    }

    //<editor-fold desc="Listeners">
    ImagePickerCallback imageCallback = new ImagePickerCallback() {
        @Override
        public void onImagesChosen(List<ChosenImage> list) {
            if (list.isEmpty()) {
                return;
            }

            setupImage(Uri.fromFile(new File(list.get(0).getOriginalPath())));
        }

        @Override
        public void onError(String s) {

        }
    };
    //</editor-fold>
}
