#!/bin/bash

#------------------------
# Sample command line arguments
SCRIPT_DIR=`dirname $0`
aut_classpath=.

#aut_directory=TippyTipper
#aut_package=net.mandaria.tippytipper
#aut_main=activities.TippyTipper

aut_directory=$1

# the length of test suite
tc_length=$2

#------------------------
# Output artifacts 
#------------------------

# Directory to store all output of the workflow 
output_dir="data/$aut_directory"

# GUI structure file
gui_file="$output_dir/$aut_directory.GUI"

# EFG file 
efg_file="$output_dir/$aut_directory.EFG"

# Log file for the ripper 
# You can examine this file to get the widget 
# signature to ignore during ripping 
log_file="$output_dir/$aut_directory.log"

# Test case directory  
testcases_dir="$output_dir/testcases"

# GUI states directory  
states_dir="$output_dir/states"

# Replaying log directory 
logs_dir="$output_dir/logs"

# Screenshot directory
screenshots_dir="$output_dir/screenshots"

rip_minute=`awk '/Elapsed:/ { print $7 }' $log_file | sed 's/^0*//'`
rip_second=`awk '/Elapsed:/ { print $9 }' $log_file | sed -n 's/:/ /p' | sed 's/^0*//'`

let rip_second=rip_minute*60+rip_second
let rip_second=rip_second*1000*3

# Generating test cases
echo ""
echo "About to generate test cases to cover all possible $tc_length-way event interactions" 
#read -p "Press ENTER to continue..."
cmd="$SCRIPT_DIR/tc-gen-sq.sh -e $efg_file -l $tc_length -m 0 -d $testcases_dir"
echo $cmd
eval $cmd 

testcase_num=`find $testcases_dir/*.tst | wc -l`

rm -rf `find adr-aut/$aut_directory -name '*.res'`
rm -rf `find adr-aut/$aut_directory -name '*.ec'`
rm -rf `find adr-aut/$aut_directory -name '*.log'`


