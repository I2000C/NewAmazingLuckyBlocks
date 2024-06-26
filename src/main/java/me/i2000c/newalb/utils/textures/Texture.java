package me.i2000c.newalb.utils.textures;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.i2000c.newalb.MinecraftVersion;
import me.i2000c.newalb.config.ConfigManager;
import me.i2000c.newalb.reflection.ReflectionManager;

@Getter
@EqualsAndHashCode(of = "id")
public final class Texture{
    
    private String id;
    private GameProfile profile;

    public Texture(String ID) throws TextureException{
        try{
            String textureURL = "http://textures.minecraft.net/texture/" + ID;
            if(ConfigManager.getMainConfig().getBoolean("CheckIfTexturesAreValid")){                
                URL url = new URL(textureURL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(1000);
                connection.setRequestMethod("GET");
                int code = connection.getResponseCode();
                if(code == 404){
                    throw new InvalidTextureException(ID);
                }
            }

            this.id = ID;
            this.profile = new GameProfile(UUID.randomUUID(), "CustomHead");
            byte[] encodedData = Base64.getEncoder().encode(String.format("{textures:{SKIN:{\"url\":\"%s\"}}}", textureURL).getBytes());
            this.profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
        }catch(IOException ex){
            this.profile = null;
            this.id = null;
            throw new URLTextureException(ex);
        }
    }

    public Texture(GameProfile profile){
        this.profile = profile;
        this.id = getIDFromProfile(profile);
    }

    public String getEncodedTexture(){
        Property property = this.profile.getProperties().get("textures").iterator().next();
        
        // From Minecraft 1.8 to 1.20.1, the method is called getValue()
        // Since Minecraft 1.20.2, the method is called value()
        if(MinecraftVersion.CURRENT_VERSION.isGreaterThanOrEqual(MinecraftVersion.v1_20_2)) {
            return ReflectionManager.callMethod(property, "value");
        } else {
            return ReflectionManager.callMethod(property, "getValue");
        }
    }

    public String getURL(){
        try{
            String preUrl = new String(Base64.getDecoder().decode(this.getEncodedTexture().getBytes()));
            Pattern p = Pattern.compile("https?[^\"]*");
            Matcher m = p.matcher(preUrl);
            if(m.find()){
                return m.group(0);
            }else{
                return null;
            }                
        }catch(Exception ex){
            return null;
        }            
    }

    public String getIDFromProfile(GameProfile profile){
        try{
            String url = this.getURL();
            Pattern p = Pattern.compile("/([^/]*)$");
            Matcher m = p.matcher(url);
            if(m.find()){
                return m.group(1);
            }else{
                return null;
            }
        }catch(Exception ex){
            return null;
        }
    }
    
    @Override
    public String toString() {
        return this.id;
    }
}
