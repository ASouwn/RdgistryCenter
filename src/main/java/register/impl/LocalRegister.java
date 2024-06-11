package register.impl;

import register.INFO.Config;
import register.INFO.Meta;
import register.Register;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LocalRegister implements Register {
    private static final Map<String, Meta> m = new ConcurrentHashMap<>();


    @Override
    public void init(Config config) {

    }

    @Override
    public void register(Meta meta) {
        m.put(meta.getKey(), meta);
    }

    @Override
    public void disRegister(Meta meta) {
        m.remove(meta.getServiceName());
    }

    @Override
    public List<Meta> getService(Meta meta) {
        return Collections.singletonList(m.get(meta.getServiceName()));
    }

}
