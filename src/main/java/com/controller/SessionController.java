package com.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SessionController {
	 private static final String LOGOUT_SESSION_KEY = null;
	private static final String URL_OF_APPLICATION_HOME_PAGE = null;
	@RequestMapping(value = "/loginname",method = RequestMethod.GET,produces = "application/json")
	 @ResponseBody
	 public ResponseEntity<String> UserSesija(){
	        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	        String name = auth.getName();
	        final HttpHeaders httpHeaders= new HttpHeaders();
	        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
	        
	        //pravi string u JSON formatu za vracanje rezultata
	        String vrati = "{\"name\":\"" + name + "\"}";
	        
	        return new ResponseEntity<String>(vrati, httpHeaders, HttpStatus.OK);	        
	 }
	 @RequestMapping(value = "/logaut",method = RequestMethod.POST)
	 @ResponseBody
	 public void logout(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		    session.setAttribute(LOGOUT_SESSION_KEY, true);
		    response.setStatus(303);
		    response.addHeader("Location", URL_OF_APPLICATION_HOME_PAGE);
	}

}