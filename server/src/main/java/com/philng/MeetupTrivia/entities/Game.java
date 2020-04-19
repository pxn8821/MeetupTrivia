package com.philng.MeetupTrivia.entities;

import javax.persistence.*;
import java.util.List;

@Entity
public class Game
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany( targetEntity =  GameQuestion.class, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "gameId" )
    private List<GameQuestion> gameQuestions;


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
}
