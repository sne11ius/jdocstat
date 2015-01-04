BASE_DIR=$HOME/Downloads/java-api
EXECUTABLE=./target/jdocstat-0.0.1-SNAPSHOT-jar-with-dependencies.jar
echo Compiling...
mvn clean package assembly:single > /dev/null
echo ... done.
echo Assuming JavaDoc files in $BASE_DIR

echo API version, \# Classes, \# Methods, \# Deprecated Classes, \# Deprecated Methods > api-statistics.csv

for version in V_1_0_2 V_1_1_8
do
	CURRENT_DIR=$BASE_DIR/$version/tree.html
	echo Parsing $CURRENT_DIR...
	java -jar $EXECUTABLE -v $version -f $CURRENT_DIR >> api-statistics.csv
done

for version in V_1_2 V_1_3_1 V_1_4_2 V_1_5 V_1_6 V_1_7 V_1_8
do
	CURRENT_DIR=$BASE_DIR/$version/overview-tree.html
	echo Parsing $CURRENT_DIR...
	java -jar $EXECUTABLE -v $version -f $CURRENT_DIR >> api-statistics.csv
done
