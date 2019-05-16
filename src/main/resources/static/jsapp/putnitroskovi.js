
var sifra="",firma="0",godina="0",meses="0",brisplate="0",datum="";
var neoporeziviiznos="",porez="",brutt="0",ukuiznn="0",bruto="0",iznosporeza="0",ukupaniznos="0";

var myLayout1 = new dhtmlXLayoutObject({parent: myLayout.cells("b"), pattern: "2E"});
myLayout1.cells("a").setHeight(500);
myLayout1.cells("a").setText("PUTNI TROŠKOVI");
myLayout1.cells("a").cell.style.backgroundColor = "#cbc9af";

myLayout1.cells("b").setText("TABELA PUTNIH TROŠKOVA");

formData = [
	{type: "settings", position: "label-left", labelWidth: 120, inputWidth: 60,labelHeight:20,inputHeight:22},
	{type: "block", offsetTop: 5, list: [
		{type: "input", label: "Mesec: ",name:"mesec", value:"",maxLength: 2,validate:"NotEmpty,ValidInteger",
								position:"absolute", labelTop:8,inputTop:5,inputLeft:140},
		{type: "input", label: "Broj isplate:",name:"brisplate",  maxLength: 2,validate:"NotEmpty,ValidInteger",
								position:"absolute",labelTop:30,inputTop:30,inputLeft:140,inputWidth:150},
		{type: "input", label: "Šifra radnika:",name:"sifra",  maxLength: 5,validate:"NotEmpty,ValidInteger",
									position:"absolute",labelTop:55,inputTop:55,inputLeft:140,inputWidth:150},
		{type: "calendar", dateFormat: "%d-%m-%Y", label: "Datum isplate:",name:"datum",validate:"NotEmpty",
									position:"absolute",labelTop:80,inputTop:80,inputLeft:140,inputWidth:150},
		{type: "input", label: "Neto iznos.:",name:"neto",  maxLength: 12,validate:"NotEmpty,ValidNumeric",
								position:"absolute",labelTop:105,inputTop:105,inputLeft:140,inputWidth:150},
		{type: "input", label: "Neopr. iznos:",name:"neoporiznos",  maxLength: 12,validate:"NotEmpty,ValidNumeric",
								position:"absolute",labelTop:130,inputTop:130,inputLeft:140,inputWidth:150},
		{type: "input", label: "Porez(%):",name:"porez",  maxLength: 4,validate:"NotEmpty,ValidNumeric",
								position:"absolute",labelTop:155,inputTop:155,inputLeft:140,inputWidth:150},
		{type: "input", label: "Bruto iznos:",name:"bruto",  maxLength: 12,validate:"NotEmpty,ValidNumeric",
								position:"absolute",labelTop:180,inputTop:180,inputLeft:140,inputWidth:150},
		{type: "input", label: "Iznos poreza:",name:"iznosporeza",  maxLength: 12,validate:"NotEmpty,ValidNumeric",
								position:"absolute",labelTop:205,inputTop:205,inputLeft:140,inputWidth:150},
		{type: "input", label: "Ukupan iznos:",name:"ukupaniznos",  maxLength: 12,validate:"NotEmpty,ValidNumeric",
								position:"absolute",labelTop:230,inputTop:230,inputLeft:140,inputWidth:150}
		]},

		{type: "block", blockOffset:0,offsetTop:290, list: [
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
			//myForm.setItemFocus("prezime");
		}else{
			myForm.setItemFocus("sifra");
		}
	}else if (koji == "neto"){
		var nneto = myForm.getItemValue("neto");
		if (nneto != null && nneto.toString().length>0){
			nadjiParametre();
		}else{
			myForm.setItemFocus("neto");
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
		myForm.setItemFocus("mesec");
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
myGrid.setHeader("Mesec,Br.isplate,Šifra rad.,Datum,Neto,Neopr.iznos,Porez(%),Bruto,Izn.poreza,Ukupno");//set column names
myGrid.setInitWidths("100,100,100,120,100,100,100,100,100,100");//set column width in px
myGrid.setColAlign("left,left,left,left,left,left,left,left,left,left");//set column values align
myGrid.setColTypes("ro,ro,ro,ro,ro,ro,ro,ro,ro,ro");//set column types
myGrid.setColSorting("int,int,int,str,str,str,str,str,str,str");//set sorting
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
			myGrid.load("/putnitroskovi/prikaz?pre="+firma+"&godina="+godina.toString(),"json");			
			godina = this['godina'];
		});		
	});
}
function nadjiParametre(){
	var ffirma="0",iznporr="0",nneto="";
	
	nneto = myForm.getItemValue("neto");
	
	$.get("/putnitroskovi/parametri?neto="+nneto,{}, function(data) {
		var obj = $.parseJSON(JSON.stringify(data));	
		$.each(obj, function() {
			
			myForm.setItemValue("neoporiznos",this['neoporiznos']); 
			myForm.setItemValue("porez",this['porez']); 
			myForm.setItemValue("bruto",this['bruto']); 
			myForm.setItemValue("iznosporeza",this['iznosporeza']); 
			myForm.setItemValue("ukupaniznos",this['ukupaniznos']); 
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
		mesec = myGrid.cells(red,0).getValue();
		brisplate = myGrid.cells(red,1).getValue();
		sifra = myGrid.cells(red,2).getValue();
		myForm.clear();
	}else{
		mesec =  myForm.getItemValue("mesec").toString();
		brisplate =  myForm.getItemValue("brisplate").toString();
		sifra =  myForm.getItemValue("sifra").toString();
	}
	
	$.get("/putnitroskovi/prikazjedan/?sifra="+sifra+"&pre="+firma+"&brisplate="+brisplate+"&mesec="+mesec+
			"&godina="+godina.toString(),{}, function(data) {
		var ime='';
		var obj = $.parseJSON(JSON.stringify(data));	
		$.each(obj, function() {
			myForm.setItemValue("mesec",this['mesec']); 
			myForm.setItemValue("brisplate",this['brisplate']);
			myForm.setItemValue("sifra",this['sifra']); 
			myForm.setItemValue("datum",formatDate(this['datum'])); 
			myForm.setItemValue("neto",this['neto']); 
			myForm.setItemValue("neoporiznos",this['neoporiznos']); 
			myForm.setItemValue("porez",this['porez']); 
			myForm.setItemValue("bruto",this['bruto']); 
			myForm.setItemValue("iznosporeza",this['iznosporeza']); 
			myForm.setItemValue("ukupaniznos",this['ukupaniznos']); 
			//---------------------------------------------------
		});		
	});
}
function Unesi(xx){
	//od parametra xx zavisi da li je unos podataka ili izmena podataka
	var mess1="",strurl="";
	if(xx==1){
		strurl="/putnitroskovi/unos/";
		mess1="Upis nije uspeo";
	}else{
		strurl="/putnitroskovi/izmena/";
		mess1="Izmena nije uspela";
	}
			
	mess2="Popunite osnovne podatke";
	
	sifra = myForm.getItemValue("sifra");

	mesec = myForm.getItemValue("mesec").toString();
		if(mesec.trim().length == 0) {mesec="";}
	brisplate = myForm.getItemValue("brisplate").toString();
		if(brisplate.trim().length == 0) {brisplate="";}
	var neto = myForm.getItemValue("neto").toString();
		if(neto.trim().length == 0) {neto="0";}
	var neoporiznos = myForm.getItemValue("neoporiznos").toString();
		if(neoporiznos.trim().length == 0) {neoporiznos="0";}
	var porez = myForm.getItemValue("porez").toString();
		if(porez.trim().length == 0) {porez="0";}
	var bruto = myForm.getItemValue("bruto").toString();
		if(bruto.trim().length == 0) {bruto="0";}
	var iznosporeza = myForm.getItemValue("iznosporeza").toString();
		if(iznosporeza.trim().length == 0) {iznosporeza="0";}
	var ukupaniznos = myForm.getItemValue("ukupaniznos").toString();
		if(ukupaniznos.trim().length == 0) {ukupaniznos="1";}
		
		datum = myForm.getItemValue("datum").toString();
		if(datum.trim().length == 0) {
			datum="2000-01-01";
		}
		datum = konvertujDatumSQL(datum);
		
		if(godina == null) {
			godina="2000";
		}

	//---------------------------------------------------------------
	//parametri koji se prosledjuju serveru na obradu
	var dataajax = "?pre=" + firma;
	dataajax = dataajax + "&mesec=" + mesec;
	dataajax = dataajax + "&brisplate=" + brisplate;
	dataajax = dataajax + "&sifra=" + sifra;
	dataajax = dataajax + "&datum=" + datum;
	dataajax = dataajax + "&neto=" + neto;
	dataajax = dataajax + "&neoporiznos=" + neoporiznos;
	dataajax = dataajax + "&porez=" + porez;
	dataajax = dataajax + "&bruto=" + bruto;
	dataajax = dataajax + "&iznosporeza=" + iznosporeza;
	dataajax = dataajax + "&ukupaniznos=" + ukupaniznos;
	dataajax = dataajax + "&godina=" + godina;
	
	if(proveripolja(sifra,mesec,brisplate)){
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
					myGrid.load("/putnitroskovi/prikaz?pre="+firma+"&godina="+godina.toString(),"json");
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
	var mess1="";
	sifra = myForm.getItemValue("sifra");
	mesec = myForm.getItemValue("mesec");
	brisplate = myForm.getItemValue("brisplate");
		
	mess1="Brisanje nije uspelo";

	if(sifra!=null && sifra.toString().trim().length>0){
		dhx4.ajax.post("/putnitroskovi/brisanje/?sifra="+sifra.toString()+"&pre="+firma+"&godina="+godina+
				"&mesec="+mesec.toString()+"&brisplate="+brisplate.toString(),
			function(r){
			var t = dhx4.s2j(r.xmlDoc.responseText);
			var xml = t.toString();
			
			if(xml!=null){
				if (xml=="0"){
					poruka("Podaci nisu izbrisani");
				}else{
					myForm.clear();
					myGrid.clearAll();
					myGrid.load("/putnitroskovi/prikaz?pre="+firma+"&godina="+godina.toString(),"json");
				}
			}else{
				poruka("greska:ajax");
			}
		});
	}else{
		poruka(mess2);
	}
}
function proveripolja(ssifra,mmesec,bbriplate) {
	var aa = true;

	if(ssifra == null || ssifra.toString().length==0){
		aa = false;
		return false;
	}
	if(mmesec==null || mmesec.toString().length==0){
		aa = false;
		return false;
	}
	if(bbriplate == null || bbriplate.toString().length==0){
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