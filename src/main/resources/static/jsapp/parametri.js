
var rbr="",opis="",opis="",nadimak="";
//polje nadimak sluzi da se vidi da li je radnik vlasnik firme (D/N) jer ako jeste onda
//se on stavlja na sifru 1

var myLayout1 = new dhtmlXLayoutObject({parent: myLayout.cells("b"), pattern: "2E"});
myLayout1.cells("a").setHeight(400);
myLayout1.cells("a").setText("PARAMETRI OBRAČUNA");
myLayout1.cells("a").cell.style.backgroundColor = "#cbc9af";

myLayout1.cells("b").setText("PODACI");

formData = [
	{type: "settings", position: "label-left", labelWidth: 100, inputWidth: 60,labelHeight:20,inputHeight:22},
	{type: "block", offsetTop: 5, list: [
		{type: "input", label: "Rbr: ",name:"rbr", value:"",maxLength: 5,validate:"NotEmpty,ValidInteger",disabled:"true",
								position:"absolute", labelTop:8,inputTop:5,inputLeft:110},
		{type: "input", label: "Vrsta:",name:"vrsta",  maxLength: 4,validate:"NotEmpty,ValidInteger",disabled:"true",
								position:"absolute",labelTop:30,inputTop:30,inputLeft:110,inputWidth:150},
		{type: "input", label: "Opis:",name:"opis",  maxLength: 30,validate:"NotEmpty,ValidInteger",disabled:"true",
									position:"absolute",labelTop:55,inputTop:55,inputLeft:110,inputWidth:150},
		{type: "input", label: "Prosek da-ne:",name:"prosek",  maxLength: 4,validate:"NotEmpty,ValidInteger",
								position:"absolute",labelTop:80,inputTop:80,inputLeft:110,inputWidth:150},
		{type: "input", label: "Koeficijent:",name:"ldkoef",  maxLength: 10,validate:"NotEmpty,ValidNumber",
								position:"absolute",labelTop:105,inputTop:105,inputLeft:110,inputWidth:150},
		{type: "input", label: "Ul.u zbir neto:",name:"ldzbir",  maxLength: 4,validate:"NotEmpty,ValidInteger",
								position:"absolute",labelTop:130,inputTop:130,inputLeft:110,inputWidth:150},
		{type: "input", label: "Osn.za minuli rad:",name:"ldminu",  maxLength: 4,validate:"NotEmpty,ValidInteger",
								position:"absolute",labelTop:155,inputTop:155,inputLeft:110,inputWidth:150},
		{type: "input", label: "Osn.stimulacije:",name:"ldstim",  maxLength: 4,validate:"NotEmpty,ValidInteger",
								position:"absolute",labelTop:180,inputTop:180,inputLeft:110,inputWidth:150},
		{type: "input", label: "Osn. poreza:",name:"ldpor",  maxLength: 4,validate:"NotEmpty,ValidInteger",
									position:"absolute",labelTop:205,inputTop:205,inputLeft:110,inputWidth:150},
		{type: "input", label: "Procenat:",name:"ldproc",  maxLength: 4,validate:"NotEmpty,ValidInteger",
								position:"absolute",labelTop:230,inputTop:230,inputLeft:110},
		{type: "input", label: "Štampa sati:",name:"dalic",  maxLength: 4,validate:"NotEmpty,ValidInteger",
								position:"absolute",labelTop:255,inputTop:255,inputLeft:110,inputWidth:150},
		{type: "input", label: "Štampa iznosa:",name:"dalii",  maxLength: 4,validate:"NotEmpty,ValidInteger",
								position:"absolute",labelTop:280,inputTop:280,inputLeft:110,inputWidth:150}
		]},			
		{type: "block", blockOffset:0,offsetTop:250, list: [
		{type: "button", value: "Izmeni", name:"izmeni",position:"absolute",inputTop: 60,inputLeft:128},
		]}
		];	

myForm = myLayout1.cells("a").attachForm(formData);
myForm.enableLiveValidation(true);

myForm.attachEvent("onButtonClick", function(name){
	if(name=="izmeni"){
		//za izmenu koristi istu funkciju sa markerom 2
		Unesi(2);
	}

});

myGrid = myLayout1.cells("b").attachGrid();
myGrid.setIconsPath('../codebase/imgs/dhxgrid_material/');
myGrid.setHeader("Rbr,Vrsta,Opis,Prosek da-ne,Koeficijent,Ul.u zbir neto,Osn za minuli rad,Osn.stimulacije,Osn.poreza,Procenat,Štampa sati,Štampa iznosa");
myGrid.setInitWidths("60,60,200,80,80,80,80,80,80,80,80,80");//set column width in px
myGrid.setColAlign("left,left,left,left,left,left,left,left,left,left,left,left");//set column values align
myGrid.setColTypes("ro,ro,ro,ro,ro,ro,ro,ro,ro,ro,ro,ro");//set column types
myGrid.setColSorting("int,str,str,str,str,str,str,str,str,str,str,str");//set sorting
myGrid.setStyle("background-color:#6175ea;color:white; font-weight:bold;", "","", "");
myGrid.enableRowsHover(true, "myhover");
myGrid.init();

