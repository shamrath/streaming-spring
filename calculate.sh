#!/usr/bin/env bash
calc () {
        avg=`awk "BEGIN { print "$*" }"`
}


find_value() {
    echo "Processing file :$1"
    grep "Offset:" $1 > test.out
    sed -i -e 's/.* = //g' test.out
    sed -i -e 's/ ns//g' test.out

    readarray -t data < test.out
    count=${#data[@]}
    echo -n "$1 : data count ${count}, " >> ${output}
    sum=0
    if [ ${count} -gt 100 ] ;
    then
        for ((i=100; i < ${count}; i++))
        do
            ((sum += ${data[$i]}))
        done
        val=`expr ${count} - 100`
    else
        for ((i=2; i < ${count}; i++))
        do
            ((sum += ${data[$i]}))
        done
        val=`expr ${count} - 2`
    fi
    echo -n "Val = ${val}, " >> ${output}
    calc  ${sum}/${val}
    echo "Sum : $sum, Avg (sum/val) $sum/$val  = $avg" >> ${output}
}

start(){
    array=($(ls $1))
    for  i in "${array[@]}"
    do
        find_value ${i}
    done
}

if [ $# -lt 1 ] ; then
    echo "Required file pattern"
    return 1
else
    output=results.txt
    rm ${output}
    start $@
fi
