var pre="",firma="1",godina="0",mesec,kome="",odkoga="",tekstporuke="";
ucitajFirmu();

formDataRekap = [
	{type: "settings", position: "label-left", labelWidth: 200, inputWidth: 200,labelHeight:20,inputHeight:22},
	{type: "block", offsetTop: 5, list: [
		{type: "input", label: "Kome: ",name:"kome", value:"",maxLength: 30,validate:"NotEmpty",
								position:"absolute", labelTop:8,inputTop:5,inputLeft:200},
		{type: "input", label: "Od koga: ",name:"odkoga", value:"",maxLength: 30,validate:"NotEmpty",
								position:"absolute", labelTop:30,inputTop:30,inputLeft:200},
		{type: "input", label: "Tekst poruke: ",name:"tekstporuke", value:"",validate:"NotEmpty",rows:4,
								note:{text:"................."},
								position:"absolute", labelTop:55,inputTop:55,inputLeft:200,inputHeight:100,inputWidth:350},
		
		{type: "button", value: "Pošalji", name:"posalji", position:"absolute",inputTop: 200,inputLeft:30}
		]},			
	];	
var myLayout1 = new dhtmlXLayoutObject({parent: myLayout.cells("b"), pattern: "1C"});
myLayout1.cells("a").setHeight(600);
myLayout1.cells("a").setText("SLANJE E-MAIL PORUKE");
myLayout1.cells("a").cell.style.backgroundColor = "#cbc9af";

myFormRekap = myLayout1.cells("a").attachForm(formDataRekap);	

myFormRekap.attachEvent("onButtonClick", function(name){
	if(name=="posalji"){
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
		var pre,strurl="/mail/slanjeporuke";

		pre = firma;
		kome = myFormRekap.getItemValue("kome");
		odkoga = myFormRekap.getItemValue("odkoga");
		tekstporuke = myFormRekap.getItemValue("tekstporuke");
		
		var dataajax = "?pre=" + pre;
		dataajax = dataajax + "&godina=" + godina;
		dataajax = dataajax + "&kome=" + kome;
		dataajax = dataajax + "&odkoga=" + odkoga;
		dataajax = dataajax + "&tekstporuke=" + tekstporuke;		
		
		if(proveripolja(kome,odkoga)){
			
			dhx4.ajax.post(strurl+dataajax,
					function(r){
				var t = dhx4.s2j(r.xmlDoc.responseText);
				var xml = t.toString();
		
				if(xml!=null){
					if (xml=="1"){
						poruka("Poruka je poslata");
					}else{
						poruka("Poruka nije poslata");
					}
				}else{
					poruka("greska:ajax");
				}
			});
		}else{
			poruka(mess2);
		}
}
//-----------------------------------------------------------------------------
function proveripolja() {
	var aa = true;

	if(kome == null || kome.toString().length==0){
		return false;
	}
	if(odkoga == null || odkoga.toString().length==0){
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
//-----------------------------------------------------------------------------