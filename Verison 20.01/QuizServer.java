public class QuizServer extends Server{
  private DatabaseConnector d;
  private List<Spieler> spieler;
  private Spiel[] spiele;
  public QuizServer(int pPort) {
    super(pPort);
    d=new DatabaseConnector("",0,"C:/Users/janre/OneDrive/Desktop/Projekt/Online-Quiz-Database_3.accdb","","");
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
        this.getElo(nachrichtTeil[1]);
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
        if (spiele[Integer.parseInt(nachrichtTeil[2])].getRanked()) {
          this.eloAnpassen(spiele[Integer.parseInt(nachrichtTeil[2])]);
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
      this.registrieren(nachrichtTeil[1], nachrichtTeil[2]);
      this.send(pClientIP, pClientPort, "1::"+nachrichtTeil[1]);
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
    d.executeStatement("SELECT Elo FROM Profile WHERE Benutzername='"+pUser+"'");
    QueryResult tmp=d.getCurrentQueryResult();
    String[][] erg=tmp.getData();
    System.out.println(Integer.parseInt(erg[0][0]));
    return Integer.parseInt(erg[0][0]);
  }
  public boolean pruefLogin(String pUserName, String pPasswort){
    d.executeStatement("SELECT ID FROM Profile WHERE Benutzername='"+pUserName+"' AND Kennwort='"+pPasswort+"'");
    QueryResult tmp=d.getCurrentQueryResult();
    String[][] erg=tmp.getData();
    if (erg.length>0) {
      return true;
    }
    return false;
  }
  public void registrieren(String pUserName, String pPasswort){
    d.executeStatement("SELECT MAX(ID) FROM Profile");
    QueryResult tmp=d.getCurrentQueryResult();
    String[][] erg=tmp.getData();
    int id=Integer.parseInt(erg[0][0])+1;
    d.executeStatement("INSERT INTO Profile (ID, Benutzername, Kennwort, Anzahl_Erstellte_Quiz, Elo) VALUES ('"+id+"', '"+pUserName+"', '"+pPasswort+"', '"+0+"', '"+1000+"')");
    d.executeStatement("SELECT MAX(ID) FROM Profile");
    tmp=d.getCurrentQueryResult();
    erg=tmp.getData();
  }
  
  public void eloAnpassen(Spiel pSpiel){
    System.out.println("a");
    Spieler[] spieler=pSpiel.getSpieler();
    System.out.println(spieler[0].getName());
    System.out.println(spieler[0].getElo());
    System.out.println(spieler[1].getName());
    System.out.println(spieler[1].getElo());
    d.executeStatement("UPDATE Profile SET Elo = '"+spieler[0].getElo()+"'WHERE Benutzername = '"+spieler[0].getName()+"'");
    d.executeStatement("UPDATE Profile SET Elo = '"+spieler[1].getElo()+"'WHERE Benutzername = '"+spieler[1].getName()+"'");
  }
  public String quizLaden(int pId){
    pId=1;
    d.executeStatement("SELECT * FROM Fragen WHERE Fragen_ID=(SELECT Fragen_ID FROM Quiz WHERE Quiz_ID='"+pId+"')");
    QueryResult tmp=d.getCurrentQueryResult();
    String[][] erg=tmp.getData();
    int x=0;
    String quiz="";
    for (int i=1; i<erg[0].length; i++) {
      quiz+=erg[0][i]+"--";
      x++;
      if (x==6) {
        quiz+="//";
        x=0;
      }
    }
    return quiz;
  }
}
