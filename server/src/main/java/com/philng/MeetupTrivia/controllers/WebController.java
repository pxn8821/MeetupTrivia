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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.ws.rs.QueryParam;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    @GetMapping("admin")
    public String adminController( Model model )
    {
        Game game = gameRepository.getLatestGame();

        if( game == null )
            return "admin/admin.html";

        model.addAttribute("currentRound", game.getCurrentRound() );

        Map<Long, List<GameQuestion>> gameQuestions = new LinkedHashMap<>();
        game.getGameQuestions().forEach( q -> {
            if( !gameQuestions.containsKey( q.getRoundNumber()) )
            {
                gameQuestions.put( q.getRoundNumber(), new ArrayList<>() );
            }

            gameQuestions.get( q.getRoundNumber() ).add( q );
        });
        model.addAttribute("questions", gameQuestions );
        return "admin/admin.html";
    }

    @GetMapping("admin/changeRound")
    public RedirectView changeRound( @QueryParam("roundNumber") int roundNumber,
                                     RedirectAttributes redirectAttributes )
    {
        Game game = gameRepository.getLatestGame();

        if( game == null )
        {
            redirectAttributes.addFlashAttribute("fail", "There is no game available");
        }
        else
        {
            game.setCurrentRound( roundNumber );
            gameRepository.saveAndFlush( game );
        }
        return new RedirectView("/admin");
    }

    @GetMapping("admin/createNewGame")
    public RedirectView createNewGame(Model model,
                                      @QueryParam("numRounds") int numRounds,
                                      @QueryParam("numQuestionsPerRound") int numQuestionsPerRound,
                                      @QueryParam("difficulty") String difficulty,
                                      RedirectAttributes redirectAttributes) throws Exception
    {

        Game game = new Game();
        gameRepository.saveAndFlush( game );

        JSONObject json = null;
        try
        {
            json = TriviaDatabaseInterface.getTriviaQuestions( numRounds * numQuestionsPerRound, difficulty);
        }
        catch( Exception e )
        {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("fail", "Unable to get questions from API");
            return new RedirectView("/admin");
        }

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

        redirectAttributes.addFlashAttribute( "success", "New game created");

        return new RedirectView("/admin");
    }

}
