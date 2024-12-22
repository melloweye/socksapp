package ru.socks.registry.socksapp.controllers;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import ru.socks.registry.socksapp.exceptions.SocksNotFoundException;
import ru.socks.registry.socksapp.models.Socks;
import ru.socks.registry.socksapp.services.SocksServiceImpl;
import ru.socks.registry.socksapp.utils.QuantityFilterType;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SocksController.class)
@DisplayNameGeneration(value = DisplayNameGenerator.ReplaceUnderscores.class)
class SocksControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SocksServiceImpl socksService;

    Socks socksOne;
    Socks socksTwo;

    @BeforeEach
    void setUp() {
        socksOne = Socks.builder()
                .id(1L)
                .color("Black")
                .quantity(12)
                .cottonPart(21)
                .build();

        socksTwo = Socks.builder()
                .id(2L)
                .color("White")
                .quantity(21)
                .cottonPart(12)
                .build();
    }

    @AfterEach
    void tearDown() {
        socksOne = null;
        socksTwo = null;
    }

    @Test
    void upload_socks_using_file_CSV() throws Exception {
        String csvContent = """
                color,quantity,cottonPart
                Black,12,21
                White,21,12""";

        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "socks.csv",
                "text/scv",
                csvContent.getBytes()
        );

        List<Socks> socksList = List.of(socksOne, socksTwo);

        doNothing().when(socksService).saveAll(socksList);

        mockMvc.perform(multipart("/api/socks/batch")
                        .file(mockFile))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].color").value("Black"))
                .andExpect(jsonPath("$[0].quantity").value(12));

        verify(socksService, times(1)).saveAll(anyList());
    }

    @Test
    void get_all_socks() throws Exception {
        String color = "White";
        Integer quantity = 21;
        QuantityFilterType filterType = QuantityFilterType.LESS_THAN;

        List<Socks> socksList = List.of(socksTwo);

        when(socksService.getFilteredSocks(color, quantity, filterType)).thenReturn(socksList);

        mockMvc.perform(get("/api/socks")
                        .param("color", color)
                        .param("quantity", String.valueOf(quantity))
                        .param("filterType", filterType.name())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].color").value("White"));

        verify(socksService, times(1)).getFilteredSocks(color, quantity, filterType);
    }

    @Test
    void add_socks() throws Exception {
        when(socksService.createSocks(any(Socks.class))).thenReturn(socksOne);

        String socksJson = "{\"color\":\"Black\",\"quantity\":12,\"cottonPart\":21}";

        mockMvc.perform(post("/api/socks/income")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(socksJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.color").value("Black"))
                .andExpect(jsonPath("$.quantity").value(12))
                .andExpect(jsonPath("$.cottonPart").value(21));

        verify(socksService, times(1)).createSocks(any(Socks.class));

    }

    @Test
    void delete_socks_by_id() throws Exception {
        long id = 1L;

        doNothing().when(socksService).deleteSocksById(id);

        mockMvc.perform(delete("/api/socks/outcome/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(socksService, times(1)).deleteSocksById(id);
    }

    @Test
    void delete_socks_by_id_NotFound() throws Exception {
        long id = 2L;

        doThrow(new SocksNotFoundException("Socks with id = " + id + " not found"))
                .when(socksService).deleteSocksById(id);

        mockMvc.perform(delete("/api/socks/outcome/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Socks with id = " + id + " not found"));

        verify(socksService, times(1)).deleteSocksById(id);
    }

    @Test
    void update_existing_socks_by_id() throws Exception {
        Long id = 1L;

        Socks updatedSocks = Socks.builder()
                .id(id)
                .color("Red")
                .quantity(12)
                .cottonPart(21)
                .build();

        when(socksService.updateSocks(eq(id), any(Socks.class))).thenReturn(updatedSocks);

        mockMvc.perform(put("/api/socks/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"color\":\"Red\",\"quantity\":12,\"cottonPart\":21}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.color").value("Red"))
                .andExpect(jsonPath("$.quantity").value(12))
                .andExpect(jsonPath("$.cottonPart").value("21"));

        verify(socksService, times(1)).updateSocks(eq(id), any(Socks.class));
    }
}