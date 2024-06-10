package register.impl;

import register.Register;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LocalRegister implements Register {
    private static final Map<String, String> m = new ConcurrentHashMap<>();

    @Override
    public void register(String service, String implHost) {
        m.put(service, implHost);
    }

    @Override
    public void disRegister(String service) {
        m.remove(service);

    }

    @Override
    public String getService(String service) {
        return m.get(service);

    }

}
