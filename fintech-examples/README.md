# FinTech Examples

Example FinTech application provide in this sub module.

## fintech-api

Contains an example-api, how calls from the fintech-ui to the fintech-server may look.
The example-api is an open-api file to be found in
```
fintech-api/src/main/resources/static
```
From this files the server stubs are generated to the
<code>target/generated-sources/open-api/src/main/java</code>
folder.

## fintech-impl

Contains an example implementation of the api. Here the REST calls coming from the
Fintech-UI are handled. For that the <i>TPPServer</i>s API is called. The imiplementation contains the
Controllers to handle the Rest Calls and the Client Stubs, to
call the <i>TPPServer</i>.
To generate the client Stubs <code>spring-feign</code> is used.

## fintech server

The server simply packs the impl to an application.

To view the API:

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
        sameSite: none
tpp:
    url: http://localhost:18085

```

## fintech-ui

To serve a dev server:

```
> cd fintech-ui
> npm install
> npm run start

then http://localhost:4200
```

The app will automatically reload if you change any of the source files.
