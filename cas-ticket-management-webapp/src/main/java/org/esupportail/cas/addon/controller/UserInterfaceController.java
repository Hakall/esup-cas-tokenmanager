package org.esupportail.cas.addon.controller;

import java.util.SortedMap;
import java.util.TreeMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.Arrays;

import org.esupportail.cas.addon.model.JsonTicket;
import org.esupportail.cas.addon.model.TicketOwner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.ldap.userdetails.LdapUserDetailsImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import org.esupportail.cas.addon.model.mongo.User;
import org.esupportail.cas.addon.service.UserService;
import org.esupportail.cas.addon.model.mongo.Ticket;
import org.esupportail.cas.addon.service.TicketService;


import org.apache.log4j.Logger;

@Controller
@RequestMapping("/user")
public class UserInterfaceController {

	final static Logger LOGGER = Logger.getLogger(UserInterfaceController.class);

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

	@Value("${user.ticket.nbToDisplay.perPage}")
	private int nbToDisplay;

	@Autowired
	private RestTemplate restTemplate;

	@RequestMapping(method = RequestMethod.GET)
	public String printIndex(ModelMap model, @RequestParam(value = "delete", required = false) boolean delete,
			@RequestParam(value = "page", required = false) Integer page) {

		LOGGER.info("Access to ticket-manager");
		Object[] listObj = getTickets().toArray();
		Ticket[] listTicket = new Ticket[listObj.length];
		for(int i=0; i < listTicket.length; i++)listTicket[i]=(Ticket) listObj[i];

		model.addAttribute("delete", delete);
		model.addAttribute("expirationPolicyInSeconds", this.EXPIRATION_POLICY);
		model.addAttribute("rememberMeExpirationPolicyInSeconds", this.REMEMBER_ME_EXPIRATION_POLICY);
		model.addAttribute("activateIpGeolocation", this.ACTIVATE_IP_GEOLOCATION);
		model.addAttribute("userTickets", listTicket);
		model.addAttribute("pageTitle", "user.title");
		return "dbUserIndex";
	}

	@RequestMapping(value="/old", method = RequestMethod.GET)
	public String printOldIndex(ModelMap model, @RequestParam(value = "delete", required = false) boolean delete,
			@RequestParam(value = "page", required = false) Integer page) {

		LOGGER.info("Access to ticket-manager");
		JsonTicket[] listTicket = this.restTemplate.getForObject(this.CAS_REST_API + "/{user}/", JsonTicket[].class, this.getCurrentUser());
		int pageNumber = (int) Math.floor( listTicket.length / this.nbToDisplay );
		if(page == null || page == 0) {
			page = 0;
		} else {
			page--;
		}

		int startPoint = page * this.nbToDisplay;
		int endPoint = startPoint + this.nbToDisplay;

		// Just some calculation to make sure we won't get ArrayOutOfBoundException
		if(startPoint > listTicket.length) {
			page = pageNumber--;
			startPoint = page * this.nbToDisplay;
		}
		endPoint = endPoint < listTicket.length ? endPoint : listTicket.length;

		JsonTicket[] returnedTicket = Arrays.copyOfRange(listTicket, startPoint, endPoint);

		model.addAttribute("delete", delete);
		model.addAttribute("pageNumber", pageNumber);
		model.addAttribute("currentPage", page);
		model.addAttribute("expirationPolicyInSeconds", this.EXPIRATION_POLICY);
		model.addAttribute("rememberMeExpirationPolicyInSeconds", this.REMEMBER_ME_EXPIRATION_POLICY);
		model.addAttribute("activateIpGeolocation", this.ACTIVATE_IP_GEOLOCATION);
		model.addAttribute("userTickets", returnedTicket);
		model.addAttribute("pageTitle", "user.title");
		return "userIndex";
	}

	@RequestMapping(value = "/delete/{ticketId}", method = RequestMethod.GET)
	public String printDelete(ModelMap model, @PathVariable String ticketId) {

		String getOwnerUrl = this.CAS_REST_API + "/owner/{ticketId}/";
		TicketOwner result = this.restTemplate.getForObject(getOwnerUrl, TicketOwner.class, ticketId);

		if(this.getCurrentUser().equals(result.getOwner())) {

			String targetUrl = this.CAS_REST_API + "/ticket/{ticketId}/";
			this.restTemplate.delete(targetUrl, ticketId);
			deleteTicket(ticketId);
			return "redirect:/user?delete=true";
		}
		return "redirect:/user?delete=false";
	}

	private String getCurrentUser() {
		LdapUserDetailsImpl user = (LdapUserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String currentUser = user.getUsername();
		return currentUser;
	}

    public List<Ticket> getTickets() {  
       return ticketService.getTicketsByOwner(getCurrentUser());
    }

    public void deleteTickets() {  
       User mongoUser = userService.getUserByUid(getCurrentUser());
       if(mongoUser!=null){
               mongoUser.setTickets(new ArrayList<String>());
               userService.updateUser(mongoUser);
           }
       List<Ticket> usersTickets = ticketService.getTicketsByOwner(getCurrentUser());
       for(Ticket ticket : usersTickets){
       	ticketService.deleteTicket(ticket);
       }
    }

    public void deleteTicket(String ticketId) {  
    	User mongoUser = userService.getUserByUid(getCurrentUser());
      	if(mongoUser!=null){
               List<String> listTickets = mongoUser.getTickets();
               listTickets.remove(ticketId);
               mongoUser.setTickets(listTickets);
               userService.updateUser(mongoUser);
           }
    	ticketService.deleteTicket(ticketService.getTicketById(ticketId));
    }

}