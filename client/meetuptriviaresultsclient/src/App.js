import React, { useState, useEffect } from 'react';
import { makeStyles } from '@material-ui/core/styles';
import Drawer from '@material-ui/core/Drawer';
import CssBaseline from '@material-ui/core/CssBaseline';
import AppBar from '@material-ui/core/AppBar';
import Toolbar from '@material-ui/core/Toolbar';
import List from '@material-ui/core/List';
import Typography from '@material-ui/core/Typography';
import Divider from '@material-ui/core/Divider';
import ListItem from '@material-ui/core/ListItem';
import ListItemText from '@material-ui/core/ListItemText';
import Cached from '@material-ui/icons/Cached';
import Tooltip from '@material-ui/core/Tooltip';
import GameResult from './game/GameResult.js';
import TimeDisplay from './util/TimeDisplay.js';

const drawerWidth = 240;

const useStyles = makeStyles((theme) => ({
  root: {
    display: 'flex',
  },
  appBar: {
    width: `calc(100% - ${drawerWidth}px)`,
    marginLeft: drawerWidth,
  },
  drawer: {
    width: drawerWidth,
    flexShrink: 0,
  },
  drawerPaper: {
    width: drawerWidth,
  },
  // necessary for content to be below app bar
  toolbar: theme.mixins.toolbar,
  content: {
    flexGrow: 1,
    backgroundColor: theme.palette.background.default,
    padding: theme.spacing(3),
  },
}));

export default function App() {
  const classes = useStyles();
  const [games, setGames] = useState([]);
  const [gameId, setGameId] = useState(null);
  const [loaded, setLoaded] = useState( false );
  const [data, setData] = useState(null);

  useEffect( () => {
    if( !loaded )
    {
      console.log('test');
      fetch("http://localhost:8080/api/results/games")
      .then( res => res.json())
      .then( response => {
        setGames(response);
      } );
      setLoaded(true);
    }
  });


  var loadGame = (gameId) => {

    fetch("http://localhost:8080/api/results/game/" + gameId)
    .then( res => res.json())
    .then( response => {
      setData(response);
    } );
  }

  var renderGamePanel = ( ) => {
	if( data !== null )
	{
		return( <GameResult data={data}/> )
	}
	else
	{
		return( <Typography align='center'>Please select a game from the list on the left to view the results</Typography>)
	}

  };

  return (
    <div className={classes.root}>
      <CssBaseline />
      <AppBar position="fixed" className={classes.appBar}>
        <Toolbar>
          <Typography variant="h6" noWrap>
            Trivia Results
          </Typography>
        </Toolbar>
      </AppBar>
      <Drawer
        className={classes.drawer}
        variant="permanent"
        classes={{ paper: classes.drawerPaper }}
        anchor="left"
      >
        <div className={classes.toolbar} />
        <Divider />
        <List>
          {games.map(( game, i ) => (
            <Tooltip key={i} title={game.status === 'ACTIVE' ? 'This game is currently running, you will not be able to view the results until it is finished' : ''} >
              <ListItem button key={game.id} onClick={ () => { if( game.status !== 'ACTIVE' ) { loadGame( game.id ) } } } >
		  		    <ListItemText secondary={ <TimeDisplay time={game.timeStarted}/> } >Game {game.id}</ListItemText>
              {game.status === "ACTIVE" ? <Cached/> : ""}
              </ListItem>
            </Tooltip>
          ))}
        </List>
      </Drawer>
      <main className={classes.content}>
        <div className={classes.toolbar} />
			{renderGamePanel()}
      </main>
    </div>
  );
}
