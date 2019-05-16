package com.controller;

import com.controller.SifpreduzController.MatRowMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.model.Maticnipodaci;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.sql.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequestMapping("/maticna")
public class MaticnaController {
	@Autowired
    private JdbcTemplate jdbcTemplate;	

//-------------------------------------------------------------------------
		//vraca sve slogove
		@RequestMapping(value = "/prikaz", method = RequestMethod.GET)
		@ResponseBody
		public String getAllMaticni(@RequestParam HashMap<String, String> body ) {
			String sql = "",prezime="",juzer="",pre="0";
			prezime = body.get("prezime").trim(); 
			pre = body.get("pre").trim();
			prezime = body.get("prezime").trim(); 
			
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	        juzer = auth.getName();			
			
	        if (prezime.trim().length()>0) {
	        	sql = "select * from maticnipodaci WHERE juzer=? AND pre=? AND prezime LIKE '%" + prezime +"%'";
	        }else {
	        	sql = "select * from maticnipodaci WHERE juzer=? AND pre=?";
	        }
			
			String vrati="";
			int ii=1;
			
	        vrati = vrati + "{rows:[";
	        
	 		List<Maticnipodaci> lista = jdbcTemplate.query(sql,new Object[] { juzer, pre },new MatRowMapper()); 	 		
			
	 		for (Iterator<Maticnipodaci> iter = lista.iterator(); iter.hasNext(); ) {
	  		   if (ii>1){
				   vrati = vrati + ",";
			   }
	       		vrati = vrati + "{ id:" + String.valueOf(ii) + ",data:[";
	       		Maticnipodaci mat = iter.next();
	       		vrati = vrati + "\"" + String.valueOf(mat.getSifra()) + "\",";
	        	vrati = vrati + "\"" + String.valueOf(mat.getIme()) + "\",";
	        	vrati = vrati + "\"" + String.valueOf(mat.getPrezime()) + "\",";
	           	vrati = vrati + "\"" + String.valueOf(mat.getAdresa()) + "\",";
	           	vrati = vrati + "\"" + String.valueOf(mat.getBrbod()) + "\"";
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
	        
			sql = "select * from maticnipodaci where juzer=? AND pre=? AND sifra=?";
	        
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
		@ResponseBody
		public String unosMaticni(@RequestParam HashMap<String, String> body ) {
			int ii = 0;
			String sql="",juzer="",pre="1";
			pre = body.get("pre").trim(); 
			
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	        juzer = auth.getName();	
		
	        sql = "INSERT INTO maticnipodaci(pre,sifra,prezime,ime,gs,ms,brbod,sindi,sifmz,sifkval,isplata," +
	        		"sifban,partija,matbr,privremeno,soli,adresa,prosek,sifben,nazivrm,br,datumod,datumdo," +
	        		"sifraopstine,deonicar,datzasnivanja,juzer,email)" +
	        		"VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				try
				{
					//ubacivanje podataka u mesecnu tabelu sati i bodova
					ii = jdbcTemplate.update(sql,pre,body.get("sifra").trim(),body.get("prezime").trim(),body.get("ime").trim(),body.get("gs").trim(),
							body.get("ms"),body.get("brbod"),body.get("sindi"),body.get("sifmz"),body.get("sifkval"),
							body.get("isplata").trim(),body.get("sifban").trim(),body.get("partija").trim(),body.get("matbr").trim(),body.get("privremeno").trim(),
							body.get("soli").trim(),body.get("adresa").trim(),body.get("prosek").trim(),body.get("sifben").trim(),
							body.get("nazivrm").trim(),body.get("br").trim(),body.get("datumod").trim(),body.get("datumdo").trim(),body.get("sifraopstine").trim(),
							body.get("deonicar").trim(),body.get("datzasnivanja").trim(),body.get("email").trim(),juzer);
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
					String sql="",juzer="",sifra="",pre="1";
					sifra = body.get("sifra");
					pre = body.get("pre"); 
					
					Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			        juzer = auth.getName();			
					
			        sql = "UPDATE maticnipodaci SET prezime=?,ime=?,gs=?,ms=?,brbod=?,sindi=?,sifmz=?,sifkval=?,isplata=?," +
			        		"sifban=?,partija=?,matbr=?,privremeno=?,soli=?,adresa=?,prosek=?,sifben=?," +
			        		"nazivrm=?,br=?,datumod=?,datumdo=?,sifraopstine=?,deonicar=?,datzasnivanja=?" +
			        		",email=?" +
			        		" WHERE sifra=" + sifra + " AND pre=" + pre + " AND  juzer='" + juzer + "'";
						try
						{
							//ubacivanje podataka u mesecnu tabelu sati i bodova
							ii = jdbcTemplate.update(sql,body.get("prezime").trim(),body.get("ime").trim(),body.get("gs").trim(),
									body.get("ms").trim(),body.get("brbod").trim(),body.get("sindi").trim(),body.get("sifmz").trim(),body.get("sifkval").trim(),
									body.get("isplata").trim(),body.get("sifban").trim(),body.get("partija").trim(),body.get("matbr").trim(),body.get("privremeno").trim(),
									body.get("soli").trim(),body.get("adresa").trim(),body.get("prosek").trim(),body.get("sifben").trim(),
									body.get("nazivrm").trim(),body.get("br").trim(),body.get("datumod").trim(),body.get("datumdo").trim(),body.get("sifraopstine").trim(),
									body.get("deonicar").trim(),body.get("datzasnivanja").trim(),body.get("email").trim());
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
			String sql="",juzer="",pre="1";
			String sifra = body.get("sifra").trim(); 
			pre = body.get("pre").trim(); 
			
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	        juzer = auth.getName();		
	        
	        sql = "DELETE FROM maticnipodaci WHERE pre=? AND sifra=? AND juzer=? ";
			ii = jdbcTemplate.update(sql,pre,sifra,juzer);		
			
			return String.valueOf(ii);
		}
//==============================================================================================	
		class MatRowMapper implements RowMapper<Maticnipodaci>{
			@Override
			public Maticnipodaci mapRow(ResultSet rs, int rowNum) throws SQLException {
				   Maticnipodaci mat = new Maticnipodaci();
				   mat.setSifra(rs.getInt("sifra"));
				   mat.setIme(rs.getString("ime"));
				   mat.setPrezime(rs.getString("prezime"));
				   mat.setAdresa(rs.getString("adresa"));
				   mat.setBrbod(rs.getDouble("brbod"));
				   return mat;
			}
		}
//--------------------------------------------------------------------------		
}
