package register.impl;

import register.INFO.Meta;
import register.Register;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LocalRegister implements Register {
    private static final Map<String, String> m = new ConcurrentHashMap<>();


    @Override
    public void register(Meta meta) {
        m.put(meta.getServiceName(), meta.getServiceUrl());
    }

    @Override
    public void disRegister(Meta meta) {
        m.remove(meta.getServiceName());
    }

    @Override
    public String getService(Meta meta) {
        return m.get(meta.getServiceName());
    }

}
