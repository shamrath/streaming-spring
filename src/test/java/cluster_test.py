import paramiko
import thread
import time

def getSSH():
    julietHost = "juliet.futuresystems.org"
    username = "syodage"
    ssh_key = "/Users/syodage/.ssh/id_rsa"
    ssh = paramiko.SSHClient()
    ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())
    ssh.connect(julietHost, username=('%s' % username), key_filename=ssh_key)
    return ssh


def execCommnad(ssh, command):
    stdin, stdout, stderr = ssh.exec_command(command)
    stdin.close()
    data = stdout.read().splitlines()
    for line in data:
        print(line)



def readConfig(filename):
    # self.conf = [line.rstrip('\n') for line in open(filename)]
    with open(filename) as f:
        conf = f.read().splitlines()
    print(conf)
    return conf

def startKafka(ssh, host, command):
    # print(self.sshjuliet)
    execCommnad(ssh, command)
    pass


def setupKafka(ssh, host, command):
    pass



def main():
    conf = readConfig(filename="/Users/syodage/Projects/streaming-spring/src/test/java/resources.txt")
    ssh = getSSH()
    startKafka(ssh, "host", "ls")
    thread.start_new_thread(startKafka, (ssh ,"host", "pwd"))
    while 1:
        pass



if __name__ == '__main__':
    main()
    # test()
    pass








