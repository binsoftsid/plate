package com.controller;

import com.model.Binmesec;
import com.model.Kreditiarh;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/unosapi")
public class UnosController {
	@Autowired
    private JdbcTemplate jdbcTemplate;	

	//vraca sve slogove
	@RequestMapping(value = "/unosprikaz", method = RequestMethod.GET)
	@ResponseBody
	public String getAllNotes(@RequestParam HashMap<String, String> body ) {
		String ime="",sql = "",vrati="",mesec="",vrstaplate="",brojobrade="",godina="",pre="";
		mesec = body.get("mesec"); 
		vrstaplate = body.get("vrstaplate"); 
		brojobrade = body.get("brojobrade"); 
		godina = body.get("godina"); 
		pre = body.get("pre"); 
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        ime = auth.getName();
        
		sql = "select * from binmesec where juzer='" + ime + "' AND godina=" + godina + " AND pre=" + pre;
		if (mesec!=null && mesec.trim().length()>0){
			sql = sql + " AND mesec=" + mesec;
		}
		if (vrstaplate!=null && vrstaplate.trim().length()>0){
			sql = sql + " AND vrstaplate=" + vrstaplate;
		}
		if (brojobrade!=null && brojobrade.trim().length()>0){
			sql = sql + " AND brojobrade=" + brojobrade;
		}

		int ii=1;
        vrati = vrati + "{rows:[";
 		List<Binmesec> lista = jdbcTemplate.query(sql,new UnosRowMapper());		
		for (Iterator<Binmesec> iter = lista.iterator(); iter.hasNext(); ) {
  		   if (ii>1){
			   vrati = vrati + ",";
		   }
       		vrati = vrati + "{ id:" + String.valueOf(ii) + ",data:[";
       		Binmesec binm = iter.next();
       		vrati = vrati + "\"" + String.valueOf(binm.getSifra()) + "\",";
        	vrati = vrati + "\"" + String.valueOf(binm.getIme()) + "\",";
        	vrati = vrati + "\"" + String.valueOf(binm.getPrezime()) + "\",";
        	vrati = vrati + "\"" + String.valueOf(binm.getMesec()) + "\",";
        	vrati = vrati + "\"" + String.valueOf(binm.getBrojobrade()) + "\",";
        	vrati = vrati + "\"" + String.valueOf(binm.getVrstaplate()) + "\"";
			vrati = vrati + "]}";
			ii = ii + 1;
		}
		vrati = vrati + "]};";
 		return vrati;
	}
//---------------------------vraca izabranog radnika binmesec---------------------------------------------
	@RequestMapping(value = "/prikazjedan", method = RequestMethod.GET, produces="application/json")
	@ResponseBody
	public String getSingleNotes(@RequestParam HashMap<String, String> body ) {
		String ime="",sql = "",sifra="",mesec="",vrstaplate="",brojobrade="",godina="",pre="1";
		sifra = body.get("sifra"); 
		mesec = body.get("mesec"); 
		vrstaplate = body.get("vrstaplate"); 
		brojobrade = body.get("brojobrade"); 
		godina = body.get("godina"); 
		pre = body.get("pre"); 
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        ime = auth.getName();
        
		sql = "select * from binmesec where juzer='" + ime + "' AND godina=" + godina + " AND pre=" + pre;
		if (sifra!=null && sifra.trim().length()>0){
			sql = sql + " AND sifra=" + sifra;
		}
		if (mesec!=null && mesec.trim().length()>0){
			sql = sql + " AND mesec=" + mesec;
		}
		if (vrstaplate!=null && vrstaplate.trim().length()>0){
			sql = sql + " AND vrstaplate=" + vrstaplate;
		}
		if (brojobrade!=null && brojobrade.trim().length()>0){
			sql = sql + " AND brojobrade=" + brojobrade;
		}

        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        
        String json = "";
        final ObjectMapper mapper = new ObjectMapper();
        try {
        	json = mapper.writeValueAsString(list);
        }catch(JsonProcessingException ee) {
        	ee.printStackTrace();
        }        
  		return json;
	}
	//---------------------------vraca izabranog radnika ldmes---------------------------------------------
		@RequestMapping(value = "/prikazldmes", method = RequestMethod.GET, produces="application/json")
		@ResponseBody
		public String getSingleNotesLdmes(@RequestParam HashMap<String, String> body ) {
			String ime="",sql = "",sifra="",mesec="",vrstaplate="",brojobrade="",godina="",pre="1";
			sifra = body.get("sifra").trim(); 
			mesec = body.get("mesec").trim(); 
			vrstaplate = body.get("vrstaplate").trim(); 
			brojobrade = body.get("brojobrade").trim(); 
			godina = body.get("godina").trim(); 
			pre = body.get("pre").trim(); 
			
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	        ime = auth.getName();
	        
			sql = "select * from ldmes where juzer='" + ime + "' AND godina=" + godina + " AND pre=" + pre;
			if (sifra!=null && sifra.trim().length()>0){
				sql = sql + " AND sifra=" + sifra;
			}
			if (mesec!=null && mesec.trim().length()>0){
				sql = sql + " AND mesec=" + mesec;
			}
			if (vrstaplate!=null && vrstaplate.trim().length()>0){
				sql = sql + " AND vrstaplate=" + vrstaplate;
			}
			if (brojobrade!=null && brojobrade.trim().length()>0){
				sql = sql + " AND brojobrade=" + brojobrade;
			}

        
	        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
	        
	        String json = "";
	        final ObjectMapper mapper = new ObjectMapper();
	        try {
	        	json = mapper.writeValueAsString(list);
	        }catch(JsonProcessingException ee) {
	        	ee.printStackTrace();
	        }	        
	  		return json;
		}
//------------------------------------------------------------------------------------
	@RequestMapping(value = "/kredprikaz", method = RequestMethod.GET)
	@ResponseBody
	public String getAllKrediti(@RequestParam HashMap<String, String> body ) {
		String ime="",sql = "",vrati="",sifra="",mesec="",vrstaplate="",brojobrade="",godina="",pre="1";
		try {
			sifra = body.get("sifra").trim(); 
		}catch(NullPointerException ee) {
			sifra = null;
		}
		mesec = body.get("mesec").trim(); 
		try {
			vrstaplate = body.get("vrstaplate").trim(); 
		}catch(NullPointerException ee) {
			vrstaplate = null;
		}
		try {
			brojobrade = body.get("brojobrade").trim(); 
		}catch(NullPointerException ee) {
			brojobrade = null;
		}
		try {
			godina = body.get("godina").trim(); 
		}catch(NullPointerException ee) {
			godina = null;
		}
		try {
			pre = body.get("pre").trim(); 
		}catch(NullPointerException ee) {
			pre = null;
		}
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        ime = auth.getName();
        
		sql = "select * from kreditiarh where juzer='" + ime + "' AND godina=" + godina + " AND pre=" + pre;
		if (sifra!=null && sifra.trim().length()>0){
			sql = sql + " AND sifra=" + sifra;
		}
		if (mesec!=null && mesec.trim().length()>0){
			sql = sql + " AND mesec=" + mesec;
		}
		if (vrstaplate!=null && vrstaplate.trim().length()>0){
			sql = sql + " AND vrstaplate=" + vrstaplate;
		}
		if (brojobrade!=null && brojobrade.trim().length()>0){
			sql = sql + " AND brojobrade=" + brojobrade;
		}
		
		int ii=1;
        vrati = vrati + "{rows:[";
 		List<Kreditiarh> lista = jdbcTemplate.query(sql,new KreditiRowMapper());		
		for (Iterator<Kreditiarh> iter = lista.iterator(); iter.hasNext(); ) {
  		   if (ii>1){
			   vrati = vrati + ",";
		   }
       		vrati = vrati + "{ id:" + String.valueOf(ii) + ",data:[";
       		Kreditiarh kredd = iter.next();
       		vrati = vrati + "\"" + String.valueOf(kredd.getSifra()) + "\",";
        	vrati = vrati + "\"" + String.valueOf(kredd.getNazobs()) + "\",";
        	vrati = vrati + "\"" + String.valueOf(kredd.getDug()) + "\",";
        	vrati = vrati + "\"" + String.valueOf(kredd.getRata()) + "\"";
  			vrati = vrati + "]}";
			ii = ii + 1;
		}
		vrati = vrati + "]};";
 		return vrati;
	}

//------------------------------------------------------------------------------------
	@RequestMapping(value = "/proveriunos", method = RequestMethod.POST)
	@ResponseBody
	public String proveriUnos(@RequestParam HashMap<String, String> body ) {
		String sql="",juzer="",result="0";
		String mesec = body.get("mesec").trim(); 
		String godina = body.get("godina").trim(); 		
		String vrstaplate = body.get("vrstaplate").trim();
		String brojobrade = body.get("brojobrade").trim();
		String pre = body.get("pre").trim(); 		
		
		//uzimanje imena usera
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		juzer = auth.getName();

	    sql = "SELECT count(*) FROM binmesec WHERE juzer=? AND mesec=?" +
	    		" AND godina=? AND vrstaplate=? AND brojobrade=? AND pre=?";
		
	    int count = jdbcTemplate.queryForObject(sql, new Object[] { juzer,mesec,godina,vrstaplate,brojobrade,pre }, Integer.class);
		if (count > 0) {
			result = "1";
		}
		return "{\"rezultat\":\""+result+"\"}";
   }
//------------------------------------------------------------------------------------------------------------
	class UnosRowMapper implements RowMapper<Binmesec>{
	    @Override
	    public Binmesec mapRow(ResultSet rs, int rowNum) throws SQLException {
	    	Binmesec binmesec = new Binmesec();
	    	binmesec.setSifra(rs.getInt("sifra"));
	    	binmesec.setPrezime(rs.getString("prezime"));
	    	binmesec.setIme(rs.getString("ime"));
	    	binmesec.setMesec(rs.getInt("mesec"));
	    	binmesec.setBrojobrade(rs.getInt("brojobrade"));
	    	binmesec.setVrstaplate(rs.getInt("vrstaplate"));
	        return binmesec;
	    }
	}
	class KreditiRowMapper implements RowMapper<Kreditiarh>{
	    @Override
	    public Kreditiarh mapRow(ResultSet rs, int rowNum) throws SQLException {
	    	Kreditiarh kred = new Kreditiarh();
	    	kred.setSifra(rs.getInt("sifra"));
	    	kred.setNazobs(rs.getString("nazobs"));
	    	kred.setDug(rs.getDouble("dug"));
	    	kred.setRata(rs.getDouble("rata"));
	    	
	        return kred;
	    }
	}
}
