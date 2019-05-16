package com.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperRunManager;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.util.*;
import java.sql.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.model.PrintListic;
import com.model.PrintRekap;
import com.model.PrintRekapDop;
import com.model.PrintXML;
import com.model.PrintVirTekuci;
import com.model.PrintVirPordop;
import com.model.PrintVirSindikati;
import com.model.PrintVirKrediti;
import com.model.PrintVirSamodop;
import com.model.PrintListaPutni;
import com.model.PrintIspList;
import com.model.PrintKarticaRadnika;
import com.model.PrintKarticaKrediti;
import com.controller.ExcelController;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

@RestController
@RequestMapping("/print")
public class PrintController {
	@Autowired
    private JdbcTemplate jdbcTemplate;	

	Connection conn;
	DataSource datasource;
	String juzer;
	private PrintXML printxml;
	private ExcelController xls;
	public static final String FILE_LOCATION="/xmlfiles";
	private String nazivpre="",mestopre="";
	
	@RequestMapping(value = "/listic", method = RequestMethod.GET)
	@ResponseBody
	public void getRptListic(HttpServletRequest request,HttpServletResponse response) throws JRException, IOException {
		String strpre=request.getParameter("pre");
		String strgodina=request.getParameter("godina");
		String sifra=request.getParameter("sifra");
		String vrstaplate=request.getParameter("vrstaplate");
		String mesec=request.getParameter("mesec");
		String brojobrade=request.getParameter("brojobrade");
		
		try{
			DataSource datasource = jdbcTemplate.getDataSource();
			conn = datasource.getConnection();	
		}catch(SQLException ee){
			ee.printStackTrace();
		}		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        juzer = auth.getName();			

        
        PrintListic printlistic = new PrintListic(conn,juzer,Integer.parseInt(strpre),strgodina) ;
		printlistic.obradiListic(Integer.parseInt(vrstaplate),Integer.parseInt(mesec),Integer.parseInt(sifra),
												Integer.parseInt(brojobrade));
		
		File reportFile = new File("src/main/resources/static/reports/listic.jasper");    
		Map parameters = new HashMap();
		 nadjiPreduzece(conn,strpre,juzer);
		//ubacivanje parametara u izvestaj
		parameters.put("nazivpre", nazivpre);
		parameters.put("mestopre", mestopre);
		
		byte[] bytes = JasperRunManager.runReportToPdf(reportFile.getPath(), parameters, conn);
		response.setContentType("application/pdf");
		response.setContentLength(bytes.length);
        ServletOutputStream ouputStream = response.getOutputStream();
        ouputStream.write(bytes, 0, bytes.length); 
        ouputStream.flush();
        ouputStream.close();
		try{
			conn.close();	
		}catch(SQLException ee){
			ee.printStackTrace();
		}		
      
}
	//kreiranje listica za isplatu kao PDF fajl za slanje preko mail-a
	@RequestMapping(value = "/listicfile", method = RequestMethod.POST, produces="application/json")
	@ResponseBody
	public String getRptListicFile(HttpServletRequest request,HttpServletResponse response ) throws JRException, IOException {
		String vrati="1";
		String strpre=request.getParameter("pre");
		String strgodina=request.getParameter("godina");
		String odsifre=request.getParameter("odsifre");
		String dosifre=request.getParameter("dosifre");
		String vrstaplate=request.getParameter("vrstaplate");
		String mesec=request.getParameter("mesec");
		String brojobrade=request.getParameter("brojobrade");
		String nazivfajla="";
		int intodsifre=0,intdosifre=0;
		
		intodsifre = Integer.parseInt(odsifre);
		intdosifre = Integer.parseInt(dosifre);
		
		try{
			DataSource datasource = jdbcTemplate.getDataSource();
			conn = datasource.getConnection();	
		}catch(SQLException ee){
			ee.printStackTrace();
			vrati = "0";
		}		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        juzer = auth.getName();
        
        int i = 0;
       
        
       	nadjiPreduzece(conn,strpre,juzer);

       	for (i=intodsifre;i<intdosifre + 1;i++) {
       		if(proveriPostojePodaci(conn,i,strpre,strgodina,juzer,brojobrade,mesec,vrstaplate)) {
        
       			nazivfajla = "listici/" + juzer + "-" + strpre + "-" + String.valueOf(i) + ".pdf";
        
        		PrintListic printlistic = new PrintListic(conn,juzer,Integer.parseInt(strpre),strgodina) ;
        		printlistic.obradiListic(Integer.parseInt(vrstaplate),Integer.parseInt(mesec),i,
												Integer.parseInt(brojobrade));
		
        		File reportFile = new File("src/main/resources/static/reports/listic.jasper");    
        		Map parameters = new HashMap();
        	
         		//ubacivanje parametara u izvestaj
        		parameters.put("nazivpre", nazivpre);
        		parameters.put("mestopre", mestopre);

        		byte[] bytes = JasperRunManager.runReportToPdf(reportFile.getPath(), parameters, conn);
		
        		try (
        			FileOutputStream fos = new FileOutputStream(nazivfajla)) {
        			fos.write(bytes);
        			fos.close();
        		}catch(Exception ee) {
        			vrati = "0";
        		}
       		}//end if
		}//end for
		try{
			conn.close();	
		}catch(SQLException ee){
			ee.printStackTrace();
		}		
       	
       	return vrati;
    }

