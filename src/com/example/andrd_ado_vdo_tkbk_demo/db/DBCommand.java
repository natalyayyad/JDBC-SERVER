package com.example.andrd_ado_vdo_tkbk_demo.db;

public class DBCommand implements java.io.Serializable {
    // must be the same on client and server sides
    private static final long serialVersionUID = 1132298746559575197L;
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
        return "com.example.andrd_ado_vdo_tkbk_demo.db.DBCommand{" +
                "name='" + name + '\'' +
                ", parameter='" + parameter + '\'' +
                '}';
    }
}