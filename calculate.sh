#!/usr/bin/env bash
calc () {
        avg=`awk "BEGIN { print "$*" }"`
}

output=results.txt

find_value() {
    grep "Offset:" $1 > test.out
    sed -i -e 's/.* = //g' test.out
    sed -i -e 's/ ns//g' test.out

    readarray -t data < test.out
    count=${#data[@]}
    echo -n "data count ${count}" >> ${output}
    sum=0
    if [ ${count} -gt 100 ] ;
    then
        for ((i=100; i < ${count}; i++))
        do
            ((sum += ${data[$i]}))
        done
        val=`expr ${count} - 100`
        echo -n "Val = ${val}" >> ${output}
        calc ${sum}/${val}
    else
        for ((i=2; i < ${count}; i++))
        do
            ((sum += ${data[$i]}))
        done
        val=`expr ${count} - 2`
        echo -n "Val = ${val}" >> ${output}
        calc  ${sum}/${val}
    fi
    echo "$1 : Sum : $sum, Avg (sum/val) $sum/$val  = $avg"
}



find_value