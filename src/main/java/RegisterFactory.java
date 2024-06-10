import register.INFO.Config;
import register.Register;
import spi.SpiLoader;

public class RegisterFactory {
    static {
        SpiLoader.load(Register.class);
    }
    public static Register getInstance(String serializerKey){
        return SpiLoader.getInstance(Register.class, serializerKey);
    }
}
