package com.wooplr.appengine.domain;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/*
 * A simple Event class that is stored whenever a button is triggered.
 */

@Entity
public class Event {

	/**
     * The id for the datastore key.
     *
     * We use automatic id assignment for entities of Conference class.
     */
    @Id private long id;
	String buttonName;
	String data;
	
	public Event(long id, String buttonName, String data){
		
		this.id = id;
		this.buttonName = buttonName;
		this.data = data;
	}
	
	public long getId(){
		return id;
	}

	public String getButtonName() {
		return buttonName;
	}

	public String getData() {
		return data;
	}
}
