package me.i2000c.newalb.custom_outcomes.rewards;

import com.cryptomorin.xseries.XMaterial;

import lombok.Value;
import me.i2000c.newalb.utils.textures.Texture;
import me.i2000c.newalb.utils2.ItemStackWrapper;

@Value
public class TypeData {
    private final XMaterial material;
    private final Texture texture;
    
    public TypeData(ItemStackWrapper wrapper){
        if(wrapper == null){
            this.material = null;
            this.texture = null;
        } else {
            this.material = wrapper.getMaterial() == XMaterial.PLAYER_WALL_HEAD ? XMaterial.PLAYER_HEAD : wrapper.getMaterial();
            this.texture = wrapper.getTexture();
        }
    }
}
