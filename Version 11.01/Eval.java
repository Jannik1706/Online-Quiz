public class Eval {
  
  public static Wrap getOb(Object o) {
    return new Wrap(o);
  }
  
  protected static Object getOb(final String s) {
    return new Object() {public String result = s;};
  }
  
  protected static Object getOb(final boolean b) {
    return new Object() {public boolean result = b;};
  }
  
  protected static Object getOb(final byte b) {
    return new Object() {public byte result = b;};
  }
  
  protected static Object getOb(final char c) {
    return new Object() {public char result = c;};
  }
  
  protected static Object getOb(final double d) {
    return new Object() {public double result = d;};
  }
  
  protected static Object getOb(final float f) {
    return new Object() {public float result = f;};
  }
  
  protected static Object getOb(final int i) {
    return new Object() {public int result = i;};
  }
  
  protected static Object getOb(final long l) {
    return new Object() {public long result = l;};
  }
  protected static Object getOb(final short s) {
    return new Object() {public short result = s;};
  }
}
    
    
class Wrap { 
  public Object result; 
  
  Wrap(Object result) { 
    this.result = result; 
  } 
}