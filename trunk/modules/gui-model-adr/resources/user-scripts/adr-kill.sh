#!/bin/bash

# Clean up (kill the emulator if running, delete the emulator image, reset path
pkill emulator
killall emulator
android delete avd -n ADRGuitarTest
