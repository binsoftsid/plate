package com.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.sql.*;
import org.springframework.jdbc.core.JdbcTemplate;

public class ClassObradaPorez {
	@Autowired
	private JdbcTemplate jdbcTemplate;	 

	private Connection connection = null;
	private int intdatod=0,intdatdo=0,broj=0;
	private double topliobrok=0,kojiprocenat=1;
	private int mesec=0,brojobrade=99,sifra=0,vrstaplate=1;
	//promenljive za obradu
	private int siff,siff1,red=0,kk,netoc,gs,ms,redx,grupa,siframz,sifpros,sifpros1;
	private int sin,mesecaz,godinestaza=0,mesecistaza=0;
	private int fondsatizaobr=0,fondsatiumesecu=0;
	private String pred="",uslov="",juzer="";
	private String godina;
	//akontacije
	private double bruto=0,osnporolak=0,osnbenak,dopbenak,pioum=0,minimalnaosnovica=0;
	private double svegaobustavaak=0,brutoak=0,uplacenporez=0,dodatniuplacen=0;
	private int	brojefektivnihsati=0;

	//koeficijent korekcije poreske osnovice i poreza
	private double koeficijent=1;



 public ClassObradaPorez(Connection _connection,String _pre,String _juzer,String _godina) {
		connection = _connection;
		pred = _pre;
		juzer = _juzer;
		godina = _godina;
	
}
 //-----------------------------------------------------------------------------------------------------
 public void ObradiPorez(int _brojobrade,int _mesec,int _sifra,int _vrstaplate) {
		mesec=_mesec;
		brojobrade=_brojobrade;
		sifra=_sifra;
		vrstaplate=_vrstaplate;

	    uslov=" WHERE ldmes.sifra=" + sifra + " AND ldmes.mesec=" + mesec + 
			" AND ldmes.vrstaplate=" + vrstaplate + " AND ldmes.brojobrade=" + brojobrade + 
			" AND ldmes.pre=" + pred + " AND ldmes.juzer='" + juzer + "' AND ldmes.godina=" + godina; 
		
		nulirajPromenljive();
		//uzima podatke o satima
		uzmiPodatke();

		//proverava da li je plata 100% ili manje
		proveriProcenatPlate();

		//uzima zakonsku minimalnu osnovicu
		uzmiMinimalnuOsnovicu();

		//ako je bruto plata manja od poreske olaksice
		koeficijent = 1;
		if (bruto <= osnporolak)
		{
			koeficijent =((double)brojefektivnihsati)/((double)fondsatiumesecu);
		}

		unesiPodatkePoreza();

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
				//JOptionPane.showMessageDialog(null, "Nije uspeo da zatvori statement");
			}}
	  }
  }
//-----------------------------------------------------------------------------------------------------
   public void nulirajPromenljive() {
		//stavlja sve vrednosti promenljivih na nulu
		brojefektivnihsati=0;
		fondsatizaobr=0;
		fondsatiumesecu=0;
		bruto=0;
		osnporolak=0;

		//koji procenat plate racuna da li 100% ili 50% ili drugo
		kojiprocenat=1;
		//uzima zakonsku minimalnu osnovicu plate
		minimalnaosnovica=0;

		koeficijent=1;
  }
 //-----------------------------------------------------------------------------------------------------
   public void uzmiPodatke() {
		boolean postoji=false;
		Statement statement=null;
      
		brojefektivnihsati=0;
		fondsatizaobr=0;
		fondsatiumesecu=0;
		bruto=0;
		osnporolak=0;
	  
	  
	  try {
         statement = connection.createStatement();
               String query = "SELECT * FROM ldmes " + uslov;

		         ResultSet rs = statement.executeQuery( query );
            		if(rs.next()){
						brojefektivnihsati=rs.getInt("c29");
						fondsatizaobr=rs.getInt("fondsatizaobr");
						fondsatiumesecu=rs.getInt("fondsatiumesecu");
						bruto=rs.getDouble("i22");
						osnporolak=rs.getDouble("osnporolak");
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
 //-----------------------------------------------------------------------------------------------------
   public void proveriProcenatPlate() {
		boolean postoji=false;
		Statement statement=null;
      try {
         statement = connection.createStatement();
               String query = "SELECT * FROM binmesec WHERE sifra=" + sifra +
		            " AND mesec=" + mesec + " AND vrstaplate=" + vrstaplate +
					" AND brojobrade=" + brojobrade + " AND pre=" + pred +
					" AND binmesec.juzer='" + juzer + "' AND godina=" + godina;

		         ResultSet rs = statement.executeQuery( query );
            		if(rs.next()){
						kojiprocenat=rs.getDouble("procenat")/100;
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
  //-----------------------------------------------------------------------------------
     public void uzmiMinimalnuOsnovicu() {
		minimalnaosnovica = 0.00;
	  Statement statement = null;
      try {
         statement = connection.createStatement();
         
                String query = "SELECT * FROM poreskaskala where skala = 1 ";
		        ResultSet rs = statement.executeQuery( query );
				rs.next();
				if (kojiprocenat==1)
				{
					minimalnaosnovica = rs.getDouble("neta");
				}else{
					minimalnaosnovica = rs.getDouble("neta")/2;
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
//-----------------------------------------------------------------------------------------------------
   public void unesiPodatkePoreza() {
		//konacna poreska olaksica ovde se vodi racuna da poreska olaksica vazi za pun
		//fond sati u mesecu a ako je manji i ona se umanjuje
		double efsati=0.0,satmes=0.0,konporolak=0.0;

		efsati = (double)brojefektivnihsati;
		satmes = (double)fondsatiumesecu;
	   
	   if (efsati>satmes)
	   {
			konporolak = (osnporolak*koeficijent);
	   }else{
			konporolak = (osnporolak)*((double)brojefektivnihsati)/((double)fondsatiumesecu);
	   }
		String sql="";
			sql = "UPDATE ldmes SET oo=i22-" + konporolak +
			",i25=oo*p4,osnpor=i22";
			sql = sql + uslov; 
			izvrsi(sql);

			sql = "UPDATE ldmes SET oo=0,i25=0,osnpor=0";
			sql = sql + uslov + " AND ldmes.i25<0"; 
			izvrsi(sql);

  }
 //-----------------------------------------------------------------------------------------------------
}
