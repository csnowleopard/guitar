#!/bin/bash

android update project -p .
cp -rf src src.orig
ant instrument
ruby ../resign.sh ./bin/*-instrumented.apk ./bin/aut-resigned.apk
mkdir -p ./bin/no_fault
cp ./bin/aut-resigned.apk ./bin/no_fault
cp coverage.em ./bin/no_fault
while read line
do
	filename=${line##*/}
	pathname=${line%/*}
	original_path=`find src -name $filename`
	echo "==> Fault seeded source file: $line"
	cp $line $original_path
	ant instrument
	ruby ../resign.sh ./bin/*-instrumented.apk ./bin/aut-resigned.apk
	mkdir -p ./bin/$pathname
	cp ./bin/aut-resigned.apk ./bin/$pathname
	cp coverage.em ./bin/$pathname
done < <( find result -name *.java )

rm -rf src
mv src.orig src
rm bin/*.apk
rm *.em
