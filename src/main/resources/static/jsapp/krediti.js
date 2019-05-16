
var pre="",naobs="",zirobs="",nadimak="",sifra="",partija="",firma="1",godina="0";
var myGridRadnici,myToolbarRadnici,myToolbarKreditori,myGridKreditori;
//polje nadimak sluzi da se vidi da li je radnik vlasnik firme (D/N) jer ako jeste onda
//se on stavlja na sifru 1

ucitajFirmu();
	
var myLayout1 = new dhtmlXLayoutObject({parent: myLayout.cells("b"), pattern: "3E"});
myLayout1.cells("a").setHeight(100);
myLayout1.cells("b").setHeight(300);
myLayout1.cells("a").setText("RADNIK");
myLayout1.cells("a").cell.style.backgroundColor = "#cbc9af";


myLayout1.cells("b").setText("KREDITI - DETALJI");
myLayout1.cells("b").cell.style.backgroundColor = "#cbc9af";
myLayout1.cells("c").setText("PODACI");

formDataZag = [
	{type: "settings", position: "label-left", labelWidth: 120, inputWidth: 60,labelHeight:20,inputHeight:22},
	{type: "block", offsetTop: 5, list: [
		{type: "button", value: "Novi unos", name:"noviunos", position:"absolute",inputTop: 2,inputLeft:10},
		{type: "input", label: "Šifra radnika: ",name:"sifra", value:"",maxLength: 5,validate:"NotEmpty,ValidInteger",
								position:"absolute", labelTop:10,inputTop:10,labelLeft:160,inputWidth:50,inputLeft:250},
		{type: "button", value: "svi", name:"sviradnici",className: "button_search", position:"absolute",inputTop: 2,inputLeft:300},
		{type: "input", label: "",name:"prezime",  maxLength: 40,disabled:"true",
								position:"absolute",labelTop:10,inputTop:10,inputLeft:370,inputWidth:120},
		{type: "input", label: "",name:"ime",  maxLength: 30,disabled:"true",
									position:"absolute",labelTop:10,inputTop:10,inputLeft:500,inputWidth:120},
		{type: "button", value: "Unesi", name:"unesiradnika", position:"absolute",inputTop: 2,inputLeft:700}
		]},			
];	

formData = [
	{type: "settings", position: "label-left", labelWidth: 130, inputWidth: 60,labelHeight:20,inputHeight:22},
	{type: "block", offsetTop: 5, list: [
		{type: "input", label: "Šifra kreditora: ",name:"sifobs", value:"",maxLength: 5,validate:"NotEmpty,ValidInteger",
								position:"absolute", labelTop:8,inputTop:5,inputLeft:140},
		{type: "button", value: "svi", name:"svikreditori", position:"absolute",inputTop:0,inputLeft:220},
		{type: "input", label: "Naziv kreditora:",name:"nazobs",  maxLength: 30,validate:"NotEmpty",
								position:"absolute",labelTop:30,inputTop:30,inputLeft:140,inputWidth:200},
		{type: "input", label: "Partija kredita:",name:"partija",  maxLength: 40,validate:"NotEmpty",
									position:"absolute",labelTop:55,inputTop:55,inputLeft:140,inputWidth:200},
		{type: "input", label: "Ostatak duga:",name:"dug",  maxLength: 40,
								position:"absolute",labelTop:80,inputTop:80,inputLeft:140,inputWidth:200},
		{type: "input", label: "Rata:",name:"rata",  maxLength: 40,
									position:"absolute",labelTop:105,inputTop:105,inputLeft:140,inputWidth:200},
		
		{type: "select", label: "Način obustava:",name:"naobs",  options:[
						            {text: "DUG-RATA", value: "1"},
						            {text: "Odbija se samo rata", value: "2"}
						        ],position:"absolute",labelTop:130,labelWidth:120,inputTop: 130,inputLeft:140,inputWidth:120,inputHeight:25},

		
		]},			
		{type: "block", blockOffset:0,offsetTop:150, list: [
		{type: "button", value: "Novi", name:"novi", position:"absolute",inputTop: 60,inputLeft:30},
		{type: "button", value: "Unesi", name:"unesi",position:"absolute",inputTop: 60,inputLeft:150},
		{type: "button", value: "Izmeni", name:"izmeni",position:"absolute",inputTop: 60,inputLeft:270},
		{type: "button", value: "Briši", name:"brisi",position:"absolute",inputTop: 60,inputLeft:390}
	]}
];	

