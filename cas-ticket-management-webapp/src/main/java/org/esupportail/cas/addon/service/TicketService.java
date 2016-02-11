package org.esupportail.cas.addon.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import org.esupportail.cas.addon.model.mongo.Ticket;
import org.apache.log4j.Logger;

@Repository
public class TicketService {

	final static Logger LOGGER = Logger.getLogger(TicketService.class);
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	public static final String COLLECTION_NAME = "ticket";
	
	public void addTicket(Ticket ticket) {
		// if (!mongoTemplate.collectionExists(Ticket.class)) {
		// 	mongoTemplate.createCollection(Ticket.class);
		// }		
		mongoTemplate.insert(ticket, COLLECTION_NAME);
	}

	public Ticket getTicketById(String id){
		BasicQuery query = new BasicQuery("{id : '"+id+"' }");
		List<Ticket> tickets = mongoTemplate.find(query, Ticket.class);
		if(tickets.size()>0)return tickets.get(0);
		return null;
	}

	public List<Ticket> getTicketsByOwner(String owner){
		BasicQuery query = new BasicQuery("{owner : '"+owner+"' }");
		List<Ticket> tickets = mongoTemplate.find(query, Ticket.class);
		return tickets;
	}

	public List<Ticket> getTicketsByCriteria(List<String> criteria){
		String queryStr = "{";
		for(String critere : criteria){
			queryStr+=critere;
			queryStr+=",";
		}
		queryStr = queryStr.substring(0,queryStr.length()-1);
		queryStr += "}";
		BasicQuery query = new BasicQuery(queryStr);
		List<Ticket> tickets = mongoTemplate.find(query, Ticket.class);
		return tickets;
	}
	
	public List<Ticket> listTicket() {
		return mongoTemplate.findAll(Ticket.class, COLLECTION_NAME);
	}
	
	public void deleteTicket(Ticket ticket) {
		mongoTemplate.remove(ticket, COLLECTION_NAME);
	}

	public void drop() {
		mongoTemplate.remove(new Query(), COLLECTION_NAME);
	}
	
	public void updateTicket(Ticket ticket) {
		LOGGER.info("Ticket:"+ticket.toString());
		deleteTicket(ticket);
		addTicket(ticket);
	}
}