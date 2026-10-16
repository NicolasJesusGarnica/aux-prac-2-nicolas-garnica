package com.example.auxprac2.controller;

import com.example.auxprac2.dto.BookResponse;
import com.example.auxprac2.dto.ErrorResponse;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("API REST de libros")
class BookControllerTest {

    private static final ObjectMapper MAPPER = JsonMapper.builder().findAndAddModules().build();

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = MAPPER;

    private String body(String title, String isbn, int year) {
        return """
                {
                  "title": "%s",
                  "author": "Gabriel Garcia Marquez",
                  "isbn": "%s",
                  "publicationYear": %d,
                  "category": "Realismo magico",
                  "available": true
                }
                """.formatted(title, isbn, year);
    }

    @Test
    @DisplayName("POST /api/books registra un libro y responde 201")
    void createReturnsCreated() throws Exception {
        String payload = body("Cien anios de soledad", "9780307474728", 1967);

        MvcResult result = mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").value("Cien anios de soledad"))
                .andExpect(jsonPath("$.available").value(true))
                .andReturn();

        BookResponse creado = objectMapper.readValue(result.getResponse().getContentAsString(), BookResponse.class);
        assertThat(creado.id()).isNotNull();
    }

    @Test
    @DisplayName("GET /api/books devuelve todos los libros")
    void findAllReturnsList() throws Exception {
        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("Ficciones", "9780306406157", 1962)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[*].isbn", Matchers.hasItem("9780306406157")));
    }

    @Test
    @DisplayName("GET /api/books/{id} devuelve el libro solicitado")
    void findByIdReturnsBook() throws Exception {
        long id = crearLibro("El amor en los tiempos del colera", "9780307389732");

        mockMvc.perform(get("/api/books/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.title").value("El amor en los tiempos del colera"));
    }

    @Test
    @DisplayName("GET /api/books/{id} responde 404 si el libro no existe")
    void findByIdReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/books/{id}", 999999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("No existe un libro con el id 999999"));
    }

    @Test
    @DisplayName("PUT /api/books/{id} actualiza el libro")
    void updateReturnsUpdatedBook() throws Exception {
        long id = crearLibro("Pedro Paramo", "9780802133908");

        mockMvc.perform(put("/api/books/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("Pedro Paramo (edicion revisada)", "9780802133908", 1971)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.title").value("Pedro Paramo (edicion revisada)"));
    }

    @Test
    @DisplayName("PUT /api/books/{id} responde 404 si el libro no existe")
    void updateReturnsNotFound() throws Exception {
        mockMvc.perform(put("/api/books/{id}", 999999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("Libro inexistente", "9780307474728", 1999)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("DELETE /api/books/{id} elimina el libro y responde 204")
    void deleteReturnsNoContent() throws Exception {
        long id = crearLibro("La casa de los espiritus", "9788401337208");

        mockMvc.perform(delete("/api/books/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/books/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/books/{id} responde 404 si el libro no existe")
    void deleteReturnsNotFound() throws Exception {
        mockMvc.perform(delete("/api/books/{id}", 999999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("POST /api/books responde 400 con datos inválidos")
    void createReturnsBadRequest() throws Exception {
        String invalido = """
                {
                  "title": "",
                  "author": "",
                  "isbn": "abc",
                  "publicationYear": 1200,
                  "category": "",
                  "available": true
                }
                """;

        MvcResult result = mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalido))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.fields.title").exists())
                .andExpect(jsonPath("$.fields.isbn").exists())
                .andReturn();

        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        assertThat(body.get("fields").has("author")).isTrue();
    }

    @Test
    @DisplayName("POST /api/books responde 409 si el isbn ya existe")
    void createReturnsConflict() throws Exception {
        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("Crónica de una muerte anunciada", "9781400034956", 1981)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("Otro libro con el mismo isbn", "9781400034956", 1981)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    @DisplayName("El endpoint de documentación responde 200")
    void openApiDocsAreAvailable() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("La estructura de error cumple el contrato de ErrorResponse")
    void errorResponseShapeIsConsistent() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/books/{id}", 999999))
                .andExpect(status().isNotFound())
                .andReturn();

        ErrorResponse error = objectMapper.readValue(result.getResponse().getContentAsString(), ErrorResponse.class);
        assertThat(error.status()).isEqualTo(404);
        assertThat(error.path()).isEqualTo("/api/books/999999");
        assertThat(error.timestamp()).isNotNull();
    }

    private long crearLibro(String title, String isbn) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body(title, isbn, 2000)))
                .andExpect(status().isCreated())
                .andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
    }
}