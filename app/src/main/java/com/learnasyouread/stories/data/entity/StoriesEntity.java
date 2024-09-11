package com.learnasyouread.stories.data.entity;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
@Entity(tableName = "stories_table")

public class StoriesEntity implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "story")
    private String story;

    @ColumnInfo(name = "story_title")
    private String storyTitle;

    @ColumnInfo(name = "story_image")
    private int storyImage;

    public StoriesEntity(String story, String storyTitle, int storyImage) {
        this.story = story;
        this.storyTitle = storyTitle;
        this.storyImage = storyImage;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStory() {
        return story;
    }

    public void setStory(String story) {
        this.story = story;
    }

    public String getStoryTitle() {
        return storyTitle;
    }

    public void setStoryTitle(String storyTitle) {
        this.storyTitle = storyTitle;
    }

    public int getStoryImage() {
        return storyImage;
    }

    public void setStoryImage(int storyImage) {
        this.storyImage = storyImage;
    }
}