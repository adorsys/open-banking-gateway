# Deliverables Deployment

This project uses GitHub Actions to automate the deployment of JAR artifacts and Docker images.

## 📦 Deliverables

- **JAR artifacts**: Published to **GitHub Packages (Maven)**
- **Docker images**: Published to **GitHub Container Registry (GHCR)**

---

## 🔁 Deployment Workflows

The following GitHub Actions workflows manage deployments:

- `.github/workflows/jar-publish.yml`
- `.github/workflows/deploy-to-ghcr.yml`

Both workflows are triggered by:

- Push to the `develop` branch
- Push of a Git tag starting with `v` (e.g., `v1.2.3`)

---

### `jar-publish.yml`: Publish Maven Artifacts

**Purpose:**  
Builds and deploys the project’s JAR files to GitHub Packages (Maven repository).

**Steps:**
- Checkout code
- Setup JDK 21
- Authenticate with Maven using `${{ secrets.GHCR_DEPLOY_TOKEN }}`
- Deploy via Maven with the `-Pgithub` profile

**Triggered by:**
- `develop` branch commits
- `v*` tags

Artifacts are published under your repository’s Maven namespace on GitHub Packages.

---

### `deploy-to-ghcr.yml`: Push Docker Images

**Purpose:**  
Build and push Docker images to GHCR (GitHub Container Registry).

**Steps:**
- Clean up runner disk space
- Setup JDK 21
- Build project with Maven
- Execute `./scripts/deploy_to_ghcr.sh`
- Authenticate using `${{ secrets.GHCR_DEPLOY_TOKEN }}`

**Triggered by:**  
Same events as `jar-publish.yml`.

Docker images are published to `ghcr.io` under your GitHub username or organization.

---

### 🧩 Workflow Interaction

- Independent but complementary
- Triggered by the same Git events
- Depend on consistent project versioning
- A release tag (e.g. `v1.2.3`) triggers both workflows

---

## 🔐 GitHub Secrets Required

Make sure the following secret is configured in your repo:

- `GHCR_DEPLOY_TOKEN`: GitHub token with `write:packages` scope (used for both Maven and Docker registries)
