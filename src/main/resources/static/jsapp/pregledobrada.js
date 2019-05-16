var pre="",firma="1",godina="0",myGrid;
//polje nadimak sluzi da se vidi da li je radnik vlasnik firme (D/N) jer ako jeste onda
//se on stavlja na sifru 1
ucitajFirmu();

var myLayout1 = new dhtmlXLayoutObject({parent: myLayout.cells("b"), pattern: "1C"});
myLayout1.cells("a").setHeight(600);
myLayout1.cells("a").setText("PODACI");
myLayout1.cells("a").cell.style.backgroundColor = "#cbc9af";

myGrid = myLayout1.cells("a").attachGrid();
myGrid.setImagePath("../resources/codebase/imgs/");//path to images required by grid
myGrid.setHeader("Mesec,Broj obrade,Vrsta plate");//set column names
myGrid.setInitWidths("100,200,200");//set column width in px
myGrid.setColAlign("left,left,left");//set column values align
myGrid.setColTypes("ro,ro,ro");//set column types
myGrid.setColSorting("int,str,str");//set sorting
myGrid.setIconsPath('../resources/codebase/imgs/');
myGrid.setStyle("background-color:#6175ea;color:white; font-weight:bold;", "","", "");
myGrid.enableRowsHover(true, "myhover");

myGrid.init();
myGrid.enableAlterCss("even","uneven");


function ucitajFirmu(){
	var ffirma="0";
	$.get("/firma/ucitajfirmu",{}, function(data) {
		var obj = $.parseJSON(JSON.stringify(data));	
		$.each(obj, function() {
			firma = this['firma'];
			godina = this['godina'];
			prikaziGrid();
			
		});		
	});
}
function prikaziGrid(){
	myGrid.load("/pregledobrada/prikaz?pre="+firma.toString()+"&godina="+godina.toString(),"json");
}
