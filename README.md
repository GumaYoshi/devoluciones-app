# \# DevolucionesApp

# 

# MVP Full Stack para digitalizar la gestión de solicitudes de devolución

# por pagos duplicados o realizados en exceso.

# 

# La solución incluye una API REST en Java 21 con Spring Boot, persistencia

# en PostgreSQL, carga masiva CSV y una aplicación frontend desarrollada

# con Angular 17.

# 

# \## Stack tecnológico

# 

# \### Backend

# 

# \- Java 21

# \- Spring Boot 4

# \- Spring MVC

# \- Spring Data JPA

# \- Bean Validation

# \- PostgreSQL 16

# \- Flyway

# \- Maven

# \- JUnit 5

# \- Mockito

# 

# \### Frontend

# 

# \- Angular 17

# \- Standalone Components

# \- Reactive Forms

# \- HttpClient

# \- RxJS

# \- Signals

# \- Lazy Loading

# \- Functional Guards

# \- Functional Interceptors

# 

# \### Infraestructura

# 

# \- Docker

# \- Docker Compose

# 

# \## Funcionalidades implementadas

# 

# \### Solicitudes

# 

# \- Registro manual de solicitudes.

# \- Consulta de solicitudes.

# \- Consulta individual por ID.

# \- Actualización de solicitudes en estado BORRADOR.

# \- Validación de folios y referencias bancarias únicas.

# \- Validación de RUT chileno mediante módulo 11.

# \- Validación de monto mayor que 0 y menor o igual a 10.000.000 CLP.

# \- Máquina de estados centralizada.

# \- Historial de transiciones.

# \- Manejo global de errores HTTP.

# \- Respuestas 400, 404 y 409 controladas.

# 

# \### Carga masiva

# 

# \- Carga de archivos CSV separados por punto y coma.

# \- Validación de encabezado.

# \- Validación por fila.

# \- Procesamiento tolerante a errores.

# \- Registro de errores por número de fila.

# \- Resumen de registros exitosos y rechazados.

# \- Prevención del reprocesamiento mediante hash SHA-256.

# \- Prevención de duplicados mediante referencia bancaria.

# \- Límite configurable de filas.

# \- Límite máximo de archivo de 10 MB.

# 

# \### Frontend

# 

# \- Login simulado para ANALISTA y SUPERVISOR.

# \- Flujo de token preparado mediante interceptor.

# \- Guard de autenticación.

# \- Componentes standalone.

# \- Lazy loading.

# \- Bandeja de solicitudes.

# \- Filtros por RUT y estado.

# \- Badges visuales por estado.

# \- Formulario reactivo de creación y edición.

# \- Validación de RUT, monto y campos obligatorios.

# \- Detalle de solicitud.

# \- Historial de transiciones.

# \- Botones visibles según estado y rol.

# \- Carga masiva CSV.

# \- Visualización del resumen y errores por fila.

# 

# \## Usuarios simulados

# 

# \### Analista

# 

# ```text

# usuario: analista1

# contraseña: analista123

