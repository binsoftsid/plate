package com.model;
import java.sql.*;

public class PrintIspList {
		private Connection connection;
		private int mesec,vrstaplate,broj=99,pred;
		private double dopben;
		private String juzer="",godina="";
		private String strsql="",dam="";
		private double pio=0,zdrav=0,nezap=0,osndop=0,ppio=0,pzdrav=0,pnezap=0;
		private int olaksice=0,sifra=0,odsif=0,dosif=0;
		private double ind1=0,ind2=0,ind3=0,ind4=0,ind5=0,ind6=0;
		//pomocni iznosi doprinosa radnika
		private double pio1=0,zdrav1=0,nezap1=0;
	
	public PrintIspList() {
	}

	public void obradiListic(Connection _connection,String _juzer,int _pred,String _godina,int _vrstaplate,int _mesec,int _odsifre,
			int _dosifre,int _brojobrade) {
			
			int mesec=0,mesec1=0,odsif=0,dosif=0,jur1=0,jur2=0,pred=0,broj=99;
	
			connection = _connection;
			juzer = _juzer;
			pred = _pred;
			godina = _godina;
			odsif = _odsifre;
			dosif = _dosifre;
		
			vrstaplate = _vrstaplate;
			mesec = _mesec;
			broj = _brojobrade;

			
			String strsql,dam="";
			boolean imabenef = false;
			double pio=0,benef=0,zdrav=0,nezap=0,osndop=0,ppio=0,pzdrav=0,pnezap=0;
			int olaksice=0,sifra=0;
			double ind1=0,ind2=0,ind3=0,ind4=0,ind5=0,ind6=0,ind7=0;
			//pomocni iznosi doprinosa radnika
			double pio1=0,zdrav1=0,nezap1=0,porez=0;
			
			dam = String.valueOf(mesec) + "-" + godina;

			//===========================================================
			strsql = "DROP TABLE IF EXISTS tmplistic ";
			izvrsi(connection,strsql,1);
			//strsql = "CREATE TEMPORARY TABLE tmplistic (" +
			strsql = "CREATE TABLE tmplistic (" +
				"pre  int(11) default 0," +                   
				"mesec  tinyint(4) default 0," +              
				"sifra  int(11) default '0'," +                  
				"rbr  int(11) default 0," +                   
				"opis  varchar(22) default ' '," +              
				"c  int(11) default '0'," +                      
				"i  double default '0'," +                       
				"jur  int(11) default 0," +   
				"nazjur varchar(30) default ' '," +               
				"sifobs  int(11) default 0," +                
				"nazobs  varchar(25) default ' '," +            
				"dug  double default 0," +                     
				"rata  double default 0," +                    
				"gs  varchar(6) default '0'," +                    
				"godstaza  int(5) not null default 0," +                
				"messtaza  int(5) not null default 0," +                
				"prezime varchar(20) default ' '," +
				"ime varchar(15) default ' '," +
				"brbod double default 0," +                     
				"broj  int(11) default 0," +                   
				"vrbod double default 0," +                     
				"dam varchar(10) default ' '," +
				"tekuci varchar(20) not null default ' '," +        
				"jmbg varchar(13) not null default ' '," +        
				"adresa varchar(50) not null default ' '," +        
				"juzer varchar(20) not null default ' '," +        
	            "KEY  pre  ( pre ),"+                       
			    "KEY  vrprom  ( mesec ),"+                                                                
			    "KEY  mmag  ( jur ),"+                                                      
			    "KEY  konto  ( sifra ),"+                                                                   
			    "KEY  nalog  ( rbr )"+                                                               
				") DEFAULT CHARSET=utf8;";  
			izvrsi(connection,strsql,1);

			strsql = "DROP TABLE IF EXISTS tmplis ";
			izvrsi(connection,strsql,1);
			strsql = "CREATE TEMPORARY TABLE tmplis LIKE tmplistic ";
			izvrsi(connection,strsql,1);


		Statement statement=null;
		try {
			statement = connection.createStatement();

			strsql = "select * from ldmes where pre = " + pred + " and mesec = " + mesec + 
					" and sifra >= " + odsif + " AND juzer='" + juzer + "'" + " AND godina=" + godina +
					" and sifra <= " + dosif + " and vrstaplate=" + vrstaplate +
					" and brojobrade=" + broj;		

			ResultSet rs = statement.executeQuery( strsql );
			
			//listanje radnika koji ulaze u obracun*****************************************************
			while (rs.next())
			{
				osndop = rs.getDouble("osndop");
				//koeficijenti doprinosa radnika
				pio = rs.getDouble("p1");
				zdrav = rs.getDouble("p2");
				nezap = rs.getDouble("p3");

				//koeficijenti doprinosa poslodavca
				ppio = rs.getDouble("p11");
				pzdrav = rs.getDouble("p22");
				pnezap = rs.getDouble("p33");

				//uzimanje iznosa doprinosa radnika
				pio = osndop*pio;
				zdrav = osndop*zdrav;
				nezap = osndop*nezap;

				//---------------------------------------
				//parametri koji se koriste kod olaksica 
				pio1 = pio;
				zdrav1 = zdrav;
				nezap1 = nezap;
				porez = rs.getDouble("i25");
				olaksice = rs.getInt("br");
				sifra = rs.getInt("sifra");
				//----------------------------------------

				//doprinosi poslodavca
				ppio = osndop*ppio;
				pzdrav = osndop*pzdrav;
				pnezap = osndop*pnezap;

				if (olaksice>0)
				{
					//ako radnik ima olaksice (npr penzioner koji ne placa ZDR i NEZAP
					ind1 = rs.getDouble("ind1");
					ind2 = rs.getDouble("ind2");
					ind3 = rs.getDouble("ind3");
					ind4 = rs.getDouble("ind4");
					ind5 = rs.getDouble("ind5");
					ind6 = rs.getDouble("ind6");
					ind7 = rs.getDouble("ind7");
					
					//umanjuju se doprinosi na osnovu procenta olaksica
					pio = pio - pio*(ind1/100);
					zdrav = zdrav - zdrav*(ind2/100);
					nezap = nezap - nezap*(ind3/100);

					ppio = ppio - ppio*(ind4/100);
					pzdrav = pzdrav - pzdrav*(ind5/100);
					pnezap = pnezap - pnezap*(ind6/100);
				}


				//uzima osnovne podatke za listic iz parametara
				
				strsql = "Insert into tmplistic(pre,rbr,opis) select pre,rbr,opis " +
					"from parametriobracuna where parametriobracuna.pre = " + pred + 
					" and parametriobracuna.rbr < 37 and parametriobracuna.juzer='" + juzer + "'";
				izvrsi(connection,strsql,1);

				//dodeljuje sifru radnika kroz petlju
				strsql = "Update tmplistic set sifra = " + rs.getString("sifra") + " where sifra = 0";
				izvrsi(connection,strsql,1);
	 
				strsql = "Update tmplistic set mesec = " + mesec +
					",broj=" + broj;
				izvrsi(connection,strsql,1);
				
				int i = 1;
				String j = "";
				for (i=1;i<38 ;i++ )
				{
					j = String.valueOf(i);

	 				strsql = "UPDATE tmplistic,ldmes set tmplistic.c = ldmes.c" + j + 
						" , tmplistic.i = ldmes.i" + j + 
						" where tmplistic.rbr = " + i + " and tmplistic.sifra = ldmes.sifra " +
						" and ldmes.pre = " + pred + " and ldmes.sifra = " + rs.getString("sifra") + 
						" and ldmes.mesec = " + mesec + " and ldmes.vrstaplate=" + vrstaplate +
						" and brojobrade=" + broj + " and ldmes.juzer='" + juzer + "'" +
						" and ldmes.godina=" + godina;
									
					izvrsi(connection,strsql,1);
				}
				//proverava da li ima beneficirani staz
				dopben = proveriBenef(broj,rs.getInt("sifra"),mesec,pred,rs.getInt("jur"));
				
				if (dopben>0)
				{
					//ubacivanje stavke na listic samo ako ima benefic.r.s.
					strsql = "Insert into tmplistic(pre,rbr,mesec,jur,sifra,opis,i)" +
						" values(" + pred + ",37," + mesec + "," + jur1 + "," + 
						rs.getInt("sifra") + ",'Dop.za benef. r.s.'" +	
						"," + dopben + ")";
					izvrsi(connection,strsql,1);
				}
				//ubacivanje rasclanjenih doprinosa 25-09-2014 ==============================
					strsql = "Insert into tmplistic(pre,rbr,mesec,jur,sifra,opis,i,nazobs,dug)" +
						" values(" + pred + ",38," + mesec + "," + jur1 + "," + 
						rs.getInt("sifra") + ",'Doprinos PIO'" +	
						"," + pio + ",'PIO poslodavca'," + ppio + ")";
					izvrsi(connection,strsql,1);
					strsql = "Insert into tmplistic(pre,rbr,mesec,jur,sifra,opis,i,nazobs,dug)" +
						" values(" + pred + ",39," + mesec + "," + jur1 + "," + 
						rs.getInt("sifra") + ",'Doprinos Zdravstvo'" +	
						"," + zdrav + ",'Zdravstvo poslodavca'," + pzdrav + ")";
					izvrsi(connection,strsql,1);
					strsql = "Insert into tmplistic(pre,rbr,mesec,jur,sifra,opis,i,nazobs,dug)" +
						" values(" + pred + ",40," + mesec + "," + jur1 + "," + 
						rs.getInt("sifra") + ",'Doprinos za nezap.'" +	
						"," + nezap + ",'Nezaposlenost poslodavca'," + pnezap + ")";
					izvrsi(connection,strsql,1);

				//=======================================================================
				if (olaksice>0)
				{
					
					//modifikuje ukupne doprinose radnika
					strsql = "Update tmplistic set i=i-" + (
						pio1*(ind1/100) + zdrav1*(ind2/100) + nezap1*(ind3/100)) +
						" WHERE sifra = " + sifra + " AND rbr=23";
					izvrsi(connection,strsql,1);
					//modifikuje neto iznos
					strsql = "Update tmplistic set i=i+" + 
						(pio1*(ind1/100) + zdrav1*(ind2/100) + nezap1*(ind3/100)) +
						" WHERE sifra = " + sifra + " AND rbr=29";
					izvrsi(connection,strsql,1);
					//modifikuje svega obustava
					strsql = "Update tmplistic set i=i-" + 
						(pio1*(ind1/100) + zdrav1*(ind2/100) + nezap1*(ind3/100)) +
						" WHERE sifra = " + sifra + " AND rbr=35";
					izvrsi(connection,strsql,1);
					//modifikuje za isplatu 
					strsql = "Update tmplistic set i=i+" + 
						(pio1*(ind1/100) + zdrav1*(ind2/100) + nezap1*(ind3/100)) +
						" WHERE sifra = " + sifra + " AND rbr=36";
					izvrsi(connection,strsql,1);
				}
				
				olaksice=0;

			  }
			strsql = "DROP TABLE IF EXISTS tmplis ";
			izvrsi(connection,strsql,1);

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
				}
			}
		}
		
		//.....................................................................................
		//azuriranje rednog broja u tabeli kredita
		sifra = 0;
		int red = 0;
		Statement stmt=null;


		try {
			stmt = connection.createStatement();
			strsql = "select * from kreditiarh where pre = " + pred + 
			" and mesec = " + mesec + " AND kreditiarh.juzer='" + juzer + "'" + 
			" and kreditiarh.godina=" + godina +
			" AND brojobrade=" + broj + 
			" order by sifra ,sifobs";
			
			ResultSet rs = stmt.executeQuery( strsql );
			while (rs.next())
			{
				if (sifra != rs.getInt("sifra"))
				{
					sifra = rs.getInt("sifra");
					red = 0;
				}
				red = red + 1;

				UpdateKrediti(pred,sifra,rs.getInt("sifobs"),rs.getString("partija"),red,broj);
			 }
		}
		catch ( SQLException sqlex ) {
			sqlex.printStackTrace();
		}
			//.....................................................................................
			finally{
				if (stmt != null){
					try{
						stmt.close();
						stmt = null;
					}catch (Exception e){
						e.printStackTrace();
					}
				}
			}
			//.....................................................................................
			
			strsql = "Update tmplistic,kreditiarh set tmplistic.sifobs = kreditiarh.sifobs, " +
				"tmplistic.nazobs = mid(kreditiarh.nazobs,1,23), tmplistic.dug = kreditiarh.dug, tmplistic.rata = kreditiarh.rata " +
				"where tmplistic.sifra = kreditiarh.sifra and tmplistic.rbr = kreditiarh.rbr and kreditiarh.pre = " + pred +
				" and kreditiarh.mesec = " +  mesec + " and kreditiarh.juzer='" + juzer + "'" +
				" and kreditiarh.godina=" + godina +
				" and (kreditiarh.rata <> 0 or kreditiarh.dug <> 0)" +
				" and tmplistic.broj=kreditiarh.brojobrade";
			izvrsi(connection,strsql,1);
	 

			//odredjivanje staza=======================================
			strsql = "Update tmplistic,maticnipodaci set tmplistic.godstaza = maticnipodaci.gs," +
				"tmplistic.messtaza=maticnipodaci.ms " +
				" where tmplistic.sifra = maticnipodaci.sifra and tmplistic.pre = maticnipodaci.pre" +
				" and tmplistic.rbr = 17 and maticnipodaci.pre = " + pred + " and maticnipodaci.juzer='" +
				juzer + "'";
			izvrsi(connection,strsql,1);

			strsql = "Update tmplistic set messtaza=messtaza-1" +
				" where rbr = 17";
			izvrsi(connection,strsql,1);
			strsql = "Update tmplistic set godstaza=godstaza-1,messtaza=11 WHERE messtaza<0" +
				" and rbr = 17";
			izvrsi(connection,strsql,1);
			strsql = "Update tmplistic set godstaza=0 WHERE godstaza<0" +
				" and rbr = 17";
			izvrsi(connection,strsql,1);
			strsql = "Update tmplistic set gs = CONCAT(godstaza,'/',messtaza)" +
				" WHERE rbr = 17";
			izvrsi(connection,strsql,1);
			
			//========================================================

			//podaci iz maticne
			strsql = "Update tmplistic,maticnipodaci set tmplistic.prezime = maticnipodaci.prezime, " +
				"tmplistic.ime = maticnipodaci.ime, tmplistic.brbod = maticnipodaci.brbod," +
				"tmplistic.tekuci = maticnipodaci.partija, tmplistic.jmbg = maticnipodaci.matbr," +
				"tmplistic.adresa = maticnipodaci.adresa" + 
				" where tmplistic.sifra = maticnipodaci.sifra and maticnipodaci.pre = " + pred +
				" and maticnipodaci.juzer='" + juzer +"'";
			izvrsi(connection,strsql,1);
			
			
			//podaci iz dnevnika - vrednost boda, broj isplate i dam.
			strsql = "Update tmplistic,binmesec set tmplistic.broj=" + broj +
				",tmplistic.vrbod = binmesec.vrednostboda" +
				" where tmplistic.mesec = binmesec.mesec and binmesec.pre = " + pred +
				" and tmplistic.sifra = binmesec.sifra and binmesec.brojobrade=" + broj +
				" and binmesec.juzer='" + juzer + "' and binmesec.godina=" + godina;
			izvrsi(connection,strsql,1);

			strsql = "Update tmplistic set dam='" + dam + "'";
			izvrsi(connection,strsql,1);

			strsql = "ALTER TABLE tmplistic ADD(sifobss varchar(3) default ' '," +
				"dugs varchar(12) default ' ',ratas varchar(12) default ' ')";
			izvrsi(connection,strsql,1);

			strsql = "UPDATE tmplistic SET sifobss = CONCAT(sifobs)," +
				"dugs = CONCAT(FORMAT(dug,2)),ratas = CONCAT(FORMAT(rata,2)) " +
				"WHERE rata<>0 or dug<>0";
			izvrsi(connection,strsql,1);
			
			//stavljanje procenta stimulacije u prvu kolonu kao i dana toplog obroka
			strsql = "UPDATE tmplistic SET gs = CONCAT(c)," +
				"c=0 WHERE (rbr=16 or rbr=18)";
			izvrsi(connection,strsql,1);
			
			//brise stavke sa nulom
			strsql = "DELETE FROM tmplistic WHERE c=0 and i=0 and dug=0 and rata=0";
			izvrsi(connection,strsql,1);
	
	}
 //------------------------------------------------------------------------------------------------------------------
		public void izvrsi(Connection _connection,String sql,int _brr) {
	      Statement statement = null;
		  try {
				statement = _connection.createStatement();
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
//------------------------------------------------------------------------------------------------------------------
		public void UpdateKrediti(int _pred,int _sifra, int _sifobs,String _partija,int _red,int _brojobrade) {
		  Statement statement = null;
	      try {
	         statement = connection.createStatement();

	               String query = "UPDATE kreditiarh SET " +
					"rbr=" + _red + 
					" WHERE sifra=" + _sifra + " and pre=" + _pred +
					" and mesec = " + mesec + " AND godina=" + godina +
					" and sifobs=" + _sifobs + " and partija='" + _partija + "'" +
	    			" and brojobrade=" + _brojobrade + " AND kreditiarh.juzer='" + juzer + "'";

				   int result = statement.executeUpdate( query );
	               if ( result == 1 ){
					}     
					else {
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
//-------------------------------------------------------------------------------------
		   public double proveriBenef(int _broj,int _sifra,int _mesec,int _pred,int _brojobrade) {
			  Statement statement = null;
			  String query = "";
			  double vratidopben = 0;
		      try {
		          statement = connection.createStatement();
						query = "SELECT sifben,dopben FROM ldmes WHERE " +
							"sifra=" + _sifra + " and mesec=" + _mesec + " and brojobrade=" + _brojobrade +
							" AND pre=" + _pred + " AND jur=0" + " AND sifben=1" +
							" AND ldmes.juzer='" + juzer + "' AND ldmes.godina=" + godina;

				         ResultSet rs = statement.executeQuery( query );
				         	if(rs.next()){
								vratidopben = rs.getDouble("dopben");
							}

		      }
		      catch ( SQLException sqlex ) {
				//JOptionPane.showMessageDialog(this, "Greska u proveriBenef:"+sqlex);
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
				return vratidopben;
		  }
	
	
	}	
	

