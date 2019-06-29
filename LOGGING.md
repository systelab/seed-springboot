# Logging

Spring Boot has no mandatory logging dependency, except for the Commons Logging API, which is typically provided by the Spring starter. 

Spring Boot has a LoggingSystem abstraction that attempts to configure logging based on the content of the classpath. If Logback is available, that usually is, as it is included by the Spring starter, it is the first choice.

If the only change you need to make to logging is to set the levels of various loggers, you can do so in application.properties by using the "logging.level" prefix, as shown in the following example:

```
logging.level.org.springframework.web=DEBUG
logging.level.org.hibernate=ERROR
```

You can also set the location of a file to which to write the log (in addition to the console) by using "logging.file".

To configure the more fine-grained settings of a logging system, you need to use the native configuration format supported by the LoggingSystem in question. 

## Configure Logback

Even though the default configuration is useful, it’s most likely not enough for a production environment.

To include a Logback configuration with a different color and logging pattern, with separate specifications for console and file output, and with a decent rolling policy to avoid generating huge log files, you have to create a logback.xml (or logback-spring.xml, to take advantage of the templating features provided by Boot) file in the root of your classpath.

That's an example of a logback-spring.xml file:


```
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
 
    <property name="LOGS" value="./logs" />
 
    <appender name="Console"
        class="ch.qos.logback.core.ConsoleAppender">
        <layout class="ch.qos.logback.classic.PatternLayout">
            <Pattern>
                %black(%d{ISO8601}) %highlight(%-5level) [%blue(%t)] %yellow(%C{1.}): %msg%n%throwable
            </Pattern>
        </layout>
    </appender>
 
    <appender name="RollingFile"
        class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${LOGS}/spring-boot-logger.log</file>
        <encoder
            class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
            <Pattern>%d %p %C{1.} [%t] %m%n</Pattern>
        </encoder>
 
        <rollingPolicy
            class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <!-- rollover daily and when the file reaches 10 MegaBytes -->
            <fileNamePattern>${LOGS}/archived/spring-boot-logger-%d{yyyy-MM-dd}.%i.log
            </fileNamePattern>
            <timeBasedFileNamingAndTriggeringPolicy
                class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
                <maxFileSize>10MB</maxFileSize>
            </timeBasedFileNamingAndTriggeringPolicy>
        </rollingPolicy>
    </appender>
     
    <!-- LOG everything at INFO level -->
    <root level="info">
        <appender-ref ref="RollingFile" />
        <appender-ref ref="Console" />
    </root>
 
    <!-- LOG "com.baeldung*" at TRACE level -->
    <logger name="com.baeldung" level="trace" additivity="false">
        <appender-ref ref="RollingFile" />
        <appender-ref ref="Console" />
    </logger>
 
</configuration>
```

Check [Logback website](https://logback.qos.ch/) for more information.

## Logging in your application

In order to do some log, define a Logger field from the LoggerFactory and use the [methods provided](https://www.slf4j.org/api/org/slf4j/Logger.html) by the Object.

```
@Service
public class PatientMaintenanceService {

    private Logger logger = LoggerFactory.getLogger(PatientMaintenanceService.class);

    ...
}
```