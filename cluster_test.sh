#!/usr/bin/env bash

# This code compatible with bash 4
bashVersion=$BASH_VERSION
echo $bashVersion
if [[ $bashVersion != 4* ]] ; then
    echo "Bash version 4+ is required to run this script"
    exit 0
fi


# Initialize properties
zkClusterStart=1
kafkaClusterStart=1
_continue=0

SCRHOME=$PWD
USERHOME=`cd ~ && pwd`
KAFKA_CLUSTER_HOME="$USERHOME/workspace/kafka-cluster"
ZK_CLUSTER_HOME="$USERHOME/workspace/zookeeper-cluster"

ZK_1_HOME="$ZK_CLUSTER_HOME/1_zookeeper-3.4.6"
ZK_2_HOME="$ZK_CLUSTER_HOME/2_zookeeper-3.4.6"
ZK_3_HOME="$ZK_CLUSTER_HOME/3_zookeeper-3.4.6"

KAFKA_1_HOME="$KAFKA_CLUSTER_HOME/1_kafka_2.10-0.8.2.2"
KAFKA_2_HOME="$KAFKA_CLUSTER_HOME/2_kafka_2.10-0.8.2.2"
KAFKA_3_HOME="$KAFKA_CLUSTER_HOME/3_kafka_2.10-0.8.2.2"

#readarray hosts < data.txt
hosts[0]=j-078
hosts[1]=j-079
hosts[2]=j-080
hosts[3]=j-081
hosts[4]=j-082
hosts[5]=j-083
hosts[6]=j-084

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
    echo "Starting Zookeeper cluster on ${hosts[3]} ,${hosts[4]},${hosts[5]}"
    ssh ${hosts[3]} "$ZK_1_HOME/bin/zkServer.sh start"
    sleep 2
    ssh ${hosts[4]} "$ZK_2_HOME/bin/zkServer.sh start"
    sleep 2
    ssh ${hosts[5]} "$ZK_3_HOME/bin/zkServer.sh start"
    sleep 2
    zkClusterStart=0
    return 0
}
#check health of zk cluster
check_zk_cluster() {
    stat=`ssh ${hosts[3]} "ps ax | grep zookeeper | grep -v grep"`
    if [ $stat == *QuorumPeerMain* ] ; then
        echo -n "${hosts[3]} is running, "
    else
        echo -n "${hosts[3]} is not running, "
    fi
    stat=`ssh ${hosts[4]} "ps ax | grep zookeeper | grep -v grep"`
    if [ $stat == *QuorumPeerMain* ] ; then
        echo -n "${hosts[4]} is running, "
    else
        echo -n "${hosts[4]} is not running, "
    fi
    stat=`ssh ${hosts[5]} "ps ax | grep zookeeper | grep -v grep"`
    if [ $stat == *QuorumPeerMain* ] ; then
        echo "${hosts[5]} is running"
    else
        echo "${hosts[5]} is not running"
    fi
    echo  "Status returns $stat"
    echo "Return $zkClusterStart"
    return $zkClusterStart
}

# stop zk cluster
stop_zk_cluster() {
   echo "Stoping Zookeeper cluster on ${hosts[3]} ,${hosts[4]},${hosts[5]}"
   ssh ${hosts[3]} "$ZK_1_HOME/bin/zkServer.sh stop"
   sleep 2
   ssh ${hosts[4]} "$ZK_2_HOME/bin/zkServer.sh stop"
   sleep 2
   ssh ${hosts[5]} "$ZK_3_HOME/bin/zkServer.sh stop"
   sleep 2
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
        echo "Test ${hosts[1]}"
        echo -e -n "\nCommand to execute : "
        read val opt
        case $val in
            "kk") start_kafka_cluster ;;
            "zk") stop_zk_cluster
                  start_zk_cluster;;
            "szk" ) stop_zk_cluster;;
            "czk" ) check_zk_cluster;;
            "help") print_help $opt;;
            "exit") _continue=1;; # end  the loop
            *) echo "$val is not yet supported"
        esac
    done
    ctrl_c
}

ctrl_c (){
    echo "Exit from testing"
    if [ $zkClusterStart -eq 0 ] ; then
        echo -n "Do you want to shutdown zk cluster [y or n]? "
        read  yorn
        if [ yorn == y ] ; then
            stop_zk_cluster
        fi
    fi
    echo "Bye,  see you later"
    exit 0
}

# dispatch CTRL+C to ctrl_c function using trap
trap ctrl_c 2

start