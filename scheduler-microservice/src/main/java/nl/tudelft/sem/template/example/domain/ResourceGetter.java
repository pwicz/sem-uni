package nl.tudelft.sem.template.example.domain;

import commons.FacultyResource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;


public class ResourceGetter {
    private final transient RestTemplate restTemplate;
    private final transient String resourcesUrl;

    public ResourceGetter(RestTemplate restTemplate, String resourcesUrl) {
        this.restTemplate = restTemplate;
        this.resourcesUrl = resourcesUrl;
    }

    /**
     * Retrieves available resources that can be used by a job of specified faculty in a given day.
     *
     * @param faculty faculty of a job that is to use the resources
     * @param day day when the resources should be used
     * @return list of available resources
     */
    public List<FacultyResource> getAvailableResources(String faculty, LocalDate day) {
        ResponseEntity<FacultyResource[]> facultyResourcesResponse = restTemplate.getForEntity(resourcesUrl
                + "/resources?faculty=" + faculty + "&day=" + day, FacultyResource[].class);

        if (!facultyResourcesResponse.hasBody()) {
            return new ArrayList<>();
        }

        return Arrays.asList(Objects.requireNonNull(facultyResourcesResponse.getBody()));
    }
}
