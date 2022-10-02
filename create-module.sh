#!/bin/bash

module=$1
wd=$(pwd)

# prompt for module info
if [ -z "$module" ]; then
    read -r -p "Enter module name: " module
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
            break
            ;;
        2)
            package="com.dangerfield.spyfall.$module"
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
mv example.gradle.kts "$safename".gradle.kts

# create manifest and src path

printf "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<manifest package=\"%s\" />\n" "$safename" > src/main/AndroidManifest.xml
mkdir -p "src/main/java/${safename//.//}"

# update README
parentOrEmpty=$([ -z "$parent" ] && echo "" || echo " $parent")
echo "$(
    printf "%s%s (%s)\n" "$module" "$parentOrEmpty" "$option"
    cat README.md
)" > README.md

# update project settings
projectPath=$([ -z "$parent" ] && echo ":$module" || echo " :$parent:$module")
cd "$wd" && echo "$(
    printf "include(\"%s\")\n" "$projectPath"
    cat settings.gradle.kts
)" > settings.gradle.kts

# notify of completion
echo ""
echo "THE \"$name\" MODULE HAS BEEN CREATED!"
echo ""
echo "TODO:"
echo "* ORGANIZE AND ALPHABETIZE settings.gradle.kts"
echo "* UPDATE THE INFO IN $path/README.md"
