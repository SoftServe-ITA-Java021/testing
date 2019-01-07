import React, {Component} from 'react';
import Calendar from 'react-calendar';
import moment from 'moment';

class Property extends Component {
	constructor(props) {
		super(props);
		this.state = {
			title : "",
			description: "",
			address: "",
			price : "",
			checkIn: "",
			checkOut: "",
			imageLinkObjects: [],
			propertyBookedDates: [],
			successfullyBooked: false
		};
		this.loadPropertyData = this.loadPropertyData.bind(this);
		this.loadImages = this.loadImages.bind(this);
		this.onCalendarDateChange = this.onCalendarDateChange.bind(this);
		this.loadPropertyAvailability = this.loadPropertyAvailability.bind(this);
		this.onCalendarDateChange = this.onCalendarDateChange.bind(this);
	}


	loadPropertyData = () => {
		fetch(`http://localhost:8080/api/properties/${this.props.match.params.id}`)
			.then(response => response.json())
			.then(response => this.setState(response))
	};

	loadImages = () => {
		fetch(`http://localhost:8080/api/property-image/property/${this.props.match.params.id}`)
			.then(response => response.json())
			.then(responseJSON => this.setState({imageLinkObjects: responseJSON}))
	};

	loadPropertyAvailability = () => {
		console.log(this.props.match.params.id);
		fetch(`http://localhost:8080/api/property-availability/${this.props.match.params.id}`)
			.then(response => response.json())
			.then(response => {
				// this.setState({propertyBookedPeriods: this.parsePropertyBookedIntoPeriods(response)})
				this.setState({
					propertyBookedDates: this.getAllBookedDates(response)
				})
			})
	};

	componentWillMount() {
		this.loadPropertyData();
		this.loadImages();
		this.loadPropertyAvailability();
	}

	getAllDatesBetweenStartAndEndDate(start, end) {
		let dateArray = [];
		let currentDate = moment(start);
		let endDate = moment(end);
		while (currentDate <= endDate) {
			dateArray.push(moment(currentDate).format('YYYY-MM-DD') )
			currentDate = moment(currentDate).add(1, 'days');
		}
		return dateArray;
	};

	parsePropertyBookedIntoPeriods(array) {
		let arrayOfPeriods = [];
		let period = [];
		for (let i = 0; i < array.length; i++) {
			period.push([array[i].bookedSince, array[i].bookedUntil]);
		}
		arrayOfPeriods.push(period);
		return arrayOfPeriods;
	};

	getBookedDatesInPeriods(arrayOfPeriods) {
		let arrayOfBookedDates = [];
		for (let i = 0; i < arrayOfPeriods.length; i++) {
			for (let j = 0; j < arrayOfPeriods[i].length; j++) {
				arrayOfBookedDates.push(this.getAllDatesBetweenStartAndEndDate(arrayOfPeriods[i][j][0], arrayOfPeriods[i][j][1]))
			}
		}
		return arrayOfBookedDates;
	}

	getAllBookedDates(response) {
		let arrayOfBookedDatesPeriod = this.getBookedDatesInPeriods(this.parsePropertyBookedIntoPeriods(response));
		let allBookedDates = [];
		for (let i = 0; i < arrayOfBookedDatesPeriod.length; i++) {
			for (let j = 0; j < arrayOfBookedDatesPeriod[i].length; j++) {
				allBookedDates.push(arrayOfBookedDatesPeriod[i][j])
			}
		}
		return allBookedDates;
	}

	onCalendarDateChange = (date) => {
		this.setState({
			checkIn: moment(date[0]).format('YYYY-MM-DD'),
			checkOut: moment(date[1]).format('YYYY-MM-DD'),
		});
	};

	onBookButtonClick = () => {
		let formData = new FormData();
		formData.append('checkIn', this.state.checkIn);
		formData.append('checkOut', this.state.checkOut);
		formData.append('propertyId', this.props.match.params.id);
		fetch('http://localhost:8080/api/property-availability', {
			method: "POST",
			body: formData
		})
			.then(response => response.json())
			.then(response => console.log(response))
	};


	render() {
		return (
			<div className="container">
				<div id="carouselExampleIndicators" className="carousel slide" data-interval="false"
				     data-ride="carousel">
					<ol className="carousel-indicators">
						{
							this.state.imageLinkObjects.map((_, index) =>
								index === 0 ?
									<li data-target="#carouselExampleIndicators" data-slide-to={index} className="active"/>
									:
									<li data-target="#carouselExampleIndicators" data-slide-to={index}/>
							)
						}
					</ol>
					<div className="carousel-inner">
						{
							this.state.imageLinkObjects.map((imageLinkObject, index) =>

								index === 0 ?
									<div className="carousel-item active">
										<img className="d-block w-100" src={imageLinkObject.imageLink}/>
									</div>
									:
									<div className="carousel-item"> :
										<img className="d-block w-100" src={imageLinkObject.imageLink}/>
									</div>

							)
						}
					</div>
					<a className="carousel-control-prev" href="#carouselExampleIndicators" role="button"
					   data-slide="prev">
						<span className="carousel-control-prev-icon" aria-hidden="true"/>
						<span className="sr-only">Previous</span>
					</a>
					<a className="carousel-control-next" href="#carouselExampleIndicators" role="button"
					   data-slide="next">
						<span className="carousel-control-next-icon" aria-hidden="true"/>
						<span className="sr-only">Next</span>
					</a>
				</div>
				<div className="row mt-3 justify-content-around">
					<div className="col-11  text-black">
						<h1>{this.state.title}</h1>
					</div>
					<p className="col-11">{this.state.address}</p>
				</div>

				<div className="row mt-4 justify-content-around">
					<div className="col-6 text-black">
						<p>{this.state.description}</p>
					</div>
					<div className="col-4 text-black text-center">
						<Calendar
							onChange={this.onCalendarDateChange}
							selectRange={true}
							tileDisabled={({date, view}) =>
								(view === 'month') &&
								this.state.propertyBookedDates.some((disabledDateString) => {
										const disabledDate = new Date(disabledDateString);
										return date.getFullYear() === disabledDate.getFullYear() &&
										date.getMonth() === disabledDate.getMonth() &&
										date.getDate() === disabledDate.getDate()
									}
								)}
						/>
						<button onClick={this.onBookButtonClick} type="button" className="btn btn-primary mt-2 mb-2">Book</button>
					</div>
				</div>
			</div>
		);
	}
}

export default Property;