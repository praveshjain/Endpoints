package com.wooplr.appengine.domain;

import java.util.Date;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;

@Entity
@Index
public class CountsInDuration extends Counts {

	Date date;

	public CountsInDuration() {
		
		super();
	}

	public CountsInDuration(long id, String buttonName, Date date) {

		super(id, buttonName);
		this.date = date;
	}
	
	public Date getDate(){
		return this.date;
	}
}
