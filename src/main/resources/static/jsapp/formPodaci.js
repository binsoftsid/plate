var myForm,myFormListic,myFormZaglavlje;
//forma za unos podataka sati i iznosa za radnika
formDataZaglavlje = [
		{type: "settings", position: "label-left", labelWidth: 100, inputWidth: 60,labelHeight:20,inputHeight:22},
		{type: "block", offsetTop: 5, list: [
			{type: "button", value: "Novi unos", name:"noviunos", 
				position:"absolute",inputTop: 2,inputLeft:0},
			{type: "select", label: "Vrsta plate",name:"kombobox",  options:[
		            {text: "Redovna", value: "1"},
		            {text: "Bol.preko 30 dana", value: "2"},
		            {text: "Porodilje", value: "3"},
		            {text: "Penzioneri", value: "4"}
		        ],position:"absolute",labelTop:10,labelLeft:110,labelWidth:70,inputTop: 10,inputLeft:180,inputWidth:120,inputHeight:25},
		        
			{type: "input", label: "Mesec:",name:"mesec", value:"",maxLength:2,validate:"NotEmpty,ValidInteger",
						position:"absolute",labelLeft:320,labelWidth:60,inputWidth:30,labelTop:10,inputTop:10,inputLeft:380},
			{type: "input", label: "Broj obrade:",name:"brojisplate", value:"",maxLength:2,validate:"NotEmpty,ValidInteger",
					position:"absolute",labelLeft:420,labelWidth:80,inputWidth:30,labelTop:10,inputTop:10,inputLeft:500},
			{type: "button", value: "Unesi", name:"unesizaglavlje", 
							position:"absolute",inputTop: 2,inputLeft:550},	        
			{type: "input", label: "Vrednost boda:",name:"vrednostboda", value:"",maxLength:12,validate:"NotEmpty,ValidCurrency",
						position:"absolute",labelTop:45,labelLeft:5,labelWidth:90,inputWidth:80,inputTop:45,inputLeft:100},
			{type: "input", label: "Fond sati obr.:",name:"fondsatiobracun", value:"",maxLength:4,validate:"NotEmpty,ValidInteger",
								position:"absolute",labelTop:45,labelLeft:200,labelWidth:90,inputWidth:40,inputTop:45,inputLeft:285},
			{type: "input", label: "Sati u mes.:",name:"fondsatimesec", value:"",maxLength:4,validate:"NotEmpty,ValidInteger",
							position:"absolute",labelTop:45,labelLeft:330,labelWidth:90,inputWidth:40,inputTop:45,inputLeft:420},
			{type: "input", label: "Cena toplog obr:",name:"topliobrok", value:"",maxLength:10,validate:"NotEmpty,ValidCurrency",
									position:"absolute",labelTop:45,labelLeft:470,labelWidth:130,inputWidth:70,inputTop:45,inputLeft:590},
			{type: "input", label: "Neopor.iznos:",name:"neoporeziviiznos", value:"",maxLength:12,validate:"NotEmpty,ValidCurrency",
								position:"absolute",labelTop:80,labelLeft:5,labelWidth:90,inputWidth:70,inputTop:80,inputLeft:100},
			{type: "input", label: "Procenat plate %:",name:"procenatplate", value:"",maxLength:4,validate:"NotEmpty,ValidInteger",
										position:"absolute",labelTop:80,labelLeft:180,labelWidth:100,inputWidth:40,inputTop:80,inputLeft:285},
			{type:"checkbox", label:"Krediti:",name:"krediti",checked:true,
											position:"absolute",inputTop:80,inputLeft:440,labelTop:80,labelLeft:390},
			{type:"checkbox", label:"Min.osnovica:",name:"minosnovica",checked:true,
				position:"absolute",labelTop:80,labelLeft:500,inputTop:80,inputLeft:610,labelAlign:"right"}
			]}];	

