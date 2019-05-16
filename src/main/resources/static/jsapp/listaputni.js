var pre="",firma="1",godina="0",mesec,odsifre,dosifre,brojobrade;
//polje nadimak sluzi da se vidi da li je radnik vlasnik firme (D/N) jer ako jeste onda
//se on stavlja na sifru 1
ucitajFirmu();

formDataRekap = [
	{type: "settings", position: "label-left", labelWidth: 200, inputWidth: 60,labelHeight:20,inputHeight:22},
	{type: "block", offsetTop: 5, list: [
		{type: "input", label: "Mesec: ",name:"odmeseca", value:"",maxLength: 2,validate:"NotEmpty,ValidInteger",
								position:"absolute", labelTop:8,inputTop:5,inputLeft:150},
		{type: "input", label: "Broj isplate: ",name:"brojobrade", value:"",maxLength: 2,validate:"NotEmpty,ValidInteger",
								position:"absolute", labelTop:30,inputTop:30,inputLeft:150},
		]},			
        {type: "block", blockOffset:0,offsetTop:100, list: [
		{type: "button", value: "Štampaj", name:"formiraj", position:"absolute",inputTop: 60,inputLeft:30}
		]}
		];	

var myLayout1 = new dhtmlXLayoutObject({parent: myLayout.cells("b"), pattern: "1C"});
myLayout1.cells("a").setHeight(600);
myLayout1.cells("a").setText("SPISAK ZA PUTNE TROŠKOVE ");
myLayout1.cells("a").cell.style.backgroundColor = "#cbc9af";

myFormRekap = myLayout1.cells("a").attachForm(formDataRekap);	

myFormRekap.attachEvent("onButtonClick", function(name){
	if(name=="formiraj"){
			Formiraj();
	}
	else if(name=="izlaz"){
		dhxWins.unload();
		dhxWins = w2 = null;
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
function Formiraj(){
		var pre,vrstaplate;

		pre = firma;
		mesec = myFormRekap.getItemValue("odmeseca");
		brojobrade = myFormRekap.getItemValue("brojobrade");
		
	if (proveripolja()){		
		var dataajax = "?pre=" + firma.toString();
		dataajax = dataajax + "&godina=" + godina.toString();
		dataajax = dataajax + "&brojobrade=" + brojobrade.toString();
		dataajax = dataajax + "&mesec=" + mesec.toString();
	
		window.open("/print/listaputni"+dataajax); 
		
	}else{
		poruka("Polja parametara nisu popunjena");
	}
}
//-----------------------------------------------------------------------------
function proveripolja() {
	var aa = true;

	if(mesec == null || mesec.toString().length==0){
		poruka("mesec");
		return false;
	}
	if(brojobrade == null || brojobrade.toString().length==0){
		return false;
	}
	return aa;
}
//-----------------------------------------------------------------------------