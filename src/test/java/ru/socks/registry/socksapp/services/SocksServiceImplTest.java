package ru.socks.registry.socksapp.services;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import ru.socks.registry.socksapp.models.Socks;
import ru.socks.registry.socksapp.repositories.SocksRepository;
import ru.socks.registry.socksapp.utils.QuantityFilterType;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Socks Service работает, когда")
@DisplayNameGeneration(value = DisplayNameGenerator.ReplaceUnderscores.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class SocksServiceImplTest {

    @Mock
    SocksRepository socksRepository;

    @InjectMocks
    SocksServiceImpl socksService;

    private Socks socksOne;
    private Socks socksTwo;

    @BeforeEach
    void setUp() {
        socksOne = new Socks();
        socksTwo = new Socks();

        socksOne.setId(1L);
        socksOne.setColor("Black");
        socksOne.setQuantity(12);

        socksTwo.setId(2L);
        socksTwo.setColor("White");
        socksTwo.setQuantity(21);
    }

    @AfterEach
    void tearDown() {
        socksOne = null;
        socksTwo = null;
    }

    @Test
    @Order(1)
    @DisplayName("создается новая запись")
    void create_socks() {
        when(socksRepository.save(any(Socks.class))).thenReturn(socksOne);

        assertEquals(socksOne, socksService.createSocks(socksOne));
        assertEquals(socksOne.getColor(), socksService.createSocks(socksOne).getColor());
        assertEquals(socksOne.getQuantity(), socksService.createSocks(socksOne).getQuantity());

        assertNotNull(socksOne.getId());

        verify(socksRepository, times(3)).save(any(Socks.class));
    }

    @Test
    @Order(2)
    @DisplayName("получаем список всех существующих записей")
    void get_all_socks() {
        when(socksRepository.findAll()).thenReturn(Arrays.asList(socksOne, socksTwo));

        List<Socks> socks = socksService.getAllSocks();

        assertNotNull(socks);
        assertEquals(2, socks.size());
        assertTrue(socks.contains(socksOne));

        verify(socksRepository, times(1)).findAll();
    }

    @Test
    @Order(3)
    @DisplayName("получаем список записей с использованием фильтров - Больше, чем")
    void get_filtered_socks_more_than() {
        List<Socks> allSocks = Arrays.asList(socksOne, socksTwo);

        when(socksRepository.findAll(any(Specification.class))).thenReturn(allSocks);

        String color = "White";
        Integer quantity = 21;
        QuantityFilterType filterType = QuantityFilterType.MORE_THAN;

        List<Socks> filteredSocks = socksService.getFilteredSocks(color, quantity, filterType);

        assertNotNull(filteredSocks);

        assertEquals(2, filteredSocks.size());
        assertTrue(filteredSocks.contains(socksOne));
        assertTrue(filteredSocks.contains(socksTwo));

        verify(socksRepository, times(1)).findAll(any(Specification.class));
    }

    @Test
    @Order(4)
    @DisplayName("получаем список записей с использованием фильтров - Равно")
    void get_filtered_socks_equal() {
        List<Socks> allSocks = Arrays.asList(socksOne, socksTwo);

        when(socksRepository.findAll(any(Specification.class))).thenReturn(allSocks);

        String color = "White";
        Integer quantity = 21;
        QuantityFilterType filterType = QuantityFilterType.EQUAL;

        List<Socks> filteredSocks = socksService.getFilteredSocks(color, quantity, filterType);

        assertNotNull(filteredSocks);

        assertEquals(2, filteredSocks.size());
        assertTrue(filteredSocks.contains(socksOne));
        assertTrue(filteredSocks.contains(socksTwo));

        verify(socksRepository, times(1)).findAll(any(Specification.class));
    }

    @Test
    @Order(5)
    @DisplayName("обновляется существующая запись по заданному id")
    void update_existing_socks_by_id() {
        Socks updatedSocks = Socks.builder()
                .color("Black")
                .quantity(12)
                .build();

        when(socksRepository.findById(socksOne.getId())).thenReturn(Optional.of(socksOne));
        when(socksRepository.save(any(Socks.class))).thenReturn(updatedSocks);

        Socks result = socksService.updateSocks(socksOne.getId(), updatedSocks);

        assertNotNull(result);

        assertEquals(updatedSocks.getColor(), result.getColor());
        assertEquals(updatedSocks.getQuantity(), result.getQuantity());

        verify(socksRepository, times(1)).findById(socksOne.getId());
        verify(socksRepository, times(1)).save(any(Socks.class));
    }

    @Test
    @Order(6)
    @DisplayName("удаляется запись по заданному id")
    void delete_socks_by_id() {
        when(socksRepository.findById(socksOne.getId())).thenReturn(Optional.of(socksOne));

        socksService.deleteSocksById(socksOne.getId());

        verify(socksRepository, times(1)).findById(socksOne.getId());
        verify(socksRepository, times(1)).deleteById(socksOne.getId());
    }
}