#!/bin/bash

# This is a sample script to demonstrate 
# GUITAR general workflow 
# The output can be found in Demo directory  

#------------------------
# Running in script dir 
SCRIPT_DIR=`dirname $0`

#------------------------
# Launch adr-service

# In order to reflect your env., update the project (once) as follows
android update project --name adr-server --target 8 --path .

# Build the project
ant debug

# And sign the apk using system key
ruby resign.rb bin/adr-server-debug.apk bin/adr-server.apk

# If it's already installed, uninstall it
adb uninstall edu.umd.cs.guitar

# Install the apk
adb install bin/adr-server.apk