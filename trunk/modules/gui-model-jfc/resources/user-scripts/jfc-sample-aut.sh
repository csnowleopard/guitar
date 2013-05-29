#!/bin/bash

# This is a sample script to demonstrate 
# the GUITAR general tesing workflow 
# The output can be found in Demo directory  

#------------------------
# Running in script dir 
SCRIPT_DIR=`dirname $0`

#------------------------
# application directory 
aut_dir=$SCRIPT_DIR/jfc-aut/RadioButton/

# application classpath 
aut_classpath=$aut_dir/bin

# application main class
mainclass="Project"

java -cp $aut_classpath $mainclass
