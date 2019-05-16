
var sifra="",ime="",prezime="",firma="0",godina="0";

var myLayout1 = new dhtmlXLayoutObject({parent: myLayout.cells("b"), pattern: "2E"});
myLayout1.cells("a").setHeight(500);
myLayout1.cells("a").setText("PREDUZETNICI-MATIČNI PODACI");
myLayout1.cells("a").cell.style.backgroundColor = "#cbc9af";

myLayout1.cells("b").setText("PODACI");

formData = [
	{type: "settings", position: "label-left", labelWidth: 110, inputWidth: 60,labelHeight:20,inputHeight:20},
	{type: "block", offsetTop: 5, list: [
		{type: "input", label: "Šifra preduzetnika: ",name:"sifra", value:"",maxLength: 5,validate:"NotEmpty,ValidInteger",
								position:"absolute", labelTop:8,inputTop:5,inputLeft:120},
		{type: "input", label: "Naziv firme:",name:"nazivfirme",  maxLength: 20,validate:"NotEmpty",
								position:"absolute",labelTop:30,inputTop:30,inputLeft:120,inputWidth:150},
		{type: "input", label: "Ime:",name:"ime",  maxLength: 20,validate:"NotEmpty",
									position:"absolute",labelTop:55,inputTop:55,inputLeft:120,inputWidth:150},
		{type: "input", label: "Prezime:",name:"prezime",  maxLength: 15,validate:"NotEmpty",inputWidth:150,
								position:"absolute",labelTop:80,inputTop:80,inputLeft:120},
		{type: "input", label: "Poštanski broj:",name:"pbroj",  maxLength: 5,validate:"NotEmpty,ValidInteger",
								position:"absolute",labelTop:105,inputTop:105,inputLeft:120},
		{type: "input", label: "Mesto:",name:"mesto",  maxLength: 25,validate:"NotEmpty",inputWidth:150,
								position:"absolute",labelTop:130,inputTop:130,inputLeft:120},
		{type: "input", label: "Adresa:",name:"adresa",  maxLength: 30,validate:"NotEmpty",inputWidth:150,
								position:"absolute",labelTop:155,inputTop:155,inputLeft:120},
		{type: "input", label: "Telefon:",name:"telefon",  maxLength: 25,validate:"NotEmpty",inputWidth:150,
								position:"absolute",labelTop:180,inputTop:180,inputLeft:120},
		{type: "input", label: "E-mail:",name:"email",  maxLength: 30,validate:"NotEmpty",inputWidth:150,
								position:"absolute",labelTop:205,inputTop:205,inputLeft:120},
		{type: "input", label: "Tekući račun:",name:"tekuci",  maxLength: 25,validate:"NotEmpty",inputWidth:150,
								position:"absolute",labelTop:230,inputTop:230,inputLeft:120},
		{type: "input", label: "Šifra opštine:",name:"sifraopstine",  maxLength: 3,validate:"NotEmpty,ValidInteger",
								position:"absolute",labelTop:255,inputTop:255,inputLeft:120},
		{type: "input", label: "JMBG:",name:"jmbg",validate:"NotEmpty",maxLength: 13,inputWidth:150,
								position:"absolute",labelTop:280,inputTop:280,inputLeft:120},
		{type: "input", label: "PIB:",name:"pib",validate:"NotEmpty",maxLength: 13,inputWidth:150,
									position:"absolute",labelTop:305,inputTop:305,inputLeft:120},
		{type: "input", label: "Matični broj",name:"matbr",validate:"NotEmpty",maxLength: 9,inputWidth:150,
									position:"absolute",labelTop:330,inputTop:330,inputLeft:120}
								
		]},			
		{type: "block", blockOffset:0,offsetTop:330, list: [
		{type: "button", value: "Novi radnik", name:"novi", position:"absolute",inputTop: 60,inputLeft:30},
		{type: "button", value: "Unesi", name:"unesi",position:"absolute",inputTop: 60,inputLeft:150},
		{type: "button", value: "Izmeni", name:"izmeni",position:"absolute",inputTop: 60,inputLeft:270},
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
			myForm.setItemFocus("nazivfirme");
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
});
myGrid = myLayout1.cells("b").attachGrid();
myGrid.setImagePath("../resources/codebase/imgs/");//path to images required by grid
myGrid.setHeader("Šifra,Naziv firme,Ime,Prezime,Adresa");//set column names
myGrid.setInitWidths("100,200,200,200,200");//set column width in px
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
			myGrid.load("/matpreduz/prikaz/?nazivfirme=&pre="+firma,"json");			
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
	
	//JQuery poziv na iscitavanje podataka jednog radnika kao i prikaz u formu
	$.get("/matpreduz/prikazjedan/?sifra="+sifra+"&pre="+firma,{}, function(data) {
		var ime='';
		var obj = $.parseJSON(JSON.stringify(data));	
		$.each(obj, function() {
			ime = this['ime'];
			myForm.setItemValue("sifra",this['sifra']); 
			myForm.setItemValue("nazivfirme",this['nazivfirme'].trim()); 
			myForm.setItemValue("ime",this['ime'].trim()); 
			myForm.setItemValue("prezime",this['prezime'].trim()); 
			myForm.setItemValue("pbroj",this['pbroj']); 
			myForm.setItemValue("mesto",this['mesto']); 
			myForm.setItemValue("adresa",this['adresa']); 
			myForm.setItemValue("telefon",this['telefon']); 
			myForm.setItemValue("email",this['email']); 
			myForm.setItemValue("tekuci",this['tekuci']); 
			myForm.setItemValue("sifraopstine",this['sifraopstine']); 
			myForm.setItemValue("matbr",this['matbr']); 
			myForm.setItemValue("jmbg",this['jmbg']); 
			myForm.setItemValue("pib",this['pib']); 
 			//---------------------------------------------------
		});		
	});
}
function Unesi(xx){
	//od parametra xx zavisi da li je unos podataka ili izmena podataka
	var mess1="",strurl="";
	if(xx==1){
		strurl="/matpreduz/unos/";
		mess1="Upis nije uspeo";
	}else{
		strurl="/matpreduz/izmena/";
		mess1="Izmena nije uspela";
	}
			
	mess2="Popunite osnovne podatke";
	
	sifra = myForm.getItemValue("sifra");

	var prezime = myForm.getItemValue("prezime").toString();
		if(prezime.trim().length == 0) {prezime="";}
	var ime = myForm.getItemValue("ime").toString();
		if(ime.trim().length == 0) {ime="";}
	var nazivfirme = myForm.getItemValue("nazivfirme").toString();
		if(nazivfirme.trim().length == 0) {nazivfirme="";}
	var pbroj = myForm.getItemValue("pbroj").toString();
		if(pbroj.trim().length == 0) {pbroj="0";}
	var mesto = myForm.getItemValue("mesto").toString();
		if(mesto.trim().length == 0) {mesto="";}
	var adresa = myForm.getItemValue("adresa").toString();
		if(adresa.trim().length == 0) {adresa="";}
	var telefon = myForm.getItemValue("telefon").toString();
		if(telefon.trim().length == 0) {telefon="0";}
	var email = myForm.getItemValue("email").toString();
		if(email.trim().length == 0) {email="";}
	var tekuci = myForm.getItemValue("tekuci").toString();
		if(tekuci.trim().length == 0) {tekuci="";}
	var sifraopstine = myForm.getItemValue("sifraopstine").toString();
		if(sifraopstine.trim().length == 0) {sifraopstine="0";}
	var jmbg = myForm.getItemValue("jmbg").toString();
		if(jmbg.trim().length == 0) {jmbg="";}
	var pib = myForm.getItemValue("pib").toString();
		if(pib.trim().length == 0) {pib="";}
	var matbr = myForm.getItemValue("matbr").toString();
		if(matbr.trim().length == 0) {matbr="";}
	//---------------------------------------------------------------
	//parametri koji se prosledjuju serveru na obradu
	var dataajax = "?pre=" + firma;
	dataajax = dataajax + "&sifra=" + sifra;
	dataajax = dataajax + "&prezime=" + prezime;
	dataajax = dataajax + "&ime=" + ime;
	dataajax = dataajax + "&nazivfirme=" + nazivfirme;
	dataajax = dataajax + "&pbroj=" + pbroj;
	dataajax = dataajax + "&mesto=" + mesto;
	dataajax = dataajax + "&adresa=" + adresa;
	dataajax = dataajax + "&telefon=" + telefon;
	dataajax = dataajax + "&email=" + email;
	dataajax = dataajax + "&tekuci=" + tekuci;
	dataajax = dataajax + "&sifraopstine=" + sifraopstine;
	dataajax = dataajax + "&jmbg=" + jmbg;
	dataajax = dataajax + "&pib=" + pib;
	dataajax = dataajax + "&matbr=" + matbr;
		
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
					myGrid.load("/matpreduz/prikaz/?nazivfirme=&pre="+firma,"json");
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
		dhx4.ajax.post("/matpreduz/brisanje/?sifra="+strsifra+"&pre="+firma,
			function(r){
			var t = dhx4.s2j(r.xmlDoc.responseText);
			var xml = t.toString();
			
			if(xml!=null){
				if (xml=="0"){
					poruka("Podaci nisu izbrisani");
				}else{
					myForm.clear();
					myGrid.clearAll();
					myGrid.load("/matpreduz/prikaz/?nazivfirme=&pre="+firma,"json");
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