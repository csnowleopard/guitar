#!/bin/bash

#----------------------------------------------------------------------------------------------------
# Run GUITAR Ripper
#---------------------------------------------------------------------------------------------------- 


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

args=""

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

# turn off posix mode
set +o posix

# Preparing output directories
mkdir -p $output_dir
mkdir -p $testcases_dir
mkdir -p $states_dir
mkdir -p $logs_dir

# Ripping
found=$(which android)
if [ $found ]; then
    echo -e "==> You already set up the Android SDK youself so we will not try to install.\n"
fi

if [ $found ]; then
    echo -e "==> Android SDK Exists. We skip the installation.\n"
else
    if [ -f $HOME/android-sdk-linux/tools/android ]; then
        echo -e "==> Android SDK detected.\n"
    else
        echo -e "==> Download Android SDK"
        downloader=$(which wget)
        if [ $downloader ]; then
            wget http://dl.google.com/android/android-sdk_r15-linux.tgz
        else
            curl http://dl.google.com/android/android-sdk_r15-linux.tgz > android-sdk_r15-linux.tgz
        fi

        cp android-sdk_r15-linux.tgz $HOME
        rm android-sdk_r15-linux.tgz
        cd $HOME

        echo -e "==> Uncompress Android SDK"
        tar xvfz android-sdk_r15-linux.tgz
    fi
    tools_path="$HOME/android-sdk-linux/tools:$HOME/android-sdk-linux/platform-tools"
    original_PATH=${PATH}
    export PATH=${PATH}:${tools_path}
    android update sdk --no-ui -t tool
    android update sdk --no-ui -t platform-tool
    android update sdk --no-ui -t platform
fi

echo -e "==> Kill the emulator process if running.\n"
pkill emulator-arm
killall emulator-arm

echo "==> Delete the AVD if its name is ADRGuitarTest."
android delete avd -n ADRGuitarTest
echo -e "\n"

echo "==> Create an AVD. Its name will be ADRGuitarTest."
id=100
if [[ `android list targets | grep "android-8"` =~ [0-9]+ ]]; then
    id=${BASH_REMATCH[0]}
fi

if [[ id -ne 100 ]]; then
    echo | android create avd -n ADRGuitarTest -t $id -s WQVGA432
else
    echo "    Android 2.2 is not installed."
    echo "    Quit ADRGuitar."
    exit
fi
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
cd adr-aut/$aut_directory

