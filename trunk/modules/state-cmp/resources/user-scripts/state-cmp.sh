#!/bin/bash
##################################
# GUITAR tc-gen.sh
##################################
function usage {
	echo "Usage: $0 -s <src oracle dir> -d <dst oracle dir>"
}

base_dir=`dirname $0`
guitar_lib=$base_dir/jars

# Main classes 
stateesi_launcher=edu.umd.cs.guitar.state.StateCmp

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

if [ `uname -s | grep -i cygwin | wc -c` -gt 0 ]
then
	classpath=`cygpath -wp $classpath`
fi

STATECMP_CMD="$JAVA_CMD_PREFIX $GUITAR_OPTS -cp $classpath $stateesi_launcher $@"
$STATECMP_CMD
