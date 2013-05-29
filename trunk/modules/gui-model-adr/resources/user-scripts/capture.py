#!/usr/bin/env monkeyrunner

from com.android.monkeyrunner import MonkeyRunner as mr
from edu.umd.cs.guitar import ADRCapture as capture

device = mr.waitForConnection()
capture.start(device)
