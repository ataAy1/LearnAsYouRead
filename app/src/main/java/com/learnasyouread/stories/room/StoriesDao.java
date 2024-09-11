package com.learnasyouread.stories.room;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.learnasyouread.stories.data.entity.StoriesEntity;
import com.learnasyouread.stories.data.entity.StoryQuestionsEntity;
import com.learnasyouread.stories.data.entity.StoryWordsEntity;
import com.learnasyouread.stories.data.entity.TextOcrProcessorEntity;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface StoriesDao {
    @Insert
    Completable saveWord(StoryWordsEntity hikayeKelimeleri);

    @Insert
    Completable addStory(StoriesEntity stories);

    @Insert
    Completable addOcrText(TextOcrProcessorEntity textOcr);

    @Query("DELETE FROM stories_table WHERE id = :id")
    Single<Integer> deleteStoryById(int id);

    @Query("DELETE FROM ocr_table WHERE id = :id")
    Single<Integer> deleteOcrText(int id);

    @Insert
    Completable addStoryQuestion(StoryQuestionsEntity storyQuestions);

    @Query("UPDATE stories_table SET story_image = :newValue WHERE id = :id")
    Completable updateStoryImage(int id, int newValue);


    @Query("SELECT * FROM ocr_table")
    Single<List<TextOcrProcessorEntity>> getAllOcrTexts();

    @Query("SELECT * FROM words_table")
    Single<List<StoryWordsEntity>> getAllWords();

    @Query("SELECT * FROM stories_table")
    Single<List<StoriesEntity>> getAllStories();

    @Query("SELECT * FROM questions_table WHERE story = :getQuestion")
    Single<List<StoryQuestionsEntity>> getStoryQuestions(String getQuestion);


    @Query("SELECT * FROM words_table WHERE turkishWord LIKE :aramaKelimesi || '%'")
    Single<List<StoryWordsEntity>> searchWords(String aramaKelimesi);

    @Delete
    Completable deleteWord(StoryWordsEntity hikayeKelimeleri);


}
