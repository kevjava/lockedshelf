package com.kevinja.lockedshelf.device;

import android.app.Activity;
import android.os.Build;

/**
 * Created by kevinwallace on 2016-05-05.
 */
public abstract class DeviceLockManager {

    public static int REQUEST_CODE_UNLOCK = 5001; // TODO: Arbitrary.

    protected Activity activity;

    protected DeviceLockManager(Activity activity) {
        this.activity = activity;
    }

    public static DeviceLockManager getInstance(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return new MarshmallowDeviceLockManager(activity);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return new LollipopDeviceLockManager(activity);
        } else {
            return new LegacyDeviceLockManager(activity);
        }
    }

    public abstract boolean isDeviceLocked();

    public abstract boolean isDeviceSecured();

    public abstract void startUnlockActivity();
}
