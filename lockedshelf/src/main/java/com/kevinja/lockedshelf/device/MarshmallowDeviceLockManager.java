package com.kevinja.lockedshelf.device;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;

/**
 * Created by kevinwallace on 2016-05-05.
 */
public class MarshmallowDeviceLockManager extends LollipopDeviceLockManager {

    public MarshmallowDeviceLockManager(Activity activity) {
        super(activity);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public boolean isDeviceLocked() {
        return keyguardManager.isDeviceLocked();
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public boolean isDeviceSecured() {
        return keyguardManager.isDeviceSecure();
    }


}
