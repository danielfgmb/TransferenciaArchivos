import socket
from time import time
import hashlib
import warnings

ClientMultiSocket = socket.socket()

f = open("run.cnf", "r")
host = f.readline().strip()
port = int(f.readline().strip())
BUFFER_SIZE = int(f.readline().strip())
f.close()

print('Waiting for connection response')
try:
    ClientMultiSocket.connect((host, port))
except socket.error as e:
    print(str(e))

print(ClientMultiSocket.recv(1024).decode())
vwr = ClientMultiSocket.recv(BUFFER_SIZE).decode()

while vwr is None:
    vwr = ClientMultiSocket.recv(BUFFER_SIZE).decode()

    
lista = vwr.split("<SEPARATOR>")

ClientMultiSocket.send(str.encode(lista[1]))
nombre_arc = lista[0]
tamanio = float(lista[1])/1048576
b_temporal = True
t_total = 0

with open(nombre_arc, "wb") as f:
    while b_temporal:
        start_time = time()
        recv_data = ClientMultiSocket.recv(BUFFER_SIZE)
        elapsed_time = time() - start_time
        t_total += elapsed_time
        if not recv_data:
            b_temporal = False
        f.write(recv_data)


vel_transfer = round(tamanio/t_total, 1)
print(f"\rArchivo guardado exitosamente.")
print(f"La velocidad de transferencia fue: {vel_transfer} MB/s")
print(f"Tiempo de transferencia fue: {round(t_total, 1)} s")

f = open(nombre_arc, "rb")
fb = f.read()
f.close()

file_hash = hashlib.md5(fb)
print(file_hash)
    
hash_c = str(file_hash.hexdigest()) 
print(hash_c)

hash_r = ClientMultiSocket.recv(BUFFER_SIZE).decode()

if hash_c == hash_r:
    warnings.warn("Los hash de los archivos son diferentes")

ClientMultiSocket.close()