package extension

import org.gradle.api.plugins.PluginAware
import org.gradle.kotlin.dsl.apply
import org.gradle.plugin.use.PluginDependenciesSpec
import org.gradle.plugin.use.PluginDependencySpec

inline fun PluginDependenciesSpec.script(plugin: String): PluginDependencySpec = id("script.$plugin")

inline fun PluginAware.plugin(id: String) = apply(plugin = id)

inline fun PluginAware.script(id: String) = plugin("script.$id")