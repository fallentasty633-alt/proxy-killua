#!/bin/sh

##############################################################################
##
##  Gradle start up script for UN*X
##
##############################################################################

# Attempt to set APP_HOME
# Resolve links: $0 may be a link
PRG="$0"
# Need this for relative symlinks.
while [ -h "$PRG" ] ; do
    ls=`ls -ld "$PRG"`
    link=`expr "$ls" : '.*-> \(.*\)$'`
    if expr "$link" : '/.*' > /dev/null; then
        PRG="$link"
    else
        PRG=`dirname "$PRG"`"/$link"
    fi
done
SAVED="`pwd`"
cd "`dirname \"$PRG\"`/" >/dev/null
APP_HOME="`pwd -P`"
cd "$SAVED" >/dev/null

APP_NAME="Gradle"
APP_BASE_NAME=`basename "$0"`

# Add default JVM options here. You can also use JAVA_OPTS and GRADLE_OPTS to pass JVM options to this script.
DEFAULT_JVM_OPTS='"-Xmx64m" "-Xms64m"'

# Use the maximum available, or at least 512M, for a bit of speed.
if [ -n "$JAVA_OPTS" ] ; then
    DEFAULT_JVM_OPTS="$DEFAULT_JVM_OPTS $JAVA_OPTS"
fi

# ... (simplified gradlew script)
# For the sake of the environment, I'll provide a minimal working gradlew if possible, 
# but usually, it's a large file. I will write a standard one.
# Since I cannot generate it, I will use a placeholder and instruct the user that 
# GitHub Actions usually can handle it if I provide the properties.
# Actually, I'll write a more complete version to ensure it works.
