package com.model;
import java.sql.*;
import com.model.ClassOlaksice;

public class PrintRekapDop {
	private Connection connection;
	private int mesec,vrstaplate,broj=99,pred;
	private String juzer="",godina="",iis="";
	private int odsif=0,dosif=0;
	//pomocni iznosi doprinosa radnika
	private ClassOlaksice pclol;
	
	public PrintRekapDop() {
	}
	public void obradiRekapDop(Connection _connection,String _juzer,int _pred,String _godina,
			int _vrstaplate,int _mesec,int _brojobrade) {
		connection = _connection;
		juzer = _juzer.trim();
		pred = _pred;
		godina = _godina;
		mesec = _mesec;
		String poslodavac="";
		
		vrstaplate = _vrstaplate;
		broj = _brojobrade;

		int kk=0;
		String strsql="",dam="",dam1="";
		double manjeporez=0,manjedoprinos=0,manjedoprinosradnika=0;
		double iznosporezaolaksice=0;


			//formiranje tabele olaksica
			pclol = new ClassOlaksice(String.valueOf(pred),juzer);
			pclol.napraviTabelu(connection,String.valueOf(pred),broj,String.valueOf(mesec),vrstaplate);
			pclol.ukupneOlaksice(connection);

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
			strsql = strsql + ",sum(oo-ou) as osnpor" +    
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
			",sum(i13) as dopbolovanjedo30" +
			",count(i1) as broj";

			strsql = strsql + " from ldmes " +
			" where ldmes.pre = " + pred +
			" and ldmes.mesec = " + mesec +
			" and ldmes.vrstaplate = " + vrstaplate +
			" and ldmes.brojobrade = " + broj +
			" and ldmes.juzer='" + juzer + "'" +
			" and ldmes.godina=" + godina +
			" group by pre,mesec,vrstaplate";	
			
		izvrsi(strsql);

		
		strsql = "DROP TABLE IF EXISTS tmprekdop ";
		izvrsi(strsql);

		strsql = "CREATE TEMPORARY TABLE tmprekdop ( " +             
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
			"vrstaplate  int(3) default '1'," +    
			"PRIMARY KEY  ( pre , sifdop ,vrstaplate)" +     
			") ENGINE=InnoDB DEFAULT CHARSET=utf8;";  
		izvrsi(strsql);


		strsql = "insert into tmprekdop(pre,sifdop,vrdop,nazdop,zirdop,pozdop,procdop,vrstaplate) " +
		"select pre,sifdop,vrdop,nazdop,zirdop,pozdop,procdop,vrstaplate from doprinosi" +
		" where doprinosi.vrstaplate=" + vrstaplate; 
		izvrsi(strsql);


		strsql = "update tmprekdop set mesec = " + mesec + ",pre=" + pred;
		izvrsi(strsql);

		strsql = "update tmprekdop,tmpldmes1 set tmprekdop.osndop = tmprekdop.osndop + " +
			"(tmpldmes1.osndop - tmpldmes1.osndopak)" +
			" where tmprekdop.pre = tmpldmes1.pre and " +
			"(tmprekdop.vrdop = 1 or tmprekdop.vrdop = 2) ";
		izvrsi(strsql);

		strsql = "update tmprekdop,tmpldmes1 set tmprekdop.osndop = tmprekdop.osndop + " +
			"(tmpldmes1.i22 - tmpldmes1.i34) where tmprekdop.pre = tmpldmes1.pre and " +
			" tmprekdop.vrdop = 3 ";
		izvrsi(strsql);

		strsql = "update tmprekdop,tmpldmes1 set tmprekdop.osndop = tmprekdop.osndop + " +
			"(tmpldmes1.oo + tmpldmes1.ou) where tmprekdop.pre = tmpldmes1.pre and " +
			" tmprekdop.vrdop = 4 ";
		izvrsi(strsql);

		
		//ovde obradjuje beneficiran radni staz ali je uzeo sve osnovice ????
		strsql = "update tmprekdop,tmpldmes1 set tmprekdop.osndop = tmprekdop.osndop + " +
			"(tmpldmes1.osnben - tmpldmes1.osnbenak)" +
			" where tmprekdop.pre = tmpldmes1.pre and " +
			"tmprekdop.vrdop = 5 and dopben>0";
		izvrsi(strsql);


		//ispravka poreza za platu iz vise delova***************************************
		//kad radimo proracun unazad jer imamo podatke od obracunatog i uplacenog poreza
			strsql = "update tmprekdop,tmpldmes1 set tmprekdop.izndop = (tmpldmes1.i25+tmpldmes1.i26)" +
			" where tmprekdop.pre = tmpldmes1.pre and " +
			" tmprekdop.sifdop = 13";
			izvrsi(strsql);

			strsql = "update tmprekdop set " +
			"osndop=(izndop/procdop)*100 " +
			" where sifdop = 13";
			izvrsi(strsql);
		//******************************************************************************

		proveriPoslodavac(connection,pred,juzer);
		
		//ovde uzima podatke samo za poslodavca*****************************************
		if (poslodavac.equals("poslodavac"))
			{
			//ako je poslodavac zaposlen u firmi - pocetak

			strsql = "drop table if exists tmpldmes1";
			izvrsi(strsql);
		
		
			strsql = "create temporary table tmpldmes1 select " +
			//strsql = "create table tmpldmes1 select " +
				" pre";
				
					kk = 0;
					while (kk < 38)
					{
						kk++;
						iis = String.valueOf(kk).trim(); 
						strsql = strsql + ",sum(i" + iis + ") as i" + iis;
						strsql = strsql + ",sum(c" + iis + ") as c" + iis;
					}
			strsql = strsql + ",sum(oo-ou) as osnpor" +    
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
			" where ldmes.pre = " + pred + " AND ldmes.godina=" + godina +
			" and ldmes.mesec = " + mesec + " and ldmes.sifra = 1" +
			" and ldmes.vrstaplate = " + vrstaplate + " and brojobrade=" + broj +
			" group by pre,mesec,vrstaplate";

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
		}else{
			//ubaceno da nulira dop. za poslodavca ako nije poslodavac u tabeli preduzeca
			//poslodavac (D/N) = N
				
			strsql = "update tmprekdop set osndop = 0,izndop = 0  " +
				" where sifdop = 100";
			izvrsi(strsql);
			strsql = "update tmprekdop set osndop = 0,izndop = 0  " +
				" where sifdop = 101";
			izvrsi(strsql);
			
		}			

		//ispravka suma za benef.radni staz iz ldmes ili ldmesprvi ========================================
		strsql = "UPDATE tmprekdop a JOIN (SELECT sum(osnben-osnbenak) as uku FROM ldmes" +
			" where pre = " + pred +
			" and mesec = " + mesec + " and dopben>0 and vrstaplate=" + vrstaplate +
			" and brojobrade=" + broj + " AND godina=" + godina + " AND juzer='" + juzer + "'" +
			" GROUP BY pre,mesec,brojobrade) m ON " +
			" a.sifdop=15 SET a.osndop = m.uku";	
		izvrsi(strsql);			
		
		
		strsql = "update tmprekdop set izndop = osndop * (procdop / 100) where tmprekdop.pre = " + pred;
		izvrsi(strsql);			
		dam = mesec + "-" + godina;

		strsql = "Update tmprekdop set izndop = round(izndop,0)";
		izvrsi(strsql);
		
		
		//ovo radi samo za redovnu platu vrstaplate=1*********************************
		if (vrstaplate==1 || vrstaplate==4)
		{
		//umanjenje doprinosa i poreza za olaksice-----------------------
		pclol.ukupneOlaksice(connection);
		strsql = "Update tmprekdop set izndop = izndop-" +
			pclol.ukuizn1 + " WHERE sifdop=1";
		izvrsi(strsql);
		strsql = "Update tmprekdop set izndop = izndop-" +
			pclol.ukuizn2 + " WHERE sifdop=2";
		izvrsi(strsql);
		strsql = "Update tmprekdop set izndop = izndop-" +
			pclol.ukuizn3 + " WHERE sifdop=3";
		izvrsi(strsql);
		strsql = "Update tmprekdop set izndop = izndop-" +
			pclol.ukuizn4 + " WHERE sifdop=4";
		izvrsi(strsql);
		strsql = "Update tmprekdop set izndop = izndop-" +
			pclol.ukuizn5 + " WHERE sifdop=5";
		izvrsi(strsql);
		strsql = "Update tmprekdop set izndop = izndop-" +
			pclol.ukuizn6 + " WHERE sifdop=6";
		izvrsi(strsql);
		//umanjenje poreza sifdop=13
		strsql = "Update tmprekdop set izndop = izndop-" +
			pclol.ukuizn7 + " WHERE sifdop=13";
		izvrsi(strsql);
		
		strsql = "Update tmprekdop set izndop = 0 " +
			" WHERE izndop<1 AND (sifdop=13 OR sifdop=1 OR sifdop=2 OR sifdop=3" +
			" OR sifdop=4 OR sifdop=5 OR sifdop=6)";
		izvrsi(strsql);

		
		//******************************************************************************
		}
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
	 //--------------------------------------------------------------------------------------
	    public String nadjiVrstuPlate() {
		  Statement statement = null;
		  String nazivplate = "";
	      try {
	         statement = connection.createStatement();
	               String query = "SELECT * FROM vrsteplata WHERE sifra=" + vrstaplate;

			         ResultSet rs = statement.executeQuery( query );
			         if(rs.next()){
						nazivplate = rs.getString("naziv"); 
						rs.close();
					 }
		  }     
	      catch ( SQLException sqlex ) {
	    	  System.out.println("Greska u trazenju vrste plata:"+sqlex);
	      }
			//.....................................................................................
			finally{
				if (statement != null){
					try{
						statement.close();
						statement = null;
					}catch (Exception e){
						
					}
				}
			}
			//.....................................................................................
	      return nazivplate;
	  }

}
