package com.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.annotation.*;
import java.util.*;

import javax.sql.DataSource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;


@RestController
@RequestMapping("/lozinke")
public class LozinkeController {
	@Autowired
    private JdbcTemplate jdbcTemplate;	
 
	Connection conn;
	DataSource datasource;
	
	String enabled="",role="",firma="",godina="",ime="",prezime="",nazivfirme="";

	@RequestMapping(value = "/izmeni", method = RequestMethod.POST, produces="application/json")
	@Transactional
	@ResponseBody
	public String izmenaLoLozinke(@RequestParam HashMap<String, String> body ) {
		int ii=0;		
		String sql="",cryptpass;
		String nova="",juzer=""; 
		nova = body.get("nova").trim();

				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		        juzer = auth.getName();			

				try{
					DataSource datasource = jdbcTemplate.getDataSource();
					conn = datasource.getConnection();	
				}catch(SQLException ee){
					ee.printStackTrace();
				}		

		        procitajOstalePodatke(conn,juzer);
		        
				cryptpass = passwordEncoder().encode(nova).toString();
		        
				try
				{
					sql = "UPDATE users SET password=? WHERE username=?";
					ii = jdbcTemplate.update(sql,cryptpass,juzer);
				}
				catch (DataAccessException e)
				{
					ii = 0;
				}
				
			return String.valueOf(ii);
	}
//---------------------------------------------------------------------------------------------	
    public void procitajOstalePodatke(Connection _connection,String _juzer) {
	  Statement statement = null;
      try {
         statement = _connection.createStatement();
               String query = "SELECT * FROM users WHERE username=" + _juzer;

		         ResultSet rs = statement.executeQuery( query );
		         if(rs.next()){
		        	 
		        	 enabled=rs.getString("enabled");
		        	 role=rs.getString("role");
		        	 firma=rs.getString("firma");
		        	 godina=rs.getString("godina");
		        	 ime=rs.getString("ime");
		        	 prezime=rs.getString("prezime");
		        	 nazivfirme=rs.getString("nazivfirme");
		        	 
					 rs.close();
				 }
	    }catch ( SQLException sqlex ) {
	    	System.out.println("Greska u trazenju preduzeca:"+sqlex);
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
  }
 //---------------------------------------------------------------------------------------
  	    @Bean
  		public PasswordEncoder passwordEncoder() {
  		    return new BCryptPasswordEncoder();
  		}
}