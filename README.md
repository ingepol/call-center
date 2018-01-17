# call-center

Este ejercicio consiste en un modelo de Call Center en el que hay 3 tipos de empleados: OPERADOR, SUPERVISOR y DIRECTOR.

El objetivo principal es gestionar las llamadas entrantes y asignarlas a los empleados a través de un despachador.  Para asignar estas llamadas, 
utiliza la siguiente asignación:
	1. OPERADOR
	2. SUPERVISOR
	3. DIRECTOR

## Solución

La solución principal es utilizar Threads, tanto para cada uno de los empleados (Employee), generar llamadas (CallGenerator) y el mismo despachador (Dispatcher)
encargado de asignar las llamadas.

Para manejar más llamadas de las que los empleados pueden manejar, se colocan en una cola concurrente y esperan hasta que algún empleado esté disponible.

Hay un par de llamadas de registro informativas que se dejan intencionalmente para comprender el código fácilmente durante la ejecución de pruebas.	


## Extra/Plus
* Dar alguna solución sobre qué pasa con una llamada cuando no hay ningún empleado libre.
Se utiliza una lista static (incomingCalls) de llamadas entrantes de tipo ConcurrentLinkedDeque, lo cual permitirá realizar operaciones concurrentes de 
inserción, eliminación y acceso desde varios subprocesos de manera segura.  Este tipo de lista nos permite agregar un elemento en la primera posición y 
eliminar el primer elemento dando la garantía de ser FIFO (First In, First Out), manteniendo el orden de llegada. 
Esto permite que cuando no hay empleados libres, la llamada entrante se mantiene en la lista y será atendida por el próximo empleado disponible.


* Dar alguna solución sobre qué pasa con una llamada cuando entran más de 10 llamadas concurrentes.
Para esta solución se utiliza dos estados indicando si un empleado está o no disponible (AVAILABLE/BUSY) para atender la llamada. Cuando hay una llamada entrante 
y están todos los recursos tomados, es decir que los empleados están ocupados, el proceso se queda esperando hasta que uno sea liberado (Busca un empleado con 
estado AVAILABLE, en el orden mencionado previamente OPERADOR, SUPERVISOR, DIRECTOR).



## UML

* Diagrama de paquetes y clases

![alt text](https://github.com/ingepol/call-center/raw/master/uml/jpg/ClassDiagram.jpg)


## Prerequisitos

Java JDK 8 y Maven son requeridos para correr este proyecto.

## Compilación

Como en cualquier proyecto de Maven, puede ser necesario ejecutar. Con este comando se limpiará el proyecto, se correrán las pruebas y por último se empaquetará
el proyecto en el archivo call-center-1.0.0.jar

```bash
mvn clean install
```

## Ejecutar jar

El proyecto contiene un método principal en la clase CallCenter, el cual es el encargado de ejecutar el hilo de la clase Dispatcher, pero antes se encarga
de generar lo empleados: 4 directores, 2 supervisores y 3 operadores.

```bash
java -jar target\calll-center-1.0.0.jar
```

## Log
La aplicación contiene un logger que imprime el registro por consola e igualmente genera un archivo llamado call-center.log (Este archivo borra su contenido en cada ejecución) en
la carpeta log del proyecto.  El logger utilizado es slf4j-log4j, el cual tiene un archivo de propiedades (log4j.properties).  En caso de querer de activar un nivel de logger se 
haría por medio de este archivo, los niveles que tiene actualmente son: DEBUG, INFO, y ERROR.

## Author

* **Paul Andrés Arenas Cardona**
