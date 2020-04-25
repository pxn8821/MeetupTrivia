package com.philng.MeetupTrivia.controllers.resultsmodels;

import com.philng.MeetupTrivia.entities.Game;

import java.sql.Timestamp;

public class GameResult
{
    private Long id;

    private Timestamp timeCreated;

    private Timestamp timeStarted;

    private Integer minutesPerRound;

    private Game.Status status;

    private int currentRound;

    private int numberOfRounds;



    public GameResult( Game game )
    {
        this.id = game.getId();
        this.timeCreated = game.getTimeCreated();
        this.timeStarted = game.getTimeStarted();
        this.minutesPerRound = game.getMinutesPerRound();
        this.status = game.getStatus();
        this.currentRound = game.getCurrentRound();
        this.numberOfRounds = game.getNumberOfRounds();
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Timestamp getTimeCreated()
    {
        return timeCreated;
    }

    public void setTimeCreated(Timestamp timeCreated)
    {
        this.timeCreated = timeCreated;
    }

    public Timestamp getTimeStarted()
    {
        return timeStarted;
    }

    public void setTimeStarted(Timestamp timeStarted)
    {
        this.timeStarted = timeStarted;
    }

    public Integer getMinutesPerRound()
    {
        return minutesPerRound;
    }

    public void setMinutesPerRound(Integer minutesPerRound)
    {
        this.minutesPerRound = minutesPerRound;
    }

    public Game.Status getStatus()
    {
        return status;
    }

    public void setStatus(Game.Status status)
    {
        this.status = status;
    }

    public int getCurrentRound()
    {
        return currentRound;
    }

    public void setCurrentRound(int currentRound)
    {
        this.currentRound = currentRound;
    }

    public int getNumberOfRounds()
    {
        return numberOfRounds;
    }

    public void setNumberOfRounds(int numberOfRounds)
    {
        this.numberOfRounds = numberOfRounds;
    }
}
