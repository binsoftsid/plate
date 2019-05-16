package com.model;

import java.sql.*;
import java.text.DecimalFormat;

import javax.servlet.ServletOutputStream;

import com.model.xmlClassOlaksice;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;


public class PrintXML {
	private Connection connection;
	private String juzer="",godina="",pPre="";
	private int mesec,vrstaplate,broj=99,tipisplatioca=1,rbr=0;
	private static OutputStreamWriter output;
	
	private xmlClassOlaksice pclol;
	private int brojdanaumesecu=0,brojobrade=99,vrstaprijave;
	private int MFP1=0,MFP2=0,MFP3=0,MFP4=0,MFP5=0,MFP6=0,MFP7=0,MFP8=0,MFP9=0,MFP10=0,MFP11=0,MFP12=0;
	private double ind1=0,ind2=0,ind3=0,ind4=0,ind5=0,ind6=0,ind7=0,ind8;
	private String PIB="",matbroj="",naziv="",telefon="",adresa="",mail="",strVrstaPrijave="";
	private String imeradnika="",prezimeradnika="",adresaradnika="",mestoradnika="",JMBG="";
	private double izn1=0,izn2=0,izn3=0,izn4=0,izn5=0,izn6=0,izn7=0,izn8=0,benefkoef;
	private String oznakazakonacnu="";
	private String pattern = "#########0.00",sifraopstine;
	private DecimalFormat myFormatter = new DecimalFormat(pattern);
	private String KlijentskaOznakaDeklaracije,ObracunskiPeriod,DatumPlacanja,NajnizaOsnovica;
	public static final String FILE_LOCATION="/xmlfiles";
	
