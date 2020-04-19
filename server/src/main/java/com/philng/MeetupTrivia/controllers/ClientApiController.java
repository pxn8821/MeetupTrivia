package com.philng.MeetupTrivia.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.philng.MeetupTrivia.entities.Game;
import com.philng.MeetupTrivia.entities.GameQuestion;
import com.philng.MeetupTrivia.entities.QuestionAnswer;
import com.philng.MeetupTrivia.entities.Team;
import com.philng.MeetupTrivia.repositories.GameRepository;
import com.philng.MeetupTrivia.repositories.QuestionAnswerRepository;
import com.philng.MeetupTrivia.repositories.TeamRepository;
import com.philng.MeetupTrivia.responses.QuestionsResponse;
import com.philng.MeetupTrivia.responses.ResultsResponse;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class ClientApiController
{
    @Autowired
    TeamRepository teamRepository;

    @Autowired
    GameRepository gameRepository;

    @Autowired
    QuestionAnswerRepository questionAnswerRepository;

    @GetMapping("/api/getResults")
    @ResponseBody
    public String getResults(){
        Game game = gameRepository.getLatestGame();

        if( game != null )
        {
            ResultsResponse response = new ResultsResponse();
            response.setGameId( game.getId() );

            int currentRound = game.getCurrentRound();

            Map<Long, ResultsResponse.ResultsRound> rounds = new LinkedHashMap<>();
            Map<Long, GameQuestion> questionMap = new HashMap<>();
            Map<String, Team> teamMap = new HashMap();

            for( Team team : teamRepository.findAll() )
            {
                teamMap.put( team.getTeamUUID(), team );
            }

            for( long i = 1; i < currentRound; i++ )
            {
                ResultsResponse.ResultsRound round = new ResultsResponse.ResultsRound();
                round.setRoundId( i );
                rounds.put( i, round);
                response.getRoundResults().add( round );
            }

            for( GameQuestion question : game.getGameQuestions() )
            {
                questionMap.put(question.getId(), question);
                ResultsResponse.ResultsRound round = rounds.get( question.getRoundNumber() );
                if(round != null )
                {
                    round.getQuestions().add( question );
                }
            }

            for( QuestionAnswer answer : game.getAnswers() )
            {
                GameQuestion question = questionMap.get( answer.getQuestionId() );

                long roundNumber = question.getRoundNumber();
                ResultsResponse.ResultsRound round = rounds.get( roundNumber );

                if( round != null )
                {
                    ResultsResponse.ResultsRound.TeamAnswers teamAnswers = new ResultsResponse.ResultsRound.TeamAnswers();

                    teamAnswers.setChoice( answer.getChoice() );
                    teamAnswers.setQuestionId( answer.getQuestionId() );
                    teamAnswers.setTeamName( teamMap.get( answer.getTeamUUID() ).getTeamName() );
                    teamAnswers.setCorrect( question.getCorrectAnswer().equalsIgnoreCase( answer.getChoice() ) );

                    round.getAnswers().add(teamAnswers);
                }
            }


            Gson gson = new Gson();
            return gson.toJson( response );
        }

        Gson gson = new Gson();

        return gson.toJson( new ResultsResponse() );
    }

    @PostMapping("/api/submitQuestionAnswer")
    @ResponseBody
    public String submitQuestionAnswer(@RequestBody String json)
    {
        JSONObject jsonObject = new JSONObject(json);

        long questionId = jsonObject.getLong("questionId");
        String choice = jsonObject.getString("choice");
        String teamUUID = jsonObject.getString("teamUUID");

        QuestionAnswer answer = questionAnswerRepository.findByTeamAndQuestionId( questionId, teamUUID );

        if( answer == null )
        {
            answer = new QuestionAnswer();
            answer.setGameId( gameRepository.getLatestGame().getId() );
            answer.setTeamUUID( teamUUID );
            answer.setQuestionId( questionId );
        }

        answer.setChoice( choice );


        questionAnswerRepository.save( answer );


        return "Success";
    }

    @PostMapping("/api/getQuestionsForCurrentRound")
    @ResponseBody
    public String getQuestionsForCurrentRound(@RequestBody String teamUUID)
    {
        Game game = gameRepository.getLatestGame();

        if( game != null )
        {
            List<QuestionAnswer> answers = game.getAnswers()
                    .stream()
                    .filter( a -> a.getTeamUUID().equalsIgnoreCase(teamUUID))
                    .collect(Collectors.toList());

            int currentRound = game.getCurrentRound() > game.getNumberOfRounds() ? -1 : game.getCurrentRound();

            List<GameQuestion> questions = game.getGameQuestions()
                    .stream()
                    .filter( q -> q.getRoundNumber() == currentRound )
                    .collect(Collectors.toList());

            QuestionsResponse response = new QuestionsResponse();
            response.setCurrentRound( currentRound );

            questions.forEach( q -> {
                QuestionsResponse.Questions question = new QuestionsResponse.Questions();
                question.setQuestionId( q.getId() );
                question.setQuestion( q.getQuestion() );
                for( String incorrectAnswer : q.getIncorrectAnswers() )
                    question.getChoices().add( incorrectAnswer );

                question.getChoices().add( q.getCorrectAnswer() );

                Collections.shuffle( question.getChoices() );

                Optional<QuestionAnswer> answer = answers
                        .stream()
                        .filter( a -> a.getQuestionId() == question.getQuestionId() )
                        .findFirst();

                if( answer.isPresent() )
                {
                    question.setTeamAnswer( answer.get().getChoice() );
                }

                response.getQuestions().add( question );
            });

            Gson gson = new Gson();

            return gson.toJson( response );
        }
        else
        {
            return "{}";
        }
    }


    @PostMapping("/api/loginTeam")
    @ResponseBody
    public String loginTeam(@RequestBody String teamUUID)
    {
        Optional<Team> team = teamRepository.findAll()
                .stream()
                .filter( t -> t.getTeamUUID().equalsIgnoreCase(teamUUID.trim()))
                .findFirst();

        if( team.isPresent() )
        {
            return team.get().getTeamName();
        }
        else
        {
            return "";
        }
    }

    @PostMapping("/api/registerTeam")
    @ResponseBody
    public String registerTeam(@RequestBody String teamName )
    {
        boolean teamAlreadyExists = teamRepository.findAll().stream().anyMatch( t -> t.getTeamName().equalsIgnoreCase(teamName.trim()));


        if( teamAlreadyExists )
        {
            return "TeamAlreadyExists";
        }

        List<Team> teams = teamRepository.findAll();

        String uuid = null;
        boolean uuidExists = true;
        while( uuidExists )
        {
            uuidExists = false;
            uuid = generateUUID();
            for( Team t : teams )
            {
                if( t.getTeamUUID().equals( uuid ) )
                {
                    uuidExists = true;
                    break;
                }
            }
        }

        Team newTeam = new Team();
        newTeam.setTeamUUID( uuid );
        newTeam.setTeamName( teamName );

        teamRepository.saveAndFlush( newTeam );

        return newTeam.getTeamUUID();

    }

    private String generateUUID()
    {
        return UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }

}
