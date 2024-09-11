package com.learnasyouread.stories.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "words_table")
public class StoryWordsEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "turkishWord")
    public String turkishWord;

    @ColumnInfo(name = "englishWord")
    public String englishWord;

    public StoryWordsEntity(String turkishWord, String englishWord) {
        this.turkishWord = turkishWord;
        this.englishWord = englishWord;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTurkishWord() {
        return turkishWord;
    }

    public void setTurkishWord(String turkishWord) {
        this.turkishWord = turkishWord;
    }

    public String getEnglishWord() {
        return englishWord;
    }

    public void setEnglishWord(String englishWord) {
        this.englishWord = englishWord;
    }
}
