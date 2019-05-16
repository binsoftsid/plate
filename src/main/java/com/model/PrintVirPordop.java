package com.model;
import java.sql.*;
import com.model.ClassOlaksice;

public class PrintVirPordop {
	private Connection connection;
	private int mesec,vrstaplate,broj=99,pred;
	private String juzer="",godina="",iis="",BOP="",sifrauplate="";
	private ClassOlaksice pclol;
	
	public PrintVirPordop() {
	}
	public void obradiVirPordop(Connection _connection,String _juzer,int _pred,String _godina,
			int _vrstaplate,int _mesec,int _brojobrade,String _BOP,String _sifrauplate) {
		connection = _connection;
		juzer = _juzer.trim();
		pred = _pred;
		godina = _godina;
		mesec = _mesec;
		String poslodavac="";
		BOP = _BOP;
		sifrauplate = _sifrauplate;
		
		vrstaplate = _vrstaplate;
		broj = _brojobrade;

		int kk=0;
		String strsql="",dam="",dam1="";

		dam = mesec + "-" + godina;
		pred = pred;
		
		//pravljenje tabele olaksica
		pclol = new ClassOlaksice(String.valueOf(pred),juzer);
		pclol.napraviTabelu(connection,String.valueOf(pred),broj,String.valueOf(mesec),vrstaplate);

		//izracunava ukupne olaksice po siframa iz tabele tmpolaksice
		pclol.ukupneOlaksice(connection);

		strsql = "drop table if exists tmpldmes1";
		izvrsi(strsql);
		strsql = "create temporary table tmpldmes1 select " +
		" pre,mesec";
					kk = 0;
					while (kk < 38)
					{
						kk++;
						iis = String.valueOf(kk).trim(); 
						strsql = strsql + ",sum(i" + iis + ") as i" + iis;
						strsql = strsql + ",sum(c" + iis + ") as c" + iis;
					}
		strsql = strsql + ",sum((i25+i26)/p4) as osnpor" +    
		",sum(netoak) as netoak" +
		",sum(osndopak) as osndopak" +
		",sum(pioum) as pioum" +
		",sum(ou) as ou" +
		",sum(oo) as oo" +
		",sum(osndop) as osndop" +
		",sum(pio) as pio" +
		",sum(osnben) as osnben" +
		",sum(osnbenak) as osnbenak" +
		",sum(dopben) as dopben" +
		",sum(dopbenak) as dopbenak" +
		",count(i1) as broj";

		strsql = strsql + " from ldmes " +
		" where ldmes.pre = " + pred +
		" and ldmes.mesec = " + mesec +
		" and ldmes.vrstaplate = " + vrstaplate +
		" and brojobrade=" + broj + " AND  ldmes.godina=" + godina + 
		" AND ldmes.juzer='" + juzer + "'" +
		" group by godina,pre,mesec";
		izvrsi(strsql);

		strsql = "DROP TABLE IF EXISTS tmprekdop ";
		izvrsi(strsql);

		strsql = "CREATE temporary TABLE  tmprekdop (" +              
       "pre  int(11) NOT NULL," +            
       "sifdop  int(11) NOT NULL," +         
       "vrdop  int(11) default 0," +      
       "nazdop  varchar(40) default ' '," + 
       "zirdop  varchar(40) default ' '," + 
       "pozdop  varchar(40) default ' '," + 
       "procdop  double default 0," +     
       "osndop  double default '0'," +       
       "izndop  double default '0'," +       
       "mesec  tinyint(4) default 0," +   
       "Naziv1  varchar(35) default ' '," + 
       "Mesto  varchar(20) default ' '," +  
       "Ziror  varchar(30) default ' '," +  
       "polje1  varchar(30) default ' '," + 
       "polje2  varchar(12) default ' '," + 
       "polje3  varchar(12) default ' '," + 
       "dam  varchar(10) default ' '," +    
       "PRIMARY KEY  ( pre , sifdop )" +      
       ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
		izvrsi(strsql);

		strsql = "insert into tmprekdop(pre,sifdop,vrdop,nazdop,zirdop,pozdop,procdop)" +
				" select pre,sifdop,vrdop,nazdop,zirdop,pozdop,procdop from" +
				" doprinosi where vrstaplate=" + vrstaplate; 
		izvrsi(strsql);

		strsql = "update tmprekdop set mesec = " + mesec;
		izvrsi(strsql);

		strsql = "update tmprekdop,tmpldmes1 set tmprekdop.osndop = tmprekdop.osndop + " +
				"(tmpldmes1.osndop - tmpldmes1.osndopak) where tmprekdop.pre = tmpldmes1.pre" +
				" and tmprekdop.mesec = tmpldmes1.mesec and (tmprekdop.vrdop = 1 or tmprekdop.vrdop = 2) ";
		izvrsi(strsql);

		strsql = "update tmprekdop,tmpldmes1 set tmprekdop.osndop = tmprekdop.osndop +" +
				" (tmpldmes1.i22 - tmpldmes1.i34) where tmprekdop.pre = tmpldmes1.pre" +
				" and tmprekdop.mesec = tmpldmes1.mesec and tmprekdop.vrdop = 3 ";
		izvrsi(strsql);

		strsql = "update tmprekdop,tmpldmes1 set tmprekdop.osndop = tmprekdop.osndop +" +
				" (tmpldmes1.oo + tmpldmes1.ou) where tmprekdop.pre = tmpldmes1.pre" +
				" and tmprekdop.mesec = tmpldmes1.mesec and tmprekdop.vrdop = 4 ";
		izvrsi(strsql);


		strsql = "UPDATE tmprekdop a JOIN (SELECT sum(osnben-osnbenak) as uku FROM ldmes" +
				" where pre = " + pred +
				" and mesec = " + mesec +
				" and godina = " + godina +
				" and juzer = '" + juzer + "'" +
				" and vrstaplate = " + vrstaplate + " and brojobrade=" + broj +
				" AND dopben>0" +
				" GROUP BY pre) m ON " +
				" a.sifdop=15 SET a.osndop = m.uku";	
		izvrsi(strsql);
	
	//============================================================================================
		if (poslodavac.equals("poslodavac"))
			{
			//ako je poslodavac zaposlen u firmi - pocetak
			strsql = "drop table if exists tmpldmes1";
			izvrsi(strsql);
		
			strsql = "create temporary table tmpldmes1 select " +
			" pre";
				
					kk = 0;
					while (kk < 38)
					{
						kk++;
						iis = String.valueOf(kk).trim(); 
						strsql = strsql + ",sum(i" + iis + ") as i" + iis;
						strsql = strsql + ",sum(c" + iis + ") as c" + iis;
					}
			//strsql = strsql + ",sum(osnpor) as osnpor" +    
			strsql = strsql + ",sum((i25+i26)/p4) as osnpor" +    
			",sum(netoak) as netoak" +
			",sum(osndopak) as osndopak" +
			",sum(pioum) as pioum" +
			",sum(ou) as ou" +
			",sum(oo) as oo" +
			",sum(osndop) as osndop" +
			",sum(pio) as pio" +
		    ",sum(osnben) as osnben" +
		    ",sum(osnbenak) as osnbenak" +
		    ",sum(dopben) as dopben" +
		    ",sum(dopbenak) as dopbenak" +
			",count(i1) as broj";

			strsql = strsql + " from ldmes " +
			" where ldmes.pre = " + pred +
			" and ldmes.mesec = " + mesec + " and ldmes.sifra = 1" +
			" and ldmes.vrstaplate = " + vrstaplate +
			" and brojobrade=" + broj +
			" group by pre";
			izvrsi(strsql);

			strsql = "update tmprekdop,tmpldmes1 set tmprekdop.osndop = tmprekdop.osndop - " +
			"(tmpldmes1.osndop - tmpldmes1.osndopak)" +
			" where tmprekdop.pre = tmpldmes1.pre and " +
			" tmprekdop.sifdop = 4";
			izvrsi(strsql);

			strsql = "update tmprekdop,tmpldmes1 set tmprekdop.osndop = tmprekdop.osndop - " +
			"(tmpldmes1.osndop - tmpldmes1.osndopak)" +
			" where tmprekdop.pre = tmpldmes1.pre and " +
			" tmprekdop.sifdop = 1";
			izvrsi(strsql);

			strsql = "update tmprekdop,tmpldmes1 set tmprekdop.osndop = " +
			"(tmpldmes1.osndop - tmpldmes1.osndopak)" +
			" where tmprekdop.pre = tmpldmes1.pre and " +
			" tmprekdop.sifdop = 100";
			izvrsi(strsql);

			strsql = "update tmprekdop,tmpldmes1 set tmprekdop.osndop = " +
			"(tmpldmes1.osndop - tmpldmes1.osndopak)" +
			" where tmprekdop.pre = tmpldmes1.pre and " +
			" tmprekdop.sifdop = 101";
			izvrsi(strsql);

			//ako je poslodavac zaposlen u firmi - kraj
		}			

		//izmenjeno zbog beneficiranog rad.staza
		strsql = "update tmprekdop set izndop = osndop * (procdop / 100)" +
				" where tmprekdop.pre = " + pred;		// + " AND tmprekdop.sifdop<>15)" ;
		izvrsi(strsql);

//================================================================================================
//			OLAKSICE
		strsql = "update tmprekdop set izndop = izndop - " + pclol.ukuizn1 + " WHERE sifdop=1" +
				" AND pre = " + pred;
		izvrsi(strsql);
		strsql = "update tmprekdop set izndop = izndop - " + pclol.ukuizn2 + " WHERE sifdop=2" +
				" AND pre = " + pred;
		izvrsi(strsql);
		strsql = "update tmprekdop set izndop = izndop - " + pclol.ukuizn3 + " WHERE sifdop=3" +
				" AND pre = " + pred;
		izvrsi(strsql);
		strsql = "update tmprekdop set izndop = izndop - " + pclol.ukuizn4 + " WHERE sifdop=4" +
				" AND pre = " + pred;
		izvrsi(strsql);
		strsql = "update tmprekdop set izndop = izndop - " + pclol.ukuizn5 + " WHERE sifdop=5" +
				" AND pre = " + pred;
		izvrsi(strsql);
		strsql = "update tmprekdop set izndop = izndop - " + pclol.ukuizn6 + " WHERE sifdop=6" +
				" AND pre = " + pred;
		izvrsi(strsql);
		//umanjenje poreza sifra=13
		strsql = "update tmprekdop set izndop = izndop - " + pclol.ukuizn7 + " WHERE sifdop=13" +
				" AND pre = " + pred;
		izvrsi(strsql);

//=================================================================================================

		strsql = "update tmprekdop,ostaliparametri set tmprekdop.polje1 = ostaliparametri.polje1" +
				" , tmprekdop.polje2 = ostaliparametri.polje2 , tmprekdop.polje3 = ostaliparametri.polje5" +
				"  where tmprekdop.pre = ostaliparametri.pre ";
		izvrsi(strsql);

		strsql = "update tmprekdop,doprinosi set tmprekdop.polje3 = doprinosi.sifra" +
				"  where tmprekdop.pre = doprinosi.pre and tmprekdop.sifdop = doprinosi.sifdop "+
				" AND doprinosi.vrstaplate=" + vrstaplate; 
		izvrsi(strsql);

		strsql = "update tmprekdop,preduzeca set tmprekdop.Naziv1 = preduzeca.naziv ," +
				" tmprekdop.Mesto = preduzeca.mesto , tmprekdop.ziror = preduzeca.ziror" +
				"  where tmprekdop.pre = preduzeca.pre and preduzeca.juzer='" + juzer + "'";
		izvrsi(strsql);

		strsql = "update tmprekdop set pozdop = '" + BOP + "',dam='" + dam + "'";
		izvrsi(strsql);

		strsql = "Update tmprekdop set pozdop = '99 " + dam + "' where pozdop = '.'";
		izvrsi(strsql);


		strsql = "Update tmprekdop set izndop = round(izndop,0)";
		izvrsi(strsql);

		strsql = "delete from tmprekdop where izndop = 0";
		izvrsi(strsql);
		
		//ispravka poreza za platu iz vise delova***************************************
		//kad radimo proracun unazad jer imamo podatke od obracunatog i uplacenog poreza
		strsql = "update tmprekdop,tmpldmes1 set tmprekdop.osndop = tmpldmes1.osnpor" +
			" where tmprekdop.pre = tmpldmes1.pre and " +
			" tmprekdop.sifdop = 13";
		izvrsi(strsql);
		strsql = "update tmprekdop set izndop=(osndop*procdop)/100" +
			" where sifdop = 13";
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
	//--------------------------------------------------------------------------
	   private String proveriPoslodavac(Connection _connection,int _pred,String _juzer){
		   String poslod = ".";
			Statement statement = null;
			try {
	         statement = _connection.createStatement();
	               String query = "SELECT * FROM preduzeca WHERE Pre=" + _pred +
	            		   " AND juzer='" + _juzer + "'";

			         ResultSet rs = statement.executeQuery( query );
			         if(rs.next()){
						poslod = rs.getString("nadimak");
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
					}
				}
			}
			//.....................................................................................
			return poslod;
	   }

}

