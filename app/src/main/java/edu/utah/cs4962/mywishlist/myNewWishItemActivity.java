package edu.utah.cs4962.mywishlist;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

/**
 * Created by Jesus Zarate on 11/24/14.
 */
public class myNewWishItemActivity extends Activity
{

    public String imagePath;

    myWishItem newWishItem;

    EditText itemName;
    EditText itemLocation;
    EditText itemPrice;
    CheckBox itemOnSale;
    SeekBar itemWantLevel;

    Button addNewItemButton;


    private double latitude;
    private double longitude;

    //region Listeners

    public interface OnNewItemAddedListener
    {
        public void OnNewItemAdded(myNewWishItemActivity myNewWishItemActivity);
    }

    OnNewItemAddedListener _onNewItemAddedListener = null;

    public void setOnNewItemAddedListener(OnNewItemAddedListener onNewItemAddedListener)
    {
        this._onNewItemAddedListener = onNewItemAddedListener;
    }
    //endregion Listeners

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_wishlist_item);

        ImageView imageView = (ImageView) findViewById(R.id.imageView);

        // Create a new wish item
        newWishItem = new myWishItem();

        itemName = (EditText) findViewById(R.id.itemNameInputText);
        itemLocation = (EditText) findViewById(R.id.locationInputText);

        if (getIntent().hasExtra(myListActivity.IMAGE_PATH))
        {
            // Get the coordinates of the location where the image was taken
            GPSTracker gpsTracker = new GPSTracker(myNewWishItemActivity.this);

            if (gpsTracker.canGetLocation())
            {
                latitude = gpsTracker.getLatitude();
                longitude = gpsTracker.getLongitude();

                Toast.makeText(getApplicationContext(), "Latitude: " + latitude +
                        "\nLongitude: " + longitude, Toast.LENGTH_SHORT).show();
            }

            // Set the image taken to the item preview
            imagePath = getIntent().getStringExtra(myListActivity.IMAGE_PATH);

            Bitmap bitmap = myListFragment.getPic(imagePath);
            imageView.setImageBitmap(bitmap);
        }

        itemPrice = (EditText) findViewById(R.id.itemPriceInput);
        itemOnSale = (CheckBox) findViewById(R.id.onSaleCheckBox);
        itemWantLevel = (SeekBar) findViewById(R.id.wantLevelBar);

        //region <Add New Item Button>
        addNewItemButton = (Button) findViewById(R.id.addNewItemButton);

        // Add the new item to the user's list.
        addNewItemButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                if (checkCorrectInput())
                {
                    newWishItem.setItemName(itemName.getText().toString());
                    newWishItem.setLocationName(itemLocation.getText().toString());
                    newWishItem.setPrice(Double.parseDouble(itemPrice.getText().toString()));
                    newWishItem.setWantLevel(itemWantLevel.getProgress() / 10);
                    newWishItem.setOnSale(itemOnSale.isChecked());

                    myWishItem.Coordinates coordinates = new myWishItem.Coordinates();
                    coordinates.latidude = latitude;
                    coordinates.longitude = longitude;
                    newWishItem.setCoordinates(coordinates);


                    File dir = Environment.getExternalStorageDirectory();
                    if (dir.exists())
                    {
                        String regex = getString(R.string.image_regular_expression);
                        File from = new File(dir, myListActivity.MY_WISH_LIST_DIR + "/" + myListActivity.LATEST_IMAGE);

                        File to = new File(dir,
                                myListActivity.MY_WISH_LIST_DIR + "/" + regex + itemName.getText() +
                                        "_" + itemLocation.getText() + regex + ".png");

                        if (from.exists())
                        {
                            from.renameTo(to);
                        }

                        if (to.exists())
                        {
                            newWishItem.setImageName(to.getName());
                            addNewItemButton.setText(to.getName());
                        }
                    }
                }
                myWishList.getInstance().addWishtItem(newWishItem);

                closeActivity();

            }
        });


        //endregion <Add New Item Button>
    }

    public void closeActivity()
    {
        Intent resultIntent = new Intent();
        setResult(Activity.RESULT_OK, resultIntent);
        //finishActivity(myListActivity.NEW_ITEM_ADDED);
        finish();
    }

    public boolean checkCorrectInput()
    {
        if (itemName.getText().length() > 0 &&
                itemLocation.getText().length() > 0)
        {
            if (itemPrice.getText().length() > 0)
            {
                try
                {
                    Double.parseDouble(itemPrice.getText().toString());
                    return true;
                } catch (Exception e)
                {
                    return false;
                }
            }
            return false;
        }
        return false;
    }
}
