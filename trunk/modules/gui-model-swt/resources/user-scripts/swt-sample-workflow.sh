#!/bin/bash

# This is a sample script to demonstrate 
# GUITAR general workflow 
# The output can be found in Demo directory  

#------------------------
# Running in script dir 
SCRIPT_DIR=`dirname $0`

#------------------------
# application directory 
aut_dir=$SCRIPT_DIR/swt-aut/RadioButton

# application classpath 
aut_classpath=$aut_dir/bin

# application main class
mainclass="SWTRadioButton"

#------------------------
# Sample command line arguments 
args=""
jvm_options=""

# configuration for the application
# you can specify widgets to ignore during ripping 
# and terminal widgets 
configuration="$aut_dir/guitar-config/configuration.xml"

# initial waiting time
# change this if your application need more time to start
initial_wait=1000

# delay time between two events during ripping 
#ripper_delay=500

# the length of test suite
tc_length=2

# delay time between two events during replaying  
# this number is generally smaller than the $ripper_delay
#relayer_delay=200

#------------------------
# Output artifacts 
#------------------------

# Directory to store all output of the workflow 
output_dir="./Demo"

# GUI structure file
gui_file="$output_dir/Demo.GUI"

# EFG file 
efg_file="$output_dir/Demo.EFG"

# Log file for the ripper 
# You can examine this file to get the widget 
# signature to ignore during ripping 
log_file="$output_dir/Demo.log"

# Test case directory  
testcases_dir="$output_dir/testcases"

# GUI states directory  
states_dir="$output_dir/states"

# Replaying log directory 
logs_dir="$output_dir/logs"

#------------------------
# Main workflow 
#------------------------

# Preparing output directories
mkdir -p $output_dir
mkdir -p $testcases_dir
mkdir -p $states_dir
mkdir -p $logs_dir

# Ripping
echo ""
echo "About to rip the application " 
#read -p "Press ENTER to continue..."
cmd="$SCRIPT_DIR/swt-ripper.sh -cp $aut_classpath -c $mainclass -g  $gui_file -cf $configuration -i $initial_wait -l $log_file"

# Adding application arguments if needed 
if [ ! -z $args ] 
then 
	cmd="$cmd -a \"$args\"" 
fi
echo $cmd
eval $cmd

# Converting GUI structure to EFG
echo ""
echo "About to convert GUI structure file to Event Flow Graph (EFG) file" 
#read -p "Press ENTER to continue..."
cmd="$SCRIPT_DIR/gui2efg.sh -g $gui_file -e $efg_file"
echo $cmd
eval $cmd

# Generating test cases
echo ""
echo "About to generate test cases to cover all possible $tc_length-way event interactions" 
#read -p "Press ENTER to continue..."
cmd="$SCRIPT_DIR/tc-gen-sq.sh -e $efg_file -l $tc_length -m 10 -d $testcases_dir"
echo $cmd
eval $cmd 

# Replaying generated test cases
echo ""
echo "About to replay test case(s)" 
echo "Enter the number of test case(s): "
#read testcase_num
testcase_num=2


for testcase in `find $testcases_dir -name "*.tst"| head -n$testcase_num`  
do
	# getting test name 
	test_name=`basename $testcase`
	test_name=${test_name%.*}

	cmd="$SCRIPT_DIR/swt-replayer.sh -cp $aut_classpath -c  $mainclass -g $gui_file -e $efg_file -t $testcase -i $initial_wait -l $logs_dir/$test_name.log -gs $states_dir/$test_name.sta -cf $configuration"

	# adding application arguments if needed 
	if [ ! -z $args ] 
	then 
		cmd="$cmd -a \"$args\" " 
	fi	
	echo $cmd 
	eval $cmd
done


