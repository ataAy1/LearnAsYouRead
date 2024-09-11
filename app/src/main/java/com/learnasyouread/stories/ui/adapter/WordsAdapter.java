package com.learnasyouread.stories.ui.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.learnasyouread.stories.data.entity.StoryWordsEntity;
import com.learnasyouread.stories.databinding.LayoutStoryWordsBinding;
import com.learnasyouread.stories.ui.viewmodel.SavedWordsViewModel;

import java.util.ArrayList;
import java.util.Locale;

public class WordsAdapter extends RecyclerView.Adapter<WordsAdapter.KelimelerTasarım> {
    private final ArrayList<StoryWordsEntity> hikayeKelimelerListesi;
    private final SavedWordsViewModel kelimeViewModel;
    private final Context context;
    private TextToSpeech textToSpeech;

    public WordsAdapter(ArrayList<StoryWordsEntity> hikayeKelimelerListesi, Context context, SavedWordsViewModel kelimeViewModel) {
        this.hikayeKelimelerListesi = hikayeKelimelerListesi;
        this.kelimeViewModel = kelimeViewModel;
        this.context = context;
    }

    @NonNull
    @Override
    public KelimelerTasarım onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutStoryWordsBinding binding = LayoutStoryWordsBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new KelimelerTasarım(binding);
    }
    public void updateWords(ArrayList<StoryWordsEntity> newWords) {
        hikayeKelimelerListesi.clear();
        hikayeKelimelerListesi.addAll(newWords);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull KelimelerTasarım holder, int position) {
        StoryWordsEntity currentItem = hikayeKelimelerListesi.get(position);
        holder.binding.wordEng.setText(currentItem.getTurkishWord());
        holder.binding.wordTR.setText(currentItem.getEnglishWord());

        holder.binding.listenWord.setOnClickListener(v -> {
            if (textToSpeech == null) {
                initializeTextToSpeech();
            } else {
                speakWord(currentItem.getTurkishWord());
            }
        });

        holder.binding.deleteWord.setOnClickListener(v -> showDeleteConfirmationDialog(position));
    }

    private void initializeTextToSpeech() {
        textToSpeech = new TextToSpeech(context, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = textToSpeech.setLanguage(Locale.ENGLISH);
                if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                } else {
                    Toast.makeText(context, "Language not supported or missing data", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "TextToSpeech initialization failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void speakWord(String word) {
        if (textToSpeech != null) {
            textToSpeech.speak(word, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    private void showDeleteConfirmationDialog(int position) {
        new AlertDialog.Builder(context)
                .setMessage("Are you sure you want to delete this word?")
                .setPositiveButton("Yes", (dialog, id) -> {
                    kelimeViewModel.deleteWord(hikayeKelimelerListesi.get(position));
                    hikayeKelimelerListesi.remove(position);
                    notifyItemRemoved(position);
                    Toast.makeText(context, "Word deleted successfully", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", (dialog, id) -> dialog.dismiss())
                .create()
                .show();
    }

    @Override
    public int getItemCount() {
        return hikayeKelimelerListesi.size();
    }

    public static class KelimelerTasarım extends RecyclerView.ViewHolder {
        final LayoutStoryWordsBinding binding;

        public KelimelerTasarım(LayoutStoryWordsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
