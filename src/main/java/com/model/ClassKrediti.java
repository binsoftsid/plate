package com.model;

import java.sql.*;

public class ClassKrediti {
	private Connection connection = null;
	private int intdatod=0,intdatdo=0,broj=0;
	private double koeficijent=1,topliobrok=0;
	public static double ukuizn1=0,ukuizn2=0,ukuizn3=0,ukuizn4=0,ukuizn5=0,ukuizn6=0,ukuizn7=0;
	public int mesec=0,brojobrade=99,sifra=0,vrstaplate=1;
	//promenljive za obradu
	private int siff,siff1,red=0,kk,netoc,gs,ms,redx,grupa,siframz,sifpros,sifpros1;
	private int sin,mesecaz,godinestaza=0,mesecistaza=0;
	private int fondsatizaobr=0,fondsatiumesecu=0;
	private String pred="";
	//pomocni nizovi za koeficijente iz tabele "parametriobracuna"
	private int[] minu =  new int[22];		/* koef. za minuli rad sta ulazi */
	private double[] ldkoef = new double[22];//koef. za uvecanje-smanjenje iznosa
	private double koefminulirad=0,procenatplate=0,poreskaolaksica=0;
	private double stopasamodoprinosa=0,koefsindikat=0,koefsolidarnost=0;
	private int clansindikata=0,solidarnost=1,krediti=0;
	private String godina="",juzer="";

