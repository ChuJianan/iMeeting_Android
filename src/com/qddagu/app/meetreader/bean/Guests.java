package com.qddagu.app.meetreader.bean;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import com.qddagu.app.meetreader.AppException;

public class Guests {
	private List<Guest> guestList = new ArrayList<Guest>();
	
	public static Guests parse(String jsonString) throws AppException {
		Guests guests = new Guests();
		try {
			JSONArray jsonGuests = new JSONArray(jsonString);
			for (int i = 0; i < jsonGuests.length(); i++) {
				guests.getGuestList().add(Guest.parse(jsonGuests.getString(i)));
			}
		} catch (JSONException e) {
			throw AppException.json(e);
		} 
		return guests;
	}

	public List<Guest> getGuestList() {
		return guestList;
	}

	public void setGuestList(List<Guest> guestList) {
		this.guestList = guestList;
	}
}
