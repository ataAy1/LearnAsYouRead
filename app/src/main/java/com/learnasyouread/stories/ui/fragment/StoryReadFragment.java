package com.learnasyouread.stories.ui.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import android.speech.tts.TextToSpeech;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.learnasyouread.stories.R;
import com.learnasyouread.stories.data.entity.StoryWordsEntity;
import com.learnasyouread.stories.data.entity.StoriesEntity;
import com.learnasyouread.stories.databinding.FragmentReadStoryBinding;
import com.learnasyouread.stories.ui.viewmodel.StoryReadViewModel;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class StoryReadFragment extends Fragment {

    private FragmentReadStoryBinding binding;
    private Translator englishTurkishTranslator;
    private StoryReadViewModel viewModel;
    private TextToSpeech textToSpeech;
    private String storyTitle;
    private AlertDialog alertDialog;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentReadStoryBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(StoryReadViewModel.class);
        initializeUI();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupToolbar();
    }

    private void initializeUI() {
        StoryReadFragmentArgs args = StoryReadFragmentArgs.fromBundle(getArguments());
        StoriesEntity story = args.getSelectedStory();

        String storyText = story.getStory();
        storyTitle = story.getStoryTitle().replaceAll("(?i)\\bthe\\b", "").trim();
        int imgResource = story.getStoryImage();

        binding.textViewStoryContent.setText(storyText);
        binding.textViewTitle.setText(storyTitle);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imgResource);
        binding.imageViewStory.setImageBitmap(bitmap);

        setTextWithClickableWords(storyText);
    }

    private void setupToolbar() {
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        activity.setSupportActionBar(binding.toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.toolbar.setNavigationOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.fragmentContainerView);
            navController.navigateUp();
        });
    }

    private void setTextWithClickableWords(String text) {
        Spannable spannable = new SpannableString(text);
        Pattern pattern = Pattern.compile("\\b\\w+\\b");
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            final String word = matcher.group();
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(@NonNull View view) {
                    translateWord(word);
                }

                @Override
                public void updateDrawState(@NonNull TextPaint ds) {
                    ds.setUnderlineText(false);
                    ds.setColor(Color.BLACK);
                }
            };
            spannable.setSpan(clickableSpan, matcher.start(), matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        binding.textViewStoryContent.setText(spannable);
        binding.textViewStoryContent.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void translateWord(String word) {
        binding.textViewStoryContent.clearFocus();

        TranslatorOptions options = new TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.ENGLISH)
                .setTargetLanguage(TranslateLanguage.TURKISH)
                .build();
        englishTurkishTranslator = Translation.getClient(options);

        DownloadConditions conditions = new DownloadConditions.Builder()
                .requireWifi()
                .build();

        englishTurkishTranslator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener(unused -> englishTurkishTranslator.translate(word)
                        .addOnSuccessListener(translation -> showCustomDialog(word, translation))
                        .addOnFailureListener(this::handleError)
                )
                .addOnFailureListener(this::handleError);
    }

    private void handleError(Exception e) {
        Toast.makeText(getContext(), "Translation failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
    }

    private void showCustomDialog(String originalText, String translatedText) {
        View alertCustom = LayoutInflater.from(getContext()).inflate(R.layout.dialog_custom, null);

        setupDialogView(alertCustom, originalText, translatedText);

        alertDialog = new AlertDialog.Builder(getContext())
                .setView(alertCustom)
                .create();

        adjustDialogLayout(alertDialog);
        alertDialog.show();
    }

    private void setupDialogView(View alertCustom, String originalText, String translatedText) {
        TextView textViewEnglish = alertCustom.findViewById(R.id.textViewEnglishWord);
        TextView textViewTurkish = alertCustom.findViewById(R.id.textViewTurkıshWord);
        textViewEnglish.setText(originalText);
        textViewTurkish.setText(translatedText);

        Button listenButton = alertCustom.findViewById(R.id.listenButton);
        Button acceptButton = alertCustom.findViewById(R.id.acceptButton);
        ImageView imgExit = alertCustom.findViewById(R.id.btnExit);

        listenButton.setOnClickListener(v -> setTextToSpeech(originalText));
        acceptButton.setOnClickListener(v -> {
            viewModel.saveWord(new StoryWordsEntity(originalText, translatedText));
            Toast.makeText(getContext(), "Word saved successfully", Toast.LENGTH_SHORT).show();
            dismissDialog();
        });
        imgExit.setOnClickListener(v -> dismissDialog());
    }

    private void adjustDialogLayout(AlertDialog alertDialog) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int width = isTablet() ? 750 : 650;
        int height = isTablet() ? 350 : 450;
        alertDialog.getWindow().setLayout(width, height);
    }

    private boolean isTablet() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        return dpWidth >= 600;
    }

    private void setTextToSpeech(String text) {
        if (textToSpeech == null) {
            textToSpeech = new TextToSpeech(requireContext(), status -> {
                if (status == TextToSpeech.SUCCESS) {
                    int result = textToSpeech.setLanguage(Locale.ENGLISH);
                    if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
                    }
                }
            });
        } else {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    private void dismissDialog() {
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
    }

    @Override
    public void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
}
