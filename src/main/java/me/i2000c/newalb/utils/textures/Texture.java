package me.i2000c.newalb.utils.textures;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import me.i2000c.newalb.reflection.RefClass;
import me.i2000c.newalb.reflection.RefMethod;
import me.i2000c.newalb.utils.ConfigManager;

@Getter
@EqualsAndHashCode(of = "id")
@ToString(of = "id")
public final class Texture{
    private static final RefMethod getValue;
    
    static {
        // From Minecraft 1.8 to 1.20.1, the method is called getValue()
        // Since Minecraft 1.20.2, the method is called value()
        RefMethod refMethod = RefClass.of(Property.class).getMethod("getValue");
        if(refMethod == null) {
            refMethod = RefClass.of(Property.class).getMethod("value");
        }
        getValue = refMethod;
    }
    
    private String id;
    private GameProfile profile;

    public Texture(String ID) throws TextureException{
        try{
            String textureURL = "http://textures.minecraft.net/texture/" + ID;
            if(ConfigManager.getConfig().getBoolean("CheckIfTexturesAreValid")){                
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
        return getValue.call(property);
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
}
