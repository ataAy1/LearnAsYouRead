package com.learnasyouread.stories.ui.fragment;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.learnasyouread.stories.R;
import com.learnasyouread.stories.data.entity.StoryWordsEntity;
import com.learnasyouread.stories.data.entity.TextOcrProcessorEntity;
import com.learnasyouread.stories.databinding.FragmentTextDetailBinding;
import com.learnasyouread.stories.ui.viewmodel.TextListOCRViewModel;
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
public class TextDetailFragment extends Fragment {

    private FragmentTextDetailBinding binding;
    private TextToSpeech textToSpeech;
    private TextListOCRViewModel viewModel;
    private Translator englishTurkishTranslator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(TextListOCRViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTextDetailBinding.inflate(inflater, container, false);
        TextDetailFragmentArgs args = TextDetailFragmentArgs.fromBundle(getArguments());
        TextOcrProcessorEntity textOcr = args.getSelectedOCR();
        binding.textviewTitle.setText(textOcr.getTextTitle());
        binding.textviewContent.setText(textOcr.getText());
        setupTextWithClickableWords(binding.textviewContent.getText().toString());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AppCompatActivity appCompatActivity = (AppCompatActivity) requireActivity();
        appCompatActivity.setSupportActionBar(binding.toolbar);
        appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.toolbar.setNavigationOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.fragmentContainerView);
            navController.navigateUp();
        });
    }

    private void setupTextWithClickableWords(String text) {
        Spannable spannable = new SpannableString(text);
        Pattern pattern = Pattern.compile("\\b\\w+\\b");
        Matcher matcher = pattern.matcher(text);

        binding.textviewContent.setHighlightColor(Color.TRANSPARENT);

        while (matcher.find()) {
            final String word = matcher.group();
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(@NonNull View view) {
                    translateAndShowDialog(word);
                }

                @Override
                public void updateDrawState(@NonNull TextPaint ds) {
                    ds.setUnderlineText(false);
                    ds.setColor(Color.BLUE);
                }
            };
            spannable.setSpan(clickableSpan, matcher.start(), matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        binding.textviewContent.setText(spannable);
        binding.textviewContent.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void translateAndShowDialog(String word) {
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
                        .addOnFailureListener(e -> Toast.makeText(getContext(), "Translation failed: " + e.getMessage(), Toast.LENGTH_SHORT).show())
                )
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Model download failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void showCustomDialog(String originalText, String translatedText) {
        View alertCustom = LayoutInflater.from(getContext()).inflate(R.layout.dialog_custom, null);

        TextView textViewEnglish = alertCustom.findViewById(R.id.textViewEnglishWord);
        TextView textViewTurkish = alertCustom.findViewById(R.id.textViewTurkıshWord);
        Button listenButton = alertCustom.findViewById(R.id.listenButton);
        Button acceptButton = alertCustom.findViewById(R.id.acceptButton);
        ImageView imgExit = alertCustom.findViewById(R.id.btnExit);

        textViewEnglish.setText(originalText);
        textViewTurkish.setText(translatedText);

        AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setView(alertCustom)
                .create();

        listenButton.setOnClickListener(v -> setTextToSpeech(originalText));
        acceptButton.setOnClickListener(v -> {
            StoryWordsEntity storyWords = new StoryWordsEntity(originalText, translatedText);
            viewModel.saveWord(storyWords);
            Toast.makeText(getContext(), "Word saved successfully", Toast.LENGTH_SHORT).show();
            alertDialog.dismiss();
        });
        imgExit.setOnClickListener(v -> alertDialog.dismiss());

        alertDialog.show();
        alertDialog.getWindow().setLayout(650, 450);
    }

    private void setTextToSpeech(String text) {
        if (textToSpeech == null) {
            textToSpeech = new TextToSpeech(requireContext(), status -> {
                if (status == TextToSpeech.SUCCESS) {
                    int result = textToSpeech.setLanguage(Locale.ENGLISH);
                    if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
                    } else {
                        Toast.makeText(getContext(), "Text-to-Speech language not supported", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Text-to-Speech initialization failed", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    @Override
    public void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        if (englishTurkishTranslator != null) {
            englishTurkishTranslator.close();
        }
        super.onDestroy();
    }
}
