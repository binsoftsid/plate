package com.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.model.Preduzetnici;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.sql.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;


import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequestMapping("/matpreduz")
public class SifpreduzController {
	@Autowired
    private JdbcTemplate jdbcTemplate;	

	//-------------------------------------------------------------------------
			//vraca sve slogove
			@RequestMapping(value = "/prikaz", method = RequestMethod.GET)
			@ResponseBody
			public String getAllMaticni(@RequestParam HashMap<String, String> body ) {
				String juzer="",pre="0";
				pre = body.get("pre").trim(); 
				
				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		        juzer = auth.getName();			
				
				String vrati="";
		        vrati = vrati + "{rows:[";
		        List<Preduzetnici> lista;
				int ii=1;

				lista = jdbcTemplate.query("select * from sifpreduz WHERE juzer=? AND pre=?",
													new Object[] { juzer, pre },new MatRowMapper());    				
		 		
				for (Iterator<Preduzetnici> iter = lista.iterator(); iter.hasNext(); ) {
		  		   if (ii>1){
					   vrati = vrati + ",";
				   }
		       		vrati = vrati + "{ id:" + String.valueOf(ii) + ",data:[";
		       		Preduzetnici mat = iter.next();
		       		vrati = vrati + "\"" + String.valueOf(mat.getSifra()) + "\",";
		       		vrati = vrati + "\"" + String.valueOf(mat.getNazivfirme()) + "\",";
		        	vrati = vrati + "\"" + String.valueOf(mat.getIme()) + "\",";
		        	vrati = vrati + "\"" + String.valueOf(mat.getPrezime()) + "\",";
		           	vrati = vrati + "\"" + String.valueOf(mat.getAdresa()) + "\"";
					vrati = vrati + "]}";
					ii = ii + 1;
				}
				vrati = vrati + "]};";
		 		return vrati;
			}
	//---------------------------vraca izabranog radnika binmesec-------------------------------------------
			@RequestMapping(value = "/prikazjedan", method = RequestMethod.GET, produces="application/json")
			@ResponseBody
			public String getSingleMaticni(@RequestParam HashMap<String, String> body ) {
				String sifra="",sql="",juzer="",pre="1";
				sifra = body.get("sifra").trim(); 
				pre = body.get("pre").trim(); 
				
				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		        juzer = auth.getName();
		        
				sql = "select * from sifpreduz where juzer=? AND pre=? AND sifra=?";
		        
		        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql,juzer,pre,sifra);
		        String json = "";
		        
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
			public String unosMaticni(@RequestParam HashMap<String, String> body ) {
				int ii = 0;
				String sql="",juzer="",pre="1";
				pre = body.get("pre"); 
				
				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		        juzer = auth.getName();	
			
		        sql = "INSERT INTO sifpreduz(pre,sifra,prezime,ime,nazivfirme,pbroj,tekuci,telefon,email," +
		        		"jmbg,adresa,mesto,sifraopstine,pib,matbr,juzer)" +
		        		"VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
					try
					{
						//ubacivanje podataka u mesecnu tabelu sati i bodova
						ii = jdbcTemplate.update(sql,pre,body.get("sifra").trim(),body.get("prezime").trim(),body.get("ime").trim(),
								body.get("nazivfirme").trim(),body.get("pbroj").trim(),body.get("tekuci").trim(),
								body.get("telefon").trim(),body.get("email").trim(),body.get("jmbg").trim(),body.get("adresa").trim(),
								body.get("mesto").trim(),body.get("sifraopstine").trim(),body.get("pib").trim(),body.get("matbr").trim(),juzer);
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
			public String izmenaMaticni(@RequestParam HashMap<String, String> body ) {
						int ii = 0;
						String sql="",juzer="",sifra="",pre="1";
						sifra = body.get("sifra").trim();
						pre = body.get("pre").trim(); 
						
						Authentication auth = SecurityContextHolder.getContext().getAuthentication();
				        juzer = auth.getName();			
						
				        sql = "UPDATE sifpreduz SET prezime=?,ime=?,nazivfirme=?,adresa=?,mesto=?,pbroj=?,tekuci=?,telefon=?,email=?," +
				        		"pib=?,matbr=?,jmbg=?" +
				        		" WHERE sifra=? AND pre=? AND juzer=?";
							try
							{
								//ubacivanje podataka u mesecnu tabelu sati i bodova
								ii = jdbcTemplate.update(sql,body.get("prezime").trim(),body.get("ime").trim(),body.get("nazivfirme").trim(),
										body.get("adresa").trim(),body.get("mesto").trim(),body.get("pbroj").trim(),body.get("tekuci").trim(),
										body.get("telefon").trim(),body.get("email").trim(),body.get("pib").trim(),body.get("matbr").trim(),
										body.get("jmbg").trim(),sifra,pre,juzer);
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
			public String deleteMat(@RequestParam HashMap<String, String> body ) {
				int ii = 0;
				String sql="",juzer="",pre="1";
				String sifra = body.get("sifra").trim(); 
				pre = body.get("pre").trim(); 
				
				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		        juzer = auth.getName();		
		        
		        sql = "DELETE FROM sifpreduz WHERE pre=? AND sifra=? AND juzer=? ";
				ii = jdbcTemplate.update(sql,pre,sifra,juzer);		
				
				return String.valueOf(ii);
			}
	//==============================================================================================	
			class MatRowMapper implements RowMapper<Preduzetnici>{
				@Override
				public Preduzetnici mapRow(ResultSet rs, int rowNum) throws SQLException {
					   Preduzetnici mat = new Preduzetnici();
					   mat.setSifra(rs.getInt("sifra"));
					   mat.setNazivfirme(rs.getString("nazivfirme"));
					   mat.setIme(rs.getString("ime"));
					   mat.setPrezime(rs.getString("prezime"));
					   mat.setAdresa(rs.getString("adresa"));
					  
					   return mat;
				}
			}
	//--------------------------------------------------------------------------		
	}

