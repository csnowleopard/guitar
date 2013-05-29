#!/bin/bash
MEDIAWIKI_PATH=/var/www/mediawiki
FAULT_NO=8
for((i=1;i<FAULT_NO;i++))
do
	php $MEDIAWIKI_PATH/maintenance/edit.php -s "Quick edit" -m Fault$i < $MEDIAWIKI_PATH/maintenance/faultpages/fault$i  
#done

# change these following configuration to your test system

#
# web site under test 
#
website="http://localhost/mediawiki/index.php/Fault$i"

#
# test case directory 
#
testcases_dir=TC
statefile_dir=STA$i
#rm -rf $testcases_dir
mkdir $testcases_dir
#rm -rf GUITAR-Default.EFG GUITAR-Default.STA GUITAR-Default.GUI
rm -rf $statefile_dir
#
# number of test cases to run
#
testcase_num=15

# Use clean log
export GUITAR_OPTS="-Dlog4j.configuration=log/guitar-clean.glc"
SCRIPT_DIR=`dirname $0`
pushd $SCRIPT_DIR

# replay
echo "About to replay" 
read -p "Press ENTER to continue..."

SAMPLE_TEST=`find TC -name "*.tst" | tail -n1`

j=1
mkdir $statefile_dir
for testcase in `find $testcases_dir -name "*.tst"| head -n$testcase_num`  
do
	# getting test name 
	test_name=`basename $testcase`
	test_name=${test_name%.*}
	
	./sel-replayer.sh --website-url $website -g GUITAR-Default.GUI -e GUITAR-Default.EFG -t $testcase -g $test_name.orc -d 1 
	mv GUITAR-Default.STA $statefile_dir/GUITAR-Default.STA$j
	let j+=1
done

popd # quit to the current directory
done

let noFaults=FAULT_NO-1
perl testDiff.pl ./STA GUITAR-Default STA $noFaults
