package com.controller;

import com.model.Obrade;
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
@RequestMapping("/pregledobrada")
public class PregledobradaController {
	@Autowired
    private JdbcTemplate jdbcTemplate;	

	//-------------------------------------------------------------------------
			//vraca sve slogove
			@RequestMapping(value = "/prikaz", method = RequestMethod.GET)
			@ResponseBody
			public String getAllPO(@RequestParam HashMap<String, String> body ) {
				String sql ="",juzer="";
				String pre = body.get("pre"); 
				String godina = body.get("godina"); 
				
				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		        juzer = auth.getName();			
				
				String vrati="";
				
				int ii=1;
				try
				{
					sql = "drop table IF EXISTS tmpobrade";
					ii = jdbcTemplate.update(sql);
					
					sql = "CREATE TEMPORARY TABLE tmpobrade select DISTINCT mesec,brojobrade,vrstaplate FROM ldmes" +
							" WHERE ldmes.juzer=? AND ldmes.pre=? AND ldmes.godina=?";
					ii = jdbcTemplate.update(sql,juzer,pre,godina);
				
					sql = "ALTER TABLE tmpobrade ADD strvrsta varchar(35) not null default ''";
					ii = jdbcTemplate.update(sql);
					
					sql = "update tmpobrade,vrsteplata set tmpobrade.strvrsta=vrsteplata.naziv" +
							   " where tmpobrade.vrstaplate=vrsteplata.sifra";
					ii = jdbcTemplate.update(sql);
				}
				catch (DataAccessException e)
				{
					throw new RuntimeException(e);
				}
				
				sql = "select mesec,brojobrade,strvrsta from tmpobrade order by mesec";
		        vrati = vrati + "{rows:[";
		 		List<Obrade> lista = jdbcTemplate.query(sql,new PORowMapper());	
		 		
		 		ii=1;
				for (Iterator<Obrade> iter = lista.iterator(); iter.hasNext(); ) {
		  		   if (ii>1){
					   vrati = vrati + ",";
				   }
		       		vrati = vrati + "{ id:" + String.valueOf(ii) + ",data:[";
		       		Obrade mat = iter.next();
		       		vrati = vrati + "\"" + String.valueOf(mat.getMesec()) + "\",";
		        	vrati = vrati + "\"" + String.valueOf(mat.getBrojobrade()) + "\",";
		        	vrati = vrati + "\"" + String.valueOf(mat.getStrvrstaplate()) + "\",";
					vrati = vrati + "]}";
					ii = ii + 1;
				}
				vrati = vrati + "]};";
		 		return vrati;
		 		
		 		
			}

			//==============================================================================================	
			class PORowMapper implements RowMapper<Obrade>{
				@Override
				public Obrade mapRow(ResultSet rs, int rowNum) throws SQLException {
					   Obrade mat = new Obrade();
					   mat.setMesec(rs.getInt("mesec"));
					   mat.setBrojobrade(rs.getString("brojobrade"));
					   mat.setStrvrstaplate(rs.getString("strvrsta"));
					   return mat;
				}
			}
	//--------------------------------------------------------------------------		
	}

