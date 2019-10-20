# Health

Using actuator, you can use health information to check the status of your running application. 

It is often used by monitoring software to alert someone when a production system goes down. 

The information exposed by the health endpoint depends on the management.endpoint.health.show-details which can be configured with one of the following values:

- never
- when-authorized
- always

## REST endpoint

Head to /actuator/health to get the health check status.

## Custom health check

In order to add a custom health check create a new component implementing HealthIndicator and overriding the health() method

```java
@Component
public class MyHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        if (!someCondition()) {
            return Health.down().withDetail("Some Key", "Not Available").build();
        }
        return Health.up().withDetail("Some Key", "Available").build();
    }
}

```