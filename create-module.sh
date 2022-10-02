#!/bin/bash

module=$1
wd=$(pwd)

# prompt for module info
if [ -z "$module" ]; then
    read -r -p "Enter module name (in camelCase) : " module
fi

module=${module//:/}
path=$module
name=$module


#lowercased version of the packagename
safename="$(tr [A-Z] [a-z] <<< "$name")"
echo $safename

# prompt for module type
PS3="Please select module type: "
options=("Library" "Feature" "Quit")
select option in "${options[@]}"; do
    case $REPLY in
        1)
            package="spyfallx.$module"
            safepackage="spyfallx.$safename"
            break
            ;;
        2)
            parent="features"
            package="com.dangerfield.spyfall.$module"
            safepackage="com.dangerfield.spyfall.$safename"
            path="features/$path"
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
mv example.gradle.kts build.gradle.kts

# create manifest and src path

printf "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<manifest package=\"%s\" />\n" "$safepackage" > src/main/AndroidManifest.xml
mkdir -p "src/main/java/$safepackage"

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

# notify of completion
Red='\033[0;31m'          # Red
Green='\033[0;32m'        # Green

echo ""
echo -e ${Green}"THE \"$name\" MODULE HAS BEEN CREATED!"
echo ""
echo -e ${Red}"TODO:"
echo -e ${Red}"* ORGANIZE AND ALPHABETIZE settings.gradle.kts"
echo -e ${Red}"* UPDATE THE INFO IN $path/README.md"
