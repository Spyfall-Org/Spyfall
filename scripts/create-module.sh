#!/bin/bash

module=$1
wd=$(pwd)

# prompt for module info
if [ -z "$module" ]; then
    read -r -p "Enter module name (in camelCase) : " module
fi

read -r -p "Enter module parent (blank for none). \"libraries\" and \"features\" do not count as a parent: " parent

module=${module//:/}
path=$module
name=$module

lowercaseModuleName="$(tr [A-Z] [a-z] <<< "$module")"

# prompt for module type
PS3="Please select module type: "
options=("Library" "Feature" "Quit")
select option in "${options[@]}"; do
    case $REPLY in
        1)
            if [ -z "$parent" ]; then
              package="spyfallx.$lowercaseModuleName"
            else
              package="spyfallx.$parent.$lowercaseModuleName"
            fi
            path="libraries/$parent/$path"
            parent="libraries:$parent"
            break
            ;;
        2)
            if [ -z "$parent" ]; then
              package="com.dangerfield.spyfall.$lowercaseModuleName"
            else
              package="com.dangerfield.spyfall.$parent.$lowercaseModuleName"
            fi

            path="features/$parent/$path"
            parent="features:$parent"
            break
            ;;
        3)
            exit
            ;;
        *)
            echo "Invalid option: $REPLY"
            exit
            ;;
    esac
done

# move example dir and rename
cp -r example "$path"
cd "$path" || exit

if [[ "$path" =~ .*"features".* ]]; then
  mv featurebuild.gradle.kts build.gradle.kts
  rm librarybuild.gradle.kts
elif [[ "$path" =~ .*"libraries".* ]]; then
  mv librarybuild.gradle.kts build.gradle.kts
  rm featurebuild.gradle.kts
fi

# create manifest and src path

lowercasePackageName="$(tr [A-Z] [a-z] <<< "$package")"

printf "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<manifest package=\"%s\" />\n" "$lowercasePackageName" > src/main/AndroidManifest.xml
mkdir -p "src/main/java/$lowercasePackageName"

# update README
parentOrEmpty=$([ -z "$parent" ] && echo "" || echo " $parent")
echo "$(
    printf "%s%s (%s)\n" "$module" "$parentOrEmpty" "$option"
    cat README.md
)" > README.md

# update project settings
projectPath=$([ -z "$parent" ] && echo ":$module" || echo "$parent:$module")

cd "$wd" && echo "$(
    printf "include(\"%s\")\n" "$projectPath"
    cat settings.gradle.kts
)" > settings.gradle.kts

sort settings.gradle.kts
# notify of completion
Red='\033[0;31m'          # Red
Green='\033[0;32m'        # Green

echo ""
echo -e ${Green}"THE \"$name\" MODULE HAS BEEN CREATED!"
echo ""
echo -e ${Red}"TODO:"
echo -e ${Red}"* ORGANIZE AND ALPHABETIZE settings.gradle.kts"
echo -e ${Red}"* UPDATE THE INFO IN $path/README.md"
