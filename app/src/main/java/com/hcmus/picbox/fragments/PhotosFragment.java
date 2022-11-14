package com.hcmus.picbox.fragments;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hcmus.picbox.R;
import com.hcmus.picbox.adapters.DateItem;
import com.hcmus.picbox.adapters.GridItem;
import com.hcmus.picbox.adapters.PhotoAdapter;
import com.hcmus.picbox.adapters.PhotoItem;
import com.hcmus.picbox.models.PhotoModel;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class PhotosFragment extends Fragment {

    private static final int PERMISSION_REQUEST_CODE = 200;
    private int mSpanCount = 4;
    private RecyclerView mGallery;
    private PhotoAdapter photoAdapter;
    private ArrayList<String> imagePaths = new ArrayList<>();
    private List<PhotoModel> items = new ArrayList<>();
    private Map<LocalDate, List<PhotoModel>> photoByDays = new TreeMap<>(Collections.reverseOrder());
    private List<GridItem> inputItems = new ArrayList<>();
    private Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photos, container, false);
        context = view.getContext();
        mGallery = view.findViewById(R.id.rcv_images);
        requestPermission();
        prepareRecyclerView();

        return view;
    }

    private boolean checkPermission() {
        // in this method we are checking if the permissions are granted or not and returning the result.
        int result = ContextCompat.checkSelfPermission(context.getApplicationContext(), READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        if (checkPermission()) {
            // if the permissions are already granted we are calling
            // a method to get all images from our external storage.
            Toast.makeText(context, "Permissions granted..", Toast.LENGTH_SHORT).show();
            getImagePath();
        } else {
            // if the permissions are not granted we are
            // calling a method to request permissions.
            requestPermission();
        }
    }

    private void requestPermission() {
        //on below line we are requesting the read external storage permissions.
        requestPermissions(new String[]{READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
//        ActivityCompat.requestPermissions(context.getClass(), new String[]{READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    private void prepareRecyclerView() {

        // in this method we are preparing our recycler view.
        // on below line we are initializing our adapter class.
        photoAdapter = new PhotoAdapter(inputItems);

        // on below line we are creating a new grid layout manager.
        GridLayoutManager manager = new GridLayoutManager(context, mSpanCount);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (photoAdapter.getItemViewType(position)) {
                    case GridItem.TYPE_DATE:
                        return manager.getSpanCount();
                    case GridItem.TYPE_PHOTO:
                        return 1;
                    default:
                        return -1;

                }
            }
        });
        // on below line we are setting layout
        // manager and adapter to our recycler view.
        mGallery.setLayoutManager(manager);
        mGallery.setAdapter(photoAdapter);
    }

    private void getImagePath() {
        // in this method we are adding all our image paths
        // in our arraylist which we have created.
        // on below line we are checking if the device is having an sd card or not.
        boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);

        if (isSDPresent) {

            // if the sd card is present we are creating a new list in
            // which we are getting our images data with their ids.
            final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};

            // on below line we are creating a new
            // string to order our images by string.
            final String orderBy = MediaStore.Images.Media._ID;

            // this method will stores all the images
            // from the gallery in Cursor
            Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy);

            // below line is to get total number of images
            int count = cursor.getCount();

            // on below line we are running a loop to add
            // the image file path in our array list.
            for (int i = 0; i < count; i++) {

                // on below line we are moving our cursor position
                cursor.moveToPosition(i);

                // on below line we are getting image file path
                int dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);

                // after that we are getting the image file path
                // and adding that path in our array list.
                items.add(new PhotoModel(cursor.getString(dataColumnIndex)));
            }
            for (PhotoModel photo : items) {
                LocalDate lastModified = photo.getLastModifiedDate();
                YearMonth month = YearMonth.from(lastModified);
                lastModified = month.atDay(1);
                List<PhotoModel> list = photoByDays.get(lastModified);
                if (list == null) {
                    list = new ArrayList<>();
                    photoByDays.put(lastModified, list);
                }
                list.add(photo);
            }
            for (LocalDate date : photoByDays.keySet()) {
                DateItem dateItem = new DateItem(date);
                inputItems.add(dateItem);
                for (PhotoModel photo : photoByDays.get(date)) {
                    PhotoItem photoItem = new PhotoItem(photo);
                    inputItems.add(photoItem);
                }
            }
            photoAdapter.notifyDataSetChanged();
            // after adding the data to our
            // array list we are closing our cursor.
            cursor.close();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        // this method is called after permissions has been granted.
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            // we are checking the permission code.
            case PERMISSION_REQUEST_CODE:
                // in this case we are checking if the permissions are accepted or not.
                if (grantResults.length > 0) {
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (storageAccepted) {
                        // if the permissions are accepted we are displaying a toast message
                        // and calling a method to get image path.
                        Toast.makeText(context, "Permissions Granted..", Toast.LENGTH_SHORT).show();
                        getImagePath();
                    } else {
                        // if permissions are denied we are closing the app and displaying the toast message.
                        Toast.makeText(context, "Permissions denied, Permissions are required to use the app..", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }
}
