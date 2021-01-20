public class Test{
  public Test(){
    
    }
  public static void main (String args[]){
    double Ra=1000;
    double Rb=1000;
    double Ea=1/(1+Math.pow(10, (Rb-Ra)/400));
    System.out.println(Ea);
    double Eb=1/(1+Math.pow(10, (Ra-Rb)/400));
    System.out.println(Eb);
    double Ra2=Ra+16*(1-Ea);
    double Rb2=Rb+16*(0-Eb);
    System.out.println(Ra2);
    System.out.println(Rb2);
    }
  }
