package me.i2000c.newalb.utils.textures;

import java.io.IOException;

public class URLTextureException extends TextureException{
    public URLTextureException(IOException ex){
        super(ex.getMessage());
    }
}
