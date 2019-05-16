package com.model;
import java.sql.*;
import com.model.ClassOlaksice;

public class PrintRekap {
	private Connection connection;
	private int mesec,vrstaplate,broj=99,pred;
	private String juzer="",godina="",iis="";
	private int odsif=0,dosif=0;
	//pomocni iznosi doprinosa radnika
	private ClassOlaksice pclol;
	
	public PrintRekap() {
	}
	public void obradiRekap(Connection _connection,String _juzer,int _pred,String _godina,
			int _vrstaplate,int _mesec,int _odsif,int _dosif,int _brojobrade) {
		connection = _connection;
		juzer = _juzer.trim();
		pred = _pred;
		godina = _godina;
		mesec = _mesec;
		odsif = _odsif;
		dosif = _dosif;
		
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
			" and ldmes.sifra >= " + odsif + 
			" and ldmes.sifra <= " + dosif +
			" and ldmes.brojobrade=" + broj +
			" and ldmes.vrstaplate = " + vrstaplate +
			" and ldmes.juzer='" + juzer + "'" +
			" and ldmes.godina=" + godina +
			" group by pre";
			izvrsi(strsql);

			strsql = "DROP TABLE IF EXISTS tmpizvrek ";
			izvrsi(strsql);

			strsql = "CREATE temporary TABLE  tmpizvrek (" +
				"pre  int(11) default 0," +        
				"rbr  int(11) default 0," +        
				"opis  varchar(25) default ' '," +   
				"c  int(11) default '0'," +           
				"i  double default '0'," +            
				"sifkval  int(11) default '0'," +     
				"jur  int(11) default '0'," +         
				"pros  double default '0'," +         
				"prosld  double default '0'," +       
				"upros  double default '0'," +        
				"uprosld  double default '0'," +     
				"dam1  varchar(10) default ' '," +   
				"dam  varchar(10) default ' '," +    
				"broj  int(11) default '0'" +         
				") ENGINE=InnoDB DEFAULT CHARSET=utf8;";  
			izvrsi(strsql);

			strsql = "Insert into tmpizvrek(pre,rbr,opis) select pre,rbr,opis from parametriobracuna " +
				"where parametriobracuna.pre = " + pred + " AND juzer='" + juzer + "'";
			izvrsi(strsql);
			
			int i = 1;
			String j = "";
			for (i=1;i<38 ;i++ )
			{
				j = String.valueOf(i);
				strsql = "UPDATE tmpizvrek,tmpldmes1 set tmpizvrek.c = tmpizvrek.c + tmpldmes1.c" + j + 
					" , tmpizvrek.i = tmpizvrek.i + tmpldmes1.i" + j + " where tmpizvrek.rbr = " + i + 
					" and tmpldmes1.pre = " + pred;
				izvrsi(strsql);
			}
			
			//ubacivanje stavke beneficirani rad.staz
			strsql = "Insert into tmpizvrek(pre,rbr,opis,c) VALUES("+pred+",38,'Benef.radni staz',0) ";
			izvrsi(strsql);

			//ubacivanje stavke olaksice poreza------------------------------
			strsql = "Insert into tmpizvrek(pre,rbr,opis,c) VALUES("+pred+",39,'Olaksice poreza',0) ";
			izvrsi(strsql);
			//uzimanje ukupnih olaksica iz tabele tmpolaksice
			manjeporez = pclol.ukuizn7;
			manjedoprinos = pclol.ukuizn1 + pclol.ukuizn2 + pclol.ukuizn3 + pclol.ukuizn4 +
				pclol.ukuizn5 + pclol.ukuizn6;
			manjedoprinosradnika = pclol.ukuizn1 + pclol.ukuizn2 + pclol.ukuizn3;
				
			strsql = "UPDATE tmpizvrek SET i=" + manjeporez +
					" WHERE rbr=39";
			izvrsi(strsql);
			//---------------------------------------------------------------
			//ubacivanje stavke olaksice doprinosa
			strsql = "Insert into tmpizvrek(pre,rbr,opis,c) VALUES("+pred+",40,'Olaksice doprinosa',0) ";
			izvrsi(strsql);
				strsql = "UPDATE tmpizvrek SET i=" + manjedoprinos +
					" WHERE rbr=40";
				izvrsi(strsql);

				//ako je penzioner
			if (vrstaplate == 4)
			{
				//obracunat doprinos radnika ukupno
				strsql = "UPDATE tmpizvrek SET i=i - " + manjedoprinosradnika + 
					" WHERE rbr=23";
				izvrsi(strsql);
				//neto zarada
				strsql = "UPDATE tmpizvrek SET i=i + " + manjedoprinosradnika + 
					" WHERE rbr=29";
				izvrsi(strsql);
				//svega obustava
				strsql = "UPDATE tmpizvrek SET i=i - " + manjedoprinosradnika + 
					" WHERE rbr=35";
				izvrsi(strsql);
				//za isplatu
				strsql = "UPDATE tmpizvrek SET i=i + " + manjedoprinosradnika + 
					" WHERE rbr=36";
				izvrsi(strsql);
				//za isplatu
				strsql = "UPDATE tmpizvrek SET i=i + " + manjedoprinosradnika + 
					" WHERE rbr=37";
				izvrsi(strsql);
			}
            
			//ubacivanje sume stavki beneficirani rad.staz ali ako ima i olaksice onda ne ulazi u sumu========
				strsql = "UPDATE tmpizvrek a JOIN (SELECT sum(dopben) as uku FROM ldmes" +
				" where pre = " + pred +
				" and mesec = " + mesec +
				" and sifra >= " + odsif + 
				" and sifra <= " + dosif +
				" and br=0 " + 
				" and brojobrade=" + broj +
				" and vrstaplate = " + vrstaplate +
				" and godina = " + godina +
				" and juzer = '" + juzer + "'" +
				" GROUP BY pre) m ON " +
				" a.rbr=38 SET a.i = m.uku";	

			izvrsi(strsql);
			//=========================================================================================
			//tumbanje rednih brojeva da bi potrebna sredstva bila na kraj tabele
			strsql = "UPDATE tmpizvrek SET rbr = 41 WHERE rbr=37 ";
			izvrsi(strsql);
			strsql = "UPDATE tmpizvrek SET rbr = 37 WHERE rbr=38 ";
			izvrsi(strsql);
			strsql = "UPDATE tmpizvrek SET rbr = 38 WHERE rbr=39 ";
			izvrsi(strsql);
			strsql = "UPDATE tmpizvrek SET rbr = 39 WHERE rbr=40 ";
			izvrsi(strsql);
			strsql = "UPDATE tmpizvrek SET rbr = 40 WHERE rbr=41 ";
			izvrsi(strsql);
			//==========================================================================================
			
			strsql = "UPDATE tmpizvrek,tmpldmes1 set tmpizvrek.broj = tmpldmes1.broj where  " +
				"tmpizvrek.pre = tmpldmes1.pre and tmpizvrek.rbr = 1 ";
			izvrsi(strsql);

			dam = mesec + "-" + godina;
			dam1 = mesec + "-" + godina;
			strsql = "UPDATE tmpizvrek SET dam =  '" + dam + "' , dam1 = '" + dam1 + "'";
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
