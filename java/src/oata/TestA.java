package oata;

public class TestA {

  static TestA testA = null;

  int a;
  int b;

  private TestA()
    {
      a = 42;
      b = 43;
    }

  public static TestA getInstance()
  {
    if( testA == null )
      testA = new TestA();

    return testA;
  }

  public void seta(int ia) {
    a = ia;
  }

  public int geta() {
    return a;
  }

  public void setb(int ib) {
    b = ib;
  }

  public int getb() {
    return b;
  }
}
