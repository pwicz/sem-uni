package nl.tudelft.sem.template.example.chain;

import commons.Faculty;
import commons.Job;
import commons.Resource;
import java.time.LocalDate;
import java.util.List;
import nl.tudelft.sem.template.example.models.JobChainModel;


public class FacultyResourceValidator extends BaseResourceValidator {

    @Override
    public boolean handle(JobChainModel jobChainModel) throws JobRejectedException {
        Job job = jobChainModel.getJob();
        List<Faculty> faculty = jobChainModel.getAuthFaculty();
        LocalDate localDate = LocalDate.now(); // TODO: this has to be changed to job schedule time
        List<Resource> resources = getFacultyResources(faculty, localDate);
        int cpuAvailable = resources.stream().mapToInt(Resource::getCpu).sum();
        int gpuAvailable = resources.stream().mapToInt(Resource::getGpu).sum();
        int memoryAvailable = resources.stream().mapToInt(Resource::getMem).sum();
        if (job.getCpuUsage() > cpuAvailable || job.getGpuUsage() > gpuAvailable
                || job.getMemoryUsage() > memoryAvailable) {
            return super.checkNext(jobChainModel);
        }
        return true; // end of chain of responsibility
    }


}
