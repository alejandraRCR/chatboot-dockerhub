# 🤖 Mini Chatbot Conversacional 💬

Este proyecto integra un sistema de chatbot con microservicios de backend y una interfaz de usuario sencilla, diseñado para responder preguntas y aprender nuevas interacciones.

---

## 🐳 Despliegue y Orquestación con Docker

La solución se esta preparado para ser dockerizada para facilitar su despliegue y orquestación. Se han generado Dockerfiles específicos para cada componente, permitiendo construir imágenes eficientes para cada servicio.

### Componentes Dockerizados

* **`Dockerfile` para `chatboot.request` (Backend Chatbot):**
    Este Dockerfile empaqueta el microservicio principal del chatbot, que maneja la lógica de las conversaciones.
* **`Dockerfile` para `chatboot.security` (Backend Seguridad):**
    Este Dockerfile es para el microservicio encargado de la autenticación y autorización de usuarios mediante JWT.
* **`Dockerfile` para el Frontend:**
    Se ha creado un Dockerfile para el servidor web que sirve la interfaz de usuario de la aplicación.

### Orquestación con Docker Compose

La orquestación de todos los servicios se realiza a través de un archivo `docker-compose.yml`. Este archivo ha sido configurado respetando el orden correcto de despliegue y asegurando la comunicación entre ellos:

* El **Frontend** depende directamente de los microservicios backend para su correcto funcionamiento.
* Los microservicios de **Backend** se inician en el orden adecuado para establecer sus conexiones y dependencias internas (por ejemplo, `chatboot.security` se configura para comunicarse con `chatboot.request` a través de Feign Client).

**Nota sobre Comunicación y Despliegue:**
Para la comunicación interna entre los microservicios se ha utilizado **Feign Client**. Sin embargo, para entornos de producción que requieran despliegue automático, escalabilidad y una mejor visualización del estado de los microservicios, se recomienda encarecidamente evaluar soluciones como **ArgoCD**.

---

### 📋 Prerrequisitos

* **Docker Desktop** (o Docker Engine y Docker Compose CLI) instalado y en ejecución.
* **Apache Maven 3.2.5 o superior** (para compilar los JARs de los microservicios).
* **Java Development Kit (JDK) 18** (para compilar los microservicios).
* **Archivos de Wallet de Oracle Cloud:** Tu `wallet.zip` (o los archivos descomprimidos `cwallet.sso`, `tnsnames.ora`, `sqlnet.ora`, etc.) de tu base de datos Oracle Cloud. Debes colocar estos archivos en una carpeta llamada `oracle/wallets/chatboot` **en el mismo directorio donde se encuentra tu archivo `docker-compose.yml`**.

### ⚙️ Instrucciones de Despliegue

1.  **Compilar los Microservicios Backend:**
    * Navega a la raíz de cada directorio de microservicio (`backend-auth` y `backend-chat`) en tu terminal.
    * Ejecuta el siguiente comando para compilar el JAR ejecutable:
        ```bash
        mvn clean package
        ```
        Asegúrate de que esta operación sea exitosa y que los archivos `security-0.0.1-SNAPSHOT.jar` se generen en sus respectivas carpetas `target/`.

2.  **Configurar Variables de Entorno (Credenciales de Base de Datos):**
    * Abre el archivo `docker-compose.yml`.
    * Localiza las secciones `environment` para `backend-auth` y `backend-chat`.
    * **Reemplaza** `your_auth_db_password` y `your_chat_db_password` con las contraseñas reales de tu base de datos Oracle Cloud.
    * La ruta `TNS_ADMIN` ya está configurada para `/opt/oracle/wallets/chatboot/` dentro del contenedor, que es donde se montará tu wallet.

3.  **Construir y Ejecutar los Contenedores con Docker Compose:**
    * Navega al directorio donde se encuentra tu archivo `docker-compose.yml` en tu terminal.
    * Ejecuta el siguiente comando para construir las imágenes y levantar los servicios:
        ```bash
        docker compose up --build
        ```
    * Esto construirá las imágenes (`backend-auth`, `backend-chat`, `frontend`) y luego iniciará los contenedores en el orden de dependencia definido.

