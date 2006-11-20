#!/bin/sh
# 
# This file is a personal launcher script, it probably needs quite some
# adaptation to also be useful for you.

AMBER_HOME=$(pwd)

CLASSPATH=$AMBER_HOME/build:\
$AMBER_HOME/lib/JavaOpenAIR.jar:\
$AMBER_HOME/lib/jdom.jar:\
$AMBER_HOME/lib/jvyaml.jar:\
$AMBER_HOME/lib/informa-0.6.5/cglib2.jar:\
$AMBER_HOME/lib/informa-0.6.5/commons-beanutils.jar:\
$AMBER_HOME/lib/informa-0.6.5/commons-collections.jar:\
$AMBER_HOME/lib/informa-0.6.5/commons-lang.jar:\
$AMBER_HOME/lib/informa-0.6.5/commons-logging.jar:\
$AMBER_HOME/lib/informa-0.6.5/dom4j.jar:\
$AMBER_HOME/lib/informa-0.6.5/ehcache.jar:\
$AMBER_HOME/lib/informa-0.6.5/hibernate2.jar:\
$AMBER_HOME/lib/informa-0.6.5/hsqldb.jar:\
$AMBER_HOME/lib/informa-0.6.5/informa.jar:\
$AMBER_HOME/lib/informa-0.6.5/jdom.jar:\
$AMBER_HOME/lib/informa-0.6.5/jta.jar:\
$AMBER_HOME/lib/informa-0.6.5/lucene.jar:\
$AMBER_HOME/lib/informa-0.6.5/odmg.jar:\
$AMBER_HOME/lib/commons-cli.jar

cd $AMBER_HOME/build
java -cp $CLASSPATH amber.common.Launcher $*

