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

