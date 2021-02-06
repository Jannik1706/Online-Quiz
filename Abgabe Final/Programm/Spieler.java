/**
 * fügt die Daten eines Spieler aus der Datenabank und des Clients zusammen um damit der Server effektiv auf die Daten zugreifen kann
 * ist ein tmporär Speicher der für jeden Login neu erstellt wird.  
 */
public class Spieler{
  private int elo;
  private String name;
  private String ip;
  private int port;
  private int spiel;
  private int nr;
  private int punkte;
  public Spieler(String pName, String pIp, int pPort, int pElo){
    name=pName;
    ip=pIp;
    port=pPort;
    elo=pElo;
    spiel=0;
    nr=0;
    punkte=0;
  }
  public String getName(){
    return name;
  }
  public String getIp(){
    return ip;
  }
  public int getPort(){
    return port;
  }
  public int getElo(){
    return elo;
  }
  public void setSpiel(int pSpiel){
    spiel=pSpiel;
  }
  public int getSpiel(){
    return spiel;
  }
  public void setNr(int pNr){
    nr=pNr;
  }
  public int getNr(){
    return nr;
  }
  public void setPunkte(int pPunkte){
    punkte=pPunkte;
  }
  public int getPunkte(){
    return punkte;
  }
  public void setElo(int pElo){
    elo=pElo;
  }
}
