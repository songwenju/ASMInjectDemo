package com.xiaomi.customplugin
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * 自定义插件
 */
class CustomPluginB implements Plugin<Project> {

    @Override
    void apply(Project project) {
        println "CustomPluginB Hello"
        //将Extension注册给Plugin
        def extension = project.extensions.create("customB", CustomExtensionB)
        //定义一个任务
        project.task('CustomPluginTaskB') {
            doLast {
                println "接收外部参数：${extension.extensionArgs}"
            }
        }
    }
}