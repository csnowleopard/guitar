#!/bin/bash

# Command line argument $1 = path to the open source app directory (i.e. ../AUT/LotsoIntents16Activities), $2 = app name (no spaces)
# i.e. TippyTipper, $1 = ../guitar/example-aut/adr-aut/TippyTipper, $2 = TippyTipper

# Example: sh ./IntentFinder-workflow.sh ../guitar/example-aut/adr-aut/LotsoIntents16Activities Lotso16
# Example: sh ./IntentFinder-workflow.sh ../guitar/example-aut/adr-aut/LotsoIntents16ActivitiesPlus Lotso16Plus
# Example: sh ./IntentFinder-workflow.sh ../AUT/LotsoIntentsImplicit LotsoImplicit
# Example: sh ./IntentFinder-workflow.sh ../guitar/example-aut/adr-aut/TippyTipper TippyTipper
# Example: sh ./IntentFinder-workflow.sh ../guitar/example-aut/adr-aut/ToDoManager ToDoManager
# Example: sh ./IntentFinder-workflow.sh ../guitar/example-aut/adr-aut/ContactManager ContactManager
# Example: sh ./IntentFinder-workflow.sh ../guitar/example-aut/adr-aut/HelloAUT HelloAUT

#compile and set classpath
javac -cp lib/gson-1.7.1.jar src/*.java
export CLASSPATH=./src:./lib/gson-1.7.1.jar

# parse the app's source code for explicit intents
#java ExplicitIntent $1/$2/*.java

find $1/src -name *.java > files.txt
javaFiles=""

while read line
do
    javaFiles="$javaFiles $line "
    #echo $line
done < files.txt

java ExplicitIntent $javaFiles

# move out.xt to ../IntentOutputs
mv out.txt ../IntentOutputs

# copy the AndroidManifest.xml into src folder 
cp $1/AndroidManifest.xml ./src

cd src

# parse the app's AndroidManifest.xml 
ruby ParseManifest.rb AndroidManifest.xml

# move parsedManifest.txt to IntentOutputs folder
mv parsedManifest.txt ../../IntentOutputs

cd ..

# get all possible sequences of intents
#java TestCaseGeneratorMain
java TestCaseGeneratorMain ../IntentOutputs/out.txt ../IntentOutputs/parsedManifest.txt ../IntentOutputs/sequence.txt

# clean up the folder
cd src
rm AndroidManifest.xml
cd ..

rm files.txt
#rm ../IntentOutputs/out.txt 
#rm ../IntentOutputs/parsedManifest.txt 

#rename out.txt
mv ../IntentOutputs/out.txt ../IntentOutputs/out_$2.txt

#rename parsedManifest.txt
mv ../IntentOutputs/parsedManifest.txt ../IntentOutputs/parsedManifest_$2.txt

#mv ../IntentOutputs/sequence.txt ../IntentOutputs/sequence_$3.txt
mv ../IntentOutputs/sequence.txt ../IntentOutputs/sequence_$2.txt
