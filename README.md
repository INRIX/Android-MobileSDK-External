Android-MobileSDK-External
==========================

This repository contains multiple components that make up an SDK for building
applications to run on Android and access INRIX services/data.  

IDs/Keys
-------
Each new project will require a set of IDs/Keys to enable projects to be built and data services to be accessed.  Some IDs are specific/unique to the project.

Quick Start Guide
-------

Download the INRIX SDK .aar library files and add them to your project. These libraries can be found here: [sample-app/libs](sample-app/libs) 

Update your app's **build.gradle** to include INRIX SDK dependences

```groovy
dependencies {
    // INRIX SDK.
    compile(name: "com.inrix.sdk-${inrixSdkVersion}", ext: 'aar')
    compile(name: "com.inrix.sdk.cache-${inrixCacheVersion}", ext: 'aar')

    // Google Play Services.
    compile "com.google.android.gms:play-services-base:${googlePlayServicesVersion}"
    compile "com.google.android.gms:play-services-gcm:${googlePlayServicesVersion}"
    compile "com.google.android.gms:play-services-location:${googlePlayServicesVersion}"

    // Additional INRIX SDK dependencies.
    compile "com.android.support:support-v4:${supportLibVersion}"
    compile "com.google.code.gson:gson:${gsonVersion}"
    compile "com.android.volley:volley:${volleyVersion}"
    compile "org.slf4j:slf4j-api:${slf4jVersion}"

	...
}

```

Configure and initialize INRIX SDK:

1. Add following configuration to AndroidManifest.xml in your project under `<application>` node: 

	```xml
	<meta-data
	    android:name="com.inrix.appKey"
	    android:value="@string/inrix_app_key"/>
	<meta-data
	    android:name="com.inrix.appId"
	    android:value="@string/inrix_app_id"/>
	```

2. Initialize INRIX SDK in Application's `onCreate`

	```java
	public class App extends Application {
	    @Override
	    public void onCreate() {
	        super.onCreate();
	
	        InrixCore.initialize(this);
	    }
	}
	```
	if additional configuration is needed, [InrixCore.initialize(context, Configuration)](http://inrix.github.io/Android-MobileSDK-External/com/inrix/sdk/InrixCore.html#initialize(android.content.Context%2C%20com.inrix.sdk.Configuration)) can be used to configure INRIX SDK.

Call INRIX APIs:

```java
public void onButtonClick() {
    final IncidentsManager incidentsManager = InrixCore.getIncidentsManager();
    final IncidentsManager.IncidentRadiusOptions options = new IncidentRadiusOptions(new GeoPoint(47, -122), 500);
    incidentsManager.getIncidentsInRadius(options, new IIncidentsResponseListener() {
        @Override
        public void onResult(List<Incident> incidents) {
            // TODO: Handle results.
        }

        @Override
        public void onError(Error error) {
            // TODO: Handle error.
        }
    });
}
```

Migrating from 6.x to 7.0
-------------------------

In 7.0 we changed the default `InrixCore.initialize(Context)` behavior and updated the **Configuration.Builder** default features. The INRIX SDK is now initialized with the following **Configuration.Builder** methods disabled, unless explicitly set to **true**:

| Methods now disabled by default |
| --- |
| com.inrix.sdk.Configuration.Builder.calendarSyncEnabled |
| com.inrix.sdk.Configuration.Builder.tripRecordingEnabled |
| com.inrix.sdk.Configuration.Builder.monitorUserLocation |

We have also removed the `android.permission.READ_CALENDAR` permission from the SDK's manifest. In order for calendar syncing to function properly, the application must have this permission granted.

JavaDocs
-------

INRIX SDK JavaDocs can be found here: [http://inrix.github.io/Android-MobileSDK-External/](http://inrix.github.io/Android-MobileSDK-External/)

License
-------

The SDK Developer License can be found here as License.pdf

Terms of Service & Privacy Policy
-------

Now included are samples for the “Terms of Service” and “Privacy Policy”.  These are just samples.  All commercial applications will access these documents via a future public URL.