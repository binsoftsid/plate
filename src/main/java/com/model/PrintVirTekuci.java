package com.model;
import java.sql.*;

public class PrintVirTekuci {
	private Connection connection;
	private int mesec,vrstaplate,broj=99,pred;
	private String juzer="",godina="",iis="",BOP="",sifrauplate="";
	private int odsif=0,dosif=0;
	
	public PrintVirTekuci() {
	}
	public void obradiVirTekuci(Connection _connection,String _juzer,int _pred,String _godina,
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
		double manjeporez=0,manjedoprinos=0,manjedoprinosradnika=0;
		double iznosporezaolaksice=0;
		
		dam = String.valueOf(mesec) + "-" + godina;

		strsql = "DROP TABLE IF EXISTS tmpvirmani ";
		izvrsi(strsql);

		strsql = "CREATE temporary TABLE tmpvirmani (" +              
			"pre  int(11) default 0," +         
            "Naziv1  varchar(35) default ' '," +  
            "Mesto  varchar(20) default ' '," +   
            "Ziror  varchar(30) default ' '," +   
            "polje1  varchar(30) default ' '," +
			"polje2  varchar(12) default ' '," +  
			"polje3  varchar(12) default ' '," + 
			"dam  varchar(10) default ' '," +     
            "sifra  int(11) default 0," +      
            "imeprezime  varchar(50) default ' '," +   
            "iznos  double default '0'," +         
            "sifobs  int(11) default 0," +      
            "nazobs  varchar(25) default ' '," +  
            "zirobs  varchar(30) default ' '," +  
            "pozobs  varchar(30) default ' '," +  
            "sifban  int(11) default 0," +      
            "nazban  varchar(40) default ' '," +  
            "zirban  varchar(40) default ' '," +  
            "pozban  varchar(40) default ' '," + 
            "sifsind  int(11) default 0," +     
            "nazsind  varchar(30) default ' '," + 
            "zirsind  varchar(20) default ' '," + 
            "pozsind  varchar(30) default ' '," + 
            "procsind  double default '0'" +    
		    ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
		izvrsi(strsql);

		//sifra i iznos virmana
		strsql = "insert into tmpvirmani(pre,sifra,iznos) select distinct pre,sifra,i36" +
			" from ldmes " +
			" where ldmes.pre = " + pred +
			" and ldmes.mesec = " + mesec + 
			" and ldmes.vrstaplate = " + vrstaplate +
			" and brojobrade=" + broj +		
			" and juzer='" + juzer + "'" +		
			" and ldmes.godina=" + godina;
		izvrsi(strsql);
 
		//Ziro racun nalogodavca
		strsql = "update tmpvirmani,preduzeca set tmpvirmani.Naziv1 = preduzeca.naziv ," +
		" tmpvirmani.Mesto = preduzeca.mesto , tmpvirmani.Ziror = preduzeca.ziror  " +
				"where tmpvirmani.pre = preduzeca.pre AND preduzeca.juzer='" + juzer + "'";
		izvrsi(strsql);
		
		//maticni podaci imeprezime,sifrabanke,tekuci racun
		strsql = "update tmpvirmani,maticnipodaci set" +
		" tmpvirmani.sifban = maticnipodaci.sifban" +
		",tmpvirmani.imeprezime = CONCAT(TRIM(maticnipodaci.ime),' ',TRIM(maticnipodaci.prezime))" +
		",tmpvirmani.zirban = maticnipodaci.partija" +
		" where maticnipodaci.juzer='" + juzer + "'" +
		" AND tmpvirmani.pre = maticnipodaci.pre and tmpvirmani.sifra = maticnipodaci.sifra "; 
		izvrsi(strsql);
 
		//uzimanje naziva banke iz sifarnika banaka
		strsql = "update tmpvirmani,banke set tmpvirmani.nazban = banke.nazban" +
		" where tmpvirmani.sifban = banke.sifban AND banke.juzer='" + juzer + "'";
		izvrsi(strsql);
 

 		//preuzima sifru uplate iz unosa
		strsql = "update tmpvirmani set polje3 = '" + sifrauplate + "'";
		izvrsi(strsql);


		strsql = "Update tmpvirmani set dam = '" + dam + "'";
		izvrsi(strsql);
 
		strsql = "update tmpvirmani set pozban = '" + BOP + "'";
		izvrsi(strsql);
		//*************************************************************************************
		//ako je vrsta plate penzioner mora modifikovati iznos zbog olaksice
		if (vrstaplate == 4)
		{
			strsql = "update tmpvirmani,ldmes set" +
			" tmpvirmani.iznos = tmpvirmani.iznos + ldmes.osndop*(" +
			"ldmes.p1*(ldmes.ind1/100)+" +
			"ldmes.p2*(ldmes.ind2/100)+" +
			"ldmes.p3*(ldmes.ind3/100)" +
			")" +
			" where " +
			" tmpvirmani.pre = ldmes.pre and tmpvirmani.sifra = ldmes.sifra " +
			" AND ldmes.mesec=" + mesec + " AND ldmes.brojobrade=" + broj + 
			" AND ldmes.vrstaplate=4 AND ldmes.godina=" + godina + " AND ldmes.juzer='" + juzer + "'"; 
		izvrsi(strsql);
		}
	//*************************************************************************************
		
		strsql = "delete from tmpvirmani where iznos=0";
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

