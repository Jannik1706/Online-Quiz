/**
 * startet den Server 
 * wir haben uns gegen eine .jar Datei entschieden weil wir dann nicht mehr system.printout benutzen k�nnen
 */
public class StartServer{
  public StartServer(){
    
  }                      
  public static void main (String args[]){
    QuizServer s=new QuizServer(50000);
  }
}                                                                     