 public ClassKrediti(Connection _connection,String _pre,String _juzer,String _godina) {
		connection = _connection;
		pred = _pre;
		juzer = _juzer;
	
		//ovo zbog kredita polje "dam"(mm-gggg)
		godina = _godina;
}
 //-----------------------------------------------------------------------------------------------------
	public void izvrsi(String sql) {
      Statement statement = null;
	  try {
			statement = connection.createStatement();
			int result = statement.executeUpdate( sql );
      }
      catch ( SQLException sqlex ) {
      }
	  finally{
		if (statement != null){
			try{
				statement.close();
				statement = null;
			}catch (Exception e){
			}}
	  }
  }
 //-----------------------------------------------------------------------------------------------------
   public void obradiKredite(int _brojobrade,int _mesec,int _sifra,int _vrstaplate) {
		String strmesec="0",strdam="";
		mesec=_mesec;
		brojobrade=_brojobrade;
		sifra=_sifra;
		vrstaplate=_vrstaplate;
		
 		//prvo brise stavke kredita za taj mesec ako postoji
		String sql = "DELETE FROM kreditiarh WHERE " +
			" pre=" + pred + " AND sifra=" + sifra +
			" AND mesec=" + mesec + 
			" AND kreditiarh.juzer='" + juzer + "'" +
			" AND godina=" + godina + 
			" AND brojobrade=" + brojobrade;
		
		sql = "DROP TABLE IF EXISTS tmpkrediti";
		izvrsi(sql);
		//kreiranje tabele tmpkrediti kao kreditiarh bez indexa
		sql = "CREATE TEMPORARY TABLE tmpkrediti SELECT * FROM kreditiarh";                                       
		izvrsi(sql);
		sql = "DELETE FROM tmpkrediti";                                       
		izvrsi(sql);

 		sql = "INSERT INTO tmpkrediti(pre,sifra,sifobs,partija,nazobs,dug,rata," +
			"dam,naobs,rbr,vrstaplate,juzer,godina) SELECT pre,sifra,sifobs,partija,nazobs,dug," +
			"rata,dam,naobs,rbr,vrstaplate,juzer,godina FROM krediti WHERE " +  
			" krediti.pre=" + pred + " AND krediti.sifra=" + sifra +
			" AND krediti.dug>0 AND krediti.naobs=1 AND krediti.juzer='" + juzer + "'" +
			" AND krediti.godina=" + godina;
		izvrsi(sql);
 		sql = "INSERT INTO tmpkrediti(pre,sifra,sifobs,partija,nazobs,dug,rata," +
			"dam,naobs,rbr,vrstaplate,juzer,godina) SELECT pre,sifra,sifobs,partija,nazobs,dug," +
			"rata,dam,naobs,rbr,vrstaplate,juzer,godina FROM krediti WHERE " +  
			" krediti.pre=" + pred + " AND krediti.sifra=" + sifra +
			" AND krediti.dug=0 AND krediti.naobs=2 AND krediti.juzer='" + juzer + "'" + 
			" AND krediti.godina=" + godina;
		izvrsi(sql);

 		sql = "UPDATE tmpkrediti SET mesec=" + mesec + ",brojobrade=" + brojobrade + 
			",vrstaplate=" + vrstaplate;
		izvrsi(sql);

		if (mesec<10)
		{
		   strmesec = "0" + String.valueOf(mesec);
		}else{
		   strmesec = String.valueOf(mesec);
		}
 		strdam = strmesec + "-" + godina;

		sql = "UPDATE tmpkrediti SET mesec=" + mesec +
			",dam='" + strdam + "'";  
		izvrsi(sql);
		
		sql = "UPDATE tmpkrediti SET rata=dug WHERE rata>dug and naobs=1";  
		izvrsi(sql);

		sql = "UPDATE tmpkrediti SET dug=dug-rata WHERE naobs=1";  
		izvrsi(sql);
		
		
		sql = "UPDATE tmpkrediti SET dug=ROUND(dug,2),rata=ROUND(rata,2)";  
		izvrsi(sql);

		sql = "INSERT INTO kreditiarh SELECT * FROM tmpkrediti";
		izvrsi(sql);

 		sql = "UPDATE krediti SET dug=dug-rata WHERE " +
			" pre=" + pred + " AND sifra=" + sifra +
			" AND dug>0 AND naobs=1 AND krediti.juzer='" + juzer + "'" + 
			" AND krediti.godina=" + godina;
		izvrsi(sql);
 		sql = "UPDATE krediti SET dug=0,rata=0 WHERE " +
			" pre=" + pred + " AND sifra=" + sifra +
			" AND (dug<0 OR dug=0) AND naobs=1 AND krediti.juzer='" + juzer +"'" + 
			" AND krediti.godina=" + godina;
		izvrsi(sql);

		sql = "DROP TABLE IF EXISTS tmpkrediti";
		izvrsi(sql);
  }
 //-----------------------------------------------------------------------------------------------------
   public void vratiKredite(int _brojobrade,int _mesec,int _sifra,int _vrstaplate) {

		izlistajKredite(_brojobrade,_mesec,_sifra,_vrstaplate);

		String sql = "DELETE FROM kreditiarh WHERE " +
			" pre=" + pred + " AND sifra=" + _sifra +
			" AND mesec= + " + _mesec + 
			" AND vrstaplate=" + _vrstaplate +
			" AND brojobrade=" + _brojobrade +
			" AND kreditiarh.juzer='" + juzer + "'" +
			" AND godina=" + godina;
		izvrsi(sql);
  }
  //-----------------------------------------------------------------------------------
   public void izlistajKredite(int _brojobrade,int _mesec,int _sifra,int _vrstaplate) {
	  double dug = 0.0,rata=0.0;
	  String partija="",sql="",query="";
	  int sifobs=0;
	  Statement statement = null;
      try {
         statement = connection.createStatement();
         
                query = "SELECT * FROM kreditiarh " +
					" WHERE sifra = " + _sifra + " AND pre=" + pred +
					" AND mesec=" + _mesec + " AND brojobrade=" + _brojobrade +
					" AND vrstaplate=" + _vrstaplate + " AND kreditiarh.juzer='" + juzer +"'" +
					" AND godina=" + godina;

		        ResultSet rs = statement.executeQuery( query );
				while(rs.next()){
					rata = rs.getDouble("rata");
					sifobs = rs.getInt("sifobs");
					partija = rs.getString("partija");

					sql = "UPDATE krediti set dug=dug+" + rata + ",rata=" + rata +
						" WHERE sifra = " + _sifra + " AND pre=" + pred +
						" AND sifobs=" + sifobs + " AND partija='" + partija + "'" +
						" AND naobs=1 AND krediti.juzer='" + juzer +"'" +
						" AND godina=" + godina;
					izvrsi(sql);

				}

      }
      catch ( SQLException sqlex ) {
		//JOptionPane.showMessageDialog(null, "Greska u izlistajKredite:"+sqlex);
      }
		//.....................................................................................
		finally{
			if (statement != null){
				try{
					statement.close();
					statement = null;
				}catch (Exception e){
					//JOptionPane.showMessageDialog(null, "Nije uspeo da zatvori statement");
				}}
		}
		//.....................................................................................
  }
  //-----------------------------------------------------------------------------------
   public double uzmiKrediteUkupno(int _brojobrade,int _mesec,int _sifra,int _vrstaplate) {
	  double ukukred = 0;
	  Statement statement = null;
      try {
         statement = connection.createStatement();
         
                String query = "SELECT SUM(rata) FROM kreditiarh " +
					" WHERE sifra = " + _sifra + " AND pre=" + pred +
					" AND mesec=" + _mesec + " AND brojobrade=" + _brojobrade +
					" AND kreditiarh.juzer='" + juzer +"'" +
					" AND godina=" + godina +
					" GROUP BY pre,sifra,mesec,brojobrade";
		        ResultSet rs = statement.executeQuery( query );
				if(rs.next()){
					ukukred = rs.getDouble(1);
				}else{
					ukukred = 0;
				}

      }
      catch ( SQLException sqlex ) {
		//JOptionPane.showMessageDialog(null, "Greska u uzmiKrediteUkupno:"+sqlex);
      }
		//.....................................................................................
		finally{
			if (statement != null){
				try{
					statement.close();
					statement = null;
				}catch (Exception e){
					//JOptionPane.showMessageDialog(null, "Nije uspeo da zatvori statement");
				}}
		}
		//.....................................................................................
		return ukukred;
  }
   
//===================================================================================== 
	
	
}
