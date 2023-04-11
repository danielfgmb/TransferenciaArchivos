# TransferenciaArchivos

## Transferencia por UDP

Para ejecutar el servidor o el cliente UDP se debe tener mínimo la versión JDK 11. De tal manera que los caracteres unicode se muestren correctamente. A continuación se muestran los comandos para instalar el JRE en Linux.

```console
foo@bar:~$ sudo apt-get update
foo@bar:~$ sudo apt-get install default-jre
```
Luego para ejecutar se utiliza en Linux o Windows el siguiente comando. El ejecutable crea carpetas y archivos por lo que dependiendo de la carpeta donde esté puede ser necesario utilizar permisos de administrador.

```console
foo@bar:~$ sudo java -jar UDPCliente.jar
foo@bar:~$ sudo java -jar UDPServer.jar
```
Consideraciones para usuarios de Windows: Los puertos UDP están bloqueados por defecto así que para probar el programa puede ser necesario deshabilitar el Firewall. En este link encuentra información https://support.microsoft.com/en-us/windows/turn-microsoft-defender-firewall-on-or-off-ec0844f7-aebd-0583-67fe-601ecf5d774f

## Transferencia por TCP


Instrucciones:

0. Se necesitan los siguientes paquetes de Python para el cliente y servidor.

```console
foo@bar:~$ pip3 install hashlib
foo@bar:~$ pip3 install logging
foo@bar:~$ pip3 install socket
```

1. Descargar el repositorio en .zip

Para el servidor linux:
2.1. Navegar a la carpeta Server
2.2. Ejecutar serv.py con:

python3 serv.py


Para el Cliente
2.1. Navegar a la carpeta Ciente
2.2. Ejecutar cliente.py con:

python cliente.py

o hacer doble click en cliente.py
2.3. Para ejecutar 25 clientes a la vez ejecutar el batchfile

