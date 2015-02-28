package oata;

import cljrepl.main;

public class HelloWorld {
    public static void main(String[] args) {
        System.out.println("Hello World");

        TestA myTestA = TestA.getInstance();

        cljrepl.main.main( new String[] { "clojure/test.clj" } );
        // cljrepl.main.main( null );

        System.out.println("---- continue with java code ----");
        System.out.println("after Clojure invocation have myTestA.a: "+myTestA.geta() );
        System.out.println("after Clojure invocation have myTestA.b: "+myTestA.getb() );
    }
}