myGrid = myLayout1.cells("c").attachGrid();
myGrid.setImagePath("../resources/codebase/imgs/");//path to images required by grid
myGrid.setHeader("Šifra kred.,Naziv,Partija,Dug,Rata,Vrsta otplate");//set column names
myGrid.setInitWidths("100,200,200,120,120,100");//set column width in px
myGrid.setColAlign("left,left,left,right,right,right");//set column values align
myGrid.setColTypes("ro,ro,ro,ro,ro,ro");//set column types
myGrid.setColSorting("int,str,str,str,str,str");//set sorting
myGrid.enableAlterCss("even","uneven");
myGrid.setIconsPath('../resources/codebase/imgs/');
myGrid.setStyle("background-color:#6175ea;color:white; font-weight:bold;", "","", "");
myGrid.enableRowsHover(true, "myhover");
myGrid.init();
myGrid.attachEvent("onRowSelect",doOnRowSelected);
myGrid.load("/krediti/prikaz/?sifra=0&pre="+firma.toString()+"&godina="+godina.toString(),"json");


myFormZag = myLayout1.cells("a").attachForm(formDataZag);
myFormZag.enableLiveValidation(true);

myFormZag.attachEvent("onBlur", function(koji){
	if (koji == "sifra"){
		var ppsifra = myFormZag.getItemValue("sifra");
		if (ppsifra != null && ppsifra.toString().length>0){
			prikaziRadnika(ppsifra);
			myFormZag.setItemFocus("unesiradnika");
		}else{
		}
	}
});

myFormZag.attachEvent("onButtonClick", function(name){
	if(name=="noviunos"){
		myFormZag.clear();
		myForm.clear();
		myFormZag.setItemFocus("sifra");
	}else if(name=="unesiradnika"){
		var ppsifra = myFormZag.getItemValue("sifra");
		if (ppsifra != null && ppsifra.toString().length>0){
			myGrid.clearAll();

			myGrid.load("/krediti/prikaz/?sifra="+ppsifra+"&pre="+firma.toString()+"&godina="+godina.toString(),"json");
			myForm.clear();
			myForm.setItemFocus("novi");
		}else{
			poruka("Radnik nije unešen");
			myFormZag.setItemFocus("sifra");
		}
	}else if(name=="sviradnici"){
		doWindow();		
	}
});

myForm = myLayout1.cells("b").attachForm(formData);
myForm.enableLiveValidation(true);

myForm.attachEvent("onBlur", function(koji){
	if (koji == "sifobs"){
		var ppsifra = myForm.getItemValue("sifobs");
		if (ppsifra != null && ppsifra.toString().length>0){
			prikaziKreditora(ppsifra);
		}else{
			myForm.setItemFocus("sifobs");
		}
	}
});

