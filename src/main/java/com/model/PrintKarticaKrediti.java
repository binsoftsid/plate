package com.model;
import java.sql.*;
import com.model.ClassOlaksice;

public class PrintKarticaKrediti {
	private Connection connection = null;
	private int mesec,vrstaplate,broj=99,pred;
	private String juzer="",godina="",iis="";
	private int sifra=0,odmeseca=0,domeseca=0,sifrakreditora=0;
	//pomocni iznosi doprinosa radnika
	private ClassOlaksice pclol;
	
	public PrintKarticaKrediti() {
	}
	public void obradiKarticu(Connection _connection,String _juzer,int _pred,String _godina,
			int _odmeseca,int _domeseca,int _sifra,int _sifrakreditora) {
		
		String strsql="",dam="",sqll="";
		int yy = 0;
		connection = _connection;
		juzer = _juzer.trim();
		pred = _pred;
		godina = _godina;
		odmeseca = _odmeseca;
		domeseca = _domeseca;
		sifra = _sifra;
		sifrakreditora = _sifrakreditora;
		
		strsql = "DROP TABLE IF EXISTS tmpkreditiarh ";
		izvrsi(strsql);
		strsql = "CREATE TEMPORARY TABLE tmpkreditiarh LIKE kreditiarh";
		izvrsi(strsql);
		if (sifrakreditora == 0) {
			strsql = "INSERT INTO tmpkreditiarh SELECT * FROM kreditiarh " + 
					" WHERE kreditiarh.godina=" + godina + " AND kreditiarh.juzer='" + juzer + 
					"' AND kreditiarh.sifra=" + sifra +
					" AND kreditiarh.mesec>=" + odmeseca + " AND kreditiarh.mesec<=" + domeseca;
		}else {
			strsql = "INSERT INTO tmpkreditiarh SELECT * FROM kreditiarh " + 
					" WHERE kreditiarh.godina=" + godina + " AND kreditiarh.juzer='" + juzer + 
					"' AND kreditiarh.sifra=" + sifra +
					" AND kreditiarh.mesec>=" + odmeseca + " AND kreditiarh.mesec<=" + domeseca + 
					" AND kreditiarh.sifobs=" + sifrakreditora;
		}
		izvrsi(strsql);
	
}
//------------------------------------------------------------------------------------------------------------------
	public void izvrsi(String sql) {
	      Statement statement = null;
		  try {
				statement = connection.createStatement();
				int result = statement.executeUpdate( sql );
	      }
	      catch ( SQLException sqlex ) {
	    	  sqlex.printStackTrace();
	      }
		  finally{
			if (statement != null){
				try{
					statement.close();
					statement = null;
				}catch (Exception e){
					e.printStackTrace();
				}}
		  }
	  }

}
