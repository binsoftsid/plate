package com.controller;

import com.model.Sindikati;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.sql.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/sindikati")
public class SindikatiController {
	@Autowired
    private JdbcTemplate jdbcTemplate;	
	
//-------------------------------------------------------------------------
		//vraca sve slogove
		@RequestMapping(value = "/prikaz", method = RequestMethod.GET)
		@ResponseBody
		public String getAllSind(@RequestParam HashMap<String, String> body ) {
			String sql ="",juzer="",pre="1";
			
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	        juzer = auth.getName();			
			
			sql = "select * from sindikati WHERE juzer='" + juzer + "' AND pre=" + pre;
			String vrati="";
			int ii=1;
			
	        vrati = vrati + "{rows:[";
	 		List<Sindikati> lista = jdbcTemplate.query(sql,new SindRowMapper());	
	 		
			for (Iterator<Sindikati> iter = lista.iterator(); iter.hasNext(); ) {
	  		   if (ii>1){
				   vrati = vrati + ",";
			   }
	       		vrati = vrati + "{ id:" + String.valueOf(ii) + ",data:[";
	       		Sindikati mat = iter.next();
	       		vrati = vrati + "\"" + String.valueOf(mat.getSifsind()) + "\",";
	        	vrati = vrati + "\"" + String.valueOf(mat.getNazsind()) + "\",";
	        	vrati = vrati + "\"" + String.valueOf(mat.getZirsind()) + "\",";
	           	vrati = vrati + "\"" + String.valueOf(mat.getPozsind()) + "\",";
	           	vrati = vrati + "\"" + String.valueOf(mat.getProcsind()) + "\"";
				vrati = vrati + "]}";
				ii = ii + 1;
			}
			vrati = vrati + "]};";
	 		return vrati;
		}
//---------------------------vraca izabrano preduzece-------------------------------------------
		@RequestMapping(value = "/prikazjedan", method = RequestMethod.GET, produces="application/json")
		@ResponseBody
		public String getSingleSind(@RequestParam HashMap<String, String> body ) {
			String sifsind="",sql="",juzer="",pre="1";
			sifsind = body.get("sifsind").trim(); 
			
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	        juzer = auth.getName();
	        
			sql = "select * from sindikati where juzer='" + juzer + "' AND pre=" + pre;
			if (sifsind!=null && sifsind.trim().length()>0){
				sql = sql + " AND sifsind=" + sifsind;
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
		public String unosSind(@RequestParam HashMap<String, String> body ) {
			int ii = 0;
			String sql="",juzer="",pre="1";
			
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	        juzer = auth.getName();			
			
	        sql = "INSERT INTO sindikati(pre,sifsind,nazsind,zirsind,pozsind,procsind,juzer)" + 
	        		" VALUES(?,?,?,?,?,?,?)";
				try
				{
					//ubacivanje podataka u tabelu sindikati
					ii = jdbcTemplate.update(sql,pre,body.get("sifsind").trim(),body.get("nazsind").trim(),
							body.get("zirsind").trim(),body.get("pozsind").trim(),body.get("procsind").trim(),juzer);
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
		public String izmenaSind(@RequestParam HashMap<String, String> body ) {
					int ii = 0;
					String sql="",juzer="",pre="1";
					String sifsind = ""; 
					sifsind = body.get("sifsind").trim();
					
					Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			        juzer = auth.getName();			
					
			        sql = "UPDATE sindikati SET nazsind=?,zirsind=?,pozsind=?,procsind=?" + 
			        		" WHERE sifsind=? AND  juzer=? AND pre=?";
						try
						{
							//izmena podataka u tabelu sindikati
							ii = jdbcTemplate.update(sql,body.get("nazsind").trim(),body.get("zirsind").trim(),
									body.get("pozsind").trim(),body.get("procsind").trim(),sifsind,juzer,pre);
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
		public String deleteSind(@RequestParam HashMap<String, String> body ) {
			int ii = 0;
			String sql="",juzer="",pre="1";
			String sifsind = body.get("sifsind").trim(); 
			
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	        juzer = auth.getName();		
	        
	        sql = "DELETE FROM sindikati WHERE sifsind=? AND juzer=? AND pre=?";
			ii = jdbcTemplate.update(sql,sifsind,juzer,pre);		
			
			return String.valueOf(ii);
		}
//==============================================================================================	
		class SindRowMapper implements RowMapper<Sindikati>{
			@Override
			public Sindikati mapRow(ResultSet rs, int rowNum) throws SQLException {
				   Sindikati mat = new Sindikati();
				   mat.setSifsind(rs.getInt("sifsind"));
				   mat.setNazsind(rs.getString("nazsind"));
				   mat.setZirsind(rs.getString("zirsind"));
				   mat.setPozsind(rs.getString("pozsind"));
				   mat.setProcsind(rs.getDouble("procsind"));
				   return mat;
			}
		}
//--------------------------------------------------------------------------		
}