formData = [
	{type: "settings", position: "label-left", labelWidth: 100, inputWidth: 60,labelHeight:20,inputHeight:22},
	{type: "block", offsetTop: 5, list: [
		{type: "input", label: "Šifra radnika: ",name:"sifra", value:"",maxLength: 12,validate:"NotEmpty,ValidInteger",
								position:"absolute", labelTop:8,inputTop:5,inputLeft:110},
		{type: "button", value: "***", name:"radnici", position:"absolute",inputTop:0,inputLeft:178,inputWidth:40,inputHeight:7},
		
		{type: "input", label: "Po učinku:",name:"cas1", value:"",maxLength: 4,validate:"NotEmpty,ValidInteger",
								position:"absolute",labelTop:30,inputTop:30,inputLeft:110},
		{type: "input", name:"bod1",value: "",maxLength: 10,validate:"NotEmpty,ValidCurrency", 
								position:"absolute",inputTop:30,inputLeft:180},
		{type: "input", label: "Po vremenu:",name:"cas2", value:"",maxLength: 4,validate:"NotEmpty,ValidInteger",
									position:"absolute",labelTop:55,inputTop:55,inputLeft:110},
		{type: "input", label: "Produženi rad:",name:"cas3", value:"",maxLength: 4,validate:"NotEmpty,ValidInteger",
								position:"absolute",labelTop:80,inputTop:80,inputLeft:110},
		{type: "input", label: "Noćni rad:",name:"cas4", value:"",maxLength: 4,validate:"NotEmpty,ValidInteger",
								position:"absolute",labelTop:105,inputTop:105,inputLeft:110},
		{type: "input", label: "Godišnji odmor:",name:"cas5", value:"",maxLength: 4,validate:"NotEmpty,ValidInteger",
								position:"absolute",labelTop:130,inputTop:130,inputLeft:110},
		{type: "input", label: "Plaćeno odsustvo:",name:"cas6", value:"",maxLength: 4,validate:"NotEmpty,ValidInteger",
								position:"absolute",labelTop:155,inputTop:155,inputLeft:110},
		{type: "input", label: "Praznici:",name:"cas7", value:"",maxLength: 4,validate:"NotEmpty,ValidInteger",
								position:"absolute",labelTop:180,inputTop:180,inputLeft:110},
		{type: "input", label: "Rad na prazn.:",name:"cas8", value:"",maxLength: 4,validate:"NotEmpty,ValidInteger",
								position:"absolute",labelTop:205,inputTop:205,inputLeft:110},
		{type: "input", label: "Naknade od dr.:",name:"cas9", value:"",maxLength: 4,validate:"NotEmpty,ValidInteger",
								position:"absolute",labelTop:230,inputTop:230,inputLeft:110},
		{type: "input", label: "Noćni rad drž.pr.:",name:"cas10", value:"",maxLength: 4,validate:"NotEmpty,ValidInteger",
								position:"absolute",labelTop:255,inputTop:255,inputLeft:110},
		{type: "input", label: "Razlika + - :",name:"bod11", value:"",maxLength: 4,validate:"NotEmpty,ValidInteger",
								position:"absolute",labelTop:280,inputTop:280,inputLeft:180},
		{type: "input", label: "Iznos 65%:",name:"cas12", value:"",maxLength: 4,validate:"NotEmpty,ValidInteger",
								position:"absolute",labelTop:305,inputTop:305,inputLeft:110},
				{type: "input", name:"bod12",value: "",maxLength: 10,validate:"NotEmpty,ValidCurrency", 
								position:"absolute",inputTop:305,inputLeft:180}
		]},
		{type: "block", labelWidth: 150, inputWidth: 60, offsetTop: 5,offsetLeft: 300, list: [
			{type: "input", label: "Bol.do 30 dana: ", name:"cas13", value:"",maxLength:4,validate:"NotEmpty,ValidInteger",
								position:"absolute",labelTop:3,inputTop:1,inputLeft:140},	
					{type: "input", name:"bod13",value: "",maxLength: 10,validate:"NotEmpty,ValidCurrency", 
								position:"absolute",inputTop:1,inputLeft:210},								
			{type: "input", label: "Bolovanje 100%: ", name:"cas14", value:"",maxLength:4,validate:"NotEmpty,ValidInteger",
									position:"absolute",labelTop:28,inputTop:26,inputLeft:140},	
					{type: "input", name:"bod14",value: "",maxLength:10,validate:"NotEmpty,ValidCurrency", 
								position:"absolute",inputTop:26,inputLeft:210},								
			{type: "input", label: "Bol.preko 30 dana:", name:"cas15", value:"",maxLength:4,validate:"NotEmpty,ValidInteger",
								position:"absolute",labelTop:53,inputTop:51,inputLeft:140,labelWidth:150},	
			{type: "input", label: "Stimulacija:", name:"cas16", value:"",maxLength:4,validate:"NotEmpty,ValidInteger",
								position:"absolute",labelTop:78,inputTop:76,inputLeft:140},	
			{type: "input", label: "Topli obrok:", name:"cas18", value:"",maxLength:4,validate:"NotEmpty,ValidInteger",
								position:"absolute",labelTop:103,inputTop:101,inputLeft:140},	
			{type: "input", label: "Terenski dodatak: ", name:"cas19", value:"",maxLength:4,validate:"NotEmpty,ValidInteger",
								position:"absolute",labelTop:128,inputTop:126,inputLeft:140},	
					{type: "input", name:"bod19",value: "",maxLength:10,validate:"NotEmpty,ValidCurrency", 
								position:"absolute",inputTop:126,inputLeft:210},								
			{type: "input", label: "Regres: ", name:"bod20", value:"",maxLength:4,validate:"NotEmpty,ValidInteger",
								position:"absolute",labelTop:153,inputTop:151,inputLeft:210},	
			{type: "input", label: "Ostala primanja: ", name:"cas21", value:"",maxLength:4,validate:"NotEmpty,ValidInteger",
									position:"absolute",labelTop:178,inputTop:176,inputLeft:140},	
						{type: "input", name:"bod21",value: "",maxLength:10,validate:"NotEmpty,ValidCurrency", 
								position:"absolute",inputTop:176,inputLeft:210},								
			{type: "input", label: "Kalen.br. dana:", name:"cas22", value:"",maxLength:4,validate:"NotEmpty,ValidInteger",
								position:"absolute",labelTop:203,inputTop:201,inputLeft:140},	
			{type: "input", label: "% angažovanja:", name:"cas23", value:"",maxLength:4,validate:"NotEmpty,ValidInteger",
									position:"absolute",labelTop:228,inputTop:226,inputLeft:140}	
		]},			
		{type: "block", blockOffset:0,offsetTop:270, list: [
		{type: "button", value: "Novi radnik", name:"novi", position:"absolute",inputTop: 60,inputLeft:30},
		{type: "button", value: "Unesi", name:"unesi",position:"absolute",inputTop: 60,inputLeft:150},
		{type: "button", value: "Izmeni", name:"izmeni",position:"absolute",inputTop: 60,inputLeft:270},
		{type: "button", value: "Briši", name:"brisi",position:"absolute",inputTop: 60,inputLeft:390}
		]}
		];	


