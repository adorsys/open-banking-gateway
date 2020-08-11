To test image deployment and promotion one can start 2 Docker registries.

- Creating self-signed certs in `docker/source_docker/certs`, `docker/target_docker/certs`
1. `cd docker/source_docker/certs && openssl req -new -x509 -nodes -sha1 -days 365 -key domain.key -out domain.crt && chmod 400 domain.key`
1. `cd docker/target_docker/certs && openssl req -new -x509 -nodes -sha1 -days 365 -key domain.key -out domain.crt && chmod 400 domain.key`

- Creating users in for Docker-registries:
1. `docker run --rm -ti xmartlabs/htpasswd source_username password > docker/source_docker/auth/htpasswd`
1. `docker run --rm -ti xmartlabs/htpasswd target_username password > docker/target_docker/auth/htpasswd`

- Running registries:

```shell script
docker run -d \
  --restart=always \
  --name source_registry \
  -v `pwd`/docker/source_docker/auth:/auth \
  -v `pwd`/docker/source_docker/certs:/certs \
  -e REGISTRY_AUTH=htpasswd \
  -e REGISTRY_AUTH_HTPASSWD_REALM="Registry Realm" \
  -e REGISTRY_AUTH_HTPASSWD_PATH=/auth/htpasswd \
  -e REGISTRY_HTTP_ADDR=0.0.0.0:5000 \
  -e REGISTRY_HTTP_TLS_CERTIFICATE=/certs/domain.crt \
  -e REGISTRY_HTTP_TLS_KEY=/certs/domain.key \
  -p 5000:5000 \
  registry:2.7.0
```

```shell script
docker run -d \
  --restart=always \
  --name target_registry \
  -v `pwd`/docker/target_docker/auth:/auth \
  -v `pwd`/docker/target_docker/certs:/certs \
  -e REGISTRY_AUTH=htpasswd \
  -e REGISTRY_AUTH_HTPASSWD_REALM="Registry Realm" \
  -e REGISTRY_AUTH_HTPASSWD_PATH=/auth/htpasswd \
  -e REGISTRY_HTTP_ADDR=0.0.0.0:6000 \
  -e REGISTRY_HTTP_TLS_CERTIFICATE=/certs/domain.crt \
  -e REGISTRY_HTTP_TLS_KEY=/certs/domain.key \
  -p 6000:6000 \
  registry:2.7.0
```
