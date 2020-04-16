
getProp = (name, defaultValue) => {
  if (process.env[name]) {
    return process.env[name];
  }

  return defaultValue;
};

const PROXY_CONFIG = {
  '/embedded-server/*': {
    target: getProp("EMBEDDED_SERVER_URL", "https://obg-dev-openbankinggateway.cloud.adorsys.de"),
    pathRewrite: {
      "^/embedded-server/": ""
    },
    logLevel: "debug",
    secure: false,
    changeOrigin: true,
    onProxyRes: (proxyRes, req, res) => {
      if (proxyRes.headers['location']) {

        // change remote UI to local UI
        if (proxyRes.headers['location'].includes('https://obg-dev-consentui.cloud.adorsys.de')) {
          proxyRes.headers['location'] = proxyRes.headers['location'].replace('https://obg-dev-consentui.cloud.adorsys.de', 'http://localhost:4200');
        }

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
