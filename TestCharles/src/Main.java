import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.bytecode.ConstPool;

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
                        CtMethod showRegistrationStatus = splashWindowClass.getDeclaredMethod("showRegistrationStatus");
                        ConstPool constPool = showRegistrationStatus.getMethodInfo().getConstPool();
                        int size = constPool.getSize();
                        for (int i = 1; i < size; i++) {
                            if (constPool.getTag(i) == ConstPool.CONST_Methodref) {
                                String className1 = constPool.getMethodrefClassName(i);
                                String methodName = constPool.getMethodrefName(i);
                                String signature = constPool.getMethodrefType(i);
                                if (!className1.equals("com.xk72.charles.gui.SplashWindow") && className1.startsWith("com.xk72.charles") && signature.equals("()Z")) {
                                    license.setIsLicensedMethod(methodName);
                                    license.setIsLicenseClassName(className1);
                                    license.setIsLicensedMethodType(CtClass.booleanType);
                                    continue;
                                }
                                if (!className1.equals("com.xk72.charles.gui.SplashWindow") && className1.startsWith("com.xk72.charles") && signature.equals("()Ljava/lang/String;")) {
                                    license.setRegisterToMethod(methodName);
                                    license.setRegisterToClassName(className1);
                                    license.setRegisterToMethodType(stringClass);
                                }
                            }
                        }
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
