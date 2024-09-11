package com.learnasyouread.stories.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.learnasyouread.stories.data.entity.StoryWordsEntity;
import com.learnasyouread.stories.data.repo.Repository;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class SavedWordsViewModel extends ViewModel {
    private final Repository storyRepository;
    private final LiveData<List<StoryWordsEntity>> wordsLiveData;

    @Inject
    public SavedWordsViewModel(Repository storyRepository) {
        this.storyRepository = storyRepository;
        this.wordsLiveData = storyRepository.getAllWordsLiveData();
        fetchAllWords();
    }

    public LiveData<List<StoryWordsEntity>> getWordsLiveData() {
        return wordsLiveData;
    }

    public void fetchAllWords() {
        storyRepository.fetchAllWords();
    }

    public LiveData<List<StoryWordsEntity>> searchWordsLiveData(String searchQuery) {
        return storyRepository.searchWordsLiveData(searchQuery);
    }

    public void deleteWord(StoryWordsEntity word) {
        storyRepository.deleteWord(word);
    }
}