formDataListic = [
	{type: "settings", position: "label-left", labelWidth: 100, inputWidth: 40,labelHeight:20,inputHeight:20},
	{type: "block", offsetTop: 5, list: [
		{type: "input", label: "Minuli rad: ",name:"minulirad", value:"",maxLength: 4,readonly : "true",
								position:"absolute", labelTop:5,inputTop:5,inputLeft:110},
				{type: "input", name:"bodd17",value: "",maxLength: 10,readonly : "true",numberFormat:"0,000.00",
									position:"absolute",inputTop:5,inputLeft:150,inputWidth:70},
		{type: "input", label: "Po učinku:",name:"cass1", value:"",maxLength: 4,readonly : "true",
								position:"absolute",labelTop:30,inputTop:30,inputLeft:110},
				{type: "input", name:"bodd1",value: "",maxLength: 10,readonly : "true",numberFormat:"0,000.00",
								position:"absolute",inputTop:30,inputLeft:150,inputWidth:70},
		{type: "input", label: "Po vremenu:",name:"cass2", value:"",maxLength: 4,readonly : "true",
									position:"absolute",labelTop:55,inputTop:55,inputLeft:110},
				{type: "input", name:"bodd2",value: "",maxLength: 10,readonly : "true",numberFormat:"0,000.00",
									position:"absolute",inputTop:55,inputLeft:150,inputWidth:70},
		{type: "input", label: "Produženi rad:",name:"cass3", value:"",maxLength: 4,readonly : "true",
								position:"absolute",labelTop:80,inputTop:80,inputLeft:110},
				{type: "input", name:"bodd3",value: "",maxLength: 10,readonly : "true",numberFormat:"0,000.00",
									position:"absolute",inputTop:80,inputLeft:150,inputWidth:70},
		{type: "input", label: "Noćni rad:",name:"cass4", value:"",maxLength: 4,readonly : "true",
								position:"absolute",labelTop:105,inputTop:105,inputLeft:110},
				{type: "input", name:"bodd4",value: "",maxLength: 10,readonly : "true",numberFormat:"0,000.00", 
									position:"absolute",inputTop:105,inputLeft:150,inputWidth:70},
		{type: "input", label: "Godišnji odmor:",name:"cass5", value:"",maxLength: 4,readonly : "true",
								position:"absolute",labelTop:130,inputTop:130,inputLeft:110},
				{type: "input", name:"bodd5",value: "",maxLength: 10,readonly : "true",numberFormat:"0,000.00", 
									position:"absolute",inputTop:130,inputLeft:150,inputWidth:70},
		{type: "input", label: "Plaćeno odsustvo:",name:"cass6", value:"",maxLength: 4,readonly : "true",
								position:"absolute",labelTop:155,inputTop:155,inputLeft:110},
				{type: "input", name:"bodd6",value: "",maxLength: 10,readonly : "true",numberFormat:"0,000.00", 
									position:"absolute",inputTop:155,inputLeft:150,inputWidth:70},
		{type: "input", label: "Praznici:",name:"cass7", value:"",maxLength: 4,readonly : "true",
								position:"absolute",labelTop:180,inputTop:180,inputLeft:110},
				{type: "input", name:"bodd7",value: "",maxLength: 10,readonly : "true",numberFormat:"0,000.00",
									position:"absolute",inputTop:180,inputLeft:150,inputWidth:70},
		{type: "input", label: "Rad na praz.:",name:"cass8", value:"",maxLength: 4,readonly : "true",
								position:"absolute",labelTop:205,inputTop:205,inputLeft:110},
				{type: "input", name:"bodd8",value: "",maxLength: 10,readonly : "true",numberFormat:"0,000.00", 
									position:"absolute",inputTop:205,inputLeft:150,inputWidth:70},
		{type: "input", label: "Naknade od dr.:",name:"cass9", value:"",maxLength: 4,readonly : "true",
								position:"absolute",labelTop:230,inputTop:230,inputLeft:110},
				{type: "input", name:"bodd9",value: "",maxLength: 10,readonly : "true",numberFormat:"0,000.00", 
									position:"absolute",inputTop:230,inputLeft:150,inputWidth:70},
		{type: "input", label: "Noćni rad praz.:",name:"cass10", value:"",maxLength: 4,readonly : "true",
								position:"absolute",labelTop:255,inputTop:255,inputLeft:110},
				{type: "input", name:"bodd10",value: "",maxLength: 10,readonly : "true",numberFormat:"0,000.00", 
									position:"absolute",inputTop:255,inputLeft:150,inputWidth:70},
		{type: "input", label: "Razlika + - :",name:"cass11", value:"",maxLength: 4,readonly : "true",
								position:"absolute",labelTop:280,inputTop:280,inputLeft:110},
				{type: "input", name:"bodd11",value: "",maxLength: 10,readonly : "true",numberFormat:"0,000.00", 
									position:"absolute",inputTop:280,inputLeft:150,inputWidth:70},
		{type: "input", label: "Iznos 65%:",name:"cass12", value:"",maxLength: 4,readonly : "true",
								position:"absolute",labelTop:305,inputTop:305,inputLeft:110},
				{type: "input", name:"bodd12",value: "",maxLength: 10,readonly : "true",numberFormat:"0,000.00", 
								position:"absolute",inputTop:305,inputLeft:150,inputWidth:70},
		{type: "input", label: "Bol.do 30 dana: ", name:"cass13", value:"",maxLength:4,readonly : "true",
								position:"absolute",labelTop:330,inputTop:330,inputLeft:110},	
					{type: "input", name:"bodd13",value: "",maxLength: 10,readonly : "true",numberFormat:"0,000.00", 
								position:"absolute",inputTop:330,inputLeft:150,inputWidth:70},								
		{type: "input", label: "Bolovanje 100%: ", name:"cas14", value:"",maxLength:4,readonly : "true",
									position:"absolute",labelTop:355,inputTop:355,inputLeft:110},	
					{type: "input", name:"bodd14",value: "",maxLength:10,readonly : "true",numberFormat:"0,000.00", 
								position:"absolute",inputTop:355,inputLeft:150,inputWidth:70},
		{type: "input", label: "Bol.preko 30 dana:", name:"cass15", value:"",maxLength:4,readonly : "true",
								position:"absolute",labelTop:380,inputTop:380,inputLeft:110},	
					{type: "input", name:"bodd15",value: "",maxLength:10,readonly : "true",numberFormat:"0,000.00", 
								position:"absolute",inputTop:380,inputLeft:150,inputWidth:70},
		{type: "input", label: "Stimulacija:", name:"cass16", value:"",maxLength:4,readonly : "true",
								position:"absolute",labelTop:405,inputTop:405,inputLeft:110},	
					{type: "input", name:"bodd16",value: "",maxLength:10,readonly : "true",numberFormat:"0,000.00", 
								position:"absolute",inputTop:405,inputLeft:150,inputWidth:70},
		{type: "input", label: "Topli obrok:", name:"cass18", value:"",maxLength:4,readonly : "true",
								position:"absolute",labelTop:430,inputTop:430,inputLeft:110},	
					{type: "input", name:"bodd18",value: "",maxLength:10,readonly : "true",numberFormat:"0,000.00", 
								position:"absolute",inputTop:430,inputLeft:150,inputWidth:70},
		{type: "input", label: "Terenski dodatak: ", name:"cass19", value:"",maxLength:4,readonly : "true",
								position:"absolute",labelTop:455,inputTop:455,inputLeft:110},	
					{type: "input", name:"bodd19",value: "",maxLength:10,readonly : "true",numberFormat:"0,000.00", 
								position:"absolute",inputTop:455,inputLeft:150,inputWidth:70},
		{type: "input", label: "Regres: ", name:"cass20", value:"",maxLength:4,readonly : "true",
								position:"absolute",labelTop:480,inputTop:480,inputLeft:110},	
					{type: "input", name:"bodd20",value: "",maxLength:10,readonly : "true",numberFormat:"0,000.00", 
								position:"absolute",inputTop:480,inputLeft:150,inputWidth:70},
		{type: "input", label: "Ostala primanja: ", name:"cass21", value:"",maxLength:4,readonly : "true",
									position:"absolute",labelTop:505,inputTop:505,inputLeft:110},	
					{type: "input", name:"bodd21",value: "",maxLength:10,readonly : "true",numberFormat:"0,000.00", 
								position:"absolute",inputTop:505,inputLeft:150,inputWidth:70}

		]},
		{type: "block", labelWidth: 250, inputWidth: 60, offsetTop: 0,offsetLeft: 250, list: [
			{type: "input", label: "BRUTO ZARADA:",name:"cass22", value:"",readonly : "true",numberFormat:"0,000.00",
				position:"absolute",labelTop:30,inputTop:30,inputLeft:150,inputWidth:80,labelWidth:250},
			{type: "input", label: "Obračunat dop.:",name:"cass23", value:"",readonly : "true",numberFormat:"0,000.00",
				position:"absolute",labelTop:55,inputTop:55,inputLeft:150,inputWidth:80,labelWidth:250},
			{type: "input", label: "Uplaćen dop.:",name:"cass24", value:"",readonly : "true",numberFormat:"0,000.00",
				position:"absolute",labelTop:80,inputTop:80,inputLeft:150,inputWidth:80,labelWidth:250},
			{type: "input", label: "Obračunat por:",name:"cass25", value:"",readonly : "true",numberFormat:"0,000.00",
				position:"absolute",labelTop:105,inputTop:105,inputLeft:150,inputWidth:80,labelWidth:250},
			{type: "input", label: "Uplaćen por:",name:"cass26", value:"",readonly : "true",numberFormat:"0,000.00",
				position:"absolute",labelTop:130,inputTop:130,inputLeft:150,inputWidth:80,labelWidth:250},
			{type: "input", label: "Dodatni d.obr.:",name:"cass27", value:"",readonly : "true",numberFormat:"0,000.00",
				position:"absolute",labelTop:155,inputTop:155,inputLeft:150,inputWidth:80,labelWidth:250},
			{type: "input", label: "Dodatni d.upl.:",name:"cass28", value:"",readonly : "true",numberFormat:"0,000.00",
				position:"absolute",labelTop:180,inputTop:180,inputLeft:150,inputWidth:80,labelWidth:250},
			{type: "input", label: "NETO :",name:"cass29", value:"",readonly : "true",numberFormat:"0,000.00",
				position:"absolute",labelTop:205,inputTop:205,inputLeft:150,inputWidth:80,labelWidth:250},
			{type: "input", label: "Samodoprinos:",name:"cass30", value:"",readonly : "true",numberFormat:"0,000.00",
				position:"absolute",labelTop:230,inputTop:230,inputLeft:150,inputWidth:80,labelWidth:250},
			{type: "input", label: "Krediti:",name:"cass31", value:"",readonly : "true",numberFormat:"0,000.00",
				position:"absolute",labelTop:255,inputTop:255,inputLeft:150,inputWidth:80,labelWidth:250},
			{type: "input", label: "Sindikati :",name:"cass32", value:"",readonly : "true",numberFormat:"0,000.00",
				position:"absolute",labelTop:280,inputTop:280,inputLeft:150,inputWidth:80,labelWidth:250},
			{type: "input", label: "Solidarnost:",name:"cass33", value:"",readonly : "true",numberFormat:"0,000.00",
				position:"absolute",labelTop:305,inputTop:305,inputLeft:150,inputWidth:80,labelWidth:250},
			{type: "input", label: "Akontacija: ", name:"cass34", value:"",readonly : "true",numberFormat:"0,000.00",
				position:"absolute",labelTop:330,inputTop:330,inputLeft:150,inputWidth:80,labelWidth:250},	
			{type: "input", label: "SVEGA OBUST.:", name:"cass35", value:"",readonly : "true",numberFormat:"0,000.00",
				position:"absolute",labelTop:355,inputTop:355,inputLeft:150,inputWidth:80,labelWidth:250},	
			{type: "input", label: "ZA ISPLATU:", name:"cass36", value:"",readonly : "true",numberFormat:"0,000.00",
				position:"absolute",labelTop:380,inputTop:380,inputLeft:150,inputWidth:80,labelWidth:250},	
			{type: "input", label: "POTREBNO SR.:", name:"cass37", value:"",readonly : "true",numberFormat:"0,000.00",
				position:"absolute",labelTop:405,inputTop:405,inputLeft:150,inputWidth:80,labelWidth:250},	
			{type: "input", label: "Doprinos PIO:", name:"cass38", value:"",readonly : "true",numberFormat:"0,000.00",
				position:"absolute",labelTop:430,inputTop:430,inputLeft:150,inputWidth:80,labelWidth:250},	
			{type: "input", label: "Doprinos ZDR.: ", name:"cass39", value:"",readonly : "true",numberFormat:"0,000.00",
				position:"absolute",labelTop:455,inputTop:455,inputLeft:150,inputWidth:80,labelWidth:250},	
			{type: "input", label: "Doprinos NEZ.: ", name:"cass40", value:"",readonly : "true",numberFormat:"0,000.00",
				position:"absolute",labelTop:480,inputTop:480,inputLeft:150,inputWidth:80,labelWidth:250},	
			{type: "input", label: "Benef.rad.staž: ", name:"cass41", value:"",readonly : "true",numberFormat:"0,000.00",
					position:"absolute",labelTop:505,inputTop:505,inputLeft:150,inputWidth:80,labelWidth:250}	
	]}];

