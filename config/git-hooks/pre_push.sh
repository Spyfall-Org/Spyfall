#!/bin/sh

# run code analysis
./gradlew detektAll
./gradlew checkstyleAll
