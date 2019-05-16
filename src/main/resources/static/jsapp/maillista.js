var pre="",firma="1",godina="0",mesec,odsifre,dosifre,brojobrade;
var vrstaplate,dataajax,dataajax1,formirano=0;
var loader = document.getElementById("load");

ucitajFirmu();

formDataRekap = [
	{type: "settings", position: "label-left", labelWidth: 200, inputWidth: 60,labelHeight:20,inputHeight:22},
	{type: "block", offsetTop: 5, list: [
		{type: "input", label: "Mesec: ",name:"odmeseca", value:"",maxLength: 2,validate:"NotEmpty,ValidInteger",
								position:"absolute", labelTop:8,inputTop:5,inputLeft:200},
		{type: "input", label: "Broj obrade: ",name:"brojobrade", value:"",maxLength: 2,validate:"NotEmpty,ValidInteger",
								position:"absolute", labelTop:30,inputTop:30,inputLeft:200},
		{type: "input", label: "Od šifre: ",name:"odsifre", value:"",maxLength: 5,validate:"NotEmpty,ValidInteger",
								position:"absolute", labelTop:55,inputTop:55,inputLeft:200},
		{type: "input", label: "Do šifre: ",name:"dosifre", value:"",maxLength: 5,validate:"NotEmpty,ValidInteger",
								position:"absolute", labelTop:80,inputTop:80,inputLeft:200},
								
		{type: "select", label: "Vrsta plate",name:"vrstaplate",  options:[
			{text: "Redovna", value: "1"},
            {text: "Bol.preko 30 dana", value: "2"},
            {text: "Porodilje", value: "3"},
            {text: "Penzioneri", value: "4"}
        ],position:"absolute",labelTop:105,inputTop: 105,inputLeft:200,inputWidth:200,inputHeight:25},
        {type: "container",name:"load",label:"",position:"absolute",inputWidth:330,inputHeight:200,
        						inputTop:110}
		
		]},			
        {type: "block", blockOffset:0,offsetTop:200, list: [
		{type: "button", value: "Formiraj", name:"formiraj", position:"absolute",inputTop: 60,inputLeft:30},
		{type: "button", value: "Pošalji", name:"posalji", position:"absolute",inputTop: 60,inputLeft:220}
		]}
		];	
//1C
var myLayout1 = new dhtmlXLayoutObject({parent: myLayout.cells("b"), pattern: "2E"});
myLayout1.cells("a").setHeight(500);
myLayout1.cells("a").setText("SLANJE ISPLATNIH LISTA RADNICIMA - E-mail  (URADITE PRVO FORMIRANJE)");
myLayout1.cells("a").cell.style.backgroundColor = "#cbc9af";
myLayout1.cells("b").setText("PORUKA:");
myFormRekap = myLayout1.cells("a").attachForm(formDataRekap);	

myFormRekap.attachEvent("onButtonClick", function(name){
	if(name=="formiraj"){
			Formiraj();
	}
	else if(name=="posalji"){
		PosaljiMail();
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
		pre = firma;
		mesec = myFormRekap.getItemValue("odmeseca");
		brojobrade = myFormRekap.getItemValue("brojobrade");
		odsifre = myFormRekap.getItemValue("odsifre");
		dosifre = myFormRekap.getItemValue("dosifre");
		vrstaplate = myFormRekap.getItemValue("vrstaplate");
		
		myLayout1.cells("b").attachObject(loader);	
		
	if (proveripolja()){
		var i=0;
		var intodsifre = parseInt(odsifre);
		var intdosifre = parseInt(dosifre);
		dataajax = "?pre=" + pre;
		dataajax = dataajax + "&godina=" + godina;
		dataajax = dataajax + "&mesec=" + mesec;
		dataajax = dataajax + "&brojobrade=" + brojobrade;
		dataajax = dataajax + "&vrstaplate=" + vrstaplate;				
		dataajax = dataajax + "&odsifre=" + odsifre;				
		dataajax = dataajax + "&dosifre=" + dosifre;				
		
		$("#load").css("display", "block");
			dhx4.ajax.post("/print/listicfile"+dataajax,
					function(r){
				var t = dhx4.s2j(r.xmlDoc.responseText);
				var xml = t.toString();
				
				$('#loading-image').hide();
				if(xml!=null){
					if (xml=="1"){
						formirano=1;
						poruka("Listići su formirani");
						myLayout1.cells("b").detachObject(loader);	
						
					}else{
						poruka("Listići nisu formirani");
						myLayout1.cells("b").detachObject(loader);
					}
				}else{
					poruka("greska:ajax");
				}
			});
	}else{
		poruka("Polja nisu popunjena");
	}
}
function PosaljiMail(){
	if(formirano == 1){
		myLayout1.cells("b").attachObject(loader);	

		$("#load").css("display", "block");
		dhx4.ajax.post("/mail/slanjeporukeattachment"+dataajax,
				function(r){
			var t = dhx4.s2j(r.xmlDoc.responseText);
			var xml = t.toString();
			
			$('#loading-image').hide();
			if(xml!=null){
				if (xml=="1"){
					poruka("Listići su poslati");
					myLayout1.cells("b").detachObject(loader);	
					
				}else{
					poruka("Listići nisu poslati");
					myLayout1.cells("b").detachObject(loader);
				}
			}else{
				poruka("greska:ajax");
			}
		});
		
	
	}else{
		poruka("Listići nisu formirani");
		myFormRekap.setItemFocus("formiraj");
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
	if(odsifre == null || odsifre.toString().length==0){
		return false;
	}
	if(dosifre == null || dosifre.toString().length==0){
		return false;
	}
	return aa;
}
//-----------------------------------------------------------------------------