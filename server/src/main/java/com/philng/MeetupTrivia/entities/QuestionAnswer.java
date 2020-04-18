package com.philng.MeetupTrivia.entities;

import javax.persistence.*;

@Entity
@Table( indexes = { @Index(name="QUESTION_ANSWER_ID1", columnList = "questionId,teamUUID") } )

public class QuestionAnswer
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long questionId;

    private Long gameId;

    private String teamUUID;

    private String choice;


    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getQuestionId()
    {
        return questionId;
    }

    public void setQuestionId(Long questionId)
    {
        this.questionId = questionId;
    }

    public Long getGameId()
    {
        return gameId;
    }

    public void setGameId(Long gameId)
    {
        this.gameId = gameId;
    }

    public String getTeamUUID()
    {
        return teamUUID;
    }

    public void setTeamUUID(String teamUUID)
    {
        this.teamUUID = teamUUID;
    }

    public String getChoice()
    {
        return choice;
    }

    public void setChoice(String choice)
    {
        this.choice = choice;
    }
}

