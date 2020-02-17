
getProp = (name, defaultValue) => {
  if (process.env[name]) {
    return process.env[name];
  }

  return defaultValue;
};

const PROXY_CONFIG = {
  '/embedded-server/*': {
    target: getProp("EMBEDDED_SERVER_URL", "http://localhost:8085"),
    pathRewrite: {
      "^/embedded-server": ""
    },
    logLevel: "debug",
    secure: false,
    changeOrigin: true,
    onProxyRes: (proxyRes, req, res) => {
      if (proxyRes.headers['location']) {

        // -> store auth session
        if (proxyRes.headers['location'].includes(getProp("TECHNICAL_UI_HOST_AND_PORT", "localhost:5500"))) {
          let queryStart = proxyRes.headers['location'].includes('?') ? '' : '?stub=Stubbed';
          proxyRes.headers['location'] = proxyRes.headers['location'] + queryStart + '&authorizationSessionId=' + proxyRes.headers['authorization-session-id'] + '&redirectCode=' + proxyRes.headers['redirect-code'] + '&serviceSessionId=' + proxyRes.headers['service-session-id'];
        }

        // -> avoid cors on sandbox
        let sandboxUi = getProp("TPP_BANKING_UI_HOST_AND_PORT", "localhost:4400");
        if (proxyRes.headers['location'].includes(sandboxUi)) {
          let orig = proxyRes.headers['location'];
          proxyRes.headers['location'] = proxyRes.headers['location'].replace(sandboxUi, 'localhost:5500/sandbox') + '&redirToSandbox=' + orig;
        }
      }
    }
  },
  '/sandbox/*': {
    target: "http://" + getProp("TPP_BANKING_UI_HOST_AND_PORT", "localhost:4400"),
    pathRewrite: {
      "^/sandbox": ""
    },
    logLevel: "debug",
    secure: false,
    changeOrigin: true
  }
};

module.exports = PROXY_CONFIG;
