# What is this
 
This is request normalizer for request signer code generator for external clients (FinTech) and WebFilter generator 
for OpenBanking to validate request signature.

It adds annotation processor that uses API definition YAML files to generate request normalizers that convert 3rd
party request to OpenBanking to String.

# Request conversion rules

String canonical (normalized) form of the request data is concatenated (in order):
1. request path + '&' delimiter
1. request headers in alphabetical order each with '&' delimiter
1. request query parameters in alphabetical order each with + '&' delimiter
1. request body

For example:
```
POST /payment?auth=8799879798&from=anton.brueckner
- Headers:
Amount=13.00
SourceIban=1231242314
- Body:
<payment><id>1234</id></payment>
```

Has canonical string:
```
/payment&Amount=13.00&SourceIban=1231242314&auth=8799879798&from=anton.brueckner&body=<payment><id>1234</id></payment>
```

Another example:
```
POST /payment?auth=8799879798&from=anton.brueckner
- Headers:
Amount=13.00
SourceIban=1231242314
```

Has canonical string:
```
/payment&Amount=13.00&SourceIban=1231242314&auth=8799879798&from=anton.brueckner&
```

Short canonical form of the request data is SHA-256 hash of the canonical string.

Note: Technically hash strength other than collision resistance is not of much importance here as the value
is going to be signed with JWS  