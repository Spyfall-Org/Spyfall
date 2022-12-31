#!/bin/sh
CMDNAME=`basename $0`
VERSION=0.1

# command help
usage() {
    cat <<- END 1>&2
	$CMDNAME [-adv] [-l depth] [-p pattern] [path]
	    -a           print all files. default, not print hide files (pattern:"/\..*")
	    -d           print only directory. default, print all type files(file, dir, link)
	    -l depth     print directory max depth. default, no limit
	    -p pattern   print only match pattern
	    -v           print command version
	    path         tree root directory path. default, current directory
END
    return 0
}

# analyze options & parameters
FILETYPE=
MAXDEPTH=
IGNORE_PATTERN='/\..*'
MATCH_PATTERN='.*'
OPT=
while getopts 'adl:p:v' OPT
do
    case $OPT in
    a)  IGNORE_PATTERN="^$"
        ;;
    d)  FILETYPE="-type d"
        ;;
    l)  MAXDEPTH="-maxdepth $OPTARG"
        ;;
    p)  MATCH_PATTERN="$OPTARG"
        ;;
    v)  echo $VERSION
        exit 0
        ;;
    \?) usage
        exit 1
        ;;
    esac
done
shift `expr $OPTIND - 1`

ROOTDIR=${1-`pwd`}
if [ ! -d "$ROOTDIR" ]; then
    echo '"'"$ROOTDIR"'" is not found or not directory'
    exit 1
fi

# print file-tree
echo $ROOTDIR
cd $ROOTDIR
find . $FILETYPE $MAXDEPTH        | \
    sort                          | \
    sed '1d'                      | \
    sed 's/^\.//'                 | \
    grep -v "$IGNORE_PATTERN"     | \
    grep "$MATCH_PATTERN"         | \
    sed 's/\/\([^/]*\)$/|-- \1/'  | \
    sed 's/\/[^/|]*/|   /g'
