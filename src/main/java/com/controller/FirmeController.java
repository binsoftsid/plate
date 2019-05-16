package com.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequestMapping("/firma")
public class FirmeController {

	@Autowired
    private JdbcTemplate jdbcTemplate;	

//---------------------------vraca izabranu firmu-------------------------------------------
		@RequestMapping(value = "/ucitajfirmu", method = RequestMethod.GET, produces="application/json")
		@ResponseBody
		public String getSingleFirma(@RequestParam HashMap<String, String> body ) {
			String sql="",juzer="";

			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	        juzer = auth.getName();
	        
			sql = "select firma,godina,nazivfirme from users where username=?";
	        
	        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql,juzer);
	        String json = "";	        
	        final ObjectMapper mapper = new ObjectMapper();
	        try {
	        	json = mapper.writeValueAsString(list);
	        }catch(JsonProcessingException ee) {
	        	ee.printStackTrace();
	        }
	        return json;
		}
//-------------------------------------------------------------------------------------------
		@RequestMapping(value = "/upisifirmu", method = RequestMethod.POST)
		@Transactional
		@ResponseBody
		public String izmenaFirma(@RequestParam HashMap<String, String> body ) {
					int ii = 0;
					String sql="",juzer="",pre="0",godina="",nazivfirme="";
					pre = body.get("pre"); 
					godina = body.get("godina"); 
					nazivfirme = body.get("nazivfirme"); 
					
					Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			        juzer = auth.getName();			
					
			        sql = "UPDATE users SET firma=?,godina=?,nazivfirme=? WHERE username=?";
						try{
							//ubacivanje firme u tabelu users
							ii = jdbcTemplate.update(sql,pre,godina,nazivfirme,juzer);
							//ii = jdbcTemplate.update(sql);
						}
						catch (DataAccessException e)
						{
							throw new RuntimeException(e);
						}
					
					return String.valueOf(ii);
		}
//--------------------------------------------------------------------------		
}
