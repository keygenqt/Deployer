Deployer
===================

<img width="100px" src="https://github.com/keygenqt/deployer/blob/master/src/main/resources/static/images/icon.png" />

## Latest Release

The latest version of the module is v0.0.6 `BETA!!!` **(in developing)**

#### Info:

```
Usage: java -jar deployer.jar COMMAND=ARG...

Deployer is a tool for:
    * Web interface for generate access token Oauth 2.0.
    * Building apk or bundle package default build types as well as custom
    * Upload to Google Play production, internal... (Play Developer API)
    * CHANGELOG.md generation based git history (Thymeleaf)
    * Newsletter after upload build in Google Play (Gmail API)
    * Connect webhooks slack after upload test or prod
    * One file for all features!

Options

  --path                        PATH to folder with project
    
  Build:
    --build                     Type: apk/bundle
    --build-type                Type gradle build (release, debug or other builds type)
    --store-password            Password store *.jks
    --key-password              Password key *.jks
    --store-file                Path to file *.jks
    --upload-track              Upload type build (production/internal/alpha/beta)
    --upload-note               Upload note text
    --upload-note-version       Add to note versionCode
    --version-code-up           Raising versionCode during assembly
    --version-name-up           Raising versionName during assembly (up path version)
    --mailing                   Newsletter (gmail, slack) when will upload build in Google Play

  Server:
    --server                    Run server oauth. (http://localhost:8080)
    
  Changelog:
    --changelog                 Generate CHANGELOG.md

  Other:
    --email                     Email user with oauth authentication (if use google api)
    --debug                     Enable processes logging terminal
    --version                   Show the version and exit
    --help                      Show help
```

## Usage

### Run server Oauth 2.0

```bash
java -jar deployer.jar --server
```

### Generate CHANGELOG.md

```bash
java -jar deployer.jar --path=/to/porject --changelog
```

### Get versionCode UP

```bash
java -jar deployer.jar --path=/to/porject --version-code-up
```

### Get versionName UP

```bash
java -jar deployer.jar --path=/to/porject --version-name-up
```

### Build apk

```bash
java -jar deployer.jar --path=/to/porject --build=apk --build-type=release --store-password=0000 --key-password=1111 --store-file=/to/porject/key.jks
```

### Build bundle

```bash
java -jar deployer.jar --path=/to/porject --build=bundle --build-type=release --store-password=0000 --key-password=1111 --store-file=/to/porject/key.jks
```

### Build with versionCode Up

```bash
java -jar deployer.jar --path=/to/porject --build=bundle --build-type=release --store-password=0000 --key-password=1111 --store-file=/to/porject/key.jks --version-code-up
```

### Build with versionName Up (patch)

```bash
java -jar deployer.jar --path=/to/porject --build=bundle --build-type=release --store-password=0000 --key-password=1111 --store-file=/to/porject/key.jks --version-name-up
```

### Build with upload to Google Play

```bash
java -jar deployer.jar --path=/to/porject --build=bundle --build-type=release --store-password=0000 --key-password=1111 --store-file=/to/porject/key.jks --upload-track=production --email=email@oauth.user
```

### Build upload to Google Play and Newsletter

```bash
java -jar deployer.jar --path=/to/porject --build=bundle --build-type=release --store-password=0000 --key-password=1111 --store-file=/to/porject/key.jks --upload-track=production --email=email@oauth.user --mailing 
```

### Build FULL upload to Google Play

```bash
java -jar deployer.jar --path=/to/porject --build=bundle --build-type=release --store-password=0000 --key-password=1111 --store-file=/to/porject/key.jks --upload-track=production ---upload-note="My note" --upload-note-version --email=email@oauth.user --version-code-up --version-name-up --mailing
```
