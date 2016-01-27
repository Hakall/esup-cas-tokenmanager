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
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import org.apache.log4j.Logger;

@Controller
@RequestMapping("/admin")
public class AdminInterfaceController {

	final static Logger LOGGER = Logger.getLogger(AdminInterfaceController.class);

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

	@RequestMapping(method = RequestMethod.GET)
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

		LOGGER.info("page");
		LOGGER.info(page);

		LOGGER.info("pageNumber");
		LOGGER.info(pageNumber);

		LOGGER.info("endPoint");
		LOGGER.info(endPoint);
		
		LOGGER.info("startPoint");
		LOGGER.info(startPoint);
		
		if(endPoint<usersList.size() && startPoint<=endPoint ){
			LOGGER.info("if(endPoint<usersList.size() && startPoint!=endPoint)");
			target = usersTreeMap.subMap(usersList.get(startPoint),usersList.get(endPoint));
		}


		model.addAttribute("pageNumber", pageNumber);
		model.addAttribute("currentPage", page);
		model.addAttribute("users", target);
		return "adminIndex";
	}

	@RequestMapping(value="/deleteAll", method = RequestMethod.POST)
	public String handleDeleteForm(@ModelAttribute("TicketOwner") TicketOwner ticketOwner, BindingResult result) {

		String userId = ticketOwner.getOwner();

		String targetUrl = this.CAS_REST_API + "/userTickets/{userId}/";
		this.restTemplate.delete(targetUrl, userId);

		return "redirect:/admin?delete=true";
	}

	@RequestMapping(value = "/delete/{ticketId}", method = RequestMethod.GET)
	public String printDelete(ModelMap model, @PathVariable String ticketId) {

			String targetUrl = this.CAS_REST_API + "/ticket/{ticketId}/";
			this.restTemplate.delete(targetUrl, ticketId);
			return "redirect:/admin?delete=true";

	}
}
