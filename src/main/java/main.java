import register.INFO.Meta;
import register.Register;

public class main {
    public static void main(String[] args) {
        Register register = RegisterFactory.getInstance("local");
        Meta meta = Meta.builder()
                .serviceName("asouwn")
                .serviceUrl("http://hello/asouwn").build();
        register.register(meta);
        System.out.println(register.getService(meta));
    }

}
