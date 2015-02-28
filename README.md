# Integration of a Clojure REPL into a Java ANT Project

This code snippet illustrates how to integrate Clojure's [read eval print loop
(repl)](http://en.m.wikipedia.org/wiki/REPL) within an [Ant](http://ant.apache.org) based Java
application. The approach allows to advance to a state of the art functional programming style and
to introspect and manipulate a Java application while it is running.

The Clojure libraries including application specific implemention is managed by Clojure's build
system [Leiningen](http://leiningen.org) which generates a single jar archive file. This so called
'uberjar' stand-alone java archive is afterwords published to the classpath.

## Prerequesites

Beneith the JDK and ant you need the clojure build tool leinignen for compilation. Download
the lein script file from Github

    $ cd ~/bin
    $ wget http://github.com/technomancy/leiningen/raw/stable/bin/lein
    $ chmod +x lein

and type

    $ lein self-install

## Build

To build the complete demo just invoke the script 'build.sh'

   $ ./build.sh

or in order to build the Clojure and the Java part separately enter

   $ cd clojure
   $ lein uberjar
   $ mv target/cljrepl-1.0.0-SNAPSHOT-standalone.jar ../java/lib/

   $ cd ../java
   $ ant jar

## Setting up the Classpath and Startup of the Application

This example requires to setup the classpath and invoke the main class explicitly:

   $ cd java
   $ java -classpath lib/cljrepl-1.0.0-SNAPSHOT-standalone.jar:build/jar/HelloWorld.jar oata.HelloWorld

##  REPL Based Web Development
The so called Read-Eval-Print-Loop, in short REPL, is a very powerful way of changing Clojure code
while its is executed. This is in fact one of most powerful concepts in software development and
allows to try and apply changes from the program editor directly while a program is still
running. You can connect your favorite editor to both, the client side running compiled
Clojurescript code within the Webbrowser and the server side running Clojure code on top of the
JVM. We give a very brief summary of the required steps to do this in the following. You a required
to have Emacs installed on your machine and to setup it appropriately. You will need the Emacs
extensions [cider](https://github.com/clojure-emacs/cider), [clojure-mode](https://github.com/technomancy/clojure-mode).
Alternatively you can clone the Emacs setup which was used for the most of the development in this
project from [Github](https://github.com/linneman/emacs-setup).

* start the Java application as explained above
* open up a Clojure file
* e.g. clojure/src/cljrepl/main.clj
* connect slime with meta-x followed by "cider-connect"
* enter 'localhost' for the server and port number 7888 in the command line dialogue afterwords
* emacs should show two buffers now - 'main.clj' and the 'cider-repl'. If not setup the frames manually. Refer to the emacs documentation how to do this.
* change the namespace to main.clj by hitting ctrl-x meta-n. Set 'main.clj' as active buffer first
* you can now evaluate the Clojure expressions in core.clj the cursor after the expression and pressing crtl-x-e
* More information is available on the [cider project](https://github.com/clojure-emacs/cider)

## Licence
This implementation code stands under the terms of the
[Eclipse Public License -v 1.0](http://opensource.org/licenses/eclipse-1.0.txt), the same as Clojure.

February 2015, Otto Linnemann

## Resources and links
Thanks to all the giants whose shoulders we stand on. And the giants theses giants stand on...
And special thanks to Rich Hickey (and the team) for Clojure. Really, thanks!

* Clojure: http://clojure.org
* Leiningen: https://github.com/technomancy/leiningen
