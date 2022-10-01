#!/bin/bash

module=$1
wd=$(pwd)

# prompt for module info
if [ -z "$module" ]; then
    read -r -p "Enter module name: " module
fi

read -r -p "Enter module parent (blank for none): " parent

module=${module//:/}
path=$module
name=$module

# setup with parent if exists
if [ -n "$parent" ]; then
    parent=${parent//:/}
    path=$parent/$module
    name=$parent-$module

    # verify parent path exists
    [ ! -d "$parent" ] && echo "$parent DOES NOT exist!" && exit
fi

# prompt for module type
PS3="Please select module type: "
options=("Library" "Feature" "Quit")
select option in "${options[@]}"; do
    case $REPLY in
        1)
            package="dangerfield.$module"
            break
            ;;
        [2])
            package="com.dangerfield.spyfall.features.$module"
            break
            ;;
        4)
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
mv example.gradle.kts "$name".gradle.kts

# create manifest and src path
printf "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<manifest package=\"%s\" />\n" "$package" > src/main/AndroidManifest.xml
mkdir -p "src/main/java/${package//.//}"

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
