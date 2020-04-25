import React from 'react';
import { List, ListItem, ListItemIcon,ListItemText,Typography,Chip,Tooltip,LinearProgress } from '@material-ui/core';
import Star from '@material-ui/icons/Star';
import { Chart } from "react-google-charts";

import "react-sweet-progress/lib/style.css";

export default function RoundResult(props)
{
    var data = props.data;

    var roundLeaderboard = [['Team', 'Number Correct']];

    data.teamResults.sort( function(a, b ){ return b.numberCorrect - a.numberCorrect } );

    for(var team of data.teamResults)
    {
        roundLeaderboard.push([team.teamName, team.numberCorrect]);
    }

    var getColorForAnswer = (answer) => {
        if( answer.correct === true )
            return '#4caf50';
        else if( !answer.correct && answer.choice !== null)
            return '#f44336';
        else
            return '#ff9800'
    }

    var sortName = (a, b) => {
        if(a.team.teamName < b.team.teamName) { return -1; }
        if(a.team.teamName > b.team.teamName) { return 1; }
        return 0;  
    }

    var printAnswer = (question, choice) => {
        var color = choice === question.correctAnswer ? 'green' : 'red';

        var numAnswers = question.teamAnswers.length;
        var numVoted = 0;
        for( var teamAnswer of question.teamAnswers)
        {
            if( teamAnswer.choice === choice )
            {
                numVoted++;
            }
        }

        var percentageCorrect = Math.round(100 * (numVoted/numAnswers));

        return(<Typography type="body2" style={{ color: color }} >({percentageCorrect}%) <span dangerouslySetInnerHTML={{__html: choice}}/></Typography>);
    }

    return(<>
        <div>
            <h3>Round {data.roundNumber} results</h3>
            <Chart
            width={'100%'}
            height={'400px'}
            chartType="Bar"
            loader={<div>Loading Chart</div>}
            data={roundLeaderboard}
            options={{
                // Material design options
                chart: {
                title: 'Round Leaderboard',
                subtitle: 'Number of questions correct'
                },
                legend: {position: 'none' },
                bars: 'horizontal',
            }}
            />

            {data.questions.map((question, i) => (<>
                <div key={i}>
                    <b>{i+1}: <span dangerouslySetInnerHTML={{__html: question.question}}></span></b>

                    <List>
                        {question.choices.map((choice, j) => {
                            if( choice === question.correctAnswer )
                            {
                                return (<>
                                    <ListItem>
                                        <ListItemIcon>
                                            <Star />
                                        </ListItemIcon>
                                        <ListItemText primary={printAnswer(question, choice)} />
                                    </ListItem>
                                </>)
                            }
                            else
                            {
                                return (<>
                                    <ListItem>
                                        <ListItemIcon>
                                        </ListItemIcon>
                                        <ListItemText primary={printAnswer(question, choice)} />
                                    </ListItem>
                                </>)
                            }
                        })}
                    </List>
                    
                    {question.teamAnswers.sort(sortName).map( (answer, k) => {
                        return(<>
                            <Tooltip title={answer.choice === null ? "User did not select an answer" : answer.choice}>
                                <Chip label={answer.team.teamName}  color='primary' style={ {backgroundColor : getColorForAnswer(answer) } } />    
                            </Tooltip>&nbsp;
                        </>)
                    })}
                    <hr/>
                </div>
            </>))}
        </div>
    </>)
}