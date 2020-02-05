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
      proxyRes.headers['location'] = proxyRes.headers['location'] + '&authorizationSessionId=' + proxyRes.headers['authorization-session-id'];
    }
  }
};

module.exports = PROXY_CONFIG;
