package com.philng.MeetupTrivia;

import com.philng.MeetupTrivia.entities.Game;
import com.philng.MeetupTrivia.entities.GameQuestion;
import com.philng.MeetupTrivia.entities.QuestionAnswer;
import com.philng.MeetupTrivia.entities.Team;
import com.philng.MeetupTrivia.repositories.GameRepository;
import com.philng.MeetupTrivia.repositories.TeamRepository;
import com.philng.MeetupTrivia.responses.ResultsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Scope("singleton")
public class GameResultsGenerator
{
    @Autowired
    GameRepository gameRepository;

    @Autowired
    TeamRepository teamRepository;

    public ResultsResponse getResults()
    {
        return getResults( false, gameRepository.getActiveGame() );
    }

    public ResultsResponse getResults( boolean getAdminInfo, Game game )
    {
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

            for( long i = 1; i <= (getAdminInfo ? game.getNumberOfRounds() : currentRound-1); i++ )
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

            Set<Team> teams = new LinkedHashSet<>();
            for( QuestionAnswer answer : game.getAnswers() )
            {
                teams.add( teamMap.get( answer.getTeamUUID() ) );

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

            // If not admin, then don't return UUIDs
            if( !getAdminInfo )
                teams.forEach( t -> t.setTeamUUID( null ));

            response.setTeams( new ArrayList<>(teams) );

            return response;
        }

        return new ResultsResponse();
    }
}
