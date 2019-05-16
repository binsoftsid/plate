package com.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.sql.*;
import org.springframework.jdbc.core.JdbcTemplate;


public class ClassOlaksice {
	@Autowired
	
	private Connection connection = null;
	public String mesec,sql="",pre="1",juzer="";
	private int broj=0;
	public static double ukuizn1=0,ukuizn2=0,ukuizn3=0,ukuizn4=0,ukuizn5=0,ukuizn6=0,ukuizn7=0;
	public static double ind1=0,ind2=0,ind3=0,ind4=0,ind5=0,ind6=0,ind7=0,ind8=0;
	private String datumod="",datumdo="";
	private int brojdanaumesecu=0;

 
 public ClassOlaksice(String _pre,String _juzer) {
	 ind1=0;
	 ind2=0;
	 ind3=0;
	 ind4=0;
	 ind5=0;
	 ind6=0;
	 ind7=0;
	 ind8=0;
	 pre = _pre;
	 juzer = _juzer;
}
public double getInd1(){
	return ind1;
}
public double getInd2(){
	return ind2;
}
public double getInd3(){
	return ind3;
}
public double getInd4(){
	return ind4;
}
public double getInd5(){
	return ind5;
}
public double getInd6(){
	return ind6;
}
public double getInd7(){
	return ind7;
}
public double getInd8(){
	return ind8;
}
 //-----------------------------------------------------------------------------------------------------
 //proverava da li radnik ima olaksicu br>0 i da li je istekla olaksica
   public boolean proveriOlaksicu(Connection connection,int _sifraradnika,int _mesec,String _godina) {
	 ind1=0;
	 ind2=0;
	 ind3=0;
	 ind4=0;
	 ind5=0;
	 ind6=0;
	 ind7=0;
	 ind8=0;
	    
		
		boolean imaolaksicu = false;
		String strmesec = String.valueOf(_mesec);
		if (strmesec.length()==1)
		{
			strmesec = "0"+strmesec;
		}
		Statement statement=null;
      try {
         statement = connection.createStatement();
               String query = "SELECT * FROM  maticnipodaci WHERE pre=" + pre.trim() +
				   " AND sifra=" + _sifraradnika + " and br>0 AND maticnipodaci.juzer='" + juzer + "'" +
					" AND ABS(CONCAT(SUBSTRING(datumod,1,4),SUBSTRING(datumod,6,2)))<="+_godina+strmesec+
					" AND ABS(CONCAT(SUBSTRING(datumdo,1,4),SUBSTRING(datumdo,6,2)))>="+_godina+strmesec;

		         ResultSet rs = statement.executeQuery( query );
            		if(rs.next()){
						imaolaksicu = true;
						broj = rs.getInt("br");
						//uzima datume vazenja olaksice
						datumod = rs.getString("datumod");
						datumdo = rs.getString("datumdo");

						//nalazi broj dana u tom mesecu
						brojdanaumesecu =  brojDanaUMesecu(_godina,_mesec);

						//uzima parametre iz tabele "plola" na osnovu broja olaksice
						uzmiParametre(connection,broj);

						//koriguje parametre u zavisnosti od datuma od-do
						korigujParametre(_godina,_mesec);

					}else{
						 imaolaksicu = false;
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
		return imaolaksicu;
  }
//------------------------------------------------------------------------------------------------------------------
	public void uzmiParametre(Connection connection,int _broj) {
	  String query = "";
	  Statement statement = null;
      try {
         statement = connection.createStatement();
                 query = "SELECT * FROM plola WHERE br=" + _broj;
				 ResultSet rs = statement.executeQuery( query );
		         if(rs.next()){
		         	ind1 = rs.getDouble("ind1");
		         	ind2 = rs.getDouble("ind2");
		         	ind3 = rs.getDouble("ind3");
		         	ind4 = rs.getDouble("ind4");
		         	ind5 = rs.getDouble("ind5");
		         	ind6 = rs.getDouble("ind6");
		         	ind7 = rs.getDouble("ind7");
		         	ind8 = rs.getDouble("ind8");
			 }
     }catch ( SQLException sqlex ) {
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
    protected int brojDanaUMesecu(String _godina,int _mes) {
		int brdanaumes=0;
		int _god = Integer.parseInt(_godina);
		
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
//--------------------------------------------------------------------------------------
    protected void korigujParametre(String _godina,int _mes) {
		int god_od=0,mes_od=0,dan_od=0;
		int god_do=0,mes_do=0,dan_do=0;
		int ggodina=0,mmesec=0;
		double koeficijent=0.0;
		double aa=0,bb=0;
		try{
			god_od = Integer.parseInt(datumod.substring(0,4));
			mes_od = Integer.parseInt(datumod.substring(5,7));
			dan_od = Integer.parseInt(datumod.substring(8,10));
			god_do = Integer.parseInt(datumdo.substring(0,4));
			mes_do = Integer.parseInt(datumdo.substring(5,7));
			dan_do = Integer.parseInt(datumdo.substring(8,10));
			ggodina =  Integer.parseInt(_godina);
			mmesec = _mes;
		}catch(Exception ee){
		
		}

		//ispituje da li je olaksica pocela u tom mesecu
		if (god_od == ggodina && mes_od == mmesec)
		{
			aa = (double)(brojdanaumesecu - dan_od + 1);
			bb = (double)(brojdanaumesecu);
			koeficijent = aa/bb;
		}

		//ispituje da li olaksica zavrsava u tom mesecu
		if (god_do == ggodina && mes_do == mmesec)
		{
			
			aa =(double)(dan_do);
			bb = (double)(brojdanaumesecu);
			koeficijent = aa/bb;
		}

		//ako je koeficijent manji od 1 koriguju se parametri
		if (koeficijent < 1 && koeficijent > 0)
		{
			ind1 = ind1*koeficijent;
			ind2 = ind2*koeficijent;
			ind3 = ind3*koeficijent;
			ind4 = ind4*koeficijent;
			ind5 = ind5*koeficijent;
			ind6 = ind6*koeficijent;
			ind7 = ind7*koeficijent;
			ind8 = ind8*koeficijent;
		}
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
 		
  		//racunanje iznosa olaksica na osnovu iznosa doprinosa i poreza gde je izn7=poreska olaksica
 		sql = "UPDATE tmpolaksice SET izn1=(izndop1*(ind1/100))*koefolaksice,"+
 			"izn2=(izndop2*(ind2/100))*koefolaksice,"+
 			"izn3=(izndop3*(ind3/100))*koefolaksice,"+
 			"izn4=(izndop4*(ind4/100))*koefolaksice,"+
 			"izn5=(izndop5*(ind5/100))*koefolaksice,"+
 			"izn6=(izndop6*(ind6/100))*koefolaksice,"+
 			"izn8=izndop7*(ind8/100),"+	
 			"izn7=(iznpor*(ind7/100))*koefolaksice WHERE pre=" + pre.trim();
 	    izvrsi(connection,sql);
   
   }
 //------------------------------------------------------------------------------------	
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
    	   sqlex.printStackTrace();
       }
 		//.....................................................................................
 		finally{
 			if (statement != null){
 				try{
 					statement.close();
 					statement = null;
 				}catch (Exception e){
 					e.printStackTrace();
 					
 				}}
 		}
 		//.....................................................................................
   }
}
