# Práctica 2 — API REST de Libros (Spring Boot)

API REST desarrollada con **Spring Boot 4.1.1** que permite administrar los libros de una
biblioteca mediante operaciones **CRUD**. La información se mantiene **temporalmente en
memoria** usando una estructura `List`, sin utilizar base de datos, tal como requiere el
enunciado.

- **Proyecto:** `aux-prac-2-nicolas-garnica`
- **Grupo / paquete:** `com.example` / `com.example.auxprac2`
- **Lenguaje:** Java 25
- **Build:** Gradle (Groovy)
- **Documentación:** Swagger UI / OpenAPI 3.1 (springdoc-openapi)

---

## 1. Requisitos

- JDK 25 instalado
- Git (para el versionamiento)

## 2. Cómo ejecutar el proyecto

### Desde IntelliJ IDEA

1. Importar el proyecto como proyecto **Gradle**.
2. Ejecutar la clase `AuxPrac2NicolasGarnicaApplication` (botón ▶ verde).

### Desde la terminal

```bash
# Linux / macOS
./gradlew bootRun

# Windows
gradlew.bat bootRun
```

La aplicación levanta en `http://localhost:8080`.

### Compilar y ejecutar las pruebas

```bash
./gradlew clean build
```

## 3. Swagger / OpenAPI

Con la aplicación corriendo:

| Documentación | URL |
|---|---|
| **Swagger UI (interfaz)** | http://localhost:8080/swagger-ui.html |
| Documento OpenAPI (JSON) | http://localhost:8080/v3/api-docs |

Desde Swagger UI se pueden ejecutar todos los endpoints directamente en el navegador
(*Try it out*). Antes de entregar se deben verificar todos los endpoints de esta manera.

## 4. Endpoints disponibles

| Método  | Endpoint            | Descripción                        | Respuesta exitosa |
|---------|---------------------|------------------------------------|-------------------|
| `POST`  | `/api/books`        | Registrar un libro                 | `201 Created` + libro creado |
| `GET`   | `/api/books`        | Obtener todos los libros           | `200 OK` + lista de libros |
| `GET`   | `/api/books/{id}`   | Obtener un libro por ID            | `200 OK` + libro |
| `PUT`   | `/api/books/{id}`   | Actualizar un libro                | `200 OK` + libro actualizado |
| `DELETE`| `/api/books/{id}`   | Eliminar un libro                  | `204 No Content` |

### Códigos de error

| Código | Cuándo ocurre |
|--------|---------------|
| `400 Bad Request` | El JSON enviado no cumple las validaciones o el `id` no es numérico |
| `404 Not Found`   | El libro solicitado no existe (consultar, actualizar o eliminar) |
| `409 Conflict`    | El `isbn` ya está registrado en otro libro |
| `500 Internal Server Error` | Error inesperado (ver consola) |

Formato de la respuesta de error:

```json
{
  "timestamp": "2026-03-01T10:15:30",
  "status": 404,
  "message": "No existe un libro con el id 99",
  "path": "/api/books/99",
  "fields": null
}
```

En errores de validación (`400`) el campo `fields` contiene el detalle por atributo:

```json
{
  "timestamp": "2026-03-01T10:15:30",
  "status": 400,
  "message": "Los datos enviados no son válidos",
  "path": "/api/books",
  "fields": {
    "isbn": "El isbn debe tener entre 10 y 13 digitos numericos",
    "title": "El titulo es obligatorio"
  }
}
```

## 5. Modelo de datos

Atributos del libro:

| Atributo          | Tipo    | Reglas de validación |
|-------------------|---------|----------------------|
| `id`              | Long    | Generado por el servidor, no se envía |
| `title`           | String  | Obligatorio, máx. 150 caracteres |
| `author`          | String  | Obligatorio, máx. 120 caracteres |
| `isbn`            | String  | Obligatorio, entre 10 y 13 dígitos, único |
| `publicationYear` | Integer | Obligatorio, entre 1450 y 2100 |
| `category`        | String  | Obligatorio, máx. 80 caracteres |
| `available`       | Boolean | Obligatorio |

### Ejemplo de petición

```json
{
  "title": "Cien años de soledad",
  "author": "Gabriel García Márquez",
  "isbn": "9780307474728",
  "publicationYear": 1967,
  "category": "Realismo mágico",
  "available": true
}
```

### Ejemplo de respuesta

```json
{
  "id": 1,
  "title": "Cien años de soledad",
  "author": "Gabriel García Márquez",
  "isbn": "9780307474728",
  "publicationYear": 1967,
  "category": "Realismo mágico",
  "available": true
}
```

## 6. Estructura del proyecto

```
src/main/java/com/example/auxprac2/
├── AuxPrac2NicolasGarnicaApplication.java   # Punto de entrada
├── config/
│   └── OpenApiConfig.java                    # Configuración de la documentación OpenAPI
├── controller/
│   ├── BookController.java                   # Endpoints REST (solo coordinan y devuelven respuestas HTTP)
│   └── GlobalExceptionHandler.java           # Traduce excepciones a códigos HTTP
├── dto/
│   ├── BookRequest.java                      # Datos de entrada + validaciones
│   ├── BookResponse.java                     # Datos de salida
│   └── ErrorResponse.java                    # Respuesta de error estandarizada
├── exception/
│   ├── ResourceNotFoundException.java        # → HTTP 404
│   └── DuplicateResourceException.java       # → HTTP 409
├── model/
│   └── Book.java                             # Entidad de dominio
└── service/
    ├── BookService.java                      # Contrato del servicio
    └── BookServiceImpl.java                  # Lógica de negocio + almacenamiento en memoria (List)
```

**Separación de responsabilidades:** el `Controller` solo gestiona HTTP, el `Service`
contiene toda la lógica de negocio y el almacenamiento, el `Model` representa la entidad
y el `DTO` define los datos que viajan por la API.

## 7. Pruebas

El proyecto incluye pruebas automatizadas:

- `BookServiceImplTest` — pruebas unitarias de la lógica de negocio (crear, buscar,
  actualizar, eliminar y los casos de recurso inexistente).
- `BookControllerTest` — pruebas de integración con `MockMvc` que verifican los códigos
  HTTP de cada endpoint (`201`, `200`, `204`, `400`, `404`, `409`) y la disponibilidad de
  `/v3/api-docs`.

```bash
./gradlew test
```

## 8. Notas

- No se utiliza base de datos. Los datos se pierden al detener la aplicación.
- No se utilizan librerías de Lombok: las clases usan `record` de Java.
- El proyecto debe publicarse en un repositorio de GitHub llamado
  `aux-prac-2-nicolas-garnica` y se entrega únicamente el enlace.