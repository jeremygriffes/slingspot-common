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

## Publishing

The library jars can be published to github packages as follows:

```
./gradlew publish -Prepo=slingspot-common -Pgroup=net.slingspot -Pversion=0.1.1
```

That assumes the presence of these environment variables:
- GITHUB_USERNAME
- GITHUB_TOKEN
- GITHUB_ORG

