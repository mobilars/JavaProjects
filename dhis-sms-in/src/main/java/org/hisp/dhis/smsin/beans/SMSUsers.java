package org.hisp.dhis.smsin.beans;

import java.util.Map;

import org.springframework.stereotype.Controller;

public class SMSUsers {

	private Map <String,String> users;
	
    public SMSUsers() {
    }

	public Map getUsers() {
		return users;
	}

	public void setUsers(Map users) {
		this.users = users;
	}



}