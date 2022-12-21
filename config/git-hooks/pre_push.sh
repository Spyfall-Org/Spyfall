#!/bin/sh

gradle=${GRADLE_CMD:-./gradlew}

# run code analysis
$gradle \
    checkstyleAll \
    detektAll