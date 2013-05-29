import os, os.path, sys
from com.android.monkeyrunner import MonkeyRunner, MonkeyDevice

# Make sure enough arguments are entered
if (len(sys.argv) < 2):
  print "Usage - monkeyrunner ADRScreenshot.py <pathToFile> <windowName>"

# Pull in the arguments from the script call and set up the filename
path = sys.argv[1]
windowName = sys.argv[2]
file = path + '/' + windowName + '.png'

# Get the device that's running
device = MonkeyRunner.waitForConnection()

# Pull the image from the framebuffer
result = device.takeSnapshot()

# Make the directory to the filename if it does not exist already
if not os.path.exists(path):
  os.makedirs(path)

# Check to make sure the file doesn't already exist
if not os.path.isfile(file):
  result.writeToFile(file, 'png')