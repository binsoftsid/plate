//definisanje globalnih varijabli - objekata
var myTree,myToolbar,myGrid,form_1,w1,formData,myForm;
var idd = null,nazivv = null,predd = null,jurr=null;

formData = [
		{type: "settings", position: "label-left", labelWidth: 100, inputWidth: 120},
		{type: "block", inputWidth: "auto", offsetTop: 12, list: [
			{type: "input", label: "Pred", name:"pre", value: "",maxLength: 4,validate: "NotEmpty,ValidInteger"},
			{type: "input", label: "Sifra", name:"jur", value: "",maxLength: 4,validate: "NotEmpty,ValidInteger"},
			{type: "input", label: "Naziv", name: "nazjur", value: "",maxLength: 60,width:200}
			]},
			{type: "block", blockOffset:0, list: [
			{type: "button", value: "Upamti", name:"upamti", position:"absolute",inputTop: 60,inputLeft:30},
			//{type: "newcolumn"},
			{type: "button", value: "Odustani", name:"odustani",position:"absolute",inputTop: 60,inputLeft:150}
			]}
	];	

var myLayout = new dhtmlXLayoutObject({parent: "layoutObj", pattern: "3L"});
myLayout.cells("a").setWidth(300);
myLayout.cells("b").setHeight(600);
myLayout.cells("a").setText("MENU");
myLayout.cells("b").setText("Detalji");


myTree = myLayout.cells("a").attachTree();
myTree.setImagePath("../codebase/imgs/dhxtree_skyblue/");
myTree.load("xml/tree.xml");
myTree.setOnClickHandler(tonclick);


function tonclick(id){
	myToolbar = myLayout.cells("b").attachToolbar();
	myToolbar.addButton("novi", 1, "Novi", "", "");
	myToolbar.addButton("izmeni", 2, "Izmeni", "", "");
	myToolbar.addButton("brisi", 3, "Briši", "", "");
	myToolbar.addButton("stampa", 4, "Štampa", "", "");
	myToolbar.setItemImage("novi","common/imgs/new.gif");
	myToolbar.setItemImage("brisi","common/imgs/close.gif");
	myToolbar.setItemImage("izmeni","common/imgs/undo.gif");
	myToolbar.setItemImage("stampa","common/imgs/print.gif");
	myToolbar.attachEvent("onClick", function(id) {
		if(id == "novi"){
			doWindow(id);
		}
		else if(id == "izmeni"){
			doWindowUpdate(id);
		}
		else if(id == "brisi"){
			brisislog();
		}
		else if(id == "stampa"){
			stampa();
		}
	});	
	myGrid = myLayout.cells("b").attachGrid();
	myGrid.setImagePath("codebase/imgs/");//path to images required by grid
	myGrid.setHeader("Preduz,Sifra,Naziv");//set column names
	myGrid.setInitWidths("60,60,150");//set column width in px
	myGrid.setColAlign(",centercenter,left");//set column values align
	myGrid.setColTypes("ro,ro,ro");//set column types
	myGrid.setColSorting("int,int,str");//set sorting
	myGrid.setSkin("dhx_skyblue");//set grid skin

	myGrid.setIconsPath('codebase/imgs/');
	myGrid.init();
	myGrid.enableSmartRendering(true,50);
	myGrid.attachEvent("onRowSelect",doOnRowSelected);
	//myGrid.load("./jsp/rj-getpodaci.jsp");	//myGrid.load("./jsp/data.jsp","json");
	myGrid.load("./jsp/rjobrada.jsp");	//myGrid.load("./jsp/data.jsp","json");
	
	
	
};
function doWindow(aa){
	dhxWins = new dhtmlXWindows();
	
	dhxWins.attachViewportTo("layoutObj");
	
	w1 = dhxWins.createWindow("w1", 150, 50, 450, 300);
	w1.setText(aa);
	myForm = w1.attachForm(formData, true);
	myForm.setValidation("pre","ValidInteger");	
	myForm.setValidation("jur","ValidInteger");	
	myForm.setItemFocus("pre");		

	//stavljanje klik dogadjaja na dugme u formi
	myForm.attachEvent("onButtonClick", function(name){
		//ako je dugme "upamti" (koji=1)
		predd = myForm.getItemValue("pre");
		jurr = myForm.getItemValue("jur");
		nazivv = myForm.getItemValue("nazjur");
	
		if(name=="upamti"){
			obradi(1);
		}

		//ako je dugme "odustani" skloni windows
		if(name=="odustani"){
			dhxWins.unload();
		}

	});
}

