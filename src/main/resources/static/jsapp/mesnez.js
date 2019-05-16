
var pre="",nazmz="",zirmz="",nadimak="";
//polje nadimak sluzi da se vidi da li je radnik vlasnik firme (D/N) jer ako jeste onda
//se on stavlja na sifru 1

var myLayout1 = new dhtmlXLayoutObject({parent: myLayout.cells("b"), pattern: "2E"});
myLayout1.cells("a").setHeight(400);
myLayout1.cells("a").setText("MESNE ZAJEDNICE");
myLayout1.cells("a").cell.style.backgroundColor = "#cbc9af";

myLayout1.cells("b").setText("PODACI");

/*
nista,sifmz,nazmz,zirmz,pozmz,procmz,juzer
*/


formData = [
	{type: "settings", position: "label-left", labelWidth: 120, inputWidth: 60,labelHeight:20,inputHeight:22},
	{type: "block", offsetTop: 5, list: [
		{type: "input", label: "Šifra: ",name:"sifmz", value:"",maxLength: 5,validate:"NotEmpty,ValidInteger",
								position:"absolute", labelTop:8,inputTop:5,inputLeft:140},
		{type: "input", label: "Naziv mes.z.:",name:"nazmz",  maxLength: 20,validate:"NotEmpty",
								position:"absolute",labelTop:30,inputTop:30,inputLeft:140,inputWidth:150},
		{type: "input", label: "Žiro račun:",name:"zirmz",  maxLength: 40,validate:"NotEmpty",
									position:"absolute",labelTop:55,inputTop:55,inputLeft:140,inputWidth:150},
		{type: "input", label: "Poziv na broj:",name:"pozmz",  maxLength: 40,
								position:"absolute",labelTop:80,inputTop:80,inputLeft:140,inputWidth:150},
		{type: "input", label: "% samodoprinosa:",name:"procmz",  maxLength: 6,
								position:"absolute",labelTop:105,inputTop:105,inputLeft:140,inputWidth:150},
		]},			
		{type: "block", blockOffset:0,offsetTop:220, list: [
		{type: "button", value: "Novi", name:"novi", position:"absolute",inputTop: 60,inputLeft:30},
		{type: "button", value: "Unesi", name:"unesi",position:"absolute",inputTop: 60,inputLeft:150},
		{type: "button", value: "Izmeni", name:"izmeni",position:"absolute",inputTop: 60,inputLeft:270},
		{type: "button", value: "Briši", name:"brisi",position:"absolute",inputTop: 60,inputLeft:390}
	]}
];	

myForm = myLayout1.cells("a").attachForm(formData);
myForm.enableLiveValidation(true);

myForm.attachEvent("onBlur", function(koji){
	if (koji == "sifmz"){
		var ppsifmz = myForm.getItemValue("sifmz");
		if (ppsifmz != null && ppsifmz.toString().length>0){
			prikaziMZjedan(ppsifmz.toString());
			myForm.setItemFocus("nazmz");
		}else{
			myForm.setItemFocus("sifmz");
		}
	}
});

myForm.attachEvent("onButtonClick", function(name){
	if(name=="novi"){
		myForm.clear();
		myForm.setItemFocus("sifmz");
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
myGrid.setHeader("Šifra,Naziv,Žiro račun,Poziv na broj,% samodoprinosa");//set column names
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
myGrid.load("/mesnezajednice/prikaz","json");

function doOnRowSelected(){
	prikaziMZ();
}

function prikaziMZjedan(ssifmz){
	
	//JQuery poziv na iscitavanje podataka jednog radnika kao i prikaz u formu
	$.get("/mesnezajednice/prikazjedan/?sifmz="+ssifmz,{}, function(data) {
		var obj = $.parseJSON(JSON.stringify(data));	
		$.each(obj, function() {
			myForm.setItemValue("sifmz",this['sifmz']); 
			myForm.setItemValue("nazmz",this['nazmz']); 
			myForm.setItemValue("zirmz",this['zirmz']); 
			myForm.setItemValue("pozmz",this['pozmz']); 
			myForm.setItemValue("procmz",this['procmz']); 
			//---------------------------------------------------
		});		
	});
}
function prikaziMZ(){
	var datzasn,datod,datdo;
	var red = myGrid.getSelectedRowId();
	//uzimanje vrednosti iz grida
	sifmz = myGrid.cells(red,0).getValue();
	myForm.clear();
	
	//JQuery poziv na iscitavanje podataka jednog radnika kao i prikaz u formu
	$.get("/mesnezajednice/prikazjedan/?sifmz="+sifmz,{}, function(data) {
		var obj = $.parseJSON(JSON.stringify(data));	
		$.each(obj, function() {
			myForm.setItemValue("sifmz",this['sifmz']); 
			myForm.setItemValue("nazmz",this['nazmz']); 
			myForm.setItemValue("zirmz",this['zirmz']); 
			myForm.setItemValue("pozmz",this['pozmz']); 
			myForm.setItemValue("procmz",this['procmz']); 
			//---------------------------------------------------
		});		
	});
}
function Unesi(xx){
	//od parametra xx zavisi da li je unos podataka ili izmena podataka
	var mess1="",strurl="";
	if(xx==1){
		strurl="/mesnezajednice/unos/";
		mess1="Upis nije uspeo";
	}else{
		strurl="/mesnezajednice/izmena/";
		mess1="Izmena nije uspela";
	}
			
	mess2="Popunite osnovne podatke";
	
	sifmz = myForm.getItemValue("sifmz");

	var nazmz = myForm.getItemValue("nazmz").toString();
		if(nazmz.trim().length == 0) {nazmz=" ";}
	var zirmz = myForm.getItemValue("zirmz").toString();
		if(zirmz.trim().length == 0) {zirmz="";}
	var pozmz = myForm.getItemValue("pozmz").toString();
		if(pozmz.trim().length == 0) {pozmz=" ";}
	var procmz = myForm.getItemValue("procmz").toString();
		if(procmz.trim().length == 0) {procmz=" ";}
	//---------------------------------------------------------------
	//parametri koji se prosledjuju serveru na obradu

	var dataajax = "?sifmz=" + sifmz;
	dataajax = dataajax + "&nazmz=" + nazmz;
	dataajax = dataajax + "&zirmz=" + zirmz;
	dataajax = dataajax + "&pozmz=" + pozmz;
	dataajax = dataajax + "&procmz=" + procmz;
		
	if(proveripolja(sifmz,nazmz)){
		
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
					myGrid.load("/mesnezajednice/prikaz","json");
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
	var mess1="",strsifmz;
	sifmz = myForm.getItemValue("sifmz");
	strsifmz = sifmz.toString();
		
	mess1="Brisanje nije uspelo";

	if(sifmz!=null && strsifmz.trim().length>0){
		dhx4.ajax.post("/mesnezajednice/brisanje/?sifmz=" + strsifmz,
			function(r){
			var t = dhx4.s2j(r.xmlDoc.responseText);
			var xml = t.toString();
			
			if(xml!=null){
				if (xml=="0"){
					poruka("Podaci nisu izbrisani");
				}else{
					myForm.clear();
					myGrid.clearAll();
					myGrid.load("/mesnezajednice/prikaz","json");
				}
			}else{
				poruka("greska:ajax");
			}
		});
	}else{
		poruka(mess2);
	}
}
function proveripolja(psifmz,nnazmz) {
	var aa = true;

	if(psifmz == null || psifmz.toString().length==0){
		aa = false;
		return false;
	}
	if(nnazmz==null || nnazmz.toString().length==0){
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