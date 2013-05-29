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
aut_path=$4
aut=$aut_package.$aut_main
port=10737

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

#------------------------
# Main workflow 
#------------------------

found=$(which android)
if [ ! $found ]; then
    echo -e "ERROR: You need to run adr-setup.sh first.\n"
	exit -1;
fi

echo -e "==> Kill the emulator process if running.\n"
pkill emulator
killall emulator

echo "==> Delete the AVD if its name is ADRGuitarTestTest."
android delete avd -n ADRGuitarTest
echo -e "\n"

echo "==> Create an AVD. Its name will be ADRGuitarTestTest."
echo | android create avd -n ADRGuitarTest -t android-8 -s WQVGA432
echo -e "\n"

# ADR-Server Building
echo -e "==> Build ADR-Server"
cd adr-aut
rm -f adr-server.apk
cd adr-server

android update project --name adr-server --target android-8 --path .
python rename.py AndroidManifest.xml $aut_package
ant debug
../resign.sh ./bin/adr-server-debug.apk ../adr-server.apk
cd ../../

# AUT Building
echo -e "==> Build AUT"
cur_path=`pwd`
cd $aut_path

# Add the two important permissions to allow for manipulating the emulator
grep -q "INTERNET" "AndroidManifest.xml" || sed -i '/<uses-sdk/a \<uses-permission android:name="android.permission.INTERNET" />' "AndroidManifest.xml"
grep -q "INJECT_EVENTS" "AndroidManifest.xml" || sed -i '/<uses-sdk/a \<uses-permission android:name="android.permission.INJECT_EVENTS" />' "AndroidManifest.xml"

rm -rf build.xml
android update project --target android-8 -p .
cp -rf src src.orig
if [ ! -f ./bin/no_fault/aut-resigned.apk ] || [ ! -f ./bin/no_fault/coverage.em ]; then
    ant instrument
    ../resign.sh ./bin/*-instrumented.apk ./bin/aut-resigned.apk
    mkdir -p ./bin/no_fault
    cp ./bin/aut-resigned.apk ./bin/no_fault
    cp coverage.em ./bin/no_fault
fi

rm -rf src
mv src.orig src
rm -rf bin/*.apk
cd $cur_path

# Run an emulator process
echo -e "==> Run the created emulator.\n"
emulator -avd ADRGuitarTest -cpu-delay 0 -netfast $5 -no-snapshot-save &

# Install ADR-Server
echo "==> Install ADR-Server."
cont=true
while $cont ;
do
    while read line
    do
        found=$(echo $line | grep Success)
        if [ "$found" ]; then
            cont=false
        fi
    done < <( adb install adr-aut/adr-server.apk )

    if $cont ; then
        echo "  The emulator is booting."
        echo "  We will retry."
        adb kill-server
        adb start-server
    fi
    sleep 10
done
echo -e "\n"

apk_file=`find adr-aut/$aut_directory -name *.apk | sed -n '/no_fault/p'`

# Install AUT
echo "==> Install AUT: $apk_file"
cont=true
while $cont ;
do
    while read line
    do
        found=$(echo $line | grep Success)
        if [ "$found" ]; then
            cont=false
        fi
    done < <( adb install $apk_file )

    if $cont ; then
        echo "    The emulator is booting."
        echo "    We will retry."
    fi
    sleep 10
done
echo -e "\n"