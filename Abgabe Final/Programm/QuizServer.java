/**
 * verwaltet die Anfragen der Clients und stellt die Verbindung zur Datenbank her
 */
public class QuizServer extends Server{
  private DatabaseConnector d;
  private List<Spieler> spieler;
  private Spiel[] spiele;
  private List<String> users;
  public QuizServer(int pPort) {
    super(pPort);
    d=new DatabaseConnector("",0,"Pfad/Online-Quiz-Database_6.accdb","","");   //Hier pfad zu Online-Quiz-Database_6.accdb mit / anstatt \ einfügen
    spieler=new List<Spieler>();
    spiele=new Spiel[100];//Die Array größe limitiert die Spiele die gleichzeitig gespielt werden können.
    users=new List<String>();
  }
  public void processNewConnection(String pClientIP, int pClientPort){
    this.send(pClientIP, pClientPort, "Willkommen!");
  }  
  public void processMessage(String pClientIP, int pClientPort, String pMessage){
    String[] nachrichtTeil = pMessage.split("::");
    System.out.println(pMessage);
    if(nachrichtTeil[0].equals("1")){           
      users.toFirst();//Die Liste users beinhaltet alle Namen der User die bereits eingeloggt sind.
      while (users.hasAccess()) { 
        if (users.getContent().equals(nachrichtTeil[1])) {//Es wird überprüft, ob der User schon eingeloggt ist.
          this.send(pClientIP, pClientPort, "2::du bist bereits eingeloggt");//Falls ja wird dem Client der Fehler geschickt.
          return;                                                            //Bei jedem Server-Befehl der mit 2:: anfängt zeigt die GUI automatisch im pop up Fenster den weiteren Text als Fehler an
        }
        users.next();
      }
      if (pruefLogin(nachrichtTeil[1], nachrichtTeil[2])) {//Falls der User noch nicht angemeldet war wird überprüft ob Passwort und Name übereinstimmen.
        this.send(pClientIP, pClientPort, "1::"+nachrichtTeil[1]);
        users.append(nachrichtTeil[1]);//Der User hat sich erfolgreich angemeldet und wird der Liste der aktiven User hinzugefügt.
                                       //Beim ausloggen oder schließen der GUI wird der User aus der Liste entfernt (siehe nachrichtTeil[0].equals("19")(unkommentiert))
      }else{
        this.send(pClientIP, pClientPort, "2::falsches Passwort oder Username");  
      }
    }
    if(nachrichtTeil[0].equals("2")){           
      this.send(pClientIP, pClientPort, "3::"+quizLaden(0));
    }
    if(nachrichtTeil[0].equals("3")){           
      Spieler tmp=new Spieler(nachrichtTeil[1], pClientIP, pClientPort, this.getElo(nachrichtTeil[1]));//Die Daten des neuen Spielers werden aus der Datenbank abgerufen.
      Spieler gegner=this.findeGegner(tmp.getElo());//Es wird ein Gegener gesucht der bereits in der Warteschlange ist und die passende Elo hat.
      if (gegner!=null) {//Falls ein Gegner gefunden wurde wird das Quiz gestartet.
        int tmp2=gegner.getSpiel();//Die Nr. des Spiel in dem sich der Gegner bereits befindet wird als tmp2 gepeichert.
        this.spielerhinzufuegen(tmp, tmp2);//Der neue Spieler tmp wird zum Spiel mit der Nr. tmp2 hinzugefügt.
        this.starteQuiz(spiele[tmp2]);//Das Quiz wird autmatisch gestartet
        spieler.remove();//der Spieler wird aus der Warteschlange entfernt
        return;
      }
      this.spielerhinzufuegen(tmp, this.spielErstellen(2, true, this.randomQuizId(), null, false));//Falls kein geeigneter Spieler in der Warteschlange befindet wird für den Spieler ein neues Spiel erstellt.
      spieler.append(tmp);//Der neue Spieler wird in die Warteschlange hinzugefügt.
    }
    if(nachrichtTeil[0].equals("4")){//Der User hat das Quiz beendet und sein Ergebnis an den Server geschickt.          
      spiele[Integer.parseInt(nachrichtTeil[2])].punkteGeben(Integer.parseInt(nachrichtTeil[3]), Integer.parseInt(nachrichtTeil[1]));//Die Punktezahl im des Spielers wird gespeichrt.
                                                                                                                                     //Der Spieler speichert selbst in welchen Spiel er sich befindet und welche Nr. er in diesem Spiel hat.
      Spieler[] tmp=spiele[Integer.parseInt(nachrichtTeil[2])].gewinner();//Wenn alle Spieler des Spiels ihr Ergebnis abgegeben haben wird von der Methode gewinner() ein Spieler[] zurückgegeben.
      if (tmp!=null) {
        for (int i=0; i<tmp.length; i++) {//Tmp ist nach den erziehlten Punkten sortiert
          this.sende(tmp[i], "4::"+(i+1));//Die Spieler kriegen ihre Platzierung zugeschickt.
        }
        if (spiele[Integer.parseInt(nachrichtTeil[2])].getRanked()) {//Falls es ein ranked Spiel war werden die Statistiken angepasst.
          this.eloAnpassen(spiele[Integer.parseInt(nachrichtTeil[2])]);//Die Elo beider Spieler wird angepasst.
          this.gewonnenVerloren(true, tmp[0].getName());//Es wird einzelnt gespeichert ob die Spieler gewonnen oder verloren haben
          this.gewonnenVerloren(false, tmp[1].getName());
        }                  
      }
    }
    if(nachrichtTeil[0].equals("5")){           
      Spieler tmp=new Spieler(nachrichtTeil[1], pClientIP, pClientPort, this.getElo(nachrichtTeil[1]));
      for (int i=0; i<spiele.length; i++) {
        if (spiele[i]!=null&&!spiele[i].spielVoll()&&!spiele[i].getRanked()&&!spiele[i].getSpielt()) {
          spiele[i].spielerhinzufuegen(tmp);
          if (spiele[i].spielVoll()) {
            starteQuiz(spiele[i]);
          }
          return;
        }
      }
      this.spielerhinzufuegen(tmp, this.spielErstellen(3, false, this.randomQuizId(), null, false));
    }
    if(nachrichtTeil[0].equals("6")){
      this.quizErstellen(nachrichtTeil[1], nachrichtTeil[2], nachrichtTeil[3], nachrichtTeil[4]);
    }
    if(nachrichtTeil[0].equals("7")){
      if (this.registrieren(nachrichtTeil[1], nachrichtTeil[2])) {
        this.send(pClientIP, pClientPort, "1::"+nachrichtTeil[1]);
      }else {
        this.send(pClientIP, pClientPort, "2::Name ist bereits vergeben");  
      }
    }
    if(nachrichtTeil[0].equals("8")){
      boolean unique=false;
      Spieler tmp=new Spieler(nachrichtTeil[1], pClientIP, pClientPort, this.getElo(nachrichtTeil[1]));
      int tmp2=spielErstellen(10, false, this.randomQuizId(), tmp, true);
      int tmp3=spielerhinzufuegen(tmp, tmp2);
      while (!unique) { 
        unique=true;
        int code=(int)(Math.random()*900000)+100000;
        for (int i=0; i<spiele.length; i++) {
          if (spiele[i]!=null&&spiele[i].getPrivat()&&code==spiele[i].getCode()) {
            unique=false;
          }
        }
        if (unique) {
          spiele[tmp2].setCode(code);
          System.out.println(code);
          this.sende(tmp, "12::"+tmp2+"::"+tmp3+"::"+code);
        }
      }
    }
    if(nachrichtTeil[0].equals("9")){
      Spieler tmp=new Spieler(nachrichtTeil[1], pClientIP, pClientPort, this.getElo(nachrichtTeil[1]));
      for (int i=0;i<spiele.length;i++) {
        if (spiele[i]!=null&&spiele[i].getCode()==Integer.parseInt(nachrichtTeil[2])) {
          this.sende(tmp, "12::"+spiele[i].getNr()+"::"+spielerhinzufuegen(tmp, i)+"::"+spiele[i].getCode());
          this.spielerListe(spiele[i]);
          return;
        } 
      }
      this.sende(tmp, "2::Die Lobby wurde nicht gefunden");
    }
    if(nachrichtTeil[0].equals("13")){
      this.sendeAllen(spiele[Integer.parseInt(nachrichtTeil[2])], "6::"+spiele[Integer.parseInt(nachrichtTeil[2])].getSpieler()[Integer.parseInt(nachrichtTeil[3])].getName()+": "+nachrichtTeil[1]);
    }
    if(nachrichtTeil[0].equals("14")){
      spieler.toFirst();
      while (spieler.hasAccess()) { 
        if(spieler.getContent().getName().equals(spiele[Integer.parseInt(nachrichtTeil[1])].getSpieler()[Integer.parseInt(nachrichtTeil[2])].getName())){
          spieler.remove();
        }
      }
      if (spiele[Integer.parseInt(nachrichtTeil[1])].spielerEntfernen(Integer.parseInt(nachrichtTeil[2]))) {
        spiele[Integer.parseInt(nachrichtTeil[1])]=null;
      }
    }
    if(nachrichtTeil[0].equals("15")){
      this.quizBewerten(spiele[Integer.parseInt(nachrichtTeil[2])].getQuizId(), Integer.parseInt(nachrichtTeil[1]));
    }
    if(nachrichtTeil[0].equals("16")){
      this.send(pClientIP, pClientPort, "9::"+statistiken(nachrichtTeil[1]));
    }
    if(nachrichtTeil[0].equals("17")){
      if (this.quizIdPruefen(nachrichtTeil[1])) {
        spiele[Integer.parseInt(nachrichtTeil[2])].setQuizId(Integer.parseInt(nachrichtTeil[1]));
        this.sendeAllen(spiele[Integer.parseInt(nachrichtTeil[2])], "15::"+this.getQuizName(spiele[Integer.parseInt(nachrichtTeil[2])].getQuizId()));
      }else {
        this.sende(spiele[Integer.parseInt(nachrichtTeil[2])].getHost(), "2::Das Quiz existiert nicht");
      }
    }
    if(nachrichtTeil[0].equals("18")){
      this.send(pClientIP, pClientPort, "11::"+this.quizSuchen(nachrichtTeil[1]));
    }
    if(nachrichtTeil[0].equals("19")){
      users.toFirst();
      while (users.hasAccess()) { 
        if (users.getContent().equals(nachrichtTeil[1])) {
          users.remove();
          return;
        }
        users.next();
      }
    }
    if(nachrichtTeil[0].equals("20")){
      this.starteQuiz(spiele[Integer.parseInt(nachrichtTeil[1])]);
    }
  }
  public void spielerListe(Spiel pSpiel){
    Spieler[] array=pSpiel.getSpieler();
    String erg="";
    for (int i=0; i<array.length; i++) {
      if (array[i]!=null) {
        erg+=array[i].getName()+"//";
      }
    }
    this.sendeAllen(pSpiel, "14::"+erg);
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
  public int spielErstellen(int pAnzahl, boolean pRanked, int pQuizId, Spieler pSpieler, boolean pPrivat){
    for (int i=0; i<spiele.length; i++) {
      if (spiele[i]==null) {
        spiele[i]=new Spiel(pAnzahl, pRanked, i, pQuizId, pSpieler, pPrivat);
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
    if (pSpieler!=null) {
      this.send(pSpieler.getIp(), pSpieler.getPort(), pNachricht);
    }
  }
  public void starteQuiz(Spiel pSpiel){
    Spieler tmp[]=pSpiel.getSpieler();
    pSpiel.setSpielt(true);
    for (int i=0; i<tmp.length; i++) {
      if (tmp[i]!=null) {
        this.sende(tmp[i], "3::"+quizLaden(pSpiel.getQuizId())+"::"+tmp[i].getSpiel()+"::"+tmp[i].getNr());
      }
    }
  }
  public int randomQuizId(){
    d.executeStatement("SELECT MAX(Quiz_ID) FROM Quiz");
    QueryResult tmp=d.getCurrentQueryResult();
    String[][] erg=tmp.getData();
    int a=Integer.parseInt(erg[0][0]);
    int id=(int)(Math.random()*a)+1;
    return id;
  }
  public void sendeAllen(Spiel pSpiel, String pNachricht){
    Spieler tmp[]=pSpiel.getSpieler();
    for (int i=0; i<tmp.length; i++) {
      this.sende(tmp[i], pNachricht);
    }
  }
  public void sendeBewertungen(Spiel pSpiel, int pId){
    Spieler tmp[]=pSpiel.getSpieler();
    for (int i=0; i<tmp.length; i++) {
      if (tmp[i]!=null) {
        this.sende(tmp[i], "7::"+pId); 
      }
    }
  }
  //SQL-Anbindung
  public String quizLaden(int pId){
    d.executeStatement("SELECT * FROM Frage WHERE Quiz_ID='"+pId+"'");
    QueryResult tmp=d.getCurrentQueryResult();
    String[][] erg=tmp.getData();
    String quiz="";
    for (int a=0; a<erg.length; a++) {
      for (int i=2; i<erg[a].length; i++) {
        quiz+=erg[a][i]+"--";
      }
      quiz+="//";
    }
    System.out.println(quiz);
    return quiz;
  }
  public void quizErstellen(String pFragen, String pKategorie, String pName, String pUser){
    d.executeStatement("SELECT MAX(Quiz_ID) FROM Quiz");
    QueryResult tmp=d.getCurrentQueryResult();
    String[][] erg=tmp.getData();
    int id=Integer.parseInt(erg[0][0])+1;
    d.executeStatement("INSERT INTO Quiz (Quiz_ID, Kategorie, Name, Ersteller) VALUES ('"+id+"', '"+pKategorie+"', '"+pName+"', '"+pUser+"')");
    this.frageErstellen(pFragen, id);
    this.quizErstellt(pUser);
  }
  public void frageErstellen(String pFragen, int pId){
    String[] Fragen=pFragen.split("//");
    for (int i=0; i<Fragen.length; i++) {
      String[] Frage=Fragen[i].split("--");
      d.executeStatement("INSERT INTO Frage (Quiz_ID, Frage_Nr, Frage, Antwort1, Antwort2, Antwort3, Antwort4, Antwort_richtig) VALUES ('"+pId+"', '"+i+"', '"+Frage[0]+"', '"+Frage[1]+"', '"+Frage[2]+"', '"+Frage[3]+"', '"+Frage[4]+"', '"+Frage[5]+"')");
    }
  }
  public void quizErstellt(String pUser){
    int counter=0;
    d.executeStatement("SELECT Anzahl_Erstellte_Quiz FROM Profile WHERE Benutzername='"+pUser+"'");
    QueryResult tmp=d.getCurrentQueryResult();
    String[][] erg=tmp.getData();
    counter=Integer.parseInt(erg[0][0])+1;
    d.executeStatement("UPDATE Profile SET Anzahl_Erstellte_Quiz = '"+counter+"'WHERE Benutzername = '"+pUser+"'");
  }
  public String quizSuchen(String pSuche){
    if (pSuche.equals("alle")) {
      d.executeStatement("SELECT * FROM Quiz");
      QueryResult tmp=d.getCurrentQueryResult();
      String[][] erg=tmp.getData();
      String quiz="";
      for (int a=0; a<erg.length; a++) {
        for (int i=0; i<erg[a].length; i++) {
          quiz+=erg[a][i]+"--";
        }
        quiz+="//";
      }
      return quiz;
    }
    d.executeStatement("SELECT * FROM Quiz WHERE Name LIKE '*"+pSuche+"*' OR Kategorie LIKE '*"+pSuche+"*' OR Ersteller LIKE '"+pSuche+"' OR Ersteller LIKE '*"+pSuche+"*'");
    QueryResult tmp=d.getCurrentQueryResult();
    String[][] erg=tmp.getData();
    String quiz="";
    if (erg.length==0) {
      return "keine Quize gefunden";
    } // 
    for (int a=0; a<erg.length; a++) {
      for (int i=0; i<erg[a].length; i++) {
        quiz+=erg[a][i]+"--";
      }
      quiz+="//";
    }
    return quiz;
  }
  public void quizBewerten(int pId, int pBewertung){
    int counter=0;
    d.executeStatement("SELECT Bewertung FROM Quiz WHERE Quiz_ID='"+pId+"'");
    QueryResult tmp=d.getCurrentQueryResult();
    String[][] erg=tmp.getData();
    if (erg[0][0]==null) {
      counter=pBewertung;
    } else {
      counter=Integer.parseInt(erg[0][0])+pBewertung;  
    }
    d.executeStatement("UPDATE Quiz SET Bewertung = '"+counter+"'WHERE Quiz_ID='"+pId+"'");
  }
  /**
   * Die Methode prüft ob der Name und das Passwort des Users der sich anmelden will übereinstimmen.
   * SQL ist nicht case-sensitive und des wegen müssen die Daten nochmal in Java geprüft werden; mehr dazu im Quellcode.
   */
  public boolean pruefLogin(String pUserName, String pPasswort){
    d.executeStatement("SELECT Benutzername, Kennwort FROM Profile WHERE Benutzername LIKE '"+pUserName+"' AND Kennwort LIKE '"+pPasswort+"'");//Die relevanten Daten der User werden aus der Datenbank aufgerufen.
    QueryResult tmp=d.getCurrentQueryResult();
    String[][] erg=tmp.getData();
    if (erg.length>0) {//nur wenn mindestens ein Treffer in der Datenbank gefunden wurde wird die Schleife ausgeführt
      for (int i=0; i<erg.length; i++) {//es kann mehere Treffer geben weil SQL nicht case-sensitive ist
        if (erg[i][0].equals(pUserName)&&erg[i][1].equals(pPasswort)) {//falls ein Datensatz tatsächlich mit der Eingabe übereinstimmt wird true zurückgegeben
          return true;
        }
      }
    }
    return false;//Falls es gar keine Übereinstimmungen gibt oder diese wegen der Groß- und Kleinschreibung flasch sind wir false returned.
  }
  public boolean registrieren(String pUserName, String pPasswort){
    d.executeStatement("SELECT Benutzername FROM Profile WHERE Benutzername LIKE '"+pUserName+"'");
    QueryResult tmp=d.getCurrentQueryResult();
    String[][] erg=tmp.getData();
    if (erg.length>0) { 
      return false;
    }
    d.executeStatement("INSERT INTO Profile (Benutzername, Kennwort, Anzahl_Erstellte_Quiz, Elo, Spiele_Gewonnen, Spiele_Verloren) VALUES ('"+pUserName+"', '"+pPasswort+"', '"+3+"', '"+1000+"' , '"+100+"' , '"+67+"')");
    return true;
  }
  public void eloAnpassen(Spiel pSpiel){
    Spieler[] spieler=pSpiel.getSpieler();
    d.executeStatement("UPDATE Profile SET Elo = '"+spieler[0].getElo()+"'WHERE Benutzername = '"+spieler[0].getName()+"'");
    d.executeStatement("UPDATE Profile SET Elo = '"+spieler[1].getElo()+"'WHERE Benutzername = '"+spieler[1].getName()+"'");
  }
  public int getElo(String pUser){
    d.executeStatement("SELECT Elo FROM Profile WHERE Benutzername='"+pUser+"'");
    QueryResult tmp=d.getCurrentQueryResult();
    String[][] erg=tmp.getData();
    System.out.println(Integer.parseInt(erg[0][0]));
    return Integer.parseInt(erg[0][0]);
  }
  public String statistiken(String pUser){
    String stats="";
    String[] tmp2=new String[10];
    d.executeStatement("SELECT Benutzername, Anzahl_Erstellte_Quiz, Elo, Spiele_Gewonnen, Spiele_Verloren FROM Profile WHERE Benutzername='"+pUser+"'");
    QueryResult tmp=d.getCurrentQueryResult();
    String[][] erg=tmp.getData();
    int prozentGewonnen=0;
    int spieleGesamt=0;
    for (int i=0; i<erg[0].length; i++) {
      System.out.println(erg[0][i]);
    }
    if (erg.length>0) {
      for (int i=0; i<erg.length; i++) {
        if (erg[i][0].equals(pUser)) {
          if (erg[i].length>1) {
            tmp2[0]="Anzahl erstellter Quize: "+erg[i][1];
          }
          if (erg[i].length>2) {
            tmp2[1]="Elo: "+erg[i][2];
          }
          if (erg[i].length>3) {
            tmp2[2]="Spiele gewonnen: "+erg[i][3];
          }
          if (erg[i].length>4) {
            tmp2[3]="Spiele verloren: "+erg[i][4];
          }
          if (erg[i].length>4) {
            spieleGesamt=Integer.parseInt(erg[i][3])+Integer.parseInt(erg[i][4]);
            tmp2[4]="gesamte Spiele: "+String.valueOf(spieleGesamt);
          }
          if (spieleGesamt!=0) {
            prozentGewonnen=(int)((double)Integer.parseInt(erg[i][3])/(double)spieleGesamt*100);
            tmp2[5]="Winrate: "+String.valueOf(prozentGewonnen);
          }
        }
      }
      for (int i=0; i<tmp2.length; i++) {
        if (tmp2[i]!=null) {
          stats+=tmp2[i]+"//";
        }
      }
    }
    System.out.println(stats);
    return stats;
  }
  public void gewonnenVerloren(boolean pGewonnen, String pUser){
    int counter=0;
    if (pGewonnen) {
      d.executeStatement("SELECT Spiele_Gewonnen FROM Profile WHERE Benutzername='"+pUser+"'");
      QueryResult tmp=d.getCurrentQueryResult();
      String[][] erg=tmp.getData();
      counter=Integer.parseInt(erg[0][0])+1;
      d.executeStatement("UPDATE Profile SET Spiele_Gewonnen = '"+counter+"'WHERE Benutzername = '"+pUser+"'");
    }else{
      d.executeStatement("SELECT Spiele_Verloren FROM Profile WHERE Benutzername='"+pUser+"'");
      QueryResult tmp=d.getCurrentQueryResult();
      String[][] erg=tmp.getData();
      counter=Integer.parseInt(erg[0][0])+1;
      d.executeStatement("UPDATE Profile SET Spiele_Verloren = '"+counter+"'WHERE Benutzername = '"+pUser+"'");
    }
  }
  public String getQuizName(int pId){
    d.executeStatement("SELECT Name FROM Quiz WHERE Quiz_ID='"+pId+"'");
    QueryResult tmp=d.getCurrentQueryResult();
    String[][] erg=tmp.getData();
    return erg[0][0];
  }
  public boolean quizIdPruefen(String pId){
    d.executeStatement("SELECT Name FROM Quiz WHERE Quiz_ID='"+pId+"'");
    QueryResult tmp=d.getCurrentQueryResult();
    String[][] erg=tmp.getData();
    if (erg.length==0) {
      return false;
    }else {
      return true; 
    } 
  }
}
