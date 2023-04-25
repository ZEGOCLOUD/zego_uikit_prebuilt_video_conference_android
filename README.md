# Quick start

- - -

[![](https://img.shields.io/badge/chat-on%20discord-7289da.svg)](https://discord.gg/EtNRATttyp)

> If you have any questions regarding bugs and feature requests, visit the [ZEGOCLOUD community](https://discord.gg/EtNRATttyp) .


## Integrate the SDK

[![Tutorial | How to build video conference using Android in 10 mins with ZEGOCLOUD](https://res.cloudinary.com/marcomontalbano/image/upload/v1682409701/video_to_markdown/images/youtube--HqzoiKZF_lM-c05b58ac6eb4c4700831b2b3070cd403.jpg)](https://youtu.be/HqzoiKZF_lM "Tutorial | How to build video conference using Android in 10 mins with ZEGOCLOUD")

### Add ZegoUIKitPrebuiltVideoConference as dependencies

1. Add the `jitpack` configuration.
- If your `gradle` version is later than **6.8**, modify your `settings.gradle` file like this:
``` groovy
dependencyResolutionManagement {
   repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
   repositories {
      maven { url 'https://www.jitpack.io' } // <- Add this line.
   }
}
```
- If not, modify your project-level `build.gradle` file instead:
```groovy
allprojects {
    repositories {
        maven { url "https://jitpack.io" }  // <- Add this line.
    }
}
```

2. Modify your app-level `build.gradle` file:
```groovy
dependencies {
    ...
    implementation 'com.github.ZEGOCLOUD:zego_uikit_prebuilt_video_conference_android:1.0.0'    // Add this line in your module-level build.gradle file's dependencies, usually named [app].
}
```  

### Using the ZegoUIKitPrebuiltVideoConferenceFragment in your project

- Go to [ZEGOCLOUD Admin Console\|_blank](https://console.zegocloud.com/), get the `appID` and `appSign` of your project.
- Specify the `userID` and `userName` for connecting the Video Conference Kit service. 
- Create a `conferenceID` that represents the conference you want to start. 

<div class="mk-hint">

- `userID` and `conferenceID` can only contain numbers, letters, and underlines (_). 
- Using the same `conferenceID` will enter the same video conference.
</div>

```java
public class ConferenceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conference);

        addFragment();
    }

    public void addFragment() {
        long appID = yourAppID;
        String appSign = yourAppSign;

        String conferenceID = conferenceID;
        String userID = userID;
        String userName = userName;

        ZegoUIKitPrebuiltVideoConferenceConfig config = new ZegoUIKitPrebuiltVideoConferenceConfig();
        ZegoUIKitPrebuiltVideoConferenceFragment fragment = ZegoUIKitPrebuiltVideoConferenceFragment.newInstance(appID, appSign, userID, userName,conferenceID,config);

        getSupportFragmentManager().beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commitNow();
    }
}
```

Modify the auto-created `activity_conference.xml` file:
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
  android:id="@+id/fragment_container"
  android:layout_width="match_parent"
  android:layout_height="match_parent">

</androidx.constraintlayout.widget.ConstraintLayout>
```

Now, you can start a video conference by starting your `ConferenceActivity`.


## Run & Test

Now you have finished all the steps!

You can simply click the **Run** on Android Studio to run and test your App on your device.


## Related guide

[Custom prebuilt UI](!VideoConferenceKit_Custom_prebuiltUI)


## Resources


<div class="md-grid-list-box">
  <a href="https://github.com/ZEGOCLOUD/zego_uikit_prebuilt_video_conference_example_android" class="md-grid-item" target="_blank">
    <div class="grid-title">Sample code</div>
    <div class="grid-desc">Click here to get the complete sample code.</div>
  </a>
</div>