formDataXML = [
	{type: "settings", position: "label-left", labelWidth: 200, inputWidth: 60,labelHeight:15,inputHeight:20},
	{type: "block", offsetTop: 5, list: [
		{type: "input", label: "Klijentska oznaka deklaracije: ",name:"kliozndek", value:"",maxLength: 12,validate:"NotEmpty,ValidInteger",
								position:"absolute", labelTop:8,inputTop:5,inputLeft:200},
		{type: "select", label: "Vrsta prijave",name:"vrstaprijave",  options:[
						            {text: "1-Opšta prijava", value: "1"},
						            {text: "2-Prijava po službenoj dužnosti", value: "2"},
						            {text: "3-Prijava po članu 182b ZPPPA", value: "3"},
						            {text: "4-Prijava po nalazu kontrole", value: "4"},
						            {text: "5-Prijava po odluci suda", value: "5"}
		],position:"absolute",labelTop:30,inputTop: 30,inputLeft:200,inputWidth:200,inputHeight:25},
		{type: "select", label: "Tip isplatioca",name:"tipisplatioca",  options:[
            {text: "1-Pravno lice", value: "1"},
            {text: "2-Pravno lice iz budžeta", value: "2"},
            {text: "3-Predstavništvo", value: "3"},
            {text: "4-Preduzetnik", value: "4"},
            {text: "5-Fizičko lice", value: "5"},
            {text: "6-Vojska", value: "5"},
            {text: "7-Poljoprivredno gazdinstvoa", value: "5"}
         ],position:"absolute",labelTop:55,labelWidth:100,inputTop: 55,inputLeft:200,inputWidth:200,inputHeight:25},
		{type: "input", label: "Obračunski period(gggg-mm):",name:"obracunskiperiod", value:"",maxLength: 15,validate:"NotEmpty,ValidInteger",
								position:"absolute",labelTop:85,inputTop:85,inputLeft:200,inputWidth:100},
		{type: "input", label: "Oznaka za konačnu(K):",name:"oznkonac", value:"",maxLength: 1,validate:"NotEmpty,ValidInteger",
								position:"absolute",labelTop:110,inputTop:110,inputLeft:200,inputWidth:100},
		{type: "input", label: "Datum plaćanja(gggg-mm-dd):",name:"datumplacanja", value:"",maxLength: 10,validate:"NotEmpty,ValidInteger",
								position:"absolute",labelTop:135,inputTop:135,inputLeft:200,inputWidth:100},
		{type: "input", label: "Najniža osnovica:",name:"najnizaosnovica", value:"",maxLength: 20,validate:"NotEmpty,ValidInteger",
								position:"absolute",labelTop:160,inputTop:160,inputLeft:200,inputWidth:100},
		{type: "input", label: "Broj obrade:",name:"brobrade", value:"",maxLength: 2,validate:"NotEmpty,ValidInteger",
								position:"absolute",labelTop:185,inputTop:185,inputLeft:200,inputWidth:100},
		{type: "input", label: "Vrsta plate",name:"vrstaplate",   value:"",maxLength: 20,validate:"NotEmpty,ValidInteger",
									position:"absolute",labelTop:210,inputTop:210,inputLeft:200,inputWidth:150,readonly : "true"}

		]},			
			{type: "block", blockOffset:0,offsetTop:200, list: [
			{type: "button", value: "Formiraj", name:"formiraj", position:"absolute",inputTop: 60,inputLeft:30},
			{type: "button", value: "Izlaz", name:"izlaz",position:"absolute",inputTop: 60,inputLeft:150}
		]}
];	


