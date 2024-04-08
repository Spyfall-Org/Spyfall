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

echo "\n${CYAN} *****Checking String Changes Are Synced******\n"
# Base directory for string resources
RES_DIR="./dictionary/src/main/res"

# Function to extract string names from a given strings.xml file
extract_string_names() {
    grep '<string name=' "$1" | sed -E 's/.*<string name="([^"]+)".*/\1/' > /tmp/strings_$2.txt
}

# Get a list of modified strings.xml files
MODIFIED_STRINGS_FILES=$(git diff --cached --name-only | grep 'res/values.*/strings.xml')

# Check if any strings.xml files have been modified
if [ -z "$MODIFIED_STRINGS_FILES" ]; then
    # No strings.xml modifications, proceed with the push
    echo "\n\n${GREEN}All string resources are synchronized.\n\n"
    exit 0
fi

# Extract string names from all strings.xml files
for lang_dir in $(ls -d $RES_DIR/values*/); do
    lang=$(basename $lang_dir)
    strings_file="$lang_dir/strings.xml"
    if [ -f "$strings_file" ]; then
        extract_string_names "$strings_file" "$lang"
    fi
done

# Initialize a flag to track inconsistencies
inconsistencies_found=0
echo "Checking for inconsistencies..."

# Loop for each modified file to compare with all languages
for modified_file in $MODIFIED_STRINGS_FILES; do
    modified_lang=$(basename $(dirname $modified_file))
    modified_lang=${modified_lang/#values/} # Remove 'values' prefix
    modified_lang=${modified_lang:-"default"} # Set default if empty

    # Compare the modified strings.xml files against all others
    for lang_dir in $(ls -d $RES_DIR/values*/); do
        lang=$(basename $lang_dir)
        lang=${lang/#values/} # Remove 'values' prefix

        if [ "$lang" != "$modified_lang" ]; then
            # Compare string names
            DIFF=$(comm -3 /tmp/strings_${modified_lang:-default}.txt /tmp/strings_$lang.txt)
            if [ ! -z "$DIFF" ]; then
                echo "Error: Inconsistencies found between ${modified_lang:-default} and $lang"
                echo "Affected strings:"
                echo "$DIFF"
                inconsistencies_found=1
            fi
        fi
    done
done

if [ $inconsistencies_found -eq 1 ]; then
    echo "\n${RED}String resources are not synchronized across all languages. Please review the errors above."
    exit 1
fi

echo "\n${GREEN}All string resources are synchronized."
exit 0