myForm.attachEvent("onButtonClick", function(name){
	if(name=="novi"){
		myForm.clear();
		myForm.setItemFocus("sifobs");
	}else if(name=="unesi"){
		proveriPostojiUnos();
	}
	else if(name=="izmeni"){
		//za izmenu koristi istu funkciju sa markerom 2
		Unesi(2);
	}
	else if(name=="brisi"){
		Brisi();
	}
	else if(name=="svikreditori"){
		doWindowKreditori();
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

function doOnRowSelected(){
	prikaziKred();
}
function prikaziKreditora(ssifra){
	$.get("/kreditori/prikazjedan/?sifobs="+ssifra,{}, function(data) {
		var obj = $.parseJSON(JSON.stringify(data));	
		$.each(obj, function() {
			myForm.setItemValue("sifobs",this['sifobs']); 
			myForm.setItemValue("nazobs",this['nazobs']); 
			//---------------------------------------------------
		});		
	});
	
}
function prikaziRadnika(ssifra){
	//JQuery poziv na iscitavanje podataka jednog radnika kao i prikaz u formu
	$.get("/maticna/prikazjedan/?sifra="+ssifra.toString()+"&pre="+firma.toString(),{}, function(data) {
		var ime='';
		var obj = $.parseJSON(JSON.stringify(data));	
		$.each(obj, function() {
			myFormZag.setItemValue("prezime",this['prezime'].trim()); 
			myFormZag.setItemValue("ime",this['ime'].trim()); 
			//---------------------------------------------------
		});		
	});
}
function prikaziKREDjedan(ssifobs){

	var ppsifra = myFormZag.getItemValue("sifra");
	
	//JQuery poziv na iscitavanje podataka jednog radnika kao i prikaz u formu
	$.get("/krediti/prikazjedan/?sifobs="+ssifobs+"&sifra="+ppsifra+"&pre="+firma.toString()+
			"&godina="+godina.toString(),{}, function(data) {
		var obj = $.parseJSON(JSON.stringify(data));	
		$.each(obj, function() {
			myForm.setItemValue("sifobs",this['sifobs']); 
			myForm.setItemValue("nazobs",this['nazobs']); 
			myForm.setItemValue("partija",this['partija']); 
			myForm.setItemValue("dug",this['dug']); 
			myForm.setItemValue("rata",this['rata']); 
			myForm.setItemValue("naobs",this['naobs']); 
			//---------------------------------------------------
		});		
	});
}
function prikaziKred(){
	var red = myGrid.getSelectedRowId();
	//uzimanje vrednosti iz grida
	sifobs = myGrid.cells(red,0).getValue();
	partija = myGrid.cells(red,2).getValue();
	sifra = myFormZag.getItemValue("sifra");
	myForm.clear();
	
	//JQuery poziv na iscitavanje podataka jednog radnika kao i prikaz u formu
	$.get("/krediti/prikazjedan/?sifra="+sifra.toString()+"&sifobs="+sifobs+"&partija="+partija+
			"&pre="+firma.toString()+"&godina="+godina.toString(),{}, function(data) {
		var obj = $.parseJSON(JSON.stringify(data));	
		$.each(obj, function() {
			myForm.setItemValue("sifobs",this['sifobs']); 
			myForm.setItemValue("nazobs",this['nazobs']); 
			myForm.setItemValue("partija",this['partija']); 
			myForm.setItemValue("dug",this['dug']); 
			myForm.setItemValue("rata",this['rata']); 
			myForm.setItemValue("naobs",this['naobs']); 
			//---------------------------------------------------
		});		
	});
}
function proveriPostojiUnos(){
	var ssifra = myFormZag.getItemValue("sifra");
	var ssifobs = myForm.getItemValue("sifobs");
	var ppartija = myForm.getItemValue("partija");
	var nnaobs = myForm.getItemValue("naobs");
	if(proveripolja(ssifra,ssifobs,nnaobs,ppartija)){
		$.get("/krediti/provera/?sifra="+ssifra.toString()+"&sifobs="+ssifobs+"&partija="+ppartija+
				"&godina="+godina,{}, function(data) {
			var postoji = 0;
			//poruka(data.toString());
			postoji = parseInt(data.toString());
			if (postoji>0){
				poruka("Podaci za ovaj kredit već postoje");
				//return true;
			}else{
				//poruka("ne postoji");
				Unesi(1);
				//return false;
			}
		
		});
	}else{
		poruka("Podaci nisu popunjeni");
	}
}
function Unesi(xx){
	//od parametra xx zavisi da li je unos podataka ili izmena podataka
	var mess1="",strurl="";
	if(xx==1){
		strurl="/krediti/unos/";
		mess1="Upis nije uspeo";
	}else{
		strurl="/krediti/izmena/";
		mess1="Izmena nije uspela";
	}
			
	mess2="Popunite osnovne podatke";
	sifobs = myForm.getItemValue("sifobs");

	var nazobs = myForm.getItemValue("nazobs").toString();
		if(nazobs.trim().length == 0) {nazobs=" ";}
	var partija = myForm.getItemValue("partija").toString();
		if(partija.trim().length == 0) {partija="";}
	var dug = myForm.getItemValue("dug").toString();
		if(dug.trim().length == 0) {dug=" ";}
	var rata = myForm.getItemValue("rata").toString();
		if(rata.trim().length == 0) {rata=" ";}
	var naobs = myForm.getItemValue("naobs").toString();
		if(naobs.trim().length == 0) {naobs=" ";}
	//---------------------------------------------------------------
	//parametri koji se prosledjuju serveru na obradu
	sifra = myFormZag.getItemValue("sifra");
	var dataajax = "?sifra=" + sifra;
	dataajax = dataajax + "&sifobs=" + sifobs;
	dataajax = dataajax + "&nazobs=" + nazobs;
	dataajax = dataajax + "&partija=" + partija;
	dataajax = dataajax + "&dug=" + dug;
	dataajax = dataajax + "&rata=" + rata;
	dataajax = dataajax + "&naobs=" + naobs;
	dataajax = dataajax + "&pre=" + firma;
	dataajax = dataajax + "&godina=" + godina;
		
	if(proveripolja(sifra,sifobs,naobs,partija)){
		
		dhx4.ajax.post(strurl+dataajax,
				function(r){
			var t = dhx4.s2j(r.xmlDoc.responseText);
			var xml = t.toString();
	
			if(xml!=null){
				if (xml=="0"){
					poruka(mess1);
				}else{
					if(xx==1){
						poruka("Podaci su upisani");
					}else{
						poruka("Podaci su izmenjeni");
					}
					myForm.clear();
					myGrid.clearAll();
					myGrid.load("/krediti/prikaz/?sifra="+sifra+"&pre="+firma+"&godina="+godina,"json");
				}
			}else{
				poruka("greska:ajax");
			}
		});
	}else{
		poruka(mess2);
	}
}
function Brisi(){
	dhtmlx.modalbox({
			title: "Brisanje",
			text: "Da li zaista brišete ove podatke",
			buttons:["Ok","Odustani"],
			callback:function(r){
				if(r==0){
					ObrisiSlog();
				}
			}
	});
}
function ObrisiSlog(){
	var mess1="",strsifobs;
	sifra = myFormZag.getItemValue("sifra");
	sifobs = myForm.getItemValue("sifobs");
	partija = myForm.getItemValue("partija");
	strsifobs = sifobs.toString();
		
	mess1="Brisanje nije uspelo";

	if(sifobs!=null && strsifobs.trim().length>0){
		dhx4.ajax.post("/krediti/brisanje/?sifra=" + sifra.toString() + "&sifobs=" + strsifobs.toString() + 
				"&partija="+partija.toString()+"&pre="+firma+"&godina="+godina,
			function(r){
			var t = dhx4.s2j(r.xmlDoc.responseText);
			var xml = t.toString();
			
			if(xml!=null){
				if (xml=="0"){
					poruka("Podaci nisu izbrisani");
				}else{
					myForm.clear();
					myGrid.clearAll();
					myGrid.load("/krediti/prikaz/?sifra="+sifra+"&pre="+firma.toString()+"&godina="+godina.toString(),"json");
				}
			}else{
				poruka("greska:ajax");
			}
		});
	}else{
		poruka(mess2);
	}
}
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
			myGridRadnici.load("/maticna/prikaz?prezime= &pre="+firma,"json");
		}
	});	
	myGridRadnici = myLayoutRadnici.cells("a").attachGrid();
	myGridRadnici.setImagePath("../resources/codebase/imgs/");
	myGridRadnici.setHeader("Šifra,Ime,Prezime");
	myGridRadnici.setInitWidths("60,200,200");
	myGridRadnici.setColAlign("left,left,left");
	myGridRadnici.setColTypes("ro,ro,ro");
	myGridRadnici.setColSorting("int,str,str");
	myGridRadnici.setStyle("background-color:#6175ea;color:white; font-weight:bold;", "","", "");
	myGridRadnici.init();
	myGridRadnici.enableAlterCss("even","uneven");
	myGridRadnici.attachEvent("onRowSelect",doOnRowSelectedRadnici);
	myGridRadnici.load("/maticna/prikaz?prezime= &pre="+firma,"json");
}
function doOnRowSelectedRadnici(){
	var red = myGridRadnici.getSelectedRowId();
	sifra = myGridRadnici.cells(red,0).getValue();
	var imee,prezimee;
	imee = myGridRadnici.cells(red,1).getValue();
	prezimee = myGridRadnici.cells(red,2).getValue();
	myFormZag.clear();
	myFormZag.setItemValue("sifra",sifra); 
	myFormZag.setItemValue("ime",imee); 
	myFormZag.setItemValue("prezime",prezimee); 
	myFormZag.setItemFocus("unesiradnika");
	dhxWins.unload();
	dhxWins = w1 = null;
}
function doWindowKreditori(){
	dhxWins = new dhtmlXWindows();
	dhxWins.attachViewportTo("layoutObj");
	w1 = dhxWins.createWindow("w1", 150, 50, 600, 500);
	
	w1.setText("Šifarnik kreditora");
	var myLayoutKreditori = new dhtmlXLayoutObject({parent:w1, pattern: "1C"});
	myLayoutKreditori.cells("a").hideHeader();
	
	myToolbarKreditori = myLayoutKreditori.cells("a").attachToolbar();
	myToolbarKreditori.addText("tekst", 5, "Traži po nazivu");
	myToolbarKreditori.addInput("txttrazi", 6, "",100);
	myToolbarKreditori.addButton("trazi", 7, "Traži", "", "");
	myToolbarKreditori.addButton("svi", 8, "Svi", "", "");	
	myToolbarKreditori.setItemImage("trazi","/common/imgs/search.gif");
	myToolbarKreditori.setItemImage("svi","/common/imgs/tombs.gif");
	
	myToolbarKreditori.attachEvent("onClick", function(id) {
		if(id == "trazi"){
			var uslov = myToolbarKreditori.getValue("txttrazi");
			var struslov = uslov.toString();			
			if(struslov.trim().length>0){
				myGridKreditori.clearAll();
				myGridKreditori.load("/kreditori/prikaz/?naziv="+struslov,"json");
			}
		}
		else if(id == "svi"){
			myGridKreditori.clearAll();
			myGridKreditori.load("/kreditori/prikaz/?naziv= ","json");
		}
	});	
	myGridKreditori = myLayoutKreditori.cells("a").attachGrid();
	myGridKreditori.setImagePath("../resources/codebase/imgs/");//path to images required by grid
	myGridKreditori.setHeader("Šifra,Naziv,Žiro račun,Poziv na broj,Način obustava");//set column names
	myGridKreditori.setInitWidths("100,200,200,200,200");//set column width in px
	myGridKreditori.setColAlign("left,left,left,left,left");//set column values align
	myGridKreditori.setColTypes("ro,ro,ro,ro,ro");//set column types
	myGridKreditori.setColSorting("int,str,str,str,str");//set sorting

	myGridKreditori.setIconsPath('../resources/codebase/imgs/');
	myGridKreditori.setStyle("background-color:#6175ea;color:white; font-weight:bold;", "","", "");
	myGridKreditori.init();
	myGridKreditori.enableAlterCss("even","uneven");
	myGridKreditori.attachEvent("onRowSelect",doOnRowSelectedKreditori);
	myGridKreditori.load("/kreditori/prikaz/?naziv= ","json");
}
function doOnRowSelectedKreditori(){
	var red = myGridKreditori.getSelectedRowId();
	sifra = myGridKreditori.cells(red,0).getValue();
	var nazivv;
	nazivv = myGridKreditori.cells(red,1).getValue();
	myForm.clear();
	myForm.setItemValue("sifobs",sifra); 
	myForm.setItemValue("nazobs",nazivv); 
	myForm.setItemFocus("partija");
	dhxWins.unload();
	dhxWins = w1 = null;
}

function proveripolja(ssifra,psifobs,nnaobs,ppartija) {
	var aa = true;
	if(ssifra == null || ssifra.toString().length==0){
		aa = false;
		return false;
	}
	if(psifobs == null || psifobs.toString().length==0){
		aa = false;
		return false;
	}
	if(nnaobs==null || nnaobs.toString().length==0){
		aa = false;
		return false;
	}
	if(ppartija==null || ppartija.toString().length==0){
		aa = false;
		return false;
	}	return aa;
}
function poruka(tekstporuke) {
	dhtmlx.alert({
		title:"Upozorenje",
		type:"alert-error",
		text:tekstporuke
	});
}