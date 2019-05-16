package com.controller;

import com.model.Doprinosi;
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
@RequestMapping("/doprinosi")
public class DoprinosiController {
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
			
			sql = "select * from doprinosi WHERE pre=?";
			String vrati="";
			int ii=1;
			
	        vrati = vrati + "{rows:[";
	 		
	        //lista = jdbcTemplate.query(sql,new DopRowMaper());	
	        List<Doprinosi> lista = jdbcTemplate.query(sql,new Object[] { pre },new DopRowMaper());    				
	 		
			for (Iterator<Doprinosi> iter = lista.iterator(); iter.hasNext(); ) {
	  		   if (ii>1){
				   vrati = vrati + ",";
			   }
	       		vrati = vrati + "{ id:" + String.valueOf(ii) + ",data:[";
	       		Doprinosi mat = iter.next();
	       		vrati = vrati + "\"" + String.valueOf(mat.getVrstaplate()) + "\",";
	       		vrati = vrati + "\"" + String.valueOf(mat.getSifdop()) + "\",";
	        	vrati = vrati + "\"" + String.valueOf(mat.getVrdop()) + "\",";
	        	vrati = vrati + "\"" + String.valueOf(mat.getNazdop()) + "\",";
	        	vrati = vrati + "\"" + String.valueOf(mat.getZirdop()) + "\",";
	           	vrati = vrati + "\"" + String.valueOf(mat.getPozdop()) + "\",";
	           	vrati = vrati + "\"" + String.valueOf(mat.getProcdop()) + "\"";
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
			String sifdop="",sql="",juzer="",pre="1",vrstaplate="";
			sifdop = body.get("sifdop"); 
			vrstaplate = body.get("vrstaplate"); 
			
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	        juzer = auth.getName();
	        
	        List<Map<String, Object>> list;
	        
			sql = "select * from doprinosi where pre=?";
			
			if (sifdop!=null && sifdop.trim().length()>0 && vrstaplate!=null && vrstaplate.trim().length()>0){
				sql = sql + " AND sifdop=? AND vrstaplate=?";
				list = jdbcTemplate.queryForList(sql,pre,sifdop,vrstaplate);
			}else {
				list = jdbcTemplate.queryForList(sql,juzer,pre);
			}
	        
        
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
		public String unosSind(@RequestParam HashMap<String, String> body ) {
			int ii = 0;
			String sql="",juzer="",pre="1";
			
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	        juzer = auth.getName();			
			
	        sql = "INSERT INTO doprinosi(pre,sifdop,vrdop,nazdop,zirdop,pozdop,procdop,sifra,vrstaplate)" + 
	        		" VALUES(?,?,?,?,?,?,?,?,?)";
				try
				{
					//ubacivanje podataka u tabelu doprinosi
					ii = jdbcTemplate.update(sql,pre,body.get("sifdop").trim(),body.get("vrdop").trim(),body.get("nazdop").trim(),
							body.get("zirdop").trim(),body.get("pozdop").trim(),body.get("procdop").trim(),body.get("sifra").trim(),
							body.get("vrstaplate").trim());
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
		public String izmenaSind(@RequestParam HashMap<String, String> body ) {
					int ii = 0;
					String sql="",juzer="",pre="1",vrstaplate="1";
					String sifdop = ""; 
					sifdop = body.get("sifdop");
					vrstaplate = body.get("vrstaplate");
					
					Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			        juzer = auth.getName();			
					
			        sql = "UPDATE doprinosi SET nazdop=?,vrdop=?,zirdop=?,pozdop=?,sifra=?,procdop=?" + 
			        		" WHERE vrstaplate=? AND sifdop=? AND pre=?";
						try
						{
							//izmena podataka u tabelu doprinosi
							ii = jdbcTemplate.update(sql,body.get("nazdop"),body.get("vrdop"),body.get("zirdop"),
									body.get("pozdop"),body.get("sifra"),body.get("procdop"),vrstaplate,sifdop,pre);
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
		public String deleteSind(@RequestParam HashMap<String, String> body ) {
			int ii = 0;
			String sql="",juzer="",pre="1",vrstaplate="1";
			String sifdop = body.get("sifdop"); 
			vrstaplate = body.get("vrstaplate");
			
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	        juzer = auth.getName();		
	        
	        sql = "DELETE FROM doprinosi WHERE sifdop=? AND pre=? AND vrstaplate=?";
			ii = jdbcTemplate.update(sql,sifdop,pre,vrstaplate);		
			
			return String.valueOf(ii);
		}
//==============================================================================================	
		class DopRowMaper implements RowMapper<Doprinosi>{
			@Override
			public Doprinosi mapRow(ResultSet rs, int rowNum) throws SQLException {
				   Doprinosi mat = new Doprinosi();
				   mat.setVrstaplate(rs.getInt("vrstaplate"));
				   mat.setSifdop(rs.getInt("sifdop"));
				   mat.setNazdop(rs.getString("nazdop"));
				   mat.setVrdop(rs.getInt("vrdop"));
				   mat.setZirdop(rs.getString("zirdop"));
				   mat.setPozdop(rs.getString("pozdop"));
				   mat.setSifra(rs.getString("sifra"));
				   mat.setProcdop(rs.getDouble("procdop"));
				   return mat;
			}
		}
//--------------------------------------------------------------------------		
}
