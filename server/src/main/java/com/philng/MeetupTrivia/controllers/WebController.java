package com.philng.MeetupTrivia.controllers;

import com.google.gson.Gson;
import com.philng.MeetupTrivia.GameResultsGenerator;
import com.philng.MeetupTrivia.TriviaDatabaseInterface;
import com.philng.MeetupTrivia.entities.Game;
import com.philng.MeetupTrivia.entities.GameQuestion;
import com.philng.MeetupTrivia.repositories.GameQuestionRepository;
import com.philng.MeetupTrivia.repositories.GameRepository;
import com.philng.MeetupTrivia.responses.ResultsResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.ws.rs.QueryParam;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class WebController
{
    @Autowired
    GameRepository gameRepository;

    @Autowired
    GameQuestionRepository gameQuestionRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    GameResultsGenerator gameResultsGenerator;

    @GetMapping("/index.html")
    public String home(Model model)
    {
        return "client.html";
    }


    @PostMapping("admin/api/getResults")
    @ResponseBody
    public String getResults(@RequestBody String json)
    {
        JSONObject jsonObject = new JSONObject(json);

        int roundNumber = jsonObject.getInt("roundNumber");
        int gameId = jsonObject.getInt("gameId");

        Optional<Game> game = gameRepository.findById((long) gameId);

        ResultsResponse response;
        if (game.isPresent())
        {
            response = gameResultsGenerator.getResults(true, game.get());

            List<ResultsResponse.ResultsRound> rounds = response.getRoundResults();

            if( roundNumber != -1 )
            {
                response.setRoundResults(rounds.stream().filter(r -> r.getRoundId() == roundNumber).collect(Collectors.toList()));
            }

        }
        else
        {
            response = new ResultsResponse();
        }

        Gson gson = new Gson();
        return gson.toJson(response);
    }

    @GetMapping("admin/api/getGames")
    @ResponseBody
    public String getGames()
    {
        Gson gson = new Gson();
        return gson.toJson(gameRepository.findAll());
    }

    @GetMapping("admin/admin.html")
    public String adminController(Model model)
    {

        model.addAttribute( "activeGame", gameRepository.getActiveGame() );
        model.addAttribute("games", gameRepository.findAll());

        return "admin/admin.html";
    }

    @PostMapping("admin/setActiveGame")
    public RedirectView changeActiveGame(@RequestParam("gameSelect") Long gameId,
                                         RedirectAttributes redirectAttributes )
    {
        gameRepository.findAll().stream().forEach( g -> {
            if(g.getStatus() == Game.Status.ACTIVE && g.getId() != gameId )
            {
                g.setStatus( Game.Status.FINISHED );
                gameRepository.save( g );
            }

            if( g.getId() == gameId )
            {
                g.setStatus( Game.Status.ACTIVE );
                g.setTimeStarted( new Timestamp(System.currentTimeMillis()));
                gameRepository.save(g);
            }

        });
        redirectAttributes.addFlashAttribute("success", "Game " + gameId + " is now the active game");

        return new RedirectView("admin.html");
    }

    @GetMapping("admin/changeRound")
    public RedirectView changeRound( @QueryParam("roundNumber") int roundNumber,
                                     RedirectAttributes redirectAttributes )
    {
        Game game = gameRepository.getActiveGame();

        if( game == null )
        {
            redirectAttributes.addFlashAttribute("fail", "There is no game available");
        }
        else
        {
            game.setCurrentRound( roundNumber );
            gameRepository.saveAndFlush( game );

            // Send notifications to connected clients
            messagingTemplate.convertAndSend("/topic/updates", "newRound");

        }
        return new RedirectView("admin.html");
    }

    @GetMapping("admin/incrementRound")
    public RedirectView incrementRound( RedirectAttributes redirectAttributes )
    {
       Game game = gameRepository.getActiveGame();
       return changeRound( game.getCurrentRound() + 1, redirectAttributes);
    }

    @GetMapping("admin/decrementRound")
    public RedirectView decrementRound( RedirectAttributes redirectAttributes )
    {
        Game game = gameRepository.getActiveGame();
        return changeRound( game.getCurrentRound() - 1, redirectAttributes);
    }

    @GetMapping("admin/createNewGame")
    public RedirectView createNewGame(Model model,
                                      @QueryParam("numRounds") int numRounds,
                                      @QueryParam("numQuestionsPerRound") int numQuestionsPerRound,
                                      @QueryParam("difficulty") String difficulty,
                                      @QueryParam("roundMode") String roundMode,
                                      @QueryParam("minutePerRound") int minutePerRound,
                                      @QueryParam("category") String category,
                                      RedirectAttributes redirectAttributes) throws Exception
    {

        Game game = new Game();
        game.setNumberOfRounds( numRounds );
        game.setTimeCreated( new Timestamp( System.currentTimeMillis() ));

        if( roundMode.equalsIgnoreCase("auto") )
            game.setMinutesPerRound( minutePerRound );

        gameRepository.saveAndFlush( game );

        JSONObject json = null;
        try
        {
            json = TriviaDatabaseInterface.getTriviaQuestions( numRounds * numQuestionsPerRound, difficulty, category);
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

        // Send notifications to connected clients
        messagingTemplate.convertAndSend("/topic/updates", "newRound");

        redirectAttributes.addFlashAttribute( "success", "New game created");

        return new RedirectView("admin.html");
    }

}
