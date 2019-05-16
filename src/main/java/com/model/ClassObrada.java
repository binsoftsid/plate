package com.model;

import java.sql.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.model.ClassKrediti;
import com.model.ClassObradaPorez;
import com.model.ClassOlaksice;


/*
*	kod proseka se racuna sum(i22)/sum(c29) - ukupnobruto/ukupno sati za neto
*
*/

public class ClassObrada {
	
	//DataSource dataSource;
	private Connection connection = null;
	private int broj=0;
	private double topliobrok=0;
	private int mesec=0,brojobrade=99,sifra=0,vrstaplate=1;
	//promenljive za obradu
	private int red=0,kk,netoc,gs,ms,redx,siframz;
	private int sin,mesecaz,godinestaza=0,mesecistaza=0;
	public int[] cc =  new int[41];			//casovi
	private double[] bb = new double[41];	//bodovi
	private double[] ii = new double[41];	//iznosi
	private int fondsatizaobr=0,fondsatiumesecu=0;
	private int[] ldzbir =  new int[41];	//sta ulazi u zbir neto sati
	private String pred="1";
	//pomocni nizovi za koeficijente iz tabele "parametriobracuna"
	private int[] minu =  new int[22];		//koef. za minuli rad sta ulazi
	private int[] stim =  new int[22];		//koef. za stimulaciju
	private double[] ldkoef = new double[22];//koef. za uvecanje-smanjenje iznosa
	private int[] prosekkoef = new int[22];//koef. za prosek da li se koristi u obracunu
	private double koefminulirad=0,procenatplate=0,poreskaolaksica=0;
	private double stopasamodoprinosa=0,koefsindikat=0,koefsolidarnost=0,stopa27=0;
	private int clansindikata=0,solidarnost=1,krediti=0;
	private String godina,uslov="",juzer="";
	private ClassKrediti classkrediti1;
	private ClassObradaPorez classporez;
	private ClassOlaksice classolaksice;
	//akontacije
	private double uplacendoprinos=0,netoak=0,osndopak=0,osnbenak,dopbenak,pioum=0;
	private double svegaobustavaak=0,brutoak=0,uplacenporez=0,dodatniuplacen=0;
	private double minimalnaosnovica=0,maksimalnaosnovica=0,osnovicadoprinosa=0;
	private double brojbodova=0,vrednostboda=0,prosek=0;

	//da li se radi sa minimalnom osnovicom ili ne 0-radi, 1-ne radi
	private int minosn=0;

