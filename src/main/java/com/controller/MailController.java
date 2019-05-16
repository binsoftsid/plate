package com.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.bind.annotation.*;
import java.util.*;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.sql.DataSource;

import java.io.File;
import java.sql.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;


@RestController
@RequestMapping("/mail")
public class MailController {
	@Autowired
    private JdbcTemplate jdbcTemplate;	
	
    @Autowired
    public JavaMailSender emailSender;

	Connection conn;
	DataSource datasource;
	
	//citanje varijabli iz application.properties
	@Autowired
	private Environment env;
//-------------------------------------------------------------------------
		@RequestMapping(value = "/slanjeporuke", method = RequestMethod.POST, produces="application/json")
		@ResponseBody
		public String postMail(@RequestParam HashMap<String, String> body ) {
			String kome="",odkoga="",tekstporuke="",vrati="0";
			
			kome = body.get("kome"); 
			odkoga = body.get("odkoga"); 
			tekstporuke = body.get("tekstporuke"); 
	        
	    try {
	        SimpleMailMessage message = new SimpleMailMessage(); 
	        message.setTo(kome); 
	        message.setSubject(odkoga); 
	        message.setText(tekstporuke);
	        emailSender.send(message);	
	        vrati="1";
	    }catch(MailException ee) {
	    	vrati="0;";
	    }
       
	    return vrati;
	}
//-------------------------------------------------------------------------
		@RequestMapping(value = "/kontaktporuka", method = RequestMethod.POST, produces="application/json")
		@ResponseBody
		public String postMailKontakt(@RequestParam HashMap<String, String> body ) {
					String ime ="",email="",telefon="",poruka="",vrati="0";
					
					//citanje parametra iz application.properties
					String kome = env.getProperty("spring.mail.username");
					
					ime = body.get("ime"); 
					email = body.get("email").trim(); 
					
					telefon = body.get("telefon"); 
					poruka = body.get("poruka"); 
					poruka = " Ime i prezime: " + ime + " \n email: \n " + email + " \n telefon: " + telefon + " \n Poruka: " + poruka;

			    try {
			        SimpleMailMessage message = new SimpleMailMessage(); 
			        message.setTo(kome); 
			        message.setSubject(ime); 
			        message.setText(poruka);
			        emailSender.send(message);	
			        vrati="1";
			    }catch(MailException ee) {
			    	vrati="0;";
			    }
		       
			    return vrati;
		}
//--------------------------------------------------------------------------	
		//slanje poruke sa attachment-om
		@RequestMapping(value = "/slanjeporukeattachment", method = RequestMethod.POST, produces="application/json")
		@ResponseBody
		public String postMailAttach(@RequestParam HashMap<String, String> body ) {
			String vrati="0",juzer="",nazivfajla="";
			int i =0;
			String strpre=body.get("pre").trim();
			String odsifre=body.get("odsifre").trim();
			String dosifre=body.get("dosifre").trim();
			String pathToAttachment="";
			String mailadresa="";
			
			int intodsifre = Integer.parseInt(odsifre);
			int intdosifre = Integer.parseInt(dosifre);

			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	        juzer = auth.getName();	
			
	        try{
				DataSource datasource = jdbcTemplate.getDataSource();
				conn = datasource.getConnection();	
			}catch(SQLException ee){
				ee.printStackTrace();
			}		
			
	        for (i=intodsifre;i<intdosifre+1;i++) {
	        
	        	mailadresa = nadjiEmail(conn,i,strpre,juzer);
	        	
	        	pathToAttachment = "listici/" + juzer + "-" + strpre + "-" + String.valueOf(i) + ".pdf"; 
	        	nazivfajla = juzer + "-" + strpre + "-" + String.valueOf(i) + ".pdf"; 
	  
	        	if (proveriFajl(pathToAttachment) && mailadresa.trim().length()>0){
	    	   
	        		try {
	        			MimeMessage message = emailSender.createMimeMessage();
	        
	        			MimeMessageHelper helper = new MimeMessageHelper(message, true);

	        			helper.setTo(mailadresa);
	        			helper.setSubject(juzer);
	        			helper.setText("listic za platu");

	        			FileSystemResource file = new FileSystemResource(new File(pathToAttachment));
	        			helper.addAttachment(nazivfajla, file);
	     
	        			emailSender.send(message);
	        			vrati="1";
	        		}catch(MessagingException ee) {
	        			vrati="0;";
	        		}
	        		
	        		//pauza izmedju slanja poruka zbog gmail akaunta
	        		try {
	        			Thread.sleep(12000);
	        		}catch(InterruptedException ee) {}
	        		
	        	}//end if
			}//end for
	    return vrati;
	}
	private boolean proveriFajl(String _fajl) {
		boolean postoji = false;
		File f = new File(_fajl);
		if (f.isFile() && f.canRead()) {
			postoji = true;
		}		
		return postoji;
	}
//.................................................................................................................
    public String nadjiEmail(Connection _connection,int _sifra,String _pre,String _juzer) {
    	String stremail="";
	  Statement statement = null;
      try {
         statement = _connection.createStatement();
               String query = "SELECT * FROM maticnipodaci WHERE pre=" + _pre +
            		   " AND juzer='" + _juzer + "' AND sifra=" + _sifra;

		         ResultSet rs = statement.executeQuery( query );
		         if(rs.next()){
		        	 stremail = rs.getString("email");
					 rs.close();
				 }
	  }     
      catch ( SQLException sqlex ) {
    	  System.out.println("Greska u trazenju email-a:"+sqlex);
      }
		//.....................................................................................
		finally{
			if (statement != null){
				try{
					statement.close();
					statement = null;
				}catch (Exception e){
					System.out.println("Nije uspeo da zatvori statement");
				}}
		}
		//.....................................................................................
      return stremail;
  }
}
