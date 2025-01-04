package com.mockcompany.webapp.service;

import com.mockcompany.webapp.api.SearchReportResponse;
import com.mockcompany.webapp.data.ProductItemRepository;
import com.mockcompany.webapp.model.ProductItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class SearchService {


    private final EntityManager entityManager;

    @Autowired
    public SearchService(ProductItemRepository productItemRepository, EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    // Method for handling the report search logic
    public SearchReportResponse generateReport() {
        Map<String, Integer> searchTermHits = new HashMap<>();
        List<Number> matchingIds = getMatchingIds("cool");

        searchTermHits.put("Cool", matchingIds.size());

        int kidCount = 0;
        int perfectCount = 0;
        int amazingCount = 0;

        // Create regex for "kids"
        Pattern kidPattern = Pattern.compile(".*[kK][iI][dD][sS].*");
        List<ProductItem> allItems = entityManager.createQuery("SELECT item FROM ProductItem item").getResultList();

        for (ProductItem item : allItems) {
            // Count "Kids"
            if (kidPattern.matcher(item.getName()).matches() || kidPattern.matcher(item.getDescription()).matches()) {
                kidCount++;
            }
            // Count "Perfect"
            if (item.getName().toLowerCase().contains("perfect") || item.getDescription().toLowerCase().contains("perfect")) {
                perfectCount++;
            }
            // Count "Amazing"
            if (item.getName().toLowerCase().contains("amazing") || item.getDescription().toLowerCase().contains("amazing")) {
                amazingCount++;
            }
        }

        searchTermHits.put("Kids", kidCount);
        searchTermHits.put("Perfect", perfectCount);
        searchTermHits.put("Amazing", amazingCount);

        // Prepare and return SearchReportResponse
        SearchReportResponse reportResponse = new SearchReportResponse();
        reportResponse.setSearchTermHits(searchTermHits);
        reportResponse.setProductCount(allItems.size());

        return reportResponse;
    }

    // Helper method to fetch matching ids based on search term
    private List<Number> getMatchingIds(String searchTerm) {
        List<Number> matchingIds = new ArrayList<>();
        matchingIds.addAll(
                this.entityManager.createQuery("SELECT item.id from ProductItem item where item.name like :searchTerm")
                        .setParameter("searchTerm", "%" + searchTerm + "%")
                        .getResultList()
        );
        matchingIds.addAll(
                this.entityManager.createQuery("SELECT item.id from ProductItem item where item.description like :searchTerm")
                        .setParameter("searchTerm", "%" + searchTerm + "%")
                        .getResultList()
        );
        return new ArrayList<>(new HashSet<>(matchingIds)); // Remove duplicates
    }

    // Method for handling search query logic

    public List<ProductItem> searchProducts(String query, Iterable<ProductItem> allItems) {
        return Collections.emptyList(); // This will break tests
    }

}
