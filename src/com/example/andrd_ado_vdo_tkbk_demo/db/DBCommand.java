public class DBCommand implements java.io.Serializable {
    private String name;
    private String parameter;

    public DBCommand(String name, String parameter) {
        this.name = name;
        this.parameter = parameter;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    @Override
    public String toString() {
        return "DBCommand{" +
                "name='" + name + '\'' +
                ", parameter='" + parameter + '\'' +
                '}';
    }
}