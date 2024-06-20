package me.i2000c.newalb.custom_outcomes.rewards;

import com.cryptomorin.xseries.XMaterial;

import lombok.Value;
import me.i2000c.newalb.utils.textures.Texture;
import me.i2000c.newalb.utils2.ItemStackWrapper;

@Value
public class TypeData {
    private final XMaterial material;
    private final Texture texture;
    
    public TypeData(XMaterial material, Texture texture) {
        this.material = material == XMaterial.PLAYER_WALL_HEAD ? XMaterial.PLAYER_HEAD : material;
        this.texture = texture;
    }
    
    public TypeData(ItemStackWrapper wrapper) {
        this(wrapper != null ? wrapper.getMaterial() : null, 
             wrapper != null ? wrapper.getTexture() : null);
    }
}
