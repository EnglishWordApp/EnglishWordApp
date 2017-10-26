package com.elifakay.englishwordapp.Model;

/**
 * Created by elf_4 on 24.10.2017.
 */

public class QuestionScore {
    private String User;
    private String Score;

    public QuestionScore()
    {

    }
    public QuestionScore(String user,String score)
    {
        this.User=user;
        this.Score=score;
    }

    public String getUser() {
        return User;
    }

    public void setUser(String user) {
        User = user;
    }

    public String getScore() {
        return Score;
    }

    public void setScore(String score) {
        Score = score;
    }
}


