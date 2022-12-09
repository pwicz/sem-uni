package nl.tudelft.sem.template.example.domain.processing;

import org.springframework.stereotype.Service;

@Service
public class RemovingJobsService {

    /**
     * Removes the job and frees the allocated resources.
     *
     * @param jobId ID of a job that is to be removed
     * @return true if the job was properly removed
     */
    public boolean removeJob(long jobId) {
        // 1. Search for Requests in the database

        // 2. Remove them and update the DaySummery objects
        System.out.println("Removing job " + jobId);
        // 3. Return true if everything went well
        return true;
    }
}
