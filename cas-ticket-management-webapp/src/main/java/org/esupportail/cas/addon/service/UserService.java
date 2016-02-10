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

import org.esupportail.cas.addon.model.mongo.User;
import org.apache.log4j.Logger;

@Repository
public class UserService {

	final static Logger LOGGER = Logger.getLogger(UserService.class);
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	public static final String COLLECTION_NAME = "user";
	
	public void addUser(User user) {
		// if (!mongoTemplate.collectionExists(User.class)) {
		// 	mongoTemplate.createCollection(User.class);
		// }		
		mongoTemplate.insert(user, COLLECTION_NAME);
	}

	public User getUserByUid(String name){
		BasicQuery query = new BasicQuery("{uid : '"+name+"' }");
		List<User> users = mongoTemplate.find(query, User.class);
		if(users.size()>0)return users.get(0);
		return null;
	}
	
	public List<User> listUser() {
		return mongoTemplate.findAll(User.class, COLLECTION_NAME);
	}
	
	public void deleteUser(User user) {
		mongoTemplate.remove(user, COLLECTION_NAME);
	}

	public void drop() {
		mongoTemplate.remove(new Query(), COLLECTION_NAME);
	}
	
	public void updateUser(User user) {
		deleteUser(user);
		addUser(user);
	}
}