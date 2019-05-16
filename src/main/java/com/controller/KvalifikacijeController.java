package com.controller;

import com.model.Kvalifikacije;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.sql.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/kvalifikacije")
public class KvalifikacijeController {
	@Autowired
    private JdbcTemplate jdbcTemplate;	
	
//-------------------------------------------------------------------------
		//vraca sve slogove
		@RequestMapping(value = "/prikaz", method = RequestMethod.GET)
		@ResponseBody
		public String getAllKvalifikacije(@RequestParam HashMap<String, String> body ) {
			String sql ="";
			
			sql = "select * from kvalifikacije";
			String vrati="";
			int ii=1;
			
	        vrati = vrati + "{rows:[";
	 		List<Kvalifikacije> lista = jdbcTemplate.query(sql,new KvalifikacijeRowMapper());	
	 		
			for (Iterator<Kvalifikacije> iter = lista.iterator(); iter.hasNext(); ) {
	  		   if (ii>1){
				   vrati = vrati + ",";
			   }
	       		vrati = vrati + "{ id:" + String.valueOf(ii) + ",data:[";
	       		Kvalifikacije mat = iter.next();
	       		vrati = vrati + "\"" + String.valueOf(mat.getSifkval()) + "\",";
	        	vrati = vrati + "\"" + String.valueOf(mat.getNazkval()) + "\"";
				vrati = vrati + "]}";
				ii = ii + 1;
			}
			vrati = vrati + "]};";
	 		return vrati;
		}
//---------------------------vraca izabrano preduzece-------------------------------------------
		@RequestMapping(value = "/prikazjedan", method = RequestMethod.GET, produces="application/json")
		@ResponseBody
		public String getSingleKvalifikacije(@RequestParam HashMap<String, String> body ) {
			String sifkval="",sql="";
			sifkval = body.get("sifkval").trim(); 
			
	        if (sifkval!=null && sifkval.trim().length()>0){
	        	sql = "select * from kvalifikacije where sifkval=" + sifkval;
			}
	        
	        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
	        
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
		@ResponseBody
		public String unosKvalifikacije(@RequestParam HashMap<String, String> body ) {
			int ii = 0;
			String sql="";
			
	        sql = "INSERT INTO kvalifikacije(sifkval,nazkval)" + 
	        		" VALUES(?,?)";
				try
				{
					//ubacivanje podataka u tabelu kvalifikacije
					ii = jdbcTemplate.update(sql,body.get("sifkval").trim(),body.get("nazkval").trim());
				}
				catch (DataAccessException e)
				{
					throw new RuntimeException(e);
				}
			
			return String.valueOf(ii);
		}
//-------------------------------------------------------------------------------------------
		@RequestMapping(value = "/izmena", method = RequestMethod.POST, produces="application/json")
		@ResponseBody
		public String izmenaKvalifikacije(@RequestParam HashMap<String, String> body ) {
					int ii = 0;
					String sql="";
					String sifkval = ""; 
					sifkval = body.get("sifkval").trim();
					
			        sql = "UPDATE kvalifikacije SET nazkval=?" + 
			        		" WHERE sifkval=?";
						try
						{
							//izmena podataka u tabelu kvalifikacije
							ii = jdbcTemplate.update(sql,body.get("nazkval").trim(),sifkval);
						}
						catch (DataAccessException e)
						{
							throw new RuntimeException(e);
						}
					
					return String.valueOf(ii);
		}
//---------------------------------------------------------------------------------------------	
		@RequestMapping(value = "/brisanje", method = RequestMethod.POST)
		@ResponseBody
		public String deleteKvalifikacije(@RequestParam HashMap<String, String> body ) {
			int ii = 0;
			String sql="";
			String sifkval = body.get("sifkval").trim(); 
			
	        sql = "DELETE FROM kvalifikacije WHERE sifkval=?";
			ii = jdbcTemplate.update(sql,sifkval);		
			
			return String.valueOf(ii);
		}
//==============================================================================================	
		class KvalifikacijeRowMapper implements RowMapper<Kvalifikacije>{
			@Override
			public Kvalifikacije mapRow(ResultSet rs, int rowNum) throws SQLException {
				   Kvalifikacije mat = new Kvalifikacije();
				   mat.setSifkval(rs.getInt("sifkval"));
				   mat.setNazkval(rs.getString("nazkval"));
				   return mat;
			}
		}
//--------------------------------------------------------------------------		
}

