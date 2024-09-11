package com.learnasyouread.stories.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.learnasyouread.stories.data.entity.StoryQuestionsEntity;
import com.learnasyouread.stories.data.repo.Repository;

import java.util.List;
import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class StoryQuestionsViewModel extends ViewModel {

    private final Repository storyRepository;

    @Inject
    public StoryQuestionsViewModel(Repository storyRepository) {
        this.storyRepository = storyRepository;
    }


    public void loadStoryQuestions(String storyTitle) {
        storyRepository.fetchStoryQuestions(storyTitle);
    }


    public LiveData<List<StoryQuestionsEntity>> getStoryQuestionsLiveData() {
        return storyRepository.getStoryQuestionsLiveData();
    }
}
