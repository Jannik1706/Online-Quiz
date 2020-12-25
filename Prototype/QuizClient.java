public class QuizClient extends Client{
  private GUI gui;
  private List<String> fragen;
  public QuizClient(String pServerIP, int pServerPort, GUI pGUI){
    super(pServerIP, pServerPort);
    gui=pGUI;
    fragen=new List<String>();
  } 
  public void processMessage(String pMessage){
    String[] nachrichtTeil = pMessage.split("::");
    if(nachrichtTeil[0].equals("2")){          
      String[] pFragen=nachrichtTeil[1].split("//");
      for (int i=0;i<pFragen.length;i++) {
        fragen.append(pFragen[i]);
      }
      this.frageLaden();
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
  public void frageAnfordern(){
    this.send("1");
  }
}
