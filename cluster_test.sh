#!/usr/bin/env bash

# This code compatible with bash 4
bashVersion=$BASH_VERSION
echo $bashVersion
if [[ $bashVersion != 4* ]] ; then
    echo "Bash version 4+ is required to run this script"
    exit 0
fi


# Initialize properties
kafkaClusterStart=1
_continue=0

SCRHOME=$PWD
USERHOME=$HOME
KAFKA_CLUSTER_HOME="$USERHOME/workspace/kafka-cluster"
KAFKA_DATA_LOCATION="/scratch/shameera/kafka-logs"
ZK_CLUSTER_HOME="$USERHOME/workspace/zookeeper-cluster"
ZK_DATA_LOCATION="/scratch/shameera/zookeeper/data/version-2"

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

RED=`tput setaf 1`
GREEN=`tput setaf 2`
YELLOW=`tput setaf 3`
BLUE=`tput setaf 4`
PURPLE=`tput setaf 5`
RESET=`tput sgr0`

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
     echo "[kt] start kafka test"
     echo "[resetdata] clear all zk and kafka data "
     echo "[help]   print this help menu"
     echo "[exit]   exit test"
}

#check health of zk cluster
check_zk_cluster() {
    clusterStatus=3
    echo "----------------------------Checking Zookeeper Cluster Status----------------------------------------"
    for i in 3 4 5
    do
        stat=`ssh ${hosts[${i}]} "ps ax | grep zookeeper | grep -v grep"`
        if [[ $stat == *QuorumPeerMain* ]] ; then
            echo -n "${GREEN}${hosts[${i}]} is running${RESET}, "
            clusterStatus=$((clusterStatus-1))
        else
            echo -n "${RED}${hosts[${i}]} is not running${RESET}, "
        fi
    done
    echo ""
    sleep 2
    return $clusterStatus
}

# stop zk cluster
stop_zk_cluster() {
   check_zk_cluster
   if [ $? -eq 3 ] ; then
      echo "${YELLOW}Zookeeper cluster already in down state${RESET}"
      return 0
   fi

   check_kafka_cluster
   if [ $? -ne 3 ] ; then
      echo "Please shutdown Kafka cluster first"
      return 1
   fi

   echo "------------------Stopping Zookeeper cluster on ${hosts[3]} ,${hosts[4]},${hosts[5]}-------------------"
   ssh ${hosts[3]} "$ZK_1_HOME/bin/zkServer.sh stop"
   sleep 2
   echo ""
   ssh ${hosts[4]} "$ZK_2_HOME/bin/zkServer.sh stop"
   sleep 2
   echo ""
   ssh ${hosts[5]} "$ZK_3_HOME/bin/zkServer.sh stop"
   sleep 2
   echo ""
   return 0
}

# start zk Cluster
start_zk_cluster() {
    check_zk_cluster
    if [ $? -ne 3 ] ; then
        stop_zk_cluster
    fi
    echo "------------------Starting Zookeeper cluster on ${hosts[3]} ,${hosts[4]},${hosts[5]}------------------"
    ssh ${hosts[3]} "$ZK_1_HOME/bin/zkServer.sh start"
    sleep 2
    echo ""
    ssh ${hosts[4]} "$ZK_2_HOME/bin/zkServer.sh start"
    sleep 2
    echo ""
    ssh ${hosts[5]} "$ZK_3_HOME/bin/zkServer.sh start"
    sleep 2
    echo ""
    return 0
}

#check health of kafka cluster
check_kafka_cluster(){
    clusterStatus=3
    echo "----------------------------Checking Kafka Cluster Status----------------------------------------"
    for i in 0 1 2
    do
        stat=`ssh ${hosts[${i}]} "ps ax | grep kafka | grep -v grep"`
        if [[ $stat == *kafka* ]] ; then
            echo -n "${GREEN}${hosts[${i}]} is running${RESET}, "
            clusterStatus=$((clusterStatus-1))
        else
            echo -n "${RED}${hosts[${i}]} is not running${RESET}, "
        fi
    done
    echo ""
    sleep 2
    return $clusterStatus
}

