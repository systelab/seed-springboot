# Metrics

In SpringBoot, Actuator is used to expose operational information about the running application, like health, metrics, info, dump, or env.

## Predefined Endpoints

Some predefined wendopoint are:

- **/actuator/auditevents**: Lists security audit-related events such as user login/logout. Also, we can filter by principal or type among others fields.
- **/actuator/beans**: Returns all available beans in our BeanFactory. Unlike /auditevents, it doesn't support filtering.
- **/actuator/conditions**: Formerly known as /autoconfig, builds a report of conditions around auto-configuration.
- **/actuator/configprops**: Allows us to fetch all @ConfigurationProperties beans.
- **/actuator/env**: Returns the current environment properties. Additionally, we can retrieve single properties.
- **/actuator/flyway**: Provides details about our Flyway database migrations.
- **/actuator/health**: Summarises the health status of our application.
- **/actuator/heapdump**: Builds and returns a heap dump from the JVM used by our application.
- **/actuator/info**: Returns general information. It might be custom data, build information or details about the latest commit.
- **/actuator/liquibase**: Behaves like /flyway but for Liquibase.
- **/actuator/logfile**: Returns ordinary application logs.
- **/actuator/loggers**: Enables us to query and modify the logging level of our application.
- **/actuator/metrics**: Details metrics of our application. This might include generic metrics as well as custom ones.
- **/actuator/prometheus**: Returns metrics like the previous one, but formatted to work with a Prometheus server.
- **/actuator/scheduledtasks**: Provides details about every scheduled task within our application.
- **/actuator/sessions**: Lists HTTP sessions given we are using Spring Session.
- **/actuator/threaddump**: Dumps the thread information of the underlying JVM.