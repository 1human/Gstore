# Distribution (maintainers)

`.github/workflows/build-desktop-platforms.yml` currently ships **Windows only**: one amd64 portable zip.

- Triggers: push to `generate-installers`, or manual "Run workflow" (`workflow_dispatch`).
- `build-windows` runs `:composeApp:createDistributable` on `windows-latest` and zips the jpackage app-image (launcher + bundled JRE) as `Komi-Store-<version>-windows-x64-portable.zip`. No EXE/MSI installers, nothing is signed.
- `release` reads `projectVersionName` from `gradle.properties`, collects that zip and creates the draft release `v<version>`. It refuses to overwrite a published release, so bump `projectVersionName` before re-running.

Bundle size: `includeAllModules = true` used to pull every JDK module into the runtime image — 460 MB of the 576 MB app-image (152 MB zipped). The build now lists only the modules the app needs (88 MB runtime, ~204 MB app-image, ~126 MB zipped). The remaining weight is `material-icons-extended` (37 MB), Skia (`skiko-windows-x64.dll` + `icudtl.dat`, 24 MB) and the feature jars (54 MB).

Packaging needs a **full JDK** (jmods + jpackage). The Gradle daemon runs on the JDK 25 runtime image provisioned by `gradle-daemon-jvm.properties`, which has no `jmods`, so jlink failed with "This JDK does not contain packaged modules". `composeApp/build.gradle.kts` therefore sets `compose.desktop.application.javaHome` from `PACKAGING_JAVA_HOME`, or an installed Java 21 toolchain otherwise. CI exports `PACKAGING_JAVA_HOME="$JAVA_HOME"` (the Temurin 21 JDK from `setup-java`); locally, export the path of any full JDK 21+ if packaging fails the same way.

macOS and Linux jobs are removed for now; re-add them to the same workflow when those artifacts come back. Because the macOS Homebrew, WinGet (`.msi`) and Scoop channels depend on artifacts we no longer produce, leave those workflows unused until installers exist again.

## Windows signing (SignPath Foundation) — paused

The `sign-windows` job was removed together with the EXE/MSI installers. Secrets `SIGNPATH_API_TOKEN`, `SIGNPATH_ORGANIZATION_ID` and the variable `SIGNPATH_SIGNING_POLICY_SLUG` are still configured but unused; restore the job from git history if signed installers are needed again.

## WinGet — paused

`.github/workflows/winget-publish.yml` fires on `release: [released]` and publishes the MSI (`installers-regex: '\.msi$'`) as `zed.rainxch.githubstore`. There is no MSI any more, so do not publish a release until this workflow is updated to the portable zip.

## Scoop

`.github/workflows/scoop-publish.yml` fires on release (or by hand) and dispatches `komi-store-release` to `komi-store/komi-store-scoop-bucket` with the secret `SCOOP_BUCKET_TOKEN`.

## macOS (Homebrew) — paused

The cask lives in the tap `komi-store/komi-store` (repository `homebrew-komi-store`): `brew install --cask komi-store`. CI no longer builds `.dmg`/`.pkg`, so `homebrew-tap-publish.yml` has nothing to publish until the macOS job returns.
