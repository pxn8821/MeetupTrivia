package com.philng.MeetupTrivia.repositories;

import com.philng.MeetupTrivia.entities.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, String>
{
}
