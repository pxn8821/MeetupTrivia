package com.philng.MeetupTrivia.entities;

import javax.persistence.*;
import java.util.List;

@Entity
public class GameQuestion
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long gameId;

    private Long roundNumber;

    private String question;

    private String correctAnswer;

    @ElementCollection
    private List<String> incorrectAnswers;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getGameId()
    {
        return gameId;
    }

    public void setGameId(Long gameId)
    {
        this.gameId = gameId;
    }

    public Long getRoundNumber()
    {
        return roundNumber;
    }

    public void setRoundNumber(Long roundNumber)
    {
        this.roundNumber = roundNumber;
    }

    public String getQuestion()
    {
        return question;
    }

    public void setQuestion(String question)
    {
        this.question = question;
    }

    public String getCorrectAnswer()
    {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer)
    {
        this.correctAnswer = correctAnswer;
    }

    public List<String> getIncorrectAnswers()
    {
        return incorrectAnswers;
    }

    public void setIncorrectAnswers(List<String> incorrectAnswers)
    {
        this.incorrectAnswers = incorrectAnswers;
    }
}
