#!/bin/sh

# run code analysis
echo "*****Running Code Analysis******"

./gradlew detektAll
./gradlew checkstyleAll

status=$?

echo "*****Done with Code Analysis******"

exit $status

