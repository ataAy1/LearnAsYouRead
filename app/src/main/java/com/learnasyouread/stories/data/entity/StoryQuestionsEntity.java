package com.learnasyouread.stories.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "questions_table")
public class StoryQuestionsEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "question")
    private String question;

    @ColumnInfo(name = "answerA")
    private String answerA;

    @ColumnInfo(name = "answerB")
    private String answerB;

    @ColumnInfo(name = "answerC")
    private String answerC;

    @ColumnInfo(name = "answerD")
    private String answerD;

    @ColumnInfo(name = "correctAnswer")
    private String correctAnswer;

    @ColumnInfo(name = "story")
    private String story;

    public StoryQuestionsEntity(String question, String answerA, String answerB, String answerC, String answerD, String correctAnswer, String story) {
        this.question = question;
        this.answerA = answerA;
        this.answerB = answerB;
        this.answerC = answerC;
        this.answerD = answerD;
        this.correctAnswer = correctAnswer;
        this.story = story;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswerA() {
        return answerA;
    }

    public void setAnswerA(String answerA) {
        this.answerA = answerA;
    }

    public String getAnswerB() {
        return answerB;
    }

    public void setAnswerB(String answerB) {
        this.answerB = answerB;
    }

    public String getAnswerC() {
        return answerC;
    }

    public void setAnswerC(String answerC) {
        this.answerC = answerC;
    }

    public String getAnswerD() {
        return answerD;
    }

    public void setAnswerD(String answerD) {
        this.answerD = answerD;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public String getStory() {
        return story;
    }

    public void setStory(String story) {
        this.story = story;
    }
}
