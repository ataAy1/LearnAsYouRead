package com.learnasyouread.stories.room;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.learnasyouread.stories.data.entity.StoriesEntity;
import com.learnasyouread.stories.data.entity.StoryQuestionsEntity;
import com.learnasyouread.stories.data.entity.StoryWordsEntity;
import com.learnasyouread.stories.data.entity.TextOcrProcessorEntity;

@androidx.room.Database(entities = {StoriesEntity.class, StoryWordsEntity.class, StoryQuestionsEntity.class, TextOcrProcessorEntity.class}, version = 2, exportSchema = false)
public abstract class Database extends RoomDatabase {

    public abstract StoriesDao getStoriesDao();

    private static volatile Database INSTANCE;

    public static Database getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (Database.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    Database.class,
                                    "StoriesEnglish"
                            )
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
