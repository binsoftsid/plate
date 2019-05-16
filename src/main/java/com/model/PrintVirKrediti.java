package com.model;
import java.sql.*;

public class PrintVirKrediti {
	private Connection connection;
	private int mesec,vrstaplate,broj=99,pred;
	private String juzer="",godina="",iis="",BOP="",sifrauplate="";
	
	public PrintVirKrediti() {
	}
	public void obradiVirKrediti(Connection _connection,String _juzer,int _pred,String _godina,
			int _mesec,int _brojobrade,int _vrstaplate) {
		connection = _connection;
		juzer = _juzer.trim();
		pred = _pred;
		godina = _godina;
		mesec = _mesec;
		vrstaplate = _vrstaplate;
		String poslodavac="",odmess;
		
		broj = _brojobrade;

		int kk=0;
		String strsql="",dam="",dam1="";
		
		odmess = String.valueOf(mesec);
		if (odmess.length() == 1)
		{odmess = "0" + odmess;}	
		
		dam = odmess + "-" + godina;

		strsql = "DROP TABLE IF EXISTS tmpvirmani ";
		izvrsi(strsql);

		strsql = "CREATE TEMPORARY TABLE tmpvirmani (" +              
		"Pre  int(11) default 0," +         
        "Naziv1  varchar(35) default ' '," +  
        "Mesto  varchar(20) default ' '," +   
        "Ziror  varchar(30) default ' '," +   
        "polje1  varchar(30) default ' '," +
		"polje2  varchar(12) default ' '," +  
		"polje3  varchar(12) default ' '," + 
		"dam  varchar(10) default ' '," +     
        "sifmz  int(11) default 0," +       
        "nazmz  varchar(20) default ' '," +   
        "zirmz  varchar(20) default ' '," +   
        "iznos  double default '0'," +         
        "sifobs  int(11) default 0," +      
        "nazobs  varchar(40) default ' '," +  
        "zirobs  varchar(40) default ' '," +  
        "pozobs  varchar(40) default ' '," +  
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

			strsql = "insert into tmpvirmani(pre,sifobs) select distinct pre,sifobs from kreditiarh" +
		" where kreditiarh.pre = " + pred + " and kreditiarh.brojobrade=" + broj +
		" and kreditiarh.mesec= " +	 mesec + " and kreditiarh.vrstaplate=" + vrstaplate + 
		" and kreditiarh.godina=" + godina + " and kreditiarh.juzer='" + juzer + "'" +
		" and mid(kreditiarh.dam,1,7) = '" + dam + "'";
		izvrsi(strsql);
			
		strsql = "drop table if exists tmpkredit";
		izvrsi(strsql);

		strsql = "create temporary table tmpkredit select " +
		" pre,sifobs,sum(rata) as rata" + 
		" from kreditiarh " +
		" where kreditiarh.pre = " + pred +
		" and kreditiarh.mesec = " + mesec +
		" and kreditiarh.godina = " + godina +
		" and kreditiarh.vrstaplate = " + vrstaplate +
		" and kreditiarh.juzer = '" + juzer + "'" +
		" and kreditiarh.brojobrade = " + broj +
		" and mid(kreditiarh.dam,1,7) = '" + dam + "'" +
		" group by pre,sifobs";
		izvrsi(strsql);

		strsql = "update tmpvirmani,tmpkredit set tmpvirmani.iznos =  tmpkredit.rata" +
		" where tmpvirmani.pre = tmpkredit.pre and tmpvirmani.sifobs = tmpkredit.sifobs";
		izvrsi(strsql);


		strsql = "update tmpvirmani,ostaliparametri set tmpvirmani.polje1 = ostaliparametri.polje1," +
		" tmpvirmani.polje2 = ostaliparametri.polje2 , tmpvirmani.polje3 = ostaliparametri.polje4" +
		"  where tmpvirmani.pre = ostaliparametri.pre ";
		izvrsi(strsql);

		strsql = "Update tmpvirmani set pozobs = '99            " + dam + "' where pozobs = '.'";
		izvrsi(strsql);

		strsql = "update tmpvirmani,kreditori set tmpvirmani.nazobs = kreditori.nazobs," +
		" tmpvirmani.zirobs = kreditori.zirobs , tmpvirmani.pozobs = kreditori.pozobs" +
		" where tmpvirmani.sifobs = kreditori.sifobs and kreditori.juzer='" + juzer + "'";
		izvrsi(strsql);

		strsql = "Update tmpvirmani set pozobs = '99            " + dam + "' where pozobs = '.'";
		izvrsi(strsql);

		strsql = "update tmpvirmani,preduzeca set tmpvirmani.Naziv1 = preduzeca.naziv," +
		" tmpvirmani.Mesto = preduzeca.mesto , tmpvirmani.ziror = preduzeca.ziror" +
		"  where tmpvirmani.pre = preduzeca.pre and preduzeca.juzer='" + juzer + "'";
		izvrsi(strsql);

		strsql = "Update tmpvirmani set dam = '" + dam + "'";
		izvrsi(strsql);
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

