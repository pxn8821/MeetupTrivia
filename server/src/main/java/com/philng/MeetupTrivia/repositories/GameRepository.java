package com.philng.MeetupTrivia.repositories;

import com.philng.MeetupTrivia.entities.Game;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game, Long>
{
}
