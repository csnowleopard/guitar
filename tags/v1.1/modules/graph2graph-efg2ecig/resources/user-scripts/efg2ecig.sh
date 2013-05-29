#!/bin/bash

#--------------------------------
# GUITAR efg2ecig.sh
#--------------------------------
function usage {
   echo "Usage: $0 -e <EFG-file> <ECIG data file>"
}

base_dir=`dirname $0`
guitar_lib=$base_dir/jars

# Main classes 
efg2ecig_launcher=edu.umd.cs.guitar.graph.Graph2Graph

for file in `find $guitar_lib/ -name "*.jar"`
do
   guitar_classpath=${file}:${guitar_classpath}
done

# Change GUITAR_OPTS variable to run with the clean log file  
GUITAR_OPTS="$GUITAR_OPTS -Dlog4j.configuration=log/guitar-clean.glc"

if [ -z "$JAVA_CMD_PREFIX" ];
then
   JAVA_CMD_PREFIX="java"
fi

classpath=$guitar_classpath:$base_dir

TCGEN_CMD="$JAVA_CMD_PREFIX $GUITAR_OPTS -cp $classpath $efg2ecig_launcher -p EFG2ECIG $@"
$TCGEN_CMD
