package com.divya.mettl.first.phase.service;

import com.divya.mettl.first.phase.entity.Customer;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Divya.Gupta on 06-04-2018.
 */
@Component
public class CacheService {

    private static final Map<String, Object> userCacheMap = new HashMap<String, Object>();

    public static boolean foundInUserCacheMap(String loyaltyCardNum) {
        return null != userCacheMap.get(loyaltyCardNum);
    }

    public static void putInUserCacheMap(String loyaltyCardNum, Customer customer) {
        userCacheMap.put(loyaltyCardNum, customer);
    }

    public static Object getFromUserCacheMap(String loyaltyCardNum) {
        return userCacheMap.get(loyaltyCardNum);
    }

    public static Map<String, Object> getUserCacheMap() {
        return userCacheMap;
    }
}
