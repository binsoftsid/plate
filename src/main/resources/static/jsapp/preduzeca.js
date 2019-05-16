
var pre="",naziv="",adresa="",nadimak="";
//polje nadimak sluzi da se vidi da li je radnik vlasnik firme (D/N) jer ako jeste onda
//se on stavlja na sifru 1

var myLayout1 = new dhtmlXLayoutObject({parent: myLayout.cells("b"), pattern: "2E"});
myLayout1.cells("a").setHeight(500);
myLayout1.cells("a").setText("UNOS FIRMI DETALJI");

myLayout1.cells("a").cell.style.backgroundColor = "#cbc9af";

myLayout1.cells("b").setText("PODACI");

formData = [
	{type: "settings", position: "label-left", labelWidth: 120, inputWidth: 60,labelHeight:20,inputHeight:22},
	{type: "block", offsetTop: 5, list: [
		{type: "input", label: "Šifra: ",name:"pre", value:"",maxLength: 5,validate:"NotEmpty,ValidInteger",
								position:"absolute", labelTop:8,inputTop:5,inputLeft:140},
		{type: "input", label: "Naziv:",name:"naziv",  maxLength: 20,validate:"NotEmpty",
								position:"absolute",labelTop:30,inputTop:30,inputLeft:140,inputWidth:200},
		{type: "input", label: "Poštanski broj:",name:"pbroj",  maxLength: 6,validate:"NotEmpty,ValidInteger",
									position:"absolute",labelTop:55,inputTop:55,inputLeft:140,inputWidth:200},
		{type: "input", label: "Mesto:",name:"mesto", maxLength: 20,validate:"NotEmpty",
								position:"absolute",labelTop:80,inputTop:80,inputLeft:140,inputWidth:200},
		{type: "input", label: "Adresa:",name:"adresa",  maxLength: 25,
								position:"absolute",labelTop:105,inputTop:105,inputLeft:140,inputWidth:200},
		{type: "input", label: "telefon:",name:"telefon",  maxLength: 15,validate:"NotEmpty",
								position:"absolute",labelTop:130,inputTop:130,inputLeft:140,inputWidth:200},
		{type: "input", label: "Mail:",name:"fax",  maxLength: 20,
								position:"absolute",labelTop:155,inputTop:155,inputLeft:140,inputWidth:200},
		{type: "input", label: "Žiro račun1:",name:"ziror",  maxLength: 30,
								position:"absolute",labelTop:180,inputTop:180,inputLeft:140,inputWidth:200},
		{type: "input", label: "Žiro račun2:",name:"zirorod",  maxLength: 30,
									position:"absolute",labelTop:205,inputTop:205,inputLeft:140,inputWidth:200},
		{type: "input", label: "Šifra opštine:",name:"sifraopstine",  maxLength: 4,validate:"NotEmpty,ValidInteger",
								position:"absolute",labelTop:230,inputTop:230,inputLeft:140},
		
		{type: "input", label: "Matični broj:",name:"matbr",  maxLength: 12,validate:"NotEmpty,ValidInteger",
								position:"absolute",labelTop:255,inputTop:255,inputLeft:140,inputWidth:150},
		{type: "input", label: "Šifra delatnosti:",name:"sido",  maxLength: 12,validate:"NotEmpty",
								position:"absolute",labelTop:280,inputTop:280,inputLeft:140,inputWidth:150},
		{type: "input", label: "PIB:",name:"pib",  maxLength: 13,validate:"NotEmpty,ValidInteger",
								position:"absolute",labelTop:305,inputTop:305,inputLeft:140,inputWidth:150},
		{type: "input", label: "Poslodavac(D/N):",name:"nadimak",  maxLength: 1,
									position:"absolute",labelTop:330,inputTop:330,inputLeft:140,inputWidth:150}
		]},			
		{type: "block", blockOffset:0,offsetTop:320, list: [
		{type: "button", value: "Novi", name:"novi", position:"absolute",inputTop: 60,inputLeft:30},
		{type: "button", value: "Unesi", name:"unesi",position:"absolute",inputTop: 60,inputLeft:150},
		{type: "button", value: "Izmeni", name:"izmeni",position:"absolute",inputTop: 60,inputLeft:270},
		{type: "button", value: "Briši", name:"brisi",position:"absolute",inputTop: 60,inputLeft:390}
		]}
		];	

