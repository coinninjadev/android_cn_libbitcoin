# Android Libbitcoin

## Prerequisites

The guide assumes that you are using Linux or Mac for development.

- Install [Android Studio](https://developer.android.com/studio/index.html)
- Install [Java JDK v8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)

Below are the Android tools needed:
- Install ANDROID_BUILD_TOOLS="28.0.3"
- Install ANDROID_COMPILE_SDK="28"


## Build Terminal

First export the following variables.

```SH
    export ANDROID_HOME=your-android-sdk-location
    export ANDROID_NDK_HOME=your-android-sdk-location
    export PATH=$PATH:/sdk-tools/platform-tools/
    export PATH=$PATH:$ANDROID_NDK_HOME

```

**change to the root directory of the project**

```
./gradlew assembleAll assemble
```
    
## Release For Android Apps

Gitlab will automatically deploy created versions of this artifact to our maven repository 
as long as the branch is a release candidate `1.2.0RC`

