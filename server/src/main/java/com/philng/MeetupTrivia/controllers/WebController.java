package com.philng.MeetupTrivia.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class WebController
{

    @GetMapping("home.html")
    public String home(Model model )
    {
        return "home.html";
    }

    @GetMapping("admin/")
    public String adminController( Model model)
    {
        return "admin/admin.html";
    }

    @GetMapping("admin/createNewGame")
    public String createNewGame(Model model,
                                @PathVariable("numRounds") int numRounds,
                                @PathVariable("numQuestionsPerRound") int numQuestionsPerRound,
                                @PathVariable("difficulty") String difficulty )
    {
        return "admin/admin.html";
    }

}
