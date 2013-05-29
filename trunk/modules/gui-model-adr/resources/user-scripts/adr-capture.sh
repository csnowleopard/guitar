#!/bin/bash

#------------------------
# Sample command line arguments 
SCRIPT_DIR=`dirname $0`
aut_classpath=.

#aut_directory=TippyTipper
#aut_package=net.mandaria.tippytipper
#aut_main=activities.TippyTipper

aut_directory=$1
aut_package=$2
aut_main=$3
aut=$aut_package.$aut_main
port=10737

# configuration for the application
# you can specify widgets to ignore during ripping
# and terminal widgets
# configuration="$aut_dir/guitar-config/configuration.xml"

# intial waiting time
# change this if your application need more time to start
intial_wait=3000

# delay time between two events during ripping 
ripper_delay=500

# the length of test suite
tc_length=2

# delay time between two events during replaying  
# this number is generally smaller than the $ripper_delay
replayer_delay=1000

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

# turn off posix mode
set +o posix

# Preparing output directories
mkdir -p $output_dir
mkdir -p $testcases_dir
mkdir -p $states_dir
mkdir -p $logs_dir
mkdir -p $screenshots_dir

# Install dependencies and android-sdk
./adr-setup.sh

OLD_PATH=$PATH
export PATH="$PATH:$HOME/android-sdk-linux/platform-tools:$HOME/android-sdk-linux/tools"

# Setup and run the emulator
# $1 = aut_directory
# $2 = aut_package
# $3 = aut_main
# $4 = -no-window flag or not
./adr-emu.sh $1 $2 $3 $4

./capture.py

# Converting GUI structure to EFG
echo ""
echo "About to convert GUI structure file to Event Flow Graph (EFG) file" 
#read -p "Press ENTER to continue..."
cmd="$SCRIPT_DIR/gui2efg.sh -g $gui_file -e $efg_file"
echo $cmd
eval $cmd

# Check if user wants to replay all generated test cases
# $1 = aut_directory
# $2 = aut_package
# $3 = aut_main
# $4 = -no-window flag or not
read -p "Generate and replay test cases (Y/n)? " input
shopt -s nocasematch
case "$input" in
  y|yes|'' ) ./adr-replay.sh $1 $2 $3 $4;;
  * ) ;;
esac

# Clean up (kill the emulator if running, delete the emulator image, reset path
pkill emulator
killall emulator
android delete avd -n ADRGuitarTest
export PATH=$OLD_PATH
