package me.i2000c.newalb.utils.particles.data;

import com.cryptomorin.xseries.XMaterial;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.i2000c.newalb.api.version.MinecraftVersion;
import me.i2000c.newalb.utils.reflection.ReflectionManager;

import org.bukkit.inventory.ItemStack;
import xyz.xenondevs.particle.data.texture.BlockTexture;
import xyz.xenondevs.particle.data.texture.ItemTexture;

@Getter
@Setter
@AllArgsConstructor
public class MaterialParticleData extends ParticleData {
    
    private ItemStack item;
    private boolean isBlockData;
    
    public MaterialParticleData(XMaterial material, boolean isBlockData) {
        this(material.parseItem(), isBlockData);
    }

    @Override
    public xyz.xenondevs.particle.data.ParticleData convertToParticleLibData() {
        if(isBlockData) {
            if(MinecraftVersion.CURRENT_VERSION.isLegacyVersion()) {
                return new BlockTexture(item.getType(), (byte) item.getDurability());
            } else {
                return new BlockTexture(item.getType());
            }
        } else {
            return new ItemTexture(item);
        }
    }

    @Override
    public <T> T convertToBukkitParticleData() {
        if(isBlockData) {
            Object data = ReflectionManager.callMethod(item.getType(), "createBlockData");
            return (T) data;
        } else {
            return (T) item;
        }
    }
    
}
