#/bin/sh
# build everything, invoke leiningen and ant

PROJROOT=$(pwd)

# invoke leiningen
echo "build clojure part with leiningen ..."
cd clojure
lein uberjar && mv target/cljrepl-1.0.0-SNAPSHOT-standalone.jar ../java/lib/
echo

if [ $? -ne 0 ] ; then
    echo "build error in leiningen, exit build script!"
    exit -1
fi


# invoke ant
echo "build java part with ant ..."
cd $PROJROOT/java
ant jar

if [ $? -ne 0 ] ; then
    echo "build error in ant, exit build script!"
    exit -1
fi
