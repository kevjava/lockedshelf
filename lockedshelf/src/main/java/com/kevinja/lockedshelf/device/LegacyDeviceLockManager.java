package com.kevinja.lockedshelf.device;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;

/**
 * Created by kevinwallace on 2016-05-05.
 */
public class LegacyDeviceLockManager extends DeviceLockManager {

    protected KeyguardManager keyguardManager;
    protected static final String UNLOCK_ACTION = "com.android.credentials.UNLOCK";

    protected LegacyDeviceLockManager(Activity activity) {
        super(activity);
        keyguardManager = (KeyguardManager) activity.getSystemService(Context.KEYGUARD_SERVICE);
    }

    @Override
    public boolean isDeviceLocked() {
        // Includes a locked SIM card, which isn't something we're interested in.  isDeviceLocked
        //  was introduced in API 22.
        return keyguardManager.isKeyguardLocked();
    }

    @Override
    public boolean isDeviceSecured() {
        // Includes a secured SIM card, which isn't something we're interested in.  isDeviceSecure()
        //  was introduced in API 23.
        return keyguardManager.isKeyguardSecure();
    }

    @Override
    public void startUnlockActivity() {
        activity.startActivity(new Intent(UNLOCK_ACTION));
    }
}
