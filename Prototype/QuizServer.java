public class QuizServer extends Server{
  public QuizServer(int pPort) {
    super(pPort);
  }
  public void processNewConnection(String pClientIP, int pClientPort){
    this.send(pClientIP, pClientPort, "Willkommen!");
  }  
  public void processMessage(String pClientIP, int pClientPort, String pMessage){
    String[] nachrichtTeil = pMessage.split(":");
    if(nachrichtTeil[0].equals("1")){           
      this.send(pClientIP, pClientPort, "2::1+1=?--1--2--3--4--2//1+3=?--1--2--3--4--4");
    }
  }
  public void processClosingConnection (String pClientIP, int pClientPort){
    this.send(pClientIP, pClientPort, "Auf Wiedersehen!");
  }
}
