package com.philng.MeetupTrivia.controllers;

import com.philng.MeetupTrivia.TriviaDatabaseInterface;
import com.philng.MeetupTrivia.entities.Game;
import com.philng.MeetupTrivia.entities.GameQuestion;
import com.philng.MeetupTrivia.repositories.GameQuestionRepository;
import com.philng.MeetupTrivia.repositories.GameRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.ws.rs.QueryParam;
import java.util.List;

@Controller
public class WebController
{

    @Autowired
    GameRepository gameRepository;

    @Autowired
    GameQuestionRepository gameQuestionRepository;

    @GetMapping("home.html")
    public String home(Model model )
    {
        return "home.html";
    }

    @GetMapping("admin/")
    public String adminController( Model model)
    {
        List<Game> games = gameRepository.findAll();
        Game game = games.get( games.size() - 1 );



        return "admin/admin.html";
    }

    @GetMapping("admin/createNewGame")
    public String createNewGame(Model model,
                                @QueryParam("numRounds") int numRounds,
                                @QueryParam("numQuestionsPerRound") int numQuestionsPerRound,
                                @QueryParam("difficulty") String difficulty ) throws Exception
    {

        Game game = new Game();
        gameRepository.saveAndFlush( game );

        JSONObject json = TriviaDatabaseInterface.getTriviaQuestions( numRounds * numQuestionsPerRound, difficulty);
        JSONArray questionArray = json.getJSONArray("results" );

        long currentRound = 1;
        for( int i = 0; i < questionArray.length(); i++ )
        {
            JSONObject questionJson = questionArray.getJSONObject(i);

            GameQuestion question = new GameQuestion();
            question.setCorrectAnswer( questionJson.getString("correct_answer" ) );
            question.setGameId( game.getId() );
            question.setRoundNumber( currentRound );
            question.setQuestion( questionJson.getString("question" ));

            JSONArray incorrect_answers = questionJson.getJSONArray("incorrect_answers");
            for( int j = 0; j < incorrect_answers.length(); j++)
            {
                question.getIncorrectAnswers().add( incorrect_answers.getString(j) );
            }

            gameQuestionRepository.save( question );

            if( (i+1) % numQuestionsPerRound == 0 )
                currentRound += 1;
        }

        return "admin/admin.html";
    }

}
