package com.xiaomi.asm

import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.Format
import com.android.build.api.transform.JarInput
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInput
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import groovy.io.FileType
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.IOUtils
import org.gradle.api.Project

/**
 * 自定义的 Transform 类
 */
class CustomTransform extends Transform {
    Project mProject

    CustomTransform(Project project) {
        mProject = project
    }

    //自定义 Task 名称
    @Override
    String getName() {
        return "customTransform"
    }

    /**
     * 需要处理的数据类型，有两种枚举类型
     * CLASSES 代表处理的 java 的 class 文件，RESOURCES 代表要处理 java 的资源
     * @return
     */
    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    /**
     * 指 Transform 要操作内容的范围，官方文档 Scope 有 7 种类型：
     * 1. EXTERNAL_LIBRARIES        只有外部库
     * 2. PROJECT                   只有项目内容
     * 3. PROJECT_LOCAL_DEPS        只有项目的本地依赖(本地jar)
     * 4. PROVIDED_ONLY             只提供本地或远程依赖项
     * 5. SUB_PROJECTS              只有子项目。
     * 6. SUB_PROJECTS_LOCAL_DEPS   只有子项目的本地依赖项(本地jar)。
     * 7. TESTED_CODE               由当前变量(包括依赖项)测试的代码
     * @return
     */
    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    // 当前Transform是否支持增量编译
    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation)
        /**Transform 的 inputs 有两种类型，一种是目录，一种是 jar 包，要分开遍历 */
        transformInvocation.getInputs().each {
            TransformInput input ->
                /**遍历目录*/
                input.directoryInputs.each {
                    DirectoryInput directoryInput ->
                        /**当前这个 Transform 输出目录*/
                        def dest = transformInvocation.getOutputProvider()
                                .getContentLocation(
                                        directoryInput.name,
                                        directoryInput.contentTypes,
                                        directoryInput.scopes, Format.DIRECTORY)
                        println "directory output dest: $dest.absolutePath"
                        File dir = directoryInput.file
                        HashMap<String, File> modifyMap = new HashMap<>()
                        if (dir) {
                            /**遍历以某一扩展名结尾的文件*/
                            dir.traverse(type: FileType.FILES, nameFilter: ~/.*\.class/) {
                                File classFile -> //遍历一遍把需要遍历的类存到 map
                                    //不处理R文件，BuildConfig.class 文件
                                    if (!classFile.name.endsWith("R.class")
                                            && !classFile.name.endsWith("BuildConfig.class")
                                            && !classFile.name.contains("R\$")) {
                                        //关键方法，修改class
                                        File modified = modifyClassFile(dir, classFile, transformInvocation.context.getTemporaryDir())
                                        if (modified != null) {
                                            /**key 为包名 + 类名，如：/cn/sensorsdata/autotrack/android/app/MainActivity.class*/
                                            modifyMap.put(classFile.absolutePath.replace(dir.absolutePath, ""), modified)
                                        }
                                    }
                            }
                            FileUtils.copyDirectory(directoryInput.file, dest)
                            //取出 map
                            modifyMap.entrySet().each {
                                Map.Entry<String, File> entry ->
                                    File target = new File(dest.absolutePath + entry.getKey());
                                    if(target.exists()) {
                                        target.delete()
                                    }
                                    //将修改的覆盖掉
                                    FileUtils.copyFile(entry.getValue(), target)
                                    entry.getValue().delete()
                            }
                        }
                }

                /**遍历 jar*/
                input.jarInputs.each {
                    JarInput jarInput ->
                        String destName = jarInput.file.name
                        String absolutePath = jarInput.file.absolutePath
                        println "jarInput destName: ${destName}"
                        println "jarInput absolutePath: ${absolutePath}"
                        // 重命名输出文件（同目录copyFile会冲突）
                        def md5Name = DigestUtils.md5(absolutePath)
                        if (destName.endsWith(".jar")) {
                            destName = destName.substring(0, destName.length() - 4)
                        }

                        //def modifyJar = ModifyUtils.modifyJar(jarInput.file, transformInvocation.context.getTemporaryDir())
                        def modifyJar = null
                        if (modifyJar == null) {
                            modifyJar = jarInput.file
                        }

                        //获取输出文件
                        File dest = transformInvocation.getOutputProvider()
                                .getContentLocation(destName+"_"+md5Name,
                                        jarInput.contentTypes, jarInput.scopes, Format.JAR)
                        //中间可以将 jarInput.file 进行操作！
                        //copy 到输出目录
                        FileUtils.copyFile(modifyJar, dest)
                }


        }
    }

    /**
     * 修改目录里的 class
     * @param dir
     * @param classFile
     * @param tempDir
     * @return
     */
    private static File modifyClassFile(File dir, File classFile, File tempDir) {
        File modified = null
        try {
            println "dir.absolutePath + File.separator: ${dir.absolutePath + File.separator}"
            String className = path2ClassName(classFile.absolutePath.replace(dir.absolutePath + File.separator, ""))
            println "className: $className"
            byte[] sourceClassBytes = IOUtils.toByteArray(new FileInputStream(classFile))
            //ASM修改class
            byte[] modifyClassBytes = ModifyUtils.modifyClasses(sourceClassBytes)
            if (modifyClassBytes) {
                modified = new File(tempDir, className.replace('.', '') + '.class')
                if (modified.exists()) {
                    modified.delete()
                }
                modified.createNewFile()
                new FileOutputStream(modified).write(modifyClassBytes)
            }
        } catch (Exception e) {
            e.printStackTrace()
        }
        return modified
    }
    private static String path2ClassName(String pathName) {
        pathName.replace(File.separator, ".").replace(".class", "")
    }
}