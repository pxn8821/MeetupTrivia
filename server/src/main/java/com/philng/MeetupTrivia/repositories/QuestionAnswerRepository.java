package com.philng.MeetupTrivia.repositories;

import com.philng.MeetupTrivia.entities.QuestionAnswer;
import com.philng.MeetupTrivia.repositories.custom.QuestionAnswerCustomRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionAnswerRepository extends JpaRepository<QuestionAnswer, Long>, QuestionAnswerCustomRepository
{
}