myGrid.attachEvent("onRowSelect",doOnRowSelected);
myGrid.enableAlterCss("even","uneven");
myGrid.load("/parametri/prikaz","json");

function doOnRowSelected(){
	prikaziParametre(1);
}

function prikaziParametre(xx){
	var datzasn,datod,datdo;
	if(xx == 1){
		var red = myGrid.getSelectedRowId();
		//uzimanje vrednosti iz grida
		rbr = myGrid.cells(red,0).getValue();
		myForm.clear();
	}else{
		pre =  myForm.getItemValue("pre").toString();
	}
	//JQuery poziv na iscitavanje podataka jednog parametra kao i prikaz u formu
	$.get("/parametri/prikazjedan/?rbr="+rbr,{}, function(data) {
		var obj = $.parseJSON(JSON.stringify(data));	
		$.each(obj, function() {
			myForm.setItemValue("rbr",this['rbr']); 
			myForm.setItemValue("vrsta",this['vrsta']); 
			myForm.setItemValue("opis",this['opis']); 
			myForm.setItemValue("prosek",this['prosek']); 
			myForm.setItemValue("ldkoef",this['ldkoef']); 
			myForm.setItemValue("ldzbir",this['ldzbir']); 
			myForm.setItemValue("ldminu",this['ldminu']); 
			myForm.setItemValue("ldstim",this['ldstim']); 
			myForm.setItemValue("ldpor",this['ldpor']); 
			myForm.setItemValue("ldproc",this['ldproc']); 
			myForm.setItemValue("dalic",this['dalic']); 
			myForm.setItemValue("dalii",this['dalii']); 
			//---------------------------------------------------
		});		
	});
}
function Unesi(xx){
	//od parametra xx zavisi da li je unos podataka ili izmena podataka
	var mess1="",strurl="";
	if(xx==1){
		strurl="/parametri/unos/";
		mess1="Upis nije uspeo";
	}else{
		strurl="/parametri/izmena/";
		mess1="Izmena nije uspela";
	}
			
	mess2="Popunite osnovne podatke";
	
	pre = myForm.getItemValue("pre");

	var rbr = myForm.getItemValue("rbr").toString();
		if(rbr.trim().length == 0) {rbr="0";}
	var vrsta = myForm.getItemValue("vrsta").toString();
		if(vrsta.trim().length == 0) {vrsta="1";}
	var opis = myForm.getItemValue("opis").toString();
		if(opis.trim().length == 0) {opis="";}
	var prosek = myForm.getItemValue("prosek").toString();
		if(prosek.trim().length == 0) {prosek="0";}
	var ldkoef = myForm.getItemValue("ldkoef").toString();
		if(ldkoef.trim().length == 0) {ldkoef="1";}
	var ldzbir = myForm.getItemValue("ldzbir").toString();
		if(ldzbir.trim().length == 0) {ldzbir="1";}
	var ldminu = myForm.getItemValue("ldminu").toString();
		if(ldminu.trim().length == 0) {ldminu="0";}
	var ldstim = myForm.getItemValue("ldstim").toString();
		if(ldstim.trim().length == 0) {ldstim="0";}
	var ldpor = myForm.getItemValue("ldpor").toString();
		if(ldpor.trim().length == 0) {ldpor="0";}
	var ldproc = myForm.getItemValue("ldproc").toString();
		if(ldproc.trim().length == 0) {ldproc="0";}
	var dalic = myForm.getItemValue("dalic").toString();
		if(dalic.trim().length == 0) {dalic="0";}
	var dalii = myForm.getItemValue("dalii").toString();
		if(dalii.trim().length == 0) {dalii="0";}
	//---------------------------------------------------------------
	//parametri koji se prosledjuju serveru na obradu

	var dataajax = "?rbr=" + rbr;
	dataajax = dataajax + "&vrsta=" + vrsta;
	dataajax = dataajax + "&opis=" + opis;
	dataajax = dataajax + "&prosek=" + prosek;
	dataajax = dataajax + "&ldkoef=" + ldkoef;
	dataajax = dataajax + "&ldzbir=" + ldzbir;
	dataajax = dataajax + "&ldminu=" + ldminu;
	dataajax = dataajax + "&ldstim=" + ldstim;
	dataajax = dataajax + "&ldproc=" + ldproc;
	dataajax = dataajax + "&ldpor=" + ldpor;
	dataajax = dataajax + "&dalic=" + dalic;
	dataajax = dataajax + "&dalii=" + dalii;
		
	if(proveripolja(rbr,opis,vrsta)){
		
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
					myGrid.load("/parametri/prikaz","json");
				}
			}else{
				poruka("greska:ajax");
			}
		});
	}else{
		poruka(mess2);
	}
}
function proveripolja(rrbr,oopis,vvrsta) {
	var aa = true;

	if(rrbr == null || rrbr.toString().length==0){
		aa = false;
		return false;
	}
	if(oopis==null || oopis.toString().length==0){
		aa = false;
		return false;
	}
	if(vvrsta == null || vvrsta.toString().length==0){
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
