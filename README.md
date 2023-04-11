# TransferenciaArchivos

## Transferencia por UDP

Para ejecutar el servidor o el cliente UDP se debe tener mínimo las versión JDK 11. De tal manera que los caracteres unicode se muestren correctamente. A continuación se muestran los comandos para instalar el JRE en Linux.

```console
foo@bar:~$ sudo apt-get update
foo@bar:~$ sudo apt-get install default-jre
```
Luego para ejecutar se utiliza en Linux o Windows el siguiente comando.

```console
foo@bar:~$ sudo java -jar UDPCliente.jar
foo@bar:~$ sudo java -jar UDPServer.jar
```
Consideraciones para usuarios de Windows: Los puertos UDP están bloqueados por defecto así que para probar el programa puede ser necesario deshabilitar el Firewall. En este link encuentra información https://support.microsoft.com/en-us/windows/turn-microsoft-defender-firewall-on-or-off-ec0844f7-aebd-0583-67fe-601ecf5d774f