	public PrintXML(Connection _connection,String _juzer,String _pred,String _godina,String _brojobrade,
			String _oznakazakonacnu,String _KlijentskaOznakaDeklaracije,String _ObracunskiPeriod,
			String _DatumPlacanja,String _NajnizaOsnovica,String _mesec) {
		connection = _connection;
		juzer = _juzer;
		pPre=String.valueOf(_pred);
		godina = _godina;
		brojobrade = Integer.parseInt(_brojobrade);
		oznakazakonacnu = _oznakazakonacnu;
		KlijentskaOznakaDeklaracije = _KlijentskaOznakaDeklaracije;
		ObracunskiPeriod = _ObracunskiPeriod;
		DatumPlacanja = _DatumPlacanja;
		NajnizaOsnovica = _NajnizaOsnovica;
		mesec = Integer.parseInt(_mesec);
	}
//------------------------------------------------------------------------------------------------------------------
		public void izvrsi(String sql) {
	      Statement statement = null;
		  try {
				statement = connection.createStatement();
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
//------------------------------------------------------------------------------------------------------------------
	public void napraviXML(String _vrstaplate,String _tipisplatioca,String _vrstaprijave){
		OutputStreamWriter izlaz;
			vrstaplate = Integer.parseInt(_vrstaplate);
			tipisplatioca = Integer.parseInt(_tipisplatioca);
			vrstaprijave = Integer.parseInt(_vrstaprijave);
			
			
			//proveri olaksice odnosno napravi privremenu tabelu za olaksice ako neko ima-----------------------
				pclol = new xmlClassOlaksice(pPre.trim());
				pclol.napraviTabelu(connection,pPre.trim(),brojobrade,String.valueOf(mesec),vrstaplate);
			//--------------------------------------------------------------------------------------------------
			brojdanaumesecu = brojDanaUMesecu(Integer.parseInt(godina),mesec);
			
			
			nulirajMFP();

			benefkoef = nadjiBenefKoef();


			//ubacivanje broja preduzeca u naziv .xml fajla
			String reportFileName = juzer + pPre.trim() + "ppp.xml";
			try {
				output = new OutputStreamWriter(new FileOutputStream(reportFileName),"UTF-8");
			} catch ( IOException ioe ) {
				ioe.printStackTrace();
			}
			writeHeader();
			PodaciOPrijavi();
			PodaciOIsplatiocu();
			PodaciOPrihodima();
			writeFooter();
			
			//return output;
		}
	//------------------------------------------------------------------------------------------------------------------
	private void nulirajMFP() 
	{
		MFP1=0;
		MFP2=0;
		MFP3=0;
		MFP4=0;
		MFP5=0;
		MFP6=0;
		MFP7=0;
		MFP8=0;
		MFP9=0;
		MFP10=0;
		MFP11=0;
		MFP12=0;
		izn1=0;
		izn2=0;
		izn3=0;
		izn4=0;
		izn5=0;
		izn6=0;
		izn7=0;
		izn8=0;//benef.r.s.

		ind1=0;
		ind2=0;
		ind3=0;
		ind4=0;
		ind5=0;
		ind6=0;
		ind7=0;
		ind8=0;//benef.r.s.

	}
	//------------------------------------------------------------------------------------------------------------------
	    protected int brojDanaUMesecu(int _god,int _mes) {
			int brdanaumes=0;
			
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
	//------------------------------------------------------------------------------------------------------------------
	private void writeHeader() 
	{
		try {
			output.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
			output.write("<tns:PodaciPoreskeDeklaracije xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""+
				" xmlns:tns=\"http://pid.purs.gov.rs\" xsi:schemaLocation=\"http://pid.purs.gov.rs\">\n");
		} catch ( java.io.IOException ioe ) {
			ioe.printStackTrace();
		}
	}
	//------------------------------------------------------------------------------------------------------------------
	private void PodaciOPrijavi() 
	{
		
		int kojavrsta = vrstaprijave;
		strVrstaPrijave=String.valueOf(kojavrsta);
		
		try {
			output.write("<tns:PodaciOPrijavi>\n");
			
			output.write("<tns:KlijentskaOznakaDeklaracije>"+ KlijentskaOznakaDeklaracije + "</tns:KlijentskaOznakaDeklaracije>\n");
			output.write("<tns:VrstaPrijave>" + strVrstaPrijave + "</tns:VrstaPrijave>\n");			// 1
			output.write("<tns:ObracunskiPeriod>" + ObracunskiPeriod + "</tns:ObracunskiPeriod>\n");	//2013-04
			
			//ako je konacna isplata prikazuje TAG ako nije ne prikazuje ga uopste
			if (oznakazakonacnu.trim().equals("K"))
			{
				output.write("<tns:OznakaZaKonacnu>" + oznakazakonacnu + "</tns:OznakaZaKonacnu>\n"); //K
			}
			
			output.write("<tns:DatumPlacanja>" + DatumPlacanja + "</tns:DatumPlacanja>\n"); //2014-05-10
			output.write("<tns:NajnizaOsnovica>" + NajnizaOsnovica + "</tns:NajnizaOsnovica>\n"); //1

			output.write("</tns:PodaciOPrijavi>\n");
			
		} catch ( java.io.IOException ioe ) {
			ioe.printStackTrace();
		}
	}
	//------------------------------------------------------------------------------------------------------------------
	private void PodaciOIsplatiocu() {

		nadjiPreduzece();
		String brojzaposlenih = nadjiBrojZaposlenih();

		try {
			output.write("<tns:PodaciOIsplatiocu>\n");
			
			output.write("<tns:TipIsplatioca>" + String.valueOf(tipisplatioca) + "</tns:TipIsplatioca>\n");
			output.write("<tns:PoreskiIdentifikacioniBroj>" + PIB.trim() + "</tns:PoreskiIdentifikacioniBroj>\n");
			
			if (oznakazakonacnu.trim().equals("K"))
			{
				output.write("<tns:BrojZaposlenih>" +  brojzaposlenih + "</tns:BrojZaposlenih>\n"); //K
			}
			
			output.write("<tns:MaticniBrojisplatioca>" + matbroj.trim() + "</tns:MaticniBrojisplatioca>\n");
			output.write("<tns:NazivPrezimeIme>" + naziv.trim() + "</tns:NazivPrezimeIme>\n");
			output.write("<tns:SedistePrebivaliste>" + sifraopstine + "</tns:SedistePrebivaliste>\n");
			output.write("<tns:Telefon>" + telefon.trim() + "</tns:Telefon>\n");
			output.write("<tns:UlicaIBroj>" + adresa.trim() + "</tns:UlicaIBroj>\n");
			output.write("<tns:eMail>" + mail.trim() + "</tns:eMail>\n");

			output.write("</tns:PodaciOIsplatiocu>\n");
			
			output.write("<tns:DeklarisaniPrihodi>\n");
			
		} catch ( java.io.IOException ioe ) {
			ioe.printStackTrace();
		}
	}
	//------------------------------------------------------------------------------------------------------------------
	private void PodaciOPrihodima() 
	{
		String sql="",strOsndop="",SVP="";
		String prezime="",ime="",strBruto="",strOsnpor="",strPorez="",strPio="",strZdr="",strNezap="",strPioBen="";
		int brsati=0,brsativrem,brsatiucinak,sifra=0,olaksice=0,sifben,kalbrdana=0,intosndopak,intosnporolak;
		int intbrutoak;
		String strKalbrdana="",strOsndopak,strOsnporolak,strBrutoak;
		double bruto = 0,osnpor=0,procpor=0,porez,osndop,p1,p2,p3,pio=0,zdr=0,nezap=0,p11,p22,p33,pioben,dblosndopak;
		broj = brojobrade;
		double vrednostMFP1=0,vrednostMFP2=0,vrednostMFP4=0,vrednostMFP6=0,vrednostMFP12=0;
		double dblosnporolak=0,dblosnporolakakont=0,dblbrutoak=0,iznosbolovanja=0,procenatangazovanja=0,bolakontacija=0.0;
		int fondsatiumesecu=0,brsatibolovanjado30;
		String sifopstine;
		double koeficijent=1;
		double koeficijent1=0.0;

		
		//definisanje varijabli za izracunavanje koeficijenata neoporezivog dela kod bolovanja do 30 dana
		double dbsati=0;
		double dbbrsatibolovanjado30 = 0;
		double dbfondsatiumesecu = 0;

		//provera da li ima prethodnih isplata za taj mesec
		boolean postojeprethispl = false;	
		postojeprethispl = postojePrethodneIsplate(mesec);
		
		rbr = 1;
		sql = "SELECT c1,c2,c13,c29,sifra,ROUND(i22,2),i34,ROUND(osnpor,2),ROUND(osnporolak,2),fondsatiumesecu,ROUND((osndop-osndopak),2) as oss,"+
				"p1,p2,p3,p4,br,sifben,p11,p22,p33,ROUND(oo+ou,2),ROUND(i25,2),ROUND(dopben,2)," +
				"vrstaplate,ROUND(osndopak,2),ROUND(i13,2) FROM ldmes WHERE mesec=" + mesec + 
				" and pre=" + pPre.trim() + " and vrstaplate=" + vrstaplate + " and br!=23 and brojobrade=" + brojobrade + 
				" AND juzer='" + juzer + "' AND godina=" + godina;
		  
		  Statement statement = null;
	      try {
	        statement = connection.createStatement();
	               
	        ResultSet rs = statement.executeQuery( sql );
			int i = 1;
	        while ( rs.next() ) {	//=====================================================

	               brsati = rs.getInt("c29");
				   brsatibolovanjado30 = rs.getInt("c13");

	               sifra = rs.getInt("sifra");
	               bruto = rs.getDouble("ROUND(i22,2)");
	               iznosbolovanja = rs.getDouble("ROUND(i13,2)");
	               osnpor = rs.getDouble("ROUND(oo+ou,2)");
	               osndop = rs.getDouble("oss");
	               procpor = rs.getDouble("p4");
	               dblosndopak = rs.getDouble("ROUND(osndopak,2)");
				   fondsatiumesecu = rs.getInt("fondsatiumesecu");
	               dblbrutoak = rs.getDouble("i34");
				   dblosnporolak = rs.getDouble("ROUND(osnporolak,2)");
				   

				   //uzima poresku olaksicu kumulativno za taj mesec do ove isplate
				   dblosnporolakakont = uzmiPorOlakKumul(mesec,sifra);
				   
				   //ako postoje akontacije onda osnporolak treba uzeti kumulativno iz ldmesprvi za
				   //taj mesec
				   //22-04-2015 odsad ovo izbaceno - por. olaksica se stavlja za svaki deo plate onoloko kolika jeste
				   
				   if (dblosndopak>0 && vrstaplate !=4) //ne vazi za penzionere
				   {
						dblosnporolakakont = uzmiPorOlakKumul(mesec,sifra);
						dblosnporolak = dblosnporolak - dblosnporolakakont;
				   }
				  
				//ako je konacna isplata i postoje akontacije moraju se bruto i osnovice umanjiti za akontacije
				//osndop = bruto;
				if (vrstaplate == 3) { //u slucaju porodilja
					if (broj == 99 )
					{
						bruto = bruto - dblbrutoak;
					}
				}else {
					if (broj == 99 && postojeprethispl==true)
					{
						bruto = bruto - dblbrutoak;
						osnpor = bruto - dblosnporolak;
					}
					
				}
			//ako ima bolovanje do 30 dana onda se menja osn.poreske olaksice odnosno deli se procentualno
			//po satima za redovan rad i bolovanje
			if (brsatibolovanjado30>0 && oznakazakonacnu.trim().equals("K"))
			{
				dbsati = (double)brsati;
				dbbrsatibolovanjado30 = (double)brsatibolovanjado30;
				dbfondsatiumesecu = (double)fondsatiumesecu;

				if (vrstaplate == 1) {  
						bolakontacija = 0.0;
						koeficijent =(dbsati-dbbrsatibolovanjado30)/(dbfondsatiumesecu);
						bolakontacija = nadjiAkontacijuBolovanjaDo30(mesec,sifra);
						bruto = bruto - (iznosbolovanja - bolakontacija);
						brsati = brsati - brsatibolovanjado30;
					 	osnpor = bruto - (dblosnporolak*koeficijent);
					}else if (vrstaplate == 2) {
						koeficijent =(dbsati-dbbrsatibolovanjado30)/(dbfondsatiumesecu);
						bruto = bruto - iznosbolovanja;
						brsati = brsati - brsatibolovanjado30;
						osnpor = bruto - (dblosnporolak*koeficijent);
					}else if (vrstaplate == 3) {
						  if ((dbsati-dbbrsatibolovanjado30)<(dbfondsatiumesecu))
						  {
							  koeficijent =(dbsati-dbbrsatibolovanjado30)/(dbfondsatiumesecu);
						  }
						  osnpor = bruto - (dblosnporolak*koeficijent);
					}else if (vrstaplate == 4) {
						  koeficijent =(dbsati-dbbrsatibolovanjado30)/(dbfondsatiumesecu);
						  bruto = bruto - iznosbolovanja;
						  brsati = brsati - brsatibolovanjado30;
						  osnpor = bruto - (dblosnporolak*koeficijent);
					}
			 	osndop = bruto;
	        }
			p1 = rs.getDouble("p1");
	               p2 = rs.getDouble("p2");
	               p3 = rs.getDouble("p3");
	               p11 = rs.getDouble("p11");
	               p22 = rs.getDouble("p22");
	               p33 = rs.getDouble("p33");
				   pioben = rs.getDouble("ROUND(dopben,2)");

	               olaksice = rs.getInt("br");
	               sifben = rs.getInt("sifben");
	               vrstaplate = rs.getInt("vrstaplate");
				   
				   pio = (osndop * p1) + (osndop * p11);
				   zdr =  (osndop * p2) + (osndop * p22);
				   nezap = (osndop * p3) + (osndop * p33);

				   porez = osnpor * procpor;

			
			//ako ima olaksice umanjuje porez i doprinose
			if (olaksice>0)
			{
				//uzima parametre olaksica
				proveriOlaksice(sifra);
				pio = pio - (osndop*p1*(ind1/100)) - (osndop*p11*(ind4/100));
				zdr = zdr - (osndop*p2*(ind2/100)) - (osndop*p22*(ind5/100));
				nezap = nezap - (osndop*p3*(ind3/100)) - (osndop*p33*(ind6/100));
				porez = porez - (porez*(ind7/100));
			}
				   //porez = rs.getDouble("ROUND(i25,0)");

					
				   //UBACENO 31.01.2014 zbog BEN staza da mnozi sa osnovicom doprinosa celom bez
				   //obzira na regres i t.o.
				   if (pioben>0 && benefkoef>0)
				   {
					   pioben = osndop * (benefkoef/100);
					   pioben = ZaokruziBroj(pioben,2);
				   }
					
					
					//ako ima olaksice moramo umanjiti porez i doprinose sa vrednostima iz formirane
					//tabele tmpolaksice po sifri radnika a to su izn4,izn5,izn6 i porez izn7 odnosno
					//to su olaksice za doprinose koje placa poslodavac

				  //moraju se zaokruziti doprinosi
				   porez = ZaokruziBroj(porez,2);
				   pio = ZaokruziBroj(pio,2);
				   zdr = ZaokruziBroj(zdr,2);
				   nezap = ZaokruziBroj(nezap,2);

				   strBruto = myFormatter.format(bruto);
				   strOsnpor = myFormatter.format(osnpor);
				   strOsndop = myFormatter.format(osndop);
				   strPorez = myFormatter.format(porez);
				   strPio = myFormatter.format(pio);
				   strZdr = myFormatter.format(zdr);
				   strNezap = myFormatter.format(nezap);
				   strPioBen = myFormatter.format(pioben);

				   uzmiPodatkeRadnika(sifra);

				   kalbrdana = uzmiKalendarskiBrojDana(sifra,mesec,brojobrade);

				   strKalbrdana = String.valueOf(kalbrdana);

				   procenatangazovanja = uzmiProcenatAngazovanja(sifra,mesec,broj);


				   sifopstine = nadjiOpstinu(sifra);

	//***************************************************************************************************
	/* ispitivanje da li ima neki bruto i da li ima broj sati redovnog rada
	 * ako nema onda preskace stavku redovnog rada u XML fajlu
	 */
		if (bruto>0 && brsati>0)
		{


				
				try {
					output.write("<tns:PodaciOPrihodima>\n");
			
					output.write("<tns:RedniBroj>" + String.valueOf(rbr) + "</tns:RedniBroj>\n");
					
					//1-JMBG, 2-Izbeglicka legit. 3-Pasos 4-Posebna oznaka 9-Ostalo
					output.write("<tns:VrstaIdentifikatoraPrimaoca>" + "1" + "</tns:VrstaIdentifikatoraPrimaoca>\n");
					output.write("<tns:IdentifikatorPrimaoca>" + JMBG.trim() + "</tns:IdentifikatorPrimaoca>\n");
					output.write("<tns:Prezime>" + prezimeradnika.trim() + "</tns:Prezime>\n");
					output.write("<tns:Ime>" + imeradnika.trim() + "</tns:Ime>\n");
					output.write("<tns:OznakaPrebivalista>" + sifopstine.trim() + "</tns:OznakaPrebivalista>\n");
					
					
					//kreiranje SVP broja
					SVP = "1";
					if (sifra == 1)
					{
						SVP = SVP + "02";
					}else{
						SVP = SVP + "01";
					}
					
					
					if (vrstaplate == 1)
					{
						SVP = SVP + "101";
					}else if (vrstaplate == 2)
					{
						SVP = SVP + "204";
					}else if (vrstaplate == 3)
					{
						SVP = SVP + "206";
					}else if (vrstaplate == 4)
					{
						SVP = "109101";
					}
				
					
					//olaksice da li ima
					switch (olaksice)
					{
						case 0:
							SVP = SVP + "00";
							break;
						case 3:
							SVP = SVP + "01";
							break;
						case 4:
							SVP = SVP + "02";
							break;
						case 5:
							SVP = SVP + "03";
							break;
						case 2:
							SVP = SVP + "04";
							break;
						case 1:
							SVP = SVP + "05";
							break;
						case 21:
							SVP = SVP + "06";
							break;
						case 22:
							SVP = SVP + "07";
							break;
						case 23:
							SVP = SVP + "08";
							break;
						//kod penzionera ide druga sifra kompletna
						case 25:
							SVP = "10910100";
							break;
						default:
							SVP = SVP + "00";
					}
					//beneficiran radni staz
					switch (sifben)
					{
						case 0:
							SVP = SVP + "0";
							break;
						case 1:
							SVP = SVP + "1";
							break;
						case 2:
							SVP = SVP + "2";
							break;
						case 3:
							SVP = SVP + "3";
							break;
						case 4:
							SVP = SVP + "4";
							break;
						default:
							SVP = SVP + "0";
					}
					output.write("<tns:SVP>" + SVP + "</tns:SVP>\n");

				//dodato 02.08.2014 da broj efekt.sati ne moze biti veci od Mesecnog fonda sati
				if (brsati>fondsatiumesecu)
				{
					brsati=fondsatiumesecu;
				}
				
				if (oznakazakonacnu.trim().equals("K"))
				{
					
					//ovo se upisuje samo za konacnu isplatu
					output.write("<tns:BrojKalendarskihDana>" + strKalbrdana + "</tns:BrojKalendarskihDana>\n");
					output.write("<tns:BrojEfektivnihSati>" + String.valueOf(brsati) + "</tns:BrojEfektivnihSati>\n");
					output.write("<tns:MesecniFondSati>" + String.valueOf(fondsatiumesecu) + "</tns:MesecniFondSati>\n");
				}
					
					output.write("<tns:Bruto>" + strBruto + "</tns:Bruto>\n");
					output.write("<tns:OsnovicaPorez>" + strOsnpor + "</tns:OsnovicaPorez>\n");
					output.write("<tns:Porez>" + strPorez + "</tns:Porez>\n");
					output.write("<tns:OsnovicaDoprinosi>" + strOsndop + "</tns:OsnovicaDoprinosi>\n");
					output.write("<tns:PIO>" + strPio + "</tns:PIO>\n");
					output.write("<tns:ZDR>" + strZdr + "</tns:ZDR>\n");
					output.write("<tns:NEZ>" + strNezap + "</tns:NEZ>\n");
					output.write("<tns:PIOBen>" + strPioBen + "</tns:PIOBen>\n");
				
				if (dblosndopak>0)
				{
					//nadjiMFPVrednosti();
					//ovo se upisuje samo za konacnu isplatu
					output.write("<tns:DeklarisaniMFP>" + "\n");
							
							/* Kod zarade (naknade zarade) ukupan neoporezivi iznos 
							*  iskoriscen u prethodnim prijavama pod istom sifrom vrste prihoda
							*  za isti obracunski period *ldmes-osndopak*
							*/
						output.write("<tns:MFP>\n");
							output.write("<tns:Oznaka>" + "MFP.1" + "</tns:Oznaka>\n");
							output.write("<tns:Vrednost>" + myFormatter.format(dblosnporolakakont) + "</tns:Vrednost>\n");
						output.write("</tns:MFP>\n");
						//MFP1 = intosnporolak;
							
							/* Kod zarade (naknade zarade) ukupan iznos osnovice na koju su
							*  obracunati doprinosi u prethodnim prijavama pod istom sifrom vrste prihoda
							*  za isti obracunski period *ldmes-osndopak*
							*/
						output.write("<tns:MFP>\n");
							output.write("<tns:Oznaka>" + "MFP.2" + "</tns:Oznaka>\n");
							output.write("<tns:Vrednost>" + myFormatter.format(dblosndopak) + "</tns:Vrednost>\n");
						output.write("</tns:MFP>\n");
						//MFP2 = intosndopak;
							
							/* Kod zarade (naknade zarade) ukupan prethodno prijavljeni iznos zarade
							*  u prethodnim prijavama pod istom sifrom vrste prihoda
							*  za isti obracunski period *ldmes-osndopak*
							*/
						output.write("<tns:MFP>\n");
							output.write("<tns:Oznaka>" + "MFP.4" + "</tns:Oznaka>\n");
							output.write("<tns:Vrednost>" + myFormatter.format(dblbrutoak) + "</tns:Vrednost>\n");
						output.write("</tns:MFP>\n");
						//MFP4 = intbrutoak;
							
							
						output.write("<tns:MFP>\n");
							output.write("<tns:Oznaka>" + "MFP.11" + "</tns:Oznaka>\n");
							output.write("<tns:Vrednost>" + myFormatter.format(dblosnporolakakont) + "</tns:Vrednost>\n");
						output.write("</tns:MFP>\n");
						output.write("<tns:MFP>\n");
							output.write("<tns:Oznaka>" + "MFP.12" + "</tns:Oznaka>\n");
							output.write("<tns:Vrednost>" + myFormatter.format(dblosndopak) + "</tns:Vrednost>\n");
						output.write("</tns:MFP>\n");
						//MFP12 = intosndopak;
					output.write("</tns:DeklarisaniMFP>" + "\n");
				}
				
				//ako ima bolovanja do 30 dana mora se dodati MFP10 = 1
				if (brsatibolovanjado30>0)
				{
					output.write("<tns:DeklarisaniMFP>" + "\n");
						output.write("<tns:MFP>\n");
							output.write("<tns:Oznaka>" + "MFP.10" + "</tns:Oznaka>\n");
							output.write("<tns:Vrednost>" + "1" + "</tns:Vrednost>\n");
						output.write("</tns:MFP>\n");
					output.write("</tns:DeklarisaniMFP>" + "\n");
				}

				//ako ima procenat angazovanja sa nepunim radnim vremenom
				if (procenatangazovanja>0 && oznakazakonacnu.trim().equals("K"))
				{
					output.write("<tns:DeklarisaniMFP>" + "\n");
						output.write("<tns:MFP>\n");
							output.write("<tns:Oznaka>" + "MFP.3" + "</tns:Oznaka>\n");
							output.write("<tns:Vrednost>" + myFormatter.format(procenatangazovanja) + "</tns:Vrednost>\n");
						output.write("</tns:MFP>\n");
						output.write("<tns:MFP>\n");
							output.write("<tns:Oznaka>" + "MFP.8" + "</tns:Oznaka>\n");
							output.write("<tns:Vrednost>" + "1" + "</tns:Vrednost>\n");
						output.write("</tns:MFP>\n");

					output.write("</tns:DeklarisaniMFP>" + "\n");
				}
					output.write("</tns:PodaciOPrihodima>\n");
				//** kraj podataka o prihodima *******************************************************************************
		
		
		
					//unosi podatke u tabelu eporezi za svakog radnika
					//unesiPodatkeUTabelu(sifra,kalbrdana,brsati,bruto,osnpor,osndop,porez,pio,zdr,nezap,pioben,
					//	SVP,fondsatiumesecu,sifopstine);
			
				} catch ( java.io.IOException ioe ) {
					ioe.printStackTrace();
				}

		}else{			//kraj if bruto>0
			rbr = rbr - 1;
		}
		//******************************************************************************************
				//ispitivanje da li ima bolovanje do 30 dana posto se u tom slucaju dodaje ceo TAG
				//PodaciOPrihodima za tog istog radnika razdvojeno po sifri SVP placanja 201***********
					if (brsatibolovanjado30>0 && oznakazakonacnu.trim().equals("K") )
					{
						dbbrsatibolovanjado30 = (double)brsatibolovanjado30;
						dbfondsatiumesecu = (double)fondsatiumesecu;
						
						koeficijent1 = dbbrsatibolovanjado30/dbfondsatiumesecu;
						
						//ovo se uzima jer treba ponovo preracunati udeo por.olaks. za bolovanje
						dblosnporolak = (rs.getDouble("ROUND(osnporolak,2)"))*koeficijent1;

						BolovanjeDo30(sifra,sifopstine,kalbrdana,brsatibolovanjado30,
							fondsatiumesecu,iznosbolovanja,
							p1,p2,p3,p11,p22,p33,procpor,
							dblosnporolak,
							dblosndopak,
							dblbrutoak,
							dblosnporolakakont,bolakontacija,
							koeficijent1,olaksice,brsati,bruto
							);
					}

				//*********************************************************************************

				rbr = rbr + 1;
	          } //kraj petlje WHILE ====================================================

	        }catch ( SQLException sqlex ) {
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
	//------------------------------------------------------------------------------------------------------------------
	   private void BolovanjeDo30(int _sifra,String _sifopstine,int _kalbrdana,int _brsatibolovanja,
								int _fondsatiumesecu,double _iznosbolovanja,
								double _p1, double _p2, double _p3,
								double _p11, double _p22, double _p33,
								double _procpor,
								double _dblosnporolak,
								double _dblosndopak,
								double _dblbrutoak,
								double dblosnporolakakont,
								double iznosbolovanjaakont,
								double _koeficijent1,
								int _olaksice,
								int _brsati,double _bruto)
	   {

	 	String sql="",strOsndop="",SVP="";
		String prezime="",ime="",mesec="0",strBruto="",strOsnpor="",strPorez="",strPio="",strZdr="",strNezap="",strPioBen="";
		int brsati=0,brsativrem,sifra=0,olaksice=0,sifben=0,kalbrdana=0,vrstaplate=1,intosndopak=0;
		int intosnporolak=0,intbrutoak=0,kalbrdanabol=0;
		
		String strKalbrdana="",strOsndopak="",strOsnporolak="",strBrutoak="";
		
		double bruto = 0,osnpor=0,procpor=0,porez=0,osndop=0,p1=0,p2=0,p3=0,pio=0,zdr=0,nezap=0,p11=0,p22=0,p33=0,pioben=0,dblosndopak;
		broj = brojobrade;
		double vrednostMFP1=0,vrednostMFP2=0,vrednostMFP4=0,vrednostMFP6=0,vrednostMFP12=0;
		double dblosnporolak,dblbrutoak,iznosbolovanja;
		int fondsatiumesecu=0,brsatibolovanja;
		String sifopstine;

		//preuzimanje vrednosti promenljivih iz gornjih podataka ---------------------
		
		//dblosnporolakakont = uzmiPorOlakKumul(mesec,sifra);
		
		
		sifopstine = _sifopstine;
		brsatibolovanja = _brsatibolovanja;
		fondsatiumesecu = _fondsatiumesecu;
		iznosbolovanja = _iznosbolovanja - iznosbolovanjaakont;
		dblosnporolak = _dblosnporolak;
		dblosndopak = _dblosndopak;
		dblbrutoak = _dblbrutoak;
		olaksice = _olaksice;

		kalbrdanabol = brojdanaumesecu - _kalbrdana;
		strKalbrdana = String.valueOf(kalbrdanabol);

		p1 = _p11;
		
		procpor = _procpor;


		//osnovica poreza je bruto manje deo za poresku olaksicu
		//ako radnik ima samo bolovanje do 30 a nema redovnog rada uzima se ukupan
		//bruto (zajedno sa minulim radom i ostalim uplatama - regres i sl.)
		if (_brsati==0)
		{
			osnpor = _bruto + iznosbolovanja - (dblosnporolak-(dblosnporolakakont*_koeficijent1));
			osndop = _bruto + iznosbolovanja;		
		}else{
			osnpor = iznosbolovanja - (dblosnporolak-(dblosnporolakakont*_koeficijent1));
			osndop = iznosbolovanja;		//dobijeno iz (i13)
		}

				
			pio = (osndop * _p1) + (osndop * _p11);
			zdr =  (osndop * _p2) + (osndop * _p22);
			nezap = (osndop * _p3) + (osndop * _p33);
	   	    porez = osnpor * _procpor;
				
			//ako ima olaksice umanjuje porez i doprinose
			if (olaksice>0)
			{
				pio = pio - (osndop*_p1*(ind1/100)) - (osndop*_p11*(ind4/100));
				zdr = zdr - (osndop*_p2*(ind2/100)) - (osndop*_p22*(ind5/100));
				nezap = nezap - (osndop*_p3*(ind3/100)) - (osndop*_p33*(ind6/100));
				porez = porez - (porez*(ind7/100));
			}
				
				//racunam doprinose
									
				//moraju se zaokruziti doprinosi
				   pio = ZaokruziBroj(pio,2);
				   zdr = ZaokruziBroj(zdr,2);
				   nezap = ZaokruziBroj(nezap,2);
				   
				//porez
				   porez = ZaokruziBroj(porez,2);

				//formatiranje vrednosti za prikaz
				   strOsnpor = myFormatter.format(osnpor);
				   strOsndop = myFormatter.format(osndop);

				   strPorez = myFormatter.format(porez);
				   strPio = myFormatter.format(pio);
				   strZdr = myFormatter.format(zdr);
				   strNezap = myFormatter.format(nezap);
				
				//Verovatno nema benef. radni staz ako je na bolovanju proveriti ????????
					pioben = 0.0;
				   strPioBen = myFormatter.format(pioben);

		//-----------------------------------------------------------------------------
		//povecava redni broj za 1
		rbr = rbr + 1;
		
				try {
					output.write("<tns:PodaciOPrihodima>\n");
			
					output.write("<tns:RedniBroj>" + String.valueOf(rbr) + "</tns:RedniBroj>\n");
					
					//1-JMBG, 2-Izbeglicka legit. 3-Pasos 4-Posebna oznaka 9-Ostalo
					output.write("<tns:VrstaIdentifikatoraPrimaoca>" + "1" + "</tns:VrstaIdentifikatoraPrimaoca>\n");
					
					//JMBG,prezimeradnika,imeradnika vec ima vrednost uzeta za tog radnika posto je public promenljiva
					output.write("<tns:IdentifikatorPrimaoca>" + JMBG.trim() + "</tns:IdentifikatorPrimaoca>\n");
					output.write("<tns:Prezime>" + prezimeradnika.trim() + "</tns:Prezime>\n");
					output.write("<tns:Ime>" + imeradnika.trim() + "</tns:Ime>\n");
					output.write("<tns:OznakaPrebivalista>" + sifopstine.trim() + "</tns:OznakaPrebivalista>\n");
					//kreiranje SVP broja
					SVP = "1";
					if (sifra == 1)
					{
						SVP = SVP + "02";
					}else{
						SVP = SVP + "01";
					}
					
						SVP = SVP + "201";
					
					//olaksice da li ima
					switch (olaksice)
					{
						case 0:
							SVP = SVP + "00";
							break;
						case 3:
							SVP = SVP + "01";
							break;
						case 4:
							SVP = SVP + "02";
							break;
						case 5:
							SVP = SVP + "03";
							break;
						case 2:
							SVP = SVP + "04";
							break;
						case 1:
							SVP = SVP + "05";
							break;
						case 21:
							SVP = SVP + "06";
							break;
						case 22:
							SVP = SVP + "07";
							break;
						case 23:
							SVP = SVP + "08";
							break;
						//kod penzionera ide druga sifra kompletna za bolovanje
						case 25:
							SVP = "10920100";
							break;
						default:
							SVP = SVP + "00";
					}
					//beneficiran radni staz
					switch (sifben)
					{
						case 0:
							SVP = SVP + "0";
							break;
						case 1:
							SVP = SVP + "1";
							break;
						case 2:
							SVP = SVP + "2";
							break;
						case 3:
							SVP = SVP + "3";
							break;
						case 4:
							SVP = SVP + "4";
							break;
						default:
							SVP = SVP + "0";
					}

					output.write("<tns:SVP>" + SVP + "</tns:SVP>\n");
				if (oznakazakonacnu.trim().equals("K"))
				{
					if (sifra==89)
					{
					}
					
					
					//ovo se upisuje samo za konacnu isplatu
					output.write("<tns:BrojKalendarskihDana>" + strKalbrdana + "</tns:BrojKalendarskihDana>\n");
					output.write("<tns:BrojEfektivnihSati>" + String.valueOf(brsatibolovanja) + "</tns:BrojEfektivnihSati>\n");
					output.write("<tns:MesecniFondSati>" + String.valueOf(fondsatiumesecu) + "</tns:MesecniFondSati>\n");
				}
					output.write("<tns:Bruto>" + myFormatter.format(osndop) + "</tns:Bruto>\n");
					output.write("<tns:OsnovicaPorez>" + myFormatter.format(osnpor) + "</tns:OsnovicaPorez>\n");
					output.write("<tns:Porez>" + strPorez + "</tns:Porez>\n");
					output.write("<tns:OsnovicaDoprinosi>" + myFormatter.format(osndop) + "</tns:OsnovicaDoprinosi>\n");
					output.write("<tns:PIO>" + strPio + "</tns:PIO>\n");
					output.write("<tns:ZDR>" + strZdr + "</tns:ZDR>\n");
					output.write("<tns:NEZ>" + strNezap + "</tns:NEZ>\n");
					output.write("<tns:PIOBen>" + strPioBen + "</tns:PIOBen>\n");

				//if (t[3].getText().trim().equals("K") && imaAkontaciju())
				if (intosndopak>0)
				{
					//nadjiMFPVrednosti();
					//ovo se upisuje samo za konacnu isplatu
					output.write("<tns:DeklarisaniMFP>" + "\n");
							
							/* Kod zarade (naknade zarade) ukupan neoporezivi iznos 
							*  iskoriscen u prethodnim prijavama pod istom sifrom vrste prihoda
							*  za isti obracunski period *ldmes-osndopak*
							*/
						output.write("<tns:MFP>\n");
							output.write("<tns:Oznaka>" + "MFP.1" + "</tns:Oznaka>\n");
							output.write("<tns:Vrednost>" + myFormatter.format(dblosnporolakakont) + "</tns:Vrednost>\n");
						output.write("</tns:MFP>\n");
						//MFP1 = intosnporolak;
							
							/* Kod zarade (naknade zarade) ukupan iznos osnovice na koju su
							*  obracunati doprinosi u prethodnim prijavama pod istom sifrom vrste prihoda
							*  za isti obracunski period *ldmes-osndopak*
							*/
						output.write("<tns:MFP>\n");
							output.write("<tns:Oznaka>" + "MFP.2" + "</tns:Oznaka>\n");
							output.write("<tns:Vrednost>" + myFormatter.format(dblosndopak) + "</tns:Vrednost>\n");
						output.write("</tns:MFP>\n");
						//MFP2 = intosndopak;
							
							/* Kod zarade (naknade zarade) ukupan prethodno prijavljeni iznos zarade
							*  u prethodnim prijavama pod istom sifrom vrste prihoda
							*  za isti obracunski period *ldmes-osndopak*
							*/
						output.write("<tns:MFP>\n");
							output.write("<tns:Oznaka>" + "MFP.4" + "</tns:Oznaka>\n");
							output.write("<tns:Vrednost>" + myFormatter.format(dblbrutoak) + "</tns:Vrednost>\n");
						output.write("</tns:MFP>\n");
						//MFP4 = intbrutoak;
							
							
							/* Ukupan iznos placenog doprinosa u dobrovoljni penzijski
							*  fond i/ili placene premije dobrovoljnog zdr. osiguranja
							*  obustavljen iz zarade zaposlenog
							*/
							
							/* Ukupan iznos osnovice na koju su obracunati doprinosi u prethodnim
							*  prijavama po SVIM SIFRAMA vrste prihoda za istog primaoca
							*  i isti obracunski period.
							*/
						output.write("<tns:MFP>\n");
							output.write("<tns:Oznaka>" + "MFP.11" + "</tns:Oznaka>\n");
							output.write("<tns:Vrednost>" + myFormatter.format(dblosnporolakakont) + "</tns:Vrednost>\n");
						output.write("</tns:MFP>\n");
						output.write("<tns:MFP>\n");
							output.write("<tns:Oznaka>" + "MFP.12" + "</tns:Oznaka>\n");
							output.write("<tns:Vrednost>" + myFormatter.format(dblosndopak) + "</tns:Vrednost>\n");
						output.write("</tns:MFP>\n");
						//MFP12 = intosndopak;
					output.write("</tns:DeklarisaniMFP>" + "\n");
				}
					//dodaje se MFP10 = 1 u stavku bolovanje do 30 dana---------------------
					output.write("<tns:DeklarisaniMFP>" + "\n");
						output.write("<tns:MFP>\n");
							output.write("<tns:Oznaka>" + "MFP.10" + "</tns:Oznaka>\n");
							output.write("<tns:Vrednost>" + "1" + "</tns:Vrednost>\n");
						output.write("</tns:MFP>\n");
					output.write("</tns:DeklarisaniMFP>" + "\n");
					//-----------------------------------------------------------------------
					
					
					output.write("</tns:PodaciOPrihodima>\n");

					//unosi podatke u tabelu eporezi za svakog radnika
					//unesiPodatkeUTabelu(_sifra,kalbrdana,brsati,bruto,osnpor,osndop,porez,pio,zdr,nezap,pioben,
					//	SVP,fondsatiumesecu,sifopstine);
			
				} catch ( java.io.IOException ioe ) {
					ioe.printStackTrace();
				}
	   }
//------------------------------------------------------------------------------------------------------------------
	   public static double ZaokruziBroj(double aNumber, int aDecimals)
	   {
	     if (aDecimals < 0) {
	       throw new IllegalArgumentException("Broj decimala ne sme biti manji od 0.");
	     }
	     int factor = new Double(Math.pow(10.0D, aDecimals)).intValue();
	     double result = Math.round(aNumber * factor);
	 
	     return result / factor;
	   }
//------------------------------------------------------------------------------------------------------------------
	private void writeFooter() 
	{
		try {
			output.write("</tns:DeklarisaniPrihodi>\n");
			output.write("</tns:PodaciPoreskeDeklaracije>");
			output.close();
		} catch ( java.io.IOException ioe ) {
			ioe.printStackTrace();
		}
	}
//------------------------------------------------------------------------------------------------------------------
	/*
	private void unesiPodatkeUTabelu(int _sifra,int _kalbrdana,int _brsati,double _bruto,double _osnpor,double _osndop,
											double _porez,double _pio,double _zdr,double _nezap,double _pioben,
											String _svp,int _fondsatiumesecu,String _sifopstine) 
	{
		int osnpor=0,osndop=0,bruto=0,porez=0,pio=0,zdr=0,nezap=0,pioben=0;
		bruto = (int)_bruto;
		osnpor = (int)_osnpor;
		osndop = (int)_osndop;
		porez = (int)_porez;
		pio = (int)_pio;
		zdr = (int)_zdr;
		nezap = (int)_nezap;
		pioben = (int)_pioben;

		String sql = "INSERT INTO eporezi (pre,sifra,mesec,idplate,brplate,obrperiod,vrstaprijave,kalbrdana,brsati,brsatiumes,bruto,osnpor,porez,osndop,"+
			"pio,zdr,nezap,pioben,svp,sifraopstine,MFP1,MFP2,MFP3,MFP4,MFP5,MFP6,MFP7,MFP8,MFP9,MFP10,MFP11,MFP12)" +
			" VALUES(" + pPre.trim() + "," + _sifra + "," + ObracunskiPeriod.trim().substring(5,7) + "," + 
			Integer.parseInt(KlijentskaOznakaDeklaracije.trim()) + "," + Integer.parseInt(t[6].getText().trim()) +
			",'" + ObracunskiPeriod.trim() + "'," + strVrstaPrijave + "," + _kalbrdana + "," + _brsati + "," + _fondsatiumesecu + "," + bruto + "," + osnpor + "," + porez + "," +
			osndop + "," + pio + "," + zdr + "," + nezap + "," + pioben + ",'" + _svp + "','" + _sifopstine + "'," +
			MFP1+","+MFP2+","+MFP3+","+MFP4+","+MFP5+","+MFP6+","+MFP7+","+MFP8+","+MFP9+","+MFP10+","+MFP11+","+MFP12+")";
		izvrsi(sql);
	}
	*/
//--------------------------------------------------------------------------------------
	    public double uzmiPorOlakKumul(int _mesec, int _sifra) {
			double broj=0;
		  Statement statement = null;
		  int zadnjibrisplate = nadjiBrojZadnjeIsplate(String.valueOf(mesec),_sifra);
	      
		  try {
	         statement = connection.createStatement();
	               String query = "SELECT SUM(osnporolak) FROM ldmes WHERE pre=" + pPre.trim() +
					   " AND mesec=" + _mesec + " AND sifra=" + _sifra + " AND juzer='" + juzer + "'" + 
					   " AND godina=" + godina +
					   " AND brojobrade<" + zadnjibrisplate + " GROUP BY pre,mesec,sifra";

			         ResultSet rs = statement.executeQuery( query );
			         if(rs.next()){
						broj = rs.getDouble("SUM(osnporolak)"); 
						rs.close();
					 }else{
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
					}}
			}
			//.....................................................................................
			return broj;
	  }

	//--------------------------------------------------------------------------------------
	    public double nadjiAkontacijuBolovanjaDo30(int _mesec, int _sifra) {
			double broj=0;
		  Statement statement = null;
		  int zadnjibrisplate = nadjiBrojZadnjeIsplate(String.valueOf(mesec),_sifra);
	      
		  try {
	         statement = connection.createStatement();
	               String query = "SELECT SUM(i13) FROM ldmes WHERE pre=" + pPre.trim() +
					   " AND mesec=" + _mesec + " AND sifra=" + _sifra + " AND juzer='" + juzer + "'" + 
					   " AND godina=" + godina +
					   " AND brojobrade<" + zadnjibrisplate + " GROUP BY pre,mesec,sifra";

			         ResultSet rs = statement.executeQuery( query );
			         if(rs.next()){
						broj = rs.getDouble("SUM(i13)"); 
						rs.close();
					 }else{
						//JOptionPane.showMessageDialog(null, "Nema podataka za por.olaksicu za tu sifru i mesec");
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
					}}
			}

			//.....................................................................................
			return broj;
	  }
	//--------------------------------------------------------------------------------------
	    public int nadjiBrojZadnjeIsplate(String _mesec, int _sifra) {
			int broj=0;
		  Statement statement = null;
	      try {
	         statement = connection.createStatement();
	               String query = "SELECT MAX(brojobrade) FROM ldmes WHERE pre=" + pPre.trim() +
					   " AND mesec=" + _mesec + " AND sifra=" + _sifra + " AND juzer='" + juzer + "'" + 
					   " AND godina=" + godina;

			         ResultSet rs = statement.executeQuery( query );
			         if(rs.next()){
						broj = rs.getInt(1); 
						rs.close();
					 }else{
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
					}}
			}
			//.....................................................................................
			return broj;
	  }
	//--------------------------------------------------------------------------------------
	    public boolean postojePrethodneIsplate(int _mesec) {
			boolean postoje=false;
		  Statement statement = null;
	      try {
	         statement = connection.createStatement();
	               String query = "SELECT* FROM ldmes WHERE pre=" + pPre.trim() +
					   " AND mesec=" + _mesec + " AND brojobrade<99 AND vrstaplate=1" +
	            		  " AND juzer='" + juzer + "' AND godina=" + godina;

			         ResultSet rs = statement.executeQuery( query );
			         if(rs.next()){
						postoje = true; 
						rs.close();
					 }else{
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
					}}
			}
			//.....................................................................................
			return postoje;
	  }
	//--------------------------------------------------------------------------------------
	    public String nadjiBrojZaposlenih() {
			String brojz="0";
		    Statement statement = null;
	      
		  try {
	         statement = connection.createStatement();
	               String query = "SELECT COUNT(*) FROM ldmes WHERE pre=" + pPre.trim() +
					   " AND mesec=" + mesec + " AND godina=" + godina +
					   " AND brojobrade=" + brojobrade + " AND juzer='" + juzer + "'";
					  
			         ResultSet rs = statement.executeQuery( query );
			         if(rs.next()){
						brojz = rs.getString(1); 
						rs.close();
					 }else{
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
					}}
			}
			//.....................................................................................
			return brojz;
	  }
	//--------------------------------------------------------------------------------------
	    public void proveriOlaksice(int _sifra) {
			izn1=0;
			izn2=0;
			izn3=0;
			izn4=0;
			izn5=0;
			izn6=0;
			izn7=0;
			izn8=0;
			ind1=0;
			ind2=0;
			ind3=0;
			ind4=0;
			ind5=0;
			ind6=0;
			ind7=0;
			ind8=0;//benef.r.s.

		  Statement statement = null;
	      try {
	         statement = connection.createStatement();
	               String query = "SELECT * FROM tmpolaksice WHERE sifra=" + _sifra;

			         ResultSet rs = statement.executeQuery( query );
			         if(rs.next()){
						izn1 = rs.getDouble("izn1"); 
						izn2 = rs.getDouble("izn2"); 
						izn3 = rs.getDouble("izn3"); 
						izn4 = rs.getDouble("izn4"); 
						izn5 = rs.getDouble("izn5"); 
						izn6 = rs.getDouble("izn6"); 
						izn7 = rs.getDouble("izn7"); //porez
						izn8 = rs.getDouble("izn8"); //benef.r.s.
						ind1 = rs.getDouble("ind1"); 
						ind2 = rs.getDouble("ind2"); 
						ind3 = rs.getDouble("ind3"); 
						ind4 = rs.getDouble("ind4"); 
						ind5 = rs.getDouble("ind5"); 
						ind6 = rs.getDouble("ind6"); 
						ind7 = rs.getDouble("ind7"); //porez
						ind8 = rs.getDouble("ind8"); //benef.r.s.
						rs.close();
					 }else{
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
					}}
			}
			//.....................................................................................
	  }
	//--------------------------------------------------------------------------------------
	    public String nadjiOpstinu(int _sifra) {
			String sifop="0";
		  Statement statement = null;
	      try {
	         statement = connection.createStatement();
	               String query = "SELECT sifraopstine FROM maticnipodaci WHERE pre=" + pPre.trim() +
					   " AND sifra=" + _sifra + " AND juzer='" + "'";

			         ResultSet rs = statement.executeQuery( query );
			         if(rs.next()){
						sifop = rs.getString("sifraopstine"); 
						rs.close();
					 }else{
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
					}}
			}
			//.....................................................................................
			return sifop;
	  }
	//--------------------------------------------------------------------------------------
	    public boolean imaAkontaciju() {
			boolean ima = false;
			int akontacija=0;
		  Statement statement = null;
	      try {
	         statement = connection.createStatement();
	               String query = "SELECT osndopak FROM ldmes WHERE pre=" + pPre.trim() +
					   " AND mesec=" + mesec + " AND juzer='" + juzer + "' AND godina=" + godina;

			         ResultSet rs = statement.executeQuery( query );
			         if(rs.next()){
						akontacija = rs.getInt("osndopak"); 
						if (akontacija > 0 )
						{
							ima = true;
						}
						rs.close();
					 }else{
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
			return ima;
	  }
	//--------------------------------------------------------------------------------------
	    public void uzmiPodatkeRadnika(int _sifra) {
		  Statement statement = null;
	      try {
	         statement = connection.createStatement();
	               String query = "SELECT * FROM maticnipodaci WHERE pre=" + pPre.trim() +
					   " AND sifra=" + _sifra + " AND juzer='" + juzer + "'";

			         ResultSet rs = statement.executeQuery( query );
			         if(rs.next()){
						imeradnika = rs.getString("ime"); 
						prezimeradnika = rs.getString("prezime"); 
						JMBG = rs.getString("matbr"); 
					 }else{
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
	  }
	//--------------------------------------------------------------------------------------
	    public int uzmiKalendarskiBrojDana(int _sifra,int _mesec,int _brojobrade) {
			int brojdana = 0;
		    Statement statement = null;
			
	      try {
	         statement = connection.createStatement();
	               String query = "SELECT * FROM binmesec WHERE pre=" + pPre.trim() +
					   " AND sifra=" + _sifra + " AND mesec=" + mesec + " AND juzer='" + juzer + "'" +
					   " AND vrstaplate=" + vrstaplate + " AND brojobrade=" + brojobrade;

			         ResultSet rs = statement.executeQuery( query );
			         if(rs.next()){
						brojdana = rs.getInt("kalbrdana"); 

						rs.close();
					 }else{
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
			return brojdana;
	  }
	//--------------------------------------------------------------------------------------
	    public double uzmiProcenatAngazovanja(int _sifra,int _mesec,int _brojobrade) {
			double procenat = 0;
		  Statement statement = null;
	      try {
	         statement = connection.createStatement();
	               String query = "SELECT * FROM binmesec WHERE pre=" + pPre.trim() +
					   " AND sifra=" + _sifra + " and mesec=" + _mesec + 
					   " and vrstaplate=1 and brojobrade=" + _brojobrade;

			         ResultSet rs = statement.executeQuery( query );
			         if(rs.next()){
						procenat = rs.getInt("procangazovanja"); 
						rs.close();
					 }else{
						//JOptionPane.showMessageDialog(null, "Nema KalendarskiBrojDana za radnika:"+String.valueOf(_sifra));
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
			return procenat;
	  }
	//--------------------------------------------------------------------------------------
	    public void nadjiPreduzece() {
		  Statement statement = null;
	      try {
	         statement = connection.createStatement();
	               String query = "SELECT * FROM preduzeca WHERE pre=" + pPre.trim() +
	            		   " AND juzer='" + juzer + "'";

				try {
			         ResultSet rs = statement.executeQuery( query );
			         if(rs.next()){
						PIB = rs.getString("pib"); 
						naziv = rs.getString("naziv"); 
						matbroj = rs.getString("matbr"); 
						adresa = rs.getString("adresa"); 
						telefon = rs.getString("telefon"); 
						mail = rs.getString("fax"); 
						sifraopstine = rs.getString("sifraopstine"); 
						rs.close();
					 }
			      }
			      catch ( SQLException sqlex ) {
			         	System.out.println("Podaci ne postoje za preduzece ");
			      }
		  }     
	      catch ( SQLException sqlex ) {
			System.out.println("Greska u trazenju preduzeca ");
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
	//--------------------------------------------------------------------------------------
	    public double nadjiBenefKoef() {
			double benkoeff = 0;
		  Statement statement = null;
	      try {
	         statement = connection.createStatement();
	               String query = "SELECT * FROM doprinosi WHERE sifdop=15 and vrdop=5";

			         ResultSet rs = statement.executeQuery( query );
			         if(rs.next()){
						benkoeff = rs.getDouble("procdop"); 
						rs.close();
					 }
		  }     
	      catch ( SQLException sqlex ) {
			System.out.println("Greska u trazenju koef. za benef.rad.staz:"+sqlex);
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
			return benkoeff;
	  }
	//------------------------------------------------------------------------------------------------------------------

}
