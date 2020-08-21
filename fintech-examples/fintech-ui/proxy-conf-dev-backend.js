getProp = (name, defaultValue) => {
  if (process.env[name]) {
    return process.env[name];
  }

  return defaultValue;
};

getFintechUrl = () => {
  return getProp('OPBA_FINTECH_URL', 'https://obg-dev-fintechui.cloud.adorsys.de/fintech-api-proxy');
};

getConsentUiUrl = () => {
  return getProp('OPBA_CONSENT_UI_URL', 'https://obg-dev-consentui.cloud.adorsys.de');
};

const PROXY_CONFIG = {
  '/fintech-api-proxy/*': {
    target: getFintechUrl(),
    pathRewrite: {
      '^/fintech-api-proxy': ''
    },
    logLevel: 'debug',
    secure: false,
    changeOrigin: true,
    headers: { 'Access-Control-Expose-Headers': '*' },
    onProxyRes: (proxyRes, req, res) => {
      if (proxyRes.headers['location']) {
        // change remote UI to local UI
        if (proxyRes.headers['location'].includes(getConsentUiUrl())) {
          proxyRes.headers['location'] = proxyRes.headers['location'].replace(
            getConsentUiUrl(),
            'http://localhost:4200'
          );
        }
      }
    }
  }
};

module.exports = PROXY_CONFIG;
