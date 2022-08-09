package me.i2000c.newalb.utils.textures;

public class InvalidTextureException extends TextureException{
    public InvalidTextureException(String textureID){
        super("Invalid texture with ID: " + textureID);
    }
}
