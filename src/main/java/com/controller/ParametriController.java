package com.controller;

import com.model.Parametri;
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
@RequestMapping("/parametri")
public class ParametriController {
	@Autowired
    private JdbcTemplate jdbcTemplate;	
//-------------------------------------------------------------------------
		//vraca sve slogove
		@RequestMapping(value = "/prikaz", method = RequestMethod.GET)
		@ResponseBody
		public String getAllParametri(@RequestParam HashMap<String, String> body ) {
			String sql = "",pre="1",juzer="";
			
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	        juzer = auth.getName();			
			
			sql = "select * from parametriobracuna WHERE juzer='" + juzer + "' AND pre=" + pre;
			String vrati="";
			int ii=1;
			
	        vrati = vrati + "{rows:[";
	 		List<Parametri> lista = jdbcTemplate.query(sql,new ParametriRowMapper());	
	 		
			for (Iterator<Parametri> iter = lista.iterator(); iter.hasNext(); ) {
	  		   if (ii>1){
				   vrati = vrati + ",";
			   }
	   
	  		   vrati = vrati + "{ id:" + String.valueOf(ii) + ",data:[";
	       		Parametri mat = iter.next();
	       		vrati = vrati + "\"" + String.valueOf(mat.getRbr()) + "\",";
	        	vrati = vrati + "\"" + String.valueOf(mat.getVrsta()) + "\",";
	        	vrati = vrati + "\"" + String.valueOf(mat.getOpis()) + "\",";
	           	vrati = vrati + "\"" + String.valueOf(mat.getProsek()) + "\",";
	           	vrati = vrati + "\"" + String.valueOf(mat.getLdkoef()) + "\",";
	           	vrati = vrati + "\"" + String.valueOf(mat.getLdzbir()) + "\",";
	           	vrati = vrati + "\"" + String.valueOf(mat.getLdminu()) + "\",";
	           	vrati = vrati + "\"" + String.valueOf(mat.getLdstim()) + "\",";
	           	vrati = vrati + "\"" + String.valueOf(mat.getLdpor()) + "\",";
	           	vrati = vrati + "\"" + String.valueOf(mat.getLdproc()) + "\",";
	           	vrati = vrati + "\"" + String.valueOf(mat.getDalic()) + "\",";
	           	vrati = vrati + "\"" + String.valueOf(mat.getDalii()) + "\"";
				vrati = vrati + "]}";
				ii = ii + 1;
			}
			vrati = vrati + "]};";
	 		return vrati;
		}
//---------------------------vraca izabrano preduzece-------------------------------------------
		@RequestMapping(value = "/prikazjedan", method = RequestMethod.GET, produces="application/json")
		@ResponseBody
		public String getSingleParametri(@RequestParam HashMap<String, String> body ) {
			String pre="1",sql="",juzer="",rbr="";
			rbr = body.get("rbr"); 
			
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	        juzer = auth.getName();
	        
			sql = "select * from parametriobracuna where juzer='" + juzer + "' AND pre=" + pre;
			if (pre!=null && pre.trim().length()>0){
				sql = sql + " AND rbr=" + rbr;
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
				@RequestMapping(value = "/izmena", method = RequestMethod.POST, produces="application/json")
				@ResponseBody
				public String izmenaParametre(@RequestParam HashMap<String, String> body ) {
							int ii = 0;
							String sql="",juzer="",rbr="";
							String pre = "1"; 
							rbr = body.get("rbr").trim();
							
							Authentication auth = SecurityContextHolder.getContext().getAuthentication();
					        juzer = auth.getName();			
							
					        sql = "UPDATE parametriobracuna SET prosek=?,ldkoef=?,ldzbir=?,ldminu=?,ldstim=?,ldpor=?,ldproc=?,dalic=?," + 
					        		"dalii=? WHERE pre=? AND  juzer=? AND rbr=?";
								try
								{
									//izmena podataka u tabelu preduzeca
									ii = jdbcTemplate.update(sql,body.get("prosek").trim(),body.get("ldkoef").trim(),body.get("ldzbir").trim(),
											body.get("ldminu").trim(),body.get("ldstim").trim(),body.get("ldpor").trim(),
											body.get("ldproc").trim(),body.get("dalic").trim(),body.get("dalii").trim(),pre,juzer,rbr);
										}
								catch (DataAccessException e)
								{
									throw new RuntimeException(e);
								}
							
							return String.valueOf(ii);
				}

//==============================================================================================	
		class ParametriRowMapper implements RowMapper<Parametri>{
			@Override
			
			public Parametri mapRow(ResultSet rs, int rowNum) throws SQLException {
				   Parametri mat = new Parametri();
				   mat.setPre(rs.getInt("pre"));
				   mat.setRbr(rs.getInt("rbr"));
				   mat.setVrsta(rs.getInt("vrsta"));
				   mat.setOpis(rs.getString("opis"));
				   mat.setProsek(rs.getInt("prosek"));
				   mat.setLdkoef(rs.getInt("ldkoef"));
				   mat.setLdzbir(rs.getInt("ldzbir"));
				   mat.setLdminu(rs.getInt("ldminu"));
				   mat.setLdstim(rs.getInt("ldstim"));
				   mat.setLdpor(rs.getInt("ldpor"));
				   mat.setLdproc(rs.getDouble("ldproc"));
				   mat.setDalic(rs.getInt("dalic"));
				   mat.setDalii(rs.getInt("dalii"));
				   return mat;
			}
		}
//--------------------------------------------------------------------------		
}
