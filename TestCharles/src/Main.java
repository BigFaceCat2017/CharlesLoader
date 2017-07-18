import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.bytecode.ConstPool;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import java.io.ByteArrayInputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

public class Main {
    private static License license = new License();

    public static void premain(String agentOps, Instrumentation inst) {
        inst.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

                if (className.replace("/", ".").equals("com.xk72.charles.gui.SplashWindow")) {
                    try {
                        CtClass splashWindowClass = ClassPool.getDefault().makeClass(new ByteArrayInputStream(classfileBuffer));
                        CtClass stringClass = splashWindowClass.getClassPool().getCtClass(String.class.getName());
                        CtMethod cls = splashWindowClass.getDeclaredMethod("showRegistrationStatus");
                        cls.instrument(new ExprEditor() {
                            @Override
                            public void edit(MethodCall m) throws CannotCompileException {
                                super.edit(m);
                                String clazzName = m.getClassName();
                                String md = m.getMethodName();
                                String sign = m.getSignature();
                                if (!clazzName.equals("com.xk72.charles.gui.SplashWindow") && clazzName.startsWith("com.xk72.charles") && sign.equals("()Z")) {
                                    license.setIsLicensedMethod(md);
                                    license.setIsLicenseClassName(clazzName);
                                    license.setIsLicensedMethodType(CtClass.booleanType);
                                }
                                if (!clazzName.equals("com.xk72.charles.gui.SplashWindow") && clazzName.startsWith("com.xk72.charles") && sign.equals("()Ljava/lang/String;")) {
                                    license.setRegisterToMethod(md);
                                    license.setRegisterToClassName(clazzName);
                                    license.setRegisterToMethodType(stringClass);
                                }
                            }
                        });
                    } catch (Exception e) {
                        return new byte[0];
                    }
                } else if (!license.getIsLicensedMethod().isEmpty() && !license.getRegisterToMethod().isEmpty() && license.getIsLicenseClassName().equals(license.getRegisterToClassName()) && className.replace("/", ".").equals(license.getRegisterToClassName())) {
                    try {
                        CtClass licenseClass = ClassPool.getDefault().makeClass(new ByteArrayInputStream(classfileBuffer));
                        CtMethod isLicenseMethod = licenseClass.getDeclaredMethod(license.getIsLicensedMethod(), null);
                        isLicenseMethod.setBody("return true;");
                        CtMethod registerToMethod = licenseClass.getDeclaredMethod(license.getRegisterToMethod(), null);
                        registerToMethod.setBody("return \"qtfreet00 www.52pojie.cn\";");
                        return licenseClass.toBytecode();
                    } catch (Exception e) {
                        return new byte[0];
                    }
                }
                return new byte[0];
            }
        });
    }

    public static void main(String[] args) throws Exception {
        System.out.println("loader init success");
    }
}
