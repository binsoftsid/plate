package com.controller;

import com.model.Krediti;
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
import org.springframework.transaction.annotation.Transactional;

@RestController
@RequestMapping("/krediti")
public class KreditiController {
	@Autowired
    private JdbcTemplate jdbcTemplate;	
//-------------------------------------------------------------------------
		//vraca sve slogove
		@RequestMapping(value = "/prikaz", method = RequestMethod.GET)
		@ResponseBody
		public String getAllKrediti(@RequestParam HashMap<String, String> body ) {
			String sql ="",juzer="",pre="",sifra="",godina="";
			sifra = body.get("sifra"); 
			pre = body.get("pre"); 
			godina = body.get("godina"); 
			
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	        juzer = auth.getName();			
			
			sql = "select * from krediti WHERE juzer=? AND sifra=? AND pre=? AND godina=?";
			String vrati="";
			int ii=1;
			
	        vrati = vrati + "{rows:[";
	 		List<Krediti> lista = jdbcTemplate.query(sql,new Object[] { juzer,sifra,pre,godina },new KreditiRowMaper()); 
	 		
			for (Iterator<Krediti> iter = lista.iterator(); iter.hasNext(); ) {
	  		   if (ii>1){
				   vrati = vrati + ",";
			   }
	       		vrati = vrati + "{ id:" + String.valueOf(ii) + ",data:[";
	       		Krediti mat = iter.next();
	       		vrati = vrati + "\"" + String.valueOf(mat.getSifobs()) + "\",";
	        	vrati = vrati + "\"" + String.valueOf(mat.getNazobs()) + "\",";
	        	vrati = vrati + "\"" + String.valueOf(mat.getPartija()) + "\",";
	           	vrati = vrati + "\"" + String.valueOf(mat.getDug()) + "\",";
	           	vrati = vrati + "\"" + String.valueOf(mat.getRata()) + "\",";
	           	vrati = vrati + "\"" + String.valueOf(mat.getNaobs()) + "\"";
				vrati = vrati + "]}";
				ii = ii + 1;
			}
			vrati = vrati + "]};";
	 		return vrati;
		}
//---------------------------vraca izabrani kredit------------------------------------------------------
		@RequestMapping(value = "/prikazjedan", method = RequestMethod.GET, produces="application/json")
		@ResponseBody
		public String getSingleKrediti(@RequestParam HashMap<String, String> body ) {
			String sifobs="",sql="",juzer="",pre="1",sifra="",partija="",godina="";
			sifra = body.get("sifra"); 
			sifobs = body.get("sifobs"); 
			partija = body.get("partija"); 
			pre = body.get("pre"); 
			godina = body.get("godina"); 
			
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	        juzer = auth.getName();
	        
			sql = "select * from krediti where pre=? AND juzer=? AND sifobs=? AND sifra=? AND partija=? AND godina=?";
	        
	        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql,pre,juzer,sifobs,sifra,partija,godina);
	        
	        String json="";
	        final ObjectMapper mapper = new ObjectMapper();
	        try {
	        	json = mapper.writeValueAsString(list);
	        }catch(JsonProcessingException ee) {
	        	ee.printStackTrace();
	        }
	        return json;
		}
//------------------------------------------------------------------------------------------------
		@RequestMapping(value = "/unos", method = RequestMethod.POST, produces="application/json")
		@Transactional
		@ResponseBody
		public String unosKrediti(@RequestParam HashMap<String, String> body ) {
			int ii = 0;
			String sql="",juzer="",pre="1",godina="";
			pre = body.get("pre").trim(); 
			godina = body.get("godina").trim(); 
			
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	        juzer = auth.getName();			
			
	        sql = "INSERT INTO krediti(pre,sifra,sifobs,nazobs,partija,dug,rata,naobs,juzer,godina)" + 
	        		" VALUES(?,?,?,?,?,?,?,?,?,?)";
				try
				{
					//ubacivanje podataka u tabelu krediti
					ii = jdbcTemplate.update(sql,pre,body.get("sifra").trim(),body.get("sifobs").trim(),body.get("nazobs").trim(),
							body.get("partija").trim(),body.get("dug").trim(),body.get("rata").trim(),body.get("naobs").trim(),juzer,godina);
				}
				catch (DataAccessException e)
				{
					throw new RuntimeException(e);
				}
			
			return String.valueOf(ii);
		}
