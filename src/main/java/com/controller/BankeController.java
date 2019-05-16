package com.controller;

import com.model.Banke;
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
@RequestMapping("/banke")
public class BankeController {
	@Autowired
    private JdbcTemplate jdbcTemplate;	
//-------------------------------------------------------------------------
		//vraca sve slogove
		@RequestMapping(value = "/prikaz", method = RequestMethod.GET)
		@ResponseBody
		public String getAllBanke(@RequestParam HashMap<String, String> body ) {
			String sql ="",juzer="";
			
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	        juzer = auth.getName();			

			sql = "select * from banke WHERE juzer=?";
			String vrati="";
			int ii=1;
			
	        vrati = vrati + "{rows:[";
	 		List<Banke> lista = jdbcTemplate.query(sql,new BankeRowMapper(),juzer);	
	 		
			for (Iterator<Banke> iter = lista.iterator(); iter.hasNext(); ) {
	  		   if (ii>1){
				   vrati = vrati + ",";
			   }
	       		vrati = vrati + "{ id:" + String.valueOf(ii) + ",data:[";
	       		Banke mat = iter.next();
	       		vrati = vrati + "\"" + String.valueOf(mat.getSifban()) + "\",";
	        	vrati = vrati + "\"" + String.valueOf(mat.getNazban()) + "\",";
	        	vrati = vrati + "\"" + String.valueOf(mat.getZirban()) + "\",";
	           	vrati = vrati + "\"" + String.valueOf(mat.getPozban()) + "\",";
	           	vrati = vrati + "\"" + String.valueOf(mat.getIsplata()) + "\"";
				vrati = vrati + "]}";
				ii = ii + 1;
			}
			vrati = vrati + "]};";
	 		return vrati;
		}
//-------------------------------------------------------------------------------------------------------
		@RequestMapping(value = "/prikazjedan", method = RequestMethod.GET, produces="application/json")
		@ResponseBody
		public String getSingleBanke(@RequestParam HashMap<String, String> body ) {
			String sifban="",sql="",juzer="";
			sifban = body.get("sifban"); 
			
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	        juzer = auth.getName();			

	        if (sifban!=null && sifban.trim().length()>0){
	        	sql = "select * from banke where sifban=? AND juzer=?";
			}
	        
	        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql,sifban,juzer);
	        
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
		public String unosBanke(@RequestParam HashMap<String, String> body ) {
			int ii = 0;
			String sql="",juzer="";
			
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	        juzer = auth.getName();			

	        sql = "INSERT INTO banke(sifban,nazban,zirban,pozban,isplata,juzer)" + 
	        		" VALUES(?,?,?,?,?,?)";
				try
				{
					//ubacivanje podataka u tabelu banke
					ii = jdbcTemplate.update(sql,body.get("sifban").trim(),body.get("nazban").trim(),body.get("zirban").trim(),
							body.get("pozban").trim(),body.get("isplata").trim(),juzer);
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
		public String izmenaBanke(@RequestParam HashMap<String, String> body ) {
					int ii = 0;
					String sql="";
					String sifban = "",juzer=""; 
					sifban = body.get("sifban").trim();
					
					Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			        juzer = auth.getName();			

			        
			        sql = "UPDATE banke SET nazban=?,zirban=?,pozban=?,isplata=?" + 
			        		" WHERE sifban=? AND juzer=?";
						try
						{
							//izmena podataka u tabelu banke
							ii = jdbcTemplate.update(sql,body.get("nazban").trim(),body.get("zirban").trim(),body.get("pozban").trim(),
									body.get("isplata").trim(),sifban,juzer);
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
		public String deleteBanke(@RequestParam HashMap<String, String> body ) {
			int ii = 0;
			String sql="",juzer="";
			String sifban = body.get("sifban").trim(); 

			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	        juzer = auth.getName();			
			
	        sql = "DELETE FROM banke WHERE sifban=? AND juzer=?";
			ii = jdbcTemplate.update(sql,sifban,juzer);		
			
			return String.valueOf(ii);
		}
//==============================================================================================	
		class BankeRowMapper implements RowMapper<Banke>{
			@Override
			public Banke mapRow(ResultSet rs, int rowNum) throws SQLException {
				   Banke mat = new Banke();
				   mat.setSifban(rs.getInt("sifban"));
				   mat.setNazban(rs.getString("nazban"));
				   mat.setZirban(rs.getString("zirban"));
				   mat.setPozban(rs.getString("pozban"));
				   return mat;
			}
		}
//--------------------------------------------------------------------------		
}
