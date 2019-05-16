var nazkval="",sifkval="";

var myLayout1 = new dhtmlXLayoutObject({parent: myLayout.cells("b"), pattern: "2E"});
myLayout1.cells("a").setHeight(400);
myLayout1.cells("a").setText("DETALJI");
myLayout1.cells("a").cell.style.backgroundColor = "#cbc9af";

myLayout1.cells("b").setText("PODACI");

formData = [
	{type: "settings", position: "label-left", labelWidth: 100, inputWidth: 60,labelHeight:20,inputHeight:22},
	{type: "block", offsetTop: 5, list: [
		{type: "input", label: "Šifra: ",name:"sifkval", value:"",maxLength: 5,validate:"NotEmpty,ValidInteger",
								position:"absolute", labelTop:8,inputTop:5,inputLeft:140},
		{type: "input", label: "Naziv :",name:"nazkval",  maxLength: 30,validate:"NotEmpty",
								position:"absolute",labelTop:30,inputTop:30,inputLeft:140,inputWidth:180},
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
	if (koji == "sifkval"){
		var ppsifkval = myForm.getItemValue("sifkval");
		if (ppsifkval != null && ppsifkval.toString().length>0){
			prikaziKvaljedan(ppsifkval.toString());
			myForm.setItemFocus("nazkval");
		}else{
			myForm.setItemFocus("sifkval");
		}
	}
});

myForm.attachEvent("onButtonClick", function(name){
	if(name=="novi"){
		myForm.clear();
		myForm.setItemFocus("sifkval");
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
myGrid.setHeader("Šifra,Naziv");//set column names
myGrid.setInitWidths("100,250");//set column width in px
myGrid.setColAlign("left,left");//set column values align
myGrid.setColTypes("ro,ro");//set column types
myGrid.setColSorting("int,str");//set sorting
myGrid.enableAlterCss("even","uneven");
myGrid.setIconsPath('../resources/codebase/imgs/');
myGrid.enableRowsHover(true, "myhover");
myGrid.init();
myGrid.attachEvent("onRowSelect",doOnRowSelected);
myGrid.load("/kvalifikacije/prikaz","json");

function doOnRowSelected(){
	prikaziKval();
}

function prikaziKvaljedan(ssifkval){
	$.get("/kvalifikacije/prikazjedan/?sifkval="+ssifkval,{}, function(data) {
		var obj = $.parseJSON(JSON.stringify(data));	
		$.each(obj, function() {
			myForm.setItemValue("sifkval",this['sifkval']); 
			myForm.setItemValue("nazkval",this['nazkval']); 
		});		
	});
}
function prikaziKval(){
	var datzasn,datod,datdo;
	var red = myGrid.getSelectedRowId();
	sifkval = myGrid.cells(red,0).getValue();
	myForm.clear();
	
	$.get("/kvalifikacije/prikazjedan/?sifkval="+sifkval,{}, function(data) {
		var obj = $.parseJSON(JSON.stringify(data));	
		$.each(obj, function() {
			myForm.setItemValue("sifkval",this['sifkval']); 
			myForm.setItemValue("nazkval",this['nazkval']); 
		});		
	});
}
function Unesi(xx){
	//od parametra xx zavisi da li je unos podataka ili izmena podataka
	var mess1="",strurl="";
	if(xx==1){
		strurl="/kvalifikacije/unos/";
		mess1="Upis nije uspeo";
	}else{
		strurl="/kvalifikacije/izmena/";
		mess1="Izmena nije uspela";
	}
			
	mess2="Popunite osnovne podatke";
	
	sifkval = myForm.getItemValue("sifkval");

	var nazkval = myForm.getItemValue("nazkval").toString();
		if(nazkval.trim().length == 0) {nazkval=" ";}
	//---------------------------------------------------------------
	//parametri koji se prosledjuju serveru na obradu

	var dataajax = "?sifkval=" + sifkval;
	dataajax = dataajax + "&nazkval=" + nazkval;
		
	if(proveripolja(sifkval,nazkval)){
		
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
					myGrid.load("/kvalifikacije/prikaz","json");
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
	var mess1="",strsifkval;
	sifkval = myForm.getItemValue("sifkval");
	strsifkval = sifkval.toString();
		
	mess1="Brisanje nije uspelo";

	if(sifkval!=null && strsifkval.trim().length>0){
		dhx4.ajax.post("/kvalifikacije/brisanje/?sifkval=" + strsifkval,
			function(r){
			var t = dhx4.s2j(r.xmlDoc.responseText);
			var xml = t.toString();
			
			if(xml!=null){
				if (xml=="0"){
					poruka("Podaci nisu izbrisani");
				}else{
					myForm.clear();
					myGrid.clearAll();
					myGrid.load("/kvalifikacije/prikaz","json");
				}
			}else{
				poruka("greska:ajax");
			}
		});
	}else{
		poruka(mess2);
	}
}
function proveripolja(psifkval,nnazkval) {
	var aa = true;

	if(psifkval == null || psifkval.toString().length==0){
		aa = false;
		return false;
	}
	if(nnazkval==null || nnazkval.toString().length==0){
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