//---------------------------------------------------------------------------------------------------
		@RequestMapping(value = "/izmena", method = RequestMethod.POST, produces="application/json")
		@Transactional
		@ResponseBody
		public String izmenaKrediti(@RequestParam HashMap<String, String> body ) {
				int ii = 0;
				String sql="",juzer="",pre="1",sifra="",godina="";
				String sifobs = ""; 
				sifra = body.get("sifra").trim();
				sifobs = body.get("sifobs").trim();
				pre = body.get("pre").trim(); 
				godina = body.get("godina").trim(); 
					
				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			    juzer = auth.getName();			
					
			    sql = "UPDATE krediti SET nazobs=?,dug=?,rata=?,naobs=?" + 
			        		" WHERE pre=? AND sifra=? AND sifobs=? AND juzer=? AND godina=? AND partija=?";
						try
						{
							//izmena podataka u tabelu krediti
							ii = jdbcTemplate.update(sql,body.get("nazobs").trim(),body.get("dug").trim(),
									body.get("rata").trim(),body.get("naobs").trim(),pre,sifra,sifobs,juzer,godina,body.get("partija").trim());
						}
						catch (DataAccessException e)
						{
							throw new RuntimeException(e);
						}
					
				return String.valueOf(ii);
		}
//--------------------------------------------------------------------------------------------------
		@RequestMapping(value = "/brisanje", method = RequestMethod.POST)
		@Transactional
		@ResponseBody
		public String deleteKrediti(@RequestParam HashMap<String, String> body ) {
			int ii = 0;
			String sql="",juzer="",pre="1",godina="";
			String sifra = body.get("sifra").trim(); 
			String sifobs = body.get("sifobs").trim(); 
			String partija = body.get("partija").trim(); 
			pre = body.get("pre").trim(); 
			godina = body.get("godina").trim(); 
			
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	        juzer = auth.getName();		
	        
	        sql = "DELETE FROM krediti WHERE sifra=? AND sifobs=? AND juzer=? AND pre=? AND godina=? AND partija=?";
			ii = jdbcTemplate.update(sql,sifra,sifobs,juzer,pre,godina,partija);		
			
			return String.valueOf(ii);
		}
//--------------------------------------------------------------------------------------------------
		@RequestMapping(value = "/provera", method = RequestMethod.GET, produces="application/json")
		@ResponseBody
		public String getProveraKrediti(@RequestParam HashMap<String, String> body ) {
					String sifobs="",sql="",juzer="",pre="1",sifra="",partija="",godina="";
					int postoji=0;
					sifra = body.get("sifra").trim(); 
					sifobs = body.get("sifobs").trim(); 
					partija = body.get("partija").trim(); 
					pre = body.get("pre").trim(); 
					godina = body.get("godina").trim(); 
					
					Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			        juzer = auth.getName();
			        
					sql = "select count(*) from krediti where juzer=? AND sifobs=? AND sifra=? AND pre=? AND partija=?" +
							" AND godina=?";
			        
			        
					postoji =  jdbcTemplate.queryForObject(sql, new Object[] { juzer,sifobs,sifra,pre,partija,godina }, Integer.class);
			        
			        String vrati= String.valueOf(postoji);
			        return vrati;
		}
//=================================================================================================
		class KreditiRowMaper implements RowMapper<Krediti>{
			@Override
			public Krediti mapRow(ResultSet rs, int rowNum) throws SQLException {
				   Krediti mat = new Krediti();
				   mat.setSifra(rs.getInt("sifra"));
				   mat.setSifobs(rs.getInt("sifobs"));
				   mat.setPartija(rs.getString("partija"));
				   mat.setNazobs(rs.getString("nazobs"));
				   mat.setDug(rs.getDouble("dug"));
				   mat.setRata(rs.getDouble("rata"));
				   mat.setNaobs(rs.getInt("naobs"));
				   return mat;
			}
		}
//---------------------------------------------------------------------------------------------------	
}
