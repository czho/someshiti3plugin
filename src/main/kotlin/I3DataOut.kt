import com.lambda.client.event.SafeClientEvent
import com.lambda.client.module.Category
import com.lambda.client.plugin.api.PluginModule
import com.lambda.client.util.InfoCalculator
import com.lambda.client.util.MovementUtils.realSpeed
import com.lambda.client.util.TpsCalculator
import com.lambda.client.util.math.Direction
import com.lambda.client.util.math.RotationUtils
import com.lambda.client.util.threads.safeListener
import com.lambda.commons.utils.MathUtils
import net.minecraft.client.Minecraft
import net.minecraftforge.fml.common.gameevent.TickEvent
import java.io.File

internal object I3DataOut: PluginModule(
    name = "I3DataOut",
    category = Category.MISC,
    description = "Outputs data to file to be interperated by i3status wrapper",
    pluginMain = I3OutPlugin
) {
    val username by setting("Username", false)
    val armor by setting("armor", false)
    val fps by setting("fps", false)
    val ping by setting("ping", false)
    val serverbrand by setting("serverbrand", false)
    val tps by setting("tps", false)
    val direction by setting("direction", false)
    val playerspeed by setting("playerspeed", false)
    val pitch by setting("pitch", false)
    val yaw by setting("yaw", false)
    val coordinates by setting("coordinates", false)


    init {
        safeListener<TickEvent.ClientTickEvent> {
            if(it.phase != TickEvent.Phase.END) return@safeListener

            if(player.ticksExisted % 20 == 0) return@safeListener

            var line = ""

            if (username) line += "n: " + player.name + " | "
            if (armor) line += "d: " + getArmorDura() + " | "
            if (fps) line += "f: " + Minecraft.getDebugFPS().toString() + " | "
            if (ping) line += "p: " + InfoCalculator.ping().toString() + "ms | "
            if (serverbrand) line += (mc.player?.serverBrand ?: "Unknown Server Type") + " | "
            if (tps) line += "t: " + MathUtils.round(TpsCalculator.tickRate,3) + " | "
            if (direction) line += Direction.fromEntity(mc.renderViewEntity ?: player).displayNameXY + " | "
            if (playerspeed) line += "s: " + MathUtils.round(player.realSpeed * 3.6, 2) + "km/h | "
            if (pitch) line += "p: " + MathUtils.round(RotationUtils.normalizeAngle(mc.player?.rotationYaw ?: 0.0f), 1).toString() + " | "
            if (yaw) line += "y: " + MathUtils.round(mc.player?.rotationPitch ?: 0.0f, 1).toString() + " | "
            val coords = (mc.renderViewEntity ?: player).position
            if (coordinates) line += "x=${coords.x}, y=${coords.y}, z=${coords.z}"

            File(System.getProperty("user.home") + File.separator + "i3data").writeText(line)
        }


    }

    fun SafeClientEvent.getArmorDura(): String {
        //parts were taken from armor hudelement https://github.com/lambda-client/lambda/blob/master/src/main/kotlin/com/lambda/client/gui/hudgui/elements/combat/Armor.kt

        var allDura = ""

        for((index, itemStack) in player.armorInventoryList.reversed().withIndex()){
            if (!itemStack.isItemStackDamageable) continue

            val dura = itemStack.maxDamage - itemStack.itemDamage
            val duraMultiplier = dura / itemStack.maxDamage.toFloat()
            val duraPercent = MathUtils.round(duraMultiplier * 100.0f, 1)

            allDura += " | ${duraPercent}%"
        }

        return allDura
    }
}