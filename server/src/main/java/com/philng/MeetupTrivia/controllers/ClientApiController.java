package com.philng.MeetupTrivia.controllers;

import com.philng.MeetupTrivia.entities.Team;
import com.philng.MeetupTrivia.repositories.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.UUID;

@Controller
public class ClientApiController
{

    @Autowired
    TeamRepository teamRepository;

    @PostMapping("/api/registerTeam")
    @ResponseBody
    public String registerTeam(@RequestBody String teamName )
    {
        boolean teamAlreadyExists = teamRepository.findAll().stream().anyMatch( t -> t.getTeamName().equalsIgnoreCase(teamName));

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
