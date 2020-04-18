package com.philng.MeetupTrivia.repositories.custom;

import com.philng.MeetupTrivia.entities.Game;

public interface GameCustomRepository
{
    public Game getLatestGame();
}
