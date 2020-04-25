import React from 'react';
import Moment from 'react-moment';

export default function TimeDisplay(props){
    return( <Moment format="MM/DD/YY hh:mm a">{props.time}</Moment> )
}