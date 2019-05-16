package com.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import javax.sql.DataSource;
import java.sql.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.dao.DataAccessException;
import java.lang.RuntimeException;
import com.model.ClassObrada;

@RestController
@RequestMapping("/obrada")
public class ObradaController {
	@Autowired
    private JdbcTemplate jdbcTemplate;	
	
	private Connection connection;
	private ClassObrada classObrada;
	private String juzer="";

	@RequestMapping(value = "/unossati", method = RequestMethod.POST)
	@ResponseBody
	public String createSati(@RequestParam HashMap<String, String> body ) {
		int ii = 0;
		//parametar satiucinak predstavlja pokazatelj da se radi sa obracunom sati a ne ucinkom
		String sql="",sql1="",sqldel="",juzer="",satiucinak="2";
		int kojaobrada=1;
		String pre = body.get("pre").trim(); 
		String godina = body.get("godina").trim(); 
		String sifra = body.get("sifra").trim(); 
		String mesec = body.get("mesec").trim(); 
		String vrstaplate = body.get("vrstaplate").trim(); 
		String brojobrade = body.get("brojobrade").trim(); 
		String krediti = body.get("krediti").trim(); 
		String ime="",prezime="";
		
		//parametar koji odredjuje da li je unos podataka ili izmena (1,2)
		kojaobrada = Integer.parseInt(body.get("kojaobrada").trim());
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        juzer = auth.getName();			
        
        try{
			DataSource datasource = jdbcTemplate.getDataSource();
			connection = datasource.getConnection();	
		}catch(SQLException ee){
			ee.printStackTrace();
		}	
        
        classObrada = new ClassObrada(connection,godina,pre);
		
        sql = "INSERT INTO binmesec(pre,mesec,brojobrade,sifra,vrstaplate,prezime,ime,c1,c2,c3,c4,c5,c6,c7,c8,c9,c10,c12," +
        		"c13,c14,c15,c16,c18,c19,c21,b1,b11,b12,b13,b14,b19,b20,b21,kalbrdana,procangazovanja,vrednostboda," +
        		"topliobrok,krediti,porolak,procenat,fondsatiumesecu,fondsatizaobracun,satiucinak,minosn,godina,juzer)" +
        		"VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			try
			{
				//ovo je slucaj izmene podataka
				if (kojaobrada == 2) {
						classObrada.vratiObradu(Integer.parseInt(brojobrade),Integer.parseInt(mesec),Integer.parseInt(sifra),
								Integer.parseInt(vrstaplate),Integer.parseInt(krediti));					
						//brise podatke iz tabele sati (binmesec)
						sqldel = "DELETE FROM binmesec WHERE pre=? AND sifra=? AND vrstaplate=? AND godina=?" +
				        		" AND mesec=? AND brojobrade=? AND juzer=?";
						ii = jdbcTemplate.update(sqldel,pre,sifra,vrstaplate,godina,mesec,brojobrade,juzer);				
				}
				//ubacivanje podataka u mesecnu tabelu sati i bodova
				ii = jdbcTemplate.update(sql,pre,mesec,brojobrade,sifra,vrstaplate,ime,prezime,
						body.get("cas1").trim(),body.get("cas2").trim(),body.get("cas3").trim(),body.get("cas4").trim(),body.get("cas5").trim(),
						body.get("cas6").trim(),body.get("cas7").trim(),body.get("cas8").trim(),body.get("cas9").trim(),body.get("cas10").trim(),
						body.get("cas12").trim(),body.get("cas13").trim(),body.get("cas14").trim(),body.get("cas15").trim(),body.get("cas16").trim(),
						body.get("cas18").trim(),body.get("cas19").trim(),body.get("cas21").trim(),
						body.get("bod1").trim(),body.get("bod11").trim(),body.get("bod12").trim(),body.get("bod13").trim(),body.get("bod14").trim(),
						body.get("bod19").trim(),body.get("bod20").trim(),body.get("bod21").trim(),body.get("cas22").trim(),body.get("cas23").trim(),
						body.get("vrednostboda").trim(),body.get("topliobrok").trim(),krediti,body.get("porolak").trim(),
						body.get("procenat").trim(),body.get("fondsatimesec").trim(),body.get("fondsatiobracun").trim(),satiucinak,
						body.get("minosn").trim(),godina,juzer);
				
				sql1 = "UPDATE binmesec,maticnipodaci SET binmesec.ime=maticnipodaci.ime,binmesec.prezime=maticnipodaci.prezime" +
						" WHERE binmesec.sifra=maticnipodaci.sifra AND binmesec.juzer=maticnipodaci.juzer AND binmesec.pre=maticnipodaci.pre" +
						" AND binmesec.mesec=" + mesec + " AND binmesec.pre=" + pre + " AND binmesec.juzer='" + juzer + 
						"' AND maticnipodaci.sifra=" + sifra +
						" AND binmesec.godina=" + godina + " AND maticnipodaci.juzer='" + juzer + "' AND maticnipodaci.pre=" + pre;
				 
				//ubacivanje imena i prezimena radnika
				ii = jdbcTemplate.update(sql1);
				
				//obrada zarade radnika i kreiranje unosa u tabelu ldmes
				classObrada.Obradi(Integer.parseInt(brojobrade),Integer.parseInt(mesec),Integer.parseInt(sifra),
		        		Integer.parseInt(vrstaplate),godina);
			}
			catch (DataAccessException e)
			{
				throw new RuntimeException(e);
			}
	        try{
				connection.close();	
			}catch(SQLException ee){
				ee.printStackTrace();
			}	
		
		return String.valueOf(ii);
	}
	@RequestMapping(value = "/deletesati", method = RequestMethod.POST)
	@ResponseBody
	public String deleteSati(@RequestParam HashMap<String, String> body ) {
		int ii = 0;
		String sql="";
		String pre = body.get("pre"); 
		String godina=body.get("godina"); 
		String sifra = body.get("sifra"); 
		String mesec = body.get("mesec"); 
		String vrstaplate = body.get("vrstaplate"); 
		String brojobrade = body.get("brojobrade"); 
		String krediti = body.get("krediti"); 	
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        juzer = auth.getName();		
        
        try{
			DataSource datasource = jdbcTemplate.getDataSource();
			connection = datasource.getConnection();	
		}catch(SQLException ee){
			ee.printStackTrace();
		}	        
        classObrada = new ClassObrada(connection,godina,pre);
        
        classObrada.vratiObradu(Integer.parseInt(brojobrade),Integer.parseInt(mesec),Integer.parseInt(sifra),
        		Integer.parseInt(vrstaplate),Integer.parseInt(krediti));
        
        sql = "DELETE FROM binmesec WHERE pre=? AND sifra=? AND vrstaplate=? AND godina=?" +
        		" AND mesec=? AND brojobrade=? AND juzer=?";
		ii = jdbcTemplate.update(sql,pre,sifra,vrstaplate,godina,mesec,brojobrade,juzer);		
		
		return String.valueOf(ii);
	}

}