	@RequestMapping(value = "/rekap", method = RequestMethod.GET)
	@ResponseBody
	public void getRptRekap(HttpServletRequest request,HttpServletResponse response) throws JRException, IOException {
		String strpre=request.getParameter("pre");
		String strgodina=request.getParameter("godina");
		String odsifre=request.getParameter("odsifre");
		String dosifre=request.getParameter("dosifre");
		String vrstaplate=request.getParameter("vrstaplate");
		String mesec=request.getParameter("mesec");
		String brojobrade=request.getParameter("brojobrade");

		try{
			DataSource datasource = jdbcTemplate.getDataSource();
			conn = datasource.getConnection();	
		}catch(SQLException ee){
			ee.printStackTrace();
		}
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        juzer = auth.getName();			

        PrintRekap printrekap = new PrintRekap() ;
        
        printrekap.obradiRekap(conn,juzer,Integer.parseInt(strpre),strgodina,Integer.parseInt(vrstaplate),Integer.parseInt(mesec),Integer.parseInt(odsifre),
        		Integer.parseInt(dosifre),Integer.parseInt(brojobrade));
        
		
        
        File reportFile = new File("src/main/resources/static/reports/rekapplate.jasper");    
		Map parameters = new HashMap();
        nadjiPreduzece(conn,strpre,juzer);
		//ubacivanje parametara u izvestaj
		parameters.put("nazivpre", nazivpre);
		parameters.put("mestopre", mestopre);
		parameters.put("brisplate", brojobrade);
		parameters.put("vrboda", "-");
		
		byte[] bytes = JasperRunManager.runReportToPdf(reportFile.getPath(), parameters, conn);
		response.setContentType("application/pdf");
        response.setContentLength(bytes.length);
        ServletOutputStream ouputStream = response.getOutputStream();
        ouputStream.write(bytes, 0, bytes.length); 
        ouputStream.flush();
        ouputStream.close();
		try{
			conn.close();	
		}catch(SQLException ee){
			ee.printStackTrace();
		}		
       
	}
	@RequestMapping(value = "/isplatneliste", method = RequestMethod.GET)
	@ResponseBody
	public void getRptIsplatneListe(HttpServletRequest request,HttpServletResponse response) throws JRException, IOException {
		String strpre=request.getParameter("pre");
		String strgodina=request.getParameter("godina");
		String odsifre=request.getParameter("odsifre");
		String dosifre=request.getParameter("dosifre");
		String vrstaplate=request.getParameter("vrstaplate");
		String mesec=request.getParameter("mesec");
		String brojobrade=request.getParameter("brojobrade");

		try{
			DataSource datasource = jdbcTemplate.getDataSource();
			conn = datasource.getConnection();	
		}catch(SQLException ee){
			ee.printStackTrace();
		}
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        juzer = auth.getName();			

        PrintIspList printlist = new PrintIspList() ;
        
        printlist.obradiListic(conn,juzer,Integer.parseInt(strpre),strgodina,Integer.parseInt(vrstaplate),Integer.parseInt(mesec),Integer.parseInt(odsifre),
        		Integer.parseInt(dosifre),Integer.parseInt(brojobrade));
        
        File reportFile = new File("src/main/resources/static/reports/listic.jasper");    
		Map parameters = new HashMap();
        nadjiPreduzece(conn,strpre,juzer);
		//ubacivanje parametara u izvestaj
		parameters.put("nazivpre", nazivpre);
		parameters.put("mestopre", mestopre);
		parameters.put("brisplate", brojobrade);
		parameters.put("vrboda", "-");
		
		byte[] bytes = JasperRunManager.runReportToPdf(reportFile.getPath(), parameters, conn);
		response.setContentType("application/pdf");
        response.setContentLength(bytes.length);
        ServletOutputStream ouputStream = response.getOutputStream();
        ouputStream.write(bytes, 0, bytes.length); 
        ouputStream.flush();
        ouputStream.close();
		try{
			conn.close();	
		}catch(SQLException ee){
			ee.printStackTrace();
		}		
       
	}