function doOnRowSelected(){
	var red = myGrid.getSelectedRowId();
	//uzimanje vrednosti iz grida
	predd = myGrid.cells(red,0).getValue();
	jurr = myGrid.cells(red,1).getValue();
	nazivv = myGrid.cells(red,2).getValue();
}

function doWindowUpdate(aa){
	dhxWins = new dhtmlXWindows();
	
	dhxWins.attachViewportTo("layoutObj");
	
	w1 = dhxWins.createWindow("w1", 150, 50, 450, 300);
	w1.setText(aa);
	myForm = w1.attachForm(formData, true);
	
	myForm.setItemValue("pre",predd); 
	myForm.setItemValue("jur",jurr); 
	myForm.setItemValue("nazjur",nazivv); 
	myForm.setItemFocus("nazjur");		

	//stavljanje klik dogadjaja na dugme u formi
	myForm.attachEvent("onButtonClick", function(name){
		//ako je dugme "upamti" (koji=1)
		predd = myForm.getItemValue("pre");
		jurr = myForm.getItemValue("jur");
		nazivv = myForm.getItemValue("nazjur");
	
		if(name=="upamti"){
			obradi(2);
		}
		//ako je dugme "odustani" skloni windows
		if(name=="odustani"){
			dhxWins.unload();
		}

	});
}

function brisislog(){
	dhtmlx.modalbox({
		title: "Brisanje",
		text: "Da li zaista brišete ove podatke",
		buttons:["Ok","Odustani"],
		callback:function(r){
			if(r==0){
				obradi(3);
			}
		}
	});
}
function obradi(koji) {
	var mess1="",mess2="";
	switch(koji){
	case 1:
		mess1="Upis nije uspeo";
		mess2="Popunite osnovne podatke";
		break;
	case 2:
		mess1="Izmena nije uspela";
		mess2="Popunite osnovne podatke";
		break;
	case 3:
		mess1="Brisanje nije uspelo";
		mess2="Nisu selektovani podaci";
		break;
	}
	if(proveripolja()){
		//uzimanje vrednosti iz forme podaci se salju kao "id="+idd a sledeci
		//podatak se nastavlja sa znakom &

		//pozivanje ajax funkcije da odradi nesto sa podacima u fajlu rjobrada.jsp (Rjobrada.java)
		dhx4.ajax.post("./jsp/rjobrada.jsp",
			"pre="+predd+"&jur="+jurr+"&naziv="+nazivv+"&koji="+koji, 
			function(r){
		
			//(r) je xml fajl sto vraca php kao odgovor
			var xml = r.xmlDoc.responseXML;
			
			if(xml!=null){
				//uzimanje prvog reda root taga u xml fajlu
				var root = xml.getElementsByTagName("root")[0];
			
				//uzimanje elementa status iz root taga
				var status = root.getAttribute("status");
				myGrid.clearAll();
				myGrid.load("./jsp/rjobrada.jsp");
				if (status=="0"){
					poruka(mess1);
				}else{
					//brisanje sadrzaja polja i stavljanje fokusa na prvo polje
					//nuliranje promenljivih iz polja
					poruka("Podaci su obradjeni");
					predd = null;
					jurr = null;
					nazivv = null;
					if(koji<3){
						myForm.clear();
					}
				}
			}else{
				poruka("greska:ajax");
			}
		});
	}else{
		poruka(mess2);
	}
}
function stampa() {
	//dhx.ajax().get("rjizvestaj");
	//header("Content-Type: text/pdf");
	//header.location("rjizvestaj");
	window.open("http://localhost:8080/jsp/rjizvestaj"); 
	//dhx.ajax.get("rjizvestaj?keep_alive=1");
	//var pdf = "pdf/sifrj.pdf";
	//window.open(pdf);	
}

function poruka(tekstporuke) {
	dhtmlx.alert({
		title:"Upozorenje",
		type:"alert-error",
		text:tekstporuke
	});
}

function proveripolja() {
	var aa = true;
	if(predd==null || predd.toString().length==0){
		aa = false;
		return false;
	}
	if(jurr==null || jurr.toString().length==0){
		aa = false;
		return false;
	}
	if(nazivv == null){
		nazivv = "";
	}

	return aa;
}

