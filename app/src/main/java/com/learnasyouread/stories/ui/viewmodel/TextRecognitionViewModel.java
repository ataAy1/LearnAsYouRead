package com.learnasyouread.stories.ui.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.learnasyouread.stories.data.entity.TextOcrProcessorEntity;
import com.learnasyouread.stories.data.repo.Repository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class TextRecognitionViewModel extends ViewModel {

    private final Repository textRepository;
    private final MutableLiveData<List<TextOcrProcessorEntity>> ocrTexts = new MutableLiveData<>(new ArrayList<>());

    @Inject
    public TextRecognitionViewModel(Repository repository) {
        this.textRepository = repository;
    }


    public void addOcrText(TextOcrProcessorEntity textOcrProcessorEntity) {
        List<TextOcrProcessorEntity> currentList = ocrTexts.getValue();
        if (currentList != null) {
            currentList.add(textOcrProcessorEntity);
            ocrTexts.setValue(currentList);
        }
        textRepository.addOcrText(textOcrProcessorEntity);
    }
}