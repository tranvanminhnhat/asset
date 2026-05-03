package com.minhnhat.demo.common.client.service;

import com.minhnhat.demo.common.client.dto.response.SJCPriceResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@DisplayName("SJCClient Unit Tests")
class SJCClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private SCJClientImpl sjcClient;

    private static final String SJC_PRICE_URL = "https://sjc.com.vn/GoldPrice/Services/PriceService.ashx";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Set the URL using reflection
        ReflectionTestUtils.setField(sjcClient, "sjcPriceUrl", SJC_PRICE_URL);
    }

    @Test
    @DisplayName("Should successfully fetch SJC price data")
    void testGetSJCPriceSuccess() {
        // Arrange
        List<SJCPriceResponseDto.SJCPriceItemDto> priceItems = createMockPriceItems();
        SJCPriceResponseDto mockResponse = SJCPriceResponseDto.builder()
                .success(true)
                .latestDate("13:43 29/04/2026")
                .data(priceItems)
                .build();

        when(restTemplate.getForObject(eq(SJC_PRICE_URL), eq(SJCPriceResponseDto.class)))
                .thenReturn(mockResponse);

        // Act
        SJCPriceResponseDto result = sjcClient.getSJCPrice();

        // Assert
        assertNotNull(result);
        assertTrue(result.getSuccess());
        assertEquals("13:43 29/04/2026", result.getLatestDate());
        assertNotNull(result.getData());
        assertEquals(3, result.getData().size());
        verify(restTemplate, times(1)).getForObject(eq(SJC_PRICE_URL), eq(SJCPriceResponseDto.class));
    }

    @Test
    @DisplayName("Should handle API call failure with RestClientException")
    void testGetSJCPriceWithRestClientException() {
        // Arrange
        when(restTemplate.getForObject(eq(SJC_PRICE_URL), eq(SJCPriceResponseDto.class)))
                .thenThrow(new RestClientException("Connection refused"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            sjcClient.getSJCPrice();
        });

        assertTrue(exception.getMessage().contains("Failed to fetch SJC gold prices"));
        verify(restTemplate, times(1)).getForObject(eq(SJC_PRICE_URL), eq(SJCPriceResponseDto.class));
    }

    @Test
    @DisplayName("Should handle null response from API")
    void testGetSJCPriceWithNullResponse() {
        // Arrange
        when(restTemplate.getForObject(eq(SJC_PRICE_URL), eq(SJCPriceResponseDto.class)))
                .thenReturn(null);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            sjcClient.getSJCPrice();
        });

        assertTrue(exception.getMessage().contains("Failed to fetch SJC gold prices"));
        verify(restTemplate, times(1)).getForObject(eq(SJC_PRICE_URL), eq(SJCPriceResponseDto.class));
    }

    @Test
    @DisplayName("Should handle unsuccessful response (success=false)")
    void testGetSJCPriceWithUnsuccessfulResponse() {
        // Arrange
        SJCPriceResponseDto mockResponse = SJCPriceResponseDto.builder()
                .success(false)
                .latestDate(null)
                .data(null)
                .build();

        when(restTemplate.getForObject(eq(SJC_PRICE_URL), eq(SJCPriceResponseDto.class)))
                .thenReturn(mockResponse);

        // Act
        SJCPriceResponseDto result = sjcClient.getSJCPrice();

        // Assert
        assertNotNull(result);
        assertFalse(result.getSuccess());
        verify(restTemplate, times(1)).getForObject(eq(SJC_PRICE_URL), eq(SJCPriceResponseDto.class));
    }

    @Test
    @DisplayName("Should handle generic exception")
    void testGetSJCPriceWithGenericException() {
        // Arrange
        when(restTemplate.getForObject(eq(SJC_PRICE_URL), eq(SJCPriceResponseDto.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            sjcClient.getSJCPrice();
        });

        assertTrue(exception.getMessage().contains("Unexpected error fetching SJC prices"));
        verify(restTemplate, times(1)).getForObject(eq(SJC_PRICE_URL), eq(SJCPriceResponseDto.class));
    }

    @Test
    @DisplayName("Should parse response data correctly")
    void testGetSJCPriceDataParsing() {
        // Arrange
        SJCPriceResponseDto.SJCPriceItemDto item = SJCPriceResponseDto.SJCPriceItemDto.builder()
                .id(1)
                .typeName("Vàng SJC 1L, 10L, 1KG")
                .branchName("Hồ Chí Minh")
                .buy("163,000")
                .buyValue(new BigDecimal("163000000"))
                .sell("166,000")
                .sellValue(new BigDecimal("166000000"))
                .buyDiffer(null)
                .buyDifferValue(BigDecimal.ZERO)
                .sellDiffer(null)
                .sellDifferValue(BigDecimal.ZERO)
                .groupDate("/Date(-62135596800000)/")
                .build();

        SJCPriceResponseDto mockResponse = SJCPriceResponseDto.builder()
                .success(true)
                .latestDate("13:43 29/04/2026")
                .data(Arrays.asList(item))
                .build();

        when(restTemplate.getForObject(eq(SJC_PRICE_URL), eq(SJCPriceResponseDto.class)))
                .thenReturn(mockResponse);

        // Act
        SJCPriceResponseDto result = sjcClient.getSJCPrice();

        // Assert
        assertNotNull(result.getData());
        assertEquals(1, result.getData().size());

        SJCPriceResponseDto.SJCPriceItemDto retrievedItem = result.getData().get(0);
        assertEquals(1, retrievedItem.getId());
        assertEquals("Vàng SJC 1L, 10L, 1KG", retrievedItem.getTypeName());
        assertEquals("Hồ Chí Minh", retrievedItem.getBranchName());
        assertEquals(new BigDecimal("163000000"), retrievedItem.getBuyValue());
        assertEquals(new BigDecimal("166000000"), retrievedItem.getSellValue());
    }

    @Test
    @DisplayName("Should call RestTemplate with correct parameters")
    void testRestTemplateCallParameters() {
        // Arrange
        SJCPriceResponseDto mockResponse = SJCPriceResponseDto.builder()
                .success(true)
                .latestDate("13:43 29/04/2026")
                .data(Arrays.asList())
                .build();

        when(restTemplate.getForObject(anyString(), eq(SJCPriceResponseDto.class)))
                .thenReturn(mockResponse);

        // Act
        sjcClient.getSJCPrice();

        // Assert - verify correct URL and class type were used
        verify(restTemplate).getForObject(SJC_PRICE_URL, SJCPriceResponseDto.class);
    }

    /**
     * Helper method to create mock price items
     */
    private List<SJCPriceResponseDto.SJCPriceItemDto> createMockPriceItems() {
        SJCPriceResponseDto.SJCPriceItemDto item1 = SJCPriceResponseDto.SJCPriceItemDto.builder()
                .id(1)
                .typeName("Vàng SJC 1L, 10L, 1KG")
                .branchName("Hồ Chí Minh")
                .buy("163,000")
                .buyValue(new BigDecimal("163000000"))
                .sell("166,000")
                .sellValue(new BigDecimal("166000000"))
                .buyDiffer(null)
                .buyDifferValue(BigDecimal.ZERO)
                .sellDiffer(null)
                .sellDifferValue(BigDecimal.ZERO)
                .groupDate("/Date(-62135596800000)/")
                .build();

        SJCPriceResponseDto.SJCPriceItemDto item2 = SJCPriceResponseDto.SJCPriceItemDto.builder()
                .id(2)
                .typeName("Vàng SJC 5 chỉ")
                .branchName("Hồ Chí Minh")
                .buy("163,000")
                .buyValue(new BigDecimal("163000000"))
                .sell("166,020")
                .sellValue(new BigDecimal("166020000"))
                .buyDiffer(null)
                .buyDifferValue(BigDecimal.ZERO)
                .sellDiffer(null)
                .sellDifferValue(BigDecimal.ZERO)
                .groupDate("/Date(-62135596800000)/")
                .build();

        SJCPriceResponseDto.SJCPriceItemDto item3 = SJCPriceResponseDto.SJCPriceItemDto.builder()
                .id(3)
                .typeName("Vàng SJC 0.5 chỉ, 1 chỉ, 2 chỉ")
                .branchName("Hồ Chí Minh")
                .buy("163,000")
                .buyValue(new BigDecimal("163000000"))
                .sell("166,030")
                .sellValue(new BigDecimal("166030000"))
                .buyDiffer(null)
                .buyDifferValue(BigDecimal.ZERO)
                .sellDiffer(null)
                .sellDifferValue(BigDecimal.ZERO)
                .groupDate("/Date(-62135596800000)/")
                .build();

        return Arrays.asList(item1, item2, item3);
    }
}

