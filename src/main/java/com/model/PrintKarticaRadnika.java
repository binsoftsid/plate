package com.model;
import java.sql.*;
import com.model.ClassOlaksice;

public class PrintKarticaRadnika {
	private Connection connection;
	private int mesec,vrstaplate,broj=99,pred;
	private String juzer="",godina="",iis="";
	private int sifra=0,odmeseca=0,domeseca=0;
	//pomocni iznosi doprinosa radnika
	private ClassOlaksice pclol;
	
	public PrintKarticaRadnika() {
	}
	public void obradiKarticu(Connection _connection,String _juzer,int _pred,String _godina,
			int _odmeseca,int _domeseca,int _sifra) {
		String strsql="",dam="",sqll="";
		int yy = 0;
		connection = _connection;
		juzer = _juzer.trim();
		pred = _pred;
		godina = _godina;
		odmeseca = _odmeseca;
		domeseca = _domeseca;
		sifra = _sifra;
		
		dam = String.valueOf(odmeseca) + "-" + godina;
		yy = domeseca;

		strsql = "DROP TABLE IF EXISTS tmpkarta ";
		izvrsi(strsql);

		strsql = "CREATE temporary TABLE tmpkarta (" +               
		//strsql = "CREATE TABLE tmpkarta (" +               
		"pre  int(11) default '1'," +        
		"sifra  int(11) default '0'," +       
		"prezime varchar(20) default ' '," +       
		"ime varchar(15) default ' '," +       
		"rbr  int(11) default 0," +        
		"opis  varchar(20) default ' '," +   
		"c1  double default '0'," +          
		"i1  double default '0'," +          
		"c2  double default '0'," +          
		"i2  double default '0'," +           
		"c3  double default '0'," +           
		"i3  double default '0'," +           
		"c4  double default '0'," +           
		"i4  double default '0'," +           
		"c5  double default '0'," +           
		"i5  double default '0'," +           
		"c6  double default '0'," +           
		"i6  double default '0'," +           
		"c7  double default '0'," +           
		"i7  double default '0'," +           
		"c8  double default '0'," +           
		"i8  double default '0'," +           
		"c9  double default '0'," +           
		"i9  double default '0'," +           
		"c10  double default '0'," +          
		"i10  double default '0'," +          
		"c11  double default '0'," +          
		"i11  double default '0'," +          
		"c12  double default '0'," +          
		"i12  double default '0'," +          
		"cu  double default '0'," +          
		"iu  double default '0'," +           
		"vrstaplate int(3) NOT NULL DEFAULT '1'," +
		"godina  int(11) default '0'" +       
		") DEFAULT CHARSET=utf8;";
		izvrsi(strsql);


			strsql = "Insert into tmpkarta(pre,rbr,opis) select pre,rbr,opis from " +
				"parametriobracuna where parametriobracuna.pre = " + pred + 
				" and (parametriobracuna.dalic = 1 or parametriobracuna.dalii = 1)" +
				" and juzer='" + juzer + "'";
			izvrsi(strsql);

			strsql = "Update tmpkarta set sifra = " + sifra + " where sifra = 0";
			izvrsi(strsql);

		//.....................................................................................

		int y = 0;
		int i = 0;
		String z = "",j = "";


		while (y < domeseca)
		{
			y++;
			z = String.valueOf(y);
			i = 0;
		
		
			while (i < 36)
			{
				i++;
				j = String.valueOf(i); 
				
				strsql = "Update tmpkarta,ldmes set tmpkarta.c" + z + " = ldmes.c" + j + 
				" , tmpkarta.i" + z + " = ldmes.i" + j + " where tmpkarta.rbr = " + i + 
				" and tmpkarta.sifra = ldmes.sifra and  ldmes.pre = " + pred + 
				" and ldmes.mesec=" + y + " and ldmes.brojobrade=99 and ldmes.juzer='" + juzer + "'" +
				" and ldmes.godina=" + godina;
				izvrsi(strsql); 

				//ako radnik ima olaksice - penzioner korekcija obr. doprinosa
				if (i == 23)
				{
					sqll = "UPDATE tmpkarta,ldmes set tmpkarta.i"+z+" = tmpkarta.i"+z+"-((ldmes.osndop*ldmes.p1*(ldmes.ind1/100))+" + 
						"(ldmes.osndop*ldmes.p2*(ldmes.ind2/100))+" +
						"(ldmes.osndop*ldmes.p3*(ldmes.ind3/100))" +
						")" +
						" where tmpkarta.rbr = 23" + 
						" and tmpkarta.sifra = ldmes.sifra and ldmes.pre = " + pred + 
						" and ldmes.mesec=" + y + " and ldmes.brojobrade=99 and ldmes.vrstaplate=4" +
						" and ldmes.godina=" + godina + " and ldmes.juzer='" + juzer + "'";
					izvrsi(sqll); 
				}
				//ako radnik ima olaksice - penzioner korekcija neto iznosa
				if (i == 29)
				{
					sqll = "UPDATE tmpkarta,ldmes set tmpkarta.i"+z+"=tmpkarta.i" + z +
						"+((ldmes.osndop*ldmes.p1*(ldmes.ind1/100))+" + 
						"(ldmes.osndop*ldmes.p2*(ldmes.ind2/100))+" +
						"(ldmes.osndop*ldmes.p3*(ldmes.ind3/100))+" +
						"(ldmes.oo*ldmes.p4*(ldmes.ind7/100))" +
						")" +
						" where tmpkarta.rbr = 29" + 
						" and tmpkarta.sifra = ldmes.sifra and ldmes.pre = " + pred + 
						" and ldmes.mesec=" + y + " and ldmes.brojobrade=99 and ldmes.vrstaplate=4" +
						" and ldmes.godina=" + godina + " and ldmes.juzer='" + juzer + "'";
					izvrsi(sqll); 
				}

			}
		}
			strsql = "Update tmpkarta set cu = c1 + c2 + c3 + c4 + c5 + c6 + c7" +
					" + c8 + c9 + c10 + c11 + c12, iu = i1 + i2 + i3 + i4 + i5 + i6 + i7" +
					" + i8 + i9 + i10 + i11 + i12 where pre = " + pred;
			izvrsi(strsql);

			strsql = "delete from tmpkarta where cu = 0 and iu = 0 and pre = " + pred;
			izvrsi(strsql);

			strsql = "Update tmpkarta set godina = " + godina;
			izvrsi(strsql);
	
			strsql = "update tmpkarta,maticnipodaci set tmpkarta.prezime = maticnipodaci.prezime," + 
					"tmpkarta.ime = maticnipodaci.ime where tmpkarta.sifra = maticnipodaci.sifra" +
					" and tmpkarta.pre = maticnipodaci.pre and maticnipodaci.juzer='" + juzer + "'";
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
