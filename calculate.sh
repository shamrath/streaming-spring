#!/usr/bin/env bash
calc () {
        avg=`awk "BEGIN { print "$*" }"`
}

# args : file_name , ignore count
find_value() {
    echo "Processing file :$1"
    grep "Offset:" $1 > test.out
    sed -i -e 's/.* = //g' test.out
    sed -i -e 's/ ns//g' test.out

    readarray -t data < test.out
    count=${#data[@]}
    echo "Ignore value -> ${2} ,line count -> ${count}"
    echo -n "$1 : data count ${count}, " >> ${output}
    sum=0
    if [ ${count} -gt ${2} ] ;
    then
        for ((i=${2}; i < ${count}; i++))
        do
            ((sum += ${data[$i]}))
        done
        val=`expr ${count} - ${2}`
    else
        echo "Ignore value -> ${2} is grater than line count -> ${count}"
        retun 1
    fi
    echo -n "Val = ${val}, " >> ${output}
    calc  ${sum}/${val}
    echo "Sum : $sum, Avg (sum/val) $sum/$val  = $avg" >> ${output}
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
    start $@
fi
