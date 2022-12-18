package nl.tudelft.sem.template.example.chain;

import commons.Job;

public interface Validator {

    void setNext(Validator handler);

    boolean handle(Job job) throws JobRejectedException;
}
