package com.xbyg_plus.silicon.service;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.orhanobut.logger.Logger;

public class InstanceIDService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        //String token = FirebaseInstanceId.getInstance().getToken();
        //Logger.d("token:" + token);
    }
}
