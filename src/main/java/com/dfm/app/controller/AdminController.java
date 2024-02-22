package com.dfm.app.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.dfm.app.model.User;
import com.dfm.app.service.AdminService;
import com.dfm.app.service.UserService;




@Controller
public class AdminController {
	
	@Autowired
	private AdminService adminService;
	
	@Autowired
	private UserService userService;
	
	@GetMapping("/admin")
	public String getAdminWelcomePage(@ModelAttribute("user") User user, Model model, HttpSession session)
	{
		@SuppressWarnings("unchecked")
        List<String> messages = (List<String>) session.getAttribute("MY_SESSION_MESSAGES");

		if(messages == null) {
			model.addAttribute("errormsg", "Session Expired. Please Login Again");
			return "home/error";
		}
        model.addAttribute("sessionMessages", messages);
        User userdata = userService.findUser(messages.get(0));
		return "admin/welcomeadmin";
	}
	
	@GetMapping("/employee")
	public String employee(Model model) {
		
		List<User> users = adminService.findAllUsers();
		model.addAttribute("staffs", users);
		User user = new User();
		model.addAttribute("user", user);
		return "admin/employee";
		
	}

	@GetMapping("/editStaff/{id}")
	public String editEmployee(Model model, HttpSession session, @PathVariable(name="id") Long id) {
		
		
		@SuppressWarnings("unchecked")
        List<String> messages = (List<String>) session.getAttribute("MY_SESSION_MESSAGES");
		if(messages == null) {
			model.addAttribute("errormsg", "Session Expired. Please Login Again");
			return "home/error";
		}
		User user = userService.getUserById(id);
		model.addAttribute("user", user);
		
        model.addAttribute("sessionMessages", messages);
		
		return "admin/updateemployee";
	}
	
	@PostMapping("/updateEmployee")
	public String updateEmployee(@ModelAttribute("user") User user, Model model, HttpSession session)
	{
		System.out.println("employee updated");
		
		@SuppressWarnings("unchecked")
        List<String> messages = (List<String>) session.getAttribute("MY_SESSION_MESSAGES");

		if(messages == null) {
			model.addAttribute("errormsg", "Session Expired. Please Login Again");
			return "home/error";
		}
        model.addAttribute("sessionMessages", messages);
		
		
		adminService.updateEmployee(user);
		
			return "redirect:/employee";
		
	}
	
	@PostMapping("/deleteStaff/{id}")
	public String deleteEmployee(@PathVariable(name="id") Long id, Model model, HttpSession session)
	{
		adminService.deleteEmployee(id);
		@SuppressWarnings("unchecked")
        List<String> messages = (List<String>) session.getAttribute("MY_SESSION_MESSAGES");

		if(messages == null) {
			model.addAttribute("errormsg", "Session Expired. Please Login Again");
			return "home/error";
		}
        model.addAttribute("sessionMessages", messages);
		
			return "redirect:/employee";
		
	}
	
	@PostMapping("/saveStaff")
	public String saveStaff(@ModelAttribute("user") User user,Model model) {
		System.out.println("save===user");

		User existingUser = userService.findUser(user.getEmail());

		if (existingUser != null) {
			model.addAttribute("errormsg", "Email already exists");
			return "home/error";
		}

		User existingUsername = userService.findUserByUsername(user.getUsername());

		if (existingUsername != null) {
			model.addAttribute("errormsg", "Username already exists");
			return "home/error";
		}

		int output = userService.saveUser(user);
		
		if (output > 0) {
			return "redirect:/login";
		} else {
			model.addAttribute("errormsg", "Account creation failed");
			return "home/error";
		}
	}
	
}
