package com.controller;

import com.model.Mesnezajednice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.sql.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/mesnezajednice")
public class MZController {
	@Autowired
    private JdbcTemplate jdbcTemplate;	
	
//-------------------------------------------------------------------------
		//vraca sve slogove
		@RequestMapping(value = "/prikaz", method = RequestMethod.GET)
		@ResponseBody
		public String getAllMZ(@RequestParam HashMap<String, String> body ) {
			String sql ="",juzer="";
			
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	        juzer = auth.getName();			
			
			sql = "select * from mesnezajednice WHERE juzer=?";
			String vrati="";
			int ii=1;
			
	        vrati = vrati + "{rows:[";
	        
	 		List<Mesnezajednice> lista = jdbcTemplate.query(sql,new Object[] { juzer },new MZRowMapper());
	 		
	 		
			for (Iterator<Mesnezajednice> iter = lista.iterator(); iter.hasNext(); ) {
	  		   if (ii>1){
				   vrati = vrati + ",";
			   }
	       		vrati = vrati + "{ id:" + String.valueOf(ii) + ",data:[";
	       		Mesnezajednice mat = iter.next();
	       		vrati = vrati + "\"" + String.valueOf(mat.getSifmz()) + "\",";
	        	vrati = vrati + "\"" + String.valueOf(mat.getNazmz()) + "\",";
	        	vrati = vrati + "\"" + String.valueOf(mat.getZirmz()) + "\",";
	           	vrati = vrati + "\"" + String.valueOf(mat.getPozmz()) + "\",";
	           	vrati = vrati + "\"" + String.valueOf(mat.getProcmz()) + "\"";
				vrati = vrati + "]}";
				ii = ii + 1;
			}
			vrati = vrati + "]};";
	 		return vrati;
		}
//---------------------------vraca izabrano preduzece-------------------------------------------
		@RequestMapping(value = "/prikazjedan", method = RequestMethod.GET, produces="application/json")
		@ResponseBody
		public String getSingleMZ(@RequestParam HashMap<String, String> body ) {
			String sifmz="",sql="",juzer="";
			sifmz = body.get("sifmz").trim(); 
			
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	        juzer = auth.getName();
	        
			sql = "select * from mesnezajednice where juzer=? AND sifmz=?";
	        
	        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql,juzer,sifmz);
	        
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
		public String unosMZ(@RequestParam HashMap<String, String> body ) {
			int ii = 0;
			String sql="",juzer="";
			
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	        juzer = auth.getName();			
			
	        sql = "INSERT INTO mesnezajednice(sifmz,nazmz,zirmz,pozmz,procmz,juzer)" + 
	        		" VALUES(?,?,?,?,?,?)";
				try
				{
					//ubacivanje podataka u tabelu mesnezajednice
					ii = jdbcTemplate.update(sql,body.get("sifmz").trim(),body.get("nazmz").trim(),
							body.get("zirmz").trim(),body.get("pozmz").trim(),
							body.get("procmz").trim(),juzer);
				}
				catch (DataAccessException e)
				{
					throw new RuntimeException(e);
				}
			
			return String.valueOf(ii);
		}
//-------------------------------------------------------------------------------------------
		@RequestMapping(value = "/izmena", method = RequestMethod.POST, produces="application/json")
		@Transactional
		@ResponseBody
		public String izmenaMZ(@RequestParam HashMap<String, String> body ) {
					int ii = 0;
					String sql="",juzer="";
					String sifmz = ""; 
					sifmz = body.get("sifmz").trim();
					
					Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			        juzer = auth.getName();			
					
			        sql = "UPDATE mesnezajednice SET nazmz=?,zirmz=?,pozmz=?,procmz=?" + 
			        		" WHERE sifmz=? AND  juzer=?";
						try
						{
							//izmena podataka u tabelu mesnezajednice
							ii = jdbcTemplate.update(sql,body.get("nazmz").trim(),body.get("zirmz").trim(),body.get("pozmz").trim(),
									body.get("procmz").trim(),sifmz,juzer);
						}
						catch (DataAccessException e)
						{
							throw new RuntimeException(e);
						}
					
					return String.valueOf(ii);
		}
//---------------------------------------------------------------------------------------------	
		@RequestMapping(value = "/brisanje", method = RequestMethod.POST)
		@Transactional
		@ResponseBody
		public String deleteMZ(@RequestParam HashMap<String, String> body ) {
			int ii = 0;
			String sql="",juzer="";
			String sifmz = body.get("sifmz").trim(); 
			
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	        juzer = auth.getName();		
	        
	        sql = "DELETE FROM mesnezajednice WHERE sifmz=? AND juzer=? ";
			ii = jdbcTemplate.update(sql,sifmz,juzer);		
			
			return String.valueOf(ii);
		}
//==============================================================================================	
		class MZRowMapper implements RowMapper<Mesnezajednice>{
			@Override
			public Mesnezajednice mapRow(ResultSet rs, int rowNum) throws SQLException {
				   Mesnezajednice mat = new Mesnezajednice();
				   mat.setSifmz(rs.getInt("sifmz"));
				   mat.setNazmz(rs.getString("nazmz"));
				   mat.setZirmz(rs.getString("zirmz"));
				   mat.setPozmz(rs.getString("pozmz"));
				   mat.setProcmz(rs.getDouble("procmz"));
				   return mat;
			}
		}
//--------------------------------------------------------------------------		
}
