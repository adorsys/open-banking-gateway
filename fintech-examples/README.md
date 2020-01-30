# FinTech Examples

Example FinTech application provide in this sub module. To view the API:

```
> mvn clean install
> cd fintech-server
> mvn spring-boot:run

then http://localhost:8086
```

The application tries to connect to TppServer on port 18080. This is configured in
```
open-banking-gateway/fintech-examples/fintech-server/src/main/resources/application.yml
```

First call to FintechServer is always post request 
<code>/v1/login</code>

When credentials are sufficiant, two cookies will be returned. A sessionCookie with name
<code>SESSION-COOKIE</code> and an CORS Cookie with name <code>XSRF-TOKEN</code>. The second cookies
value has to be passed from the client with every succeeding call in a 
header field called X-XSRF-TOKEN. Otherwise all other calls will fail with
<code>ttpStatus.Unauthorized</code>.

For the Cookies returned by the <code>fintechServer</code> to the client, all attributes
can be set in the <code>application.yml</code> file.
```
server:
  port: 8086
  controller:
     cookie:
        secure: false
        maxAge: 300
        httpOnly: false
        path: /
        sameSite: anyValue
tpp:
    url: http://localhost:18085

``` 