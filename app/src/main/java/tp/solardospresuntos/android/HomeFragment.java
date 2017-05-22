package tp.solardospresuntos.android;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kbeanie.multipicker.api.CacheLocation;
import com.kbeanie.multipicker.api.CameraImagePicker;
import com.kbeanie.multipicker.api.ImagePicker;
import com.kbeanie.multipicker.api.Picker;
import com.kbeanie.multipicker.api.callbacks.ImagePickerCallback;
import com.kbeanie.multipicker.api.entity.ChosenImage;

import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import tp.solardospresuntos.android.base.BaseNavigationActivity;
import tp.solardospresuntos.android.databinding.FragmentHomeBinding;
import tp.solardospresuntos.android.utils.BitmapUtils;
import tp.solardospresuntos.android.utils.BuildUtils;
import tp.solardospresuntos.android.utils.FileUtils;
import tp.solardospresuntos.android.utils.PackageUtils;
import tp.solardospresuntos.android.utils.PermissionUtils;
import tp.solardospresuntos.android.utils.SettingsUtils;
import tp.solardospresuntos.android.utils.Utils;
import tp.solardospresuntos.android.utils.listener.DebouncedOnClickListener;

/**
 * Created by filiperodrigues on 18/05/17.
 */

public class HomeFragment extends Fragment implements ImagePickerCallback {
    private static final String CAMERA_PICKER_PATH_KEY = "picker_path_instance_key";
    private static final int IMAGE_MAX_SIZE = 1920;
    private static final int IMAGE_COMPRESS_JPEG_QUALITY = 80;
    private static final int PERMISSIONS_REQUEST_STORAGE = 142;
    private static final String FIRST_TIME_STORAGE_PERMISSION_REQUEST = "FIRST_TIME_STORAGE_PERMISSION_REQUEST";

