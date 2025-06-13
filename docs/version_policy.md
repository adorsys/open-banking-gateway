# Versioning, Release and Support policy

In general the OpenBanking Gateway Team follows [SemVer](https://semver.org/) for versioning. This means our versions follow the model A.B.C, where:

- A - is the major version, pointing out mainline.

- B - is the minor version, pointing out the next release in the mainline. Minimum 2 versions backward compatibility is guaranteed for stable mainlines.

C - is the hotfix version, used to deliver patches between releases when needed. If omitted, version 4.5 will be considered equal to 4.5.0.

We support one stable and one development version at any moment.

To keep it simple:

- We use even major version to mark stable support mainlines (2.x, 4.x, 6.x etc)

- We use odd major version to mark development mainlines (1.x, 3.x, 5.x etc)

## Backward compatibility
For stable mainlines we provide backward compatibility of APIs and Database schema. Although for stable versions backward compatibility is high priority and we try our best to keep it as much as possible, we can guarantee backward compatibility only for two versions before.

I.e. if you get version 4.25, it will keep backward compatibility with 4.24 and 4.23, but some changes may appear between APIs of version 4.25 with version 4.22.

The same is valid for the database schema.

If you need extended support, please contact [adorsys Team](https://adorsys.de/kontakt/).

## Stable versions
Stable versions are recommended for production usage. Normally they have support period of time at least 6 months. If you need additional support, please contact [adorsys Team](https://adorsys.de/kontakt/). There is no stable version released as for now.
