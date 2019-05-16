
var pre="",naobs="",zirobs="",nadimak="";
//polje nadimak sluzi da se vidi da li je radnik vlasnik firme (D/N) jer ako jeste onda
//se on stavlja na sifru 1

var myLayout1 = new dhtmlXLayoutObject({parent: myLayout.cells("b"), pattern: "2E"});
myLayout1.cells("a").setHeight(400);
myLayout1.cells("a").setText("KREDITORI");
myLayout1.cells("a").cell.style.backgroundColor = "#cbc9af";

myLayout1.cells("b").setText("PODACI");

formData = [
	{type: "settings", position: "label-left", labelWidth: 140, inputWidth: 60,labelHeight:20,inputHeight:22},
	{type: "block", offsetTop: 5, list: [
		{type: "input", label: "Šifra: ",name:"sifobs", value:"",maxLength: 5,validate:"NotEmpty,ValidInteger",
								position:"absolute", labelTop:8,inputTop:5,inputLeft:140},
		{type: "input", label: "Naziv:",name:"nazobs",  maxLength: 30,validate:"NotEmpty",
								position:"absolute",labelTop:30,inputTop:30,inputLeft:140,inputWidth:200},
		{type: "input", label: "Žiro račun:",name:"zirobs",  maxLength: 40,validate:"NotEmpty",
									position:"absolute",labelTop:55,inputTop:55,inputLeft:140,inputWidth:200},
		{type: "input", label: "Poziv na broj:",name:"pozobs",  maxLength: 40,
								position:"absolute",labelTop:80,inputTop:80,inputLeft:140,inputWidth:200},
		{type: "select", label: "Način obustava:",name:"naobs",  options:[
						            {text: "DUG-RATA", value: "1"},
						            {text: "Odbija se samo rata", value: "2"}
						        ],position:"absolute",labelTop:105,labelWidth:110,inputTop: 105,inputLeft:140,inputWidth:120,inputHeight:25},

		
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
	if (koji == "sifobs"){
		var ppsifobs = myForm.getItemValue("sifobs");
		if (ppsifobs != null && ppsifobs.toString().length>0){
			prikaziKREDjedan(ppsifobs.toString());
			myForm.setItemFocus("nazobs");
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
myGrid.setHeader("Šifra,Naziv,Žiro račun,Poziv na broj,Način obustava");//set column names
myGrid.setInitWidths("100,200,200,200,200");//set column width in px
myGrid.setColAlign("left,left,left,left,left");//set column values align
myGrid.setColTypes("ro,ro,ro,ro,ro");//set column types
myGrid.setColSorting("int,str,str,str,str");//set sorting
myGrid.enableAlterCss("even","uneven");
myGrid.setIconsPath('../resources/codebase/imgs/');
myGrid.setStyle("background-color:#6175ea;color:white; font-weight:bold;", "","", "");
myGrid.enableRowsHover(true, "myhover");
myGrid.init();
myGrid.attachEvent("onRowSelect",doOnRowSelected);
myGrid.load("/kreditori/prikaz?naziv= ","json");

function doOnRowSelected(){
	prikaziKred();
}

function prikaziKREDjedan(ssifobs){
	
	//JQuery poziv na iscitavanje podataka jednog radnika kao i prikaz u formu
	$.get("/kreditori/prikazjedan/?sifobs="+ssifobs,{}, function(data) {
		var obj = $.parseJSON(JSON.stringify(data));	
		$.each(obj, function() {
			myForm.setItemValue("sifobs",this['sifobs']); 
			myForm.setItemValue("nazobs",this['nazobs']); 
			myForm.setItemValue("zirobs",this['zirobs']); 
			myForm.setItemValue("pozobs",this['pozobs']); 
			myForm.setItemValue("naobs",this['naobs']); 
			//---------------------------------------------------
		});		
	});
}
function prikaziKred(){
	var datzasn,datod,datdo;
	var red = myGrid.getSelectedRowId();
	//uzimanje vrednosti iz grida
	sifobs = myGrid.cells(red,0).getValue();
	myForm.clear();
	
	//JQuery poziv na iscitavanje podataka jednog radnika kao i prikaz u formu
	$.get("/kreditori/prikazjedan/?sifobs="+sifobs,{}, function(data) {
		var obj = $.parseJSON(JSON.stringify(data));	
		$.each(obj, function() {
			myForm.setItemValue("sifobs",this['sifobs']); 
			myForm.setItemValue("nazobs",this['nazobs']); 
			myForm.setItemValue("zirobs",this['zirobs']); 
			myForm.setItemValue("pozobs",this['pozobs']); 
			myForm.setItemValue("naobs",this['naobs']); 
			//---------------------------------------------------
		});		
	});
}
function Unesi(xx){
	//od parametra xx zavisi da li je unos podataka ili izmena podataka
	var mess1="",strurl="";
	if(xx==1){
		strurl="/kreditori/unos/";
		mess1="Upis nije uspeo";
	}else{
		strurl="/kreditori/izmena/";
		mess1="Izmena nije uspela";
	}
			
	mess2="Popunite osnovne podatke";
	
	sifobs = myForm.getItemValue("sifobs");

	var nazobs = myForm.getItemValue("nazobs").toString();
		if(nazobs.trim().length == 0) {nazobs=" ";}
	var zirobs = myForm.getItemValue("zirobs").toString();
		if(zirobs.trim().length == 0) {zirobs="";}
	var pozobs = myForm.getItemValue("pozobs").toString();
		if(pozobs.trim().length == 0) {pozobs=" ";}
	var naobs = myForm.getItemValue("naobs").toString();
		if(naobs.trim().length == 0) {naobs=" ";}
	//---------------------------------------------------------------
	//parametri koji se prosledjuju serveru na obradu

	var dataajax = "?sifobs=" + sifobs;
	dataajax = dataajax + "&nazobs=" + nazobs;
	dataajax = dataajax + "&zirobs=" + zirobs;
	dataajax = dataajax + "&pozobs=" + pozobs;
	dataajax = dataajax + "&naobs=" + naobs;
		
	if(proveripolja(sifobs,naobs)){
		
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
					myGrid.load("/kreditori/prikaz?naziv= ","json");
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
	sifobs = myForm.getItemValue("sifobs");
	strsifobs = sifobs.toString();
		
	mess1="Brisanje nije uspelo";

	if(sifobs!=null && strsifobs.trim().length>0){
		dhx4.ajax.post("/kreditori/brisanje/?sifobs=" + strsifobs,
			function(r){
			var t = dhx4.s2j(r.xmlDoc.responseText);
			var xml = t.toString();
			
			if(xml!=null){
				if (xml=="0"){
					poruka("Podaci nisu izbrisani");
				}else{
					myForm.clear();
					myGrid.clearAll();
					myGrid.load("/kreditori/prikaz?naziv= ","json");
				}
			}else{
				poruka("greska:ajax");
			}
		});
	}else{
		poruka(mess2);
	}
}
function proveripolja(psifobs,nnaobs) {
	var aa = true;

	if(psifobs == null || psifobs.toString().length==0){
		aa = false;
		return false;
	}
	if(nnaobs==null || nnaobs.toString().length==0){
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
