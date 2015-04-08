package com.wooplr.appengine.api;

import static com.wooplr.appengine.service.OfyService.ofy;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.inject.Named;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.Query;
import com.wooplr.appengine.Constants;
import com.wooplr.appengine.domain.Counts;
import com.wooplr.appengine.domain.CountsInDuration;
import com.wooplr.appengine.domain.Event;

/**
 * Defines v1 of a helloworld API, which provides simple "greeting" methods.
 */
@Api(name = "helloworld", version = "v1", scopes = { Constants.EMAIL_SCOPE }, clientIds = { Constants.WEB_CLIENT_ID,
        Constants.ANDROID_CLIENT_ID, Constants.IOS_CLIENT_ID }, audiences = { Constants.ANDROID_AUDIENCE })
public class Greetings {

	/**
	 * Use this static method for getting the Objectify service factory.
	 * 
	 * @return ObjectifyFactory.
	 */
	public static ObjectifyFactory factory() {
		return ObjectifyService.factory();
	}

	/**
	 * A method to create an Event.
	 * 
	 * @param button
	 *            is the name of the button pressed
	 * @param data
	 *            is the data to be stored along with that button.
	 * @throws ParseException
	 * 
	 */
	@ApiMethod(name = "clickButton", path = "Event", httpMethod = HttpMethod.POST)
	public Event clickButton(@Named("button") String button, @Named("data") String data) throws ParseException {

		/*
		 * First check whether a counts for this button is already being tracked
		 */
		Query<Counts> query = ofy().load().type(Counts.class);
		query = query.filter("buttonName =", button);
		/*
		 * If no then create a new Counts for this button and store
		 */
		if (query.list().isEmpty()) {
			Key<Counts> countsKey = factory().allocateId(Counts.class);
			Counts counts = new Counts(countsKey.getId(), button);
			ofy().save().entity(counts).now();
		}
		/*
		 * If yes then update it
		 */
		else {
			Counts counts = query.list().get(0);
			counts.increment();
			ofy().save().entity(counts).now();
		}

		/*
		 * Get current time and extract the time at the start of the hour
		 */
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		Date date = new Date();
		String dateString = sdf.format(date);
		String[] dateParts = dateString.split(":");
		String currentHourString = dateParts[0] + ":00:00" + dateParts[2].substring(2);
		Date currentHour = sdf.parse(currentHourString);

		/*
		 * Check if a count for this button in this hour is already present
		 */
		Query<CountsInDuration> query1 = ofy().load().type(CountsInDuration.class);
		query1 = query1.filter("date =", currentHour);
		query1 = query1.filter("buttonName =", button);

		/*
		 * If not, create it
		 */
		if (query1.list().isEmpty()) {
			Key<CountsInDuration> countsKey = factory().allocateId(CountsInDuration.class);
			CountsInDuration countsInDuration = new CountsInDuration(countsKey.getId(), button, currentHour);
			ofy().save().entity(countsInDuration).now();
		}
		/*
		 * If yes, then update it
		 */
		else {
			CountsInDuration countsInDuration = query1.list().get(0);
			countsInDuration.increment();
			ofy().save().entity(countsInDuration).now();
		}

		/*
		 * Finally, save the Event
		 */
		final Key<Event> eventKey = factory().allocateId(Event.class);
		final long eventId = eventKey.getId();

		Event event = new Event(eventId, button, data);
		ofy().save().entity(event).now();
		return event;
	}

	/**
	 * Method to list all events in ascending order of date.
	 */
	@ApiMethod(name = "getRecentEvents", path = "getRecentEvents", httpMethod = HttpMethod.POST)
	public List<Event> getRecentEvents() {
		Query<Event> query = ofy().load().type(Event.class).order("date");
		for (Event e : query.list()) {
			System.out.println(e.getDate());
		}
		return query.list();
	}

	/**
	 * Use this method to get Events after a particular date. It takes an input
	 * date in the format 2015-03-27T15:32:40.016+05:30
	 * 
	 * @throws ParseException
	 */
	@ApiMethod(name = "getEventsInDuration", path = "getEventsInDuration", httpMethod = HttpMethod.POST)
	public List<Event> getEventsInDuration(@Named("fromDate") String fromDate) throws ParseException {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		Date date = sdf.parse(fromDate);
		Query<Event> query = ofy().load().type(Event.class).order("date");
		query = query.filter("date >", date);
		return query.list();
	}

	/**
	 * Use this method to get the number of events that happened in a particular
	 * hour
	 * @throws ParseException 
	 */
	@ApiMethod(name = "getCountsInDuration", path = "getCountsInDuration", httpMethod = HttpMethod.POST)
	public List<CountsInDuration> getCountsInDuration(@Named("dateTime") String dateTime) throws ParseException{
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		Date hour = sdf.parse(dateTime);
		String dateString = sdf.format(hour);
		String[] dateParts = dateString.split(":");
		String startingHourString = dateParts[0] + ":00:00" + dateParts[2].substring(2);
		Date startingHour = sdf.parse(startingHourString);
		Query<CountsInDuration> query = ofy().load().type(CountsInDuration.class).order("buttonName");
		query = query.filter("date =", startingHour);
		return query.list();
	}

	/**
	 * A method to count the number of separate Events, based on the buttonName.
	 * 
	 * @return List<Count>
	 */
	@ApiMethod(name = "countEvents", path = "countEvents", httpMethod = HttpMethod.POST)
	public List<Counts> countEvents() {

		Query<Counts> query = ofy().load().type(Counts.class).order("buttonName");
		return query.list();
	}
}