#stop kafka cluster
stop_kafka_cluster() {
    check_kafka_cluster
    if [ $? -eq 3 ] ; then
        echo "Kafka cluster already in down state"
         return 0
    fi

    echo "------------------Stopping Kafka cluster on ${hosts[0]} ,${hosts[1]},${hosts[2]}-------------------"
    ssh ${hosts[0]} "$KAFKA_1_HOME/bin/kafka-server-stop.sh"
    sleep 2
    echo ""
    ssh ${hosts[1]} "$KAFKA_2_HOME/bin/kafka-server-stop.sh"
    sleep 2
    echo ""
    ssh ${hosts[2]} "$KAFKA_3_HOME/bin/kafka-server-stop.sh"
    sleep 2
    echo ""
    check_kafka_cluster
    return 0
}

#start kafka cluster
start_kafka_cluster() {
    #check zk cluster has been started
    check_zk_cluster
    if [ $? -ne 0 ] ; then
        echo "Start zookeeper cluster first"
        return 1
    fi

    check_kafka_cluster
    if [ $? -ne 3 ] ; then
        stop_kafka_cluster
    fi

    echo "------------------Starting Kafka cluster on ${hosts[0]} ,${hosts[1]},${hosts[2]}------------------"
    ssh ${hosts[0]} "$KAFKA_1_HOME/bin/kafka-server-start.sh -daemon $KAFKA_1_HOME/config/server.properties"
    sleep 2
    echo ""
    ssh ${hosts[1]} "$KAFKA_2_HOME/bin/kafka-server-start.sh -daemon $KAFKA_2_HOME/config/server.properties"
    sleep 2
    echo ""
    ssh ${hosts[2]} "$KAFKA_3_HOME/bin/kafka-server-start.sh -daemon $KAFKA_3_HOME/config/server.properties"
    sleep 2
    echo ""
    check_kafka_cluster
    return 0
}

#check health of rabbitmq cluster
check_rabbitmq_cluster(){
    clusterStatus=3
    echo "----------------------------Checking Rabbitmq Cluster Status----------------------------------------"
    for i in 0 1 2
    do
        stat=`ssh ${hosts[${i}]} "ps ax | grep rabbitmq | grep -v grep"`
        if [[ $stat == *rabbitmq* ]] ; then
            echo -n "${GREEN}${hosts[${i}]} is running${RESET}, "
            clusterStatus=$((clusterStatus-1))
        else
            echo -n "${RED}${hosts[${i}]} is not running${RESET}, "
        fi
    done
    if [ $clusterStatus -ne 3 ] ; then
        ssh "${hosts[0]}" "sudo rabbitmqctl cluster_status"
    fi
    echo ""
    sleep 2
    return $clusterStatus
}

#stop rabbitmq cluster
stop_rabbitmq_cluster() {
    echo "------------------Stopping RabbitMQ cluster on ${hosts[0]} ,${hosts[1]},${hosts[2]}-------------------"
    for i in 0 1 2
    do
        ssh "${hosts[${i}]}" "sudo service rabbitmq-server stop"
        sleep 2
    done
    return 0
}

#start rabbitmqCluster
start_rabbitmq_cluster(){
    echo "------------------Starting RabbitMQ cluster on ${hosts[0]} ,${hosts[1]},${hosts[2]}-------------------"
    for i in 0 1 2
    do
        ssh "${hosts[${i}]}" "sudo service rabbitmq-server start"
        sleep 2
    done
    return 0
}

# build the project
#run consumers
# args: 1 outputFile , 2... commands to consumer
start_consumer() {
    output=$1
    shift
    ssh ${hosts[6]} "java -jar $SCRHOME/target/stream-performance-1.0-jar-with-dependencies.jar $@" > $output
}

stop_consumer() {
   ssh ${hosts[6]} "ps ax | grep -i 'stream-performance-' | grep java | grep -v grep | awk '{print $1}' | xargs kill -SIGTERM"
}

#run producer
# args: 1... commands to producer
start_producer() {
    ssh ${hosts[6]} "java -jar $SCRHOME/target/stream-performance-1.0-jar-with-dependencies.jar $@"
}

