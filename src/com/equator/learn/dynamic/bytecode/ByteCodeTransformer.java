package com.equator.learn.dynamic.bytecode;

import com.equator.learn.dynamic.base.GsonUtils;
import com.equator.learn.dynamic.base.LogData;
import com.google.gson.JsonObject;
import javassist.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

/**
 * @author libinkai
 * @date 2020/11/4 10:15 上午
 */
@Slf4j
public class ByteCodeTransformer implements ClassFileTransformer {
    // @SneakyThrows 巨坑！！！有异常不抛出
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        try {
            log.info("替换字节码");
            ClassPool classPool = ClassPool.getDefault();
            CtClass ctClass = classPool.get("com.equator.learn.dynamic.ordinary.TransformationA");
            CtMethod ctMethod = ctClass.getDeclaredMethod("transform");
            // 注意事项：语句或者代码块，引用类需要全限定名，只能替换一次
            ctMethod.setBody("{\n" +
                    "        String source = \"Topic2\";\n" +
                    "        log.info(\"转换数据源：{}\", source);\n" +
                    "        String sourceDataStr = getSourceData(source);\n" +
                    "        if (org.apache.commons.lang3.StringUtils.isNotEmpty(sourceDataStr)) {\n" +
                    "            com.equator.learn.dynamic.base.LogData logData = com.equator.learn.dynamic.base.GsonUtils.fromJson(sourceDataStr, com.equator.learn.dynamic.base.LogData.class);\n" +
                    "            logData.setData(new java.lang.String(\"666\"));\n" +
                    "            System.out.println(logData);\n" +
                    "        }\n" +
                    "    }");
            ctClass.writeFile("/Users/libinkai/Desktop");
            return ctClass.toBytecode();
        } catch (IOException | NotFoundException e) {
            e.printStackTrace();
        } catch (CannotCompileException e) {
            e.printStackTrace();
        }
        return null;
    }
}