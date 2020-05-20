# Session management of Fintech Server

## HOW IT IS NOW

Currently session management of fintechServer is designed and implemented in that way, that user always has just ONE valid session cookie. As long as user is working on fintechUI it has session cookie with short ttl. As soon, as user is redirected to consent ui, user gets new session cookie with long ttl. But this session cookie is only valid for this special redirect. Any other call fails.

After redirect is done, session cookie with long ttl will be replaced with new session cookie with short ttl again.

When user opens second tab, he is already logged in, as session cookie is avaiable. When user did no redirected yet, both tabs work fine. But as soon as user starts redirect in first tab, second tab can not do anything, except logging out. In this case first tab will loose session cookie too and redirect eventuall would fail.

If same user logs in on two different browser, two redirects can be done perfectly fine in parallel.

Independant of sessions, consents are stored in fintech directly under user. Consent can be for ais or pis. For ais only one consent can be valid at a time. But more than one consent can be retrieved in parallel. As soon as consent is confirmed, this consent will be used for any other ais call.

Consent for payment will be allways individually. But this is not yet implemented.

## TO MAKE REDIRECT WORK IN TWO TABS OF SAME BROWSER

To make it possible to do two redirects in two tabs of same browser session management of fintechServer must be redesigned and reimplemented in that way, that user always has just ONE valid session cookie. But as soon, as redirect is started, redirect cookie will NOT replace previous session cookie. So different redirects can be started in parallel. For starting redirect they all use same session cookie and will get individual redirect cookie with long ttl.

As soon as any redirect ends, redirect cookie will be replaced by new session cookie. But replaceemtn onyl takes place, if old session cookie is no more valid. Once first redirect ends and replaces session cookie, next ending redirect is asking for a new session cookie too, but will be returned session cookie that has been created from first redirect end.
