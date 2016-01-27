package org.esupportail.cas.addon.controller;

import java.util.TreeMap;

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

	@Autowired
	private RestTemplate restTemplate;

	@RequestMapping(method = RequestMethod.GET)
	public String printIndex(ModelMap model, @RequestParam(required = false) boolean delete) {
		LOGGER.info("Access to admin-ticket-manager");
		model.addAttribute("command", new TicketOwner());
		model.addAttribute("delete", delete);
		model.addAttribute("pageTitle", "admin.title");
		model.addAttribute("expirationPolicyInSeconds", this.EXPIRATION_POLICY);
		model.addAttribute("rememberMeExpirationPolicyInSeconds", this.REMEMBER_ME_EXPIRATION_POLICY);
		model.addAttribute("activateIpGeolocation", this.ACTIVATE_IP_GEOLOCATION);

		TreeMap usersTreeMap = this.restTemplate.getForObject(this.CAS_REST_API + "/users", TreeMap.class);
		LOGGER.info(usersTreeMap);

		model.addAttribute("users", usersTreeMap);
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
