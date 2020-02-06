const PROXY_CONFIG = {
  '/embedded-server/*': {
    target: "http://localhost:8085",
    pathRewrite: {
      "^/embedded-server": ""
    },
    logLevel: "debug",
    secure: false,
    changeOrigin: true,
    onProxyRes: (proxyRes, req, res) => {
      if (proxyRes.headers['location']) {

        // -> store auth session
        if (proxyRes.headers['location'].includes("localhost:5500")) {
          proxyRes.headers['location'] = proxyRes.headers['location'] + '&authorizationSessionId=' + proxyRes.headers['authorization-session-id'] + '&redirectCode=' + proxyRes.headers['redirect-code'];
        }

        // -> avoid cors on sandbox
        if (proxyRes.headers['location'].includes("localhost:4400")) {
          let orig = proxyRes.headers['location'];
          proxyRes.headers['location'] = proxyRes.headers['location'].replace('localhost:4400', 'localhost:5500/sandbox') + '&redirToSandbox=' + orig;
        }
      }
    }
  },
  '/sandbox/*': {
    target: "http://localhost:4400",
    pathRewrite: {
      "^/sandbox": ""
    },
    logLevel: "debug",
    secure: false,
    changeOrigin: true
  }
};

module.exports = PROXY_CONFIG;
