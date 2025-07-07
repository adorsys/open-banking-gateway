# Release Process

Releases are initiated locally via a script and published to GitHub. Once pushed, GitHub Actions handle artifact and image deployment.

---

## 🚀 How to Release

Run the local release script:

```bash
./scripts/release-scripts/release.sh -s <release-version> <next-version>
```
Use -s to skip appending -SNAPSHOT to the release version.
- <release-version>: the version to release (e.g., 1.2.3)
- <next-version>: the version to continue development with (e.g., 1.2.4)

Then push the results:
   ```bash
   git push --atomic origin master develop --follow-tags
   ```
This will:
- Push commits to master and develop
- Push the release tag
- Trigger both GitHub Actions workflows: 
   * .github/workflows/jar-publish.yml
   * .github/workflows/deploy-to-ghcr.yml

These workflows will build and deploy the JAR artifacts and Docker images automatically.

---

## 📝 Release Notes Requirements

Every contributor performing a release must:
1. Update the `releasenotes.md` index file with a new entry for the current release.
2. Create a new file named `releasenotes-<release-version>.md` inside the docs/release-notes/ directory.
Example:
  ```bash
   docs/release-notes/releasenotes-1.2.3.md
   ```
Each `releasenotes-<release-version>.md` file should follow the standard format and include:
- Release title and version
- Release date
- A short summary of changes (features, fixes, updates)

This ensures that all releases are properly documented and easy to trace for future reference.
