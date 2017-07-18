import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.bytecode.ConstPool;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import java.io.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Crack {
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("please enter the correct path,such as java -jar charlesLoader.jar old.jar new.jar");
            return;
        }
        System.out.println("Charles 4.x Crack tool by qtfreet00");
        System.out.println("start search the class");
        ClassPool pool = ClassPool.getDefault();
        pool.insertClassPath(args[0]);
        CtClass splashWindowClass = pool.getCtClass("com.xk72.charles.gui.SplashWindow");
        License license = new License();
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
                    System.err.println(clazzName + "." + md + sign);
                    license.setIsLicensedMethod(md);
                    license.setIsLicenseClassName(clazzName);
                    license.setIsLicensedMethodType(CtClass.booleanType);
                }
                if (!clazzName.equals("com.xk72.charles.gui.SplashWindow") && clazzName.startsWith("com.xk72.charles") && sign.equals("()Ljava/lang/String;")) {
                    System.err.println(clazzName + "." + md + sign);
                    license.setRegisterToMethod(md);
                    license.setRegisterToClassName(clazzName);
                    license.setRegisterToMethodType(stringClass);
                }
            }
        });
        if (!license.getIsLicensedMethod().isEmpty() && !license.getRegisterToMethod().isEmpty() && license.getIsLicenseClassName().equals(license.getRegisterToClassName())) {
            System.err.println("found class " + license.getIsLicenseClassName());
            System.out.println("start crack");
            CtClass licenseClass = pool.getCtClass(license.getIsLicenseClassName());
            CtMethod isLicenseMethod = licenseClass.getDeclaredMethod(license.getIsLicensedMethod(), null);
            isLicenseMethod.setBody("return true;");
            CtMethod registerToMethod = licenseClass.getDeclaredMethod(license.getRegisterToMethod(), null);
            registerToMethod.setBody("return \"qtfreet00 www.52pojie.cn\";");
            byte[] bytes = licenseClass.toBytecode();
            System.out.println("start making new jar file");
            processJar(new File(args[0]), new File(args[1]), license.getIsLicenseClassName(), bytes);
            System.err.println("crack completed");
        }

    }

    private static void processJar(File jarIn, File jarOut, String name, byte[] in) throws IOException {
        ZipInputStream zis = null;
        ZipOutputStream zos = null;
        try {

            zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(jarIn)), Charset.forName("UTF-8"));
            zos = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(jarOut)), Charset.forName("UTF-8"));
            ZipEntry entryIn;
            Map<String, Integer> processedEntryNamesMap = new HashMap<>();
            while ((entryIn = zis.getNextEntry()) != null) {
                final String entryName = entryIn.getName();
                if (!processedEntryNamesMap.containsKey(entryName)) {
                    ZipEntry entryOut = new ZipEntry(entryIn);
                    entryOut.setCompressedSize(-1);
                    zos.putNextEntry(entryOut);
                    if (!entryIn.isDirectory()) {
                        if (entryName.endsWith(".class") && entryName.replace("/", ".").equals(name + ".class")) {
                            zos.write(in);
                        } else {
                            copy(zis, zos);
                        }
                    }
                    zos.closeEntry();
                    processedEntryNamesMap.put(entryName, 1);
                }
            }

        } finally {
            closeQuietly(zos);
            closeQuietly(zis);
        }
    }

    private static void closeQuietly(Closeable target) {
        if (target != null) {
            try {
                target.close();
            } catch (Exception e) {
                // Ignored.
            }
        }
    }

    private static int copy(InputStream in, OutputStream out) throws IOException {
        int total = 0;
        byte[] buffer = new byte[8192];
        int c;
        while ((c = in.read(buffer)) != -1) {
            total += c;
            out.write(buffer, 0, c);
        }
        return total;
    }
}
