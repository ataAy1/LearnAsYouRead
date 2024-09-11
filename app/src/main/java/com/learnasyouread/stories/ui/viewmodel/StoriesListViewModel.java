package com.learnasyouread.stories.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.learnasyouread.stories.R;
import com.learnasyouread.stories.data.entity.StoriesEntity;
import com.learnasyouread.stories.data.repo.Repository;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class StoriesListViewModel extends ViewModel {
    private final Repository storyRepo;

    @Inject
    public StoriesListViewModel(Repository hikayeRepo) {
        this.storyRepo = hikayeRepo;
        initializeStoryImages();
        hikayeRepo.fetchAllStories();
    }

    private void initializeStoryImages() {
        for (int i = 1; i <= 17; i++) {
            int resId = getResourceIdForStoryImage(i);
            storyRepo.updateStoryImage(i, resId);
        }
    }

    private int getResourceIdForStoryImage(int storyNumber) {
        switch (storyNumber) {
            case 1: return R.drawable.hikaye1;
            case 2: return R.drawable.hikaye2;
            case 3: return R.drawable.hikaye3;
            case 4: return R.drawable.hikaye4;
            case 5: return R.drawable.hikaye5;
            case 6: return R.drawable.hikaye6;
            case 7: return R.drawable.hikaye7;
            case 8: return R.drawable.hikaye8;
            case 9: return R.drawable.hikaye9;
            case 10: return R.drawable.hikaye10;
            case 11: return R.drawable.hikaye11;
            case 12: return R.drawable.hikaye12;
            case 13: return R.drawable.hikaye13;
            case 14: return R.drawable.hikaye14;
            case 15: return R.drawable.hikaye15;
            case 16: return R.drawable.hikaye16;
            case 17: return R.drawable.hikaye17;
            default: throw new IllegalArgumentException("Invalid story number");
        }
    }

    public LiveData<List<StoriesEntity>> getAllStories() {
        return storyRepo.getAllStoriesLiveData();
    }
}
