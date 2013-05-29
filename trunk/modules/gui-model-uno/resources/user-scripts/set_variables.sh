#!/bin/bash

#Number of testcases to run, for each initial state and each fault seeded version
NUM_TESTCASES=20

#Code for source folder within sw module, without any faults
CORRECT_FOLDER=/media/Data/nofaultversion

#Fault seeded versions are in this folder
FOLDERS=/media/Data/faultversions/*

#Working OpenOffice build
SRC_ROOT=/media/Data/srctest/OOO330_m20
