package com.kevinja.lockedshelf.device;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;

/**
 * Created by kevinwallace on 2016-05-05.
 */
public class LollipopDeviceLockManager extends LegacyDeviceLockManager {

    protected LollipopDeviceLockManager(Activity activity) {
        super(activity);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void startUnlockActivity() {
        Intent intent = keyguardManager.createConfirmDeviceCredentialIntent(null, null);
        if (intent != null) {
            activity.startActivityForResult(intent, DeviceLockManager.REQUEST_CODE_UNLOCK);
        }
    }

}
