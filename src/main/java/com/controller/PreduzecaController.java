package com.controller;

import com.model.Preduzeca;
import com.model.ClassParametri;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.annotation.*;
import java.util.*;

import javax.sql.DataSource;

import java.sql.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/preduzeca")
public class PreduzecaController {
	@Autowired
    private JdbcTemplate jdbcTemplate;	
	
	private ClassParametri classParametri;
	
	
//-------------------------------------------------------------------------
		//vraca sve slogove
		@RequestMapping(value = "/prikaz", method = RequestMethod.GET)
		@ResponseBody
		public String getAllPreduzeca(@RequestParam HashMap<String, String> body ) {
			String sql = "",juzer="";
			
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	        juzer = auth.getName();			
			
			sql = "select * from preduzeca WHERE juzer='" + juzer + "'";
			String vrati="";
			int ii=1;
			
	        vrati = vrati + "{rows:[";
	 		List<Preduzeca> lista = jdbcTemplate.query(sql,new PredRowMapper());	
	 		
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
//---------------------------vraca izabrano preduzece-------------------------------------------
		@RequestMapping(value = "/prikazjedan", method = RequestMethod.GET, produces="application/json")
		@ResponseBody
		public String getSinglePreduzeca(@RequestParam HashMap<String, String> body ) {
			String pre="",sql="",juzer="";
			pre = body.get("pre").trim(); 
			
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	        juzer = auth.getName();
	        
			sql = "select * from preduzeca where juzer='" + juzer + "'";
			if (pre!=null && pre.trim().length()>0){
				sql = sql + " AND pre=" + pre;
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
		public String unosMaticni(@RequestParam HashMap<String, String> body ) {
			Connection connection=null;
			int ii = 0;
			String sql="",juzer="",pre="";
			pre = body.get("pre");
			
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	        juzer = auth.getName();			
			
	        sql = "INSERT INTO preduzeca(pre,naziv,mesto,adresa,telefon,fax,ziror,pbroj,zirorod,nadimak," + 
	        		"sido,matbr,pib,sifraopstine,juzer)" +
	        		"VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				try
				{
					//ubacivanje podataka u tabelu preduzeca
					ii = jdbcTemplate.update(sql,pre,body.get("naziv").trim(),body.get("mesto").trim(),body.get("adresa").trim(),
							body.get("telefon").trim(),body.get("fax").trim(),body.get("ziror").trim(),body.get("pbroj").trim(),
							body.get("zirorod").trim(),body.get("nadimak").trim(),body.get("matbr").trim(),body.get("sido").trim(),
							body.get("pib").trim(),body.get("sifraopstine").trim(),juzer);
			        try{
						DataSource datasource = jdbcTemplate.getDataSource();
						connection = datasource.getConnection();	
						classParametri = new ClassParametri();
						classParametri.kopirajParametre(connection,pre, juzer);
					}catch(SQLException ee){
						ee.printStackTrace();
					}						
					//kopiranje parametara obracuna u novo preduzece za tog usera
			        //classParametri = new ClassParametri(connection);
			       
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
		public String izmenaMaticni(@RequestParam HashMap<String, String> body ) {
					int ii = 0;
					String sql="",juzer="";
					String pre = "1"; 
					pre = body.get("pre").trim();
					
					Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			        juzer = auth.getName();			
					
			        sql = "UPDATE preduzeca SET naziv=?,mesto=?,adresa=?,telefon=?,fax=?,ziror=?,pbroj=?,zirorod=?," + 
			        		"nadimak=?,sido=?,matbr=?,pib=?,sifraopstine=?" +
			        		" WHERE pre=" + pre + " AND  juzer='" + juzer + "'";
						try
						{
							//izmena podataka u tabelu preduzeca
							ii = jdbcTemplate.update(sql,body.get("naziv").trim(),body.get("mesto").trim(),
									body.get("adresa").trim(),body.get("telefon").trim(),body.get("fax").trim(),
									body.get("ziror").trim(),body.get("pbroj").trim(),body.get("zirorod").trim(),
									body.get("nadimak").trim(),body.get("sido").trim(),body.get("matbr").trim(),
									body.get("pib").trim(),body.get("sifraopstine").trim());
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
		public String deleteMat(@RequestParam HashMap<String, String> body ) {
			int ii = 0;
			String sql="",juzer="";
			String pre = body.get("pre").trim(); 
			
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	        juzer = auth.getName();		
	        
	        sql = "DELETE FROM preduzeca WHERE pre=? AND juzer=?";
			ii = jdbcTemplate.update(sql,pre,juzer);		
	        sql = "DELETE FROM parametriobracuna WHERE pre=? AND juzer=?";
			ii = jdbcTemplate.update(sql,pre,juzer);		
			
			return String.valueOf(ii);
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
