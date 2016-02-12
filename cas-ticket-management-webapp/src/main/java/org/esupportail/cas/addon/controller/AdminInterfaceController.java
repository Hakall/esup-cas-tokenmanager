package org.esupportail.cas.addon.controller;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.Arrays;

import org.esupportail.cas.addon.model.JsonTicket;
import org.esupportail.cas.addon.model.TicketOwner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import org.esupportail.cas.addon.model.mongo.User;
import org.esupportail.cas.addon.service.UserService;
import org.esupportail.cas.addon.model.mongo.Ticket;
import org.esupportail.cas.addon.service.TicketService;


import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

@Controller
@RequestMapping("/admin")
public class AdminInterfaceController {

	final static Logger LOGGER = Logger.getLogger(AdminInterfaceController.class);

	static Boolean routine = false;
    static Timer timer = new Timer();
    private TimerTask task = new TimerTask()
        {
            @Override
            public void run() 
            {
                saveTickets();
            }   
        }; 

	@Autowired
	private UserService userService;

	@Autowired
	private TicketService ticketService;

	@Value("${server.api}")
	private String CAS_REST_API;

	@Value("${cas.ticket.expirationPolicy}")
	private long EXPIRATION_POLICY;

	@Value("${cas.ticket.rememberMeExpirationPolicy}")
	private long REMEMBER_ME_EXPIRATION_POLICY;

	@Value("${ip.geolocation}")
	private boolean ACTIVATE_IP_GEOLOCATION;

	@Value("${admin.user.nbToDisplay.perPage}")
	private int nbToDisplay;

	@Autowired
	private RestTemplate restTemplate;

	@RequestMapping(value="/activate", method = RequestMethod.GET)
    public String runRoutine() {
        if(!this.routine){
        	userService.drop();
        	ticketService.drop();
            this.timer.scheduleAtFixedRate(task, 0, 1000*60*3);
            this.routine =true;
        }
        return "redirect:/admin";
    }  

    // IF You Do Not Use a DB Service :Uncomment
	// @RequestMapping(method = RequestMethod.GET)
	public String printIndex(ModelMap model, @RequestParam(value = "delete", required = false) boolean delete,
			@RequestParam(value = "page", required = false) Integer page) {
		LOGGER.info("Access to admin-ticket-manager");
		model.addAttribute("command", new TicketOwner());
		model.addAttribute("delete", delete);
		model.addAttribute("pageTitle", "admin.title");
		model.addAttribute("expirationPolicyInSeconds", this.EXPIRATION_POLICY);
		model.addAttribute("rememberMeExpirationPolicyInSeconds", this.REMEMBER_ME_EXPIRATION_POLICY);
		model.addAttribute("activateIpGeolocation", this.ACTIVATE_IP_GEOLOCATION);

		TreeMap usersTreeMap = this.restTemplate.getForObject(this.CAS_REST_API + "/users", TreeMap.class);
		usersTreeMap.put("admin", new JsonTicket[0]);

		int pageNumber = (int) Math.floor( usersTreeMap.size() / this.nbToDisplay );
		if(page == null || page == 0) {
			page = 0;
		} else {
			page--;
		}

		int startPoint = page * this.nbToDisplay;
		int endPoint = startPoint + this.nbToDisplay;

		Set usersSet = usersTreeMap.keySet();
		List<String> usersList = new ArrayList<String>();
		usersList.addAll(usersSet);

		// Just some calculation to make sure we won't get ArrayOutOfBoundException
		if(startPoint > usersList.size()) {
			page = pageNumber--;
			startPoint = page * this.nbToDisplay;
		}
		endPoint = endPoint < usersList.size() ? endPoint : usersList.size();
		if(endPoint==usersList.size())endPoint-=1;
		SortedMap<String,JsonTicket[]> target = new TreeMap<String,JsonTicket[]>();
		
		if(endPoint<usersList.size() && startPoint<=endPoint ){
			target = usersTreeMap.subMap(usersList.get(startPoint),usersList.get(endPoint));
		}


		model.addAttribute("pageNumber", pageNumber);
		model.addAttribute("currentPage", page);
		model.addAttribute("users", target);
		return "adminIndex";
	}

	// IF You Use a DB Service
	@RequestMapping(method = RequestMethod.GET)
	public String printNewIndex(ModelMap model, @RequestParam(value = "delete", required = false) boolean delete,
			@RequestParam(value = "page", required = false) Integer page,
			 @RequestParam(value = "owner", required = false) String owner,
			  @RequestParam(value = "userAgent", required = false) String userAgent) {
		LOGGER.info("Access to admin-ticket-manager");
		model.addAttribute("command", new TicketOwner());
		model.addAttribute("delete", delete);
		model.addAttribute("pageTitle", "admin.title");
		model.addAttribute("expirationPolicyInSeconds", this.EXPIRATION_POLICY);
		model.addAttribute("rememberMeExpirationPolicyInSeconds", this.REMEMBER_ME_EXPIRATION_POLICY);
		model.addAttribute("activateIpGeolocation", this.ACTIVATE_IP_GEOLOCATION);
		model.addAttribute("activate", this.routine);

		List<String> criteria = new ArrayList<String>();
		if(owner!=null){
			if(!owner.equals(""))criteria.add("'owner' : {$regex: '.*"+owner+".*', $options: 'i'}");
		}
		if(userAgent!=null){
			if(!userAgent.equals(""))criteria.add("'authenticationAttributes.userAgent' : {$regex: '.*"+userAgent+".*', $options: 'i'}");
		}
		List<User> users; 
		List<Ticket> tickets = null;
		if(criteria.size()>0){
			tickets = getTicketListByCriteria(criteria); 
			users = getUserListByTickets(tickets);
		}
		else users = getUserList();

		if(users.size()>this.nbToDisplay){
			int pageNumber = (int) Math.floor( users.size() / this.nbToDisplay );
			if(page == null || page == 0) {
				page = 0;
			} else {
				page--;
			}

			int startPoint = page * this.nbToDisplay;
			int endPoint = startPoint + this.nbToDisplay;

			if(startPoint > users.size()) {
				page = pageNumber--;
				startPoint = page * this.nbToDisplay;
			}
			endPoint = endPoint < users.size() ? endPoint : users.size();
			if(endPoint==users.size() && endPoint>0)endPoint-=1;

			users = users.subList(startPoint, endPoint);

			model.addAttribute("pageNumber", pageNumber);
			model.addAttribute("currentPage", page);
		}

		List<Ticket> resTickets = new ArrayList<Ticket>();
		if(tickets==null){
			tickets = new ArrayList<Ticket>();
			for(User user : users){
				tickets.addAll(getUserTickets(user.getUid()));
			}
		}else{
			for(Ticket ticket : tickets){
				for(User user : users){
					if(user.getUid().equals(ticket.getOwner())){
						resTickets.add(ticket);
						break;
					}
				}
			}
			tickets = resTickets;
		}

		model.addAttribute("users", users);
		model.addAttribute("tickets", tickets);
		return "dbAdminIndex";
	}

