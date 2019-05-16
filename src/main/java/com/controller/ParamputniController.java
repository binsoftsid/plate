package com.controller;

import com.model.Paramputni;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.sql.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/paramputni")
public class ParamputniController {
	@Autowired
    private JdbcTemplate jdbcTemplate;	
	
//-------------------------------------------------------------------------
		//vraca sve slogove
		@RequestMapping(value = "/prikaz", method = RequestMethod.GET)
		@ResponseBody
		public String getAllMZ(@RequestParam HashMap<String, String> body ) {
			String sql ="",rbr="1";
			
			sql = "select * from paramputni WHERE rbr=?";
			String vrati="";
			int ii=1;
			
	        vrati = vrati + "{rows:[";
	        
	 		List<Paramputni> lista = jdbcTemplate.query(sql,new Object[] { rbr },new PPRowMapper());
	 		
	 		
			for (Iterator<Paramputni> iter = lista.iterator(); iter.hasNext(); ) {
	  		   if (ii>1){
				   vrati = vrati + ",";
			   }
	       		vrati = vrati + "{ id:" + String.valueOf(ii) + ",data:[";
	       		Paramputni mat = iter.next();
	       		vrati = vrati + "\"" + String.valueOf(mat.getRbr()) + "\",";
	        	vrati = vrati + "\"" + String.valueOf(mat.getNeoporiznos()) + "\",";
	        	vrati = vrati + "\"" + String.valueOf(mat.getPorez()) + "\"";
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
			String sql="",rbr="1";
			
	        
			sql = "select * from paramputni where rbr=?";
	        
	        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql,rbr);
	        
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
			String sql="";
			
			
	        sql = "INSERT INTO paramputni(rbr,neoporeziviiznos,porez)" + 
	        		" VALUES(?,?,?)";
				try
				{
					//ubacivanje podataka u tabelu mesnezajednice
					ii = jdbcTemplate.update(sql,"1",body.get("neoporeziviiznos").trim(),body.get("porez").trim());
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
					String sql="";
					
			        sql = "UPDATE paramputni SET neoporeziviiznos=?,porez=?" + 
			        		" WHERE rbr=?";
						try
						{
							//izmena podataka u tabelu mesnezajednice
							ii = jdbcTemplate.update(sql,body.get("neoporeziviiznos").trim(),body.get("porez").trim(),"1");
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
			String sql="";
			
	        sql = "DELETE FROM paramputni WHERE rbr=?";
			ii = jdbcTemplate.update(sql,"1");		
			
			return String.valueOf(ii);
		}
//==============================================================================================	
		class PPRowMapper implements RowMapper<Paramputni>{
			@Override
			public Paramputni mapRow(ResultSet rs, int rowNum) throws SQLException {
				   Paramputni mat = new Paramputni();
				   mat.setRbr(rs.getInt("rbr"));
				   mat.setNeoporiznos(rs.getDouble("neoporeziviiznos"));
				   mat.setPorez(rs.getDouble("porez"));
				   return mat;
			}
		}
//--------------------------------------------------------------------------		
}
