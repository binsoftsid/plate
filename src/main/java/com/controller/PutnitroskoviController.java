package com.controller;

import com.model.Putnitroskovi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.annotation.*;
import java.util.*;

import javax.sql.DataSource;

import java.sql.*;
import java.text.DecimalFormat;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/putnitroskovi")
public class PutnitroskoviController {
	@Autowired
    private JdbcTemplate jdbcTemplate;	
	
//-------------------------------------------------------------------------
		//vraca sve slogove
		@RequestMapping(value = "/prikaz", method = RequestMethod.GET)
		@ResponseBody
		public String getAllSind(@RequestParam HashMap<String, String> body ) {
			String sql ="",juzer="",pre="1",godina="";
			pre = body.get("pre").trim(); 
			godina = body.get("godina").trim(); 
		
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	        juzer = auth.getName();			
			
			sql = "select * from putnitroskovi WHERE juzer=? AND pre=? AND godina=? ORDER BY pre,godina,mesec,brisplate,sifra";
			String vrati="";
			int ii=1;
			
	        vrati = vrati + "{rows:[";
	 		
	        List<Putnitroskovi> lista = jdbcTemplate.query(sql,new Object[] { juzer,pre,godina },new PutntrRowMaper());    				
	 		
			for (Iterator<Putnitroskovi> iter = lista.iterator(); iter.hasNext(); ) {
	  		   if (ii>1){
				   vrati = vrati + ",";
			   }
	       		vrati = vrati + "{ id:" + String.valueOf(ii) + ",data:[";
	       		Putnitroskovi mat = iter.next();
	       		
	       		vrati = vrati + "\"" + String.valueOf(mat.getMesec()) + "\",";
	       		vrati = vrati + "\"" + String.valueOf(mat.getBrisplate()) + "\",";
	        	vrati = vrati + "\"" + String.valueOf(mat.getSifra()) + "\",";
	        	vrati = vrati + "\"" + String.valueOf(mat.getDatum()) + "\",";
	        	vrati = vrati + "\"" + String.valueOf(mat.getNeto()) + "\",";
	        	vrati = vrati + "\"" + String.valueOf(mat.getNeoporiznos()) + "\",";
	        	vrati = vrati + "\"" + String.valueOf(mat.getPorez()) + "\",";
	        	vrati = vrati + "\"" + String.valueOf(mat.getBruto()) + "\",";
	        	vrati = vrati + "\"" + String.valueOf(mat.getIznosporeza()) + "\",";
	        	vrati = vrati + "\"" + String.valueOf(mat.getUkupaniznos()) + "\"";
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
			String sifra="",sql="",juzer="",pre="1",brisplate="",mesec="",godina="0";
			sifra = body.get("sifra").trim(); 
			brisplate = body.get("brisplate").trim(); 
			pre = body.get("pre").trim(); 
			mesec = body.get("mesec").trim(); 
			godina = body.get("godina").trim(); 
			
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	        juzer = auth.getName();
	        
	        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
	        
			sql = "select * from putnitroskovi where juzer=? AND pre=? AND godina=?";
			
			if (sifra!=null && sifra.trim().length()>0 && mesec!=null && mesec.trim().length()>0
					&& brisplate!=null && brisplate.trim().length()>0){
				sql = sql + " AND mesec=? AND brisplate=? AND sifra=?";
				list = jdbcTemplate.queryForList(sql,juzer,pre,godina,mesec,brisplate,sifra);
			}else {
				Map mmap = new HashMap<String, Object>();
				mmap.put("0", 0);
				list.set(0, mmap);
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
			
	        sql = "INSERT INTO putnitroskovi(pre,mesec,brisplate,sifra,datum,neto,neoporiznos,porez," +
	        		"bruto,iznosporeza,ukupaniznos,juzer,godina) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)";
				try
				{
					//ubacivanje podataka u tabelu doprinosi
					ii = jdbcTemplate.update(sql,pre,body.get("mesec").trim(),body.get("brisplate").trim(),body.get("sifra").trim(),
							body.get("datum").trim(),body.get("neto").trim(),body.get("neoporiznos").trim(),body.get("porez").trim(),
							body.get("bruto").trim(),body.get("iznosporeza").trim(),body.get("ukupaniznos").trim(),juzer,body.get("godina").trim());
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
					String sql="",juzer="",pre="1",mesec="1",sifra="",brisplate="";
					pre = body.get("pre").trim(); 
					mesec = body.get("mesec").trim(); 
					sifra = body.get("sifra").trim(); 
					brisplate = body.get("brisplate").trim(); 
					
					Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			        juzer = auth.getName();			
					
			        sql = "UPDATE putnitroskovi SET datum=?,neto=?,neoporiznos=?,porez=?,bruto=?," +
			        		"iznosporeza=?,ukupaniznos=?" + 
			        		" WHERE mesec=? AND brisplate=? AND sifra=? AND juzer=? AND pre=? AND godina=?";
						try
						{
							//izmena podataka u tabelu doprinosi
							ii = jdbcTemplate.update(sql,body.get("datum").trim(),body.get("neto").trim(),body.get("neoporiznos").trim(),
								body.get("porez").trim(),body.get("bruto").trim(),body.get("iznosporeza").trim(),body.get("ukupaniznos").trim(),
								mesec,brisplate,sifra,juzer,pre,body.get("godina").trim());
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
			String sql="",juzer="",pre="1",mesec="1",sifra="",brisplate="",godina="";
			pre = body.get("pre").trim(); 
			mesec = body.get("mesec").trim(); 
			sifra = body.get("sifra").trim(); 
			brisplate = body.get("brisplate").trim(); 
			godina = body.get("godina").trim(); 
			
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	        juzer = auth.getName();		
	        
	        sql = "DELETE FROM putnitroskovi WHERE sifra=? AND juzer=? AND pre=? AND mesec=? AND brisplate=? AND godina=?";
			ii = jdbcTemplate.update(sql,sifra,juzer,pre,mesec,brisplate,godina);		
			
			return String.valueOf(ii);
		}
//--------------------------------------------------------------------------------------
		@RequestMapping(value = "/parametri", method = RequestMethod.GET, produces="application/json")
		@ResponseBody
		public String getParametri(@RequestParam HashMap<String, String> body ) {
			String pattern = "##########.00";
			DecimalFormat myFormatter = new DecimalFormat(pattern);

			String neto = body.get("neto"); 
			String jsonstring = ""; 
			double nett = Double.parseDouble(neto);
			double neopizn = 0.0,porr=0.0,koeficijent=0.0,brutt=0.0,iznporr=0.0,ukuiznn=0.0;

		    List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		    Map<String, Object> map = new HashMap<String, Object>();
			
			Connection conn = null;
			try{
				DataSource datasource = jdbcTemplate.getDataSource();
				conn = datasource.getConnection();	
			}catch(SQLException ee){
				ee.printStackTrace();
			}		
			  Statement statement = null;
		      try {
		         statement = conn.createStatement();
		               String query = "select * from paramputni where rbr=1";

				         ResultSet rs = statement.executeQuery( query );
				         if(rs.next()){
				        	 neopizn = rs.getDouble("neoporeziviiznos"); 
				        	 porr = rs.getDouble("porez"); 
							
				        	 koeficijent = (100-porr)/100;
				        	 
				 			if ((nett-neopizn)>0)
							{
								brutt = (nett-neopizn)/koeficijent;
								iznporr = brutt * (porr/100);
								ukuiznn = nett + iznporr;
							}else{
								ukuiznn = nett;
							}
				 			
				 			map.put("neoporiznos",myFormatter.format(neopizn));
				 			map.put("porez",myFormatter.format(porr));
				 			map.put("iznosporeza",myFormatter.format(iznporr));
				 			map.put("bruto",myFormatter.format(brutt));
				 			map.put("ukupaniznos",myFormatter.format(ukuiznn));
						       
				 			list.add(map);				 			
				 			
							rs.close();
						 }
			  }     
		      catch ( SQLException sqlex ) {
		    	  System.out.println("Greska u trazenju preduzeca:"+sqlex);
		      }
				//.....................................................................................
				finally{
					if (statement != null){
						try{
							statement.close();
							statement = null;
						}catch (Exception e){
							System.out.println("Nije uspeo da zatvori statement");
						}}
				}
				//.....................................................................................
		        final ObjectMapper mapper = new ObjectMapper();
		        try {
		        	jsonstring = mapper.writeValueAsString(list);
		        }catch(JsonProcessingException ee) {
		        	ee.printStackTrace();
		        }        
			    return jsonstring;
		}
//==============================================================================================	
		class PutntrRowMaper implements RowMapper<Putnitroskovi>{
			@Override
			public Putnitroskovi mapRow(ResultSet rs, int rowNum) throws SQLException {
				Putnitroskovi mat = new Putnitroskovi();
				   mat.setMesec(rs.getInt("mesec"));
				   mat.setBrisplate(rs.getInt("brisplate"));
				   mat.setSifra(rs.getInt("sifra"));
				   mat.setDatum(rs.getDate("datum"));
				   mat.setNeto(rs.getDouble("neto"));
				   mat.setNeoporiznos(rs.getDouble("neoporiznos"));
				   mat.setPorez(rs.getDouble("porez"));
				   mat.setBruto(rs.getDouble("bruto"));
				   mat.setIznosporeza(rs.getDouble("iznosporeza"));
				   mat.setUkupaniznos(rs.getDouble("ukupaniznos"));
				   return mat;
			}
		}
//--------------------------------------------------------------------------		
}
