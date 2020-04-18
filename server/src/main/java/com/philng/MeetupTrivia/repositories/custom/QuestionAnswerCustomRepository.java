package com.philng.MeetupTrivia.repositories.custom;

import com.philng.MeetupTrivia.entities.QuestionAnswer;

import java.util.List;

public interface QuestionAnswerCustomRepository
{
    QuestionAnswer findByTeamAndQuestionId(long questionId, String teamUUID);
}
