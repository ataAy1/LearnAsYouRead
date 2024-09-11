package com.learnasyouread.stories.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.learnasyouread.stories.data.entity.StoryWordsEntity;
import com.learnasyouread.stories.data.entity.TextOcrProcessorEntity;
import com.learnasyouread.stories.data.repo.Repository;
import java.util.List;
import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class TextListOCRViewModel extends ViewModel {

    private final Repository storyRepository;
    private final MutableLiveData<Boolean> saveSuccess = new MutableLiveData<>();
    private final MutableLiveData<Boolean> deleteSuccess = new MutableLiveData<>();

    @Inject
    public TextListOCRViewModel(Repository storyRepository) {
        this.storyRepository = storyRepository;
    }

    public LiveData<List<TextOcrProcessorEntity>> getAllOcrTexts() {
        return storyRepository.getAllOcrTextsLiveData();
    }

    public void saveWord(StoryWordsEntity storyWordsEntity) {
        try {
            storyRepository.saveWord(storyWordsEntity);
            saveSuccess.setValue(true);
        } catch (Exception e) {
            saveSuccess.setValue(false);
        }
    }

    public LiveData<Boolean> getSaveSuccess() {
        return saveSuccess;
    }

    public void deleteOcrText(int id) {
        try {
            storyRepository.deleteOcrText(id);
            deleteSuccess.setValue(true);
        } catch (Exception e) {
            deleteSuccess.setValue(false);
        }
    }

    public LiveData<Boolean> getDeleteSuccess() {
        return deleteSuccess;
    }
}
