#/bin/sh
# illustration how to start the java application
# including some class path foo
# everything needs be compiled first by invoking build

cd java
java -classpath lib/cljrepl-1.0.0-SNAPSHOT-standalone.jar:build/jar/HelloWorld.jar oata.HelloWorld
