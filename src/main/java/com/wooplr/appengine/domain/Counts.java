package com.wooplr.appengine.domain;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
@Index
public class Counts {

	@Id
	public long id;
	String buttonName;
	int count;
	
	public Counts(){
		
	}
	
	public Counts(long id, String buttonName) {
		this.id = id;
	    this.buttonName = buttonName;
	    this.count = 1;
    }

	public long getId() {
		return this.id;
	}
	
	public String getButtonName(){
		return buttonName;
	}

	public int getCount() {
		return count;
	}
	
	public void increment(){
		this.count++;
	}
}