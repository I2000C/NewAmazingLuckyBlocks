package me.i2000c.newalb.utils.textures;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Texture{
    private String ID;
    private GameProfile profile;

    public Texture(String ID) throws TextureException{
        try{
            String textureURL = "http://textures.minecraft.net/texture/" + ID;
            URL url = new URL(textureURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(1000);
            connection.setRequestMethod("GET");
            int code = connection.getResponseCode();
            if(code == 404){
                throw new InvalidTextureException(ID);
            }

            this.ID = ID;
            this.profile = new GameProfile(UUID.randomUUID(), "CustomHead");
            byte[] encodedData = Base64.getEncoder().encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", textureURL).getBytes());
            this.profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
        }catch(IOException ex){
            this.profile = null;
            this.ID = null;
            throw new URLTextureException(ex);
        }
    }

    public Texture(GameProfile profile){
        this.profile = profile;
        this.ID = getIDFromProfile(profile);
    }

    public GameProfile getProfile(){
        return this.profile;
    }

    public String getEncodedTexture(){
        try{
            return this.profile.getProperties().get("textures").iterator().next().getValue();
        }catch(Exception ex){
            return null;
        }
    }

    public String getURL(){
        try{
            String preUrl = new String(Base64.getDecoder().decode(this.getEncodedTexture().getBytes()));
            Pattern p = Pattern.compile("\"([^\"]*)\"");
            Matcher m = p.matcher(preUrl);
            if(m.find()){
                return m.group(1);
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

    public String getID(){
        return this.ID;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Texture other = (Texture) obj;
        return Objects.equals(this.ID, other.ID);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + Objects.hashCode(this.ID);
        return hash;
    }

    @Override
    public String toString(){
        return this.getID();
    }
}
