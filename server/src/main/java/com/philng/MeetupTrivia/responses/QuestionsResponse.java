package com.philng.MeetupTrivia.responses;

import java.util.ArrayList;
import java.util.List;

public class QuestionsResponse
{
    private int currentRound;

    private List<Questions> questions = new ArrayList<>();

    public int getCurrentRound()
    {
        return currentRound;
    }

    public void setCurrentRound(int currentRound)
    {
        this.currentRound = currentRound;
    }

    public List<Questions> getQuestions()
    {
        return questions;
    }

    public void setQuestions(List<Questions> questions)
    {
        this.questions = questions;
    }

    public static class Questions
    {
        private long questionId;
        private String question;
        private List<String> choices = new ArrayList<>();
        private String teamAnswer;

        public String getQuestion()
        {
            return question;
        }

        public void setQuestion(String question)
        {
            this.question = question;
        }

        public List<String> getChoices()
        {
            return choices;
        }

        public void setChoices(List<String> choices)
        {
            this.choices = choices;
        }

        public String getTeamAnswer()
        {
            return teamAnswer;
        }

        public void setTeamAnswer(String teamAnswer)
        {
            this.teamAnswer = teamAnswer;
        }

        public long getQuestionId()
        {
            return questionId;
        }

        public void setQuestionId(long questionId)
        {
            this.questionId = questionId;
        }
    }
}
