#!/bin/bash

old_dir=`pwd`
original_PATH=${PATH}

# Install openjdk-6 if it is not installed
if [ ! -d "/usr/lib/jvm/java-1.6.0-openjdk" ]; then
  if [ -f /etc/debian_version ]; then
    echo "==> Installing openjdk-6"
    sudo apt-get -y install openjdk-6-jdk openjdk-6-jre
  else
    echo "ERROR: Please install OpenJDK-1.6.0"
	exit -1
  fi
else
  echo "==> OpenJDK 1.6.0 is installed"
fi

# Install ant if it is not installed
if [ ! command -v ant >/dev/null 2>&1 ]; then
  if [ -f /etc/debian_version ]; then
    echo "==> Installing Apache Ant"
    sudo apt-get -y install ant
  else
    echo "ERROR: Please install Apache Ant"
	exit -1
  fi
else
  echo "==> Apache Ant is installed"
fi
	
# Install svn if it is not installed
if [ ! command -v svn >/dev/null 2>&1 ]; then
  if [ -f /etc/debian_version ]; then
    echo "==> Installing Subversion"
    sudo apt-get -y install subversion
  else
    echo "ERROR: Please install Subversion"
	exit -1
  fi
else
  echo "==> Subversion is installed"
fi

# Install python if it is not installed
if [ ! command -v python >/dev/null 2>&1 ]; then
  if [ -f /etc/debian_version ]; then
    echo "==> Installing Python"
    sudo apt-get -y install python
  else
    echo "ERROR: Please install Python"
	exit -1
  fi
else
  echo "==> Python is installed"
fi

# Add the android sdk directory to the path if it exists and is not already in path
if [ -d $HOME/android-sdk-linux/tools ]; then
  echo "$PATH" | grep -q "android-sdk-linux/tools" || export PATH="$PATH:$HOME/android-sdk-linux/tools:$HOME/android-sdk-linux/platform-tools"
fi

# Check that android executable exists and install if it doesn't
if [ $(which android) ]; then
    echo -e "==> Android SDK already installed, skipping install.\n"
else
    if [ -f $HOME/android-sdk-linux/tools/android ]; then
        echo -e "==> Android SDK detected.\n"
    else
        echo -e "==> Download Android SDK"
		cd $HOME
        downloader=$(which wget)
        if [ $downloader ]; then
            wget http://dl.google.com/android/android-sdk_r15-linux.tgz
        else
            curl http://dl.google.com/android/android-sdk_r15-linux.tgz > android-sdk_r15-linux.tgz
        fi

        echo -e "==> Uncompress Android SDK"
        tar xvfz android-sdk_r15-linux.tgz
		rm android-sdk_r15-linux.tgz
    fi
	
    tools_path="$HOME/android-sdk-linux/tools:$HOME/android-sdk-linux/platform-tools"
    export PATH=${PATH}:${tools_path}
    android update sdk --no-ui -t tool
    android update sdk --no-ui -t platform-tool
    android update sdk --no-ui -t platform
fi

cd $old_dir

export PATH=$original_PATH
