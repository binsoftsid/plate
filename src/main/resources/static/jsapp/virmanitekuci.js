var pre="",firma="1",godina="0",mesec,odsifre,dosifre,brojobrade,bop,datumuplate,sifrauplate;
var vrstaplate;
ucitajFirmu();

formDataRekap = [
	{type: "settings", position: "label-left", labelWidth: 200, inputWidth: 60,labelHeight:20,inputHeight:22},
	{type: "block", offsetTop: 5, list: [
		{type: "select", label: "Vrsta plate",name:"vrstaplate",  options:[
			{text: "Redovna", value: "1"},
            {text: "Bol.preko 30 dana", value: "2"},
            {text: "Porodilje", value: "3"},
            {text: "Penzioneri", value: "4"}
        ],position:"absolute",labelTop:8,inputTop: 5,inputLeft:200,inputWidth:200,inputHeight:25},
		
		{type: "input", label: "Mesec: ",name:"odmeseca", value:"",maxLength: 2,validate:"NotEmpty,ValidInteger",
								position:"absolute", labelTop:30,inputTop:30,inputLeft:200,inputWidth:200},
		{type: "input", label: "Broj obrade: ",name:"brojobrade", value:"",maxLength: 2,validate:"NotEmpty,ValidInteger",
								position:"absolute", labelTop:55,inputTop:55,inputLeft:200,inputWidth:200},
		{type: "input", label: "BOP: ",name:"bop", value:"",maxLength: 20,validate:"NotEmpty,ValidInteger",
								position:"absolute", labelTop:80,inputTop:80,inputLeft:200,inputWidth:200},
		{type: "input", label: "Šifra uplate: ",name:"sifrauplate", value:"",maxLength: 5,validate:"NotEmpty,ValidInteger",
								position:"absolute", labelTop:105,inputTop:105,inputLeft:200,inputWidth:200}
		]},	
        {type: "block", blockOffset:0,offsetTop:130, list: [
		{type: "button", value: "Štampa", name:"formiraj", position:"absolute",inputTop: 60,inputLeft:30}
		]}
		];	

var myLayout1 = new dhtmlXLayoutObject({parent: myLayout.cells("b"), pattern: "1C"});
myLayout1.cells("a").setHeight(600);
myLayout1.cells("a").setText("VIRMANI TEKUĆI-RAČUNI");
myLayout1.cells("a").cell.style.backgroundColor = "#cbc9af";

myFormRekap = myLayout1.cells("a").attachForm(formDataRekap);	

myFormRekap.attachEvent("onButtonClick", function(name){
	if(name=="formiraj"){
			Formiraj();
	}
});

myFormRekap.setItemFocus("vrstaplate");

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
	
		pre = firma;
		vrstaplate = myFormRekap.getItemValue("vrstaplate");
		mesec = myFormRekap.getItemValue("odmeseca");
		brojobrade = myFormRekap.getItemValue("brojobrade");
		bop = myFormRekap.getItemValue("bop");
		sifrauplate = myFormRekap.getItemValue("sifrauplate");
		
	if (proveripolja()){		
		var dataajax = "?pre=" + firma.toString();
		dataajax = dataajax + "&godina=" + godina.toString();
		dataajax = dataajax + "&vrstaplate=" + vrstaplate.toString();
		dataajax = dataajax + "&brojobrade=" + brojobrade.toString();
		dataajax = dataajax + "&mesec=" + mesec.toString();
		dataajax = dataajax + "&bop=" + bop.toString();
		dataajax = dataajax + "&sifrauplate=" + sifrauplate.toString();

		window.open("/print/virmantekuci"+dataajax); 
		
	}else{
		poruka("Polja parametara nisu popunjena");
	}
}
//-----------------------------------------------------------------------------
function proveripolja() {
	var aa = true;

	if(mesec == null || mesec.toString().length==0){
		return false;
	}
	if(brojobrade == null || brojobrade.toString().length==0){
		return false;
	}
	if(bop == null || bop.toString().length==0){
		return false;
	}
	if(sifrauplate == null || sifrauplate.toString().length==0){
		return false;
	}
	return aa;
}