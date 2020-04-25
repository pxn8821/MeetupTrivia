import React, { useState, useEffect } from 'react';
import { Chart } from "react-google-charts";

export default function OverallResults(props)
{
    var data = props.data;

    var overallLeaderboard=[['Team', 'Number Correct']];

    data.teamsPresent.sort( function(a, b ){ return b.numberCorrect - a.numberCorrect } );

    for(var team of data.teamsPresent)
    {
        overallLeaderboard.push([team.teamName, team.numberCorrect]);
    }

    return(
        <div>
            <h3>Overall Results</h3>
            <div><b>Number of rounds: </b> {data.numberOfRounds}</div>
            <div><b>Number of questions: </b> {data.numberOfQuestions}</div>
            <div><b>Round type: </b> {data.minutesPerRound === null ? "Manual rounds" : data.minutesPerRound + " minutes per round"}</div>
            <hr/>
            <Chart
            width={'100%'}
            height={'400px'}
            chartType="Bar"
            loader={<div>Loading Chart</div>}
            data={overallLeaderboard}
            options={{
                // Material design options
                chart: {
                title: 'Overall Leaderboard',
                subtitle: 'Number of questions correct'
                },
                legend: {position: 'none' },
                bars: 'horizontal',
            }}
            />

            </div>
    );
}
