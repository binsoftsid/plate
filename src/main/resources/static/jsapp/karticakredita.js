var pre="",firma="1",godina="0",odmeseca,domeseca,sifra,sifrakreditora="0",ime="",prezime="";

ucitajFirmu();

formDataRekap = [
	{type: "settings", position: "label-left", labelWidth: 200, inputWidth: 60,labelHeight:20,inputHeight:22},
	{type: "block", offsetTop: 5, list: [
		{type: "input", label: "Od meseca: ",name:"odmeseca", value:"",maxLength: 2,validate:"NotEmpty,ValidInteger,[0-9]+",
								position:"absolute", labelTop:8,inputTop:5,inputLeft:200},
		{type: "input", label: "Do meseca: ",name:"domeseca", value:"",maxLength: 2,validate:"NotEmpty,ValidInteger,[0-9]+",
								position:"absolute", labelTop:30,inputTop:30,inputLeft:200},
		{type: "input", label: "Šifra radnika: ",name:"sifra", value:"",maxLength: 5,validate:"NotEmpty,ValidInteger",
								position:"absolute", labelTop:55,inputTop:55,inputLeft:200},
		{type: "button", value: "***", name:"radnici", position:"absolute",inputTop:50,inputLeft:280,inputWidth:40,inputHeight:7},
		{type: "input", label: "Šifra kreditora: ",name:"sifrakreditora", value:"",maxLength: 5,validate:"NotEmpty,ValidInteger",
								position:"absolute", labelTop:80,inputTop:80,inputLeft:200},
		{type: "button", value: "***", name:"kreditori", position:"absolute",inputTop:75,inputLeft:280,inputWidth:40,inputHeight:7}
									
		]},			
        {type: "block", blockOffset:0,offsetTop:100, list: [
		{type: "button", value: "Formiraj", name:"formiraj", position:"absolute",inputTop: 60,inputLeft:30}
		]}
	];	

var myLayout1 = new dhtmlXLayoutObject({parent: myLayout.cells("b"), pattern: "1C"});
myLayout1.cells("a").setHeight(600);
myLayout1.cells("a").setText("KARTICA KREDITA");
myLayout1.cells("a").cell.style.backgroundColor = "#cbc9af";//#c4e4ec


myFormKred = myLayout1.cells("a").attachForm(formDataRekap);	

myFormKred.attachEvent("onButtonClick", function(name){
	if(name=="formiraj"){
			Formiraj();
	}
	else if(name=="radnici"){
		doWindow();
	}
	else if(name=="kreditori"){
		doWindowKreditori();
	}	
});

myFormKred.attachEvent("onFocus", function(name, value){
	if(name=="sifrakreditora"){
		NadjiRadnika();
	}
});

myFormKred.setItemFocus("odmeseca");

