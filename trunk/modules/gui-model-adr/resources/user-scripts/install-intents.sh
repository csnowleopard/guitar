#!/bin/bash


# Step 1: Install Dependencies
#XVFB
sudo apt-get install xvfb
#ANT
sudo apt-get install ant
#SVN/subversion
sudo apt-get install subversion
#java SDK
sudo add-apt-repository "deb http://archive.canonical.com/ lucid partner"
sudo apt-get update

sudo apt-get install sun-java6-jre sun-java6-jdk
#if you get an error about java directories, change echo "0" to echo "2"
echo "2" | sudo update-alternatives --config java
#sudo update-alternatives --config java

#python
sudo apt-get install python

#rubygems and ruby -DO NOT SUDO APT-GETs

#Graphvis dependencies
   #graphViz
tar -zxvf graphviz.tar.gz
cd graphviz-2.26.3
./configure --prefix=/usr &&
make
sudo make install
cd ..

sudo gem install ruby-graphviz

   #glib
sudo apt-get install libglib2.0-dev libnetpbm10-dev m4 libproj-dev \
libgsl0-dev libnetcdf-dev libode-dev libfftw3-dev libgtkglext1-dev libstartup-notification0-dev ffmpeg

   #gts
   tar -zxvf gts-snapshot.tar.gz
   cd gts
   ./configure
   make
   sudo make install
   cd ..

#ruby and rubygems
wget http://production.cf.rubygems.org/rubygems/rubygems-1.8.7.tgz
tar -zxvf rubygems-1.8.7.tgz
cd rubygems-1.8.7
sudo ruby setup.rb
cd ..