//---------------------------------------------------------------------------------
function ocistiListic(){
	sifra="";cass1="";cass2="";cass3="";cass4="";cass5="";cass6="";
}
function ocistiUnos(){
	sifra="";cas1="";cas2="";cas3="";cas4="";cas5="";cas6="";
}
function ocistiZaglavlje(){
	mesec="";brojobrade="";vrednostboda="";fondsatiobracun="";fondsatimesec="";neoporeziviiznos="";topliobrok="";
}
function poravnajListic(){
		myFormListic.getInput("bodd1").style.textAlign= "right";
		myFormListic.getInput("bodd2").style.textAlign= "right";
		myFormListic.getInput("bodd3").style.textAlign= "right";
		myFormListic.getInput("bodd4").style.textAlign= "right";
		myFormListic.getInput("bodd5").style.textAlign= "right";
		myFormListic.getInput("bodd6").style.textAlign= "right";
		myFormListic.getInput("bodd7").style.textAlign= "right";
		myFormListic.getInput("bodd8").style.textAlign= "right";
		myFormListic.getInput("bodd9").style.textAlign= "right";
		myFormListic.getInput("bodd10").style.textAlign= "right";
		myFormListic.getInput("bodd11").style.textAlign= "right";
		myFormListic.getInput("bodd12").style.textAlign= "right";
		myFormListic.getInput("bodd13").style.textAlign= "right";
		myFormListic.getInput("bodd14").style.textAlign= "right";
		myFormListic.getInput("bodd15").style.textAlign= "right";
		myFormListic.getInput("bodd16").style.textAlign= "right";
		myFormListic.getInput("bodd17").style.textAlign= "right";
		myFormListic.getInput("bodd18").style.textAlign= "right";
		myFormListic.getInput("bodd19").style.textAlign= "right";
		myFormListic.getInput("bodd20").style.textAlign= "right";
		myFormListic.getInput("bodd21").style.textAlign= "right";

		//-------------------------------------------------------
		myFormListic.getInput("cass22").style.textAlign= "right";
		myFormListic.getInput("cass23").style.textAlign= "right";	
		myFormListic.getInput("cass24").style.textAlign= "right";	
		myFormListic.getInput("cass25").style.textAlign= "right";	
		myFormListic.getInput("cass26").style.textAlign= "right";	
		myFormListic.getInput("cass27").style.textAlign= "right";
		myFormListic.getInput("cass28").style.textAlign= "right";	
		myFormListic.getInput("cass29").style.textAlign= "right";	
		myFormListic.getInput("cass30").style.textAlign= "right";	
		myFormListic.getInput("cass31").style.textAlign= "right";	
		myFormListic.getInput("cass32").style.textAlign= "right";	
		myFormListic.getInput("cass33").style.textAlign= "right";	
		myFormListic.getInput("cass34").style.textAlign= "right";	
		myFormListic.getInput("cass35").style.textAlign= "right";	
		myFormListic.getInput("cass36").style.textAlign= "right";	
		myFormListic.getInput("cass37").style.textAlign= "right";	
		myFormListic.getInput("cass38").style.textAlign= "right";	
		myFormListic.getInput("cass39").style.textAlign= "right";	
		myFormListic.getInput("cass40").style.textAlign= "right";	
		myFormListic.getInput("cass41").style.textAlign= "right";	
}

