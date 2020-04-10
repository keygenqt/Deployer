Deployer
===================

![picture](https://bitbucket.org/keygenqt_work/deployer/raw/9634ac1c37f76f6594acf31a1df428a9344d4fc6/src/main/resources/static/images/icon2.png)

## Release

[![Get it from the Snap Store](https://snapcraft.io/static/images/badges/en/snap-store-black.svg)](https://snapcraft.io/deployer)

#### Info:

```
Usage: deployer COMMAND=ARG...

Deployer is a tool for:
    * Web interface for generate access token Oauth 2.0
    * Upload to Google Play production, internal... (Play Developer API)
    * Newsletter after upload build in Google Play (Gmail API)
    * Connect webhooks after upload test or prod (Slack API)
    * CHANGELOG.md generation based git history (Thymeleaf)

Options
    
  Upload:
    --path-build                Path to build for upload
    --upload-track              Upload type build (production/internal)
    --note-add                  Upload note text
    --note-add-version          Add to note versionCode (auto)
    --user-email                Email user with oauth authentication (if use google api)
    --mailing                   Newsletter (gmail, slack) when will upload build in Google Play
    --mailing-gmail             Newsletter only GMail
    --mailing-slack             Newsletter only Slack
    --mailing-slack-desc        Slack additional Information
    
  GradleHelper
    --path                      Path to folder with project
    --get-application-id        Get applicationId
    --get-version-code          Get versionCode
    --get-version-name          Get versionName
    --get-version-code-up       Get versionCode Up
    --get-version-name-up       Get versionName Up
    --version-code-up           Update versionCode - Up
    --version-name-up           Update versionName - Up (patch)

  Server:
    --path                      Path to folder with project
    --server                    Run server oauth. (http://localhost:8080)
    
  Changelog:
    --path                      Path to folder with project
    --changelog                 Generate CHANGELOG.md

  Other:
    --debug                     Enable processes logging terminal
    --version                   Show the version and exit
    --help                      Show help
```

## Usage

### Run server Oauth 2.0

```bash
sudo crontab -e
```

```bash
@reboot sudo -H -u {your user} bash -c "deployer --server"
```

```bash
cat /etc/apache2/sites-enabled/oauth.com.conf

<VirtualHost *:80>
    ServerName oauth.com
    ProxyPreserveHost on
    RequestHeader set X-Forwarded-Proto https
    RequestHeader set X-Forwarded-Port 443
    ProxyPass / http://127.0.0.1:8080/
    ProxyPassReverse / http://127.0.0.1:8080/
</VirtualHost>
```

### Generate CHANGELOG.md

```bash
deployer --path==/your/dir/project --changelog
```

### Upload

```bash
deployer --path-build=/your/dir/project/app/build/outputs/bundle/release/app-release.aab --upload-track=production --user-email=user@oauth.com
```

### Gradle Helper

```bash
# Get applicationId
deployer --path==/your/dir/project --get-application-id

# Get versionCode
deployer --path==/your/dir/project --get-version-code

# Get versionName
deployer --path==/your/dir/project --get-version-name

# Get versionCode Up
deployer --path==/your/dir/project --get-version-code-up

# Get versionName Up
deployer --path==/your/dir/project --get-version-name-up

# Update versionCode - Up
deployer --path==/your/dir/project --version-code-up

# Update versionName - Up (patch)
deployer --path==/your/dir/project --version-name-up
```