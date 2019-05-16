package com.model;
import java.sql.*;

public class PrintListaPutni {
	private Connection connection;
	private int mesec,vrstaplate,broj=99,pred;
	private String juzer="",godina="",iis="";
	
	public PrintListaPutni() {
	}
	public void obradiListu(Connection _connection,String _juzer,int _pred,String _godina,
			int _mesec,int _brojobrade) {
		connection = _connection;
		juzer = _juzer.trim();
		pred = _pred;
		godina = _godina;
		mesec = _mesec;
		
		broj = _brojobrade;

		String strsql = "DROP TABLE IF EXISTS tmpputntr ";
		izvrsi(strsql);
		
		strsql = "CREATE temporary TABLE  tmpputntr LIKE putnitroskovi ";
		izvrsi(strsql);

		strsql = "INSERT INTO tmpputntr select * FROM putnitroskovi WHERE pre=" + pred +
			" AND mesec=" + mesec + " AND brisplate=" + broj;
		izvrsi(strsql);

 		strsql = "ALTER TABLE tmpputntr ADD ime varchar(35)not null default ' '";
		izvrsi(strsql);

		strsql = "Update tmpputntr,maticnipodaci Set tmpputntr.ime = "+
			"CONCAT(TRIM(maticnipodaci.ime),' ',TRIM(maticnipodaci.prezime)) " +
			"WHERE tmpputntr.sifra = maticnipodaci.sifra AND tmpputntr.pre=maticnipodaci.pre";
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
