var pre="",firma="1",godina="0",odmeseca,domeseca,sifra;

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
		{type: "button", value: "***", name:"radnici", position:"absolute",inputTop:50,inputLeft:280,inputWidth:40,inputHeight:7}
									
		]},			
        {type: "block", blockOffset:0,offsetTop:100, list: [
		{type: "button", value: "Formiraj", name:"formiraj", position:"absolute",inputTop: 60,inputLeft:30}
		]}
	];	

var myLayout1 = new dhtmlXLayoutObject({parent: myLayout.cells("b"), pattern: "1C"});
myLayout1.cells("a").setHeight(600);
myLayout1.cells("a").setText("KARTICA RADNIKA");
myLayout1.cells("a").cell.style.backgroundColor = "#cbc9af";


myFormRekap = myLayout1.cells("a").attachForm(formDataRekap);	

myFormRekap.attachEvent("onButtonClick", function(name){
	if(name=="formiraj"){
			Formiraj();
	}
	else if(name=="radnici"){
		doWindow();
	}
});

myFormRekap.setItemFocus("odmeseca");

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
	imee = myGridRadnici.cells(red,1).getValue();
	prezimee = myGridRadnici.cells(red,2).getValue();
	
	myLayout1.cells("a").setText("KARTICA RADNIKA :&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + imee + "&nbsp;&nbsp; " + prezimee);
	
	myFormRekap.setItemValue("sifra",sifra); 
	myFormRekap.setItemFocus("formiraj");	
	dhxWins.unload();
	dhxWins = w1 = null;

}//--------------------------------------------------------------------------------
function Formiraj(){
	
		var pre,vrstaplate;

		pre = firma;
		odmeseca = myFormRekap.getItemValue("odmeseca");
		domeseca = myFormRekap.getItemValue("domeseca");
		sifra = myFormRekap.getItemValue("sifra");
		
	if (proveripolja()){		
		var dataajax = "?pre=" + firma.toString();
		dataajax = dataajax + "&godina=" + godina.toString();
		dataajax = dataajax + "&odmeseca=" + odmeseca.toString();
		dataajax = dataajax + "&domeseca=" + domeseca.toString();
		dataajax = dataajax + "&sifra=" + sifra.toString();
	
		window.open("/print/karticaradnika"+dataajax); 
		
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