# args: 1 replication factor
create_kafka_topic() {
    if [ $# -lt 1 ] ; then
        echo "${RED}Replicatoin Factor is required, but not provided${RESET}"
        return 1
    fi
    tput setaf 3
    ${KAFKA_1_HOME}/bin/kafka-topics.sh --zookeeper ${hosts[3]}:2181,${hosts[4]}:2181,${hosts[5]}:2181 \
        --delete --topic test
    sleep 5
    ${KAFKA_1_HOME}/bin/kafka-topics.sh --zookeeper ${hosts[3]}:2181,${hosts[4]}:2181,${hosts[5]}:2181 \
        -create --topic test --partitions 3 --replication-factor $1
    sleep 2
    ${KAFKA_1_HOME}/bin/kafka-topics.sh --zookeeper ${hosts[3]}:2181,${hosts[4]}:2181,${hosts[5]}:2181 \
        --describe --topic test
    tput sgr0
}

#Reset zk and kafka data
reset_zk_kafka_data() {
    check_zk_cluster
    if [ $? -eq 0 ] ; then
        echo "${RED}Can't clean data while clusters are running${RESET}"
        return 1
    fi
    for i in 0 1 2
    do
        ssh ${hosts[${i}]} "rm -rf $KAFKA_DATA_LOCATION"
    done

    for j in 3 4 5
    do
        ssh ${hosts[${j}]} "rm -rf $ZK_DATA_LOCATION"
    done
}

#args 1 data input file, 2 num of messages
kafka_test(){
    if [ $# -lt 1 ] ; then
        echo "kafka test require data file name and test message count"
        echo "Stop Test"
        return 0
    fi
   check_zk_cluster
   if [ $? -ne 0 ] ; then
        start_zk_cluster
        if [ $? -ne 0 ] ; then
            echo "${RED}Test failed ... Couldn't start Zookeeper cluster${RESET}"
            return 1
        fi
   fi

   check_kafka_cluster
   if [ $? -ne 0 ] ; then
        start_kafka_cluster
        if [ $? -ne 0 ] ; then
            echo "${RED}Test failed ... Couldn't start Kafka cluster${RESET}"
            return 1
        fi
   fi

   readarray -t inputs < inputs.txt

    for i in "${inputs[@]}"
    do
        for j in 1 2
        do
            # create kafka topic
            create_kafka_topic ${j}
            if [ $? -ne 0 ] ; then
                echo "${RED}Kafka topic creation failed${RESET}"
                return 1
            fi

            if [ -f $USERHOME/testdata/${i}_rep${j}.out ] ; then
                rm $USERHOME/testdata/${i}_rep${j}.out
            fi
            echo "${BLUE} run test with output file $USERHOME/testdata/${i}_rep${j}.out ${RESET}"
            start_consumer $USERHOME/testdata/${i}_rep${j}.out -kc -n 3 &
            cPID=$!
            start_producer -kp -d $SCRHOME/data/${i}.txt -n $1
            sleep 10
            stop_consumer
            sleep 2
            kill ${cPID}
        done
    done

   echo "Kafka test finished"
   echo -n "Do you need to shutdown kafka and zookeeper clusters [y or n]? "
   read yorn
   if [[ ${yorn} == y ]] ; then
        stop_kafka_cluster
        stop_zk_cluster
   fi
   return 0

}

# start testing
start(){
    while [ ${_continue} -eq 0 ]
    do
        echo "--------------------------------------------------------------------"
        echo -e -n "Command to execute : "
        read val opt
        case $val in
            "zk") start_zk_cluster;;
            "szk" ) stop_zk_cluster;;
            "czk" ) check_zk_cluster;;
            "kk") start_kafka_cluster ;;
            "skk" ) stop_kafka_cluster;;
            "ckk" ) check_kafka_cluster;;
            "kt" ) kafka_test ${opt};;
            "rr" ) start_rabbitmq_cluster;;
            "crr" ) check_rabbitmq_cluster;;
            "srr" ) stop_rabbitmq_cluster;;
            "resetdata" ) reset_zk_kafka_data;;
            "help") print_help ${opt};;
            "exit") _continue=1;; # end  the loop
            *) echo "$val is not yet supported"
        esac
    done
    ctrl_c
}

ctrl_c (){
    echo "Exit from testing"
    check_kafka_cluster
#    if [ $? -ne 3 ] ; then
#        echo -n "Do you want to shutdown Kafka cluster [y or n]? "
#        read yorn
#        if [[ ${yorn} == y ]] ; then
#            stop_kafka_cluster
#        fi
#        echo ""
#    fi
    check_zk_cluster
#    if [ $? -ne 3 ] ; then
#        echo -n "Do you want to shutdown zk cluster [y or n]? "
#        read yorn
#        if [[ ${yorn} == y ]] ; then
#            stop_zk_cluster
#        fi
#        echo ""
#    fi
    check_rabbitmq_cluster

    echo "Bye,  see you later"
    exit 0
}

# dispatch CTRL+C to ctrl_c function using trap
trap ctrl_c 2

start
