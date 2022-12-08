Agenda for next time!
Next week we meet from 8:45.
Acronym of project is DB (from Delft Blue)

Feedback on report:

- diagram: it's good we have it, but the format must be more specific - microservices must be labeled with subsystem written under the name of the microservice. In the microservice add components - if it does more than one thing it needs more services.
- model databases: which microservices will interact with databases - put names of dbs
- use bulls and sockets for connections between microservices
- document: explain how security will work: will we validate the token in each microservice, or have a gateway which forwards the requests if necessary. |||
- how microservices communicate between each other (explain) - we can refer to the diagram, explain with which microservice interacts and with which not |||
- more details on how the code will be structured (what models, controllers etc.)
- for each microservice explain its architecture (doesn't have to be based on service if we think there is a better way) and add its functionality

///

Before 12AM on Wednesday: will be feedback
Before 6PM on Wednesday: might be feedback
After: no feedback

- we're focusing on small details too much, we must have a working system that interach with each other (unless the edge case is mentioned in the assignment)

TODO:
list of requirements of issues we want to create (don't be too vague): put it on gitlab

We should start coding from this week:

- everyone codes equally
- decide on at least 2 design patterns beforehand so that we can integrate it as we go: inform the TA about the choice

upload the notes to Gitlab
For end of each sprint do the retrospective!!

# Add design paterns to the report

=====

gradle build: specify port for each microservice
Grading on code: classes, tests, comments
How we manage Gitlab
