getProp = (name, defaultValue) => {
  if (process.env[name]) {
    return process.env[name];
  }

  return defaultValue;
};

getOpbaUrl = () => {
  return getProp('OPBA_SERVER_URL', 'https://obg-dev-openbankinggateway.cloud.adorsys.de');
};

getConsentUiUrl = () => {
  return getProp('OPBA_CONSENT_UI_URL', 'https://obg-dev-consentui.cloud.adorsys.de');
};

getFintechUiUrl = () => {
  return getProp('OPBA_CONSENT_UI_URL', 'https://obg-dev-fintechui.cloud.adorsys.de');
};

const PROXY_CONFIG = {
  '/embedded-server/*': {
    target: getOpbaUrl(),
    pathRewrite: {
      '^/embedded-server': ''
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
        //getback to fintechui
        if (proxyRes.headers['location'].includes(getFintechUiUrl())) {
          proxyRes.headers['location'] = proxyRes.headers['location'].replace(
            getFintechUiUrl(),
            'http://localhost:4444'
          );
        }
      }
    }
  }
};

module.exports = PROXY_CONFIG;
