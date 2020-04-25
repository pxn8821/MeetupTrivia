import React, { useState, useEffect } from 'react';
import PropTypes from 'prop-types';
import { makeStyles } from '@material-ui/core/styles';
import AppBar from '@material-ui/core/AppBar';
import Paper from '@material-ui/core/Paper';
import Tabs from '@material-ui/core/Tabs';
import Tab from '@material-ui/core/Tab';
import Typography from '@material-ui/core/Typography';
import Box from '@material-ui/core/Box';
import OverallResults from './OverallResults.js';
import RoundResult from './RoundResult.js';

function TabPanel(props) {
  const { children, value, index, ...other } = props;

  return (
    <Typography
      component="div"
      role="tabpanel"
      hidden={value !== index}
      id={`scrollable-auto-tabpanel-${index}`}
      aria-labelledby={`scrollable-auto-tab-${index}`}
      {...other}
    >
      {value === index && <Box p={3}>{children}</Box>}
    </Typography>
  );
}

TabPanel.propTypes = {
  children: PropTypes.node,
  index: PropTypes.any.isRequired,
  value: PropTypes.any.isRequired,
};

function a11yProps(index) {
  return {
    id: `scrollable-auto-tab-${index}`,
    'aria-controls': `scrollable-auto-tabpanel-${index}`,
  };
}

const useStyles = makeStyles((theme) => ({
  root: {
    flexGrow: 1,
    width: '100%',
    backgroundColor: theme.palette.background.paper,
  },
}));


export default function GameResult(props)
{
    const classes = useStyles();
    const [value, setValue] = React.useState(0);
  
    const handleChange = (event, newValue) => {
      setValue(newValue);
    };

    var data = props.data;

    return(
        <>
            <Paper variant="outlined">
                <Typography variant='h6'>
                    Game #{data.id} Results
                </Typography>
            </Paper>
            <br/>
            <div className={classes.root}>
            <AppBar position="static" color="default">
                <Tabs
                value={value}
                onChange={handleChange}
                indicatorColor="primary"
                textColor="primary"
                variant="scrollable"
                scrollButtons="auto"
                >
                <Tab label="Overall Results" {...a11yProps(0)} />

                {data.rounds.map(( round, i ) => (
                  <Tab key={i} label={'Round ' + round.roundNumber} {...a11yProps(round.roundNumber)} />
                  ))}
                </Tabs>
            </AppBar>
            <TabPanel value={value} index={0}>
                <OverallResults data={data}/>
            </TabPanel>
                
            {data.rounds.map(( round, i ) => (
                <TabPanel key={i} value={value} index={round.roundNumber}>
                  <RoundResult data={round}/>
                </TabPanel>        
            ))}  

            </div>
        </>
    )
}