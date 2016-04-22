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



find_value $@