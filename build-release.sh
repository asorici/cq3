#!/bin/bash

## Step 1: create cqserver and cqclient folders
echo "CREATING FOLDERS ... \n"
mkdir cqserver
mkdir cqclient

## Step 2: build the jars
echo "BUILDING server and client jars ... "
(cd framework; ant -buildfile build.xml)

## Step 3: populate directories
echo "POPULATING directories ... "
cp -r framework/lib cqserver/
cp -r framework/images cqserver/
cp -r framework/maps cqserver/
cp framework/cq.policy cqserver/
cp framework/logging.properties cqserver/
cp framework/mapdata-release cqserver/mapdata
cp framework/secrets-release.txt cqserver/secrets.txt
cp framework/GamePolicy-release.xml cqserver/GamePolicy.xml
cp framework/dist/cqserver.jar cqserver/

cp -r framework/lib cqclient/
cp -r framework/doc cqclient/
cp -r framework/cqclient-src-release cqclient/src
cp framework/cq.policy cqclient/
cp framework/logging.properties cqclient/
cp framework/clientbuild.xml cqclient/
cp framework/dist/cqclient.jar cqclient/lib/

## Step 4: Create cqclient and cqserver archives
echo "CREATING client and server archives ... "
zip -9 -q -r cqserver.zip cqserver/
zip -9 -q -r cqclient.zip cqclient/

## Step 5: Create source archive - we need to remove filter out some files
echo "CREATING source archive ... "
zip -9 -q -r cqsources.zip framework/ -x '*.settings*' '*dist*' '*bin*' '*build*' '*-release*' '*server_gui_log*' '*.project*' '*.classpath*' '*.gitignore*'

## Step 6: Create the release archive -- include the above created archives, the rulebook, the tutorial, the README and the license file
echo "CREATING release archive ... "
zip -9 -q cq-framework.zip cqclient.zip cqserver.zip cqsources.zip CQ3-rulebook.pdf CQ3-tutorial.pdf README LICENSE.txt

## Cleanup cqclient and cqserver directories
echo "CLEANUP AND FINISH"
rm -rf cqserver/
rm -rf cqclient/
rm -f cqserver.zip
rm -f cqclient.zip
rm -f cqsources.zip