 public ClassObrada(Connection _conn,String _godina,String _pre) {
	 	
	 	connection = _conn;
	 	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        juzer = auth.getName();	
        juzer = juzer.trim();
	 	godina = _godina;
		pred = _pre;

		classkrediti1 = new ClassKrediti(connection,pred,juzer,godina ); 
		classporez = new ClassObradaPorez(connection,pred,juzer,godina);
		classolaksice = new ClassOlaksice(pred,juzer);
}
 //-----------------------------------------------------------------------------------------------------
 public void Obradi(int _brojobrade,int _mesec,int _sifra,int _vrstaplate,String _godina) {
		godina = _godina;
		mesec=_mesec;
		brojobrade=_brojobrade;
		sifra=_sifra;
		vrstaplate=_vrstaplate;

	//nulira promenljive za obradu
	nulirajPromenljive();

	//proverava postojanje podataka za obradu u tabeli "binmesec"
	if (proveriPostoji())
	{
		if (proveriPostojiRadnik(1))
		{
			//prvo brise obradu ako vec postoji za tog radnika
			obrisiObraduAkoPostoji();
		
			//uzima podatke za unos iz tabele "binmesec"
			uzmiVrednostiUnosaIzBinmesec();

			//uzmi podatke iz parametara
			uzmiKoefParametara();
		
			//OBRADI i unesi podatke u tabelu "ldmes"
			unesiPodatke();
		}else{
			//JOptionPane.showMessageDialog(null, "Ne postoje radnik ili nije pod tom vrstom plate");
		}
	}else{
		//JOptionPane.showMessageDialog(null, "Ne postoje ulazni podaci za obracun");
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
//-----------------------------------------------------------------------------------------------------
   public void nulirajPromenljive() {
		//stavlja sve vrednosti promenljivih na nulu
		for (int i=1;i<41 ;i++ )
		{
			cc[i] = 0;
			bb[i]=0;
			ii[i]=0;
			ldzbir[i]=0;
		}
		for (int i=1;i<22 ;i++ )
		{
			minu[i] = 0;
			ldkoef[i]=0;
			prosekkoef[i]=0;
			stim[i]=0;
		}
		stopasamodoprinosa=0;
		koefsindikat=0;
		koefsolidarnost=0;
		stopa27=0;
		
		fondsatizaobr=0;
		fondsatiumesecu=0;
		koefminulirad=0;
		topliobrok=0;
		procenatplate=0;
		poreskaolaksica=0;
		fondsatizaobr=0;
		fondsatiumesecu=0;
		procenatplate=0;
		poreskaolaksica=0;
		//akontacije za vise isplata u jednom mesecu
		uplacendoprinos=0; //i24 sa - predznakom
		netoak=0;
		osndopak=0;
		osnbenak=0;
		dopbenak=0;

		godinestaza=0;
		mesecistaza=0;

		pioum=0;	//olaksica
		svegaobustavaak=0; //i36
		brutoak=0;		//i34 sumira sum(i22) iz prethodnih obracuna
		uplacenporez=0;  // sumira (i25-obracunat porez iz prethodnih)
		dodatniuplacen=0;
		minimalnaosnovica = 0;
		maksimalnaosnovica=0;
		osnovicadoprinosa=0;
		brojbodova=0;
		vrednostboda=0;
		prosek=0;
		minosn=0;
  }
 //-----------------------------------------------------------------------------------------------------
   public boolean proveriPostoji() {
		boolean postoji=false;
		Statement statement=null;
      try {
         statement = connection.createStatement();
               String query = "SELECT * FROM binmesec WHERE sifra=" + sifra +
		            " AND mesec=" + mesec + " AND vrstaplate=" + vrstaplate +
					" AND brojobrade=" + brojobrade + " AND pre=" + pred +
				    " AND satiucinak=2 AND binmesec.juzer='" + juzer + "' AND godina=" + godina;

		         ResultSet rs = statement.executeQuery( query );
            		if(rs.next()){
						krediti=rs.getInt("krediti");
						brojobrade=rs.getInt("brojobrade");
						postoji = true;
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
		return postoji;
  }
 //-----------------------------------------------------------------------------------------------------
   public boolean proveriPostojiRadnik(int obradailivrati) {
		boolean postoji=false;
		Statement statement=null;
		/*
			* parametar obradailivrati je bitan zbog toga da se vidi koja funkcija poziva ovu jer
			* nije isto da li se poziva kod obrade tada dodaje mesec staza i utice na minuli rad ili 
			* ako vraca obradu onda ne sme dodavati mesec staza. Inace mesec staza se dodaje pre
			* obrade zbog obracuna minulog rada jer posto se plata radi za prosli mesec radnik tada
			* vec ima jedan mesec staza vise.
		*/

      try {
         statement = connection.createStatement();
               String query = "SELECT * FROM maticnipodaci WHERE sifra=" + sifra +
		            " AND pre=" + pred + " AND maticnipodaci.juzer='" + juzer + "'";

		         ResultSet rs = statement.executeQuery( query );
            		if(rs.next()){
						godinestaza = rs.getInt("gs");
						mesecistaza = rs.getInt("ms");
						postoji = true;
						siframz=rs.getInt("sifmz");
						clansindikata=rs.getInt("sindi");
						solidarnost=rs.getInt("soli");
						brojbodova=rs.getDouble("brbod");
						prosek=rs.getDouble("prosek");
					
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
		return postoji;
  }
//-----------------------------------------------------------------------------------------------------
   public void obrisiObraduAkoPostoji() {
	   String sql = "DELETE FROM ldmes WHERE pre=" + pred +
		   " AND mesec=" + mesec + " AND brojobrade=" + brojobrade +
		   " AND sifra=" + sifra + " and vrstaplate=" + vrstaplate + " AND ldmes.juzer='" + juzer + "'" +
		   " AND godina=" + godina;
	   izvrsi(sql);
  }
//-----------------------------------------------------------------------------------------------------
   public void uzmiVrednostiUnosaIzBinmesec() {
		String queryy="";
		Statement statement = null;
		try {
			statement = connection.createStatement();
				queryy = "SELECT * FROM binmesec WHERE sifra=" + sifra +
		            " AND mesec=" + mesec + " AND vrstaplate=" + vrstaplate +
					" AND brojobrade=" + brojobrade + " AND pre=" + pred +
					" AND satiucinak=2 AND binmesec.juzer='" + juzer + "' AND godina=" + godina;
					ResultSet rs = statement.executeQuery( queryy );
					if(rs.next()){
						vrednostboda=rs.getDouble("vrednostboda");

						for (int i=1;i<22 ; i++)
						{
							cc[i]=rs.getInt("c"+String.valueOf(i));
							bb[i]=(rs.getDouble("b"+String.valueOf(i)));
							//ovde su iznosi isto sto i bodovi
							//ii[i] = bb[i];
						}
						fondsatizaobr=rs.getInt("fondsatizaobracun");
						fondsatiumesecu=rs.getInt("fondsatiumesecu");
						topliobrok=rs.getDouble("topliobrok");
						procenatplate=rs.getDouble("procenat");
						poreskaolaksica=rs.getDouble("porolak");
						minosn=rs.getInt("minosn");
						if (procenatplate>0)
						{
							procenatplate = procenatplate/100;
						}

					}
      }
      catch ( SQLException sqlex ) {
    	  sqlex.printStackTrace();
      }
		finally{//*************************************************************************************
		if (statement != null){
			try{
				statement.close();
				statement = null;
			}catch (Exception e){
				e.printStackTrace();
			}}
		}//********************************************************************************************
  }
//-----------------------------------------------------------------------------------------------------
   public void uzmiKoefParametara() {
		String queryy="";
		int i=1;
		Statement statement = null;
		try {
			statement = connection.createStatement();
				queryy = "SELECT * FROM parametriobracuna WHERE pre=" + pred  + " AND parametriobracuna.juzer='" + juzer + "'" +
						" ORDER BY rbr";
					ResultSet rs = statement.executeQuery( queryy );
					while(rs.next()){
						if (i<22)
						{
							if (i==17)
							{
								koefminulirad=rs.getDouble("ldproc");
							}
							//ovo su koeficijenti sta se uzima u minuli rad
							minu[i] =  rs.getInt("ldminu");		
							//ovo su koeficijenti mnozenja pojedinih sati
							ldkoef[i] = rs.getDouble("ldkoef");
							//ovo su koeficijenti sta ulzai u prosek
							prosekkoef[i] = rs.getInt("prosek");
							//sta ulazi u zbir neto sati
							ldzbir[i] = rs.getInt("ldzbir");		
							//ovo su koeficijenti sta se uzima u stimulaciju
							stim[i] =  rs.getInt("ldstim");								
						}
						
						if (i==32)
						{
							koefsindikat=rs.getDouble("ldproc");
						}
						if (i==33)
						{
							koefsolidarnost=rs.getDouble("ldproc");
						}
						if (i==27)
						{
							//stopa za dodatni doprinos
							stopa27=rs.getDouble("ldproc");
						}
						
						i = i+1;
					}
      }
      catch ( SQLException sqlex ) {
    	  sqlex.printStackTrace();
      }
		finally{//*************************************************************************************
		if (statement != null){
			try{
				statement.close();
				statement = null;
			}catch (Exception e){
				e.printStackTrace();
			}}
		}//********************************************************************************************
  }
//-----------------------------------------------------------------------------------------------------
   public void unesiPodatke() {
	  //uslov za izmene podataka u ldmes
	   uslov=" WHERE ldmes.sifra=" + sifra + " AND ldmes.mesec=" + mesec + 
			" AND ldmes.vrstaplate=" + vrstaplate + " AND ldmes.brojobrade=" + brojobrade + 
			" AND ldmes.pre=" + pred + " AND ldmes.juzer='" + juzer + "' AND ldmes.godina=" + godina; 
	   
	 //prvo unosi osnovne podatke
	   String sql = "INSERT INTO ldmes(pre,mesec,brojobrade,vrstaplate,sifra,juzer,godina)" +
		   " VALUES(" + pred + "," + mesec + "," + brojobrade +
		   "," + vrstaplate + "," + sifra + ",'" + juzer + "'," + godina + ")";
	   izvrsi(sql);
  
		sql = "UPDATE ldmes SET ";
		for (int i=1;i<22 ;i++ )
		{
			sql = sql + "c" + String.valueOf(i) + "=" + cc[i] + ",";

			sql = sql + "b" + String.valueOf(i) + "=" + bb[i] + ",";
			
			if (cc[i] == 0 && bb[i] == 0)
			{
				sql = sql + "i" + String.valueOf(i) + "=0,";
			}else if (cc[i] > 0 && bb[i] == 0)
			{
				/*u ovom slucaju iznose po vrstama poslova racuna na osnovu proseka zadnju
				* godinu dana a to vidi iz parametara obracuna da li racuna na prosek ili ne
				* n.p.r. 7.Drzavni praznik kolona Prosek da-ne stoji broj 1 dok n.p.r. pod
				* 2.Po satima stoji 0.
				*/
				if (prosekkoef[i] == 1 && prosek>0)
				{
					sql = sql + "i" + String.valueOf(i) + "=" + 
					(cc[i]*prosek*ldkoef[i]*procenatplate) + ",";
				}else{
					sql = sql + "i" + String.valueOf(i) + "=" + 
					(cc[i]*brojbodova*vrednostboda*ldkoef[i])/fondsatiumesecu + ",";
				}
				//----------------------------------------------------------------------

			}else if (bb[i] > 0)
			{
				sql = sql + "i" + String.valueOf(i) + "=" + bb[i] + ",";
			}
		}
			sql = sql + "fondsatizaobr=" + fondsatizaobr + ",";
			sql = sql + "gs=" + godinestaza + ",";
			sql = sql + "fondsatiumesecu=" + fondsatiumesecu;
			sql = sql + uslov; 
	    izvrsi(sql);
		
		//prvo uzimam akontaciju (prethodne isplate u tom mesecu)
		//==================================================================
		//AKONTACIJE kad ima vise isplata u toku meseca
		uzmiAkontacije();
		sql = "UPDATE ldmes SET i24=" + uplacendoprinos + "," +
			"osnbenak=" + osnbenak + ",dopbenak=" +	dopbenak + "," +
			"osndopak=" + osndopak +",netoak=" + netoak + ",pioum=" + pioum +
			",i26=" + uplacenporez + ",i28=" + dodatniuplacen +",i34=" + brutoak;
		sql = sql + uslov; 
	    izvrsi(sql);
		//==================================================================
		//ubacujem poresku olaksicu i polje pio
		sql = "UPDATE ldmes SET osnporolak=" + poreskaolaksica + ",pio=osnporolak*p4"; 	
		sql = sql + uslov; 
	    izvrsi(sql);
		//--------------------------------------------------------------------
		//racunam topli obrok
		sql = "UPDATE ldmes SET i18=c18*" + topliobrok;
		sql = sql + uslov; 
	    izvrsi(sql);
		//--------------------------------------------------------------------
		//====================================================================
		//			STIMULACIJA
		//prvo nuliram stimulaciju
		sql = "UPDATE ldmes SET i16=0";
		sql = sql + uslov; 
	    izvrsi(sql);

		//racunam osnovicu stimulacije
		sql = "UPDATE ldmes SET osnstim=i1*" + stim[1] + 
			"+i2*" + stim[2] + "+i3*" + stim[3] + "+i4*" + stim[4] + 
			"+i5*" + stim[5] + "+i6*" + stim[6] + "+i7*" + stim[7] + 
			"+i8*" + stim[8] + "+i9*" + stim[9] + "+i10*" + stim[10] + 
			"+i11*" + stim[11] + "+i12*" + stim[12] + "+i13*" + stim[13] + 
			"+i14*" + stim[14] + "+i15*" + stim[15] + "+i16*" + stim[16] + 
			"+i18*" + stim[18] + "+i19*" + stim[19] + "+i20*" + stim[20] + 
			"+i21*" + stim[21];
		sql = sql + uslov; 
	    izvrsi(sql);
		//racunam iznos stimulacije gde je C16 dato u %
		sql = "UPDATE ldmes SET i16=((c16*osnstim)/100) ";
		sql = sql + uslov + " AND osnstim>0 AND c16>0"; 
	    izvrsi(sql);
	    //-----------------------------------------------------------------------
	    //racunam osnovicu minulog rada
		sql = "UPDATE ldmes SET osnmin=i1*" + minu[1] + 
			"+i2*" + minu[2] + "+i3*" + minu[3] + "+i4*" + minu[4] + 
			"+i5*" + minu[5] + "+i6*" + minu[6] + "+i7*" + minu[7] + 
			"+i8*" + minu[8] + "+i9*" + minu[9] + "+i10*" + minu[10] + 
			"+i11*" + minu[11] + "+i12*" + minu[12] + "+i13*" + minu[13] + 
			"+i14*" + minu[14] + "+i15*" + minu[15] + "+i16*" + minu[16] + 
			"+i18*" + minu[18] + "+i19*" + minu[19] + "+i20*" + minu[20] + 
			"+i21*" + minu[21];
		sql = sql + uslov; 
	    izvrsi(sql);
		//--------------------------------------------------------------------
		//racunam minuli rad za sve osim porodilja vrstaplate=3
		if (vrstaplate!=3)
		{
			sql = "UPDATE ldmes SET i17=osnmin*" + 
				"(" + koefminulirad + "*gs/100) ";
			sql = sql + uslov; 
		}else{
			sql = "UPDATE ldmes SET osnmin=0,i17=0";
			sql = sql + uslov; 
		}
	    izvrsi(sql);
		//ako je minuli rad manji od 0
		sql = "UPDATE ldmes SET i17=0 ";
		sql = sql + uslov + " AND i17<0"; 
	    izvrsi(sql);

		//--------------------------------------------------------------------
		//sabiram sate u polje c29 zbog izvestaja
		sql = "UPDATE ldmes SET c29=c1+c2";
		int i=3;
		for (i=3;i<38 ;i++ ){
			if (ldzbir[i] == 1)
			{
				sql = sql + "+c" + String.valueOf(i);
			}
		}
		//sql = "UPDATE ldmes SET c29=c1+c2+c3+c4+c5+c6+c7+c8+c9+c10+c11+c12+" +
		//	"c13+c14+c15+c16+c19+c20";
		sql = sql + uslov; 
	    izvrsi(sql);
		//--------------------------------------------------------------------
		//racunam bruto i osnovicu doprinosa
		sql = "UPDATE ldmes SET i22=i1+i2+i3+i4+i5+i6+i7+i8+i9+i10+i11+i12+i13+i14+i15"+
			"+i16+i17+i18+i19+i20+i21,osndop=i22";
		sql = sql + uslov; 
	    izvrsi(sql);
		proveriOsnovicuDoprinosa();
		//--------------------------------------------------------------------
		//racunam dodatni doprinos
		sql = "UPDATE ldmes SET i27=i22*" + stopa27;
		sql = sql + uslov; 
	    izvrsi(sql);
		//--------------------------------------------------------------------
		//ubacujem neke parametre iz maticnih podataka
		sql = "UPDATE ldmes,maticnipodaci SET ldmes.sifkval=maticnipodaci.sifkval, " + 
				"ldmes.sifban=maticnipodaci.sifban," +
				"ldmes.sifmz=maticnipodaci.sifmz," +
				"ldmes.partija=maticnipodaci.partija," +
				"ldmes.sindi=maticnipodaci.sindi," +
				"ldmes.isplata=maticnipodaci.isplata," +
				"ldmes.sifben=maticnipodaci.sifben," +
				"ldmes.br=maticnipodaci.br," +
				"ldmes.jur=maticnipodaci.jur," +
				"ldmes.grupa=2 " +
				uslov + " AND ldmes.sifra=maticnipodaci.sifra AND maticnipodaci.pre=" + pred +
				" AND maticnipodaci.juzer='" + juzer + "'";
	    izvrsi(sql);
		//--------------------------------------------------------------------
		//proverava da li radnik ima olaksice
		if (classolaksice.proveriOlaksicu(connection,sifra,mesec,godina)==true)
		{
			//ubacujem parametre iz tabele poreskih olaksica "plola"
			sql = "UPDATE ldmes SET ldmes.ind1=" + classolaksice.getInd1() +
				",ldmes.ind2=" + classolaksice.getInd2() +
				",ldmes.ind3=" + classolaksice.getInd3() +
				",ldmes.ind4=" + classolaksice.getInd4() +
				",ldmes.ind5=" + classolaksice.getInd5() +
				",ldmes.ind6=" + classolaksice.getInd6() +
				",ldmes.ind7=" + classolaksice.getInd7() +
				",ldmes.ind8=" + classolaksice.getInd8() +
				uslov;
			izvrsi(sql);
		}else{
			//ako nema olaksicu treba da stavi marker br=0 u tabeli ldmes
			sql = "UPDATE ldmes SET br=0 " + uslov;
			izvrsi(sql);
		}		
		//--------------------------------------------------------------------
		//ubacujem stope doprinosa i poreza(p4)
		sql = "UPDATE ldmes,doprinosi SET ldmes.p1=(doprinosi.procdop)/100 " + 
			uslov + " AND doprinosi.pre=1 AND doprinosi.sifdop=1 AND doprinosi.vrstaplate=" +
			vrstaplate;
	    izvrsi(sql);
		sql = "UPDATE ldmes,doprinosi SET ldmes.p2=(doprinosi.procdop)/100 " + 
			uslov + " AND doprinosi.pre=1 AND doprinosi.sifdop=2 AND doprinosi.vrstaplate=" +
			vrstaplate;
	    izvrsi(sql);
		sql = "UPDATE ldmes,doprinosi SET ldmes.p3=(doprinosi.procdop)/100 " + 
			uslov + " AND doprinosi.pre=1 AND doprinosi.sifdop=3 AND doprinosi.vrstaplate=" +
			vrstaplate;
	    izvrsi(sql);
		sql = "UPDATE ldmes,doprinosi SET ldmes.p11=(doprinosi.procdop)/100 " + 
			uslov + " AND doprinosi.pre=1 AND doprinosi.sifdop=4 AND doprinosi.vrstaplate=" +
			vrstaplate;
	    izvrsi(sql);
		sql = "UPDATE ldmes,doprinosi SET ldmes.p22=(doprinosi.procdop)/100 " + 
			uslov + " AND doprinosi.pre=1 AND doprinosi.sifdop=5 AND doprinosi.vrstaplate=" +
			vrstaplate;
	    izvrsi(sql);
		sql = "UPDATE ldmes,doprinosi SET ldmes.p33=(doprinosi.procdop)/100 " + 
			uslov + " AND doprinosi.pre=1 AND doprinosi.sifdop=6 AND doprinosi.vrstaplate=" +
			vrstaplate;
	    izvrsi(sql);
		//stopa doprinosa za porez
		sql = "UPDATE ldmes,doprinosi SET ldmes.p4=(doprinosi.procdop)/100 " + 
			uslov + " AND doprinosi.pre=1 AND doprinosi.sifdop=13 AND doprinosi.vrstaplate=" +
			vrstaplate;
	    izvrsi(sql);
		//stopa doprinosa za benef. r.s.
		sql = "UPDATE ldmes,doprinosi SET ldmes.pben=(doprinosi.procdop)/100 " + 
			uslov + " AND doprinosi.pre=1 AND doprinosi.sifdop=15 AND doprinosi.vrstaplate=" +
			vrstaplate;
	    izvrsi(sql);
		//--------------------------------------------------------------------
		//racunam obracunat doprinos i23
		//sql = "UPDATE ldmes SET i23=i22*(p1+p2+p3+p11+p22+p33)"; ??????????
		sql = "UPDATE ldmes SET i23=osndop*(p1+p2+p3)";
		sql = sql + uslov; 
	    izvrsi(sql);
		//--------------------------------------------------------------------
		//racunam osnovicu poreza(i22,osnpor) i iznos poreza(i25)
		//sql = "UPDATE ldmes SET osnpor=i22-"+ ((poreskaolaksica/procenatplate)*100) +
		
		classporez.ObradiPorez(brojobrade,mesec,sifra,vrstaplate);
		//--------------------------------------------------------------------
		//racunam beneficiran radni staz ide samo na redovan rad osim bolovanja
		//ne ide na bolov. preko 30 dana i porodilje
		if (vrstaplate!=2 && vrstaplate!=3)
		{
			sql = "UPDATE ldmes SET osnben=osndop-i13,dopben=(osnben-osnbenak)*pben";
			sql = sql + uslov + " AND sifben=1"; 
			izvrsi(sql);
		}
		//--------------------------------------------------------------------
		//racunam neto iznos i29
		sql = "UPDATE ldmes SET i29=i22-i23-i25";
		sql = sql + uslov; 
	    izvrsi(sql);
		//--------------------------------------------------------------------
		//racunam potrebna sredstva i37 (neto + dopr.radnika+dopr.poslodavca(37.8%)+porez +
		//dop.za benef.radni staz)
		//ukupni doprinosi(radnik+poslodavac)=(bruto-brutoaku)*(p1+p2+p3+p11+p22+p33)
		sql = "UPDATE ldmes SET i37=(i29-netoak)+(i25+i26)+(osndop-i34)*" +
			"(p1+p2+p3+p11+p22+p33)+(i27+i28)+(dopben+dopbenak)";
		sql = sql + uslov; 
	    izvrsi(sql);
		//--------------------------------------------------------------------
		//samodoprinos i30
		stopasamodoprinosa = PristupiMesnimZajednicama(siframz);  
		sql = "UPDATE ldmes SET i30=(i29-netoak)*"+stopasamodoprinosa;
		sql = sql + uslov; 
	    izvrsi(sql);
		//--------------------------------------------------------------------
		//KREDITI i31
		if (krediti==1)
		{
			double ukukrediti=0;
			classkrediti1.obradiKredite(brojobrade,mesec,sifra,vrstaplate);
			ukukrediti = classkrediti1.uzmiKrediteUkupno(brojobrade,mesec,sifra,vrstaplate);
			sql = "UPDATE ldmes SET i31="+ukukrediti;
			sql = sql + uslov; 
			izvrsi(sql);
		}
		//--------------------------------------------------------------------
		//sindikalna clanarina i32   (neto*koef)
		if (clansindikata==2)
		{
			if (koefsindikat>0)
			{
				sql = "UPDATE ldmes SET i32=(i29-netoak)*" + (koefsindikat/100);
				sql = sql + uslov; 
				izvrsi(sql);
			}
		}
		//--------------------------------------------------------------------
		//solidarnost clanarina i33   (neto*koef)
		if (solidarnost==2)
		{
			if (koefsolidarnost>0)
			{
				sql = "UPDATE ldmes SET i33=i29*" + (koefsolidarnost/100);
				sql = sql + uslov; 
				izvrsi(sql);
			}
		}
		//===================================================================
		//za isplatu i36 i ubacivanje polja osnporolak(poreskaolaksica) i polja
		//pio = poreskaolaksica * koeficijent poreza (p4)
		sql = "UPDATE ldmes SET i36=i29-i30-i31-i32-i33-"+netoak; 
		sql = sql + uslov; 
	    izvrsi(sql);
		//--------------------------------------------------------------------
		//svega obustava i35 
		sql = "UPDATE ldmes SET i35=i23+i24+i25+i26+i27+i28+i30+i31+i32+i33+i34";
		sql = sql + uslov; 
	    izvrsi(sql);
		//--------------------------------------------------------------------
		if (brojobrade==99)
		{
			dodajMesecStaza();
		}
		
		//==================================================================
  }
 //-----------------------------------------------------------------------------------------------------
 public void vratiObradu(int _brojobrade,int _mesec,int _sifra,int _vrstaplate,int _krediti) {
		
		mesec=_mesec;
		brojobrade=_brojobrade;
		sifra=_sifra;
		vrstaplate=_vrstaplate;

	//proverava postojanje podataka za brisanje obrade u tabeli "binmesec"
	if (proveriPostojiObrada())
	{
		if (proveriPostojiRadnik(2))
		{
			//brise obradu ako vec postoji za tog radnika
			obrisiObraduAkoPostoji();

			//ovo radi zbog uzimanja parametra za kredite da li vraca
			uzmiParametreUnosa();

			//ako je imao kredite vraca ih
			if (krediti==1)
			{
				classkrediti1.vratiKredite(brojobrade,mesec,sifra,vrstaplate);
			}
			//ako je konacna obrada vraca staz u maticnim podacima
			if (brojobrade==99)
			{
				vratiMesecStaza();
			}
		}
		
	}
  }
//-----------------------------------------------------------------------------------------------------
   private void uzmiParametreUnosa() {
		String queryy="";
		krediti=0;
		Statement statement = null;
		try {
			statement = connection.createStatement();
				queryy = "SELECT * FROM binmesec WHERE sifra=" + sifra +
		            " AND mesec=" + mesec + " AND vrstaplate=" + vrstaplate +
					" AND brojobrade=" + brojobrade + " AND pre=" + pred +
					" AND satiucinak=2 AND binmesec.juzer='" + juzer + "' AND godina=" + godina;
					ResultSet rs = statement.executeQuery( queryy );
					if(rs.next()){
						krediti=rs.getInt("krediti");
					}
      }
      catch ( SQLException sqlex ) {
			//JOptionPane.showMessageDialog(null, "Greska u uzmiParametreUnosa:"+sqlex);
			//JOptionPane.showMessageDialog(null, "sql:"+queryy);
      }
		finally{//*************************************************************************************
		if (statement != null){
			try{
				statement.close();
				statement = null;
			}catch (Exception e){
				//JOptionPane.showMessageDialog(null, "U:proveriSifm: Nije uspeo da zatvori statement");
			}}
		}//********************************************************************************************
  }
//-----------------------------------------------------------------------------------------------------
   public void vratiMesecStaza() {
	   if (mesecistaza>0)
	   {
			mesecistaza = mesecistaza - 1;
	   }else{
			godinestaza = godinestaza - 1;
			mesecistaza = mesecistaza + 12 -1;
	   }
	   String sql = "UPDATE maticnipodaci SET gs=" + godinestaza + 
			",ms=" + mesecistaza +   
			" WHERE pre=" + pred + " AND vrstaplate=" + vrstaplate +
		    " AND sifra=" + sifra + " AND maticnipodaci.juzer='" + juzer + "'";
	   izvrsi(sql);
  }
//-----------------------------------------------------------------------------------------------------
   public void dodajMesecStaza() {
		mesecistaza = mesecistaza + 1;
	   if (mesecistaza==12)
	   {
			godinestaza = godinestaza + 1;
			mesecistaza = 0;
	   }

	   String sql = "UPDATE maticnipodaci SET gs=" + godinestaza + 
			",ms=" + mesecistaza +   
			" WHERE pre=" + pred + " AND vrstaplate=" + vrstaplate +
		    " AND sifra=" + sifra + " AND maticnipodaci.juzer='" + juzer + "'";
	   izvrsi(sql);
  }
//-----------------------------------------------------------------------------------------------------
   public boolean proveriPostojiObrada() {
		boolean postoji=false;
		Statement statement=null;
      try {
         statement = connection.createStatement();
               String query = "SELECT * FROM ldmes WHERE sifra=" + sifra +
		            " AND mesec=" + mesec + " AND vrstaplate=" + vrstaplate +
					" AND brojobrade=" + brojobrade + " AND pre=" + pred +
					" AND ldmes.juzer='" + juzer + "' AND godina=" + godina;

		         ResultSet rs = statement.executeQuery( query );
            		if(rs.next()){
						postoji = true;
					}
      }
      catch ( SQLException sqlex ) {
			//JOptionPane.showMessageDialog(null, "Greska u proveriPostojiObrada:"+sqlex);
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
		return postoji;
  }
  //-----------------------------------------------------------------------------------
   public double PristupiMesnimZajednicama(int _sifmz) {
	  double procmz = 0;
	  Statement statement = null;
      try {
         statement = connection.createStatement();
         
                String query = "SELECT * FROM mesnezajednice " +
			    " where sifmz = " + _sifmz + " AND procmz>0 AND mesnezajednice.juzer='" + juzer + "'";
		        ResultSet rs = statement.executeQuery( query );
				if(rs.next()){
					procmz = rs.getDouble("procmz")/100;
				}else{
					procmz = 0;
				}

      }
      catch ( SQLException sqlex ) {
		//JOptionPane.showMessageDialog(null, "Greska u mesnim zajednicama:"+sqlex);
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
		return procmz;
  }
//------------------------------------------------------------------------------------------------------------------
    public void uzmiAkontacije() {
		String strsql;
		Statement statement = null;
			strsql = "SELECT @u1:=ROUND(SUM(i23),2), @u2:=ROUND(SUM(osnben),2)," +
				" @u3:=ROUND(SUM(dopben),2),@u4:=ROUND(SUM(osndop),2), " +
				"@u5:=ROUND(SUM(i29),2),@u6:=ROUND(SUM(pio),2),@u7:=ROUND(SUM(i36),2)," +
				"@u8:=ROUND(SUM(i22),2),@u9:=ROUND(SUM(i25),2)," +
				"@u10:=ROUND(SUM(i27),2) FROM ldmes " +
				" WHERE pre=" + pred + " AND mesec=" + mesec + " AND sifra=" +
				sifra + " AND brojobrade<" + brojobrade + " AND ldmes.juzer='" + juzer + "' AND godina=" + godina;
	  try {
         statement = connection.createStatement();
		        ResultSet rs = statement.executeQuery( strsql );
		        if(rs.next()){
					uplacendoprinos = (-1)*(rs.getDouble(1));
					osnbenak= rs.getDouble(2);
					dopbenak= rs.getDouble(3);
					osndopak=rs.getDouble(4);
					netoak=rs.getDouble(5);
					pioum = (-1)*(rs.getDouble(6));
					svegaobustavaak=rs.getDouble(7);
					brutoak = rs.getDouble(8);
					uplacenporez = (-1)*(rs.getDouble(9));
					dodatniuplacen=(-1)*(rs.getDouble(10));
					rs.close();
				}
      }
      catch ( SQLException sqlex ) {
		//JOptionPane.showMessageDialog(null, "Greska u uzmiAkontacije:"+sqlex);
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
  }
//------------------------------------------------------------------------------------------------------------------
    public void proveriOsnovicuDoprinosa() {
		
		int netocasova=0;  //ukupan fond sati za neto
		double minos=0.0;

		minimalnaosnovica = PristupiPoreskoj(1);
		maksimalnaosnovica = PristupiPoreskoj(2);
		osnovicadoprinosa = 0;

		netocasova = uzmiNetoCasove(); //ovde uzima i osnovicadoprinosa i neto casove

		minos = minimalnaosnovica*( ((double)netocasova)/((double)fondsatiumesecu) );


		if (minosn == 0)
		{
			if (netoc < fondsatiumesecu)
			{
				if ( osnovicadoprinosa < minos )
				{
					osnovicadoprinosa = minos;
					izmeniOsnovicuDoprinosa(osnovicadoprinosa);
				}
			}
			else{
				if (osnovicadoprinosa < minimalnaosnovica)
				{
					osnovicadoprinosa = minimalnaosnovica;
					izmeniOsnovicuDoprinosa(osnovicadoprinosa);
				}
			}
		}
		
		//ovo ne zavisi od broja efektivnih sati
		if (osnovicadoprinosa > maksimalnaosnovica && maksimalnaosnovica>minimalnaosnovica)
		{
				osnovicadoprinosa = maksimalnaosnovica;
				izmeniOsnovicuDoprinosa(osnovicadoprinosa);
		} 
 }
  //-----------------------------------------------------------------------------------------------------
   public void izmeniOsnovicuDoprinosa(double _osndop) {
	   //kod porodilja se ne primenjuje najniza osnovica doprinosa
	   if (vrstaplate!=3)
	   {
			String sql = "UPDATE ldmes SET osndop=" + _osndop + uslov;
			izvrsi(sql);
	   }
  }
//-----------------------------------------------------------------------------------
     public double PristupiPoreskoj(int _koja) {
	  //Provera postojanja pravila za prenos
		double osnovic = 0.00;
	  Statement statement = null;
      try {
         statement = connection.createStatement();
         
                String query = "SELECT * FROM poreskaskala where skala =" + _koja;
		        ResultSet rs = statement.executeQuery( query );
				if(rs.next()){
					osnovic = rs.getDouble("neta");
				}

      }
      catch ( SQLException sqlex ) {
		//JOptionPane.showMessageDialog(null, "Greska u PristupiPoreskoj:"+sqlex);
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
		return osnovic;
  }
  //-----------------------------------------------------------------------------------
     public int uzmiNetoCasove() {
	  //Provera postojanja pravila za prenos
		int casovi = 0;
	  Statement statement = null;
      try {
         statement = connection.createStatement();
         
                String query = "SELECT c29,i22 FROM ldmes " + uslov;
		        ResultSet rs = statement.executeQuery( query );
				if(rs.next()){
					casovi = rs.getInt("c29");
					osnovicadoprinosa = rs.getDouble("i22");
				}

      }
      catch ( SQLException sqlex ) {
		//JOptionPane.showMessageDialog(null, "Greska u uzmiNetoCasove:"+sqlex);
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
		return casovi;
  }
//===================================================================================== 
 }//end of class fPrintKarKon
