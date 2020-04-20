package com.philng.MeetupTrivia.repositories.custom;


import com.philng.MeetupTrivia.entities.Game;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class GameCustomRepositoryImpl implements GameCustomRepository
{
    @PersistenceContext
    EntityManager entityManager;

    @Override
    public Game getActiveGame()
    {
        try
        {
            return entityManager.createQuery("SELECT g FROM Game g WHERE g.status = 'ACTIVE' ORDER BY g.id DESC", Game.class).setMaxResults(1).getSingleResult();
        }
        catch( NoResultException e )
        {
            return null;
        }
    }
}
