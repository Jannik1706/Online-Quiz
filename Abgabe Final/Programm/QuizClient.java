/**
 * führt die Befehle des Servers aus in dem er die GUI Methoden aufruft
 */
public class QuizClient extends Client{
  private GUI gui;
  private List<String> fragen;
  private String user;
  private int spiel;
  private int spieler;
  public QuizClient(String pServerIP, int pServerPort, GUI pGUI){
    super(pServerIP, pServerPort);
    gui=pGUI;
    fragen=new List<String>();
    user="";
    spiel=0;
    spieler=0;
  } 
  public void processMessage(String pMessage){
    String[] nachrichtTeil = pMessage.split("::");
    if(nachrichtTeil[0].equals("1")){
      user=nachrichtTeil[1];
      gui.loginSuccess(nachrichtTeil[1]);
    }
    if(nachrichtTeil[0].equals("2")){
      gui.fehlerAnzeigen(nachrichtTeil[1]);
    }
    //Der Client erhält das Quiz im Format Frage--Antwort1...//Frage--Antwort1..2//.
    //In der if Anweisungen werden zunächst die Fragen getrennt und in einer Liste gespeichert.
    if(nachrichtTeil[0].equals("3")){         
      spiel=Integer.parseInt(nachrichtTeil[2]);//Der GUI wird übergegeben in welchen Spiel sie ist
      spieler=Integer.parseInt(nachrichtTeil[3]);//und welcher Spieler im Array des Spiels.
      String[] pFragen=nachrichtTeil[1].split("//");//Der Quiz-String wird in Fragen aufgeteilt.
      for (int i=0;i<pFragen.length;i++) {
        fragen.append(pFragen[i]);//Die Fragen werden der Liste hinzugefügt.
      }
      this.frageLaden();//Die Fragen werden in dieser Methode der GUI übergeben
    }
    if(nachrichtTeil[0].equals("4")){         
      gui.ergebnis(nachrichtTeil[1]);                                       
    }
    if(nachrichtTeil[0].equals("6")){         
      gui.chatAnzeigen(nachrichtTeil[1]);                                      
    } 
    
    if(nachrichtTeil[0].equals("8")){         
      gui.idAnzeigen(nachrichtTeil[1]);                                      
    } 
    if(nachrichtTeil[0].equals("9")){         
      gui.statistikenAnzeigen(nachrichtTeil[1].split("//"));                                      
    } 
    if(nachrichtTeil[0].equals("11")){         
      gui.quizSucheAnzeigen(nachrichtTeil[1].split("//"));                                      
    } 
    if(nachrichtTeil[0].equals("12")){         
      spiel=Integer.parseInt(nachrichtTeil[1]);
      spieler=Integer.parseInt(nachrichtTeil[2]);
      gui.idAnzeigen(nachrichtTeil[3]);                                      
    } 
    if(nachrichtTeil[0].equals("14")){         
      gui.spielerListe(nachrichtTeil[1].split("//"));                                                                             
    } 
    if(nachrichtTeil[0].equals("15")){         
      gui.quizName(nachrichtTeil[1]);                                                                             
    } 
  }  
  public void frageLaden(){
    fragen.toFirst();
    if (!fragen.hasAccess()) {//Wenn alle Fragen angezeit wurden wird das der GUI mit dem Parameter null übermittelt.
      gui.frageAnzeigen(null);
      return;
    }
    String[] frage=fragen.getContent().split("--");//Die Frage wird in den Fragetext und den Antwortmöglichkeiten zerteilt.
    fragen.remove();
    gui.frageAnzeigen(frage);//Die GUI zeigt die Frage an.
    //Sobald die Frage beantwortet wurde führt die GUI die Methode erneut aus bis es keine Fragen mehr gibt.
  }
  public void anmelden(String pUserName, String pPasswort){
    this.send("1::"+pUserName+"::"+pPasswort);
  }
  public void quizAnfordern(){
    this.send("2");
  }
  public void sucheGegner(){
    this.send("3::"+user);
  }
  public void ergebnisVerschicken(int pPunkte){
    this.send("4::"+pPunkte+"::"+spiel+"::"+spieler);
  }
  public void lobby(){
    this.send("5::"+user);
  }
  public void quizErstellen(String pFragen, String pKategorie, String pName){
    this.send("6::"+pFragen+"::"+pKategorie+"::"+pName+"::"+user);
  }
  public void registrieren(String pUserName, String pPasswort){
    this.send("7::"+pUserName+"::"+pPasswort);
  }
  public void privateLobby(){
    this.send("8::"+user);
  }
  public void joinPrivate(int pCode){
    this.send("9::"+user+"::"+pCode);
  }
  public void sendeNachricht(String pNachricht){
    this.send("13::"+pNachricht+"::"+spiel+"::"+spieler);
  }
  public void spielVerlassen(){
    this.send("14::"+spiel+"::"+spieler);
  }
  public void bewertungAbgeben(int pBewertung){
    this.send("15::"+pBewertung+"::"+spiel);
  }
  public void statistiken(){
    this.send("16::"+user);
  }
  public void quizAnfragen(int pQuizId){
    this.send("17::"+pQuizId+"::"+spiel);
  }
  public void quizSuchen(String pSuche){
    this.send("18::"+pSuche);
  }
  public void abmelden(){
    this.send("19::"+user);
  }
  public void privatStart(){
    this.send("20::"+spiel);
    }
}
