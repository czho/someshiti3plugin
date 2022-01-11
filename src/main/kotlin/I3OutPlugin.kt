import com.lambda.client.plugin.api.Plugin

internal object I3OutPlugin: Plugin() {

    override fun onLoad() {
        // Load any modules, commands, or HUD elements here
        modules.add(I3DataOut)
    }

    override fun onUnload() {
        // Here you can unregister threads etc...
    }
}