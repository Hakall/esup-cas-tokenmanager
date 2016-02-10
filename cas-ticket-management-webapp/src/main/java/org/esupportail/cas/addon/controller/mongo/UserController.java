// package org.esupportail.cas.addon.controller.mongo;

// import java.util.SortedMap;
// import java.util.TreeMap;
// import java.util.List;
// import java.util.ArrayList;
// import java.util.Set;
// import java.util.Arrays;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Controller;
// import javax.annotation.PostConstruct;
// import org.springframework.ui.ModelMap;
// import org.springframework.util.StringUtils;
// import org.springframework.web.bind.annotation.ModelAttribute;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestMethod;
// import org.springframework.web.bind.annotation.RequestParam;

// import org.springframework.web.servlet.View;
// import org.springframework.web.servlet.view.RedirectView;
// import org.springframework.web.client.RestTemplate;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.beans.factory.annotation.Value;

// import org.esupportail.cas.addon.model.JsonTicket;
// import org.esupportail.cas.addon.model.TicketOwner;

// import org.esupportail.cas.addon.model.mongo.User;
// import org.esupportail.cas.addon.service.UserService;
// import java.util.Timer;
// import java.util.TimerTask;

// import org.apache.log4j.Logger;
   
// @Controller    
// public class UserController {  

//     final static Logger LOGGER = Logger.getLogger(UserController.class);

//     @Value("${server.api}")
//     private String CAS_REST_API;

//     @Autowired
//     private RestTemplate restTemplate;

//     static private Boolean routine = false;
//     private Timer timer = new Timer();
//     private TimerTask task = new TimerTask()
//         {
//             @Override
//             public void run() 
//             {
//                 saveTickets();
//             }   
//         }; 
   
// 	@Autowired
// 	private UserService userService;
	
//     // @RequestMapping(value = "/mongo/user", method = RequestMethod.GET)  
// 	public List<User> getUserList() { 
//         return userService.listUser();  
//     }  
    
//     // @RequestMapping(value = "/mongo/user/save", method = RequestMethod.POST)  
// 	public void createUser(@ModelAttribute User user, ModelMap model) {
//     	if(StringUtils.hasText(user.getUid())) {
//     		userService.updateUser(user);
//     	} else {
//     		userService.addUser(user);
//     	}
//     }
        
//     // @RequestMapping(value = "/mongo/user/delete", method = RequestMethod.GET)  
// 	public void deleteUser(@ModelAttribute User user, ModelMap model) {  
//         userService.deleteUser(user);  
//     }

//     // @RequestMapping(value = "/mongo/tickets/save", method = RequestMethod.GET)
//     public void saveTickets(){
//         TreeMap<String,List<JsonTicket>> usersTreeMap = this.restTemplate.getForObject(this.CAS_REST_API + "/users", TreeMap.class);
//         Set set=usersTreeMap.keySet();
//         for(Object user : set){
//             User mongoUser = userService.getUserByUid(user.toString());
//             if(mongoUser== null){
//                 mongoUser = new User();
//                 mongoUser.setUid(user.toString());
//                 mongoUser.setTickets(usersTreeMap.get(user));
//                 userService.addUser(mongoUser);
//             }else{
//             mongoUser.setTickets(usersTreeMap.get(user));
//             userService.updateUser(mongoUser);
//             }
//         }
//     }

//     public void deleteUserTickets(String uid) {  
//        User mongoUser = userService.getUserByUid(uid);
//        if(mongoUser!=null){
//                mongoUser.setTickets(new ArrayList<JsonTicket>());
//                userService.updateUser(mongoUser);
//            }
//     }

//     // @RequestMapping(value = "/mongo/user/tickets", method = RequestMethod.GET)
//     public List<JsonTicket> getUserTickets(@RequestParam(value = "uid", required = true) String uid) {  
//        User mongoUser = userService.getUserByUid(uid);
//        if(mongoUser!=null){
//             return mongoUser.getTickets();
//            }
//         return null;
//     }

//     // @PostConstruct
//     // public void routine() {
//     //     if(!this.routine){
//     //         this.timer.scheduleAtFixedRate(task, 0, 1000*60*7);
//     //         this.routine =true;
//     //     }
//     // }    
// }