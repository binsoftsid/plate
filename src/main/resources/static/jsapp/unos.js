//korisnik:
window.ime='';
var idd = null,nazivv = null,predd = null,jurr=null;
var myGrid,myToolbar,myGridKrediti,myWins,myGridRadnici,myFormXML;
var sifra,vrstaplate,mesec,brojobrade,firma,godina;
var stopapio,stopazdrav,stopanezap,osndop;
var pio,zdrav,nezap,benef,intpio,intzdrav,intnezap,intbenef,olaksice,benef,intbenef,porez,intporez;
var ind1,ind2,ind3,ind4,ind5,ind6,ind7,potrebnasredstva,piop,zdravp,nezapp;
var mess2="Popunite osnovne podatke";

ucitajFirmu();

//smestanje novog layouta u postojeci
var myLayout1 = new dhtmlXLayoutObject({parent: myLayout.cells("b"), pattern: "2U"});
myLayout1.cells("a").setWidth(700);


var myLayout2 = new dhtmlXLayoutObject({parent: myLayout1.cells("a"), pattern: "3E"});
var myLayout3 = new dhtmlXLayoutObject({parent: myLayout1.cells("b"), pattern: "2E"});

	myLayout2.cells("a").setText("PODACI ZAGLAVLJE");
	myLayout2.cells("a").cell.style.backgroundColor = "#cbc9af";
	
	myLayout2.cells("a").setHeight(170);
	myLayout2.cells("c").setHeight(210);
	myLayout2.cells("b").setText("UNOS SATI");
	myLayout2.cells("b").cell.style.backgroundColor = "#cbc9af";
	myLayout2.cells("c").setText("PODACI MESEC - TABELA");
	
	myLayout3.cells("a").setText("ISPLATNI LISTIĆ");
	myLayout3.cells("a").cell.style.backgroundColor = "#cbc9af";
	myLayout3.cells("b").setText("KREDITI - TABELA");
	myLayout3.cells("b").setHeight(210);
	
	myForm = myLayout2.cells("b").attachForm(formData);

	myForm.attachEvent("onBlur", function(koji){
		if (koji == "sifra"){
			var ppsifra = myForm.getItemValue("sifra");
			if (ppsifra != null && ppsifra.toString().length>0){
				prikaziRadnika(2);
				myForm.setItemFocus("cas1");
			}else{
				myForm.setItemFocus("sifra");
			}
		}
	});
	
	//glavni dogadjaji u formi za obradu plate
	myForm.attachEvent("onButtonClick", function(name){
		if(name=="novi"){
			myLayout2.cells("b").setText("UNOS SATI");
			myForm.clear();
			myFormListic.clear();
			myGridKrediti.clearAll();
			myForm.setItemFocus("sifra");
		}else if(name=="unesi"){
			Unesi(1);
		}
		else if(name=="izmeni"){
			//za izmenu koristi istu funkciju sa markerom 2
			Unesi(2);
		}
		else if(name=="brisi"){
			Brisi();
		}
		else if(name=="radnici"){
			doWindow();
		}
	});
	
	myFormListic = myLayout3.cells("a").attachForm(formDataListic);
		
	myFormZaglavlje = myLayout2.cells("a").attachForm(formDataZaglavlje);
	
	myToolbar = myLayout2.cells("b").attachToolbar();
	myToolbar.addText("tekst1", 1, "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
	myToolbar.addText("tekst2", 2, "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
	myToolbar.addText("tekst5", 4, "ČASOVI&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;IZNOS");
	myToolbar.addText("tekst6", 5, "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
	myToolbar.addText("tekst7", 6, "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
	myToolbar.addText("tekst9", 9, "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
	myToolbar.addText("tekst11", 10, "ČASOVI&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;IZNOS");
	
	myToolbar1 = myLayout3.cells("a").attachToolbar();
	myToolbar1.addText("tekst1", 1, "&nbsp;");
	myToolbar1.addButton("stampalistic", 10, "Štampa listić", "", "");
	myToolbar1.setItemImage("stampalistic","/common/imgs/print.gif");
	myToolbar1.attachEvent("onClick", function(id) {
		if(id == "stampalistic"){
			stampaListic();
		}

	});		
	myToolbar1.addText("tekst1", 11, "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
	myToolbar1.addButton("stampa", 12, "Formira XML", "", "");
	myToolbar1.setItemImage("stampa","/common/imgs/new.gif");
	myToolbar1.attachEvent("onClick", function(id) {
		if(id == "stampa"){
			popuniXML();
		}

	});	

	myGrid = myLayout2.cells("c").attachGrid();
	myGrid.setIconsPath('../codebase/imgs/dhxgrid_material/');
	myGrid.setHeader("Šifra,Ime,Prezime,Mesec,Br.obrade,Vrsta plate");//set column names
	myGrid.setInitWidths("60,120,150,100,100,100");//set column width in px
	myGrid.setColAlign("left,left,left,left,left,left");//set column values align
	myGrid.setColTypes("ro,ro,ro,ro,ro,ro");//set column types
	myGrid.setColSorting("int,str,str,int,int,int");//set sorting
	myGrid.setStyle("background-color:#6175ea;color:white; font-weight:bold;", "","", "");
	myGrid.enableRowsHover(true, "myhover");

	myGrid.setIconsPath('../resources/codebase/imgs/');
	myGrid.init();
	myGrid.enableAlterCss("even","uneven");
	myGrid.attachEvent("onRowSelect",doOnRowSelected);
	myGrid.load("/unosapi/unosprikaz?mesec=99","json");
	
	myGridKrediti = myLayout3.cells("b").attachGrid();
	myGridKrediti.setIconsPath('../codebase/imgs/dhxgrid_material/');
	myGridKrediti.setHeader("Šifra,Naziv kredita,Dug,Rata");//set column names
	myGridKrediti.setInitWidths("60,200,100,100");//set column width in px
	myGridKrediti.setColAlign("left,left,right,right");//set column values align
	myGridKrediti.setColTypes("ro,ro,edn,edn");//set column types
	myGridKrediti.setStyle("background-color:#6175ea;color:white; font-weight:bold;", "","", "");
	myGridKrediti.enableRowsHover(true, "myhover");

	myGridKrediti.init();
	myGrid.enableAlterCss("even","uneven");
	myGridKrediti.load("/unosapi/kredprikaz?mesec=99","json");

	myFormZaglavlje.attachEvent("onButtonClick", function(name){
		if(name=="noviunos"){
			myForm.clear();
			myFormListic.clear();
			myFormZaglavlje.clear();
			myGrid.clearAll();
			myGridKrediti.clearAll();
			myFormZaglavlje.setItemFocus("kombobox");
			myFormZaglavlje.setItemValue("krediti",true);
			myFormZaglavlje.setItemValue("minosnovica",true);

		}else if(name=="unesizaglavlje"){
			proveriPostojiUnos();
		}
	});
	function ucitajFirmu(){
		var ffirma="0";
		$.get("/firma/ucitajfirmu",{}, function(data) {
			var obj = $.parseJSON(JSON.stringify(data));	
			$.each(obj, function() {
				firma = this['firma'];
				godina = this['godina'];
				
			});		
		});
	}
//-----------------------------------------------------------------------------
	function proveriPostojiUnos() {
		mesec = myFormZaglavlje.getItemValue("mesec");
		brojobrade = myFormZaglavlje.getItemValue("brojisplate");
		vrstaplate = myFormZaglavlje.getItemValue("kombobox");
		
		if(proveripolja(1)){
			dhx4.ajax.post("/unosapi/proveriunos/?mesec=" + mesec + "&vrstaplate=" + vrstaplate + "&brojobrade=" + 
					brojobrade + "&godina=" + godina + "&pre=" + firma,
				function(data){
				var xml =  dhx4.s2j(data.xmlDoc.responseText);
				var vrati = xml.rezultat;
				if(vrati!=null){
					//uzimanje elementa status iz root taga
					if (vrati=="0"){
						//poruka("nema podataka");
						myGrid.clearAll();
						myFormZaglavlje.setItemFocus("vrednostboda");
					}else{
						//poruka("ima podataka"+vrati);
						myGrid.load("/unosapi/unosprikaz/?mesec=" + mesec + "&vrstaplate=" + vrstaplate + 
								"&brojobrade=" + brojobrade + "&godina=" + godina + "&pre=" + firma,"json");
					}
				}else{
					poruka("greska:ajax");
				}
			});
		}else{
			poruka("nisu popunjena polja");
		}
	}
//-----------------------------------------------------------------------------
function popuniXML(){
	if (proveripolja(1)){
	
		dhxWins = new dhtmlXWindows();
		
		dhxWins.attachViewportTo("layoutObj");
		
		w2 = dhxWins.createWindow("w2", 150, 50, 580, 380);
		
		w2.setText("PPP-PD obrazac");
		var myLayoutXML = new dhtmlXLayoutObject({parent:w2, pattern: "1C"});
		myLayoutXML.cells("a").hideHeader();
		myFormXML = myLayoutXML.cells("a").attachForm(formDataXML);	
		
		//uzimanje teksta iz combobox-a
		var opts = myFormZaglavlje.getOptions("kombobox");
		var txt = (opts[opts.selectedIndex].text);		
		
		var brobr = myFormZaglavlje.getItemValue("brojisplate");	
		
		myFormXML.setItemValue("vrstaplate",txt.toString());
		myFormXML.setItemValue("brobrade",brobr.toString());
		myFormXML.setItemValue("oznkonac","K");
		myFormXML.attachEvent("onButtonClick", function(name){
			if(name=="novi"){
				myLayout2.cells("b").setText("UNOS PODATAKA");
				myForm.clear();
				myFormListic.clear();
				myGridKrediti.clearAll();
				myForm.setItemFocus("sifra");
			}else if(name=="formiraj"){
					Formiraj();
			}
			else if(name=="izlaz"){
				dhxWins.unload();
				dhxWins = w2 = null;
			}
		});
	}else{
		poruka("Polja zaglavlja nisu popunjena");
	}
}
//--------------------------------------------------------------------------------
function doWindow(){
	dhxWins = new dhtmlXWindows();
	
	dhxWins.attachViewportTo("layoutObj");
	
	w1 = dhxWins.createWindow("w1", 150, 50, 600, 500);
	
	w1.setText("Šifarnik radnika");
	var myLayoutRadnici = new dhtmlXLayoutObject({parent:w1, pattern: "1C"});
	myLayoutRadnici.cells("a").hideHeader();
	
	myToolbarRadnici = myLayoutRadnici.cells("a").attachToolbar();
	myToolbarRadnici.addText("tekst", 5, "Traži po prezimenu");
	myToolbarRadnici.addInput("txttrazi", 6, "",100);
	myToolbarRadnici.addButton("trazi", 7, "Traži", "", "");
	myToolbarRadnici.addButton("svi", 8, "Svi", "", "");	
	myToolbarRadnici.setItemImage("trazi","/common/imgs/search.gif");
	myToolbarRadnici.setItemImage("svi","/common/imgs/tombs.gif");
	
	myToolbarRadnici.attachEvent("onClick", function(id) {
		if(id == "trazi"){
			var uslov = myToolbarRadnici.getValue("txttrazi");
			var struslov = uslov.toString();			
			if(struslov.trim().length>0){
				myGridRadnici.clearAll();
				myGridRadnici.load("/maticna/prikaz?prezime="+struslov+"&pre="+firma,"json");
			}
		}
		else if(id == "svi"){
			myGridRadnici.clearAll();
			myGridRadnici.load("/maticna/prikaz?prezime=&pre="+firma,"json");
		}

	});	
	myGridRadnici = myLayoutRadnici.cells("a").attachGrid();
	myGridRadnici.setImagePath("../resources/codebase/imgs/");//path to images required by grid
	myGridRadnici.setHeader("Šifra,Ime,Prezime");//set column names
	myGridRadnici.setInitWidths("60,200,200");//set column width in px
	myGridRadnici.setColAlign("left,left,left");//set column values align
	myGridRadnici.setColTypes("ro,ro,ro");//set column types
	myGridRadnici.setColSorting("int,str,str");//set sorting
	myGridRadnici.setStyle("background-color:#6175ea;color:white; font-weight:bold;", "","", "");
	myGridRadnici.enableRowsHover(true, "myhover");
	myGridRadnici.init();
	myGridRadnici.attachEvent("onRowSelect",doOnRowSelectedRadnici);
	myGrid.enableAlterCss("even","uneven");
	myGridRadnici.load("/maticna/prikaz?prezime=&pre=" + firma,"json");
}
//-----------------------------------------------------------------------------
function doOnRowSelected(){
	prikaziRadnika(1);
}
//-----------------------------------------------------------------------------
function doOnRowSelectedRadnici(){
	var red = myGridRadnici.getSelectedRowId();
	sifra = myGridRadnici.cells(red,0).getValue();
	var imee,prezimee;
	imee = myGridRadnici.cells(red,1).getValue();
	prezimee = myGridRadnici.cells(red,2).getValue();
	myLayout2.cells("b").setText("UNOS SATI :&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + imee + "&nbsp;&nbsp; " + prezimee);
	
	myForm.clear();
	myForm.setItemValue("sifra",sifra); 
	if(proveripolja(1)){
		prikaziRadnika(2);
	}
	
	myForm.setItemFocus("cas1");
	dhxWins.unload();
	dhxWins = w1 = null;

}
//-----------------------------------------------------------------------------
function prikaziRadnika(x){
	if (x == 1){
		//uzimanje parametara iz grida
		var red = myGrid.getSelectedRowId();
		sifra = myGrid.cells(red,0).getValue();
		mesec = myGrid.cells(red,3).getValue();
		vrstaplate = myGrid.cells(red,5).getValue();
		brojobrade = myGrid.cells(red,4).getValue();
		var imee,prezimee;
		imee = myGrid.cells(red,1).getValue();
		prezimee = myGrid.cells(red,2).getValue();
		myLayout2.cells("b").setText("UNOS SATI :&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + imee + "&nbsp;&nbsp; " + prezimee);
		
		prikaziRadnikaPom(sifra,mesec,vrstaplate,brojobrade);
	}else{
		//uzimanje parametara iz forme zaglavlje i sifre radnika
		sifra =  myForm.getItemValue("sifra").toString();
		mesec =  myFormZaglavlje.getItemValue("mesec").toString();		
		vrstaplate =  myFormZaglavlje.getItemValue("kombobox").toString();		
		brojobrade =  myFormZaglavlje.getItemValue("brojisplate").toString();	
		prikaziRadnikaPom(sifra,mesec,vrstaplate,brojobrade);
	}
	
}
function prikaziRadnikaPom(_sifra,_mesec,_vrstaplate,_brojobrade){	
	myFormListic.clear();
	
	//JQuery poziv na iscitavanje podataka jednog radnika kao i prikaz u formu
	$.get("/unosapi/prikazjedan/?sifra="+_sifra+"&mesec=" + _mesec + "&vrstaplate=" + _vrstaplate + "&brojobrade=" + 
			_brojobrade + "&godina=" + godina + "&pre=" + firma, 
			{}, function(data) {
		var ime='';
		var obj = $.parseJSON(JSON.stringify(data));	
		$.each(obj, function() {
			ime = this['ime'];
			myForm.setItemValue("sifra",this['sifra']); 
			myForm.setItemValue("cas1",this['c1']); 
			myForm.setItemValue("bod1",this['b1']); 
			myForm.setItemValue("cas2",this['c2']); 
			myForm.setItemValue("cas3",this['c3']); 
			myForm.setItemValue("cas4",this['c4']); 
			myForm.setItemValue("cas5",this['c5']); 
			myForm.setItemValue("cas6",this['c6']); 
			myForm.setItemValue("cas7",this['c7']); 
			myForm.setItemValue("cas8",this['c8']); 
			myForm.setItemValue("cas9",this['c9']); 
			myForm.setItemValue("cas10",this['c10']); 
			myForm.setItemValue("bod11",this['b11']); 
			myForm.setItemValue("cas12",this['c12']); 
			myForm.setItemValue("cas13",this['c13']); 
			myForm.setItemValue("bod13",this['b13']); 
			myForm.setItemValue("cas14",this['c14']); 
			myForm.setItemValue("bod14",this['b14']); 
			myForm.setItemValue("cas15",this['c15']); 
			myForm.setItemValue("cas16",this['c16']); 
			myForm.setItemValue("cas18",this['c18']); 
			myForm.setItemValue("cas19",this['c19']); 
			myForm.setItemValue("bod19",this['b19']); 
			myForm.setItemValue("bod20",this['b20']); 
			myForm.setItemValue("cas21",this['c21']); 
			myForm.setItemValue("bod21",this['b21']); 
			myForm.setItemValue("cas22",this['kalbrdana']); 
			myForm.setItemValue("cas23",this['procangazovanja']); 
			//---------------------------------------------------
			myFormZaglavlje.setItemValue("kombobox",this['vrstaplate']); 
			myFormZaglavlje.setItemValue("brojisplate",this['brojobrade']); 
			myFormZaglavlje.setItemValue("mesec",this['mesec']); 
			myFormZaglavlje.setItemValue("vrednostboda",this['vrednostboda']); 
			myFormZaglavlje.setItemValue("fondsatiobracun",this['fondsatizaobracun']); 
			myFormZaglavlje.setItemValue("fondsatimesec",this['fondsatiumesecu']); 
			myFormZaglavlje.setItemValue("topliobrok",this['topliobrok']); 
			myFormZaglavlje.setItemValue("neoporeziviiznos",this['porolak']); 
			myFormZaglavlje.setItemValue("procenatplate",this['procenat']); 
			myFormZaglavlje.setItemValue("krediti",this['krediti']); 
			myFormZaglavlje.setItemValue("minosnovica",this['minosn']);
			
			//ispis imena radnika na layout iz tabele binmesec
			myLayout2.cells("b").setText("UNOS SATI :&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + this['ime'] + "&nbsp;&nbsp; " + this['prezime']);
		
		});		
	});
	poravnajListic();
	//JQuery poziv na iscitavanje podataka jednog radnika kao i prikaz u formu
	$.get("/unosapi/prikazldmes/?sifra="+_sifra+"&mesec=" + _mesec + "&vrstaplate=" + _vrstaplate + "&brojobrade=" + 
			_brojobrade + "&godina=" + godina + "&pre=" + firma, 
			{}, function(data) {
		var obj = $.parseJSON(JSON.stringify(data));	
		$.each(obj, function() {
			myFormListic.setItemValue("minulirad",this['gs']); 
			myFormListic.setItemValue("cass1",this['c1']);
			myFormListic.setItemValue("bodd1",this['i1']); 
			myFormListic.setItemValue("cass2",this['c2']); 
			myFormListic.setItemValue("bodd2",this['i2']); 
			myFormListic.setItemValue("cass3",this['c3']); 
			myFormListic.setItemValue("bodd3",this['i3']); 
			myFormListic.setItemValue("cass4",this['c4']); 
			myFormListic.setItemValue("bodd4",this['i4']); 
			myFormListic.setItemValue("cass5",this['c5']); 
			myFormListic.setItemValue("bodd5",this['i5']); 
			myFormListic.setItemValue("cass6",this['c6']); 
			myFormListic.setItemValue("bodd6",this['i6']); 
			myFormListic.setItemValue("cass7",this['c7']); 
			myFormListic.setItemValue("bodd7",this['i7']); 
			myFormListic.setItemValue("cass8",this['c8']); 
			myFormListic.setItemValue("bodd8",this['i8']); 
			myFormListic.setItemValue("cass9",this['c9']); 
			myFormListic.setItemValue("bodd9",this['i9']); 
			myFormListic.setItemValue("cass10",this['c10']); 
			myFormListic.setItemValue("bodd10",this['i10']); 
			myFormListic.setItemValue("cass11",this['c11']); 
			myFormListic.setItemValue("bodd11",this['i11']); 
			myFormListic.setItemValue("cass12",this['c12']); 
			myFormListic.setItemValue("bodd12",this['i12']); 
			myFormListic.setItemValue("cass13",this['c13']); 
			myFormListic.setItemValue("bodd13",this['i13']); 
			myFormListic.setItemValue("cass14",this['c14']); 
			myFormListic.setItemValue("bodd14",this['i14']); 
			myFormListic.setItemValue("cass15",this['c15']); 
			myFormListic.setItemValue("bodd15",this['i15']); 
			myFormListic.setItemValue("cass16",this['c16']); 
			myFormListic.setItemValue("bodd16",this['i16']);
			myFormListic.setItemValue("bodd17",this['i17']); 
			myFormListic.setItemValue("cass18",this['c18']); 
			myFormListic.setItemValue("bodd18",this['i18']); 
			myFormListic.setItemValue("cass19",this['c19']); 
			myFormListic.setItemValue("bodd19",this['i19']); 
			myFormListic.setItemValue("cass20",this['c20']); 
			myFormListic.setItemValue("bodd20",this['i20']); 
			myFormListic.setItemValue("cass21",this['c21']); 
			myFormListic.setItemValue("bodd21",this['i21']); 
			//----------------------------------------------
			myFormListic.setItemValue("cass22",this['i22']); 
			myFormListic.setItemValue("cass24",this['i24']); 
			myFormListic.setItemValue("cass26",this['i26']); 
			myFormListic.setItemValue("cass27",this['i27']); 
			myFormListic.setItemValue("cass28",this['i28']); 
			myFormListic.setItemValue("cass29",this['i29']); 
			myFormListic.setItemValue("cass30",this['i30']); 
			myFormListic.setItemValue("cass31",this['i31']); 
			myFormListic.setItemValue("cass32",this['i32']); 
			myFormListic.setItemValue("cass33",this['i33']); 
			myFormListic.setItemValue("cass34",this['i34']); 
			myFormListic.setItemValue("cass35",this['i35']); 
			myFormListic.setItemValue("cass36",this['i36']); 

			osndop = parseFloat(this['osndop']); 
			benef = parseFloat(this['dopben']); 
			stopapio = parseFloat(this['p1']);
			stopazdrav = parseFloat(this['p2']);
			stopanezap = parseFloat(this['p3']);
			porez = parseFloat(this['i25']); 
			potrebnasredstva = parseFloat(this['i37']); 
			//doprinosi poslodavca-------------
			piop = parseFloat(this['p11']); 
			zdravp = parseFloat(this['p22']); 
			nezapp = parseFloat(this['p33']); 
			
			olaksice = parseFloat(this['br']); 

			pio = stopapio * osndop;
			zdrav = stopazdrav * osndop;
			nezap = stopanezap * osndop;

			if (olaksice>0){
				ind1 = parseFloat(this['ind1']);
				ind2 = parseFloat(this['ind2']);
				ind3 = parseFloat(this['ind3']);
				ind4 = parseFloat(this['ind4']);
				ind5 = parseFloat(this['ind5']);
				ind6 = parseFloat(this['ind6']);
				ind7 = parseFloat(this['ind7']);
				
				potrebnasredstva = potrebnasredstva - pio*(ind1/100) - zdrav*(ind2/100) -
				nezap*(ind3/100) - piop*(ind4/100) - zdravp*(ind5/100) -
				nezapp*(ind6/100) - porez*(ind7/100);
				
				pio = pio - pio*(ind1/100);	
				zdrav = zdrav - zdrav*(ind2/100);
				nezap = nezap - nezap*(ind3/100); 
				porez = porez - porez*(ind7/100); 
				
			}
			
			//zaokruzenja----------------
			intpio = Math.round(pio);
			intzdrav = Math.round(zdrav);
			intnezap = Math.round(nezap);
			intbenef = Math.round(benef);
			intporez = Math.round(porez);
			potrebnasredstva = Math.round(potrebnasredstva);
			
			var ukudoprinos = intpio + intzdrav + intnezap;

			myFormListic.setItemValue("cass23",ukudoprinos.toString()); 
			myFormListic.setItemValue("cass25",intporez.toString()); 
			myFormListic.setItemValue("cass37",potrebnasredstva.toString()); 

			myFormListic.setItemValue("cass38",intpio.toString()); 
			myFormListic.setItemValue("cass39",intzdrav.toString());  
			myFormListic.setItemValue("cass40",intnezap.toString()); 
			myFormListic.setItemValue("cass41",intbenef.toString()); 
			
		});		
	});
	myGridKrediti.clearAll();
	myGridKrediti.load("/unosapi/kredprikaz?sifra="+_sifra+"&mesec="+_mesec+"&vrstaplate="+_vrstaplate+"&brojobrade="+
			_brojobrade + "&godina=" + godina + "&pre=" + firma,"json");
}
//-----------------------------------------------------------------------------
function brisislog(){
	dhtmlx.modalbox({
		title: "Brisanje",
		text: "Da li zaista brišete ove podatke",
		buttons:["Ok","Odustani"],
		callback:function(r){
			if(r==0){
				obradi(3);
			}
		}
	});
}
//-----------------------------------------------------------------------------
function Unesi(xx) {
	var mess1="";
	mess1="Upis nije uspeo";
	mess2="Popunite osnovne podatke";
	
	var kojaobrada = xx;
	
	sifra = myForm.getItemValue("sifra");
	vrstaplate = myFormZaglavlje.getItemValue("kombobox");
	mesec = myFormZaglavlje.getItemValue("mesec");
	brojobrade = myFormZaglavlje.getItemValue("brojisplate");	

	//poruka("sifra="+sifra);
	//---------------------------------------------------------------
	var topliobrok = myFormZaglavlje.getItemValue("topliobrok").toString();
		if(topliobrok.trim().length == 0) {topliobrok="0";}
	var fondsatiobracun = myFormZaglavlje.getItemValue("fondsatiobracun").toString();
		if(fondsatiobracun.trim().length == 0) {fondsatiobracun="0";}
	var fondsatimesec = myFormZaglavlje.getItemValue("fondsatimesec").toString();
		if(fondsatimesec.trim().length == 0) {fondsatimesec="0";}
	var porolak = myFormZaglavlje.getItemValue("neoporeziviiznos").toString();
		if(porolak.trim().length == 0) {porolak="0";}
	var procenat = myFormZaglavlje.getItemValue("procenatplate").toString();
		if(procenat.trim().length == 0) {procenat="0";}
	var krediti = myFormZaglavlje.getItemValue("krediti").toString();
		if(krediti.trim().length == 0) {krediti="0";}
	var vrednostboda = myFormZaglavlje.getItemValue("vrednostboda").toString();
		if(vrednostboda.trim().length == 0) {vrednostboda="0";}
	
	var cas1 = myForm.getItemValue("cas1").toString();
		if(cas1.trim().length == 0) {cas1="0";}
	var bod1 = myForm.getItemValue("bod1").toString();
		if(bod1.trim().length == 0) {bod1="0";}
	var cas2 = myForm.getItemValue("cas2").toString();
		if(cas2.trim().length == 0) {cas2="0";}
	var cas3 = myForm.getItemValue("cas3").toString();
		if(cas3.trim().length == 0) {cas3="0";}
	var cas4 = myForm.getItemValue("cas4").toString();
		if(cas4.trim().length == 0) {cas4="0";}
	var cas5 = myForm.getItemValue("cas5").toString();
		if(cas5.trim().length == 0) {cas5="0";}
	var cas6 = myForm.getItemValue("cas6").toString();
		if(cas6.trim().length == 0) {cas6="0";}
	var cas7 = myForm.getItemValue("cas7").toString();
		if(cas7.trim().length == 0) {cas7="0";}
	var cas8 = myForm.getItemValue("cas8").toString();
		if(cas8.trim().length == 0) {cas8="0";}
	var cas9 = myForm.getItemValue("cas9").toString();
		if(cas9.trim().length == 0) {cas9="0";}
	var cas10 = myForm.getItemValue("cas10").toString();
		if(cas10.trim().length == 0) {cas10="0";}
	var bod11 = myForm.getItemValue("bod11").toString();
		if(bod11.trim().length == 0) {bod11="0";}
	var cas12 = myForm.getItemValue("cas12").toString();
		if(cas12.trim().length == 0) {cas12="0";}
	var bod12 = myForm.getItemValue("bod12").toString();
		if(bod12.trim().length == 0) {bod12="0";}
	var cas13 = myForm.getItemValue("cas13").toString();
		if(cas13.trim().length == 0) {cas13="0";}
	var bod13 = myForm.getItemValue("bod13").toString();
		if(bod13.trim().length == 0) {bod13="0";}
	var cas14 = myForm.getItemValue("cas14").toString();
		if(cas14.trim().length == 0) {cas14="0";}
	var bod14 = myForm.getItemValue("bod14").toString();
		if(bod14.trim().length == 0) {bod14="0";}
	var cas15 = myForm.getItemValue("cas15").toString();
		if(cas15.trim().length == 0) {cas15="0";}
	var cas16 = myForm.getItemValue("cas16").toString();
		if(cas16.trim().length == 0) {cas16="0";}
	var cas18 = myForm.getItemValue("cas18").toString();
		if(cas18.trim().length == 0) {cas18="0";}
	var cas19 = myForm.getItemValue("cas19").toString();
		if(cas19.trim().length == 0) {cas19="0";}
	var bod19 = myForm.getItemValue("bod19").toString();
		if(bod19.trim().length == 0) {bod19="0";}
	var bod20 = myForm.getItemValue("bod20").toString();
		if(bod20.trim().length == 0) {bod20="0";}
	var cas21 = myForm.getItemValue("cas21").toString();
		if(cas21.trim().length == 0) {cas21="0";}
	var bod21 = myForm.getItemValue("bod21").toString();
		if(bod21.trim().length == 0) {bod21="0";}
	var cas22 = myForm.getItemValue("cas22").toString();
		if(cas22.trim().length == 0) {cas22="0";}
	var cas23 = myForm.getItemValue("cas23").toString();
		if(cas23.trim().length == 0) {cas23="0";}
	//---------------------------------------------------------------
	var dataajax = "?mesec=" + mesec.toString();
	dataajax = dataajax + "&pre=" + firma.toString();
	dataajax = dataajax + "&godina=" + godina.toString();
	dataajax = dataajax + "&vrstaplate=" + vrstaplate.toString();
	dataajax = dataajax + "&brojobrade=" + brojobrade.toString();
	dataajax = dataajax + "&sifra=" + sifra.toString();
	dataajax = dataajax + "&vrednostboda=" + vrednostboda;
	dataajax = dataajax + "&topliobrok=" + topliobrok;
	dataajax = dataajax + "&fondsatiobracun=" + fondsatiobracun;
	dataajax = dataajax + "&fondsatimesec=" + fondsatimesec;
	dataajax = dataajax + "&porolak=" + porolak;
	dataajax = dataajax + "&procenat=" + procenat;
	dataajax = dataajax + "&krediti=" + krediti;
	dataajax = dataajax + "&minosn=" + myFormZaglavlje.getItemValue("minosnovica").toString();
	
	dataajax = dataajax + "&cas1=" + cas1;
	dataajax = dataajax + "&bod1=" + bod1;
	dataajax = dataajax + "&cas2=" + cas2;
	dataajax = dataajax + "&cas3=" + cas3;
	dataajax = dataajax + "&cas4=" + cas4;
	dataajax = dataajax + "&cas5=" + cas5;
	dataajax = dataajax + "&cas6=" + cas6;
	dataajax = dataajax + "&cas7=" + cas7;
	dataajax = dataajax + "&cas8=" + cas8;
	dataajax = dataajax + "&cas9=" + cas9;
	dataajax = dataajax + "&cas10=" + cas10;
	dataajax = dataajax + "&bod11=" + bod11;
	dataajax = dataajax + "&cas12=" + cas12;
	dataajax = dataajax + "&bod12=" + bod12;
	dataajax = dataajax + "&cas13=" + cas13;
	dataajax = dataajax + "&bod13=" + bod13;
	dataajax = dataajax + "&cas14=" + cas14;
	dataajax = dataajax + "&bod14=" + bod14;
	dataajax = dataajax + "&cas15=" + cas15;
	dataajax = dataajax + "&cas16=" + cas16;
	dataajax = dataajax + "&cas18=" + cas18;
	dataajax = dataajax + "&cas19=" + cas19;
	dataajax = dataajax + "&bod19=" + bod19;
	dataajax = dataajax + "&bod20=" + bod20;
	dataajax = dataajax + "&cas21=" + cas21;
	dataajax = dataajax + "&bod21=" + bod21;
	dataajax = dataajax + "&cas22=" + cas22;
	dataajax = dataajax + "&cas23=" + cas23;
	
	//salje podatak koja je obrada (unos ili izmena podataka)
	dataajax = dataajax + "&kojaobrada=" + kojaobrada.toString();
	
	if(proveripolja(2)){
		dhx4.ajax.post("/obrada/unossati/"+dataajax,
				function(r){
			var t = dhx4.s2j(r.xmlDoc.responseText);
			var xml = t.toString();
	
			if(xml!=null){
				if (xml=="0"){
					poruka(mess1);
				}else{
					if (kojaobrada == 1){
						poruka("Podaci su upisani");
					}else{
						poruka("Podaci su izmenjeni");
					}
					
					//myForm.clear();
					myFormListic.clear();
					myGrid.clearAll();
					myGridKrediti.clearAll();
					myGrid.load("/unosapi/unosprikaz/?mesec=" + mesec + "&vrstaplate=" + vrstaplate + 
					"&brojobrade=" + brojobrade + "&godina=" + godina + "&pre=" + firma,"json");
					prikaziRadnika(2);
				}
			}else{
				poruka("greska:ajax");
			}
		});
	}else{
		poruka(mess2);
	}
}
//-----------------------------------------------------------------------------
function Izmeni() {
	var mess1="";
	mess1="Izmena nije uspela";
	mess2="Popunite osnovne podatke";

	if(proveripolja(2)){

	}else{
		poruka(mess2);
	}
}
//-----------------------------------------------------------------------------
function Brisi() {
	var mess1="",strsifra,strvrstaplate,strmesec,strbrojobrade,strkrediti;
	sifra = myForm.getItemValue("sifra");
	vrstaplate = myFormZaglavlje.getItemValue("kombobox");
	mesec = myFormZaglavlje.getItemValue("mesec");
	brojobrade = myFormZaglavlje.getItemValue("brojisplate");	
	krediti = myFormZaglavlje.getItemValue("krediti");	
	
	mess1="Brisanje nije uspelo";

	if(proveripolja(2)){
		dhx4.ajax.post("/obrada/deletesati/?sifra=" + sifra.toString() + "&vrstaplate=" + vrstaplate.toString() + 
						"&mesec=" + mesec.toString() + "&brojobrade=" + brojobrade.toString() + 
						"&krediti=" + krediti.toString()+"&pre="+firma.toString()+"&godina="+godina.toString(),
			function(r){
			var t = dhx4.s2j(r.xmlDoc.responseText);
			var xml = t.toString();
			
			if(xml!=null){
				if (xml=="0"){
					poruka("Podaci nisu izbrisani");
				}else{
					myForm.clear();
					myFormListic.clear();
					myGrid.clearAll();
					myGridKrediti.clearAll();
					myGrid.load("/unosapi/unosprikaz/?mesec=" + mesec + "&vrstaplate=" + vrstaplate + 
							"&brojobrade=" + brojobrade + "&godina=" + godina + "&pre=" + firma,"json");
				}
			}else{
				poruka("greska:ajax");
			}
		});
	}else{
		poruka(mess2);
	}
}
//-----------------------------------------------------------------------------
function stampaListic() {
	
	sifra = myForm.getItemValue("sifra");
	vrstaplate = myFormZaglavlje.getItemValue("kombobox");
	mesec = myFormZaglavlje.getItemValue("mesec");
	brojobrade = myFormZaglavlje.getItemValue("brojisplate");	

	if(proveripolja(2)){
		window.open("/print/listic?mesec="+mesec+"&vrstaplate="+vrstaplate+"&brojobrade="+brojobrade+
				"&sifra="+sifra+"&pre="+firma+"&godina="+godina); 
	}else{
		poruka(mess2);
	}
}	
//-----------------------------------------------------------------------------
function proveripolja(x) {
	var aa = true;

	if(mesec == null || mesec.toString().length==0){
		return false;
	}
	if(vrstaplate==null || vrstaplate.toString().length==0){
		return false;
	}
	if(brojobrade == null || brojobrade.toString().length==0){
		return false;
	}
	if(x==2){
		if(sifra==null || sifra.toString().length==0){
			return false;
		}
	}
	return aa;
}
//-----------------------------------------------------------------------------
function Formiraj() {
		var pre,vrstaplate,tipisplatioca,mesec,brojobrade,vrstaprijave,oznakazakonacnu;
		var klijentskaoznakadeklaracije,obracunskiperiod,datumplacanja,najnizaosnovica;

		pre = firma;
		vrstaplate = myFormZaglavlje.getItemValue("kombobox");
		tipisplatioca = myFormXML.getItemValue("tipisplatioca");
		klijentskaoznakadeklaracije = myFormXML.getItemValue("kliozndek");
		vrstaprijave = myFormXML.getItemValue("vrstaprijave");
		oznakazakonacnu = myFormXML.getItemValue("oznkonac");
		obracunskiperiod = myFormXML.getItemValue("obracunskiperiod");
		datumplacanja = myFormXML.getItemValue("datumplacanja");
		najnizaosnovica = myFormXML.getItemValue("najnizaosnovica");
		mesec = myFormZaglavlje.getItemValue("mesec");
		brojobrade = myFormZaglavlje.getItemValue("brojisplate");	
		
		var dataajax = "?pre=" + firma.toString();
		dataajax = dataajax + "&godina=" + godina.toString();
		dataajax = dataajax + "&vrstaplate=" + vrstaplate.toString();
		dataajax = dataajax + "&tipisplatioca=" + tipisplatioca.toString();
		dataajax = dataajax + "&mesec=" + mesec.toString();
		dataajax = dataajax + "&brojobrade=" + brojobrade.toString();
		dataajax = dataajax + "&vrstaprijave=" + vrstaprijave.toString();
		dataajax = dataajax + "&oznakazakonacnu=" + oznakazakonacnu.toString();
		dataajax = dataajax + "&klijentskaoznakadeklaracije=" + klijentskaoznakadeklaracije.toString();
		dataajax = dataajax + "&obracunskiperiod=" + obracunskiperiod.toString();
		dataajax = dataajax + "&datumplacanja=" + datumplacanja.toString();
		dataajax = dataajax + "&najnizaosnovica=" + najnizaosnovica.toString();
	
		window.open("/print/xml"+dataajax); 

}
//-----------------------------------------------------------------------------
function poruka(tekstporuke) {
	dhtmlx.alert({
		title:"Upozorenje",
		type:"alert-error",
		text:tekstporuke
	});
}
