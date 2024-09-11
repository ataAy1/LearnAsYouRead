package com.learnasyouread.stories.data.repo;

import android.util.Log;
import androidx.lifecycle.MutableLiveData;
import com.learnasyouread.stories.data.entity.*;
import com.learnasyouread.stories.room.StoriesDao;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import io.reactivex.CompletableObserver;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

@Singleton
public class Repository {

    private final StoriesDao storiesDao;
    private final MutableLiveData<List<StoryWordsEntity>> wordsLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<StoryQuestionsEntity>> storyQuestionsLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<StoriesEntity>> allStoriesLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<TextOcrProcessorEntity>> allOcrTextsLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<StoryWordsEntity>> searchedWordsLiveData = new MutableLiveData<>();

    @Inject
    public Repository(StoriesDao storiesDao) {
        this.storiesDao = storiesDao;
    }

    public MutableLiveData<List<StoriesEntity>> getAllStoriesLiveData() {
        return allStoriesLiveData;
    }

    public MutableLiveData<List<TextOcrProcessorEntity>> getAllOcrTextsLiveData() {
        return allOcrTextsLiveData;
    }

    public MutableLiveData<List<StoryWordsEntity>> getAllWordsLiveData() {
        return wordsLiveData;
    }

    public MutableLiveData<List<StoryQuestionsEntity>> getStoryQuestionsLiveData() {
        return storyQuestionsLiveData;
    }

    public MutableLiveData<List<StoryWordsEntity>> searchWordsLiveData(String searchQuery) {
        storiesDao.searchWords(searchQuery)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<StoryWordsEntity>>() {
                    @Override
                    public void onSubscribe(Disposable d) { }

                    @Override
                    public void onSuccess(List<StoryWordsEntity> result) {
                        searchedWordsLiveData.setValue(result);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("SearchWordsError", "Error fetching searched words", e);
                    }
                });
        return searchedWordsLiveData;
    }

    public void fetchAllStories() {
        storiesDao.getAllStories()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<StoriesEntity>>() {
                    @Override
                    public void onSubscribe(Disposable d) { }

                    @Override
                    public void onSuccess(List<StoriesEntity> stories) {
                        allStoriesLiveData.setValue(stories);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("FetchAllStoriesError", "Error fetching stories", e);
                    }
                });
    }

    public void fetchAllWords() {
        storiesDao.getAllWords()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<StoryWordsEntity>>() {
                    @Override
                    public void onSubscribe(Disposable d) { }

                    @Override
                    public void onSuccess(List<StoryWordsEntity> words) {
                        wordsLiveData.setValue(words);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("FetchAllWordsError", "Error fetching words", e);
                    }
                });
    }

    public void fetchStoryQuestions(String storyName) {
        storiesDao.getStoryQuestions(storyName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<StoryQuestionsEntity>>() {
                    @Override
                    public void onSubscribe(Disposable d) { }

                    @Override
                    public void onSuccess(List<StoryQuestionsEntity> questions) {
                        storyQuestionsLiveData.setValue(questions);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("FetchStoryQuestionsError", "Error fetching story questions", e);
                    }
                });
    }

    public void addStory(StoriesEntity story) {
        storiesDao.addStory(story)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) { }

                    @Override
                    public void onComplete() {
                        Log.d("AddStorySuccess", "Story added successfully");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("AddStoryError", "Error adding story", e);
                    }
                });
    }

    public void addOcrText(TextOcrProcessorEntity textOcr) {
        storiesDao.addOcrText(textOcr)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) { }

                    @Override
                    public void onComplete() {
                        Log.d("AddOcrTextSuccess", "OCR text added successfully");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("AddOcrTextError", "Error adding OCR text", e);
                    }
                });
    }

    public void addStoryQuestion(StoryQuestionsEntity storyQuestion) {
        storiesDao.addStoryQuestion(storyQuestion)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) { }

                    @Override
                    public void onComplete() {
                        Log.d("AddQuestionSuccess", "Question added successfully");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("AddQuestionError", "Error adding question", e);
                    }
                });
    }

    public void updateStoryImage(int id, int newValue) {
        storiesDao.updateStoryImage(id, newValue)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) { }

                    @Override
                    public void onComplete() {
                        Log.d("UpdateImageSuccess", "Story image updated");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("UpdateImageError", "Error updating story image", e);
                    }
                });
    }

    public void deleteOcrText(int id) {
        storiesDao.deleteOcrText(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    public void deleteWord(StoryWordsEntity word) {
        storiesDao.deleteWord(word)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    public void saveWord(StoryWordsEntity word) {
        storiesDao.saveWord(word)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) { }

                    @Override
                    public void onComplete() {
                        Log.d("SaveWordSuccess", "Word saved successfully");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("SaveWordError", "Error saving word", e);
                    }
                });
    }

}

