#!/bin/bash

current_dir=`pwd`
output_dir="$current_dir/Demo"
replayer_dir="$output_dir/replayer"
testcases_dir="$output_dir/testcases"
#cov_output_name="codeCoverage.out"

#echo "Enter 0 for statement coverage or 1 for branch coverage"
#read cov_mode
testcase_count=`ls $replayer_dir | wc -l | sed -e 's/^[ \t]*//'`

echo "How many testcases do you want to run? [1-$testcase_count]"
read cov_count

echo -e "\nAbout to generate the code coverage matrix"
read -p "Press ENTER to continue..."

for testcase in `find $testcases_dir -name "*.tst"| head -n$cov_count`
do
    test_name=`basename $testcase`
    test_name=${test_name%.*}
    cov_output_name="$current_dir/$test_name.tmp"
    echo "For testcase '$test_name':"
    
    dir="$replayer_dir/$test_name/gcov"
    cd $dir
    rm -f $cov_output_name
    echo $test_name >> $cov_output_name

    #for file in `find . -name "*.gcno"`
    for file in `find -f $replayer_dir/$test_name | grep gcno`
    do
	file_name=`basename $file .gcno`
	echo $file_name >> $cov_output_name
	echo "Processing file: $file_name.m"
	#gcov -n $file | tail -n3 >> $cov_output_name
#	if [ $cov_mode -eq 0 ]; then
	    str=`gcov -n $file | tail -n2 | head -n1 | sed s/Lines\ executed:// | sed 's/\(%\).*/\1/'`
	    printf "%s | " "${str}" >> $cov_output_name
	    # checking whether there's any branch or not
   	    tmp=`gcov -b $file | tail -n4 | head -n1`
	    case $tmp in
		N*) exist=0 ;;
		*) exist=1 ;;
	    esac
	    # if there's no braches at all, then print out "N/P"
	    if [ $exist -eq 1 ]; then
   		tmp=`gcov -b $file | tail -n5 | head -n1`
		echo $tmp | sed s/Branches\ executed:// | sed 's/\(%\).*/\1/' >> $cov_output_name
	    else
		echo "n/a" >> $cov_output_name
	    fi
#	fi
    done
    
    echo -e "Done.\n"
    cd $current_dir
done

# Running the matrix generator in Python
python ../../../../modules/gui-model-iph/resources/user-scripts/codeCovMatrix.py > ./codeCovMatrix.txt

rm -f *.gcov
rm -f *.tmp
echo "For code coverage matrix, check file: $current_dir/codeCovMatrix.txt"
