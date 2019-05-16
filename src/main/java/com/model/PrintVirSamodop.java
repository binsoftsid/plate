package com.model;
import java.sql.*;

public class PrintVirSamodop {
	private Connection connection;
	private int mesec,vrstaplate,broj=99,pred;
	private String juzer="",godina="",iis="",BOP="",sifrauplate="";
	
	public PrintVirSamodop() {
	}
	public void obradiVirSamodop(Connection _connection,String _juzer,int _pred,String _godina,
			int _mesec,int _brojobrade,int _vrstaplate) {
		connection = _connection;
		juzer = _juzer.trim();
		pred = _pred;
		godina = _godina;
		mesec = _mesec;
		vrstaplate = _vrstaplate;
		String poslodavac="";
		
		broj = _brojobrade;

		int kk=0;
		String strsql="",dam="",dam1="";
		
		
		dam = mesec + "-" + godina;

		strsql = "drop table if exists tmpldmes1";
		izvrsi(strsql);
		strsql = "create temporary table tmpldmes1 select " +
		" pre,sifmz";
				
			kk = 0;
			while (kk < 38)
			{
				kk++;
				iis = String.valueOf(kk).trim(); 
				strsql = strsql + ",sum(i" + iis + ") as i" + iis;
				strsql = strsql + ",sum(c" + iis + ") as c" + iis;
			}
		strsql = strsql + ",sum(osnpor) as osnpor" +    
		",sum(netoak) as netoak" +
		",sum(osndopak) as osndopak" +
		",sum(pioum) as pioum" +
		",sum(ou) as ou" +
		",sum(oo) as oo" +
		",sum(osndop) as osndop" +
		",sum(pio) as pio" +
		",count(i1) as broj";
		
		strsql = strsql + " from ldmes " +
		" where ldmes.pre = " + pred +
		" and ldmes.mesec = " + mesec + 
		" and ldmes.brojobrade=" + broj +
		" and ldmes.godina=" + godina +
		" and ldmes.vrstaplate=" + vrstaplate +
		" and ldmes.juzer='" + juzer + "'" +
		" group by pre,sifmz";
		izvrsi(strsql);

	strsql = "DROP TABLE IF EXISTS tmpvirmani ";
	izvrsi(strsql);

	strsql = "CREATE temporary TABLE tmpvirmani (" +              
		"Pre  int(11) default 0," +         
        "Naziv1  varchar(35) default ' '," +  
        "Mesto  varchar(20) default ' '," +   
        "Ziror  varchar(30) default ' '," +   
        "polje1  varchar(40) default ' '," +
		"polje2  varchar(12) default ' '," +  
		"polje3  varchar(12) default ' '," + 
		"dam  varchar(10) default ' '," +     
        "sifmz  int(11) default 0," +       
        "nazmz  varchar(20) default ' '," +   
        "zirmz  varchar(40) default ' '," +   
        "iznos  double default '0'," +         
        "sifobs  int(11) default 0," +      
        "nazobs  varchar(25) default ' '," +  
        "zirobs  varchar(30) default ' '," +  
        "pozobs  varchar(30) default ' '," +  
        "sifban  int(11) default 0," +      
        "nazban  varchar(40) default ' '," +  
        "zirban  varchar(30) default ' '," +  
        "pozban  varchar(30) default ' '," + 
        "sifsind  int(11) default 0," +     
        "nazsind  varchar(30) default ' '," + 
        "zirsind  varchar(20) default ' '," + 
        "pozsind  varchar(30) default ' '," + 
        "procsind  double default '0'" +    
	    ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
	izvrsi(strsql);

		strsql = "delete from tmpvirmani where pre = " + pred;
	izvrsi(strsql);

	strsql = "insert into tmpvirmani(pre,sifmz) select distinct pre,sifmz from tmpldmes1" +
		" where tmpldmes1.pre = " + pred;
	izvrsi(strsql);

	strsql = "update tmpvirmani,tmpldmes1 set tmpvirmani.iznos = tmpvirmani.iznos + tmpldmes1.i30 where" +
		" tmpvirmani.pre = tmpldmes1.pre and tmpvirmani.sifmz = tmpldmes1.sifmz";
	izvrsi(strsql);

	strsql = "update tmpvirmani,ostaliparametri set " +
		" tmpvirmani.polje2 = ostaliparametri.polje2 , tmpvirmani.polje3 = ostaliparametri.polje5" +
		"  where tmpvirmani.pre = ostaliparametri.pre ";
	izvrsi(strsql);

	strsql = "update tmpvirmani,mesnezajednice set tmpvirmani.nazmz = mesnezajednice.nazmz ," +
	" tmpvirmani.polje1 = mesnezajednice.pozmz" +
	",tmpvirmani.zirmz = mesnezajednice.zirmz where tmpvirmani.sifmz = mesnezajednice.sifmz" +
	" and mesnezajednice.juzer='" + juzer + "'";
	izvrsi(strsql);

	strsql = "update tmpvirmani,preduzeca set tmpvirmani.Naziv1 = preduzeca.naziv ," +
		" tmpvirmani.Mesto = preduzeca.mesto , tmpvirmani.ziror = preduzeca.ziror" +
		"  where tmpvirmani.pre = preduzeca.pre and preduzeca.juzer-'" + juzer + "'";
	izvrsi(strsql);

	strsql = "Update tmpvirmani set dam = '" + dam + "',iznos=round(iznos,0)";
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

