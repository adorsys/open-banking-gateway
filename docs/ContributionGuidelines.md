## Development and contributing

### Internal development process
Development is performed by 2-weeks sprints (We use kind of Scrum framework).
We use [Git flow](http://nvie.com/posts/a-successful-git-branching-model/) for development. 
Leading branch is always <code>develop</code>.
Each new feature/bugfix is done in its own branch. All features/bugs are documented in 
[zenhub](https://app.zenhub.com/workspaces/open-banking-gateway-5dd3b3daf010250001260675).

After finishing branch and seeing that branch has successfully been build by 
[travis](https://travis-ci.com/adorsys/open-banking-gateway)
a pull request to branch develop must be created in 
[github](https://github.com/adorsys/open-banking-gateway).

Each developer of the team with approval rights can have a look at pull request and do the review.
If no review is done for more than 24 hours, the developer is allowed to merge the branch to develop branch.
Of course build in [travis](https://travis-ci.com/adorsys/open-banking-gateway) must work after merge.

The master branch is never touched. Never, except when release build is done. This is triggered by
the project management. When release build is triggered the current develop is merged into master. 
For that master never ever must be updated manually.
 
### Definition of Ready
The task is ready to be put into a sprint when all following conditions are met:
* All dependencies are clear and the work to work with them are clarified
* Use-case is defined in the task
* Acceptance criteria are defined

### Definition of Done
The Task could be accepted only when following requirements are met:
* Code is reviewed (and approved) by another developer
* API documentation in Swagger UI corresponds to acceptance criteria
* At least one automated test for every Use-case exists

nice to have

* Project documentation (Markdown files) contains the information how to run the demo of use case
* Javadocs for public methods are written (including parameter description). 
  For REST interfaces Swagger-annotations are sufficient.

### Contributing
Any person are free to join us by implementing some parts of code or fixing some bugs. For that separate  branch has to be created.
 
### Technical conditions for the implementations

#### Code styling
If you are using Intellij IDEs, like we do, please consider importing our code-style settings.
Further explanation in [checkstyle](../README.md#checkstyle)

#### Java
* Please use Optionals and corresponding streams instead of null-checks where possible
* We prefer using Mockito over EasyMock for unit-tests.
* We prefer SpringBoot autoconfiguration over manual Configuration where possible

## How to Release

Release is being done using the [release scripts](https://github.com/borisskert/release-scripts) ( (C) by [Boris Skert](https://github.com/borisskert) ) located under scripts/release-scripts.
For detailed info see [README for release-scripts](https://github.com/borisskert/release-scripts/README.md).

### Steps to make a release

**Release is made from local copy! Ensure that you have enough rights to push to master and develop branches**
```bash
$ git submodule update --init --remote
$ scripts/release-scripts/release.sh <release-version> <next-develop-version>
``` 
Example
```bash
$ scripts/release-scripts/release.sh 1.0 1.1
```
