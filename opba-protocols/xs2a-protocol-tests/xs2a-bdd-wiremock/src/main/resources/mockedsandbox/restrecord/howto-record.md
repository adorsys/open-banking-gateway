## Prerequisites:
1. BasicTest sandbox started
1. OpenBankingEmbeddedApplication started with updated Sandbox port to `30000`

## Recording
Wiremock proxy to record requests from OpenBankingGateway to Sandbox:
```sh
java -jar 'wiremock-standalone-2.25.1.r' --port 30000 --proxy-all http://127.0.0.1:20014 --record-mappings --match-headers accept,psu-id,x-request-id,content-type,psu-ip-address --root-dir ../results
```

Clean recorded directory (assuming your current directory is /results):
```sh
find . -type f -print0 | xargs -0 rm -f
``` 
