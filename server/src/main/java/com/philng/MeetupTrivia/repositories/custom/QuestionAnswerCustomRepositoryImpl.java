package com.philng.MeetupTrivia.repositories.custom;

import com.philng.MeetupTrivia.entities.QuestionAnswer;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class QuestionAnswerCustomRepositoryImpl implements QuestionAnswerCustomRepository
{

    @PersistenceContext
    EntityManager entityManager;


    @Override
    public QuestionAnswer findByTeamAndQuestionId(long questionId, String teamUUID)
    {
        String query = "SELECT a FROM QuestionAnswer a WHERE a.questionId = :questionId AND a.teamUUID = :teamUUID";

        List<QuestionAnswer> results = entityManager.createQuery( query, QuestionAnswer.class)
                .setParameter("questionId", questionId )
                .setParameter("teamUUID", teamUUID )
                .getResultList();

        if( results.size() > 0)
        {
            return results.get(0);
        }
        else
        {
            return null;
        }
    }
}
