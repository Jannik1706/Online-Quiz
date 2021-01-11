public class QuizServer extends Server{
  private DatabaseConnector d;
  private List<Spieler> spieler;
  private Spiel[] spiele;
  public QuizServer(int pPort) {
    super(pPort);
    d=new DatabaseConnector("",0,"Datenbank.accdb","","");
    spieler=new List<Spieler>();
    spiele=new Spiel[100];
  }
  public void processNewConnection(String pClientIP, int pClientPort){
    this.send(pClientIP, pClientPort, "Willkommen!");
  }  
  public void processMessage(String pClientIP, int pClientPort, String pMessage){
    String[] nachrichtTeil = pMessage.split("::");
    System.out.println(pMessage);
    if(nachrichtTeil[0].equals("1")){           
      if (pruefLogin(nachrichtTeil[1], nachrichtTeil[2])) {
        this.send(pClientIP, pClientPort, "1::"+nachrichtTeil[1]);
      }else{
        this.send(pClientIP, pClientPort, "2::falsches Passwort oder Username");  
      }
    }
    if(nachrichtTeil[0].equals("2")){           
      this.send(pClientIP, pClientPort, "3::"+quizLaden(0));
    }
    if(nachrichtTeil[0].equals("3")){           
      Spieler tmp=new Spieler(nachrichtTeil[1], pClientIP, pClientPort, this.getElo(nachrichtTeil[1]));
      Spieler gegner=this.findeGegner(tmp.getElo());
      if (gegner!=null) {
        int tmp2=gegner.getSpiel();
        System.out.println(tmp2+gegner.getName());
        this.spielerhinzufuegen(tmp, tmp2);
        this.starteQuiz(spiele[tmp2]);
        spieler.remove();
        return;
      }
      this.spielerhinzufuegen(tmp, this.spielErstellen(2, true, -1));
      spieler.append(tmp);
    }
    if(nachrichtTeil[0].equals("4")){           
      spiele[Integer.parseInt(nachrichtTeil[2])].punkteGeben(Integer.parseInt(nachrichtTeil[3]), Integer.parseInt(nachrichtTeil[1]));
      Spieler[] tmp=spiele[Integer.parseInt(nachrichtTeil[2])].gewinner();
      if (tmp!=null) {
        for (int i=0; i<tmp.length; i++) {
          this.sende(tmp[i], "4::"+(i+1));
        }                         
        spiele[Integer.parseInt(nachrichtTeil[2])]=null;
      }
    }
    if(nachrichtTeil[0].equals("5")){           
      Spieler tmp=new Spieler(nachrichtTeil[1], pClientIP, pClientPort, this.getElo(nachrichtTeil[1]));
      for (int i=0; i<spiele.length; i++) {
        if (spiele[i]!=null&&!spiele[i].spielVoll()&&!spiele[i].getRanked()) {
          spiele[i].spielerhinzufuegen(tmp);
          if (spiele[i].spielVoll()) {
            starteQuiz(spiele[i]);
          }
          return;
        }
      }
      this.spielerhinzufuegen(tmp, this.spielErstellen(3, false, -1));
    }
    if(nachrichtTeil[0].equals("6")){
      this.quizErstellen(nachrichtTeil[1], nachrichtTeil[2]);
    }
    if(nachrichtTeil[0].equals("7")){
      if (registrieren(nachrichtTeil[1], nachrichtTeil[2])) {
        this.send(pClientIP, pClientPort, "5::"+nachrichtTeil[1]);
      }else{
        this.send(pClientIP, pClientPort, "2::Der Username ist bereits vergeben");
      }
    }
    if(nachrichtTeil[0].equals("8")){
      Spieler tmp=new Spieler(nachrichtTeil[1], pClientIP, pClientPort, this.getElo(nachrichtTeil[1]));
      int tmp2=spielErstellen(Integer.parseInt(nachrichtTeil[3]), false, Integer.parseInt(nachrichtTeil[2]));
      this.spielerhinzufuegen(tmp, tmp2);
      int code=(int)(Math.random()*900000)+100000;
      spiele[tmp2].setCode(code);
      this.sende(tmp, "6::"+code);
    }
    if(nachrichtTeil[0].equals("9")){
      Spieler tmp=new Spieler(nachrichtTeil[1], pClientIP, pClientPort, this.getElo(nachrichtTeil[1]));
      for (int i=0;i<spiele.length;i++) {
        if (spiele[i].getCode()==Integer.parseInt(nachrichtTeil[2])) {
          spielerhinzufuegen(tmp, i);
        } 
      }
      this.sende(tmp, "2::Die Lobby wurde nicht gefunden");
    }
    if(nachrichtTeil[0].equals("13")){
      this.sendeAllen(spiele[Integer.parseInt(nachrichtTeil[2])], nachrichtTeil[3], nachrichtTeil[1]);
    }
  }
  public Spieler findeGegner(int pElo){
    spieler.toFirst();
    while (spieler.hasAccess()) { 
      if (Math.abs(spieler.getContent().getElo()-pElo)<100) {
        return spieler.getContent();
      }
      spieler.next();
    }
    return null;
  }
  public int spielErstellen(int pAnzahl, boolean pRanked, int pQuizId){
    for (int i=0;i<spiele.length;i++) {
      if (spiele[i]==null) {
        spiele[i]=new Spiel(pAnzahl, pRanked, i, pQuizId);
        return i;
      }
    }
    return -1;
  }
  public int spielerhinzufuegen(Spieler pSpieler, int pSpiel){
    return spiele[pSpiel].spielerhinzufuegen(pSpieler);
  }
  public void processClosingConnection (String pClientIP, int pClientPort){
    this.send(pClientIP, pClientPort, "Auf Wiedersehen!");
  }
  public void sende(Spieler pSpieler, String pNachricht){
    this.send(pSpieler.getIp(), pSpieler.getPort(), pNachricht);
    System.out.println(pSpieler.getName()+pSpieler.getIp()+pSpieler.getPort()+pNachricht);
  }
  public void starteQuiz(Spiel pSpiel){
    Spieler tmp[]=pSpiel.getSpieler();
    for (int i=0;i<tmp.length;i++) {
      this.sende(tmp[i], "3::"+quizLaden(0)+"::"+tmp[i].getSpiel()+"::"+tmp[i].getNr());
    }
  }
  public void sendeAllen(Spiel pSpiel, String pUser, String pNachricht){
    Spieler tmp[]=pSpiel.getSpieler();
    for (int i=0;i<tmp.length;i++) {
      this.sende(tmp[i], "6::"+pUser+"::"+pNachricht);
    }
  }
  //SQL-Anbindung
  public void quizErstellen(String pFragen, String pKategorie){
    
  }
  public int getElo(String pUser){
    if (pUser.equals("a")) {
      return 1500;
    }
    if (pUser.equals("b")) {
      return 1000;
    }
    return 1010;
  }
  public boolean pruefLogin(String pUserName, String pPasswort){
    return true; 
  }
  public boolean registrieren(String pUserName, String pPasswort){
    //returned false falls der Username schon vergeben ist
    return true;
  }
  
  public void eloAnpassen(String pUser, int pElo){
    
  }
  public String quizLaden(int pId){
    return "1+1=?--1--2--3--4--2//1+3=?--1--2--3--4--4";
  }
}
