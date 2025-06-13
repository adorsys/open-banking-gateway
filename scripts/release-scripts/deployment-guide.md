##  Release Deployment Guide

This project uses a local release script to prepare and tag new versions. 
After the release is pushed, GitHub Actions automatically build and publish deliverables (JARs and Docker images).

-  **JAR artifacts** published to **GitHub Packages**
-  **Docker images** published to **GitHub Container Registry (GHCR)**

---

###  How Deployment Works

Two GitHub Actions workflows are triggered during deployments:

- `.github/workflows/jar-publish.yml`
- `.github/workflows/deploy-to-ghcr.yml`

Both workflows are activated automatically when:

- A commit is pushed to the `develop` branch
- A Git tag matching `v*` is pushed (e.g. `v1.2.3`)
- A GitHub release is published

---

###  `jar-publish.yml`: Publish Maven Artifacts

**Purpose:**  
Builds and deploys the project’s JAR files to GitHub Packages (Maven repository).

**Key Steps:**
- Checks out the code
- Sets up JDK 21
- Configures Maven credentials using `${{ secrets.GHCR_DEPLOY_TOKEN }}`
- Deploys using Maven with the `-Pgithub` profile and a custom settings file

**Triggered By:**
- Push to `develop`
- Git tag starting with `v`
- GitHub release publication

Artifacts are published under your repository’s Maven namespace on GitHub Packages.

---

### `deploy-to-ghcr.yml`: Push Docker Images

**Purpose:**  
Builds Docker images and pushes them to GHCR (GitHub Container Registry).

**Key Steps:**
- Frees up disk space on GitHub-hosted runners
- Sets up JDK 21
- Builds the application using Maven
- Executes `./scripts/deploy_to_ghcr.sh` to push images to GHCR
- Uses `${{ secrets.GHCR_DEPLOY_TOKEN }}` for authentication

**Triggered By:**  
Same as `jar-publish.yml`.

Docker images are published to `ghcr.io` under your GitHub username or organization.

---

###  Interaction Between Workflows

- These workflows are **independent but complementary**
- Both are triggered by the same events (commits to `develop`, release tags, or published GitHub releases)
- Both rely on consistent Maven builds and release versioning
- A release tag (e.g., `v1.2.3`) triggers both pipelines and deploys the release artifacts and images

---

###  Deploying a Release

The release process is initiated using the `release.sh` script, which performs the following steps locally:

- Creates a dedicated **release branch** from `develop`
- Updates project versions and removes `-SNAPSHOT` suffixes
- Builds and verifies the project
- Merges the release into `master` and `develop`
- Creates a Git **tag** representing the version (e.g. `v1.2.3`)
- Optionally updates `develop` with the next `-SNAPSHOT` version
- Prepares and stages a GitHub **release**

Once the changes and tag are pushed to GitHub, two GitHub Actions workflows are automatically triggered to publish the deliverables.

To run the script, use:

   ```bash
   ./scripts/release-scripts/release.sh -s <release-version> <next-version>
   ```
Use -s to skip appending -SNAPSHOT to the release version.


Then push the results:
   ```bash
   git push --atomic origin master develop --follow-tags
   ```
This will trigger both workflows and deploy the JAR artifacts and Docker images.

###  Required GitHub Secrets
Ensure the following secrets are configured in your repository:

`GHCR_DEPLOY_TOKEN`: GitHub token with write:packages scope (used for both Maven and Docker authentication)

