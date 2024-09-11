package com.learnasyouread.stories.ui.fragment;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dd.morphingbutton.MorphingButton;
import com.learnasyouread.stories.R;
import com.learnasyouread.stories.data.entity.TextOcrProcessorEntity;
import com.learnasyouread.stories.databinding.FragmentStoryImageToTextBinding;
import com.learnasyouread.stories.ui.viewmodel.TextRecognitionViewModel;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TextRecognitionStoryFragment extends Fragment {

    private static final int REQUEST_CAMERA_PERMISSION = 1001;
    private static final int REQUEST_EXTERNAL_STORAGE_PERMISSION = 1002;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;
    private static final int UCROP_REQUEST_CODE = UCrop.REQUEST_CROP;

    private TextRecognitionViewModel viewModel;
    private FragmentStoryImageToTextBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentStoryImageToTextBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(TextRecognitionViewModel.class);

        initListeners();

        return binding.getRoot();
    }

    private void initListeners() {
        binding.imageCamera.setOnClickListener(v -> checkPermissionAndCaptureImage());
        binding.imageGallery.setOnClickListener(v -> checkPermissionAndPickImage());
        binding.saveBtn.setOnClickListener(v -> saveText());
    }

    private void checkPermissionAndCaptureImage() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else {
            dispatchTakePictureIntent();
        }
    }

    private void checkPermissionAndPickImage() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE_PERMISSION);
        } else {
            dispatchPickImageIntent();
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(requireContext(), "Camera not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void dispatchPickImageIntent() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        try {
            startActivityForResult(pickPhoto, REQUEST_IMAGE_PICK);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(requireContext(), "Error picking an image", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveText() {
        String textTitle = binding.textViewContent.getText().toString().trim();
        String textName = binding.textViewTitle.getText().toString().trim();

        if (!textTitle.isEmpty() && !textName.isEmpty()) {
            MorphingButton.Params circle = MorphingButton.Params.create()
                    .duration(750)
                    .cornerRadius((int) getResources().getDimension(R.dimen.mb_height_56))
                    .width((int) getResources().getDimension(R.dimen.mb_height_56))
                    .height((int) getResources().getDimension(R.dimen.mb_height_56))
                    .color(ContextCompat.getColor(requireContext(), R.color.greenYellow))
                    .colorPressed(ContextCompat.getColor(requireContext(), R.color.greenYellow))
                    .icon(R.drawable.ok_icon);

            binding.saveBtn.morph(circle);

            TextOcrProcessorEntity ocr = new TextOcrProcessorEntity(textTitle, textName);
            viewModel.addOcrText(ocr);

            binding.textViewContent.setText("");
            binding.textViewTitle.setText("");

            new Handler().postDelayed(() -> {
                MorphingButton.Params originalParams = MorphingButton.Params.create()
                        .duration(750)
                        .width(525)
                        .height(125)
                        .text("SAVE")
                        .color(Color.CYAN);
                binding.saveBtn.morph(originalParams);
            }, 2000);
        } else {
            Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            switch (requestCode) {
                case REQUEST_IMAGE_CAPTURE:
                    handleCameraResult(data);
                    break;
                case REQUEST_IMAGE_PICK:
                    handleGalleryResult(data);
                    break;
                case UCROP_REQUEST_CODE:
                    handleUCropResult(resultCode, data);
                    break;
            }
        }
    }

    private void handleUCropResult(int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            Uri resultUri = UCrop.getOutput(data);
            Log.d("MyApp", "Crop successful. Result URI: " + resultUri);
            loadCroppedImage(resultUri);
        } else if (resultCode == UCrop.RESULT_ERROR && data != null) {
            Throwable cropError = UCrop.getError(data);
            Log.e("MyApp", "Crop error: " + cropError.getMessage());
            Toast.makeText(requireContext(), "Error cropping image", Toast.LENGTH_SHORT).show();
        }
    }

    private File saveBitmapToFile(Bitmap bitmap) {
        File tempFile = new File(requireContext().getCacheDir(), "temp_image.jpg");
        try (FileOutputStream out = new FileOutputStream(tempFile)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (IOException e) {
            Log.e("MyApp", "Error saving bitmap to file", e);
        }
        return tempFile;
    }

    private void loadCroppedImage(Uri resultUri) {
        try (InputStream inputStream = requireContext().getContentResolver().openInputStream(resultUri)) {
            Bitmap croppedBitmap = BitmapFactory.decodeStream(inputStream);
            processImage(croppedBitmap);
        } catch (IOException e) {
            Log.e("MyApp", "Failed to load cropped image", e);
            Toast.makeText(requireContext(), "Failed to load image", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleCameraResult(Intent data) {
        Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
        Uri imageUri = Uri.fromFile(saveBitmapToFile(imageBitmap));
        Uri destinationUri = Uri.fromFile(new File(requireContext().getCacheDir(), "cropped_image.jpg"));
        startUCrop(imageUri, destinationUri);
    }

    private void handleGalleryResult(Intent data) {
        Uri imageUri = data.getData();
        Uri destinationUri = Uri.fromFile(new File(requireContext().getCacheDir(), "cropped_image.jpg"));
        startUCrop(imageUri, destinationUri);
    }

    private void startUCrop(Uri sourceUri, Uri destinationUri) {
        UCrop uCrop = UCrop.of(sourceUri, destinationUri);
        uCrop.withAspectRatio(1, 1);
        uCrop.withMaxResultSize(600, 600);
        uCrop.start(requireContext(), this);
    }

    private void processImage(Bitmap bitmap) {
        InputImage image = InputImage.fromBitmap(bitmap, 0);
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

        recognizer.process(image)
                .addOnSuccessListener(this::handleTextRecognitionSuccess)
                .addOnFailureListener(e -> {
                    Log.e("MyApp", "Text recognition failed", e);
                    Toast.makeText(requireContext(), "Text recognition failed", Toast.LENGTH_SHORT).show();
                });
    }

    private void handleTextRecognitionSuccess(Text result) {
        StringBuilder recognizedText = new StringBuilder();
        for (Text.TextBlock block : result.getTextBlocks()) {
            recognizedText.append(block.getText()).append("\n");
        }
        binding.textViewContent.setText(recognizedText.toString());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    dispatchTakePictureIntent();
                } else {
                    Toast.makeText(requireContext(), "Camera permission required", Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_EXTERNAL_STORAGE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    dispatchPickImageIntent();
                } else {
                    Toast.makeText(requireContext(), "Storage permission required", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
