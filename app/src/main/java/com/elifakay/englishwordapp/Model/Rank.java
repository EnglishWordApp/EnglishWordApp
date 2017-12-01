package com.elifakay.englishwordapp.Model;

/**
 * Created by elf_4 on 30.11.2017.
 */

public class Rank {

    private String name;
    private long score;
    private long rank;

    public Rank()
    {}

    public Rank(String name,long score)
    {
        this.name=name;
        this.score=score;
        this.rank=0;
    }
    public String getName() {
        return name;
    }

    public void setName(String userName) {
        this.name = userName;
    }

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }

    public long getRank() {
        return rank;
    }

    public void setRank(long rank) {
        this.rank = rank;
    }
}
