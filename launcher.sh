#!/bin/sh
# 
# This file is a personal launcher script, it probably needs quite some
# adaptation to also be useful for you.

AMBER_HOME=/home/christian/Source/Amber
LOCAL_JAVA_HOME=/home/christian/.java/deployment

CLASSPATH=$AMBER_HOME/build:\
$AMBER_HOME/lib/JavaOpenAIR.jar:\
$AMBER_HOME/lib/jdom.jar:\
$AMBER_HOME/lib/jvyaml.jar:\
$LOCAL_JAVA_HOME/ext/informa-0.6.5/lib/cglib2.jar:\
$LOCAL_JAVA_HOME/ext/informa-0.6.5/lib/commons-beanutils.jar:\
$LOCAL_JAVA_HOME/ext/informa-0.6.5/lib/commons-collections.jar:\
$LOCAL_JAVA_HOME/ext/informa-0.6.5/lib/commons-lang.jar:\
$LOCAL_JAVA_HOME/ext/informa-0.6.5/lib/commons-logging.jar:\
$LOCAL_JAVA_HOME/ext/informa-0.6.5/lib/dom4j.jar:\
$LOCAL_JAVA_HOME/ext/informa-0.6.5/lib/ehcache.jar:\
$LOCAL_JAVA_HOME/ext/informa-0.6.5/lib/hibernate2.jar:\
$LOCAL_JAVA_HOME/ext/informa-0.6.5/lib/hsqldb.jar:\
$LOCAL_JAVA_HOME/ext/informa-0.6.5/lib/informa.jar:\
$LOCAL_JAVA_HOME/ext/informa-0.6.5/lib/jdom.jar:\
$LOCAL_JAVA_HOME/ext/informa-0.6.5/lib/jta.jar:\
$LOCAL_JAVA_HOME/ext/informa-0.6.5/lib/lucene.jar:\
$LOCAL_JAVA_HOME/ext/informa-0.6.5/lib/odmg.jar:\
/usr/share/java/commons-cli.jar:\
/usr/share/java/commons-cli-1.0.jar

cd $AMBER_HOME/build
java -cp $CLASSPATH amber.common.Launcher $*

