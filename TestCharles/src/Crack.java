import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.bytecode.ConstPool;

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
            System.out.println("请正确输入charles.jar包的路径和新生成的jar包存放路径");
            return;
        }
        System.out.println("Charles 4.x Crack tool by qtfreet00");
        System.out.println("开始查找待破解的方法");
        ClassPool pool = ClassPool.getDefault();
        pool.insertClassPath(args[0]);
        CtClass splashWindowClass = pool.getCtClass("com.xk72.charles.gui.SplashWindow");
        License license = new License();
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
                    System.out.println(className1);
                    System.out.println(methodName);
                    license.setIsLicensedMethod(methodName);
                    license.setIsLicenseClassName(className1);
                    license.setIsLicensedMethodType(CtClass.booleanType);
                    continue;
                }
                if (!className1.equals("com.xk72.charles.gui.SplashWindow") && className1.startsWith("com.xk72.charles") && signature.equals("()Ljava/lang/String;")) {
                    license.setRegisterToMethod(methodName);
                    license.setRegisterToClassName(className1);
                    license.setRegisterToMethodType(stringClass);
                    break;
                }
            }
        }
        if (!license.getIsLicensedMethod().isEmpty() && !license.getRegisterToMethod().isEmpty() && license.getIsLicenseClassName().equals(license.getRegisterToClassName())) {
            System.out.println("已找到类" + license.getIsLicenseClassName());
            System.out.println("开始破解");
            CtClass licenseClass = pool.getCtClass(license.getIsLicenseClassName());
            CtMethod isLicenseMethod = licenseClass.getDeclaredMethod(license.getIsLicensedMethod(), null);
            isLicenseMethod.setBody("return true;");
            CtMethod registerToMethod = licenseClass.getDeclaredMethod(license.getRegisterToMethod(), null);
            registerToMethod.setBody("return \"qtfreet00 www.52pojie.cn\";");
            byte[] bytes = licenseClass.toBytecode();
            System.out.println("开始生成新jar包");
            processJar(new File(args[0]), new File(args[1]), license.getIsLicenseClassName(), bytes);
            System.out.println("破解完成");
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
