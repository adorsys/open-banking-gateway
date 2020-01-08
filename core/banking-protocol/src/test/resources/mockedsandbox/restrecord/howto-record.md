## Prerequisites:
1. BasicTest sandbox started
2. tpp-ui started with  (src/app/consts.ts):
```typescript
export const Consts = {
  VALIDATION_ERR_HEADER: "X-VALIDATION",
  API_V1_URL_BASE: "http://localhost:28080/v1/"
};
```
3. Banking protocol started with (application.yml):
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/open_banking?currentSchema=banking_protocol
    username: postgres
    password: docker
    type: com.zaxxer.hikari.HikariDataSource
    driver-class-name: org.postgresql.Driver
    hikari:
      schema: banking_protocol
  jpa:
    hibernate:
      naming:
        physical-strategy: de.adorsys.opba.core.protocol.config.hibernate.PrefixAndSnakeCasePhysicalNamingStrategy
      # DDL is handled by Liquibase
      ddl-auto: validate
    open-in-view: false
  liquibase:
    enabled: true
    change-log: classpath:migration/master.xml
    parameters:
      table-prefix: opb_
      fill-with-mock-data: true

protocol:
  redirect:
    consent-accounts:
      ok: 'http://localhost:28080/v1/consents/confirm/accounts/#{context.getAction()}/sagas/#{execution.getRootProcessInstanceId()}'
      nok: 'http://localhost:8080/v1/consents/failed/accounts/#{context.getAction()}/sagas/#{execution.getRootProcessInstanceId()}'
    parameters:
      provide-more: 'http://localhost:5500/parameters/provide-more/#{execution.getId()}?q=#{urlSafe(context.getViolations().toString())}'
      provide-psu-password: 'http://localhost:5500/parameters/provide-psu-password/#{execution.getId()}'
      select-sca-method: 'http://localhost:5500/parameters/select-sca-method/#{execution.getId()}?q=#{urlSafe(context.getAvailableSca().toString())}'
      report-sca-result: 'http://localhost:5500/parameters/report-sca-result/#{execution.getId()}?q=#{urlSafe(context.getScaSelected().getAuthenticationType() + ":" + context.getScaSelected().getName())}'

pkcs12:
  keyStore: sample-qwac.keystore
  password: password

xs2a-profile:
  id: 53c47f54-b9a4-465a-8f77-bc6cd5f0cf46
  aspspName: adorsys xs2a
  bic: ADORSYS
  url: http://localhost:30000
  adapterId: adorsys-integ-adapter
  idpUrl:
  aspspScaApproaches:

flowable:
  process-definition-location-prefix: classpath*:/processes/**/
```
## Recording
Wiremock proxy to record requests:
```sh
java -jar 'wiremock-standalone-2.25.1.r' -port 38080 --proxy-all http://127.0.0.1:8080 --record-mappings --match-headers accept,psu-id,x-request-id,content-type,psu-ip-address --root-dir ../results
java -jar 'wiremock-standalone-2.25.1.r' --port 30000 --proxy-all http://127.0.0.1:20014 --record-mappings --match-headers accept,psu-id,x-request-id,content-type,psu-ip-address --root-dir ../results
java -jar 'wiremock-standalone-2.25.1.r' -port 28080 --proxy-all http://127.0.0.1:8080 --record-mappings --match-headers accept,psu-id,x-request-id,content-type,psu-ip-address --root-dir ../results/obg
```
Clean recorded directory (assuming your current directory is /results):
```sh
find . -type f -print0 | xargs -0 rm -f
``` 