function ucitajFirmu(){
	var ffirma="0";
	$.get("/firma/ucitajfirmu",{}, function(data) {
		var obj = $.parseJSON(JSON.stringify(data));	
		$.each(obj, function() {
			firma = this['firma'];
			godina = this['godina'];
		});		
	});
}
//--------------------------------------------------------------------------------
function doWindow(){
	dhxWins = new dhtmlXWindows();
	
	dhxWins.attachViewportTo("layoutObj");
	
	w1 = dhxWins.createWindow("w1", 150, 50, 600, 500);
	
	w1.setText("Šifarnik radnika");
	var myLayoutRadnici = new dhtmlXLayoutObject({parent:w1, pattern: "1C"});
	myLayoutRadnici.cells("a").hideHeader();
	
	myToolbarRadnici = myLayoutRadnici.cells("a").attachToolbar();
	myToolbarRadnici.addText("tekst", 5, "Traži po prezimenu");
	myToolbarRadnici.addInput("txttrazi", 6, "",100);
	myToolbarRadnici.addButton("trazi", 7, "Traži", "", "");
	myToolbarRadnici.addButton("svi", 8, "Svi", "", "");	
	myToolbarRadnici.setItemImage("trazi","/common/imgs/search.gif");
	myToolbarRadnici.setItemImage("svi","/common/imgs/tombs.gif");
	
	myToolbarRadnici.attachEvent("onClick", function(id) {
		if(id == "trazi"){
			var uslov = myToolbarRadnici.getValue("txttrazi");
			var struslov = uslov.toString();			
			if(struslov.trim().length>0){
				myGridRadnici.clearAll();
				myGridRadnici.load("/maticna/prikaz?prezime="+struslov+"&pre="+firma,"json");
			}
		}
		else if(id == "svi"){
			myGridRadnici.clearAll();
			myGridRadnici.load("/maticna/prikaz?prezime=&pre="+firma,"json");
		}

	});	
	myGridRadnici = myLayoutRadnici.cells("a").attachGrid();
	myGridRadnici.setImagePath("../resources/codebase/imgs/");//path to images required by grid
	myGridRadnici.setHeader("Šifra,Ime,Prezime");//set column names
	myGridRadnici.setInitWidths("60,200,200");//set column width in px
	myGridRadnici.setColAlign("left,left,left");//set column values align
	myGridRadnici.setColTypes("ro,ro,ro");//set column types
	myGridRadnici.setColSorting("int,str,str");//set sorting
	myGridRadnici.setStyle("background-color:#6175ea;color:white; font-weight:bold;", "","", "");
	myGridRadnici.init();
	myGridRadnici.attachEvent("onRowSelect",doOnRowSelectedRadnici);
	myGridRadnici.load("/maticna/prikaz?prezime=&pre=" + firma,"json");
}
//-----------------------------------------------------------------------------
function doOnRowSelected(){
	prikaziRadnika(1);
}
//-----------------------------------------------------------------------------
function doOnRowSelectedRadnici(){
	var red = myGridRadnici.getSelectedRowId();
	sifra = myGridRadnici.cells(red,0).getValue();
	var imee,prezimee;
	ime = myGridRadnici.cells(red,1).getValue();
	prezime = myGridRadnici.cells(red,2).getValue();
	
	myLayout1.cells("a").setText("KARTICA RADNIKA :&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + ime + "&nbsp;&nbsp; " + prezime);
	
	myFormKred.setItemValue("sifra",sifra); 
	myFormKred.setItemFocus("formiraj");	
	dhxWins.unload();
	dhxWins = w1 = null;

}
//--------------------------------------------------------------------------------
function doWindowKreditori(){
	dhxWins = new dhtmlXWindows();
	dhxWins.attachViewportTo("layoutObj");
	w1 = dhxWins.createWindow("w1", 150, 50, 600, 500);
	
	w1.setText("Šifarnik kreditora");
	var myLayoutKreditori = new dhtmlXLayoutObject({parent:w1, pattern: "1C"});
	myLayoutKreditori.cells("a").hideHeader();
	
	myToolbarKreditori = myLayoutKreditori.cells("a").attachToolbar();
	myToolbarKreditori.addText("tekst", 5, "Traži po nazivu");
	myToolbarKreditori.addInput("txttrazi", 6, "",100);
	myToolbarKreditori.addButton("trazi", 7, "Traži", "", "");
	myToolbarKreditori.addButton("svi", 8, "Svi", "", "");	
	myToolbarKreditori.setItemImage("trazi","/common/imgs/search.gif");
	myToolbarKreditori.setItemImage("svi","/common/imgs/tombs.gif");
	
	myToolbarKreditori.attachEvent("onClick", function(id) {
		if(id == "trazi"){
			var uslov = myToolbarKreditori.getValue("txttrazi");
			var struslov = uslov.toString();			
			if(struslov.trim().length>0){
				myGridKreditori.clearAll();
				myGridKreditori.load("/kreditori/prikaz/?naziv="+struslov,"json");
			}
		}
		else if(id == "svi"){
			myGridKreditori.clearAll();
			myGridKreditori.load("/kreditori/prikaz/?naziv= ","json");
		}
	});	
	myGridKreditori = myLayoutKreditori.cells("a").attachGrid();
	myGridKreditori.setImagePath("../resources/codebase/imgs/");//path to images required by grid
	myGridKreditori.setHeader("Šifra,Naziv,Žiro račun,Poziv na broj,Način obustava");//set column names
	myGridKreditori.setInitWidths("100,200,200,200,200");//set column width in px
	myGridKreditori.setColAlign("left,left,left,left,left");//set column values align
	myGridKreditori.setColTypes("ro,ro,ro,ro,ro");//set column types
	myGridKreditori.setColSorting("int,str,str,str,str");//set sorting

	myGridKreditori.setIconsPath('../resources/codebase/imgs/');
	myGridKreditori.setStyle("background-color:#6175ea;color:white; font-weight:bold;", "","", "");
	myGridKreditori.init();
	myGridKreditori.enableAlterCss("even","uneven");
	myGridKreditori.attachEvent("onRowSelect",doOnRowSelectedKreditori);
	myGridKreditori.load("/kreditori/prikaz/?naziv= ","json");
}
function doOnRowSelectedKreditori(){
	var red = myGridKreditori.getSelectedRowId();
	sifrakred = myGridKreditori.cells(red,0).getValue();
	var nazivv;
	nazivv = myGridKreditori.cells(red,1).getValue();
	myFormKred.setItemValue("sifrakreditora",sifrakred); 
	myFormKred.setItemFocus("formiraj");
	dhxWins.unload();
	dhxWins = w1 = null;
}
//--------------------------------------------------------------------------------
function NadjiRadnika(){
	//JQuery poziv na iscitavanje podataka jednog radnika kao i prikaz u formu
	var ssifra = myFormKred.getItemValue("sifra"); 
	$.get("/maticna/prikazjedan/?sifra=" + ssifra + "&pre=" + firma.toString(), 
			{}, function(data) {
		var obj = $.parseJSON(JSON.stringify(data));	
		$.each(obj, function() {
			ime = this['ime'];
			prezime = this['prezime'];
			//ispis imena radnika na layout iz tabele maticnipodaci
			myLayout1.cells("a").setText("KARTICA RADNIKA :&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + ime + "&nbsp;&nbsp; " + prezime);
		
		});		
	});
}
//--------------------------------------------------------------------------------
function Formiraj(){
	
		var pre,vrstaplate;

		pre = firma;
		odmeseca = myFormKred.getItemValue("odmeseca");
		domeseca = myFormKred.getItemValue("domeseca");
		sifra = myFormKred.getItemValue("sifra");
		sifrakreditora = myFormKred.getItemValue("sifrakreditora");
		
	if (proveripolja()){		
		var dataajax = "?pre=" + firma.toString();
		dataajax = dataajax + "&godina=" + godina.toString();
		dataajax = dataajax + "&odmeseca=" + odmeseca.toString();
		dataajax = dataajax + "&domeseca=" + domeseca.toString();
		dataajax = dataajax + "&sifra=" + sifra.toString();
		dataajax = dataajax + "&sifrakreditora=" + sifrakreditora.toString();
		dataajax = dataajax + "&ime=" + ime.toString();
		dataajax = dataajax + "&prezime=" + prezime.toString();
	
		window.open("/print/karticakrediti"+dataajax); 
		
	}else{
		poruka("Polja parametara nisu popunjena");
	}
}
//-----------------------------------------------------------------------------
function proveripolja() {
	var aa = true;

	if(odmeseca == null || odmeseca.toString().length==0){
		return false;
	}
	if(domeseca == null || domeseca.toString().length==0){
		return false;
	}
	if(sifra == null || sifra.toString().length==0){
		return false;
	}
	return aa;
}
//-----------------------------------------------------------------------------
