package com.philng.MeetupTrivia.repositories;

import com.philng.MeetupTrivia.entities.GameQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameQuestionRepository extends JpaRepository<GameQuestion, Long>
{
}
