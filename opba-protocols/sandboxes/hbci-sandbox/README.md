# What is this

This is the default implementation of HBCI protocol Sandbox that imitates HBCI protocol banks.

# Running Sandbox outside the environment

**Note:** All operations presume you are **in the same directory as current file is**.  

## Building and running the HBCI-Sandbox using Docker (No Java required locally):
1. Build docker image:
```shell script
cd ../../.. && docker build . -f opba-protocols/sandboxes/hbci-sandbox/Dockerfile-no-java-needed -t adorsys/hbci-sandbox-local
```
2. Run docker image:
```shell script
docker run -p 8090:8090 adorsys/hbci-sandbox-local:latest
```
3. HBCI sandbox endpoint with POST operation accepting `application/octet-stream` or `text/plain` will be available at
`http://localhost:8090/hbci-mock/`

It will take some time as this will cause maven to download all dependencies into the container, later we will release
HBCI-Sandbox to public DockerHub.


## Building and running the HBCI-Sandbox locally:

1. Ensure you have Java JDK 8+ installed (either OpenJDK, AdoptOpenJDK, etc.)

2. Execute following command in the current folder
```shell script
../../../mvnw clean install -DskipTests
```

3. Run the Sandbox
```shell script
../../../mvnw spring-boot:run 
```
You should something like:
```shell script
2020-08-07 14:14:39.222  INFO 6976 --- [           main] o.f.j.s.i.a.AbstractAsyncExecutor        : Starting up the async job executor [org.flowable.spring.job.service.SpringAsyncExecutor].
2020-08-07 14:14:39.222  INFO 6976 --- [       Thread-3] o.f.j.s.i.a.AcquireAsyncJobsDueRunnable  : starting to acquire async jobs due
2020-08-07 14:14:39.222  INFO 6976 --- [       Thread-4] o.f.j.s.i.a.AcquireTimerJobsRunnable     : starting to acquire async jobs due
2020-08-07 14:14:39.222  INFO 6976 --- [       Thread-5] o.f.j.s.i.a.ResetExpiredJobsRunnable     : starting to reset expired jobs for engine bpmn
2020-08-07 14:14:39.261  INFO 6976 --- [           main] o.f.e.impl.cmd.ValidateV5EntitiesCmd     : Total of v5 deployments found: 0
2020-08-07 14:14:39.705  INFO 6976 --- [           main] o.s.s.c.ThreadPoolTaskScheduler          : Initializing ExecutorService 'taskScheduler'
2020-08-07 14:14:40.529  INFO 6976 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8090 (http) with context path ''
2020-08-07 14:14:40.531  INFO 6976 --- [           main] d.a.o.p.s.hbci.HbciServerApplication     : Started HbciServerApplication in 6.014 seconds (JVM running for 6.306)
```
in the console when application has started

4. HBCI-endpoint of Sandbox will be available at `http://localhost:8090/hbci-mock/`

