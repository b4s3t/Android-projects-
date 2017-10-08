package com.example.android.mystock;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.mystock.data.ProductContract.ProductEntry;

import java.io.ByteArrayOutputStream;

public class AddActivity extends AppCompatActivity {

    public static final int PICK_IMAGE_REQUEST = 1;
    public static final int TAKE_PICTURE = 2;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;

    EditText editTextName;
    EditText editTextQuantity;
    EditText editTextPrice;
    ImageView previewImage;
    String mImageURI;
    Uri ResultURI;
    Uri ResultCamUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        setTitle(R.string.add_a_new_product);

        Button selectImageButton = (Button) findViewById(R.id.btn_image);

        // set up click listener for the button of "Select Image"
        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Choose();


            }

        });

        editTextName = (EditText) findViewById(R.id.add_activity_name);
        editTextQuantity = (EditText) findViewById(R.id.add_activity_quantity);
        editTextPrice = (EditText) findViewById(R.id.add_activity_price);
        previewImage = (ImageView) findViewById(R.id.add_image_preview);


        Button saveButton = (Button) findViewById(R.id.save_btn);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                insertProduct();
            }

        });

    }
    ////////////////////////////////////////////////////////////

    private void insertProduct() {

        String nameString = editTextName.getText().toString().trim();
        String quantityString = editTextQuantity.getText().toString().trim();
        String priceString = editTextPrice.getText().toString().trim();

        if (TextUtils.isEmpty(nameString) || TextUtils.isEmpty(quantityString) || TextUtils.isEmpty(priceString)|| TextUtils.isEmpty(mImageURI)) {
            Toast.makeText(AddActivity.this, getString(R.string.product_info_not_empty), Toast.LENGTH_SHORT).show();

        } else {


            ContentValues values = new ContentValues();
            values.put(ProductEntry.COLUMN_PRODUCT_NAME, nameString);
            values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, quantityString);
            values.put(ProductEntry.COLUMN_PRODUCT_PRICE, priceString);
            if (!"".equals(mImageURI))
                values.put(ProductEntry.COLUMN_PRODUCT_IMAGE, mImageURI);
            this.getContentResolver().insert(ProductEntry.CONTENT_URI, values);

            finish();
        }
    }


//////////////////////////////////////////////////////////////


    private void Choose() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("choose image from gallary or Take a photo");
        builder.setPositiveButton("Choose From Gallery", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                tryToOpenImageSelector();
            }
        });
        builder.setNegativeButton("Take A Photo", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                takePhoto();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    public void tryToOpenImageSelector() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            return;
        }
        openImageSelector();
    }

    private void openImageSelector() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    public void takePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, TAKE_PICTURE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openImageSelector();
                    // permission was granted
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        // The ACTION_OPEN_DOCUMENT intent was sent with the request code READ_REQUEST_CODE.
        // If the request code seen here doesn't match, it's the response to some other intent,
        // and the below code shouldn't run at all.
        switch (requestCode) {
            case PICK_IMAGE_REQUEST:

                if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
                    // The document selected by the user won't be returned in the intent.
                    // Instead, a URI to that document will be contained in the return intent
                    // provided to this method as a parameter.  Pull that uri using "resultData.getData()"

                    if (resultData != null) {
                        ResultURI = resultData.getData();
                        mImageURI = ResultURI.toString();
                        previewImage.setImageURI(ResultURI);

                    }

                }
                break;
            case TAKE_PICTURE:
                if (resultData != null) {
                    Bundle extras = resultData.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    ResultCamUri = getImageUri(this, imageBitmap);
                    mImageURI = ResultCamUri.toString();
                    previewImage.setImageBitmap(imageBitmap);

                }
        }

    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

}