rm build.xml
android update project --target android-8 -p .
cp -rf src src.orig
if [ ! -f ./bin/no_fault/aut-resigned.apk ] || [ ! -f ./bin/no_fault/coverage.em ]; then
    ant instrument
    ../resign.sh ./bin/*-instrumented.apk ./bin/aut-resigned.apk
    mkdir -p ./bin/no_fault
    cp ./bin/aut-resigned.apk ./bin/no_fault
    cp coverage.em ./bin/no_fault
fi

<<faults
while read line
do
    filename=${line##*/}
    pathname=${line%/*}
    original_path=`find src -name $filename`
    if [ ! -f ./bin/$pathname/aut-resigned.apk ] || [ ! -f ./bin/$pathname/coverage.em ]; then
        echo "==> Fault seeded source file: $line"
        cp $line $original_path
        ant instrument
        ../resign.sh ./bin/*-instrumented.apk ./bin/aut-resigned.apk
        mkdir -p ./bin/$pathname
        cp ./bin/aut-resigned.apk ./bin/$pathname
        cp coverage.em ./bin/$pathname
    fi
done < <( find $4 -name *.java )
faults

rm -rf src
mv src.orig src
rm -rf bin/*.apk
cd ../../

# Run an emulator process
echo -e "==> Run the created emulator.\n"
xvfb-run -a emulator -avd ADRGuitarTest -cpu-delay 0 -netfast $5 -no-snapshot-save &

touch debug.txt
adb logcat > debug.txt &

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
#    done < <( adb install adr-server/adr-server.apk )
    done < <( adb install adr-aut/adr-server.apk )

    if $cont ; then
        echo "  The emulator is booting."
        echo "  We will retry."
        adb kill-server
	adb devices
        adb start-server
    fi
    sleep 10
done
echo -e "\n"

touch debug.txt
adb logcat > debug.txt &

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
	adb devices
        echo "    We will retry."
    fi
    sleep 10
done
echo -e "\n"

touch debug.txt
adb logcat > debug.txt &
echo "About to rip the application " 
#read -p "Press ENTER to continue..."
cmd="$SCRIPT_DIR/adr-ripper.sh -cp $aut_classpath -c $aut -pt $port -g $gui_file -l $log_file -m"

# Adding application arguments if needed 
if [ ! -z $args ] 
then 
    cmd="$cmd -a \"$args\"" 
fi
echo $cmd
eval $cmd

rip_minute=`awk '/Elapsed:/ { print $7 }' $log_file | sed 's/^0*//'`
rip_second=`awk '/Elapsed:/ { print $9 }' $log_file | sed -n 's/:/ /p' | sed 's/^0*//'`

let rip_second=rip_minute*60+rip_second
let rip_second=rip_second*1000*3

# Converting GUI structure to EFG
echo ""
echo "About to convert GUI structure file to Event Flow Graph (EFG) file" 
#read -p "Press ENTER to continue..."
cmd="$SCRIPT_DIR/gui2efg.sh -g $gui_file -e $efg_file"
echo $cmd
eval $cmd


#----------------------------------------------------------------------------------------------------
# Run IntentFinder
#----------------------------------------------------------------------------------------------------

# Assume that all AUTs to be tested will be in guitar/example-aut/adr-aut

#compile and set classpath
#javac -cp ../../../IntentFinder/lib/gson-1.7.1.jar ../../../IntentFinder/src/*.java
#export CLASSPATH=../../../IntentFinder/src:../../../IntentFinder/lib/gson-1.7.1.jar

#changed directories so that IntentFinder pointed to testcase-generator-intents

javac -cp ../../lib/platforms/adr/gson-1.7.1.jar testcase-generator-intents/src/*.java
export CLASSPATH=testcase-generator-intents/src:../../lib/platforms/adr/gson-1.7.1.jar

# parse the app's source code for explicit intents
#java ExplicitIntent $1/$2/*.java

#find $1/src -name *.java > files.txt
find ../../example-aut/adr-aut/$1/src -name *.java > files.txt
javaFiles=""

while read line
do
    javaFiles="$javaFiles $line "
    #echo $line
done < files.txt

rm files.txt

java ExplicitIntent $javaFiles

mkdir ../../../IntentOutputs
# move out.txt to ../../../IntentOutputs/out.txt
mv out.txt ../../../IntentOutputs

# copy the AndroidManifest.xml into src folder 
cp ../../example-aut/adr-aut/$1/AndroidManifest.xml testcase-generator-intents/src

cd testcase-generator-intents/src

# parse the app's AndroidManifest.xml 
ruby ParseManifest.rb AndroidManifest.xml

# move parsedManifest.txt to IntentOutputs folder
mv parsedManifest.txt ../../../../../IntentOutputs   #now we're in androidintents-code
 
cd ../../

# get all possible sequences of intents
#java TestCaseGeneratorMain
java TestCaseGeneratorMain ../../../IntentOutputs/out.txt ../../../IntentOutputs/parsedManifest.txt ../../../IntentOutputs/sequence.txt

# clean up the folder
rm testcase-generator-intents/src/AndroidManifest.xml

rm ../../../IntentOutputs/out.txt 
rm ../../../IntentOutputs/parsedManifest.txt 
#mv ../IntentOutputs/sequence.txt ../IntentOutputs/sequence_$3.txt
mv ../../../IntentOutputs/sequence.txt ../../../IntentOutputs/sequence_$1.txt

#----------------------------------------------------------------------------------------------------
# Create GUITAR testcases (.tst files) based on IntentFinder output and Demo.GUI (from the Ripper)
#----------------------------------------------------------------------------------------------------

# go to IntentFinder folder
cd testcase-generator-intents

# copy Demo.GUI to this folder
cp ../Demo/Demo.GUI ./Demo.GUI

# parse Demo.GUI and generate .tst files
ruby tstGenerator.rb ../../../../IntentOutputs/sequence_$1.txt    #don't forget to fix the IntentOutputs later

# move the .tst files to guitar/dist/guitar/Demo/testcases folder
mv ./*.tst ../Demo/testcases

# remove Demo.GUI from this folder
rm Demo.GUI

# go back to where we were
cd ..


#----------------------------------------------------------------------------------------------------
# Replay testcases
#----------------------------------------------------------------------------------------------------

# Replaying generated test cases
#echo ""
echo "About to replay test case(s)" 
#echo "Enter the number of test case(s): $testcase_num"
#echo "Enter the testcase you want to test!  Then press [enter]: "
#read testcase
#testcase=./Demo/testcases/$testcase
#echo "You have chosen:  $testcase"
#rip_second=3000

#testcase_num=`find Demo/testcases/*.tst | wc -l`
#testcase_num=2

#testcase_num
for testcase in `find $testcases_dir -name "*.tst"`  
do
    # getting test name 
    test_name=`basename $testcase`
    test_name=${test_name%.*}
    
    
    echo "Remember that in this script our goal is to re-run all the test cases"
    rm -rf $logs_dir/$test_name.log

    cmd="$SCRIPT_DIR/adr-replayer.sh -cp $aut_classpath -c $aut -pt $port -g $gui_file -e $efg_file -t $testcase -i $intial_wait -d $replayer_delay -to $rip_second -so $rip_second -l $logs_dir/$test_name.log -gs $states_dir/$test_name.sta"

    # adding application arguments if needed 
    if [ ! -z $args ] 
    then 
        cmd="$cmd -a \"$args\" " 
    fi

    if [ -f adr-aut/$aut_directory/bin/no_fault/$test_name.res ]; then
        echo "This testcase was already run so we will skip"
    else

	 #create new name based on the test being done
	    ls
	    echo "REACHED!!!!!!!!!!!!!!!!!!!"
	    [[ -d logs ]] && echo "bla" || mkdir logs
	    dumpName="dump"
	    txt=".txt"
	    newName=logs/${dumpName}_${test_name}${txt}
	    [[ -f "$newName" ]] && rm "$newName" || echo "making new file"
	    touch $newName
	    adb logcat -v time > $newName &

        echo $cmd 
        eval $cmd

#taken out just in case
<<COVERAGE
        while [ ! -f adr-aut/$aut_directory/bin/no_fault/$test_name.ec ]
        do
            adb pull /data/data/$aut_package/files/coverage.ec adr-aut/$aut_directory/bin/no_fault/$test_name.ec
            sleep 2
        done

        emma_dir=`which android`
        emma_dir=${emma_dir%/*}
        emma_dir=${emma_dir}/lib/emma.jar
        java -cp $emma_dir emma report -r xml -in adr-aut/$aut_directory/bin/no_fault/coverage.em,adr-aut/$aut_directory/bin/no_fault/$test_name.ec
        java -cp $emma_dir emma report -r html -in adr-aut/$aut_directory/bin/no_fault/coverage.em,adr-aut/$aut_directory/bin/no_fault/$test_name.ec
        mv coverage $test_name
        rm -rf adr-aut/$aut_directory/bin/no_fault/$test_name
        mv -f $test_name -t adr-aut/$aut_directory/bin/no_fault/
        mv coverage.xml adr-aut/$aut_directory/bin/no_fault/$test_name/
        cp ${logs_dir}/${test_name}.log adr-aut/$aut_directory/bin/no_fault/
        cp $states_dir/$test_name.sta adr-aut/$aut_directory/bin/no_fault/

	##########################################################################################
COVERAGE
	    #adding parser here, after the test on the emulator has been
	    #successfully run
	    # added ARGV[1] to parser (application name)
	    app_name=""
	    if [ "$aut_directory" = "CountdownTimer" ]; then
		app_name="Countdown"
	    elif [ "$aut_directory" = "HelloAUT" ]; then
		app_name="com.aut"
	    else
	    	app_name="$aut_directory"
	    fi

	    ruby parser.rb $newName $app_name	#added $app_name
	    #ruby parser.rb $newName

	    #filteredLog
	    filtered=filteredLog_${test_name}.txt
	    touch $filtered
	    egrep -i "intent|edu.umd.cs.guitar|\/$app_name" $newName > $filtered
	    
	    #rename output to masterLog + test_name
	    newName=logs/masterLog_${test_name}.txt
	    cp masterLog.txt $newName
	    rm masterLog.txt
	    #cp $newName ../../../$newName
    fi
done

#copy the masterlogs into the IntentsOutputs folder
mkdir ../../../IntentOutputs/IntentsAnalyzer
cp testcase-generator-intents/resources/IntentsAnalyzer/*.rb ../../../IntentOutputs/IntentsAnalyzer

cp -r logs/masterLog_*.txt ../../../IntentOutputs

python cv_rpt.py adr-aut/$aut_directory/bin/no_fault
python ft_rpt.py adr-aut/$aut_directory/bin/result
cp -rf *.html adr-aut/$aut_directory

pkill emulator-arm
killall emulator-arm
android delete avd -n ADRGuitarTest
export PATH=${original_PATH}

cd ../../../IntentsOutputs/IntentsAnalyzer

mkdir OUTPUT

echo "You can now run the IntentsAnalyzer by going into the IntentsOutputs folder, and then following the directions on the IntentsWiki"
