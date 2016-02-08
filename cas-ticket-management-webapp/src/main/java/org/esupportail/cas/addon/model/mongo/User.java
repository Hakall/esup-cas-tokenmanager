package org.esupportail.cas.addon.model.mongo;

import java.util.List;
import java.util.ArrayList;
import org.esupportail.cas.addon.model.JsonTicket;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class User {

	@Id
	private String uid;

	private List<JsonTicket> tickets;
	
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}

	public List<JsonTicket> getTickets() {
		return tickets;
	}
	public void setTickets(List<JsonTicket> tickets) {
		this.tickets = tickets;
	}
}