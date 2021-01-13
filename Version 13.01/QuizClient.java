lolobby(lass QuizClient extends Client{
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
      gui.loginSuccess();
    }
    if(nachrichtTeil[0].equals("2")){
      gui.fehlerAnzeigen(nachrichtTeil[1]);
    }
    if(nachrichtTeil[0].equals("3")){         
      spiel=Integer.parseInt(nachrichtTeil[2]);
      spieler=Integer.parseInt(nachrichtTeil[3]);
      String[] pFragen=nachrichtTeil[1].split("//");
      for (int i=0;i<pFragen.length;i++) {
        fragen.append(pFragen[i]);
      }
      this.frageLaden();
    }
    if(nachrichtTeil[0].equals("4")){         
      gui.ergebnis(nachrichtTeil[1]);                                       
    }
    if(nachrichtTeil[0].equals("5")){         
      user=nachrichtTeil[1];                                       
    } 
  }  
  public void frageLaden(){
    fragen.toFirst();
    if (!fragen.hasAccess()) {
      gui.frageAnzeigen(null);
      return;
    }
    String[] frage=fragen.getContent().split("--");
    fragen.remove();
    gui.frageAnzeigen(frage);
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
    this.send("6::"+pFragen+"::"+pKategorie+"::"+pName);
  }
  public void registrieren(String pUserName, String pPasswort){
    this.send("7::"+pUserName+"::"+pPasswort);
  }
  public void privateLobby(String pQuizId, int pSpieler){
    this.send("8::"+user+"::"+pQuizId+"::"+pSpieler);
  }
  public void joinPrivate(int pCode){
    this.send("9::"+user+"::"+pCode);
  }
  public void alleQuizs(){
    this.send("10");
  }
  public void kategorien(){
    this.send("11");
  }
  public void quizkategorie(String pKategorie){
    this.send("12::"+pKategorie);
  }
  public void sendeNachricht(String pNachricht){
    this.send("13::"+pNachricht+"::"+spiel+"::"+spieler);
  }
}
