package com.philng.MeetupTrivia.controllers;

import com.philng.MeetupTrivia.GameResultsGenerator;
import com.philng.MeetupTrivia.controllers.resultsmodels.GameDetailResult;
import com.philng.MeetupTrivia.controllers.resultsmodels.GameResult;
import com.philng.MeetupTrivia.entities.Game;
import com.philng.MeetupTrivia.entities.GameQuestion;
import com.philng.MeetupTrivia.entities.Team;
import com.philng.MeetupTrivia.repositories.GameRepository;
import com.philng.MeetupTrivia.repositories.QuestionAnswerRepository;
import com.philng.MeetupTrivia.repositories.TeamRepository;
import com.philng.MeetupTrivia.responses.QuestionsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
public class ResultsAPIController
{
    @Autowired
    TeamRepository teamRepository;

    @Autowired
    GameRepository gameRepository;

    @Autowired
    QuestionAnswerRepository questionAnswerRepository;

    @Autowired
    GameResultsGenerator gameResultsGenerator;



    @GetMapping("/api/results/games")
    public ResponseEntity<List<GameResult>> getGames()
    {
        List<GameResult> response = new ArrayList<>();
        gameRepository.findAll().forEach( g -> response.add( new GameResult( g ) ) );
        return new ResponseEntity<>( response, HttpStatus.OK );
    }

    @GetMapping("/api/results/game/{id}")
    public ResponseEntity<GameDetailResult> getGameDetail( @PathVariable("id") Long id )
    {
        Optional<Game> gameResult = gameRepository.findById( id );

        Map<String, Team> teamUUIDMap = teamRepository.findAll().stream().collect(Collectors.toMap( Team::getTeamUUID, Function.identity()));

        if( gameResult.isPresent() && gameResult.get().getStatus() != Game.Status.ACTIVE )
        {
            Game game = gameResult.get();

            GameDetailResult result = new GameDetailResult( game );

            Map<Long, GameQuestion> gameQuestionIdMap = game.getGameQuestions().stream().collect(Collectors.toMap( GameQuestion::getId, Function.identity()));

            //Round Number, round
            Map<Long, GameDetailResult.Round> roundMap = new LinkedHashMap<>();

            //Question ID, answer
            Map<Long, List<GameDetailResult.Round.RoundQuestion.TeamAnswer>> answerMap = new LinkedHashMap<>();

            Set<String> teamsParticipating = new LinkedHashSet<>();

            Map<String, GameDetailResult.Team> overallTeamResult = new LinkedHashMap<>();

            // Set up teams
            game.getAnswers().forEach( a -> {
                GameDetailResult.Round.RoundQuestion.TeamAnswer answer = new GameDetailResult.Round.RoundQuestion.TeamAnswer();

                GameDetailResult.Team team = new GameDetailResult.Team();
                team.setTeamName( teamUUIDMap.get( a.getTeamUUID() ).getTeamName() );

                answer.setChoice( a.getChoice() );
                answer.setTeam( team );

                if( !answerMap.containsKey( a.getQuestionId() ) )
                {
                    answerMap.put( a.getQuestionId(), new ArrayList<>());
                }

                // Figure out if the answer is correct or not
                answer.setCorrect( gameQuestionIdMap.get( a.getQuestionId() ).getCorrectAnswer().equalsIgnoreCase( a.getChoice() ) );

                answerMap.get( a.getQuestionId() ).add(answer);

                teamsParticipating.add( team.getTeamName() );

                // Add result to the overall team result
                if( !overallTeamResult.containsKey( team.getTeamName() ) )
                {
                    GameDetailResult.Team teamResult = new GameDetailResult.Team();
                    teamResult.setTeamName( team.getTeamName() );
                    overallTeamResult.put( team.getTeamName(), teamResult );
                }

                overallTeamResult.get( team.getTeamName() ).incrementNumberAnswered();
                if( answer.isCorrect() )
                    overallTeamResult.get( team.getTeamName() ).incrementNumberCorrect();

            });


            // Set up questions
            game.getGameQuestions()
                .stream()
                .forEach( q -> {
                    if( !roundMap.containsKey(q.getRoundNumber())) {
                        GameDetailResult.Round rnd = new GameDetailResult.Round();
                        rnd.setRoundNumber( q.getRoundNumber() );
                        roundMap.put( q.getRoundNumber(), rnd );
                    }

                    GameDetailResult.Round rnd = roundMap.get( q.getRoundNumber() );

                    GameDetailResult.Round.RoundQuestion roundQuestion = new GameDetailResult.Round.RoundQuestion();
                    roundQuestion.setCorrectAnswer( q.getCorrectAnswer() );
                    roundQuestion.setQuestion( q.getQuestion() );
                    roundQuestion.getChoices().add( q.getCorrectAnswer() );

                    q.getIncorrectAnswers().forEach( q1 -> roundQuestion.getChoices().add( q1 ) );
                    Collections.shuffle( roundQuestion.getChoices() );

                    Set<String> teams = new HashSet<>( teamsParticipating );
                    answerMap.get( q.getId() ).forEach( a -> {
                        roundQuestion.getTeamAnswers().add( a );
                        teams.remove( a.getTeam().getTeamName() );
                    });

                    for( String team : teams )
                    {
                        GameDetailResult.Round.RoundQuestion.TeamAnswer teamAnswer = new GameDetailResult.Round.RoundQuestion.TeamAnswer();
                        teamAnswer.setCorrect( false );
                        teamAnswer.setTeam( new GameDetailResult.Team() );
                        teamAnswer.getTeam().setTeamName( team );
                        roundQuestion.getTeamAnswers().add(teamAnswer);
                    }

                    rnd.getQuestions().add( roundQuestion );
                });


            // Calculate each team's number of correct answers for a round
            roundMap.forEach( ( index, round ) -> {
                Map<String, GameDetailResult.Team> teams = new HashMap<>();

                round.getQuestions().forEach( question -> {
                    question.getTeamAnswers().forEach( answer -> {
                        if( !teams.containsKey( answer.getTeam().getTeamName() ) )
                        {
                            GameDetailResult.Team team = new GameDetailResult.Team();
                            team.setTeamName( answer.getTeam().getTeamName() );
                            team.setNumberAnswered( 0 );
                            team.setNumberCorrect( 0 );
                            teams.put( team.getTeamName(), team );
                        }

                        GameDetailResult.Team team = teams.get( answer.getTeam().getTeamName() );

                        if( answer.getChoice() != null )
                            team.incrementNumberAnswered();

                        if( answer.isCorrect() )
                            team.incrementNumberCorrect();
                    });
                });

                teams.forEach( (index2, team ) -> round.getTeamResults().add( team ));
            });

            overallTeamResult.forEach( (teamName, team) -> result.getTeamsPresent().add(team) );
            roundMap.forEach( (index, round) -> result.getRounds().add( round ));

            return new ResponseEntity<>( result, HttpStatus.OK );
        }
        return new ResponseEntity<>( null, HttpStatus.UNAUTHORIZED );
    }

}
