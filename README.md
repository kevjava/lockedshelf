# Locked Shelf

The "Locked Shelf" is an encrypted caching mechanism for Android.  It is an extension of 
[Todd Way's Shelf library](https://github.com/toddway/Shelf) which also incorporates code from 
[Nikolay Elenkov's android-keystore](https://github.com/nelenkov/android-keystore) example code.

It allows encrypted local caching of objects (serialized with Gson) which are stored in a directory 
in an Android application's local storage.  API levels 18 and up are tested to work.

## Simple usage
A builder is provided to create a local "locked" shelf, given an Android context:

```java
    Shelf lockedShelf = new LockedShelfBuilder(context)
            .setId("locked-shelf")
            .setGenerateKeyIfNotFound(true)
            .setDefaultLifetime(15, TimeUnit.MINUTES)
            .build();
```

The `id` is the alias of the key in the Android keystore.  This allows for separate keys to be used
to encrypt potentially sensitive object(s), if perhaps multiple users can log into your application, 
or if you just want to use separate keys for separate pieces of data.

The `setGenerateKeyIfNotFound()` method will tell the locked shelf to create a brand new symmetric 
AES key and store it in the keystore, if the specified key doesn't exist in the device's keystore.  
If there is a setup process for your application's user, you should set the flag there, and depend 
on the key being there for future uses.  

The default lifetime of a shelf object is how long the cache should be used before attempting to 
fetch again. 

To store an object, just create a `ShelfItem` with its own shelf key, and "put" an object there:

```java
    String myPassword = "trustno1";
    lockedShelf.item("super-secret-password").put(myPassword);
```

This will create a file `/data/data/com.yourapp.package/locked-shelf/super-secret-password.obj`, 
containing a Base64-encoded, AES encrypted version of the object.

To fetch it later, get the `ShelfItem` and "get" it, telling the Shelf which class to serialize:

```java
    String myPassword = lockedShelf.item("super-secret-password").get(String.class);
```

There is much more to it, though - Todd's library is configurable and customizable, and includes 
hooks for RxJava to do your loading in a background thread.  This library just adds a custom 
serializer which encrypts/decrypts and deals with the keystore.

## Notes

#### The user's device must be secured.  
The device must have a PIN, password, pattern lock, or a fingerprint set up to lock the device. 
This mechanism is what unlocks the keystore that secures the AES keys.

To that end, a `KeyStoreUtil` class is provided which has some convenience methods for dealing with 
the Android Keystore.
    * `isDeviceSecure()` - returns true if the device has a secure keyguard and we're go for 
    encryption.
    * `isKeystoreReady()` - returns true if the keystore is unlocked and ready to use. 
    * `ensureKeyguardIsUnlocked()` - Will check to make sure the keyguard is open, or will prompt 
    the user to unlock it and restart your activity with an OK or CANCEL result.

#### Uninstalling the application destroys the encryption keys.
All encryption keys for the app are removed if the user uninstalls an application that uses the 
keystore.  Of course, by default, since your objects are being stored in the cache directory of the 
application, those will be gone too, so no biggy, right?  For this reason, this should be used for 
things like cache, and probably isn't good for important data you want to keep around after the app 
goes away.

## How to use it

You can import this repository as a module in Android Studio, or if you're using Gradle, it's dead 
simple:

```groovy
repositories {
    maven {
        url  "http://dl.bintray.com/kevjava/maven" // It's also on JCenter, so you might not need this.
    }
}

dependencies {
    compile 'com.kevinja.lockedshelf:lockedshelf:1.1.0'
}
```

## Dependencies (also, thanks for letting me use your code):
  * Shelf: [toddway/shelf](https://github.com/toddway/Shelf) (Apache 2.0 license)
  * Spongycastle: [rtyley/spongycastle](https://rtyley.github.io/spongycastle/) (MIT/X11 license)
  * Android-keystore example: [nelenkov/android-keystore](https://github.com/nelenkov/android-keystore) (Apache 2.0 license)
      * Note: this is not a dependency, but code from this repository has been packaged in with this 
        library to support versions of Android prior to 6.0 (Marshmallow).