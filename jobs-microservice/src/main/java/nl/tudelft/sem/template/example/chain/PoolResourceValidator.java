package nl.tudelft.sem.template.example.chain;

import commons.Job;
import commons.Resource;
import nl.tudelft.sem.template.example.models.JobChainModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

public class PoolResourceValidator extends BaseValidator {

    @Override
    public boolean handle(JobChainModel jobChainModel) throws JobRejectedException {
        Job job = jobChainModel.getJob();
        String faculty = jobChainModel.getAuthFaculty();
        LocalDate localDate = LocalDate.now(); // TODO: this has to be changed to job schedule time
        String requestPath = "/resources?faculty=" + "pool" + "&day=" + localDate;
        ResponseEntity<Resource> resourceResponseFaculty = getFacultyResource(faculty, localDate);
        ResponseEntity<Resource> resourceResponsePool = getFacultyResource("pool", localDate);
        if (!resourceResponseFaculty.getStatusCode().is2xxSuccessful()
                || !resourceResponsePool.getStatusCode().is2xxSuccessful()) {
            throw new JobRejectedException("BAD_REQUEST");
        }
        Resource resourceFaculty = resourceResponseFaculty.getBody();
        Resource resourcePool = resourceResponsePool.getBody();
        if (resourceFaculty == null || resourcePool == null) {
            throw new JobRejectedException("INVALID_BODY");
        }   
        if (job.getCpuUsage() > (resourceFaculty.getCpu() + resourcePool.getCpu())
                || job.getGpuUsage() > (resourceFaculty.getGpu() + resourcePool.getGpu())
                || job.getMemoryUsage() > (resourceFaculty.getMem() + resourcePool.getMem())) {
            return false;
        }
        return super.checkNext(jobChainModel);
    }

    public ResponseEntity<Resource> getFacultyResource(String faculty, LocalDate localDate) {
        RestTemplate restTemplate = new RestTemplate();
        String requestPath = "/resources?faculty=" + faculty + "&day=" + localDate;
        return restTemplate
                .getForEntity(requestPath, Resource.class, "");
    }
}
