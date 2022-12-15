# Design patterns:

- Factory: Data returned by the application back to the client (or between the services) might have to be in the future in different formats than JSON (for example XML), so using a factory makes it easy to add new formats in the future.
- Strategy: Depending on the type of user, different methods and implementation will be available for them. 
- Chain of Responsibility: Scheduler first checks the authentication and then if the job can be scheduled