	@RequestMapping(value = "/rekappordop", method = RequestMethod.GET)
	@ResponseBody
	public void getRptRekapDop(HttpServletRequest request,HttpServletResponse response) throws JRException, IOException {
		String strpre=request.getParameter("pre");
		String strgodina=request.getParameter("godina");
		String vrstaplate=request.getParameter("vrstaplate");
		String mesec=request.getParameter("mesec");
		String brojobrade=request.getParameter("brojobrade");

		try{
			DataSource datasource = jdbcTemplate.getDataSource();
			conn = datasource.getConnection();	
		}catch(SQLException ee){
			ee.printStackTrace();
		}
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        juzer = auth.getName();			

        PrintRekapDop printrekap = new PrintRekapDop() ;
        
        printrekap.obradiRekapDop(conn,juzer,Integer.parseInt(strpre),strgodina,Integer.parseInt(vrstaplate),Integer.parseInt(mesec),
        		Integer.parseInt(brojobrade));
		
		File reportFile = new File("src/main/resources/static/reports/rekapdop.jasper");    
        nadjiPreduzece(conn,strpre,juzer);
		Map parameters = new HashMap();
		parameters.put("nazivpre", nazivpre);
		parameters.put("mestopre", mestopre);
		parameters.put("mesec", mesec);
		parameters.put("nazivplate", mesec + "-" + strgodina);
		
		byte[] bytes = JasperRunManager.runReportToPdf(reportFile.getPath(), parameters, conn);
		response.setContentType("application/pdf");
        response.setContentLength(bytes.length);
        ServletOutputStream ouputStream = response.getOutputStream();
        ouputStream.write(bytes, 0, bytes.length); 
        ouputStream.flush();
        ouputStream.close();
		try{
			conn.close();	
		}catch(SQLException ee){
			ee.printStackTrace();
		}		
	}
	@RequestMapping(value = "/karticaradnika", method = RequestMethod.GET)
	@ResponseBody
	public void getRptKarticaRadnika(HttpServletRequest request,HttpServletResponse response) throws JRException, IOException {
		String strpre=request.getParameter("pre");
		String strgodina=request.getParameter("godina");
		String odmeseca=request.getParameter("odmeseca");
		String domeseca=request.getParameter("domeseca");
		String sifra=request.getParameter("sifra");

		try{
			DataSource datasource = jdbcTemplate.getDataSource();
			conn = datasource.getConnection();	
		}catch(SQLException ee){
			ee.printStackTrace();
		}
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        juzer = auth.getName();			

        PrintKarticaRadnika printkarticaradnika = new PrintKarticaRadnika() ;
        
        printkarticaradnika.obradiKarticu(conn,juzer,Integer.parseInt(strpre),strgodina,Integer.parseInt(odmeseca),
        		Integer.parseInt(domeseca),Integer.parseInt(sifra));
		
		File reportFile = new File("src/main/resources/static/reports/kartica.jasper");    
        nadjiPreduzece(conn,strpre,juzer);
		Map parameters = new HashMap();
		parameters.put("nazivpre", nazivpre);
		parameters.put("mestopre", mestopre);
		
		byte[] bytes = JasperRunManager.runReportToPdf(reportFile.getPath(), parameters, conn);
		response.setContentType("application/pdf");
        response.setContentLength(bytes.length);
        ServletOutputStream ouputStream = response.getOutputStream();
        ouputStream.write(bytes, 0, bytes.length); 
        ouputStream.flush();
        ouputStream.close();
		try{
			conn.close();	
		}catch(SQLException ee){
			ee.printStackTrace();
		}		
	}
	@RequestMapping(value = "/karticakrediti", method = RequestMethod.GET)
	@ResponseBody
	public void getRptKarticaKrediti(HttpServletRequest request,HttpServletResponse response) throws JRException, IOException {
		String strpre=request.getParameter("pre");
		String strgodina=request.getParameter("godina");
		String odmeseca=request.getParameter("odmeseca");
		String domeseca=request.getParameter("domeseca");
		String sifra=request.getParameter("sifra");
		String sifrakreditora=request.getParameter("sifrakreditora");
		String ime=request.getParameter("ime");
		String prezime=request.getParameter("prezime");

		try{
			DataSource datasource = jdbcTemplate.getDataSource();
			conn = datasource.getConnection();	
		}catch(SQLException ee){
			ee.printStackTrace();
		}
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        juzer = auth.getName();			

        PrintKarticaKrediti printkarticakrediti = new PrintKarticaKrediti() ;
        
        printkarticakrediti.obradiKarticu(conn,juzer,Integer.parseInt(strpre),strgodina,Integer.parseInt(odmeseca),
        		Integer.parseInt(domeseca),Integer.parseInt(sifra),Integer.parseInt(sifrakreditora));
		
		File reportFile = new File("src/main/resources/static/reports/karticakrediti.jasper");    
        nadjiPreduzece(conn,strpre,juzer);
		Map parameters = new HashMap();
		parameters.put("nazivpre", nazivpre);
		parameters.put("mestopre", mestopre);
		parameters.put("ime", ime);
		parameters.put("prezime", prezime);
		
		byte[] bytes = JasperRunManager.runReportToPdf(reportFile.getPath(), parameters, conn);
		response.setContentType("application/pdf");
        response.setContentLength(bytes.length);
        ServletOutputStream ouputStream = response.getOutputStream();
        ouputStream.write(bytes, 0, bytes.length); 
        ouputStream.flush();
        ouputStream.close();
		try{
			conn.close();	
		}catch(SQLException ee){
			ee.printStackTrace();
		}		
	}
	@RequestMapping(value = "/listaputni", method = RequestMethod.GET)
	@ResponseBody
	public void getRptListaPutni(HttpServletRequest request,HttpServletResponse response) throws JRException, IOException {
		String strpre=request.getParameter("pre");
		String strgodina=request.getParameter("godina");
		String mesec=request.getParameter("mesec");
		String brojobrade=request.getParameter("brojobrade");

		try{
			DataSource datasource = jdbcTemplate.getDataSource();
			conn = datasource.getConnection();	
		}catch(SQLException ee){
			ee.printStackTrace();
		}
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        juzer = auth.getName();			

        PrintListaPutni printlistaputni = new PrintListaPutni() ;
        
        printlistaputni.obradiListu(conn,juzer,Integer.parseInt(strpre),strgodina,Integer.parseInt(mesec),
        		Integer.parseInt(brojobrade));
		
		File reportFile = new File("src/main/resources/static/reports/putnitr.jasper");    
        nadjiPreduzece(conn,strpre,juzer);
		Map parameters = new HashMap();
		parameters.put("nazivpre", nazivpre);
		parameters.put("mestopre", mestopre);
		
		byte[] bytes = JasperRunManager.runReportToPdf(reportFile.getPath(), parameters, conn);
		response.setContentType("application/pdf");
        response.setContentLength(bytes.length);
        ServletOutputStream ouputStream = response.getOutputStream();
        ouputStream.write(bytes, 0, bytes.length); 
        ouputStream.flush();
        ouputStream.close();
		try{
			conn.close();	
		}catch(SQLException ee){
			ee.printStackTrace();
		}		
	}

