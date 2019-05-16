package com.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.model.Preduzeca;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.sql.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequestMapping("/godina")
public class GodinaController {
	@Autowired
    private JdbcTemplate jdbcTemplate;	
	//-------------------------------------------------------------------------
	//vraca sve slogove
	@RequestMapping(value = "/preduzeca", method = RequestMethod.GET)
	@ResponseBody
	public String getAllPreduzeca(@RequestParam HashMap<String, String> body ) {
		String sql = "",juzer="";
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        juzer = auth.getName();			
		
		sql = "select * from preduzeca WHERE juzer=?";
		String vrati="";
		int ii=1;
		
        vrati = vrati + "{rows:[";
 		List<Preduzeca> lista = jdbcTemplate.query(sql,new Object[] { juzer },new PredRowMapper());  
 		
 		
		for (Iterator<Preduzeca> iter = lista.iterator(); iter.hasNext(); ) {
  		   if (ii>1){
			   vrati = vrati + ",";
		   }
       		vrati = vrati + "{ id:" + String.valueOf(ii) + ",data:[";
       		Preduzeca mat = iter.next();
       		vrati = vrati + "\"" + String.valueOf(mat.getPre()) + "\",";
        	vrati = vrati + "\"" + String.valueOf(mat.getNaziv()) + "\",";
        	vrati = vrati + "\"" + String.valueOf(mat.getMesto()) + "\",";
           	vrati = vrati + "\"" + String.valueOf(mat.getAdresa()) + "\"";
			vrati = vrati + "]}";
			ii = ii + 1;
		}
		vrati = vrati + "]};";
 		return vrati;
	}
	//-------------------------------------------------------------------------
	//vraca sve slogove
	@RequestMapping(value = "/podaciuser", method = RequestMethod.GET, produces="application/json")
	@ResponseBody
	public String getAllPodaci(@RequestParam HashMap<String, String> body ) {
		String sql = "",juzer="";
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        juzer = auth.getName();			
		
		sql = "select ime,prezime,firma,godina,nazivfirme from users where username=?";
        
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

	//==============================================================================================	
			class PredRowMapper implements RowMapper<Preduzeca>{
				@Override
				public Preduzeca mapRow(ResultSet rs, int rowNum) throws SQLException {
					   Preduzeca mat = new Preduzeca();
					   mat.setPre(rs.getInt("pre"));
					   mat.setNaziv(rs.getString("naziv"));
					   mat.setMesto(rs.getString("mesto"));
					   mat.setAdresa(rs.getString("adresa"));
					   return mat;
				}
			}
	//--------------------------------------------------------------------------		
}