    private FragmentHomeBinding mBinding;
    private CameraImagePicker mCameraPicker;
    private ImagePicker mImagePicker;
    private String mCameraPickerPath;
    private AtomicBoolean mCameraLaunched = new AtomicBoolean(true);

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mBinding = FragmentHomeBinding.bind(view);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(CAMERA_PICKER_PATH_KEY)) {
                mCameraPickerPath = savedInstanceState.getString(CAMERA_PICKER_PATH_KEY);
            }
        }
        setupUI();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Picker.PICK_IMAGE_DEVICE) {
                if (mImagePicker == null) {
                    mImagePicker = new ImagePicker(this);
                }
                mImagePicker.submit(data);
            } else if (requestCode == Picker.PICK_IMAGE_CAMERA) {
                if (mCameraPicker == null) {
                    mCameraPicker = new CameraImagePicker(this);
                    mCameraPicker.reinitialize(mCameraPickerPath);
                }
                mCameraPicker.submit(data);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // You have to save path in case your activity is killed.
        // In such a scenario, you will need to re-initialize the CameraImagePicker
        outState.putString(CAMERA_PICKER_PATH_KEY, mCameraPickerPath);
        super.onSaveInstanceState(outState);
    }

    private void setupUI() {
        mBinding.homeTakePhoto.setOnClickListener(new DebouncedOnClickListener() {
            @Override
            public void onDebouncedClick(View v) {
                mCameraLaunched.getAndSet(true);

                Log.e("Click", "Camera");

                if (BuildUtils.hasMarshmallow()) {
                    if (checkStoragePermissions()) {
                        takePicture();
                    } else {
                        requestStoragePermissions();
                    }
                } else {
                    takePicture();
                }
            }
        });

        mBinding.homeAddPhoto.setOnClickListener(new DebouncedOnClickListener() {
            @Override
            public void onDebouncedClick(View v) {
                mCameraLaunched.getAndSet(false);

                Log.e("Click", "Add Photo");
                if (BuildUtils.hasMarshmallow()) {
                    if (checkStoragePermissions()) {
                        pickImageSingle();
                    } else {
                        requestStoragePermissions();
                    }
                } else {
                    pickImageSingle();
                }
            }
        });

        mBinding.homeOpenGallery.setOnClickListener(new DebouncedOnClickListener() {
            @Override
            public void onDebouncedClick(View v) {
                Log.e("Click", "System Gallery");
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    if(mCameraLaunched.get()){
                        takePicture();
                    } else{
                        pickImageSingle();
                    }

                } else {
                    // permission denied, boohoho
                    Activity activity = getActivity();
                    if(!BaseNavigationActivity.validateActivity(activity)) return;

                    Utils.showSnackbar(activity, getString(R.string.home_upload_photo_explanation));
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private boolean checkStoragePermissions() {
        Activity activity = getActivity();
        if(!BaseNavigationActivity.validateActivity(activity)) return false;

        return PermissionUtils.hasPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
                //if we have write we have read
                && PermissionUtils.hasPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void requestStoragePermissions() {
        Activity activity = getActivity();
        if(!BaseNavigationActivity.validateActivity(activity)) return;

        if(isFirstTimeRequestingPermission()) {
            updateFirstTimeRequestingPermission();
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_STORAGE);
            return;
        }

        if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Utils.showSnackbar(activity, getString(R.string.home_upload_photo_explanation), getString(android.R.string.ok), new View.OnClickListener() {
                @TargetApi(Build.VERSION_CODES.M)
                @Override
                public void onClick(View v) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_STORAGE);
                }
            });
        } else {
            //if we pass here, the user will not see the popups requesting permission anymore. we need to send him to the settings to change permission manually.
            showSnackbarStoragePermissionDeniedPermanently();
        }
    }


    /**
     * Check if this is the first time that we are requesting the location permission.
     *
     * @return true if it is the first time, false otherwise.
     */
    private boolean isFirstTimeRequestingPermission() {
        Activity activity = getActivity();
        if(!BaseNavigationActivity.validateActivity(activity)) return false;

        Boolean isFirstTimeRequestingPermission = SettingsUtils.getObjectFromSettings(activity, FIRST_TIME_STORAGE_PERMISSION_REQUEST, Boolean.class);
        if (isFirstTimeRequestingPermission == null) {
            isFirstTimeRequestingPermission = true;
        }
        return isFirstTimeRequestingPermission;
    }

    /**
     * Updates the value of the first time request permission variable.
     */
    private void updateFirstTimeRequestingPermission() {
        Activity activity = getActivity();
        if(!BaseNavigationActivity.validateActivity(activity)) return;

        SettingsUtils.saveObjectToSettings(activity, FIRST_TIME_STORAGE_PERMISSION_REQUEST, false);
    }

    /**
     * Shows a snackbar that tells the user that the permission is necessary. Once the user clicks on the
     * button in the snackbar, opens the settings for the app so that the user can change the permissions
     * of the app.
     */
    private void showSnackbarStoragePermissionDeniedPermanently() {
        final Activity activity = getActivity();
        if(!BaseNavigationActivity.validateActivity(activity)) return;

        String message = getString(R.string.home_upload_photo_permission_denied_permanently);
        Utils.showSnackbar(activity, message, getString(android.R.string.ok), Snackbar.LENGTH_LONG, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PackageUtils.openAppSettings(activity);
            }
        });
    }


    public void pickImageSingle() {
        mImagePicker = new ImagePicker(this);
        mImagePicker.shouldGenerateMetadata(true);
        mImagePicker.shouldGenerateThumbnails(true);
        mImagePicker.setImagePickerCallback(this);
        mImagePicker.pickImage();
    }

    public void takePicture() {
        mCameraPicker = new CameraImagePicker(this);
        mCameraPicker.setCacheLocation(CacheLocation.EXTERNAL_STORAGE_APP_DIR);
        mCameraPicker.setImagePickerCallback(this);
        mCameraPicker.shouldGenerateMetadata(true);
        mCameraPicker.shouldGenerateThumbnails(true);
        mCameraPickerPath = mCameraPicker.pickImage();
    }

    @Override
    public void onImagesChosen(List<ChosenImage> list) {
        Activity activity = getActivity();
        if(!BaseNavigationActivity.validateActivity(activity)) return;

        if (list.size() < 1) {
            // This isn't supposed to happen
            Utils.showSnackbar(activity, getString(R.string.home_choose_picture_generic_error));
            return;
        }

        Log.d(HomeFragment.this.getTag(), list.get(0).getOriginalPath());

        final File imgFile = new File(list.get(0).getOriginalPath());

        if (imgFile.exists() && FileUtils.validateImageFileExtension(imgFile)) {
            decodeFileAndSetBitmap(imgFile);
        } else {
            Utils.showSnackbar(activity, getString(R.string.home_choose_picture_unsupported_extension));
        }
    }

    private void decodeFileAndSetBitmap(final File imgFile) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imgFile.getAbsolutePath(), options);

        // Calculate inSampleSize_
        options.inSampleSize = BitmapUtils.calculateInSampleSize(options, IMAGE_MAX_SIZE, IMAGE_MAX_SIZE);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        Bitmap tmpBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath(), options);
    }

    @Override
    public void onError(String s) {
        Activity activity = getActivity();
        if(!BaseNavigationActivity.validateActivity(activity)) return;

        Utils.showSnackbar(activity, getString(R.string.home_choose_picture_generic_error));
    }

}
