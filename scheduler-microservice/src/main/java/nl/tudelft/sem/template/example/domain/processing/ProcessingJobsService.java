package nl.tudelft.sem.template.example.domain.processing;

import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.Queue;

// DUMMY CLASS: must be removed and replaced with the real Job
// class that is also used in the Jobs controller
class Job{
    int id;
}

@Service
public class ProcessingJobsService {

    Queue<Job> jobsToProcess;

    ProcessingJobsService(){
        jobsToProcess = new LinkedList<>();
    }

    public int addToQueue(Job job){
        jobsToProcess.add(job);
        return jobsToProcess.size();
    }
}
