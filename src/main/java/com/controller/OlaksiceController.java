package com.controller;

import com.model.Olaksice;
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
@RequestMapping("/olaksice")
public class OlaksiceController {
	@Autowired
    private JdbcTemplate jdbcTemplate;	
//-------------------------------------------------------------------------
		//vraca sve slogove
		@RequestMapping(value = "/prikaz", method = RequestMethod.GET)
		@ResponseBody
		public String getAllOlaksice(@RequestParam HashMap<String, String> body ) {
			String sql ="",juzer="";
			
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	        juzer = auth.getName();			

			sql = "select * from plola";
			String vrati="";
			int ii=1;
			
	        vrati = vrati + "{rows:[";
	 		List<Olaksice> lista = jdbcTemplate.query(sql,new OlaksiceRowMapper());	
	 		
			for (Iterator<Olaksice> iter = lista.iterator(); iter.hasNext(); ) {
	  		   if (ii>1){
				   vrati = vrati + ",";
			   }
	       		vrati = vrati + "{ id:" + String.valueOf(ii) + ",data:[";
	       		Olaksice mat = iter.next();
	       		vrati = vrati + "\"" + String.valueOf(mat.getBr()) + "\",";
	        	vrati = vrati + "\"" + String.valueOf(mat.getNaz()) + "\",";
	        	vrati = vrati + "\"" + String.valueOf(mat.getOpis()) + "\",";
	           	vrati = vrati + "\"" + String.valueOf(mat.getInd1()) + "\",";
	           	vrati = vrati + "\"" + String.valueOf(mat.getInd2()) + "\",";
	           	vrati = vrati + "\"" + String.valueOf(mat.getInd3()) + "\",";
	           	vrati = vrati + "\"" + String.valueOf(mat.getInd4()) + "\",";
	           	vrati = vrati + "\"" + String.valueOf(mat.getInd5()) + "\",";
	           	vrati = vrati + "\"" + String.valueOf(mat.getInd6()) + "\",";
	           	vrati = vrati + "\"" + String.valueOf(mat.getInd7()) + "\",";
	           	vrati = vrati + "\"" + String.valueOf(mat.getInd8()) + "\"";
				vrati = vrati + "]}";
				ii = ii + 1;
			}
			vrati = vrati + "]};";
	 		return vrati;
		}
//-------------------------------------------------------------------------------------------------------
		@RequestMapping(value = "/prikazjedan", method = RequestMethod.GET, produces="application/json")
		@ResponseBody
		public String getSingleOlaksice(@RequestParam HashMap<String, String> body ) {
			String br="0",sql="";
			br = body.get("br"); 
			

	        if (br!=null && br.trim().length()>0){
	        	sql = "select * plola where br=?";
			}
	        
	        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql,br);
	        
	        String json="";
	        final ObjectMapper mapper = new ObjectMapper();
	        try {
	        	json = mapper.writeValueAsString(list);
	        }catch(JsonProcessingException ee) {
	        	ee.printStackTrace();
	        }
	        return json;
		}
		@RequestMapping(value = "/izmena", method = RequestMethod.POST, produces="application/json")
		@Transactional
		@ResponseBody
		public String izmenaOlaksice(@RequestParam HashMap<String, String> body ) {
					int ii = 0;
					String sql="";
					String br = ""; 
					br = body.get("br");
					

			        
			        sql = "UPDATE plola SET ind1=?,ind2=?,ind3=?,ind4=?,ind5=?,ind6=?,ind7=?,ind8=?" + 
			        		" WHERE br=?";
						try
						{
							//izmena podataka u tabelu banke
							ii = jdbcTemplate.update(sql,body.get("ind1").trim(),body.get("ind2").trim(),body.get("ind3").trim(),
									body.get("ind4").trim(),body.get("ind5").trim(),body.get("ind6").trim(),
									body.get("ind7").trim(),body.get("ind8").trim(),br);
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
		public String deleteOlaksice(@RequestParam HashMap<String, String> body ) {
			int ii = 0;
			String sql="",br="0";
			br = body.get("br").trim(); 
			
	        sql = "DELETE FROM plola WHERE br=?";
			ii = jdbcTemplate.update(sql,br);		
			
			return String.valueOf(ii);
		}
//==============================================================================================	
		class OlaksiceRowMapper implements RowMapper<Olaksice>{
			@Override
			public Olaksice mapRow(ResultSet rs, int rowNum) throws SQLException {
				   Olaksice mat = new Olaksice();
				   mat.setBr(rs.getInt("br"));
				   mat.setNaz(rs.getString("naz"));
				   mat.setOpis(rs.getString("opis"));
				   mat.setInd1(rs.getDouble("ind1"));
				   mat.setInd2(rs.getDouble("ind2"));
				   mat.setInd3(rs.getDouble("ind3"));
				   mat.setInd4(rs.getDouble("ind4"));
				   mat.setInd5(rs.getDouble("ind5"));
				   mat.setInd6(rs.getDouble("ind6"));
				   mat.setInd7(rs.getDouble("ind7"));
				   mat.setInd8(rs.getDouble("ind8"));
				   return mat;
			}
		}
//--------------------------------------------------------------------------		
}
