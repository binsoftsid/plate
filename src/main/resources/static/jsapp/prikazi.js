/**
 * fajl sa prikazom sifarnika
 */
var koji=0;

//olaksice-----------------------------------------------------------------------------------------
function doWindowOlaksice(xx){
	koji = xx;
	dhxWins = new dhtmlXWindows();
	dhxWins.attachViewportTo("layoutObj");
	w1 = dhxWins.createWindow("w1", 150, 50, 600, 500);
	
	w1.setText("Šifarnik olakšica");
	var myLayoutOlaksice = new dhtmlXLayoutObject({parent:w1, pattern: "1C"});
	myLayoutOlaksice.cells("a").hideHeader();
	
	myToolbarOlaksice = myLayoutOlaksice.cells("a").attachToolbar();
	
	myGridOlaksice = myLayoutOlaksice.cells("a").attachGrid();
	myGridOlaksice.setImagePath("../resources/codebase/imgs/");
	myGridOlaksice.setHeader("Rbr,Naziv,Opis,PIO-R,ZDR-R,NEZ-R,PIO-P,ZDR-P,NEZ-P,Porez,Ben.r.s.");
	myGridOlaksice.setInitWidths("60,200,200,60,60,60,60,60,60,60,60");
	myGridOlaksice.setColAlign("left,left,left,left,left,left,left,left,left,left,left");
	myGridOlaksice.setColTypes("ro,ro,ro,ro,ro,ro,ro,ro,ro,ro,ro");
	myGridOlaksice.setColSorting("int,str,str,str,str,str,str,str,str,str,str");
	myGridOlaksice.setStyle("background-color:#6175ea;color:white; font-weight:bold;", "","", "");
	myGridOlaksice.init();
	myGridOlaksice.enableAlterCss("even","uneven");
	myGridOlaksice.attachEvent("onRowSelect",doOnRowSelectedOlaksice);
	myGridOlaksice.load("/olaksice/prikaz","json");
}
function doOnRowSelectedOlaksice(){
	var red = myGridOlaksice.getSelectedRowId();
	var sifra = myGridOlaksice.cells(red,0).getValue();
	if (koji == 1){
		myForm.setItemValue("br",sifra); 
		myForm.setItemFocus("datumod");
	}
	dhxWins.unload();
	dhxWins = w1 = null;
}
//--------------------------------------------------------------------------------------------------