	@RequestMapping(value="/deleteAll", method = RequestMethod.POST)
	public String handleDeleteForm(@ModelAttribute("TicketOwner") TicketOwner ticketOwner, BindingResult result) {

		String userId = ticketOwner.getOwner();

		String targetUrl = this.CAS_REST_API + "/userTickets/{userId}/";
		this.restTemplate.delete(targetUrl, userId);
		deleteUserTickets(userId);
		return "redirect:/admin?delete=true";
	}

	@RequestMapping(value = "/delete/{ticketId}", method = RequestMethod.GET)
	public String printDelete(ModelMap model, @PathVariable String ticketId) {

			String targetUrl = this.CAS_REST_API + "/ticket/{ticketId}/";
			this.restTemplate.delete(targetUrl, ticketId);
			deleteTicket(ticketId);
			return "redirect:/admin?delete=true";

	}

	public List<User> getUserList() { 
        return userService.listUser();  
    } 

    public List<User> getUserListByTickets(List<Ticket> tickets) { 
        List<User> users = new ArrayList<User>();
        for(Ticket ticket : tickets){
        	users.add(userService.getUserByUid(ticket.getOwner()));
        }
        return users;  
    }

    public List<Ticket> getTicketListByCriteria(List<String> criteria) { 
        return ticketService.getTicketsByCriteria(criteria);  
    }  

    public List<Ticket> getTicketList() { 
        return ticketService.listTicket();  
    }  

    @RequestMapping(value="/saveTickets", method = RequestMethod.GET)
    public void saveTickets(){
    	LOGGER.info("Saving tickets...");
        TreeMap<String,List<LinkedHashMap>> usersTreeMap = this.restTemplate.getForObject(this.CAS_REST_API + "/users", TreeMap.class);
        Set set=usersTreeMap.keySet();
        for(Object user : set){
        	List<String> idsTickets = new ArrayList<String>();
            List<LinkedHashMap> tickets = usersTreeMap.get(user);

        	User mongoUser = userService.getUserByUid(user.toString());
        	if(mongoUser!=null){
        		userService.deleteUser(mongoUser);
        	}
        	for(int i=0;i<tickets.size();i++){
            	LinkedHashMap ticket = tickets.get(i);
            	Ticket mongoTicket = ticketService.getTicketById(ticket.get("id").toString());
            	if(mongoTicket!=null){
            		ticketService.deleteTicket(mongoTicket);
            	}
            	mongoTicket = new Ticket();
            	mongoTicket.setId(ticket.get("id").toString());
            	idsTickets.add(ticket.get("id").toString());
            	mongoTicket.setOwner(user.toString());
            	mongoTicket.setCreationTime(Long.parseLong(ticket.get("creationTime").toString()));
            	mongoTicket.setLastTimeUsed(Long.parseLong(ticket.get("lastTimeUsed").toString()));
            	mongoTicket.setAuthenticationAttributes((Map<String,Object>)ticket.get("authenticationAttributes"));
            	ticketService.addTicket(mongoTicket);
            }
            mongoUser = new User();
            mongoUser.setUid(user.toString());
            mongoUser.setTickets(idsTickets);
            userService.addUser(mongoUser);
        }
        LOGGER.info("...Tickets saved");
    }

    public void deleteUserTickets(String uid) {  
       User mongoUser = userService.getUserByUid(uid);
       if(mongoUser!=null){
               mongoUser.setTickets(new ArrayList<String>());
               userService.updateUser(mongoUser);
           }
       List<Ticket> usersTickets = ticketService.getTicketsByOwner(uid);
       for(Ticket ticket : usersTickets){
       	ticketService.deleteTicket(ticket);
       }
    }

    public void deleteTicket(String ticketId) {
    	Ticket mongoTicket = ticketService.getTicketById(ticketId);  
    	User mongoUser = userService.getUserByUid(mongoTicket.getOwner());
      	if(mongoUser!=null){
               List<String> listTickets = mongoUser.getTickets();
               listTickets.remove(ticketId);
               mongoUser.setTickets(listTickets);
               userService.updateUser(mongoUser);
           }
    	ticketService.deleteTicket(mongoTicket);
    }

    // @RequestMapping(value="/getTickets", method = RequestMethod.GET)
    public List<Ticket> getUserTickets(@RequestParam(value = "uid", required = true) String uid) {  
        return ticketService.getTicketsByOwner(uid);
    }
}
