import javassist.CtClass;

public class License {
    private String isLicensedMethod;
    private CtClass isLicensedMethodType;
    private String isLicenseClassName;

    public String getIsLicenseClassName() {
        return isLicenseClassName;
    }

    public void setIsLicenseClassName(String isLicenseClassName) {
        this.isLicenseClassName = isLicenseClassName;
    }

    public String getRegisterToClassName() {
        return registerToClassName;
    }

    public void setRegisterToClassName(String registerToClassName) {
        this.registerToClassName = registerToClassName;
    }

    private String registerToMethod;
    private CtClass registerToMethodType;
    private String registerToClassName;

    public String getIsLicensedMethod() {
        return isLicensedMethod;
    }

    public void setIsLicensedMethod(String isLicensedMethod) {
        this.isLicensedMethod = isLicensedMethod;
    }

    public CtClass getIsLicensedMethodType() {
        return isLicensedMethodType;
    }

    public void setIsLicensedMethodType(CtClass isLicensedMethodType) {
        this.isLicensedMethodType = isLicensedMethodType;
    }

    public String getRegisterToMethod() {
        return registerToMethod;
    }

    public void setRegisterToMethod(String registerToMethod) {
        this.registerToMethod = registerToMethod;
    }

    public CtClass getRegisterToMethodType() {
        return registerToMethodType;
    }

    public void setRegisterToMethodType(CtClass registerToMethodType) {
        this.registerToMethodType = registerToMethodType;
    }
}
