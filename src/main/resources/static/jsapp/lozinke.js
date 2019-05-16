var pre="",firma="1",godina="0",mesec,stara="",nova="",ponovinovu="";

ucitajFirmu();


formDataRekap = [
	{type: "settings", position: "label-left", labelWidth: 200, inputWidth: 200,labelHeight:20,inputHeight:22},
	{type: "block", offsetTop: 5, list: [
		{type: "password", label: "Nova lozinka: ",name:"nova", value:"",maxLength: 30,validate:"NotEmpty",
								position:"absolute", labelTop:30,inputTop:30,inputLeft:200},
		{type: "password", label: "Ponovi novu lozinku: ",name:"ponovinovu", value:"",maxLength: 30,validate:"NotEmpty",
								position:"absolute", labelTop:55,inputTop:55,inputLeft:200},
		{type: "button", value: "Izmeni", name:"posalji", position:"absolute",inputTop: 200,inputLeft:30}
		]},			
	];	
var myLayout1 = new dhtmlXLayoutObject({parent: myLayout.cells("b"), pattern: "1C"});
myLayout1.cells("a").setHeight(600);
myLayout1.cells("a").setText("IZMENA LOZINKE");
myLayout1.cells("a").cell.style.backgroundColor = "#cbc9af";

myFormLoz = myLayout1.cells("a").attachForm(formDataRekap);	

myFormLoz.attachEvent("onButtonClick", function(name){
	if(name=="posalji"){
			IzmeniLozinku();
	}
});

myFormLoz.setItemFocus("stara");

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
//-----------------------------------------------------------------------------
function IzmeniLozinku(){
		var pre,strurl="/lozinke/izmeni";

		pre = firma;
		nova = myFormLoz.getItemValue("nova");
		ponovinovu = myFormLoz.getItemValue("ponovinovu");
		
		nova = nova.trim();
		ponovinovu = ponovinovu.trim();
		
		var dataajax = "?pre=" + pre;
		dataajax = dataajax + "&nova=" + nova;
		dataajax = dataajax + "&ponovinovu=" + ponovinovu;
		
		if(proveripolja(nova,ponovinovu)){
			if(nova === ponovinovu){
					dhx4.ajax.post(strurl+dataajax,function(r){
						var t = dhx4.s2j(r.xmlDoc.responseText);
						var xml = t.toString();
			
						if(xml!=null){
							if (xml=="0"){
								poruka("Lozinka nije izmenjena");
							}else{
								poruka("Lozinka je izmenjena");
								myFormLoz.clear();
							}
						}else{
							poruka("greska:ajax");
						}
					});
			}else{
				poruka("Nova lozinka nije pravilno unešena");
			}
		}else{
				poruka("Sva polja nisu uneta");
		}
}

//-----------------------------------------------------------------------------
function proveripolja(str1,str2) {
	var aa = true;

	if(str1 == null || str1.toString().length==0){
		return false;
	}
	if(str2 == null || str2.toString().length==0){
		return false;
	}
	return aa;
}
//-----------------------------------------------------------------------------
function poruka(tekstporuke) {
	dhtmlx.alert({
		title:"Upozorenje",
		type:"alert-error",
		text:tekstporuke
	});
}


