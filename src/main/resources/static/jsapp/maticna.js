
var sifra="",ime="",prezime="",firma="0",godina="0";

var myLayout1 = new dhtmlXLayoutObject({parent: myLayout.cells("b"), pattern: "2E"});
myLayout1.cells("a").setHeight(500);
myLayout1.cells("a").setText("RADNICI - DETALJI");
myLayout1.cells("a").cell.style.backgroundColor = "#cbc9af";//#c4e4ec
myLayout1.cells("b").setText("TABELA RADNIKA");

formData = [
	{type: "settings", position: "label-left", labelWidth: 140, inputWidth: 60,labelHeight:20,inputHeight:22},
	{type: "block", offsetTop: 5, list: [
		{type: "input", label: "Šifra radnika: ",name:"sifra", value:"",maxLength: 12,validate:"NotEmpty,ValidInteger",
								position:"absolute", labelTop:8,inputTop:5,inputLeft:140},
		{type: "input", label: "Prezime:",name:"prezime",  maxLength: 20,validate:"NotEmpty",
								position:"absolute",labelTop:30,inputTop:30,inputLeft:140,inputWidth:150},
		{type: "input", label: "Ime:",name:"ime",  maxLength: 15,validate:"NotEmpty",
									position:"absolute",labelTop:55,inputTop:55,inputLeft:140,inputWidth:150},
		{type: "input", label: "Koeficijent:",name:"brbod",  maxLength: 8,validate:"NotEmpty,ValidNumeric",
								position:"absolute",labelTop:80,inputTop:80,inputLeft:140},
		{type: "input", label: "Mes.zajedn.:",name:"sifmz",  maxLength: 4,validate:"NotEmpty,ValidInteger",
								position:"absolute",labelTop:105,inputTop:105,inputLeft:140},
		{type: "input", label: "Kvalifikacija:",name:"sifkval",  maxLength: 4,validate:"NotEmpty,ValidInteger",
								position:"absolute",labelTop:130,inputTop:130,inputLeft:140},
		{type: "input", label: "Godine staža:",name:"gs",  maxLength: 4,validate:"NotEmpty,ValidInteger",
								position:"absolute",labelTop:155,inputTop:155,inputLeft:140},
		{type: "input", label: "Meseci staža:",name:"ms",  maxLength: 4,validate:"NotEmpty,ValidInteger",
								position:"absolute",labelTop:180,inputTop:180,inputLeft:140},
		{type: "select", label: "Način isplate:",name:"isplata",  options:[
						            {text: "Tekući račun", value: "1"},
						            {text: "Gotovina", value: "2"},
						            {text: "Štednja", value: "3"},
						        ],position:"absolute",labelTop:205,inputTop:205,inputLeft:140,inputWidth:150,inputHeight:25},
								
		{type: "input", label: "Šifra banke:",name:"sifban",  maxLength: 4,validate:"NotEmpty,ValidInteger",
								position:"absolute",labelTop:230,inputTop:230,inputLeft:140},
		{type: "input", label: "Šifra opštine:",name:"sifraopstine",  maxLength: 4,validate:"NotEmpty,ValidInteger",
								position:"absolute",labelTop:255,inputTop:255,inputLeft:140},
		{type: "input", label: "Deoničar-vl(0-1):",name:"deonicar",  maxLength: 4,validate:"NotEmpty,ValidInteger",
								position:"absolute",labelTop:280,inputTop:280,inputLeft:140},
		{type: "calendar", dateFormat: "%d-%m-%Y", label: "Datum zasn.RO:",name:"datzasnivanja",validate:"NotEmpty",
								position:"absolute",labelTop:305,inputTop:305,inputLeft:140,inputWidth:150}
		]},
		{type: "block", labelWidth: 150, inputWidth: 150, offsetTop: 5,offsetLeft: 400, list: [
			{type: "input", label: "Broj računa: ", name:"partija",validate:"NotEmpty",
								position:"absolute",labelTop:3,inputTop:1,inputLeft:140,inputWidth:200},	
			{type: "select", label: "Član sindikata:",name:"sindi",  options:[
							            {text: "Nije član", value: "1"},
							            {text: "Jeste član", value: "2"},
							        ],position:"absolute",labelTop:28,inputTop:26,inputLeft:140,inputWidth:150,inputHeight:24},
			{type: "select", label: "Solidarnost:",name:"soli",  options:[
							            {text: "Nije član", value: "1"},
							            {text: "Jeste član", value: "2"}
							        ],position:"absolute",labelTop:53,inputTop:51,inputLeft:140,inputWidth:150,inputHeight:24},
			
			{type: "input", label: "JMBG:", name:"matbr", maxLength:13,validate:"NotEmpty,ValidInteger",
								position:"absolute",labelTop:78,inputTop:76,inputLeft:140,inputWidth:150},	
			{type: "input", label: "Adresa:", name:"adresa", maxLength:30,validate:"NotEmpty",
								position:"absolute",labelTop:103,inputTop:101,inputLeft:140,inputWidth:150},	
			{type: "input", label: "Prosek plate: ", name:"prosek", maxLength:9,validate:"ValidNumeric",
								position:"absolute",labelTop:128,inputTop:126,inputLeft:140,inputWidth:150},	
			{type: "input", label: "Benef.rs(1/0): ", name:"sifben", maxLength:4,validate:"ValidInteger",
								position:"absolute",labelTop:153,inputTop:151,inputLeft:140,inputWidth:150},	
			{type: "input", label: "Naziv rad.mes: ", name:"nazivrm", maxLength:20,validate:"NotEmpty",
									position:"absolute",labelTop:178,inputTop:176,inputLeft:140,inputWidth:200},	
			{type: "input", label: "E-mail:", name:"email", maxLength:39,
										position:"absolute",labelTop:203,inputTop:201,inputLeft:140,inputWidth:200},	

			{type: "input", label: "Olakšice:", name:"br", maxLength:4,validate:"ValidInteger",
								position:"absolute",labelTop:228,inputTop:226,inputLeft:140,inputWidth:150},	
			{type: "button", value: "***", name:"olaksice", position:"absolute",inputTop:226,inputLeft:300,inputWidth:40,inputHeight:7},
			
			{type: "calendar", dateFormat: "%d-%m-%Y", name: "datumod",id:"datumod", label: "Datum od:",
									position:"absolute",labelTop:253,inputTop:251,inputLeft:140,inputWidth:150},	
			{type: "calendar", dateFormat: "%d-%m-%Y", name: "datumdo",id:"datumdo", label: "Datum do:", id:"datumdo",
									position:"absolute",labelTop:278,inputTop:276,inputLeft:140,inputWidth:150}	

		]},			
		{type: "block", blockOffset:0,offsetTop:310, list: [
		{type: "button", value: "Novi radnik", name:"novi", position:"absolute",inputTop: 60,inputLeft:30},
		{type: "button", value: "Unesi", name:"unesi",position:"absolute",inputTop: 60,inputLeft:170},
		{type: "button", value: "Izmeni", name:"izmeni",position:"absolute",inputTop: 60,inputLeft:280},
		{type: "button", value: "Briši", name:"brisi",position:"absolute",inputTop: 60,inputLeft:390}
		]}
		];	

