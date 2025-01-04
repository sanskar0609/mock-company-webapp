package com.mockcompany.webapp.service;

import com.mockcompany.webapp.api.SearchReportResponse;
import com.mockcompany.webapp.model.ProductItem;
import com.mockcompany.webapp.data.ProductItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.*;

import static org.mockito.Mockito.*;

public class SearchServiceTest {

    private ProductItemRepository productItemRepository;
    private EntityManager entityManager;
    private SearchService searchService;

    @BeforeEach
    public void setUp() {
        // Mocking dependencies
        productItemRepository = mock(ProductItemRepository.class);
        entityManager = mock(EntityManager.class);
        searchService = new SearchService(productItemRepository, entityManager);
    }

    @Test
    public void testGenerateReport() {
        // Mocking the behavior of EntityManager.createQuery and getResultList

        // Mock the query for fetching all items
        Query query = mock(Query.class);
        when(entityManager.createQuery("SELECT item FROM ProductItem item")).thenReturn(query);
        when(query.getResultList()).thenReturn(new ArrayList<ProductItem>() {{
            add(new ProductItem());  // Mock a single product item
        }});

        // Mocking the query for matching product names and descriptions
        when(entityManager.createQuery("SELECT item.id from ProductItem item where item.name like '%cool%'"))
                .thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(1, 2));

        // Mocking other queries for other search terms
        when(entityManager.createQuery("SELECT item.id from ProductItem item where item.name like '%Cool%'"))
                .thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(3));

        when(entityManager.createQuery("SELECT item.id from ProductItem item where item.description like '%cool%'"))
                .thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(4));

        // Mocking report generation for other terms
        when(entityManager.createQuery("SELECT item FROM ProductItem item")).thenReturn(query);
        when(query.getResultList()).thenReturn(new ArrayList<ProductItem>() {{
            add(new ProductItem());  // Mock product item
        }});

        // Mock search term "Kids"
        when(entityManager.createQuery("SELECT item.id from ProductItem item where item.name like '%kids%'"))
                .thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(1, 2));

        // Now, call the method to test
        SearchReportResponse reportResponse = searchService.generateReport();

        // Assertions to verify the report content
        assertNotNull(reportResponse);
        assertEquals(1, reportResponse.getSearchTermHits().get("Cool"));
        assertEquals(2, reportResponse.getSearchTermHits().get("Kids"));
        assertEquals(2, reportResponse.getSearchTermHits().get("Perfect"));  // Replace with your expected result
        assertEquals(1, reportResponse.getProductCount());
    }
}