	@RequestMapping(value = "/virmantekuci", method = RequestMethod.GET)
	@ResponseBody
	public void getRptVirTekuci(HttpServletRequest request,HttpServletResponse response) throws JRException, IOException {
		String strpre=request.getParameter("pre");
		String strgodina=request.getParameter("godina");
		String vrstaplate=request.getParameter("vrstaplate");
		String mesec=request.getParameter("mesec");
		String brojobrade=request.getParameter("brojobrade");
		String bop=request.getParameter("bop");
		String sifrauplate=request.getParameter("sifrauplate");

		try{
			DataSource datasource = jdbcTemplate.getDataSource();
			conn = datasource.getConnection();	
		}catch(SQLException ee){
			ee.printStackTrace();
		}
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        juzer = auth.getName();			

        PrintVirTekuci printrekap = new PrintVirTekuci() ;
        
        printrekap.obradiVirTekuci(conn,juzer,Integer.parseInt(strpre),strgodina,Integer.parseInt(vrstaplate),Integer.parseInt(mesec),
        		Integer.parseInt(brojobrade),bop,sifrauplate);
		
		File reportFile = new File("src/main/resources/static/reports/virmantekn.jasper");    
		Map parameters = new HashMap();
		parameters.put("sifrauplate", sifrauplate);
		
		byte[] bytes = JasperRunManager.runReportToPdf(reportFile.getPath(), parameters, conn);
		response.setContentType("application/pdf");
        response.setContentLength(bytes.length);
        ServletOutputStream ouputStream = response.getOutputStream();
        ouputStream.write(bytes, 0, bytes.length); 
        ouputStream.flush();
        ouputStream.close();
		try{
			conn.close();	
		}catch(SQLException ee){
			ee.printStackTrace();
		}		
	}
	@RequestMapping(value = "/virmanpordop", method = RequestMethod.GET)
	@ResponseBody
	public void getRptVirPordop(HttpServletRequest request,HttpServletResponse response) throws JRException, IOException {
		String strpre=request.getParameter("pre");
		String strgodina=request.getParameter("godina");
		String vrstaplate=request.getParameter("vrstaplate");
		String mesec=request.getParameter("mesec");
		String brojobrade=request.getParameter("brojobrade");
		String bop=request.getParameter("bop");
		String sifraobrade=request.getParameter("sifraobrade");

		try{
			DataSource datasource = jdbcTemplate.getDataSource();
			conn = datasource.getConnection();	
		}catch(SQLException ee){
			ee.printStackTrace();
		}
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        juzer = auth.getName();			

        PrintVirPordop printrekap = new PrintVirPordop() ;
        
        printrekap.obradiVirPordop(conn,juzer,Integer.parseInt(strpre),strgodina,Integer.parseInt(vrstaplate),Integer.parseInt(mesec),
        		Integer.parseInt(brojobrade),bop,sifraobrade);
		
		File reportFile = new File("src/main/resources/static/reports/virmandop.jasper");    
		Map parameters = new HashMap();
		
		byte[] bytes = JasperRunManager.runReportToPdf(reportFile.getPath(), parameters, conn);
		response.setContentType("application/pdf");
        response.setContentLength(bytes.length);
        ServletOutputStream ouputStream = response.getOutputStream();
        ouputStream.write(bytes, 0, bytes.length); 
        ouputStream.flush();
        ouputStream.close();
		try{
			conn.close();	
		}catch(SQLException ee){
			ee.printStackTrace();
		}		
	}
	@RequestMapping(value = "/virmansindikati", method = RequestMethod.GET)
	@ResponseBody
	public void getRptVirSindikati(HttpServletRequest request,HttpServletResponse response) throws JRException, IOException {
		String strpre=request.getParameter("pre");
		String strgodina=request.getParameter("godina");
		String vrstaplate=request.getParameter("vrstaplate");
		String mesec=request.getParameter("mesec");
		String brojobrade=request.getParameter("brojobrade");
		String sifrauplate=request.getParameter("sifrauplate");

		try{
			DataSource datasource = jdbcTemplate.getDataSource();
			conn = datasource.getConnection();	
		}catch(SQLException ee){
			ee.printStackTrace();
		}
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        juzer = auth.getName();			

        PrintVirSindikati printrekap = new PrintVirSindikati() ;
        
        printrekap.obradiVirSindikati(conn,juzer,Integer.parseInt(strpre),strgodina,Integer.parseInt(vrstaplate),Integer.parseInt(mesec),
        		Integer.parseInt(brojobrade),sifrauplate);
		
		File reportFile = new File("src/main/resources/static/reports/virmansindi.jasper");    
		Map parameters = new HashMap();
		
		byte[] bytes = JasperRunManager.runReportToPdf(reportFile.getPath(), parameters, conn);
		response.setContentType("application/pdf");
        response.setContentLength(bytes.length);
        ServletOutputStream ouputStream = response.getOutputStream();
        ouputStream.write(bytes, 0, bytes.length); 
        ouputStream.flush();
        ouputStream.close();
		try{
			conn.close();	
		}catch(SQLException ee){
			ee.printStackTrace();
		}		
	}
	@RequestMapping(value = "/virmankrediti", method = RequestMethod.GET)
	@ResponseBody
	public void getRptVirKreditori(HttpServletRequest request,HttpServletResponse response) throws JRException, IOException {
		String strpre=request.getParameter("pre");
		String vrstaplate=request.getParameter("vrstaplate");
		String strgodina=request.getParameter("godina");
		String mesec=request.getParameter("mesec");
		String brojobrade=request.getParameter("brojobrade");

		try{
			DataSource datasource = jdbcTemplate.getDataSource();
			conn = datasource.getConnection();	
		}catch(SQLException ee){
			ee.printStackTrace();
		}
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        juzer = auth.getName();			

        PrintVirKrediti printrekap = new PrintVirKrediti() ;
        
        printrekap.obradiVirKrediti(conn,juzer,Integer.parseInt(strpre),strgodina,Integer.parseInt(mesec),
        		Integer.parseInt(brojobrade),Integer.parseInt(vrstaplate));
		
		File reportFile = new File("src/main/resources/static/reports/virmankred.jasper");    
		Map parameters = new HashMap();
		
		byte[] bytes = JasperRunManager.runReportToPdf(reportFile.getPath(), parameters, conn);
		response.setContentType("application/pdf");
        response.setContentLength(bytes.length);
        ServletOutputStream ouputStream = response.getOutputStream();
        ouputStream.write(bytes, 0, bytes.length); 
        ouputStream.flush();
        ouputStream.close();
		try{
			conn.close();	
		}catch(SQLException ee){
			ee.printStackTrace();
		}		
	}
	@RequestMapping(value = "/virmansamodop", method = RequestMethod.GET)
	@ResponseBody
	public void getRptVirSamodop(HttpServletRequest request,HttpServletResponse response) throws JRException, IOException {
		String strpre=request.getParameter("pre");
		String vrstaplate=request.getParameter("vrstaplate");
		String strgodina=request.getParameter("godina");
		String mesec=request.getParameter("mesec");
		String brojobrade=request.getParameter("brojobrade");

		try{
			DataSource datasource = jdbcTemplate.getDataSource();
			conn = datasource.getConnection();	
		}catch(SQLException ee){
			ee.printStackTrace();
		}
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        juzer = auth.getName();			

        PrintVirSamodop printrekap = new PrintVirSamodop() ;
        
        printrekap.obradiVirSamodop(conn,juzer,Integer.parseInt(strpre),strgodina,Integer.parseInt(mesec),
        		Integer.parseInt(brojobrade),Integer.parseInt(vrstaplate));
		
		File reportFile = new File("src/main/resources/static/reports/virmansamo.jasper");    
		Map parameters = new HashMap();
		
		byte[] bytes = JasperRunManager.runReportToPdf(reportFile.getPath(), parameters, conn);
		response.setContentType("application/pdf");
        response.setContentLength(bytes.length);
        ServletOutputStream ouputStream = response.getOutputStream();
        ouputStream.write(bytes, 0, bytes.length); 
        ouputStream.flush();
        ouputStream.close();
		try{
			conn.close();	
		}catch(SQLException ee){
			ee.printStackTrace();
		}		
	
	}
	@RequestMapping(value = "/xml", method = RequestMethod.GET)
	@ResponseBody
	public void getRptXML(HttpServletRequest request,HttpServletResponse response) throws JRException, IOException {
		String strpre=request.getParameter("pre");
		String strgodina=request.getParameter("godina");
		String vrstaplate=request.getParameter("vrstaplate");
		String tipisplatioca=request.getParameter("tipisplatioca");
		String mesec=request.getParameter("mesec");
		String brojobrade=request.getParameter("brojobrade");
		String vrstaprijave=request.getParameter("vrstaprijave");
		String oznakazakonacnu=request.getParameter("oznakazakonacnu");
		String klijentskaoznakadeklaracije = request.getParameter("klijentskaoznakadeklaracije");
		String obracunskiperiod = request.getParameter("obracunskiperiod");
		String datumplacanja = request.getParameter("datumplacanja");
		String najnizaosnovica = request.getParameter("najnizaosnovica");
		
		try{
			DataSource datasource = jdbcTemplate.getDataSource();
			conn = datasource.getConnection();	
		}catch(SQLException ee){
			ee.printStackTrace();
		}		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        juzer = auth.getName();			
			
        printxml = new PrintXML(conn,juzer,strpre,strgodina,brojobrade,oznakazakonacnu,klijentskaoznakadeklaracije,
        		obracunskiperiod,datumplacanja,najnizaosnovica,mesec);
        
        printxml.napraviXML(vrstaplate, tipisplatioca, vrstaprijave);
        
        String filePaths = juzer + strpre + "ppp.xml";
        byte[] bytes = readBytesFromFile(filePaths);
		response.setContentType("application/xml");
        response.setContentLength(bytes.length);
        ServletOutputStream ouputStream = response.getOutputStream();
        ouputStream.write(bytes, 0, bytes.length); 
        ouputStream.flush();
        ouputStream.close();
		try{
			conn.close();	
		}catch(SQLException ee){
			ee.printStackTrace();
		}		
		
	  }
	@RequestMapping(value = "/xls", method = RequestMethod.GET)
	@ResponseBody
	public void getRptXLS(HttpServletRequest request,HttpServletResponse response){
/*
		try{
			DataSource datasource = jdbcTemplate.getDataSource();
			conn = datasource.getConnection();	
		}catch(SQLException ee){
			ee.printStackTrace();
		}
		*/		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        juzer = auth.getName();			
			
        xls = new ExcelController();
        try {
        	xls.buildExcelDocument();
        }catch(Exception eee) {
        	eee.printStackTrace();
        }
        
        String filePaths = "workbook.xls";
        byte[] bytes = readBytesFromFile(filePaths);
		response.setContentType("application/vnd.ms-excel");
        response.setContentLength(bytes.length);
        try {
        	ServletOutputStream ouputStream = response.getOutputStream();
        	ouputStream.write(bytes, 0, bytes.length); 
        	ouputStream.flush();
        	ouputStream.close();
        }catch(Exception ex) {
        	ex.printStackTrace();
        }
		
	  }

	
	private static byte[] readBytesFromFile(String filePath) {

        FileInputStream fileInputStream = null;
        byte[] bytesArray = null;

        try {
            File file = new File(filePath);
            bytesArray = new byte[(int) file.length()];

            //read file into bytes[]
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(bytesArray);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bytesArray;
    }
    public void nadjiPreduzece(Connection _connection,String _preduz,String _juzer) {
	  Statement statement = null;
      try {
         statement = _connection.createStatement();
               String query = "SELECT * FROM preduzeca WHERE Pre=" + _preduz +
            		   " AND juzer='" + _juzer + "'";

		         ResultSet rs = statement.executeQuery( query );
		         if(rs.next()){
					nazivpre = rs.getString("naziv"); 
					mestopre = rs.getString("mesto"); 
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
				}}
		}
		//.....................................................................................
  }
//.................................................................................................................
    public boolean proveriPostojePodaci(Connection _connection,int _sifra,String _pre,String _godina,String _juzer,
    		String _brobrade,String _mesec,String _vrstaplate) {
    	boolean postoji = false;
	  Statement statement = null;
      try {
         statement = _connection.createStatement();
               String query = "SELECT * FROM ldmes WHERE pre=" + _pre +
            		   " AND juzer='" + _juzer + "' AND brojobrade=" + _brobrade + 
            		   " AND godina=" + _godina + " AND mesec=" + _mesec +
            		   " AND sifra=" + _sifra + " AND vrstaplate=" + _vrstaplate;

		         ResultSet rs = statement.executeQuery( query );
		         if(rs.next()){
					postoji = true;
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
				}}
		}
		//.....................................................................................
      return postoji;
  }
	
}
