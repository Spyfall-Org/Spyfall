#!/bin/sh

# run code analysis
echo "*****Running Code Analysis******"

git stash -q --keep-index

./gradlew detektAll
./gradlew checkstyleAll

status=$?

echo "*****Done with Code Analysis******"

exit $status

