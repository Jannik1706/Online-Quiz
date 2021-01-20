public class Spiel{
  private Spieler[] spieler;
  private int fertig;
  private boolean ranked;
  private int nr;
  private int quizId;
  private int code;
  public Spiel(int pSpieler, boolean pRanked, int pNr, int pQuizId){
    spieler=new Spieler[pSpieler];
    fertig=0;
    ranked=pRanked;
    nr=pNr;
    quizId=pQuizId;
    code=0;
  }
  public int spielerhinzufuegen(Spieler pSpieler){
    for (int i=0;i<spieler.length;i++) {
      if (spieler[i]==null) {
        spieler[i]=pSpieler;
        pSpieler.setNr(i);
        pSpieler.setSpiel(nr);
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
  public int getNr(){
    return nr;
  }
  public int getQuizId(){
    return quizId;
  }
  public int getCode(){
    return code;
  }
  public void setCode(int pCode){
    code=pCode;
  }
  public void eloBerechnen(Spieler[] pSpieler){
    double Ra=pSpieler[0].getElo();
    double Rb=pSpieler[1].getElo();
    double Ea=1/(1+Math.pow(10, (Rb-Ra)/400));
    double Eb=1/(1+Math.pow(10, (Ra-Rb)/400));
    double Ra2=Ra+16*(1-Ea);
    double Rb2=Rb+16*(0-Eb);
    pSpieler[0].setElo((int)Ra2);
    pSpieler[1].setElo((int)Rb2);
  }
}   
