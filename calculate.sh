#!/usr/bin/env bash
calc () {
        avg=`awk "BEGIN { print "$*" }"`
}

statistics () {
    #echo "filename :- $1"
    #awk '{sum+=$1; sumsq+=$1*$1;} END {print "stdev = " sqrt(sumsq/NR - (sum/NR)**2);}' $1
    awk '{sum+=$1; sumsq+=$1*$1;} END {print "N = " NR; print "Median = "(sum/NR); print "variance = "sumsq/NR - (sum/NR)**2; print "stdev = " sqrt(sumsq/NR - (sum/NR)**2);}' $1 >> ${output}
}
# args : file_name , ignore count
find_value() {
    echo "Processing file :$1"
    grep "Offset:" $1 > test.out
    sed -i -e 's/.* = //g' test.out
    sed -i -e 's/ ns//g' test.out
    initcount=`wc -l test.out`
    echo "tatoal count ${initcount}"
    if [ ${initcount} -gt ${2} ] ;
    then
        echo "Ignore value -> ${2} ,line count -> ${initcount}"
        val=`expr ${initcount} - ${2}`
        tail -n ${val} test.out > test2.out
        readarray -t data < test2.out
        count=${#data[@]}
        echo -n "$1 : data count ${count}, " >> ${output}
        statistics test2.out
    else
        echo "Ignore value -> ${2} is grater than line count -> ${count}"
        retun 1
    fi
}

start(){
    array=($(ls *.out))
    for  i in "${array[@]}"
    do
        find_value ${i} ${1}
    done
}

if [ $# -lt 1 ] ; then
    echo "Required ignore value"
    return 1
else
    output=results.txt
    rm ${output}
    #statistics $output
    start $@
fi
