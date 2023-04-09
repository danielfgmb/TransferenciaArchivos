import socket
import os
from _thread import *
import hashlib
import logging

ServerSideSocket = socket.socket()
host = '0.0.0.0'
port = 2004
ThreadCount = 0
BUFFER_SIZE = 4096
SEPARATOR = "<SEPARATOR>" 

logging.basicConfig(filename='con.log', encoding='utf-8', level=logging.DEBUG)
logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s [%(levelname)s] %(message)s",
    handlers=[
        logging.FileHandler("con.log"),
        logging.StreamHandler()
    ]
)

try:
    ServerSideSocket.bind((host, port))
except socket.error as e:
    logging.info(str(e))
logging.info('Socket is listening..')

ServerSideSocket.listen(5)

def multi_threaded_client(connection):
    connection.send(str.encode('Server is working:'))
    while True:

        f = open("run.cnf", "r")
        a = f.readline().strip()
        b = f.readline()
        c = f.readline()
        f.close()

        if int(c) == ThreadCount or bool(int(b)):
            
            try:
                filesize = os.path.getsize(a) 
                connection.send(f"{a}{SEPARATOR}{filesize}".encode()) 
                connection.recv(2048)
                response = f'Sending file to the host: {ThreadCount}'
                logging.info(response)
                
                with open(a, "rb") as f:  

                    while True:  
                        bytes_read = f.read(BUFFER_SIZE)  
                        if not bytes_read:  
                            break  
                        connection.sendall(bytes_read)  

                f.close()
                
                f = open(a, "r", encoding="utf-8")

                fb = f.read() 
                logging.info(hash(f))
                f.close()

                file_hash = hashlib.sha256()

                ha = str(file_hash.hexdigest())
                connection.send(ha.encode()) 

                logging.info("Hash of file: "+ha)
                connection.close()  
            except: 
                connection.close()
    
while True:
    Client, address = ServerSideSocket.accept()
    logging.info(f'Connected to:  {address[0]}:{address[1]} as host {ThreadCount}')
    start_new_thread(multi_threaded_client, (Client, ))
    ThreadCount += 1
    logging.info('Thread Number: ' + str(ThreadCount))
ServerSideSocket.close()