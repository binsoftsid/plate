package com.controller;

import com.model.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.sql.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;


@RestController
@RequestMapping("/admindata")
public class AdminController {
	@Autowired
    private JdbcTemplate jdbcTemplate;	
//-------------------------------------------------------------------------
		//vraca sve slogove
		@RequestMapping(value = "/prikaz", method = RequestMethod.GET)
		@ResponseBody
		public String getAllUsers(@RequestParam HashMap<String, String> body ) {
			String sql ="";
			
			sql = "select * from users";
			String vrati="";
			int ii=1;
			
	        vrati = vrati + "{rows:[";
	 		List<Users> lista = jdbcTemplate.query(sql,new UsersRowMapper());	
	 		
			for (Iterator<Users> iter = lista.iterator(); iter.hasNext(); ) {
	  		   if (ii>1){
				   vrati = vrati + ",";
			   }
	       		vrati = vrati + "{ id:" + String.valueOf(ii) + ",data:[";
	       		Users mat = iter.next();
	       		vrati = vrati + "\"" + String.valueOf(mat.getUsername()) + "\",";
	        	vrati = vrati + "\"" + String.valueOf(mat.getPassword()) + "\",";
	        	vrati = vrati + "\"" + String.valueOf(mat.getRole()) + "\",";
	           	vrati = vrati + "\"" + String.valueOf(mat.getIme()) + "\",";
	           	vrati = vrati + "\"" + String.valueOf(mat.getPrezime()) + "\"";
				vrati = vrati + "]}";
				ii = ii + 1;
			}
			vrati = vrati + "]};";
	 		return vrati;
		}
//-------------------------------------------------------------------------------------------------------
		@RequestMapping(value = "/prikazjedan", method = RequestMethod.GET, produces="application/json")
		@ResponseBody
		public String getSingleUsers(@RequestParam HashMap<String, String> body ) {
			String juzername="",sql="";
			juzername = body.get("juzername"); 
			
	        if (juzername!=null && juzername.trim().length()>0){
	        	sql = "select * from users where username=?";
			}
	        
	        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql,juzername);
	        
	        String json="";
	        final ObjectMapper mapper = new ObjectMapper();
	        try {
	        	json = mapper.writeValueAsString(list);
	        }catch(JsonProcessingException ee) {
	        	ee.printStackTrace();
	        }
	        return json;
		}
//-------------------------------------------------------------------------------------------
		@RequestMapping(value = "/unos", method = RequestMethod.POST, produces="application/json")
		@Transactional
		@ResponseBody
		public String unosUsers(@RequestParam HashMap<String, String> body ) {
			int ii = 0;
			String sql="",strrola="",cryptpass="";
			String juzername = body.get("juzername"); 
			String juzerpasword = body.get("juzerpasword"); 
			String ime = body.get("ime"); 
			String prezime = body.get("prezime"); 
			String rola = body.get("rola").trim(); 
			
			if (rola.equals("2")) {
				strrola = "ROLE_ADMIN";
			}else {
				strrola = "ROLE_USER";
			}
			
			cryptpass = passwordEncoder().encode(juzerpasword).toString();
			
			sql = "INSERT INTO users(username,password,role,ime,prezime)" + 
	        		" VALUES(?,?,?,?,?)";
				try
				{
					//ubacivanje podataka u tabelu Users
					ii = jdbcTemplate.update(sql,juzername,cryptpass,strrola,ime,prezime);
					
					//ubacuje podatke i u tabelu role
					sql = "INSERT INTO roles(username,role) VALUES(?,?)";
					ii = jdbcTemplate.update(sql,juzername,strrola);

				}
				catch (DataAccessException e)
				{
					throw new RuntimeException(e);
				}
			
			return String.valueOf(ii);
		}
//-------------------------------------------------------------------------------------------
		@RequestMapping(value = "/brisanje", method = RequestMethod.POST)
		@Transactional
		@ResponseBody
		public String deleteUsers(@RequestParam HashMap<String, String> body ) {
			int ii = 0;
			String sql="";
			String juzername = body.get("juzername"); 
			
	        sql = "DELETE FROM users WHERE username=?";
			ii = jdbcTemplate.update(sql,juzername);		
	        sql = "DELETE FROM roles WHERE username=?";
			ii = jdbcTemplate.update(sql,juzername);		
			
			return String.valueOf(ii);
		}
//==============================================================================================	
		class UsersRowMapper implements RowMapper<Users>{
			@Override
			public Users mapRow(ResultSet rs, int rowNum) throws SQLException {
				   Users mat = new Users();
				   mat.setUsername(rs.getString("username"));
				   mat.setPassword(rs.getString("password"));
				   mat.setRole(rs.getString("role"));
				   mat.setIme(rs.getString("ime"));
				   mat.setPrezime(rs.getString("prezime"));
				   return mat;
			}
		}
//---------------------------------------------------------------------------------------
		@Bean
		public PasswordEncoder passwordEncoder() {
		    return new BCryptPasswordEncoder();
		}
}
