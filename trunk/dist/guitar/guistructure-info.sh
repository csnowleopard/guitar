#!/bin/bash
#
#  Copyright (c) 2009-@year@. The  GUITAR group  at the University of
#  Maryland. Names of owners of this group may be obtained by sending
#  an e-mail to atif@cs.umd.edu
#
#  Permission is hereby granted, free of charge, to any person obtaining
#  a copy of this software and associated documentation files
#  (the "Software"), to deal in the Software without restriction,
#  including without limitation  the rights to use, copy, modify, merge,
#  publish,  distribute, sublicense, and/or sell copies of the Software,
#  and to  permit persons  to whom  the Software  is furnished to do so,
#  subject to the following conditions:
#
#  The above copyright notice and this permission notice shall be included
#  in all copies or substantial portions of the Software.
#
#  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
#  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
#  MERCHANTABILITY,  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
#  IN NO  EVENT SHALL THE  AUTHORS OR COPYRIGHT  HOLDERS BE LIABLE FOR ANY
#  CLAIM, DAMAGES OR  OTHER LIABILITY,  WHETHER IN AN  ACTION OF CONTRACT,
#  TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
#  SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
#

####################################################
# Write GUIStructure info to standard output
#
#	By	baonn@cs.umd.edu
#	Date: 	06/08/2011
####################################################


function usage {
	echo "Usage: `basename $0` <gui file> <show leaf>"
}

base_dir=`dirname $0`
guitar_lib=$base_dir/jars

# Parameter check
if [ $# -lt 2 ];
then
   usage
   exit 1
fi

guifilename=$1
showleaf=$2

# Main classes 
guistructure_class=edu.umd.cs.guitar.util.GUIStructureInfo

# Add GUITAR classpath
for file in `find $guitar_lib/ -name "*.jar"`
do
    guitar_classpath=${file}:${guitar_classpath}
done

# Change GUITAR_OPTS variable to run with the clean log file  
GUITAR_OPTS="$GUITAR_OPTS -Dlog4j.configuration=log/guitar-clean.glc"

if [ -z "$JAVA_CMD_PREFIX" ];
then
    # Run with clean log file 
    JAVA_CMD_PREFIX="java"
fi

classpath=$base_dir:$guitar_classpath

CMD="$JAVA_CMD_PREFIX $GUITAR_OPTS -cp $classpath $guistructure_class $guifilename $showleaf"
exec $CMD
exit $?
