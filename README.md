
# App Center Distribute Gradle Plugin

WARNING: Early stages of development. Has not been thoroughly tested.

This plugin provides Gradle tasks for each Android variant to upload and distribute via App Center. It assumes each variant corresponds to a unique App Center app.

## Usage

### Dependencies

[![](https://jitpack.io/v/danherrera/app-center-distribute.svg)](https://jitpack.io/#danherrera/app-center-distribute)

In the project's `build.gradle`:

```groovy
buildscript {
  repositories {
    //...
    maven { url 'https://jitpack.io' }
  }

  classpath {
    //...
    classpath 'com.github.danherrera:app-center-distribute:0.0.5'
  }
}
```

In the app module's `build.gradle`:

```groovy
apply plugin: 'com.github.danherrera.appcenterdistribute'
```

```groovy
appCenterDistribute {
  ownerName = "appcenterowner"
  apiToken = "appcenterapitoken"
  variantToAppName = [
    GoogleFreeDebug: "Google-Free-Debug-App-Center-Name",
    GoogleFreeRelease: "Google-Free-Release-App-Center-Name",
    GooglePaidDebug: "Google-Paid-Debug-App-Center-Name",
    GooglePaidRelease: "Google-Paid-Release-App-Center-Name",
    AmazonFreeDebug: "Amazon-Free-Debug-App-Center-Name",
    AmazonFreeRelease: "Amazon-Free-Release-App-Center-Name",
    AmazonPaidDebug: "Amazon-Paid-Debug-App-Center-Name",
    AmazonPaidRelease: "Amazon-Paid-Release-App-Center-Name"
  ]
  distributionGroups = [ "Collaborators", "QA" ]
}
```

### Tasks

Once configured properly and synchronizing Gradle with project files, a new set of gradle tasks will be available within the `AppCenter` group. 

```
AppCenter tasks
---------------
distributeAllToAppCenter - Distribute all variants to App Center
distributeGoogleFreeDebugToAppCenter - Distribute googleFreeDebug to App Center
distributeGoogleFreeReleaseToAppCenter - Distribute googleFreeRelease to App Center
distributeGooglePaidDebugToAppCenter - Distribute googlePaidDebug to App Center
distributeGooglePaidReleaseToAppCenter - Distribute googlePaidRelease to App Center
distributeAmazonFreeDebugToAppCenter - Distribute amazonFreeDebug to App Center
distributeAmazonFreeReleaseToAppCenter - Distribute amazonFreeRelease to App Center
distributeAmazonPaidDebugToAppCenter - Distribute amazonPaidDebug to App Center
distributeAmazonPaidReleaseToAppCenter - Distribute amazonPaidRelease to App Center
```

Note that these tasks will be created for all variants in the app module, regardless of whether they were defined in the `variantToAppName` map. Running a task for a variant whose App Center app name was not supplied in the `variantToAppName` map will result in a failure. However, when running the `distributeAllToAppCenter` task, failure of one variant will not impede execution of consequent variants.

### Configuration

The `appCenterDistribute` block is used to configure this plugin. Below are the attributes available for configuration:

**ownerName** (required) - This represents the owner of the app in App Center. This can be found by looking at the URL when looking App Center apps page: `https://appcenter.ms/users/{ownerName}`.

**apiToken** (required) - The API token obtained from App Center.

**variantToAppName** (required) - This maps the variant name (notice the capitalization) to the corresponding App Center app name (which can be obtained from the URL: `https//appcenter.ms/users/{ownerName}/apps/{appName}`). Only the variants intended for distribution are required.

**distributionGroups** (optional) - This defines the App Center distribution group(s) the release should be added to. Applies to all variants. Not defining this will default to distributing to the `Collaborators` distribution group.

