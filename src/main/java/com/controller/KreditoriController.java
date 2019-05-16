package com.controller;

import com.model.Kreditori;
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
@RequestMapping("/kreditori")
public class KreditoriController {
	@Autowired
    private JdbcTemplate jdbcTemplate;	
	
//-------------------------------------------------------------------------
		//vraca sve slogove
		@RequestMapping(value = "/prikaz", method = RequestMethod.GET)
		@ResponseBody
		public String getAllKreditori(@RequestParam HashMap<String, String> body ) {
			String sql ="",juzer="",naziv="";
			naziv = body.get("naziv"); 
			
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	        juzer = auth.getName();			
			
	        if (naziv.trim().length() == 0 || naziv==null){
	        	sql = "select * from kreditori WHERE juzer='" + juzer + "'";
	        }else{
	        	sql = "select * from kreditori WHERE nazobs LIKE '%" + naziv + "%' AND juzer='" + juzer + "'" ;
	        }
			String vrati="";
			int ii=1;
			
	        vrati = vrati + "{rows:[";
	 		List<Kreditori> lista = jdbcTemplate.query(sql,new KreditoriRowMaper());	
	 		
			for (Iterator<Kreditori> iter = lista.iterator(); iter.hasNext(); ) {
	  		   if (ii>1){
				   vrati = vrati + ",";
			   }
	       		vrati = vrati + "{ id:" + String.valueOf(ii) + ",data:[";
	       		Kreditori mat = iter.next();
	       		vrati = vrati + "\"" + String.valueOf(mat.getSifobs()) + "\",";
	        	vrati = vrati + "\"" + String.valueOf(mat.getNazobs()) + "\",";
	        	vrati = vrati + "\"" + String.valueOf(mat.getZirobs()) + "\",";
	           	vrati = vrati + "\"" + String.valueOf(mat.getPozobs()) + "\",";
	           	vrati = vrati + "\"" + String.valueOf(mat.getNaobs()) + "\"";
				vrati = vrati + "]}";
				ii = ii + 1;
			}
			vrati = vrati + "]};";
	 		return vrati;
		}
//---------------------------vraca izabrano preduzece-------------------------------------------
		@RequestMapping(value = "/prikazjedan", method = RequestMethod.GET, produces="application/json")
		@ResponseBody
		public String getSingleKreditori(@RequestParam HashMap<String, String> body ) {
			String sifobs="",sql="",juzer="";
			sifobs = body.get("sifobs"); 
			
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	        juzer = auth.getName();
	        
			sql = "select * from kreditori where juzer='" + juzer + "'";
			if (sifobs!=null && sifobs.trim().length()>0){
				sql = sql + " AND sifobs=" + sifobs;
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
		public String unosKreditori(@RequestParam HashMap<String, String> body ) {
			int ii = 0;
			String sql="",juzer="";
			
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	        juzer = auth.getName();			
			
	        sql = "INSERT INTO kreditori(sifobs,nazobs,zirobs,pozobs,naobs,juzer)" + 
	        		" VALUES(?,?,?,?,?,?)";
				try
				{
					//ubacivanje podataka u tabelu kreditori
					ii = jdbcTemplate.update(sql,body.get("sifobs").trim(),body.get("nazobs").trim(),body.get("zirobs").trim()
							,body.get("pozobs").trim(),body.get("naobs").trim(),juzer);
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
		public String izmenaKreditori(@RequestParam HashMap<String, String> body ) {
					int ii = 0;
					String sql="",juzer="";
					String sifobs = ""; 
					sifobs = body.get("sifobs").trim();
					
					Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			        juzer = auth.getName();			
					
			        sql = "UPDATE kreditori SET nazobs=?,zirobs=?,pozobs=?,naobs=?" + 
			        		" WHERE sifobs=? AND  juzer=?";
						try
						{
							//izmena podataka u tabelu kreditori
							ii = jdbcTemplate.update(sql,body.get("nazobs").trim(),body.get("zirobs").trim(),body.get("pozobs").trim(),
									body.get("naobs").trim(),sifobs,juzer);
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
		public String deleteKreditori(@RequestParam HashMap<String, String> body ) {
			int ii = 0;
			String sql="",juzer="";
			String sifobs = body.get("sifobs").trim(); 
			
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	        juzer = auth.getName();		
	        
	        sql = "DELETE FROM kreditori WHERE sifobs=? AND juzer=?";
			ii = jdbcTemplate.update(sql,sifobs,juzer);		
			
			return String.valueOf(ii);
		}
//==============================================================================================	
		class KreditoriRowMaper implements RowMapper<Kreditori>{
			@Override
			public Kreditori mapRow(ResultSet rs, int rowNum) throws SQLException {
				   Kreditori mat = new Kreditori();
				   mat.setSifobs(rs.getInt("sifobs"));
				   mat.setNazobs(rs.getString("nazobs"));
				   mat.setZirobs(rs.getString("zirobs"));
				   mat.setPozobs(rs.getString("pozobs"));
				   mat.setNaobs(rs.getInt("naobs"));
				   return mat;
			}
		}
//--------------------------------------------------------------------------		
}