myForm = myLayout1.cells("a").attachForm(formData);
myForm.enableLiveValidation(true);

myForm.attachEvent("onBlur", function(koji){
	if (koji == "sifra"){
		var ppsifra = myForm.getItemValue("sifra");
		if (ppsifra != null && ppsifra.toString().length>0){
			prikaziRadnika(2);
			myForm.setItemFocus("prezime");
		}else{
			myForm.setItemFocus("sifra");
		}
	}
});
myForm.attachEvent("onkeypress", function(koji){
	if (koji == "sifra"){
		shortcut.add("F9",function() {
			alert("Hi there!");
			},{
			'type':'keydown',
			'propagate':false,
			'target':document
			});
	}
});
myForm.attachEvent("onButtonClick", function(name){
	if(name=="novi"){
		myForm.clear();
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
	else if(name=="olaksice"){
		doWindowOlaksice(1);
	}
});
myGrid = myLayout1.cells("b").attachGrid();
myGrid.setImagePath("../resources/codebase/imgs/");//path to images required by grid
myGrid.setHeader("Šifra,Ime,Prezime,Adresa,Koeficijent");//set column names
myGrid.setInitWidths("100,150,200,200,100");//set column width in px
myGrid.setColAlign("left,left,left,left,left");//set column values align
myGrid.setColTypes("ro,ro,ro,ro,ro");//set column types
myGrid.setColSorting("int,str,str,str,str");//set sorting
myGrid.setStyle("background-color:#6175ea;color:white; font-weight:bold;", "","", "");
myGrid.enableRowsHover(true, "myhover");

myGrid.setIconsPath('../resources/codebase/imgs/');
myGrid.init();
myGrid.enableAlterCss("even","uneven");
myGrid.attachEvent("onRowSelect",doOnRowSelected);

ucitajFirmu();

function ucitajFirmu(){
	var ffirma="0";
	$.get("/firma/ucitajfirmu",{}, function(data) {
		var obj = $.parseJSON(JSON.stringify(data));	
		$.each(obj, function() {
			firma = this['firma'];
			myGrid.load("/maticna/prikaz?prezime= &pre="+firma,"json");			
			godina = this['godina'];
		});		
	});
}

function doOnRowSelected(){
	prikaziRadnika(1);
}

function prikaziRadnika(koji){
	var datzasn,datod,datdo;
	if (koji == 1){
		var red = myGrid.getSelectedRowId();
		//uzimanje vrednosti iz grida
		sifra = myGrid.cells(red,0).getValue();
		myForm.clear();
	}else{
		//uzimanje sifre iz forme
		sifra =  myForm.getItemValue("sifra").toString();
	}
	
	//poruka("firma="+firma);
	//JQuery poziv na iscitavanje podataka jednog radnika kao i prikaz u formu
	$.get("/maticna/prikazjedan/?sifra="+sifra+"&pre="+firma,{}, function(data) {
		var ime='';
		var obj = $.parseJSON(JSON.stringify(data));	
		$.each(obj, function() {
			ime = this['ime'];
			myForm.setItemValue("sifra",this['sifra']); 
			myForm.setItemValue("prezime",this['prezime'].trim()); 
			myForm.setItemValue("ime",this['ime'].trim()); 
			myForm.setItemValue("brbod",this['brbod']); 
			myForm.setItemValue("sifmz",this['sifmz']); 
			myForm.setItemValue("sifkval",this['sifkval']); 
			myForm.setItemValue("gs",this['gs']); 
			myForm.setItemValue("ms",this['ms']); 
			myForm.setItemValue("isplata",this['isplata']); 
			myForm.setItemValue("sifban",this['sifban']); 
			myForm.setItemValue("sifraopstine",this['sifraopstine']); 
			myForm.setItemValue("deonicar",this['deonicar']); 
			datzasn = this['datzasnivanja'];
			datzasn = formatDate(datzasn);
			if (datzasn.substring(6,10)=="1900"){
				datzasn="";
			}
			myForm.setItemValue("datzasnivanja",datzasn); 
			myForm.setItemValue("partija",this['partija']); 
			myForm.setItemValue("isplata",this['isplata']);
			myForm.setItemValue("sindi",this['sindi']); 
			myForm.setItemValue("soli",this['soli']); 
			myForm.setItemValue("matbr",this['matbr']); 
			myForm.setItemValue("adresa",this['adresa']); 
			myForm.setItemValue("prosek",this['prosek']); 
			myForm.setItemValue("sifben",this['sifben']); 
			myForm.setItemValue("nazivrm",this['nazivrm']); 
			myForm.setItemValue("br",this['br']); 
			myForm.setItemValue("email",this['email']); 
			
			datod = this['datumod'];
			datod = formatDate(datod);
			datdo = this['datumdo'];
			datdo = formatDate(datdo);
			if (datod.substring(6,10)=="2000"){
				datod="";
			}
			if (datdo.substring(6,10)=="2000"){
				datdo="";
			}
			
			myForm.setItemValue("datumod",datod); 
			myForm.setItemValue("datumdo",datdo); 
			//---------------------------------------------------
		});		
	});
}
function Unesi(xx){
	//od parametra xx zavisi da li je unos podataka ili izmena podataka
	var mess1="",strurl="";
	if(xx==1){
		strurl="/maticna/unos/";
		mess1="Upis nije uspeo";
	}else{
		strurl="/maticna/izmena/";
		mess1="Izmena nije uspela";
	}
			
	mess2="Popunite osnovne podatke";
	
	sifra = myForm.getItemValue("sifra");

	var prezime = myForm.getItemValue("prezime").toString();
		if(prezime.trim().length == 0) {prezime="";}
	var ime = myForm.getItemValue("ime").toString();
		if(ime.trim().length == 0) {ime="";}
	var gs = myForm.getItemValue("gs").toString();
		if(gs.trim().length == 0) {gs="0";}
	var ms = myForm.getItemValue("ms").toString();
		if(ms.trim().length == 0) {ms="0";}
	var brbod = myForm.getItemValue("brbod").toString();
		if(brbod.trim().length == 0) {brbod="1";}
	var sindi = myForm.getItemValue("sindi").toString();
		if(sindi.trim().length == 0) {sindi="0";}
	var sifmz = myForm.getItemValue("sifmz").toString();
		if(sifmz.trim().length == 0) {sifmz="0";}
	var sifkval = myForm.getItemValue("sifkval").toString();
		if(sifkval.trim().length == 0) {sifkval="0";}
	var isplata = myForm.getItemValue("isplata").toString();
		if(isplata.trim().length == 0) {isplata="1";}
	var sifban = myForm.getItemValue("sifban").toString();
		if(sifban.trim().length == 0) {sifban="0";}
	var partija = myForm.getItemValue("partija").toString();
		if(partija.trim().length == 0) {partija="0";}
	var matbr = myForm.getItemValue("matbr").toString();
		if(matbr.trim().length == 0) {matbr="0";}
	var privremeno = " ";
	var sindi = myForm.getItemValue("sindi").toString();
		if(sindi.trim().length == 0) {sindi="1";}
	var soli = myForm.getItemValue("soli").toString();
		if(soli.trim().length == 0) {soli="0";}
	var adresa = myForm.getItemValue("adresa").toString();
		if(adresa.trim().length == 0) {adresa="0";}
	var prosek = myForm.getItemValue("prosek").toString();
		if(prosek.trim().length == 0) {prosek="0";}
	var sifben = myForm.getItemValue("sifben").toString();
		if(sifben.trim().length == 0) {sifben="0";}
	var nazivrm = myForm.getItemValue("nazivrm").toString();
		if(nazivrm.trim().length == 0) {nazivrm="";}
	var email = myForm.getItemValue("email").toString();
		if(email.trim().length == 0) {email="";}
	var br = myForm.getItemValue("br").toString();
	var datumod;
		if(br.trim().length == 0) {br="0";}
	if(myForm.getItemValue("datumod")==null){
		datumod="2000-01-01";	
	}else{
		datumod = myForm.getItemValue("datumod").toString();
		if(datumod.trim().length == 0) {
			datumod="2000-01-01";
		}else{
			datumod = konvertujDatumSQL(datumod);
		}
	}
	var datumdo;
	if(myForm.getItemValue("datumdo")==null){
		datumdo="2000-01-01";
	}else{
		datumdo = myForm.getItemValue("datumdo").toString();
		if(datumdo.trim().length == 0) {
			datumdo="2000-01-01";
		}else{
			datumdo = konvertujDatumSQL(datumdo);
		}
	}

	var sifraopstine = myForm.getItemValue("sifraopstine").toString();
		if(sifraopstine.trim().length == 0) {sifraopstine="0";}
	var deonicar = myForm.getItemValue("deonicar").toString();
		if(deonicar.trim().length == 0) {deonicar="0";}
		
	var datzasnivanja;
	if(myForm.getItemValue("datzasnivanja")==null){
		datzasnivanja = "1900-01-01";
	}else{
		datzasnivanja = myForm.getItemValue("datzasnivanja").toString();
		if(datzasnivanja.trim().length == 0) {
			datzasnivanja="1900-01-01";
		}else{
			datzasnivanja = konvertujDatumSQL(datzasnivanja);
		}
	}
	//---------------------------------------------------------------
	//parametri koji se prosledjuju serveru na obradu
	var dataajax = "?pre=" + firma;
	dataajax = dataajax + "&sifra=" + sifra;
	dataajax = dataajax + "&prezime=" + prezime;
	dataajax = dataajax + "&ime=" + ime;
	dataajax = dataajax + "&gs=" + gs;
	dataajax = dataajax + "&ms=" + ms;
	dataajax = dataajax + "&brbod=" + brbod;
	dataajax = dataajax + "&sindi=" + sindi;
	dataajax = dataajax + "&sifmz=" + sifmz;
	dataajax = dataajax + "&sifkval=" + sifkval;
	dataajax = dataajax + "&isplata=" + isplata;
	dataajax = dataajax + "&sifban=" + sifban;
	dataajax = dataajax + "&partija=" + partija;
	dataajax = dataajax + "&matbr=" + matbr;
	dataajax = dataajax + "&soli=" + soli;
	dataajax = dataajax + "&privremeno=" + privremeno;
	dataajax = dataajax + "&adresa=" + adresa;
	dataajax = dataajax + "&prosek=" + prosek;
	dataajax = dataajax + "&sifben=" + sifben;
	dataajax = dataajax + "&nazivrm=" + nazivrm;
	dataajax = dataajax + "&email=" + email;
	dataajax = dataajax + "&br=" + br;
	dataajax = dataajax + "&datumod=" + datumod;
	dataajax = dataajax + "&datumdo=" + datumdo;
	dataajax = dataajax + "&vrstaplate=1";
	dataajax = dataajax + "&sifraopstine=" + sifraopstine;
	dataajax = dataajax + "&deonicar=" + deonicar;
	dataajax = dataajax + "&datzasnivanja=" + datzasnivanja;
		
	if(proveripolja(sifra,prezime,ime)){
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
					myGrid.load("/maticna/prikaz?prezime= &pre="+firma,"json");
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
	var mess1="",sifra,strsifra;
	sifra = myForm.getItemValue("sifra");
	strsifra = sifra.toString();
		
	mess1="Brisanje nije uspelo";

	if(sifra!=null && strsifra.trim().length>0){
		dhx4.ajax.post("/maticna/brisanje/?sifra="+strsifra+"&pre="+firma,
			function(r){
			var t = dhx4.s2j(r.xmlDoc.responseText);
			var xml = t.toString();
			
			if(xml!=null){
				if (xml=="0"){
					poruka("Podaci nisu izbrisani");
				}else{
					myForm.clear();
					myGrid.clearAll();
					myGrid.load("/maticna/prikaz?prezime= &pre="+firma,"json");
				}
			}else{
				poruka("greska:ajax");
			}
		});
	}else{
		poruka(mess2);
	}
}
function proveripolja(ssifra,pprezime,iime) {
	var aa = true;

	if(ssifra == null || ssifra.toString().length==0){
		aa = false;
		return false;
	}
	if(iime==null || iime.toString().length==0){
		aa = false;
		return false;
	}
	if(pprezime == null || pprezime.toString().length==0){
		aa = false;
		return false;
	}
	return aa;
}

function poruka(tekstporuke) {
	dhtmlx.alert({
		title:"Upozorenje",
		type:"alert-error",
		text:tekstporuke
	});
}
function formatDate(date) {
    var d = new Date(date),
        month = '' + (d.getMonth() + 1),
        day = '' + d.getDate(),
        year = d.getFullYear();
    if (month.length < 2) month = '0' + month;
    if (day.length < 2) day = '0' + day;
    return [day, month, year].join('-');
}
function konvertujDatumSQL(date) {
    var d = new Date(date),
    month = '' + (d.getMonth() + 1),
    day = '' + d.getDate(),
    year = d.getFullYear();
    if (month.length < 2) month = '0' + month;
    if (day.length < 2) day = '0' + day;
	return [year, month, day].join('-');
}