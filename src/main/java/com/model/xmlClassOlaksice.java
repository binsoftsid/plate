package com.model;
import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.Set.*;

public class xmlClassOlaksice {
	private Connection connection = null;
	public String mesec,sql="",pre="1";
	private int intdatod=0,intdatdo=0,broj=0;
	private double koeficijent=1;
	public static double ukuizn1=0,ukuizn2=0,ukuizn3=0,ukuizn4=0,ukuizn5=0,ukuizn6=0,ukuizn7=0;
	
	public xmlClassOlaksice(String _pre) {
		 ukuizn1=0;
		 ukuizn2=0;
		 ukuizn3=0;
		 ukuizn4=0;
		 ukuizn5=0;
		 ukuizn6=0;
		 ukuizn7=0;
		 pre = _pre;
	}
	//-----------------------------------------------------------------------------------------------------
	   public void napraviTabelu(Connection _connection,String _pre,int _broj,String _mesec,int _vrstaplate) {
		 connection = _connection;
		 mesec = _mesec;
		 broj = _broj;

		/*
		*	ind1	PIO radnika
		*	ind2	ZDR radnika
		*	ind3	NEZAPOSL radnika
		*
		*	ind4	PIO poslodavca
		*	ind5	ZDR poslodavca
		*	ind6	NEZAPOSL poslodavca
		*/
			
			sql = "DELETE FROM tmpolaksice WHERE pre=" + pre.trim();                      
		    izvrsi(connection,sql);
			
			//stopa poreza na platu je polje p4 u tabeli ldmes ili ldmesprvi
			sql = "INSERT IGNORE INTO tmpolaksice(sifra,br,ind1,ind2,ind3,ind4,ind5,ind6,ind7,ind8,osnpor,osndop,p4,iznpor,koefolaksice,pre)" +     
				//" SELECT sifra,br,ind1,ind2,ind3,ind4,ind5,ind6,ind7,(i25-i26),osndop,p4";
				//osnovica nije bila dobra gore (osnpor) to je bruto-11000 neoporezovan deo kod obrade a to je polje oo
				//u ldmes a iznpor je=(i25-i26)
				//polje ind8 u tmpolaksice je ustvari osnovica doprinosa osndop u ldmes
				" SELECT sifra,br,ind1,ind2,ind3,ind4,ind5,ind6,ind7,ind8,oo,osndop,p4,(i25-i26),koefolaksice,pre";
			sql = sql + " FROM ldmes";
			sql = sql + " WHERE pre=" + pre.trim() + " AND mesec=" + mesec.trim() + " AND brojobrade=" + broj +
				" AND br>0";
				if (_vrstaplate > 0)
				{
					sql = sql + " and vrstaplate=" + _vrstaplate;
				}
					
		    izvrsi(connection,sql);

			
			//uzimanje maticnih podataka iz tabele maticnipodaci
			sql = "UPDATE tmpolaksice,maticnipodaci SET tmpolaksice.prezime=maticnipodaci.prezime,"+
				"tmpolaksice.ime=maticnipodaci.ime," +
				"tmpolaksice.matbr=maticnipodaci.matbr," +
				"tmpolaksice.datumod=maticnipodaci.datumod," +
				"tmpolaksice.datumdo=maticnipodaci.datumdo" +
				" WHERE tmpolaksice.sifra=maticnipodaci.sifra AND maticnipodaci.pre=" + pre.trim()+
				" AND tmpolaksice.pre=" + pre.trim();
		    izvrsi(connection,sql);


			//uzimanje stopa doprinosa za to preduzece iz tabele doprinosi
			sql = "UPDATE tmpolaksice,doprinosi SET tmpolaksice.procdop1=doprinosi.procdop"+
				" WHERE doprinosi.sifdop=1 AND doprinosi.pre=" + pre.trim() + 
				" AND tmpolaksice.pre=" + pre.trim() + " AND doprinosi.vrstaplate=1";
		    izvrsi(connection,sql);
			sql = "UPDATE tmpolaksice,doprinosi SET tmpolaksice.procdop2=doprinosi.procdop"+
				" WHERE doprinosi.sifdop=2 AND doprinosi.pre=" + pre.trim() + 
				" AND tmpolaksice.pre=" + pre.trim() + " AND doprinosi.vrstaplate=1";
		    izvrsi(connection,sql);
			sql = "UPDATE tmpolaksice,doprinosi SET tmpolaksice.procdop3=doprinosi.procdop"+
				" WHERE doprinosi.sifdop=3 AND doprinosi.pre=" + pre.trim() + 
				" AND tmpolaksice.pre=" + pre.trim() + " AND doprinosi.vrstaplate=1";
		    izvrsi(connection,sql);
			sql = "UPDATE tmpolaksice,doprinosi SET tmpolaksice.procdop4=doprinosi.procdop"+
				" WHERE doprinosi.sifdop=4 AND doprinosi.pre=" + pre.trim() + 
				" AND tmpolaksice.pre=" + pre.trim() + " AND doprinosi.vrstaplate=1";
		    izvrsi(connection,sql);
			sql = "UPDATE tmpolaksice,doprinosi SET tmpolaksice.procdop5=doprinosi.procdop"+
				" WHERE doprinosi.sifdop=5 AND doprinosi.pre=" + pre.trim() + 
				" AND tmpolaksice.pre=" + pre.trim() + " AND doprinosi.vrstaplate=1";
		    izvrsi(connection,sql);
			sql = "UPDATE tmpolaksice,doprinosi SET tmpolaksice.procdop6=doprinosi.procdop"+
				" WHERE doprinosi.sifdop=6 AND doprinosi.pre=" + pre.trim() + 
				" AND tmpolaksice.pre=" + pre.trim() + " AND doprinosi.vrstaplate=1";
		    izvrsi(connection,sql);

			//uzimanje procenta doprinosa za benef.r.s.
			sql = "UPDATE tmpolaksice,doprinosi SET tmpolaksice.procdop7=doprinosi.procdop"+
				" WHERE doprinosi.sifdop=15 AND doprinosi.pre=" + pre.trim() + 
				" AND tmpolaksice.pre=" + pre.trim() + " AND doprinosi.vrstaplate=1";
		    izvrsi(connection,sql);

			//racunanje iznosa doprinosa
			sql = "UPDATE tmpolaksice SET izndop1=osndop*(procdop1/100),"+
				"izndop2=osndop*(procdop2/100),"+
				"izndop3=osndop*(procdop3/100),"+
				"izndop4=osndop*(procdop4/100),"+
				"izndop5=osndop*(procdop5/100),"+
				"izndop6=osndop*(procdop6/100),"+
				"izndop7=osndop*(procdop7/100)" + //izndop7 je sada benef.rs.s
				" WHERE pre=" + pre.trim();
		    izvrsi(connection,sql);
			
			//racunanje iznosa poreza uzeti su direktni iznosi (i25-i26) iz tabele ldmes
			//to su iznosi kada se radi plata iz vise delova a i iz jednog
			
			//racunanje iznosa olaksica na osnovu iznosa doprinosa i poreza gde je izn7=poreska olaksica
			sql = "UPDATE tmpolaksice SET izn1=(izndop1*(ind1/100))*koefolaksice,"+
				"izn2=(izndop2*(ind2/100))*koefolaksice,"+
				"izn3=(izndop3*(ind3/100))*koefolaksice,"+
				"izn4=(izndop4*(ind4/100))*koefolaksice,"+
				"izn5=(izndop5*(ind5/100))*koefolaksice,"+
				"izn6=(izndop6*(ind6/100))*koefolaksice,"+
				//benef.r.s. izn8 je zato sto je izn7 ranije bio ubacen za porez
				// a benef.r.s. naknadno
				"izn8=izndop7*(ind8/100),"+	
				"izn7=(iznpor*(ind7/100))*koefolaksice WHERE pre=" + pre.trim();
		    izvrsi(connection,sql);
	  
	  }
	 //-----------------------------------------------------------------------------------------------------
	   public double ukupnaOlaksicaPIORadnika(Connection connection) {
			double ukupioradn=0.0;
			Statement statement=null;
	      try {
	         statement = connection.createStatement();
	               String query = "SELECT SUM(izn1) as ukupioradnika FROM tmpolaksice WHERE pre=" + pre.trim();

			         ResultSet rs = statement.executeQuery( query );
	            		if(rs.next()){
							ukupioradn = rs.getDouble("ukupioradnika");
						}
	      }
	      catch ( SQLException sqlex ) {
	      }
			//.....................................................................................
			finally{
				if (statement != null){
					try{
						statement.close();
						statement = null;
					}catch (Exception e){
					}}
			}
			//.....................................................................................
			return ukupioradn;
	  }
	 //-----------------------------------------------------------------------------------------------------
	   public double ukupnaOlaksicaPoreza(Connection connection) {
			double ukuporez=0.0;
			Statement statement=null;
	      try {
	         statement = connection.createStatement();
	               String query = "SELECT SUM(izn7) as ukuporez FROM tmpolaksice WHERE pre=" + pre.trim();

			         ResultSet rs = statement.executeQuery( query );
	            		if(rs.next()){
							ukuporez = rs.getDouble("ukuporez");
						}
	      }
	      catch ( SQLException sqlex ) {
	      }
			//.....................................................................................
			finally{
				if (statement != null){
					try{
						statement.close();
						statement = null;
					}catch (Exception e){
							}}
			}
			//.....................................................................................
			return ukuporez;
	  }
	 //-----------------------------------------------------------------------------------------------------
	   public int proveriBrojRadnika(Connection connection) {
			int brrad=0;
			Statement statement=null;
	      try {
	         statement = connection.createStatement();
	               String query = "SELECT COUNT(*) as brojradnika FROM tmpolaksice WHERE br<6 AND pre=" + pre.trim();

			         ResultSet rs = statement.executeQuery( query );
	            		if(rs.next()){
							brrad = rs.getInt("brojradnika");
						}
	      }
	      catch ( SQLException sqlex ) {
	      }
			//.....................................................................................
			finally{
				if (statement != null){
					try{
						statement.close();
						statement = null;
					}catch (Exception e){
					}}
			}
			//.....................................................................................
			return brrad;
	  }
	 //-----------------------------------------------------------------------------------------------------
	   public int proveriUkupanBrojRadnika(Connection connection) {
			int brrad=0;
			Statement statement=null;
	      try {
	         statement = connection.createStatement();
	               String query = "SELECT COUNT(*) as brojradnika FROM tmpolaksice WHERE pre=" + pre.trim();

			         ResultSet rs = statement.executeQuery( query );
	            		if(rs.next()){
							brrad = rs.getInt("brojradnika");
						}
	      }
	      catch ( SQLException sqlex ) {
		  }
			//.....................................................................................
			finally{
				if (statement != null){
					try{
						statement.close();
						statement = null;
					}catch (Exception e){
					}}
			}
			//.....................................................................................
			return brrad;
	  }
	 //-----------------------------------------------------------------------------------------------------
	   public int proveriBrojRadnikaOlak(Connection connection,int _sifraolaksice) {
			int brrad=0;
			Statement statement=null;
	      try {
	         statement = connection.createStatement();
	               String query = "SELECT COUNT(*) as brojradnika FROM tmpolaksice WHERE br=" + _sifraolaksice + 
					   " AND pre=" + pre.trim();

			         ResultSet rs = statement.executeQuery( query );
	            		if(rs.next()){
							brrad = rs.getInt("brojradnika");
						}
	      }
	      catch ( SQLException sqlex ) {
	      }
			//.....................................................................................
			finally{
				if (statement != null){
					try{
						statement.close();
						statement = null;
					}catch (Exception e){
					}}
			}
			//.....................................................................................
			return brrad;
	  }
	 //-----------------------------------------------------------------------------------------------------
	   public double ukupnaOlaksicaPorezaInv(Connection connection,int _sifraolaksice) {
			double ukuporez=0.0;
			Statement statement=null;
	      try {
	         statement = connection.createStatement();
	               String query = "SELECT SUM(izn7) as ukuporez FROM tmpolaksice WHERE br=" + _sifraolaksice +
					   " AND pre=" + pre.trim();

			         ResultSet rs = statement.executeQuery( query );
	            		if(rs.next()){
							ukuporez = rs.getDouble("ukuporez");
						}
	      }
	      catch ( SQLException sqlex ) {
	      }
			//.....................................................................................
			finally{
				if (statement != null){
					try{
						statement.close();
						statement = null;
					}catch (Exception e){
					}}
			}
			//.....................................................................................
			return ukuporez;
	  }
	 //-----------------------------------------------------------------------------------------------------
	   public double ukupnaOlaksicaPIOPoslodavca(Connection connection) {
			double ukupioposl=0.0;
			Statement statement=null;
	      try {
	         statement = connection.createStatement();
	               String query = "SELECT SUM(izn4) as ukupioposl FROM tmpolaksice where br<19" + 
					   " AND pre=" + pre.trim();


			         ResultSet rs = statement.executeQuery( query );
	            		if(rs.next()){
							ukupioposl = rs.getDouble("ukupioposl");
						}
	      }
	      catch ( SQLException sqlex ) {
	      }
			//.....................................................................................
			finally{
				if (statement != null){
					try{
						statement.close();
						statement = null;
					}catch (Exception e){
					}}
			}
			//.....................................................................................
			return ukupioposl;
	  }
	 //-----------------------------------------------------------------------------------------------------
	   public double ukupnaOlaksicaZDRPoslodavca(Connection connection) {
			double ukupioposl=0.0;
			Statement statement=null;
	      try {
	         statement = connection.createStatement();
	               String query = "SELECT SUM(izn5) as ukupioposl FROM tmpolaksice where br<19" +
					   " AND pre=" + pre.trim();

			         ResultSet rs = statement.executeQuery( query );
	            		if(rs.next()){
							ukupioposl = rs.getDouble("ukupioposl");
						}
	      }
	      catch ( SQLException sqlex ) {
	      }
			//.....................................................................................
			finally{
				if (statement != null){
					try{
						statement.close();
						statement = null;
					}catch (Exception e){
					}}
			}
			//.....................................................................................
			return ukupioposl;
	  }
	 //-----------------------------------------------------------------------------------------------------
	   public double ukupnaOlaksicaNEZAPPoslodavca(Connection connection) {
			double ukupioposl=0.0;
			Statement statement=null;
	      try {
	         statement = connection.createStatement();
	               String query = "SELECT SUM(izn6) as ukupioposl FROM tmpolaksice where br<19" +
					   " AND pre=" + pre.trim();

			         ResultSet rs = statement.executeQuery( query );
	            		if(rs.next()){
							ukupioposl = rs.getDouble("ukupioposl");
						}
	      }
	      catch ( SQLException sqlex ) {
	      }
			//.....................................................................................
			finally{
				if (statement != null){
					try{
						statement.close();
						statement = null;
					}catch (Exception e){
					}}
			}
			//.....................................................................................
			return ukupioposl;
	  }
	 //-----------------------------------------------------------------------------------------------------
	   public double ukupneOlaksicePoslodavca(Connection connection) {
			double ukupioposl=0.0;
			Statement statement=null;
	      try {
	         statement = connection.createStatement();
	               String query = "SELECT SUM(izn4+izn5+izn6) as ukupioposl FROM tmpolaksice where br<19" +
					   " AND pre=" + pre.trim();

			         ResultSet rs = statement.executeQuery( query );
	            		if(rs.next()){
							ukupioposl = rs.getDouble("ukupioposl");
						}
	      }
	      catch ( SQLException sqlex ) {
	      }
			//.....................................................................................
			finally{
				if (statement != null){
					try{
						statement.close();
						statement = null;
					}catch (Exception e){
					}}
			}
	      	//.....................................................................................
			return ukupioposl;
	  }
	 //-----------------------------------------------------------------------------------------------------
	   public void ukupneOlaksice(Connection connection) {
			ukuizn1=0;
			ukuizn2=0;
			ukuizn3=0;
			ukuizn4=0;
			ukuizn5=0;
			ukuizn6=0;
			ukuizn7=0;
			Statement statement=null;
	      try {
	         statement = connection.createStatement();
	               String query = "SELECT SUM(izn1) as uku1,SUM(izn2) as uku2," +
					   "SUM(izn3) as uku3,SUM(izn4) as uku4," +
					   "SUM(izn5) as uku5,SUM(izn6) as uku6," +
					   "SUM(izn7) as uku7 FROM tmpolaksice" +
					   " WHERE pre=" + pre.trim();

			         ResultSet rs = statement.executeQuery( query );
	            		if(rs.next()){
							ukuizn1 = rs.getDouble("uku1");
							ukuizn2 = rs.getDouble("uku2");
							ukuizn3 = rs.getDouble("uku3");
							ukuizn4 = rs.getDouble("uku4");
							ukuizn5 = rs.getDouble("uku5");
							ukuizn6 = rs.getDouble("uku6");
							ukuizn7 = rs.getDouble("uku7");

						}
	      }
	      catch ( SQLException sqlex ) {
	      }
			//.....................................................................................
			finally{
				if (statement != null){
					try{
						statement.close();
						statement = null;
					}catch (Exception e){
					}}
			}
			//.....................................................................................
	  }
	//------------------------------------------------------------------------------------------------------------------
		public void izvrsi(Connection conn,String sql) {
	      Statement statement = null;
		  try {
				statement = conn.createStatement();
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
	//--------------------------------------------------------------------------------------
	    protected int brojDanaUMesecu(int _god,int _mes) {
			int brdanaumes=0;
			
			switch (_mes)
			{
				case 1: case 3: case 5: case 7: case 8:
				case 10: case 12:
					brdanaumes = 31;
					break;
				case 2: 
					if (((_god % 4 == 0) && !(_god % 100 == 0)) || (_god % 400 == 0 )  )
					{
						brdanaumes = 29;
					}else{brdanaumes = 28;}
					break;
				case 4: case 6: case 9: case 11:
					brdanaumes = 30;
					break;
			}

			return brdanaumes;
		}
	 //------------------------------------------------------------------------------------------------------------------
	
	
	
	
}