myForm = myLayout1.cells("a").attachForm(formData);
myForm.enableLiveValidation(true);

myForm.attachEvent("onBlur", function(koji){
	if (koji == "pre"){
		var ppsifra = myForm.getItemValue("pre");
		if (ppsifra != null && ppsifra.toString().length>0){
			prikaziPreduzece(2);
			myForm.setItemFocus("naziv");
		}else{
			myForm.setItemFocus("pre");
		}
	}
});

myForm.attachEvent("onButtonClick", function(name){
	if(name=="novi"){
		myForm.clear();
		myForm.setItemFocus("pre");
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
myGrid.setHeader("Šifra,Naziv,Mesto,Adresa");//set column names
myGrid.setInitWidths("100,200,200,200");//set column width in px
myGrid.setColAlign("left,left,left,left");//set column values align
myGrid.setColTypes("ro,ro,ro,ro");//set column types
myGrid.setColSorting("int,str,str,str");//set sorting
myGrid.setStyle("background-color:#6175ea;color:white; font-weight:bold;", "","", "");

myGrid.enableRowsHover(true, "myhover");

myGrid.setIconsPath('../resources/codebase/imgs/');
myGrid.init();
myGrid.enableAlterCss("even","uneven");
myGrid.attachEvent("onRowSelect",doOnRowSelected);
myGrid.load("/preduzeca/prikaz","json");

function doOnRowSelected(){
	prikaziPreduzece(1);
}

function prikaziPreduzece(xx){
	var datzasn,datod,datdo;
	if(xx == 1){
		var red = myGrid.getSelectedRowId();
		//uzimanje vrednosti iz grida
		pre = myGrid.cells(red,0).getValue();
		myForm.clear();
	}else{
		pre =  myForm.getItemValue("pre").toString();
	}
	
	//JQuery poziv na iscitavanje podataka jednog radnika kao i prikaz u formu
	$.get("/preduzeca/prikazjedan/?pre="+pre,{}, function(data) {
		var obj = $.parseJSON(JSON.stringify(data));	
		$.each(obj, function() {
			myForm.setItemValue("pre",this['pre']); 
			myForm.setItemValue("naziv",this['naziv'].trim()); 
			myForm.setItemValue("pbroj",this['pbroj']); 
			myForm.setItemValue("mesto",this['mesto']); 
			myForm.setItemValue("adresa",this['adresa']); 
			myForm.setItemValue("telefon",this['telefon']); 
			myForm.setItemValue("fax",this['fax']); 
			myForm.setItemValue("ziror",this['ziror']); 
			myForm.setItemValue("zirorod",this['zirorod']); 
			myForm.setItemValue("sifraopstine",this['sifraopstine']); 
			myForm.setItemValue("matbr",this['matbr']); 
			myForm.setItemValue("sido",this['sido']); 
			myForm.setItemValue("pib",this['pib']); 
			myForm.setItemValue("nadimak",this['nadimak']); 
			//---------------------------------------------------
		});		
	});
}
function Unesi(xx){
	//od parametra xx zavisi da li je unos podataka ili izmena podataka
	var mess1="",strurl="";
	if(xx==1){
		strurl="/preduzeca/unos/";
		mess1="Upis nije uspeo";
	}else{
		strurl="/preduzeca/izmena/";
		mess1="Izmena nije uspela";
	}
			
	mess2="Popunite osnovne podatke";
	
	pre = myForm.getItemValue("pre");

	var naziv = myForm.getItemValue("naziv").toString();
		if(naziv.trim().length == 0) {naziv=" ";}
	var mesto = myForm.getItemValue("mesto").toString();
		if(mesto.trim().length == 0) {mesto=" ";}
	var adresa = myForm.getItemValue("adresa").toString();
		if(adresa.trim().length == 0) {adresa="";}
	var telefon = myForm.getItemValue("telefon").toString();
		if(telefon.trim().length == 0) {telefon=" ";}
	var fax = myForm.getItemValue("fax").toString();
		if(fax.trim().length == 0) {fax=" ";}
	var ziror = myForm.getItemValue("ziror").toString();
		if(ziror.trim().length == 0) {ziror=" ";}
	var pbroj = myForm.getItemValue("pbroj").toString();
		if(pbroj.trim().length == 0) {pbroj="0";}
	var zirorod = myForm.getItemValue("zirorod").toString();
		if(zirorod.trim().length == 0) {zirorod=" ";}
	var matbr = myForm.getItemValue("matbr").toString();
		if(matbr.trim().length == 0) {matbr=" ";}
	var sido = myForm.getItemValue("sido").toString();
		if(sido.trim().length == 0) {sido=" ";}
	var pib = myForm.getItemValue("pib").toString();
		if(pib.trim().length == 0) {pib="0";}
	var sifraopstine = myForm.getItemValue("sifraopstine").toString();
		if(sifraopstine.trim().length == 0) {sifraopstine=" ";}
	var nadimak = myForm.getItemValue("nadimak").toString();
		if(nadimak.trim().length == 0) {nadimak=" ";}
	//---------------------------------------------------------------
	//parametri koji se prosledjuju serveru na obradu

	var dataajax = "?pre=" + pre;
	dataajax = dataajax + "&naziv=" + naziv;
	dataajax = dataajax + "&mesto=" + mesto;
	dataajax = dataajax + "&adresa=" + adresa;
	dataajax = dataajax + "&telefon=" + telefon;
	dataajax = dataajax + "&fax=" + fax;
	dataajax = dataajax + "&ziror=" + ziror;
	dataajax = dataajax + "&pbroj=" + pbroj;
	dataajax = dataajax + "&zirorod=" + zirorod;
	dataajax = dataajax + "&sido=" + sido;
	dataajax = dataajax + "&matbr=" + matbr;
	dataajax = dataajax + "&pib=" + pib;
	dataajax = dataajax + "&sifraopstine=" + sifraopstine;
	dataajax = dataajax + "&nadimak=" + nadimak;
		
	if(proveripolja(pre,naziv,mesto)){
		
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
						myForm.clear();
						myGrid.clearAll();
						myGrid.load("/preduzeca/prikaz","json");
						//unesiParametreObracuna(pre);
					}else{
						poruka("Podaci su izmenjeni");
						myForm.clear();
						myGrid.clearAll();
						myGrid.load("/preduzeca/prikaz","json");
					}
				}
			}else{
				poruka("greska:ajax");
			}
		});
	}else{
		poruka(mess2);
	}

}
function unesiParametreObracuna(preduz){
	/*
	//poruka(preduz.toString());
	//kopira parametre obracuna za novu firmu za tog usera
	dhx4.ajax.post("/preduzeca/unosparametara/?pre="+preduz.toString(),
			function(r){
		var t = dhx4.s2j(r.xmlDoc.responseText);
		var xml = t.toString();
		if(xml!=null){
			if (xml=="0"){
				poruka("Parametri obračuna nisu upisani");
			}else{
				myForm.clear();
				myGrid.clearAll();
				myGrid.load("/preduzeca/prikaz","json");
			}
		}else{
			poruka("greska:ajax");
		}
	});
	*/

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
	var mess1="",strpre;
	pre = myForm.getItemValue("pre");
	strpre = pre.toString();
		
	mess1="Brisanje nije uspelo";

	if(pre!=null && strpre.trim().length>0){
		dhx4.ajax.post("/preduzeca/brisanje/?pre=" + strpre,
			function(r){
			var t = dhx4.s2j(r.xmlDoc.responseText);
			var xml = t.toString();
			
			if(xml!=null){
				if (xml=="0"){
					poruka("Podaci nisu izbrisani");
				}else{
					myForm.clear();
					myGrid.clearAll();
					myGrid.load("/preduzeca/prikaz","json");
				}
			}else{
				poruka("greska:ajax");
			}
		});
	}else{
		poruka(mess2);
	}
}
function proveripolja(ppre,nnaziv,mmesto) {
	var aa = true;

	if(ppre == null || ppre.toString().length==0){
		aa = false;
		return false;
	}
	if(nnaziv==null || nnaziv.toString().length==0){
		aa = false;
		return false;
	}
	if(mmesto == null || mmesto.toString().length==0){
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