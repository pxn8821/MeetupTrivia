package com.philng.MeetupTrivia.entities;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Team
{
    public String teamName;

    @Id
    public String teamUUID;


    public String getTeamName()
    {
        return teamName;
    }

    public void setTeamName(String teamName)
    {
        this.teamName = teamName;
    }

    public String getTeamUUID()
    {
        return teamUUID;
    }

    public void setTeamUUID(String teamUUID)
    {
        this.teamUUID = teamUUID;
    }
}
