package com.wooplr.appengine.domain;

import java.util.Date;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

/*
 * A simple Event class that is stored whenever a button is triggered.
 */

@Entity
@Index
public class Event {

	/**
	 * The id for the datastore key.
	 *
	 * We use automatic id assignment for entities of Conference class.
	 */
	@Id
	private long id;
	String buttonName;
	String data;
	Date date;

	/*
	 * An empty constructor is needed in order to successfully execute queries
	 * to get Event
	 */
	public Event() {

	}

	public Event(long id, String buttonName, String data) {

		this.id = id;
		this.buttonName = buttonName;
		this.data = data;
		this.date = new Date();
	}

	public long getId() {
		return id;
	}

	public String getButtonName() {
		return buttonName;
	}

	public String getData() {
		return data;
	}

	public Date getDate() {
		return date;
	}
}
