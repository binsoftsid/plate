
var pre="",sifdop="",vrstaplate="";
//polje nadimak sluzi da se vidi da li je radnik vlasnik firme (D/N) jer ako jeste onda
//se on stavlja na sifru 1

var myLayout1 = new dhtmlXLayoutObject({parent: myLayout.cells("b"), pattern: "2E"});
myLayout1.cells("a").setHeight(400);
myLayout1.cells("a").setText("DETALJI");
myLayout1.cells("a").cell.style.backgroundColor = "#cbc9af";
myLayout1.cells("b").setText("PODACI");

/*
pre,sifdop,vrdop,nazdop,zirdop,pozdop,procdop,sifra,vrstaplate,juzer
*/

formData = [
	{type: "settings", position: "label-left", labelWidth: 100, inputWidth: 60,labelHeight:20,inputHeight:20},
	{type: "block", offsetTop: 5, list: [
		{type: "input", label: "Vrsta plate: ",name:"vrstaplate", value:"",maxLength: 5,validate:"NotEmpty,ValidInteger",
								position:"absolute", labelTop:8,inputTop:5,inputLeft:110},
		{type: "input", label: "Šifra:",name:"sifdop",  maxLength: 30,validate:"NotEmpty",
								position:"absolute",labelTop:30,inputTop:30,inputLeft:110,inputWidth:200},
		{type: "input", label: "Naziv doprinosa:",name:"nazdop",  maxLength: 40,validate:"NotEmpty",
									position:"absolute",labelTop:55,inputTop:55,inputLeft:110,inputWidth:200},
		{type: "select", label: "Vrsta doprinosa",name:"vrdop",  options:[
							            {text: "Radnik", value: "1"},
							            {text: "Poslodavac", value: "2"},
							            {text: "Na bruto zaradu", value: "3"},
							            {text: "Porez", value: "4"},
							            {text: "Beneficirani RS", value: "5"}
							        ],position:"absolute",labelTop:80,labelWidth:70,inputTop: 80,inputLeft:110,inputWidth:120,inputHeight:25},
		
		{type: "input", label: "Žiro račun:",name:"zirdop",  maxLength: 40,
								position:"absolute",labelTop:105,inputTop:105,inputLeft:110,inputWidth:200},
		{type: "input", label: "Poziv na broj:",name:"pozdop",  maxLength: 10,
								position:"absolute",labelTop:130,inputTop:130,inputLeft:110,inputWidth:200},
		{type: "input", label: "Šifra na nalogu:",name:"sifra",  maxLength: 10,
									position:"absolute",labelTop:155,inputTop:155,inputLeft:110,inputWidth:200},
		{type: "input", label: "Procenat doprinosa:",name:"procdop",  maxLength: 10,
										position:"absolute",labelTop:180,inputTop:180,inputLeft:110,inputWidth:200},
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
	if (koji == "sifdop"){
		var ppsifdop = myForm.getItemValue("sifdop");
		var pvrstaplate = myForm.getItemValue("vrstaplate");
		
		if (ppsifdop != null && ppsifdop.toString().length>0 && pvrstaplate!=null && pvrstaplate.toString().length>0){
			prikaziDopjedan(ppsifdop.toString(),pvrstaplate.toString());
			myForm.setItemFocus("nazdop");
		}else{
			myForm.setItemFocus("sifdop");
		}
	}
});

myForm.attachEvent("onButtonClick", function(name){
	if(name=="novi"){
		myForm.clear();
		myForm.setItemFocus("sifdop");
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
myGrid.setHeader("Vr.plate,Šifra,Vr.dop.,Naziv,Žiro račun,Poziv na broj,% doprinosa");//set column names
myGrid.setInitWidths("80,80,80,250,200,150,100");//set column width in px
myGrid.setColAlign("left,left,left,left,left,left,left");//set column values align
myGrid.setColTypes("ro,ro,ro,ro,ro,ro,ro");//set column types
myGrid.setColSorting("int,str,str,str,str,str,str");//set sorting
myGrid.enableRowsHover(true, "myhover");

myGrid.setIconsPath('../resources/codebase/imgs/');
myGrid.init();
myGrid.enableAlterCss("even","uneven");
myGrid.attachEvent("onRowSelect",doOnRowSelected);
myGrid.load("/doprinosi/prikaz","json");

function doOnRowSelected(){
	prikaziDop();
}

function prikaziDopjedan(ssifdop,vvrstaplate){
	
	//JQuery poziv na iscitavanje podataka jednog radnika kao i prikaz u formu
	$.get("/doprinosi/prikazjedan/?sifdop="+ssifdop+"&vrstaplate="+vvrstaplate,{}, function(data) {
		var obj = $.parseJSON(JSON.stringify(data));	
		$.each(obj, function() {
			myForm.setItemValue("vrstaplate",this['vrstaplate']); 
			myForm.setItemValue("sifdop",this['sifdop']); 
			myForm.setItemValue("nazdop",this['nazdop']); 
			myForm.setItemValue("vrdop",this['vrdop']); 
			myForm.setItemValue("zirdop",this['zirdop']); 
			myForm.setItemValue("pozdop",this['pozdop']); 
			myForm.setItemValue("sifra",this['sifra']); 
			myForm.setItemValue("procdop",this['procdop']); 
			//---------------------------------------------------
		});		
	});
}
function prikaziDop(){
	var datzasn,datod,datdo,vvrstaplate;
	var red = myGrid.getSelectedRowId();
	//uzimanje vrednosti iz grida
	vvrstaplate = myGrid.cells(red,0).getValue();
	sifdop = myGrid.cells(red,1).getValue();
	myForm.clear();
	
	//JQuery poziv na iscitavanje podataka jednog radnika kao i prikaz u formu
	$.get("/doprinosi/prikazjedan/?sifdop="+sifdop+"&vrstaplate="+vvrstaplate,{}, function(data) {
		var obj = $.parseJSON(JSON.stringify(data));	
		$.each(obj, function() {
			myForm.setItemValue("vrstaplate",this['vrstaplate']); 
			myForm.setItemValue("sifdop",this['sifdop']); 
			myForm.setItemValue("nazdop",this['nazdop']); 
			myForm.setItemValue("vrdop",this['vrdop']); 
			myForm.setItemValue("zirdop",this['zirdop']); 
			myForm.setItemValue("pozdop",this['pozdop']); 
			myForm.setItemValue("sifra",this['sifra']); 
			myForm.setItemValue("procdop",this['procdop']); 
			//---------------------------------------------------
		});		
	});
}
function Unesi(xx){
	//od parametra xx zavisi da li je unos podataka ili izmena podataka
	var mess1="",strurl="";
	if(xx==1){
		strurl="/doprinosi/unos/";
		mess1="Upis nije uspeo";
	}else{
		strurl="/doprinosi/izmena/";
		mess1="Izmena nije uspela";
	}
			
	mess2="Popunite osnovne podatke";
	
	sifdop = myForm.getItemValue("sifdop");

	vrstaplate = myForm.getItemValue("vrstaplate").toString();
		if(vrstaplate.trim().length == 0) {vrstaplate=" ";}
	var nazdop = myForm.getItemValue("nazdop").toString();
		if(nazdop.trim().length == 0) {nazdop=" ";}
	var zirdop = myForm.getItemValue("zirdop").toString();
		if(zirdop.trim().length == 0) {zirdop="";}
	var pozdop = myForm.getItemValue("pozdop").toString();
		if(pozdop.trim().length == 0) {pozdop=" ";}
	var procdop = myForm.getItemValue("procdop").toString();
		if(procdop.trim().length == 0) {procdop=" ";}
	var vrdop = myForm.getItemValue("vrdop").toString();
		if(vrdop.trim().length == 0) {vrdop=" ";}
	var sifra = myForm.getItemValue("sifra").toString();
		if(sifra.trim().length == 0) {sifra=" ";}
	//---------------------------------------------------------------
	//parametri koji se prosledjuju serveru na obradu

	var dataajax = "?sifdop=" + sifdop;
	dataajax = dataajax + "&vrstaplate=" + vrstaplate;
	dataajax = dataajax + "&nazdop=" + nazdop;
	dataajax = dataajax + "&zirdop=" + zirdop;
	dataajax = dataajax + "&pozdop=" + pozdop;
	dataajax = dataajax + "&procdop=" + procdop;
	dataajax = dataajax + "&vrdop=" + vrdop;
	dataajax = dataajax + "&sifra=" + sifra;
		
	if(proveripolja(vrstaplate,sifdop,nazdop)){
		
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
					myGrid.load("/doprinosi/prikaz","json");
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
	var mess1="",strsifdop="";
	sifdop = myForm.getItemValue("sifdop");
	strsifdop = sifdop.toString();
	vrstaplate = myForm.getItemValue("vrstaplate");
		
	mess1="Brisanje nije uspelo";

	if(sifdop!=null && strsifdop.trim().length>0){
		dhx4.ajax.post("/doprinosi/brisanje/?sifdop=" + sifdop.toString() + "&vrstaplete=" + vrstaplate.toString(),
			function(r){
			var t = dhx4.s2j(r.xmlDoc.responseText);
			var xml = t.toString();
			
			if(xml!=null){
				if (xml=="0"){
					poruka("Podaci nisu izbrisani");
				}else{
					myForm.clear();
					myGrid.clearAll();
					myGrid.load("/doprinosi/prikaz","json");
				}
			}else{
				poruka("greska:ajax");
			}
		});
	}else{
		poruka(mess2);
	}
}
function proveripolja(pvrstaplate,psifdop,nnazdop) {
	var aa = true;

	if(pvrstaplate == null || pvrstaplate.toString().length==0){
		aa = false;
		return false;
	}
	if(psifdop == null || psifdop.toString().length==0){
		aa = false;
		return false;
	}
	if(nnazdop==null || nnazdop.toString().length==0){
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