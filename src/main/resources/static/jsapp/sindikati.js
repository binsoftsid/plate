
var pre="",nazsind="",zirsind="",nadimak="";
//polje nadimak sluzi da se vidi da li je radnik vlasnik firme (D/N) jer ako jeste onda
//se on stavlja na sifru 1

var myLayout1 = new dhtmlXLayoutObject({parent: myLayout.cells("b"), pattern: "2E"});
myLayout1.cells("a").setHeight(400);
myLayout1.cells("a").setText("SINDIKATI");
myLayout1.cells("a").cell.style.backgroundColor = "#cbc9af";

myLayout1.cells("b").setText("PODACI");

/*
nista,sifsind,nazsind,zirsind,pozsind,procsind,juzer
*/


formData = [
	{type: "settings", position: "label-left", labelWidth: 100, inputWidth: 60,labelHeight:20,inputHeight:22},
	{type: "block", offsetTop: 5, list: [
		{type: "input", label: "Šifra: ",name:"sifsind", value:"",maxLength: 5,validate:"NotEmpty,ValidInteger",
								position:"absolute", labelTop:8,inputTop:5,inputLeft:140},
		{type: "input", label: "Naziv:",name:"nazsind",  maxLength: 30,validate:"NotEmpty",
								position:"absolute",labelTop:30,inputTop:30,inputLeft:140,inputWidth:200},
		{type: "input", label: "Žiro račun:",name:"zirsind",  maxLength: 40,validate:"NotEmpty",
									position:"absolute",labelTop:55,inputTop:55,inputLeft:140,inputWidth:200},
		{type: "input", label: "Poziv na broj:",name:"pozsind",  maxLength: 40,
								position:"absolute",labelTop:80,inputTop:80,inputLeft:140,inputWidth:200},
		{type: "input", label: "% sindikata:",name:"procsind",  maxLength: 10,
								position:"absolute",labelTop:105,inputTop:105,inputLeft:140,inputWidth:200},
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
	if (koji == "sifsind"){
		var ppsifsind = myForm.getItemValue("sifsind");
		if (ppsifsind != null && ppsifsind.toString().length>0){
			prikaziMZjedan(ppsifsind.toString());
			myForm.setItemFocus("nazsind");
		}else{
			myForm.setItemFocus("sifsind");
		}
	}
});

myForm.attachEvent("onButtonClick", function(name){
	if(name=="novi"){
		myForm.clear();
		myForm.setItemFocus("sifsind");
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
myGrid.setIconsPath('../resources/codebase/imgs/');
myGrid.setStyle("background-color:#6175ea;color:white; font-weight:bold;", "","", "");
myGrid.enableRowsHover(true, "myhover");

myGrid.init();
myGrid.enableAlterCss("even","uneven");
myGrid.attachEvent("onRowSelect",doOnRowSelected);
myGrid.load("/sindikati/prikaz","json");

function doOnRowSelected(){
	prikaziMZ();
}

function prikaziMZjedan(ssifsind){
	
	//JQuery poziv na iscitavanje podataka jednog radnika kao i prikaz u formu
	$.get("/sindikati/prikazjedan/?sifsind="+ssifsind,{}, function(data) {
		var obj = $.parseJSON(JSON.stringify(data));	
		$.each(obj, function() {
			myForm.setItemValue("sifsind",this['sifsind']); 
			myForm.setItemValue("nazsind",this['nazsind']); 
			myForm.setItemValue("zirsind",this['zirsind']); 
			myForm.setItemValue("pozsind",this['pozsind']); 
			myForm.setItemValue("procsind",this['procsind']); 
			//---------------------------------------------------
		});		
	});
}
function prikaziMZ(){
	var datzasn,datod,datdo;
	var red = myGrid.getSelectedRowId();
	//uzimanje vrednosti iz grida
	sifsind = myGrid.cells(red,0).getValue();
	myForm.clear();
	
	//JQuery poziv na iscitavanje podataka jednog radnika kao i prikaz u formu
	$.get("/sindikati/prikazjedan/?sifsind="+sifsind,{}, function(data) {
		var obj = $.parseJSON(JSON.stringify(data));	
		$.each(obj, function() {
			myForm.setItemValue("sifsind",this['sifsind']); 
			myForm.setItemValue("nazsind",this['nazsind']); 
			myForm.setItemValue("zirsind",this['zirsind']); 
			myForm.setItemValue("pozsind",this['pozsind']); 
			myForm.setItemValue("procsind",this['procsind']); 
			//---------------------------------------------------
		});		
	});
}
function Unesi(xx){
	//od parametra xx zavisi da li je unos podataka ili izmena podataka
	var mess1="",strurl="";
	if(xx==1){
		strurl="/sindikati/unos/";
		mess1="Upis nije uspeo";
	}else{
		strurl="/sindikati/izmena/";
		mess1="Izmena nije uspela";
	}
			
	mess2="Popunite osnovne podatke";
	
	sifsind = myForm.getItemValue("sifsind");

	var nazsind = myForm.getItemValue("nazsind").toString();
		if(nazsind.trim().length == 0) {nazsind=" ";}
	var zirsind = myForm.getItemValue("zirsind").toString();
		if(zirsind.trim().length == 0) {zirsind="";}
	var pozsind = myForm.getItemValue("pozsind").toString();
		if(pozsind.trim().length == 0) {pozsind=" ";}
	var procsind = myForm.getItemValue("procsind").toString();
		if(procsind.trim().length == 0) {procsind=" ";}
	//---------------------------------------------------------------
	//parametri koji se prosledjuju serveru na obradu

	var dataajax = "?sifsind=" + sifsind;
	dataajax = dataajax + "&nazsind=" + nazsind;
	dataajax = dataajax + "&zirsind=" + zirsind;
	dataajax = dataajax + "&pozsind=" + pozsind;
	dataajax = dataajax + "&procsind=" + procsind;
		
	if(proveripolja(sifsind,nazsind)){
		
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
					myGrid.load("/sindikati/prikaz","json");
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
	var mess1="",strsifsind;
	sifsind = myForm.getItemValue("sifsind");
	strsifsind = sifsind.toString();
		
	mess1="Brisanje nije uspelo";

	if(sifsind!=null && strsifsind.trim().length>0){
		dhx4.ajax.post("/sindikati/brisanje/?sifsind=" + strsifsind,
			function(r){
			var t = dhx4.s2j(r.xmlDoc.responseText);
			var xml = t.toString();
			
			if(xml!=null){
				if (xml=="0"){
					poruka("Podaci nisu izbrisani");
				}else{
					myForm.clear();
					myGrid.clearAll();
					myGrid.load("/sindikati/prikaz","json");
				}
			}else{
				poruka("greska:ajax");
			}
		});
	}else{
		poruka(mess2);
	}
}
function proveripolja(psifsind,nnazsind) {
	var aa = true;

	if(psifsind == null || psifsind.toString().length==0){
		aa = false;
		return false;
	}
	if(nnazsind==null || nnazsind.toString().length==0){
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