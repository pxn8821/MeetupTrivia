package com.philng.MeetupTrivia.controllers.resultsmodels;

import com.philng.MeetupTrivia.entities.Game;

import java.util.ArrayList;
import java.util.List;

public class GameDetailResult extends GameResult
{
    private List<Round> rounds = new ArrayList<>();
    private List<Team> teamsPresent = new ArrayList<>();


    public GameDetailResult()
    {
        super( null );
    }

    public GameDetailResult( Game game )
    {
        super( game );
    }

    public List<Round> getRounds()
    {
        return rounds;
    }

    public void setRounds(List<Round> rounds)
    {
        this.rounds = rounds;
    }

    public List<Team> getTeamsPresent()
    {
        return teamsPresent;
    }

    public void setTeamsPresent(List<Team> teamsPresent)
    {
        this.teamsPresent = teamsPresent;
    }

    public static class Round {
        private Long roundNumber;

        private List<RoundQuestion> questions = new ArrayList<>();


        public Long getRoundNumber()
        {
            return roundNumber;
        }

        public void setRoundNumber(Long roundNumber)
        {
            this.roundNumber = roundNumber;
        }

        public List<RoundQuestion> getQuestions()
        {
            return questions;
        }

        public void setQuestions(List<RoundQuestion> questions)
        {
            this.questions = questions;
        }

        public static class RoundQuestion{
            private String question;
            private List<String> choices = new ArrayList<>();
            private String correctAnswer;
            private List<TeamAnswer> teamAnswers = new ArrayList<>();

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

            public String getCorrectAnswer()
            {
                return correctAnswer;
            }

            public void setCorrectAnswer(String correctAnswer)
            {
                this.correctAnswer = correctAnswer;
            }

            public List<TeamAnswer> getTeamAnswers()
            {
                return teamAnswers;
            }

            public void setTeamAnswers(List<TeamAnswer> teamAnswers)
            {
                this.teamAnswers = teamAnswers;
            }

            public static class TeamAnswer {
                private Team team;
                private String choice;
                private boolean isCorrect;

                public Team getTeam()
                {
                    return team;
                }

                public void setTeam(Team team)
                {
                    this.team = team;
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


    public static class Team{
        private String teamName;
        private int numberAnswered = 0;
        private int numberCorrect = 0;

        public String getTeamName()
        {
            return teamName;
        }

        public void setTeamName(String teamName)
        {
            this.teamName = teamName;
        }

        public int getNumberAnswered()
        {
            return numberAnswered;
        }

        public void setNumberAnswered(int numberAnswered)
        {
            this.numberAnswered = numberAnswered;
        }

        public int getNumberCorrect()
        {
            return numberCorrect;
        }

        public void setNumberCorrect(int numberCorrect)
        {
            this.numberCorrect = numberCorrect;
        }

        public void incrementNumberAnswered()
        {
            numberAnswered++;
        }

        public void incrementNumberCorrect()
        {
            numberCorrect++;
        }
    }
}
