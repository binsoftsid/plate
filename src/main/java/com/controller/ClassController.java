package com.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ClassController {

   @RequestMapping("/pred")
    public String preduzeca() {
        return "Preduzeca";
    }
   @RequestMapping("/maticna")
   public String matUnos() {
       return "Maticna";
   }
   @RequestMapping("/mesnez")
   public String mesZaj() {
       return "Mesnez";
   } 
   @RequestMapping("/banke")
   public String banKe() {
       return "Banke";
   }    
   @RequestMapping("/kvalifikacije")
   public String kvaliFik() {
       return "Kvalifikacije";
   }    
   @RequestMapping("/sindikati")
   public String sindiKati() {
       return "Sindikati";
   }    
   @RequestMapping("/kreditori")
   public String krediTori() {
       return "Kreditori";
   }    
   @RequestMapping("/krediti")
   public String krediTi() {
       return "Krediti";
   }    
   @RequestMapping("/parametri")
   public String paraMetri() {
       return "Parametri";
   } 
   @RequestMapping("/pregledobrada")
   public String pregledObrada() {
       return "Pregledobrada";
   }     
   @RequestMapping("/rekapplate")
   public String rekapPlate() {
       return "RekapPlate";
   } 
   @RequestMapping("/rekappordop")
   public String rekapPordop() {
       return "RekapPordop";
   } 
   @RequestMapping("/virmanitekuci")
   public String virTekuci() {
       return "Virmanitekuci";
   }   
   @RequestMapping("/virmanipordop")
   public String virPordop() {
       return "Virmanipordop";
   }   
   @RequestMapping("/virmanisindikati")
   public String virSindikati() {
       return "Virmanisindikati";
   }   
   @RequestMapping("/virmanikreditori")
   public String virKreditori() {
       return "Virmanikreditori";
   }   
   @RequestMapping("/virmanisamodop")
   public String virSamodop() {
       return "Virmanisamodop";
   }   
   @RequestMapping("/sifpreduz")
   public String sifPreduz() {
       return "Sifpreduz";
   } 
   @RequestMapping("/maps")
   public String mapGoogle() {
       return "Maps";
   }
   @RequestMapping("/putnitroskovi")
   public String putTroskovi() {
       return "Putnitroskovi";
   }       
   @RequestMapping("/listaputni")
   public String listaPutni() {
       return "Listaputni";
   }       
   @RequestMapping("/isplatneliste")
   public String isplatneListe() {
       return "Isplatneliste";
   }       
   @RequestMapping("/karticaradnika")
   public String karticaRadnika() {
       return "Karticaradnika";
   } 
   @RequestMapping("/karticakredita")
   public String karticaKredita() {
       return "Karticakredita";
   }     
   @RequestMapping("/help")
   public String getHelp() {
       return "Help";
   } 
   @RequestMapping("/kontakt")
   public String getContact() {
       return "Kontakt";
   }
   @RequestMapping("/email")
   public String getEmail() {
       return "Mail";
   }
   @RequestMapping("/emailslanje")
   public String getEmailSlanje() {
       return "Maillista";
   }    
   @RequestMapping("/izmenalozinke")
   public String getIzmenaLozinke() {
       return "Izmenalozinke";
   }   }