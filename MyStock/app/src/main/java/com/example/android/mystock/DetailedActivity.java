package com.example.android.mystock;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.mystock.data.ProductContract.ProductEntry;

public class DetailedActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int PRODUCT_LOADER = 0;

    private Uri mCurrentPetUri;

    private TextView mNameTextView;
    private TextView mQuantityTextView;
    private TextView mPriceTextView;
    private ImageView mImageView;
    private String mImageURI;
    private Button decreaseQuantity;
    private Button increaseQuantity;
    private Button saveNewQuantity;
    private Button requestButton;
    private Button deleteButton;
    private Button deleteAllButton;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);

        Intent intent = getIntent();
        mCurrentPetUri = intent.getData();
        setTitle(getString(R.string.detailed_Activity));
        getLoaderManager().initLoader(PRODUCT_LOADER, null, this);

        mNameTextView = (TextView) findViewById(R.id.text_name);
        mQuantityTextView = (TextView) findViewById(R.id.text_view_quantity);
        mPriceTextView = (TextView) findViewById(R.id.text_price);
        mImageView = (ImageView) findViewById(R.id.detailed_image);


        increaseQuantity = (Button) findViewById(R.id.add_quantity);
        increaseQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addOneToQuantity();

            }
        });

        decreaseQuantity = (Button) findViewById(R.id.sub_quantity);
        decreaseQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subtractOneFromQuantity();

            }
        });

        saveNewQuantity = (Button) findViewById(R.id.btn_save);
        saveNewQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNewQuantity();

            }
        });

        requestButton = (Button) findViewById(R.id.ordrer);
        requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.content.Intent.ACTION_SENDTO);
                intent.setType("text/plain");
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Recurrent new order");
                String bodyMessage = "Please send us as soon as possible more from " + name ;
                intent.putExtra(android.content.Intent.EXTRA_TEXT, bodyMessage);
                startActivity(intent);
            }
        });

        deleteButton = (Button) findViewById(R.id.btn_delete);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteSingleProduct();
            }
        });

        deleteAllButton = (Button) findViewById(R.id.btn_deleteAll);
        deleteAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteTheWholeProducts();
            }
        });
    }

    ////////////////////////////////////////////////////////////////////////

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_PRODUCT_IMAGE};

        return new CursorLoader(this,
                mCurrentPetUri,
                projection,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
            int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
            int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
            int imageColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_IMAGE);

             name = cursor.getString(nameColumnIndex);
            Integer quantity = cursor.getInt(quantityColumnIndex);
            Float price = cursor.getFloat(priceColumnIndex);
            String imageURI = cursor.getString(imageColumnIndex);

            mNameTextView.setText(name);
            mQuantityTextView.setText(Integer.toString(quantity));
            mPriceTextView.setText(Float.toString(price));
            if (imageURI != null) {

                mImageView.setImageURI(Uri.parse(imageURI));


            } else {

                Toast.makeText(this, "No Image Detected", Toast.LENGTH_SHORT).show();

            }
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameTextView.setText("");
        mQuantityTextView.setText(Integer.toString(0));
        mPriceTextView.setText(Float.toString(0));
        mImageView.setImageDrawable(null);
    }

    //////////////////////////////////////////////////////////////////////////////////


    private void subtractOneFromQuantity() {
        String previousValueString = mQuantityTextView.getText().toString();
        int previousValue;
        if (previousValueString.isEmpty()) {
            return;
        } else if (previousValueString.equals("0")) {
            return;
        } else {
            previousValue = Integer.parseInt(previousValueString);
            mQuantityTextView.setText(String.valueOf(previousValue - 1));
        }
    }

    private void addOneToQuantity() {
        String previousValueString = mQuantityTextView.getText().toString();
        int previousValue;
        if (previousValueString.isEmpty()) {
            previousValue = 0;
        } else {
            previousValue = Integer.parseInt(previousValueString);
        }
        mQuantityTextView.setText(String.valueOf(previousValue + 1));
    }

    private void saveNewQuantity() {

        Integer quantity = Integer.parseInt(mQuantityTextView.getText().toString().trim());

        ContentValues values = new ContentValues();

        // quantity
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, quantity);

        int rowsAffected = getContentResolver().update(mCurrentPetUri, values, null, null);

        if (rowsAffected == 0) {
            Toast.makeText(this, getString(R.string.update_product_failed), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.update_product_successful), Toast.LENGTH_SHORT).show();
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////
    public void deleteSingleProduct() {
        showDeleteConfirmationDialog1();
    }

    private void showDeleteConfirmationDialog1() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.delete_dialog_title));
        builder.setPositiveButton(getString(R.string.btn_delete), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteProduct();
                finish();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void deleteTheWholeProducts() {
        showDeleteConfirmationDialog2();
    }

    private void showDeleteConfirmationDialog2() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.delete_dialog_title2));
        builder.setPositiveButton(getString(R.string.btn_deleteAll), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteAllProducts();
                finish();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteProduct() {
        int rowsDeleted = getContentResolver().delete(mCurrentPetUri, null, null);
        if (rowsDeleted == 0) {
            Toast.makeText(this, getString(R.string.delete_product_failed), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.delete_product_successful), Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    private void deleteAllProducts() {
        int rowsDeleted = getContentResolver().delete(ProductEntry.CONTENT_URI, null, null);
        if (rowsDeleted == 0) {
            Toast.makeText(this, getString(R.string.delete_product_failed), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.delete_product_successful), Toast.LENGTH_SHORT).show();
        }
        finish();
    }

}
