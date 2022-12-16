#!/bin/bash

mvn clean 
mvn install -Dmaven.test.skip=true

rm -rf javadoc/*
mvn javadoc:aggregate -Dimagingbook.skipjavadoc=false

echo ""
read -rsp $'Done. Press any key to quit...\n' -n1 key
