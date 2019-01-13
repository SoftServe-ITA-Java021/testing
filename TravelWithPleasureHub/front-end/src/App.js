import React, {Component, Fragment} from 'react';
import {BrowserRouter, Route, Switch,Redirect} from "react-router-dom";
import {BrowserRouter, Route, Switch, Redirect} from "react-router-dom";
import NavBar from "./components/NavBar";
import PropertyList from "./components/property-rent/PropertyList";
import Property from "./components/property-rent/Property";
import PropertyUpload from "./components/property-rent/PropertyUpload";

import AllMeetings from "./components/meetings/AllMeetings";
import MeetingAdd from './components/meetings/MeetingAdd'
import CreatedMeetings from './components/meetings/CreatedMeetings'
import OneMeeting from './components/meetings/OneMeeting'
import MeetingsFind from "./components/meetings/MeetingsFind";
import MeetingsHistory from "./components/meetings/MeetingsHistory";
import ErrorBoundary from "./ErrorBoundary";
import NoMatch from "./components/NoMatch";
import Home from "./components/Home";
import TicketsForm from "./components/tickets/TicketsForm";
import Profile from './components/signup/Profile';
import WishingUsers from "./components/meetings/WishingUsers";
import ConfirmedUsers from "./components/meetings/ConfirmedUsers";

class App extends Component {
	render() {
		return (
			<div>

				<BrowserRouter>
					<Fragment>
						<NavBar/>
						<ErrorBoundary>
							<Switch>
								<Route exact path="/upload/property" component={PropertyUpload} />
								<Route exact path="/properties/:id" component={Property} />
								<Route exact path="/properties/" component={PropertyList} />

								<Route path="/meetings/show-all-meetings/" component={AllMeetings}/>
								<Route path="/meetings/find-meetings/" component={MeetingsFind}/>
								<Route path="/meetings/add-meeting/" component={MeetingAdd}/>
								<Route path="/meetings/show-history/" component={MeetingsHistory}/>
								<Route path="/meetings/show-all-created-meetings" component={CreatedMeetings}/>
								<Route path="/meetings/show-meeting/:id" exact component={OneMeeting}/>
								<Route path="/profile" exact component={Profile}/>
								<Route path="/tickets" component={TicketsForm}/>
                                <Route exact path="/meetings/show-meeting/wishing-users/:id" component={WishingUsers}/>
                                <Route exact path="/meetings/show-meeting/confirmed-users/:id"
                                       component={ConfirmedUsers}/>
								<Route exact path="/" component={Home}/>

								<Route path="*" component={NoMatch}/>
							</Switch>

						</ErrorBoundary>
					</Fragment>

				</BrowserRouter>
				</div>
		);
	}
}

export default App;