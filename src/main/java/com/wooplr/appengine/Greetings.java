package com.wooplr.appengine;

import static com.wooplr.appengine.service.OfyService.ofy;

import java.util.ArrayList;

import javax.inject.Named;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;
import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.api.users.User;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.wooplr.appengine.domain.Event;

/**
 * Defines v1 of a helloworld API, which provides simple "greeting" methods.
 */
@Api(name = "helloworld", version = "v1", scopes = { Constants.EMAIL_SCOPE }, clientIds = { Constants.WEB_CLIENT_ID,
		Constants.ANDROID_CLIENT_ID, Constants.IOS_CLIENT_ID }, audiences = { Constants.ANDROID_AUDIENCE })
public class Greetings {

	public static ArrayList<HelloGreeting> greetings = new ArrayList<HelloGreeting>();

	static {
		greetings.add(new HelloGreeting("Hello World!"));
		greetings.add(new HelloGreeting("Goodbye World!"));
		greetings.add(new HelloGreeting("Good Morning"));
	}

	@ApiMethod(name = "getGreeting", httpMethod = "get")
	public HelloGreeting getGreeting(@Named("id") Integer id) throws NotFoundException {
		try {
			return greetings.get(id);
		} catch (IndexOutOfBoundsException e) {
			throw new NotFoundException("Greeting not found with an index: " + id);
		}
	}

	public ArrayList<HelloGreeting> listGreeting() {
		return greetings;
	}

	@ApiMethod(name = "greetings.multiply", httpMethod = "post")
	public HelloGreeting insertGreeting(@Named("times") Integer times, HelloGreeting greeting) {
		HelloGreeting response = new HelloGreeting();
		StringBuilder responseBuilder = new StringBuilder();
		for (int i = 0; i < times; i++) {
			responseBuilder.append(greeting.getMessage());
		}
		response.setMessage(responseBuilder.toString());
		return response;
	}

	@ApiMethod(name = "greetings.authed", path = "hellogreeting/authed")
	public HelloGreeting authedGreeting(User user) {
		HelloGreeting response = new HelloGreeting("hello " + user.getEmail());
		return response;
	}

	/**
	 * Use this static method for getting the Objectify service factory.
	 * 
	 * @return ObjectifyFactory.
	 */
	public static ObjectifyFactory factory() {
		return ObjectifyService.factory();
	}
	
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
}
