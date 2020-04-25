package com.philng.MeetupTrivia;

import com.philng.MeetupTrivia.entities.Game;
import com.philng.MeetupTrivia.repositories.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Handles incrementing the active game's round depending on the time
 */

@Service
@Scope("singleton")
public class AutoGameProcessor extends Thread
{

    @Autowired
    GameRepository gameRepository;


    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @PostConstruct
    public void init()
    {
        start();
    }

    @Override
    public void run()
    {
        long count = 0;
        while(true)
        {
            try
            {
                Game game = gameRepository.getActiveGame();
                if( game != null && game.getMinutesPerRound() != null && count % 5 == 0 )
                {
                    int oldCurrentRound = game.getCurrentRound();

                    // Figure out the round
                    Timestamp roundStartedTime = game.getTimeStarted();

                    for( int i=1; i <= game.getNumberOfRounds() + 1 ; i++ )
                    {
                        if( roundStartedTime.before(new Timestamp(System.currentTimeMillis())))
                        {
                            game.setCurrentRound( i );
                        }
                        roundStartedTime = getTimeIncrement( roundStartedTime, game.getMinutesPerRound() );
                    }

                    if( oldCurrentRound != game.getCurrentRound() )
                    {
                        gameRepository.saveAndFlush( game );
                        // Send notifications to connected clients
                        messagingTemplate.convertAndSend("/topic/updates", "newRound");
                        messagingTemplate.convertAndSend("/topic/newRoundNumber", game.getCurrentRound());
                    }
                }

                // Figure out and broadcast time left for current round
                if( game != null && game.getMinutesPerRound() != null && game.getCurrentRound() != 0)
                {
                    int currentRound = game.getCurrentRound();

                    int minutesBeforeNextGame = game.getMinutesPerRound() * (currentRound );

                    Timestamp timeBeforeNextRound = getTimeIncrement( game.getTimeStarted(), minutesBeforeNextGame );

                    long millis = timeBeforeNextRound.getTime() - System.currentTimeMillis();

                    if( millis < 0 )
                        millis = 0;

                    String timeRemainingStr = String.format("%02d:%02d:%02d",
                            TimeUnit.MILLISECONDS.toHours(millis),
                            TimeUnit.MILLISECONDS.toMinutes(millis) -
                                    TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), // The change is in this line
                            TimeUnit.MILLISECONDS.toSeconds(millis) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));

                    if( currentRound > game.getNumberOfRounds() )
                    {
                        timeRemainingStr = "Game ended";
                        if( game.getStatus() != Game.Status.FINISHED )
                        {
                            game.setStatus( Game.Status.FINISHED );
                            gameRepository.save( game );
                        }
                    }

                    messagingTemplate.convertAndSend("/topic/timeLeftForRound", timeRemainingStr);
                }
                else
                {
                    messagingTemplate.convertAndSend("/topic/timeLeftForRound", "-1");

                }
            }
            catch(Exception e )
            {
                e.printStackTrace();
            }


            try
            {
                Thread.sleep( 1000 );
                count++;
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }

    }

    private static Timestamp getTimeIncrement( Timestamp timestamp, int incrementMinutes )
    {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis( timestamp.getTime() );

        cal.add(Calendar.MINUTE, incrementMinutes);

        return new Timestamp( cal.getTime().getTime() );

    }

}
