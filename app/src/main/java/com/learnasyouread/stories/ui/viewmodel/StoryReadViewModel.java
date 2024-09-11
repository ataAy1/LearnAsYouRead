package com.learnasyouread.stories.ui.viewmodel;

import androidx.lifecycle.ViewModel;
import com.learnasyouread.stories.data.entity.StoryWordsEntity;
import com.learnasyouread.stories.data.repo.Repository;

import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class StoryReadViewModel extends ViewModel {
    private final Repository storyRepository;

    @Inject
    public StoryReadViewModel(Repository storyRepository) {
        this.storyRepository = storyRepository;
    }

    public void saveWord(StoryWordsEntity storyWordsEntity) {
        storyRepository.saveWord(storyWordsEntity);
    }
}
