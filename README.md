# slingspot-common

A set of building block modules for microservice applications, and some reusable components for 
Android applications.

## Build

This project relies on an external buildSrc submodule:

```
git submodule update --init  
```

After that, building can be accomplished normally:
```
./gradlew build
```

## Run

```
java -jar app-sample/build/libs/app-sample.jar [optional arguments; try --help]
```

The server depends on external values:

- path to keystore: set via --file or environment variable KEYSTORE_PATH
- keystore type: set via --type or environment variable KEYSTORE_TYPE; if omitted, defaults to "PKCS12"
- keystore password: set via --password or environment variable KEYSTORE_PASSWORD
- environment: set via --environment or environment variable ENVIRONMENT; if omitted, defaults to "development"
- http port: set via --http; if omitted, defaults to 80
- https port: set via --https; if omitted, defaults to 443
- log directory: set via --out or environment variable FILE_LOG_DIR; if omitted, no file logging occurs
- file log level: set via --file-log [VERBOSE, DEBUG, etc]; if omitted, defaults to VERBOSE
- console log level: set via --console-log [VERBOSE, DEBUG, etc]; if omitted, no console logging occurs

## Publish

The library jars can be published to github packages as follows:

```
./gradlew publish -Prepo=slingspot-common -Pgroup=net.slingspot -Pversion=0.1.1
```

That assumes the presence of these environment variables:
- GITHUB_USERNAME
- GITHUB_TOKEN
- GITHUB_ORG

