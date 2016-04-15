#!/usr/bin/env bash

# Initialize properties
zkClusterStart=1
kafkaClusterStart=1
_continue=0
# read inputs

#pass user inputs

# print help
print_help_kafka () {
     echo "**************kafka commands*************"
     echo "[kk]     start kafka cluster"
     echo "[ckk]    check kafka cluster"
     echo "[skk]    stop kafka cluster"
}

print_help_zk () {
     echo "**************Zookeeper commands*************"
     echo "[zk]     start zookeeper cluster"
     echo "[czk]    check zookeeper cluster"
     echo "[szk]    stop zookeeper cluster"
}

print_help_rabbit () {
     echo "**************RabbitMQ commands*************"
     echo "[rr]     start rabbitmq cluster"
     echo "[crr]    check rabbitmq cluster"
     echo "[srr]    stop rabbitmq cluster"
}

print_help_client (){
     echo "**************Client commands*************"
     echo "[skp]    start kafka publisher"
     echo "[skc]    start kafka consumer"
     echo "[srp]    start rabbitmq publisher"
     echo "[src]    stop rabbitmq consumer"
}

print_help_all () {
    print_help_kafka
    print_help_rabbit
    print_help_zk
    print_help_client
}

print_help(){
     case $1 in
        "kafka" ) print_help_kafka ;;
        "rabbit" ) print_help_rabbit ;;
        "zk") print_help_zk ;;
        "client" ) print_help_client ;;
        *) print_help_all;;
     esac

     echo "**************Main Commands*************"
     echo "[help]   print this help menu"
     echo "[exit]   exit test"
}


# start zk Cluster
start_zk_cluster() {
    zkClusterStart=0
    return 0
}
#check health of zk cluster
check_zk_cluster() {
    echo "Return $zkClusterStart"
    return $zkClusterStart
}

# stop zk cluster
stop_zk_cluster() {
   return 0
}
#start kafka cluster
start_kafka_cluster() {
    #check zk cluster has been started
    if [ $zkClusterStart -ne 0 ]; then
       echo "please start zk cluster first and then try"
       return 1
    elif ! check_zk_cluster; then
       echo "Zookeeper cluster need to reset before start kafka cluster"
       return 1
    else
        echo "started kafka cluster"
        return 0
    fi
}

#check health of kafka cluster
check_kafka_cluster(){
    return 0
}

#stop kafka cluster
stop_kafka_cluster() {
    return 0
}

#start rabbitmqCluster
start_rabbitmq_cluster(){
    return 0
}

#check health of rabbitmq cluster
check_rabbitmq_cluster(){
    return 0
}

#stop rabbitmq cluster
stop_rabbitmq_cluster() {
    return 0
}

# build the project
#run consumers
#run producers

# start testing
start(){
    while [ $_continue -eq 0 ]
    do
        echo -e -n "\nCommand to execute : "
        read val opt
        case $val in
            "kk") start_kafka_cluster ;;
            "zk") start_zk_cluster;;
            "help") print_help $opt;;
            "exit") _continue=1;; # end  the loop
            *) echo "$val is not yet supported"
        esac
    done
    ctrl_c
}

ctrl_c (){
    echo "Exit from testing, Bye"
    exit 0
}

# dispatch CTRL+C to ctrl_c function using trap
trap ctrl_c 2

start
