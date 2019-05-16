var pre="",nazban="",zirban="",nadimak="";


var myLayout1 = new dhtmlXLayoutObject({parent: myLayout.cells("b"), pattern: "2E"});

myLayout1.cells("a").setHeight(400);
myLayout1.cells("a").setText("BANKE");
myLayout1.cells("a").cell.style.backgroundColor = "#cbc9af";//#c4e4ec
myLayout1.cells("b").setText("PODACI");



formData = [
	{type: "settings", position: "label-left", labelWidth: 120, inputWidth: 60,labelHeight:20,inputHeight:22},
	{type: "block", offsetTop: 5, list: [
		{type: "input", label: "Šifra: ",name:"sifban", value:"",maxLength: 5,validate:"NotEmpty,ValidInteger",
								position:"absolute", labelTop:8,inputTop:5,inputLeft:140},
		{type: "input", label: "Naziv banke:",name:"nazban",  maxLength: 30,validate:"NotEmpty",
								position:"absolute",labelTop:30,inputTop:30,inputLeft:140,inputWidth:180},
		{type: "input", label: "Žiro račun:",name:"zirban",  maxLength: 40,validate:"NotEmpty",
									position:"absolute",labelTop:55,inputTop:55,inputLeft:140,inputWidth:180},
		{type: "input", label: "Poziv na broj:",name:"pozban",  maxLength: 40,
								position:"absolute",labelTop:80,inputTop:80,inputLeft:140,inputWidth:180},
		{type: "select", label: "Vrsta isplate:",name:"isplata",  options:[
						            {text: "Tekući", value: "1"},
						            {text: "Štednja", value: "2"}
						        ],position:"absolute",labelTop:105,inputTop: 105,inputLeft:140,inputWidth:180,inputHeight:25},
		
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
	if (koji == "sifban"){
		var ppsifban = myForm.getItemValue("sifban");
		if (ppsifban != null && ppsifban.toString().length>0){
			prikaziBankejedan(ppsifban.toString());
			myForm.setItemFocus("nazban");
		}else{
			myForm.setItemFocus("sifban");
		}
	}
});

myForm.attachEvent("onButtonClick", function(name){
	if(name=="novi"){
		myForm.clear();
		myForm.setItemFocus("sifban");
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
myGrid.setHeader("Šifra,Naziv,Žiro račun,Poziv na broj,Isplata");//set column names
myGrid.setInitWidths("100,200,150,150,70");//set column width in px
myGrid.setColAlign("left,left,left,left,left");//set column values align
myGrid.setColTypes("ro,ro,ro,ro,ro");//set column types
myGrid.setColSorting("int,str,str,str,str");//set sorting
myGrid.setIconsPath('../codebase/imgs/dhxgrid_material/');
myGrid.setStyle("background-color:#6175ea;color:white; font-weight:bold;", "","", "");
myGrid.enableRowsHover(true, "myhover");
myGrid.init();
myGrid.attachEvent("onRowSelect",doOnRowSelected);
myGrid.enableAlterCss("even","uneven");
myGrid.load("/banke/prikaz","json");

function doOnRowSelected(){
	prikaziBanke();
}

function prikaziBankejedan(ssifban){
	
	//JQuery poziv na iscitavanje podataka jednog radnika kao i prikaz u formu
	$.get("/banke/prikazjedan/?sifban="+ssifban,{}, function(data) {
		var obj = $.parseJSON(JSON.stringify(data));	
		$.each(obj, function() {
			myForm.setItemValue("sifban",this['sifban']); 
			myForm.setItemValue("nazban",this['nazban']); 
			myForm.setItemValue("zirban",this['zirban']); 
			myForm.setItemValue("pozban",this['pozban']); 
			myForm.setItemValue("isplata",this['isplata']); 
			//---------------------------------------------------
		});		
	});
}
function prikaziBanke(){
	var datzasn,datod,datdo;
	var red = myGrid.getSelectedRowId();
	//uzimanje vrednosti iz grida
	sifban = myGrid.cells(red,0).getValue();
	myForm.clear();
	
	//JQuery poziv na iscitavanje podataka jednog radnika kao i prikaz u formu
	$.get("/banke/prikazjedan/?sifban="+sifban,{}, function(data) {
		var obj = $.parseJSON(JSON.stringify(data));	
		$.each(obj, function() {
			myForm.setItemValue("sifban",this['sifban']); 
			myForm.setItemValue("nazban",this['nazban']); 
			myForm.setItemValue("zirban",this['zirban']); 
			myForm.setItemValue("pozban",this['pozban']); 
			myForm.setItemValue("isplata",this['isplata']); 			//---------------------------------------------------
		});		
	});
}
function Unesi(xx){
	//od parametra xx zavisi da li je unos podataka ili izmena podataka
	var mess1="",strurl="";
	if(xx==1){
		strurl="/banke/unos/";
		mess1="Upis nije uspeo";
	}else{
		strurl="/banke/izmena/";
		mess1="Izmena nije uspela";
	}
			
	mess2="Popunite osnovne podatke";
	
	sifban = myForm.getItemValue("sifban");

	var nazban = myForm.getItemValue("nazban").toString();
		if(nazban.trim().length == 0) {nazban=" ";}
	var zirban = myForm.getItemValue("zirban").toString();
		if(zirban.trim().length == 0) {zirban="";}
	var pozban = myForm.getItemValue("pozban").toString();
		if(pozban.trim().length == 0) {pozban=" ";}
	var isplata = myForm.getItemValue("isplata").toString();
		if(isplata.trim().length == 0) {isplata="1";}
	//---------------------------------------------------------------
	//parametri koji se prosledjuju serveru na obradu

	var dataajax = "?sifban=" + sifban;
	dataajax = dataajax + "&nazban=" + nazban;
	dataajax = dataajax + "&zirban=" + zirban;
	dataajax = dataajax + "&pozban=" + pozban;
	dataajax = dataajax + "&isplata=" + isplata;
		
	if(proveripolja(sifban,nazban)){
		
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
					myGrid.load("/banke/prikaz","json");
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
	var mess1="",strsifban;
	sifban = myForm.getItemValue("sifban");
	strsifban = sifban.toString();
		
	mess1="Brisanje nije uspelo";

	if(sifban!=null && strsifban.trim().length>0){
		dhx4.ajax.post("/banke/brisanje/?sifban=" + strsifban,
			function(r){
			var t = dhx4.s2j(r.xmlDoc.responseText);
			var xml = t.toString();
			
			if(xml!=null){
				if (xml=="0"){
					poruka("Podaci nisu izbrisani");
				}else{
					myForm.clear();
					myGrid.clearAll();
					myGrid.load("/banke/prikaz","json");
				}
			}else{
				poruka("greska:ajax");
			}
		});
	}else{
		poruka(mess2);
	}
}
function proveripolja(psifban,nnazban) {
	var aa = true;

	if(psifban == null || psifban.toString().length==0){
		aa = false;
		return false;
	}
	if(nnazban==null || nnazban.toString().length==0){
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