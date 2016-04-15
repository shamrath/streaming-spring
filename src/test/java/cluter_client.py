# from __future__ import print_function
__author__ = 'syodage'
import paramiko
import thread
import time

class ClusterClient:

    def __init__(self):
        julietHost = "juliet.futuresystems.org"
        username = "syodage"
        ssh_key = "/Users/syodage/.ssh/id_rsa"
        self.ssh = paramiko.SSHClient()
        self.ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())
        self.ssh.connect(julietHost, username=('%s' % username), key_filename=ssh_key)
        self.active = True
        self.kafkaHosts = []
        self.kafkaThreads = []
        self.zkHosts = []
        self.zkThreads = []
        self.rabbitHosts = []


    def execCommnad(self, command, output=None):
        stdin, stdout, stderr = self.ssh.exec_command(command)
        # data = stdout.read().splitlines()
        data = None
        if output is not None:
            target = open(output, 'w')
            for line in data:
                target.write(line)
        else:
            for line in iter(lambda: stdout.readline(2048), ""):
                print(line)

        stdin.close()


    def readConfig(self, filename):
        # self.conf = [line.rstrip('\n') for line in open(filename)]
        with open(filename) as f:
            self.conf = f.read().splitlines()
        print(self.conf)


    def startKafka(self, host, command):
        # print(self.sshjuliet)
        self.execCommnad(command)
        pass

    def startZk1(self , host="j-081", foreground=False):
        command = "ssh {} && cd /N/u/syodage/workspace/zookeeper-cluster/1_zookeeper-3.4.6/ " \
                  "&& ./bin/zkServer.sh start-foreground".format(host)
        command = "ssh {} && whoami".format(host)
        print(command)
        self.execCommnad(command)


    def setupKafka(self, zkhosts , kafkahosts, command):
        self.isKafkaStart = True
        thread.start_new_thread(self.startKafka, (kafkahosts, command))

# end of ClusterClient Class


def printUsage():
    print("COMMAND <OPTIONS> :- DESCRIPTION")
    print("zk1 <host> <foreground> :- start zookeeper node in host name, default host name is j-081, default foreground is False")
    print("kzk1 <host> :- kill zookeeper ")


def main():
    cluster = ClusterClient()
    cluster.readConfig("/Users/syodage/Projects/streaming-spring/src/test/java/resources.txt")
    cluster.setupKafka("host", "host","ls")
    time.sleep(1)
    printUsage()
    while cluster.active:
        val = raw_input(">>> ")
        print("you entered " , val)
        if val == "zk1":
            cluster.startZk1()
        elif val == "kzk1":
            print("not support yet")
        elif val == "help":
            printUsage()
        elif val == "exit":
            cluster.active = False
        else:
            print("Invalid Command")
            printUsage()
        pass


if __name__ == "__main__":
    main()

