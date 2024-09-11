package com.learnasyouread.stories.di;

import android.content.Context;

import com.learnasyouread.stories.room.Database;
import com.learnasyouread.stories.room.StoriesDao;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class DatabaseModule {

    @Provides
    @Singleton
    public Database provideDatabase(@ApplicationContext Context context) {
        return Database.getInstance(context);
    }

    @Provides
    public StoriesDao provideHikayelerDao(Database db) {
        return db.getStoriesDao();
    }

}

