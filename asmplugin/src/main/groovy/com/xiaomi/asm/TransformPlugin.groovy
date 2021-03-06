package com.xiaomi.asm
import org.gradle.api.Plugin
import org.gradle.api.Project
import com.android.build.gradle.AppExtension

/**
 * 注册自定义Transform 的插件
 */
class TransformPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        println "Hello TransformPlugin"
        def android = project.extensions.getByType(AppExtension)
        //注册 Transform，操作 class 文件
        android.registerTransform(new CustomTransform(project))
    }
}