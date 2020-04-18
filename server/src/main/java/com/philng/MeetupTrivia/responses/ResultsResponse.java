package com.philng.MeetupTrivia.responses;

import com.philng.MeetupTrivia.entities.GameQuestion;

import java.util.ArrayList;
import java.util.List;

public class ResultsResponse
{

    private long gameId;
    private List<ResultsRound> roundResults = new ArrayList<>();

    public long getGameId()
    {
        return gameId;
    }

    public void setGameId(long gameId)
    {
        this.gameId = gameId;
    }

    public List<ResultsRound> getRoundResults()
    {
        return roundResults;
    }

    public void setRoundResults(List<ResultsRound> roundResults)
    {
        this.roundResults = roundResults;
    }

    public static class ResultsRound {
        private long roundId;

        private List<GameQuestion> questions = new ArrayList<>();

        private List<TeamAnswers> answers = new ArrayList<>();

        public long getRoundId()
        {
            return roundId;
        }

        public void setRoundId(long roundId)
        {
            this.roundId = roundId;
        }

        public List<GameQuestion> getQuestions()
        {
            return questions;
        }

        public void setQuestions(List<GameQuestion> questions)
        {
            this.questions = questions;
        }

        public List<TeamAnswers> getAnswers()
        {
            return answers;
        }

        public void setAnswers(List<TeamAnswers> answers)
        {
            this.answers = answers;
        }

        public class TeamAnswers {
            private long questionId;
            private String teamName;
            private String choice;
            private boolean isCorrect;

            public long getQuestionId()
            {
                return questionId;
            }

            public void setQuestionId(long questionId)
            {
                this.questionId = questionId;
            }

            public String getTeamName()
            {
                return teamName;
            }

            public void setTeamName(String teamName)
            {
                this.teamName = teamName;
            }

            public String getChoice()
            {
                return choice;
            }

            public void setChoice(String choice)
            {
                this.choice = choice;
            }

            public boolean isCorrect()
            {
                return isCorrect;
            }

            public void setCorrect(boolean correct)
            {
                isCorrect = correct;
            }
        }
    }
}


