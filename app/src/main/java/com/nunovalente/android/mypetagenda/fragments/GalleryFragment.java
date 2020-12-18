package com.nunovalente.android.mypetagenda.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.android.material.transition.MaterialFadeThrough;
import com.nunovalente.android.mypetagenda.R;
import com.nunovalente.android.mypetagenda.activities.PreviewActivity;
import com.nunovalente.android.mypetagenda.adapters.RecyclerGalleryAdapter;
import com.nunovalente.android.mypetagenda.adapters.RecyclerItemClickListener;
import com.nunovalente.android.mypetagenda.databinding.FragmentGalleryBinding;
import com.nunovalente.android.mypetagenda.util.Constants;
import com.nunovalente.android.mypetagenda.util.Permission;
import com.nunovalente.android.mypetagenda.util.StringGenerator;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class GalleryFragment extends Fragment implements RecyclerItemClickListener {

    private static final String FILE_PATH = "/My Pet Agenda/images";
    private final static String TAG = PreviewActivity.class.getSimpleName();

    private final List<Bitmap> mImagesList = new ArrayList<>();
    private File output = null;

    private final String[] permissions = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private FragmentGalleryBinding mBinding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_gallery, container, false);
        View root = mBinding.getRoot();

        Permission.validatePermissions(new ArrayList<>(Arrays.asList(permissions)), requireActivity(), 2);
        ActivityCompat.requestPermissions(requireActivity(), permissions, 2);

        mBinding.fabGalleryCamera.setOnClickListener(v -> openCamera());


        this.setExitTransition(new MaterialFadeThrough().setDuration(getResources().getInteger(R.integer.reply_motion_duration_large)));
        return root;
    }

    private void loadImagesFromDCIM() {
        mImagesList.clear();

        File imagesPath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + FILE_PATH);
        if (imagesPath.exists()) {
            File[] files = imagesPath.listFiles();
            if (files != null) {
                for (File file : files) {
                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    mImagesList.add(bitmap);
                }

                setRecyclerView();
                mBinding.textNoImages.setVisibility(View.INVISIBLE);
            } else {
                mBinding.textNoImages.setVisibility(View.VISIBLE);
            }
        }
    }

    private void setRecyclerView() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireActivity(), 3);
        RecyclerGalleryAdapter adapter = new RecyclerGalleryAdapter(getContext(), mImagesList, this);
        mBinding.recyclerGallery.setLayoutManager(gridLayoutManager);
        mBinding.recyclerGallery.setAdapter(adapter);
        mBinding.recyclerGallery.setHasFixedSize(true);
    }

    public void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "My Pet Agenda/images");
        if (!dir.exists()) {
            Log.d(TAG, "Folder doesn't exist, creating it...");
            boolean wasSuccessful = dir.mkdir();
            Log.d(TAG, "Folder creation " + (wasSuccessful ? "success" : "failed"));
        } else {
            Log.d(TAG, "Folder already exists.");
        }

        String fileName = StringGenerator.getRandomString();
        output = new File(dir, fileName + ".jpeg");

        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(output));
        startActivityForResult(intent, Constants.REQUEST_CODE_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.REQUEST_CODE_CAMERA) {
                requireActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + output.getAbsolutePath())));
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadImagesFromDCIM();
    }

    @Override
    public void onItemClickListener(int id) {
        Bitmap bitmap = mImagesList.get(id);
        try {
            String filename = "bitmap.png";
            FileOutputStream stream = requireActivity().openFileOutput(filename, Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

            stream.close();
            bitmap.recycle();

            Intent intent = new Intent(getContext(), PreviewActivity.class);
            intent.putExtra(getString(R.string.selected_photo), filename);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLongClicked(int id) {

    }
}