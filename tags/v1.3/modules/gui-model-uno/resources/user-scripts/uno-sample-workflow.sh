#!/bin/bash

# This is a sample script to demonstrate 
# GUITAR general workflow 
# The output can be found in Demo directory  

#------------------------
# Running in script dir 
SCRIPT_DIR=`dirname $0`

#------------------------
# application directory 
aut_dir=$SCRIPT_DIR

# classpath 
for file in `find $SCRIPT_DIR/jars/ -name "*.jar"`
do
    classpath=${file}:${classpath}
done
classpath=$SCRIPT_DIR:$classpath
echo $classpath

# root_window
root_window="Untitled 1 - OpenOffice.org Writer"

#------------------------
# Sample command line arguments 
args=""
jvm_options=""

# configuration for the application
# you can specify widgets to ignore during ripping 
# and terminal widgets 
configuration="$aut_dir/configuration.xml"
data_ignore="$aut_dir/data/ignore/"
echo $configuration

# intial waiting time
# change this if your application need more time to start
intial_wait=1000

# delay time between two events during ripping 
ripper_delay=500

# the length of test suite
tc_length=2

# delay time between two events during replaying  
# this number is generally smaller than the $ripper_delay
relayer_delay=200

#------------------------
# Output artifacts 
#------------------------

# Directory to store all output of the workflow 
output_dir="./OOo"

# GUI structure file
gui_file="$output_dir/OOo.GUI"

# EFG file 
efg_file="$output_dir/OOo.EFG"

# Log file for the ripper 
# You can examine this file to get the widget 
# signature to ignore during ripping 
log_file="$output_dir/OOo.log"

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
cp -r uno-aut/data .
# Ripping
echo ""
echo "About to rip the application " 
read -p "Press ENTER to continue..."
cmd="$SCRIPT_DIR/uno-ripper.sh -cp $classpath -cf $configuration --root-window $root_window --port 5678 -i $data_ignore"

# Adding application arguments if needed 
if [ ! -z $args ] 
then 
	cmd="$cmd -a \"$args\"" 
fi
echo $cmd
eval $cmd

cp $SCRIPT_DIR/GUITAR-Default.GUI $SCRIPT_DIR/OOo/OOo.GUI
# Converting GUI structure to EFG
echo ""
echo "About to convert GUI structure file to Event Flow Graph (EFG) file" 
read -p "Press ENTER to continue..."
cmd="$SCRIPT_DIR/gui2efg.sh $gui_file $efg_file"
echo $cmd
eval $cmd

# Generating test cases
echo ""
echo "About to generate test cases to cover all possible $tc_length-way event interactions" 
read -p "Press ENTER to continue..."
cmd="$SCRIPT_DIR/tc-gen-sq.sh -e $efg_file -l $tc_length -m 0 -d $testcases_dir"
echo $cmd
eval $cmd 

# Replaying generated test cases
echo ""
echo "About to replay test case(s)" 
echo "Enter the number of test case(s): "
read testcase_num

for testcase in `find $testcases_dir -name "*.tst"| head -n$testcase_num`  
do
	# getting test name 
	test_name=`basename $testcase`
	test_name=${test_name%.*}

	cmd="$SCRIPT_DIR/uno-replayer.sh -cp $classpath -g $gui_file -e $efg_file -t $testcase -i $intial_wait -d $relayer_delay -l $logs_dir/$test_name.log -gs $states_dir/$test_name.sta -cf $configuration"

	# adding application arguments if needed 
	if [ ! -z $args ] 
	then 
		cmd="$cmd -a \"$args\" " 
	fi	
	echo $cmd 
	eval $cmd
done
