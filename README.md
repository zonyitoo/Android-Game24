# 24 Game

This is a course project for COMP7506_1A_2014.

**Copyright by: Yuteng ZHONG (3035169867)**

To my classmates: If you see these codes before the deadline, please feel free to read the code,
but **DO NOT COPY**.

## Build

This project is created by AndroidStudio and built with Gradle, but it can also be imported by Eclipse ADT and built.

### Build with Gradle

If you have installed Gradle and have a unix-like environment (Mac OS X, Linux, FreeBSD, Cygwin), just `cd` into the root directory of the project and run

```sh
./gradlew build
```

*Note: `gradlew.bat` is the script for Windows' cmd.*

Then Gradle will build the project and generate debug APKs under the directory `app/build/outputs/apk`.

### Install it into the AVD

This is one of the Gradle tasks called `installDebug`, which will install the debug build into the AVD.

```sh
./gradlew installDebug
```

To see a list of available tasks, run `./gradlew tasks`

### Build with Eclipse ADT

Just open a new project in Eclipse and import the source code under the directory `app/src/main/java` and the resource files under `app/src/main/res`.

The `AndroidManifest.xml` is located in `app/src/main/AndroidManifest.xml`.

