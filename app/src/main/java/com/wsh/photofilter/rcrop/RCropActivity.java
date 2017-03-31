package com.wsh.photofilter.rcrop;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.wsh.photofilter.R;
import com.wsh.photofilter.filteractivity.ActivityGallery2;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;

import java.io.File;
import java.util.Locale;
import java.util.Random;

/**
 * Created by Oleksii Shliama (https://github.com/shliama).
 */
public class RCropActivity extends BaseActivity {

    private static final String TAG = "SampleActivity";

    private static final int REQUEST_SELECT_PICTURE = 0x01;
    private static final String SAMPLE_CROPPED_IMAGE_NAME = "SampleCropImage";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rcrop);

        setupUI();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_SELECT_PICTURE) {
                final Uri selectedUri = data.getData();
                if (selectedUri != null) {
                    startCropActivity(data.getData());
                } else {
                    Toast.makeText(RCropActivity.this, R.string.toast_cannot_retrieve_selected_image, Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == UCrop.REQUEST_CROP) {
                handleCropResult(data);
            }
        }
        if (resultCode == UCrop.RESULT_ERROR) {
            handleCropError(data);
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_STORAGE_READ_ACCESS_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickFromGallery();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void setupUI() {
        findViewById(R.id.button_crop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickFromGallery();
            }
        });
        findViewById(R.id.button_random_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Random random = new Random();
                int minSizePixels = 800;
                int maxSizePixels = 2400;
                startCropActivity(Uri.parse(String.format(Locale.getDefault(), "https://unsplash.it/%d/%d/?random",
                        minSizePixels + random.nextInt(maxSizePixels - minSizePixels),
                        minSizePixels + random.nextInt(maxSizePixels - minSizePixels))));
            }
        });

    }


    private void pickFromGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
                    getString(R.string.permission_read_storage_rationale),
                    REQUEST_STORAGE_READ_ACCESS_PERMISSION);
        } else {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(Intent.createChooser(intent, getString(R.string.label_select_picture)), REQUEST_SELECT_PICTURE);
        }
    }

    private void startCropActivity(@NonNull Uri uri) {
        String destinationFileName = SAMPLE_CROPPED_IMAGE_NAME;
        destinationFileName += ".jpg";

        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destinationFileName)));

        uCrop = basisConfig(uCrop);
        uCrop = advancedConfig(uCrop);

        uCrop.start(RCropActivity.this);
    }

    /**
     * In most cases you need only to set crop aspect ration and max size for resulting image.
     *
     * @param uCrop - ucrop builder instance
     * @return - ucrop builder instance
     */
    private UCrop basisConfig(@NonNull UCrop uCrop) {

        return uCrop;
    }

    /**
     * Sometimes you want to adjust more options, it's done via {@link UCrop.Options} class.
     *
     * @param uCrop - ucrop builder instance
     * @return - ucrop builder instance
     */
    private UCrop advancedConfig(@NonNull UCrop uCrop) {
        UCrop.Options options = new UCrop.Options();

       /*

        Tune everything (ﾉ◕ヮ◕)ﾉ*:･ﾟ✧

        options.setMaxScaleMultiplier(5);
        options.setImageToCropBoundsAnimDuration(666);
        options.setDimmedLayerColor(Color.CYAN);
        options.setCircleDimmedLayer(true);
        options.setShowCropFrame(false);
        options.setCropGridStrokeWidth(20);
        options.setCropGridColor(Color.GREEN);
        options.setCropGridColumnCount(2);
        options.setCropGridRowCount(1);
        options.setToolbarCropDrawable(R.drawable.your_crop_icon);
        options.setToolbarCancelDrawable(R.drawable.your_cancel_icon);

        // Color palette
        options.setToolbarColor(ContextCompat.getColor(this, R.color.your_color_res));
        options.setStatusBarColor(ContextCompat.getColor(this, R.color.your_color_res));
        options.setActiveWidgetColor(ContextCompat.getColor(this, R.color.your_color_res));
        options.setToolbarWidgetColor(ContextCompat.getColor(this, R.color.your_color_res));
        options.setRootViewBackgroundColor(ContextCompat.getColor(this, R.color.your_color_res));

        // Aspect ratio options
        options.setAspectRatioOptions(1,
            new AspectRatio("WOW", 1, 2),
            new AspectRatio("MUCH", 3, 4),
            new AspectRatio("RATIO", CropImageView.DEFAULT_ASPECT_RATIO, CropImageView.DEFAULT_ASPECT_RATIO),
            new AspectRatio("SO", 16, 9),
            new AspectRatio("ASPECT", 1, 1));

       */

        return uCrop.withOptions(options);
    }

    private void handleCropResult(@NonNull Intent result) {
        final Uri resultUri = UCrop.getOutput(result);
        if (resultUri != null) {
          //  ResultActivity.startWithUri(SampleActivity.this, resultUri);
            ActivityGallery2.startWithUri(RCropActivity.this, resultUri);
        } else {
            Toast.makeText(RCropActivity.this, R.string.toast_cannot_retrieve_cropped_image, Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    private void handleCropError(@NonNull Intent result) {
        final Throwable cropError = UCrop.getError(result);
        if (cropError != null) {
            Log.e(TAG, "handleCropError: ", cropError);
            Toast.makeText(RCropActivity.this, cropError.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(RCropActivity.this, R.string.toast_unexpected_error, Toast.LENGTH_SHORT).show();
        }
    }

}
