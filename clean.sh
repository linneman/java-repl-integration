#/bin/sh
# clean everything, invoke leiningen and ant

PROJROOT=$(pwd)

# invoke leiningen
echo "clean clojure part with leiningen ..."
cd clojure
lein clean
echo

if [ $? -ne 0 ] ; then
    echo "clean error in leiningen occured!"
fi


# invoke ant
echo "clean java part with ant ..."
cd $PROJROOT/java
ant clean

if [ $? -ne 0 ] ; then
    echo "build error in ant occured!"
    exit -1
fi
