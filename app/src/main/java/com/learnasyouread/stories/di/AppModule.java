package com.learnasyouread.stories.di;

import com.learnasyouread.stories.data.repo.Repository;
import com.learnasyouread.stories.room.StoriesDao;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class AppModule {

    @Singleton
    @Provides
    public Repository provideHikayeRepo(StoriesDao storiesDao) {
        return new Repository(storiesDao);
    }
}
