package nl.tudelft.sem.template.example.chain;

import commons.Account;
import commons.FacultyRequestModel;
import commons.FacultyResponseModel;
import commons.Job;
import nl.tudelft.sem.template.example.models.JobChainModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class FacultyValidator extends BaseValidator {

    @Override
    public boolean handle(JobChainModel jobChainModel) throws JobRejectedException {
        Job job = jobChainModel.getJob();
        Account role = jobChainModel.getAuthRole();
        String faculty = jobChainModel.getAuthFaculty();

        FacultyRequestModel requestModel = new FacultyRequestModel(job.getNetId().toString());
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<FacultyResponseModel> response = restTemplate
                .getForEntity("/faculty", FacultyResponseModel.class, requestModel);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new JobRejectedException("BAD_REQUEST");
        }
        FacultyResponseModel responseModel = response.getBody();
        if (responseModel == null) {
            throw new JobRejectedException("INVALID_BODY");
        }
        if (!responseModel.getFaculty().equals(faculty) || !role.equals(Account.Faculty)) {
            throw new JobRejectedException("BAD_CREDENTIALS");
        }
        if (jobChainModel.getDirectiveJob().equals(DirectiveJob.Reject)) {
            return false;
        }
        return super.checkNext(jobChainModel);
    }
}
