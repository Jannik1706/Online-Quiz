/**
 * Dient als Speicherort für die Spieler, Ergenisse und sonstige Information.
 * Wird auch als Verteiler für das Chatsystem benutzt
 */
public class Spiel{
  private Spieler[] spieler;
  private int fertig;
  private boolean ranked;
  private int nr;
  private int quizId;
  private int code;
  private Spieler host;
  private boolean privat;
  private boolean spielt;
  public Spiel(int pSpieler, boolean pRanked, int pNr, int pQuizId, Spieler pHost, boolean pPrivat){
    spieler=new Spieler[pSpieler];
    fertig=0;
    ranked=pRanked;
    nr=pNr;
    quizId=pQuizId;
    code=0;
    host=pHost;
    privat=pPrivat;
    spielt=false;
  }
  public boolean spielerEntfernen(int pSpieler){
    spieler[pSpieler]=null;
    for (int i=0;i<spieler.length;i++) {
      if (spieler[i]!=null) {
        return false;
      }
    }
    return true;
  }
  /**
  * der Spieler wird hinzugefügt und dem Spieler wird seine Nr im Spiel und die Spiele Nr übergeben
  * damit der Server darauf zugreifen kann
  */
  public int spielerhinzufuegen(Spieler pSpieler){
    for (int i=0;i<spieler.length;i++) {
      if (spieler[i]==null) {
        spieler[i]=pSpieler;
        pSpieler.setNr(i);//stelle im Array Spieler
        pSpieler.setSpiel(nr);//Nr des Spiels
        return i;
      }
    }
    return -1;
  }
  public boolean spielVoll(){
    for (int i=0;i<spieler.length;i++) {
      if (spieler[i]==null) {
        return false;
      }
    }
    return true;
  }
  public Spieler[] getSpieler(){
    return spieler;
  }
  public void punkteGeben(int pSpieler, int pPunkte){
    spieler[pSpieler].setPunkte(pPunkte);
    fertig++;
  }
  public Spieler[] gewinner(){
    if (fertig==spieler.length) {
      if (ranked) {
        this.eloBerechnen(this.sort());
      }
      return sort();
    }
    return null;
  }
  public Spieler[] sort(){
    Spieler[] erg=spieler;
    Spieler tmp=null;
    boolean ver =true;
    while (ver) {
      ver=false; 
      for (int i=0;i+1<erg.length;i++ ) {
        if (erg[i].getPunkte()<erg[i+1].getPunkte()) {
          tmp=erg[i];
          erg[i]=erg[i+1];
          erg[i+1]=tmp;
          ver=true;
        }
      }
    }
    return erg;
  }
  public boolean getRanked(){
    return ranked;
  }
  public boolean getSpielt(){
    return spielt;
  }
  public void setSpielt(boolean pSpiel){
    spielt=pSpiel;
  }
  public int getNr(){
    return nr;
  }
  public int getQuizId(){
    return quizId;
  }
  public void setQuizId(int pQuizId){
    quizId=pQuizId;
  }
  public int getCode(){
    return code;
  }
  public void setCode(int pCode){
    code=pCode;
  }
  /**
   * Die Elo Punkte sollen das Können eines Spielers mathematisch ausdrücken.
   * Die Elo wird nach jedem ranked Spiel angepasst.
   * Das Array pSpieler ist sotiert mit dem Gewinner an der 0 Stelle und der verlierer an Stelle 1
   */
  public void eloBerechnen(Spieler[] pSpieler){
    //die aktuelle Elo der beiden Spieler wird geladen
    double Ra=pSpieler[0].getElo();
    double Rb=pSpieler[1].getElo();
    //die Wahrscheinlichkeit für den Sieg der Spieler A oder B wird brechnet
    double Ea=1/(1+Math.pow(10, (Rb-Ra)/400));
    double Eb=1/(1+Math.pow(10, (Ra-Rb)/400));
    // die neue Elo der Spieler wird brechnet
    int Ra2=(int)Math.round(Ra+16*(1-Ea));//Von dem Ergebnis wird die WK abgezogen 1 bedeut Sieg und 0 Niederlage
    int Rb2=(int)Math.round((Rb+16*(0-Eb)));//die 16 dient bestimmt wie viel Elo pro Spiel verteilt wird, wenn der Faktor höher ist werden pro Spiel mehr Punkte addiert oder subtrahiert
    //die neue Elo wird gespeichert
    pSpieler[0].setElo((int)Ra2);
    pSpieler[1].setElo((int)Rb2);
  }
  public boolean getPrivat(){
    return privat;
  }
  public Spieler getHost(){
    return host;
  }
}   
