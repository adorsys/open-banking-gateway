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
      "^/embedded-server": ""
    },
    logLevel: "debug",
    secure: false,
    changeOrigin: true,
    headers: {"Access-Control-Expose-Headers": "*"},
    onProxyRes: (proxyRes, req, res) => {
      if (proxyRes.headers['location']) {

        // change remote UI to local UI
        if (proxyRes.headers['location'].includes('https://obg-dev-consentui.cloud.adorsys.de')) {
          proxyRes.headers['location'] = proxyRes.headers['location'].replace('https://obg-dev-consentui.cloud.adorsys.de', 'http://localhost:4200');
        }
      }
    }
  }
};

module.exports = PROXY_CONFIG;
