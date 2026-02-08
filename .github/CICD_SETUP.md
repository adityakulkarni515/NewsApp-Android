# CI/CD Pipeline Setup Guide

This project uses GitHub Actions for continuous integration and deployment. The pipeline automatically builds, tests, and deploys your Android app.

## Workflows Overview

### 1. Android CI/CD Pipeline (`android-ci.yml`)
**Triggers:** Push to `development` or `main` branches, Pull Requests

**What it does:**
- ✅ Runs unit tests
- ✅ Runs instrumented tests (on Android emulator)
- ✅ Performs lint checks
- ✅ Builds debug and release APKs
- ✅ Uploads APKs as artifacts (available for 30 days)
- ✅ Generates test reports

### 2. Release Build (`release.yml`)
**Triggers:** Git tags starting with `v*` or manual workflow dispatch

**What it does:**
- 📦 Builds release and debug APKs
- 🚀 Creates a GitHub Release
- 📎 Attaches APKs to the release for public download
- 🏷️ Tags the release with version number

### 3. Signed Release Build (`signed-release.yml`)
**Triggers:** Manual workflow dispatch only

**What it does:**
- 🔐 Builds signed APK and AAB (Android App Bundle)
- 📦 Suitable for Google Play Store distribution
- 🚀 Creates a GitHub Release with signed artifacts

## How to Use

### Accessing Build Artifacts

#### For Every Push/PR:
1. Go to your repository on GitHub
2. Click on "Actions" tab
3. Click on the workflow run you want
4. Scroll down to "Artifacts" section
5. Download `app-debug` or `app-release`

#### For Releases:
1. Go to the "Releases" page on GitHub: `https://github.com/adityakulkarni515/NewsApp-Android/releases`
2. Download the APK from the latest release
3. Share the release URL with users for easy downloads

### Creating a Release

#### Option 1: Using Git Tags
```bash
# Create and push a tag
git tag v1.0.0
git push origin v1.0.0
```

#### Option 2: Manual Trigger
1. Go to "Actions" tab
2. Select "Release Build" workflow
3. Click "Run workflow"
4. Enter version number (e.g., 1.0.0)
5. Click "Run workflow"

### Creating a Signed Release (for Play Store)

**Prerequisites:**
You need to set up the following secrets in your GitHub repository:

1. Go to Settings > Secrets and variables > Actions
2. Add these secrets:
   - `KEYSTORE`: Base64 encoded keystore file
   - `KEYSTORE_PASSWORD`: Your keystore password
   - `KEY_ALIAS`: Your key alias
   - `KEY_PASSWORD`: Your key password

**To encode your keystore:**
```bash
base64 -i your-keystore.jks | pbcopy  # macOS
base64 -i your-keystore.jks           # Linux
```

**To create a signed release:**
1. Go to "Actions" tab
2. Select "Signed Release Build"
3. Click "Run workflow"
4. Enter version number
5. Click "Run workflow"

## Viewing Test Results

Test results are automatically uploaded and can be viewed:
1. Go to the workflow run
2. Click on "Summary"
3. View "Unit Test Results" and other test reports
4. Download test artifacts for detailed analysis

## Build Status Badge

Add this to your README.md to show build status:

```markdown
![Android CI](https://github.com/adityakulkarni515/NewsApp-Android/workflows/Android%20CI%2FCD%20Pipeline/badge.svg)
```

## Distribution Options

### Option 1: GitHub Releases (Recommended for Beta/Internal Testing)
- Users can download APKs directly from GitHub Releases page
- No account required to download
- Perfect for beta testers and internal distribution
- URL: `https://github.com/adityakulkarni515/NewsApp-Android/releases`

### Option 2: GitHub Actions Artifacts
- Available for 30 days after build
- Requires GitHub account to download
- Good for temporary testing

### Option 3: Google Play Store
- Use the signed AAB from `signed-release.yml`
- Upload to Google Play Console
- Best for public distribution

### Option 4: Firebase App Distribution (Optional)
You can extend the pipeline to automatically upload to Firebase App Distribution for easier beta testing.

## Customization

### Changing Build Configuration
Edit `app/build.gradle.kts` to modify:
- Version codes and names
- Build variants
- Signing configurations

### Modifying Workflows
Workflow files are located in `.github/workflows/`:
- `android-ci.yml` - Main CI pipeline
- `release.yml` - Release automation
- `signed-release.yml` - Signed release builds

### Adding More Tests
Place tests in:
- `app/src/test/` - Unit tests
- `app/src/androidTest/` - Instrumented tests

## Troubleshooting

### Build Failures
- Check the logs in Actions tab
- Ensure all dependencies are properly declared
- Verify JDK version compatibility

### Test Failures
- Review test reports in workflow artifacts
- Run tests locally: `./gradlew test`

### Signing Issues
- Verify all secrets are properly set
- Check keystore password and alias
- Ensure keystore is properly base64 encoded

## Local Development

Run the same commands locally:
```bash
# Unit tests
./gradlew test

# Instrumented tests (requires emulator or device)
./gradlew connectedCheck

# Lint
./gradlew lint

# Build debug
./gradlew assembleDebug

# Build release
./gradlew assembleRelease
```

## Support

For issues with the CI/CD pipeline:
1. Check GitHub Actions logs
2. Review this documentation
3. Open an issue in the repository
