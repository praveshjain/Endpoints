package com.wooplr.appengine.api;

import static com.wooplr.appengine.service.OfyService.ofy;

import java.util.ArrayList;
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
import com.wooplr.appengine.domain.Event;

/*
 * A class to keep track on number of counts of an Event.
 */
class Count {

	String buttonName;
	int count;

	public Count(String buttonName, int count) {
		this.buttonName = buttonName;
		this.count = count;
	}

	public String getButtonName() {
		return buttonName;
	}

	public void setButtonName(String buttonName) {
		this.buttonName = buttonName;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
}

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
	 */
	@ApiMethod(name = "clickButton", path = "Event", httpMethod = HttpMethod.POST)
	public Event clickButton(@Named("button") String button, @Named("data") String data) {

		// Allocate a key for the conference -- let App Engine allocate the ID
		// Don't forget to include the parent Profile in the allocated ID
		final Key<Event> eventKey = factory().allocateId(Event.class);

		// TODO (Lesson 4)
		// Get the Conference Id from the Key
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
		return query.list();
	}

	/**
	 * Use this method to get Events after a particular date. It takes an input
	 * date in the format 2015-03-27T15:32:40.016+05:30
	 */
	@ApiMethod(name = "getEventsInDuration", path = "getEventsInDuration", httpMethod = HttpMethod.POST)
	public List<Event> getEventsInDuration(@Named("fromDate") Date fromDate) {

		Query<Event> query = ofy().load().type(Event.class).order("date");
		query = query.filter("date >", fromDate);
		return query.list();
	}

	/**
	 * A method to count the number of separate Events, based on the buttonName.
	 * 
	 * @return List<Count>
	 */
	@ApiMethod(name = "countEvents", path = "countEvents", httpMethod = HttpMethod.POST)
	public List<Count> countEvents() {

		ofy().load().count();
		Query<Event> query = ofy().load().type(Event.class);
		int number1 = query.filter("buttonName =", "b1").count();
		Count count1 = new Count("b1", number1);
		int number2 = query.filter("buttonName =", "b2").count();
		Count count2 = new Count("b2", number2);
		int number3 = query.filter("buttonName =", "b3").count();
		Count count3 = new Count("b3", number3);
		int number4 = query.filter("buttonName =", "b4").count();
		Count count4 = new Count("b4", number4);
		List<Count> list = new ArrayList<Count>();
		list.add(count1);
		list.add(count2);
		list.add(count3);
		list.add(count4);
		return list;
	}
}
