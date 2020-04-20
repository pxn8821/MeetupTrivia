package com.philng.MeetupTrivia.entities;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Entity
public class Game
{
    public enum Status {
        CREATED,
        ACTIVE,
        FINISHED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany( targetEntity =  GameQuestion.class, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "gameId" )
    private List<GameQuestion> gameQuestions;

    private Timestamp timeCreated;

    private Timestamp timeStarted;

    private Integer minutesPerRound;

    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToMany( targetEntity = QuestionAnswer.class, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "gameId")
    private List<QuestionAnswer> answers;

    private int currentRound;

    private int numberOfRounds;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public List<GameQuestion> getGameQuestions()
    {
        return gameQuestions;
    }

    public void setGameQuestions(List<GameQuestion> gameQuestions)
    {
        this.gameQuestions = gameQuestions;
    }

    public List<QuestionAnswer> getAnswers()
    {
        return answers;
    }

    public void setAnswers(List<QuestionAnswer> answers)
    {
        this.answers = answers;
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

    public Status getStatus()
    {
        return status;
    }

    public void setStatus(Status status)
    {
        this.status = status;
    }
}
