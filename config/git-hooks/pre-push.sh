#!/bin/sh
exitStatus=0 # 0 is a success
detektErrors=0
checkstyleErrors=0
projectDir=$(pwd)

#Echo output colors:
RED='\033[0;31m'
NC='\033[0m' # No Color
CYAN='\033[0;36m'
GREEN='\033[0;32m'


#__________________________________ Detekt _______________________________________________
echo "${CYAN} *****Running Detekt******"

./gradlew detektAll
detektErrors="$?"

if ((detektErrors > 0)); then
  echo "${NC}\nDetekt result output can be found:"
  echo "   HTML: file://$projectDir/build/reports/codestyle/detekt.html"
  echo "   XML: file://$projectDir/build/reports/codestyle/detekt.xml"
  echo "\n${RED}Detekt found code style errors! Please resolve them before pushing"
  exit 1
else
  echo "\n${GREEN}No code style errors Detekt-ed "
fi


#__________________________________Localization Sync. Check _______________________________________

echo "${CYAN} ************Checking that any changed string have been updated in all languages******"

projectRoot=$(git rev-parse --show-toplevel)

kotlinScriptPath="$projectRoot/scripts/check_language_sync.main.kts"

if [ ! -f "$kotlinScriptPath" ]; then
    echo "Kotlin script not found: $kotlinScriptPath"
    exit 1
fi

kotlinc -script "$kotlinScriptPath" || exit $?