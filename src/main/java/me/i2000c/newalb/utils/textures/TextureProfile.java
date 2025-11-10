package me.i2000c.newalb.utils.textures;

import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
class TextureProfile {
	private Long timestamp;
	private String profileId;
	private String profileName;
	private Textures textures;
	
	public String getSkinId() {
		if(textures == null || textures.skin == null || textures.skin.url == null) {
			return null;
		} else {
			String[] split = textures.skin.url.split("/");
			return split[split.length - 1];
		}
	}
	
	@Data
	public static class Textures {
		@SerializedName("SKIN")
		private TextureValue skin;
		
		@SerializedName("CAPE")
		private TextureValue cape;
	}
	
	@Data
	public static class TextureValue {
		private String url;
	}
}