4.  **Acceder a la Aplicación:**
    * Una vez que los contenedores estén en ejecución, podrás acceder al frontend en tu navegador:
        [http://localhost:5001](http://localhost:5001)

---

## 🏛️ Arquitectura General

La solución se compone de los siguientes elementos principales:

* **Backend de Seguridad (`chatboot.security`):**
    * **Puerto de Ejecución:** `8090`
    * **Función:** Maneja la autenticación de usuarios (login), la generación y validación de tokens JWT. Se configuró **CORS** para aceptar peticiones solo desde el puerto `5001` (el frontend), diferente a su propio puerto.
* **Backend del Chatbot (`chatboot.request`):**
    * **Puerto de Ejecución:** `8091`
    * **Función:** Contiene la lógica principal del chatbot, incluyendo la búsqueda de preguntas, el procesamiento de palabras clave y la gestión de preguntas no respondidas.
    * **Nota:** Si el puerto 8091 está ocupado en tu entorno, debes cambiarlo en el archivo `application.properties` de este microservicio *antes* de compilarlo y desplegarlo.
* **Frontend (Aplicación Web Estática):**
    * **Puerto de Ejecución:** `5001`
    * **Tecnologías:** Desarrollado con HTML, CSS (TailwindCSS) y JavaScript Vanilla para una interfaz de usuario sencilla y directa.

---

## 🗄️ Base de Datos

### Conexión a Oracle Cloud

La base de datos se encuentra alojada en **Oracle Cloud**. La conexión se realiza a través de la configuración `TNS_ADMIN` y los archivos de wallet, que se montan desde tu sistema local al contenedor Docker.

###  Preguntas del Chatbot (Estado Inicial)

La base de datos contiene inicialmente 10 preguntas predefinidas que el chatbot puede responder:

1.  ¿Cuál es tu nombre?
2.  ¿Cómo estás hoy?
3.  ¿Qué hora es?
4.  ¿Cuál es la capital de Francia?
5.  ¿Quién descubrió América?
6.  ¿Puedes contar un chiste?
7.  ¿Cuál es la montaña más alta del mundo?
8.  ¿Cómo funciona la fotosíntesis?
9.  ¿Qué es la inteligencia artificial?
10. ... (La décima pregunta es la funcionalidad implícita de registro)

### 🧠 Funcionalidad del Chatbot

1.  **Búsqueda Exacta (Ignorando Mayúsculas/Minúsculas):**
    * El chatbot primero intenta encontrar una respuesta exacta a la pregunta, sin distinguir entre mayúsculas y minúsculas.

2.  **Búsqueda por Palabras Clave (`keywords`) con Desambiguación:**
    * Si no se encuentra una pregunta exacta, el sistema busca coincidencias con palabras clave definidas para las respuestas existentes.
    * **Nuevo Escenario:** Si una pregunta coincide con palabras clave que están asociadas a **múltiples posibles respuestas** (ej., la palabra "tiempo" en diferentes contextos), el chatbot informará al usuario que hay "posibles respuestas" y procederá a listar las opciones relevantes para que el usuario pueda elegir o clarificar.

3.  **Registro de Preguntas No Respondidas:**
    * Si una pregunta no se encuentra (ni exacta ni por palabras clave), el chatbot indica que necesita ser "entrenado" y **registra la pregunta** para una futura respuesta.

4.  **Gestión de Preguntas en Espera:**
    * Mientras una pregunta registrada no ha sido respondida por un administrador (o el sistema), si un usuario la vuelve a formular, el chatbot le informará que la pregunta **"está registrada pero aún no ha sido respondida"** (`PENDING_QUESTION`).

**Nota sobre Expansión:**
* Para este ejercicio, se validan escenarios clave de funcionalidad. Sin embargo, el potencial de un chatbot es vasto, incluyendo la integración de servicios avanzados de **aprendizaje automático (Machine Learning)** para respuestas más complejas y una comprensión más profunda del lenguaje natural.

---

##  Seguridad y Código

* El microservicio de seguridad (`chatboot.security`) se configuró para manejar la autenticación y autorización mediante **JWT (JSON Web Tokens)**.
* Se configuró **CORS** para asegurar que solo el frontend (puerto `5001`) pueda interactuar con los backends.
* Se empleó el menor código posible en los repositorios, utilizando las capacidades de los frameworks y evitando la necesidad de queries repetitivos.
* Las entidades Java están diseñadas para que sus nombres coincidan directamente con los nombres de las tablas en la base de datos.
* Se utilizó la librería **Lombok** para reducir al máximo el boilerplate code (código repetitivo).
* En el frontend, se manejaron los escenarios de interacción de forma muy sencilla con JavaScript Vanilla.
* Los usuarios para el login ya están creados en la base de datos. Por motivos de seguridad, las credenciales se proporcionarán al equipo de forma privada.

---
