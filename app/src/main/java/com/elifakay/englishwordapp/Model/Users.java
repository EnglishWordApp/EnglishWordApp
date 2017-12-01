package com.elifakay.englishwordapp.Model;

/**
 * Created by elf_4 on 29.11.2017.
 */

public class Users {
    public String name;
    public String score;

    public Users()
    {

    }
    public Users(String name,String score) {
        this.name = name;
        this.score=score;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }
}

