package me.i2000c.newalb.config;

import com.cryptomorin.xseries.XMaterial;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Cleanup;
import lombok.Getter;
import lombok.NonNull;
import me.i2000c.newalb.api.serialization.ConfigDeserializer;
import me.i2000c.newalb.api.serialization.ConfigSerializer;
import me.i2000c.newalb.config.serializers.ItemStackWrapperSerializator;
import me.i2000c.newalb.config.serializers.LocationSerializator;
import me.i2000c.newalb.utils.misc.ItemStackWrapper;
import me.i2000c.newalb.utils.misc.XMaterialUtils;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

public class Config {
    private static final int DEFAULT_SPACES_PER_LEVEL = 2;
    private static final String BACKUP_EXTENSION = ".bak";
    private static final Charset CONFIG_CHARSET = StandardCharsets.UTF_8;
    
    @Getter
    private final Map<String, List<String>> comments = new HashMap<>();
    
    private YamlConfiguration yamlConfig;    
    private int spacesPerLevel = -1;
    
    public Config() {
        if(!ConfigManager.isInitialized()) {
            throw new InternalError("ConfigManager must be initialized before using configs");
        }
        this.yamlConfig = new YamlConfiguration(); 
    }
    
    public void loadConfig(String filename) {
        File file = new File(String.format("%s/%s", ConfigManager.getDataFolder(), filename));
        loadConfig(file);
    }
    public void loadConfig(File file) {
        if(!file.exists()) {
            clearConfig();
            return;
        }
        
        try {
            byte[] fileContents = inputStreamToByteArray(file);
            loadConfigInternal(fileContents);
        } catch(Exception ex) {
            throw new InternalError(ex);
        }
    }
    public void loadConfigFromResource(String resourcePath) {
        try {
            InputStream input = ConfigManager.getPlugin().getResource(resourcePath);
            Objects.requireNonNull(input, "Resource \"" + resourcePath + "\" doesn't exist");
            
            byte[] contents = inputStreamToByteArray(input);
            loadConfigInternal(contents);
        } catch(Exception ex) {
            throw new InternalError(ex);
        }
    }
    public void saveConfig(String filename) {
        File file = new File(String.format("%s/%s", ConfigManager.getDataFolder(), filename));
        saveConfig(file);
    }
    public void saveConfig(File file) {
        try {
            saveConfigInternal(file);
        } catch(Exception ex) {
            throw new InternalError(ex);
        }
    }
    public void updateConfig(String resourcePath, String filename, String... sectionsToIgnore) {
        File file = new File(String.format("%s/%s", ConfigManager.getDataFolder(), filename));
        updateConfig(resourcePath, file, sectionsToIgnore);
    }
    public void updateConfig(String resourcePath, File configFile, String... sectionsToIgnore) {
        try {
            updateConfigInternal(resourcePath, configFile, sectionsToIgnore);
        } catch(Exception ex) {
            throw new InternalError(ex);
        }
    }
    
    public void clearConfig() {
        this.yamlConfig = new YamlConfiguration();
    }
    
    public YamlConfiguration getBukkitConfig() {
        return this.yamlConfig;
    }
    
    private void loadConfigInternal(byte[] fileContents) throws IOException {
        comments.clear();
        
        List<String> localComments = null;        
        List<String> configPath = new ArrayList<>();
        
        @Cleanup BufferedReader reader = getReader(fileContents);
        for(String line = reader.readLine(); line != null; line = reader.readLine()) {
            if(isComment(line)) {
                // Comment or empty line
                if(localComments == null) {
                    localComments = new ArrayList<>();
                }
                
                localComments.add(line.trim());
            } else {
                // Config line
                String configKey = line.split(":")[0];                
                int identationSpaces = getIdentationSpaces(configKey);
                
                if(identationSpaces > 0 && spacesPerLevel == -1) {
                    spacesPerLevel = identationSpaces;
                }
                
                int configLevel = identationSpaces / spacesPerLevel;
                configPath = configPath.subList(0, configLevel);
                configPath.add(configKey.trim());
                
                if(localComments != null) {
                    String path = configPath.stream().collect(Collectors.joining("."));
                    comments.put(path, localComments);
                    localComments = null;
                }
            }
        }
        
        @Cleanup BufferedReader reader2 = getReader(fileContents);
        yamlConfig = YamlConfiguration.loadConfiguration(reader2);
    }
    
    private void saveConfigInternal(File file) throws IOException {
        file.getParentFile().mkdirs();
        @Cleanup BufferedWriter writer = getWriter(file);
        
        for(String fullKey : yamlConfig.getKeys(true)) {
            if(fullKey.charAt(0) == '.') fullKey = fullKey.substring(1);
            String[] splitKey = fullKey.split("\\.");
            
            int level = splitKey.length - 1;
            int spacesPerLevelAux = spacesPerLevel > 0 ? spacesPerLevel : DEFAULT_SPACES_PER_LEVEL;
            String spacesString = getSpacesString(level * spacesPerLevelAux);
            
            String key = splitKey[level];
            Object value = yamlConfig.get(fullKey);
            
            List<String> fieldComments = this.comments.getOrDefault(fullKey, Collections.emptyList());
            for(String comment : fieldComments) {
                writer.append(spacesString);
                writer.append(comment);
                writer.newLine();
            }
            
            if(yamlConfig.isConfigurationSection(fullKey)) {
                writer.append(spacesString);
                writer.append(key).append(":");
                writer.newLine();
            } else if(yamlConfig.isList(fullKey)) {
                List<String> valueAux = (List<String>) value;
                writer.append(spacesString);
                
                if(valueAux.isEmpty()) {
                    writer.append(key).append(": []");
                } else {
                    writer.append(key).append(":");
                    for(String elem : valueAux) {
                        elem = elem.replace("\"", "\\\"");
                        writer.newLine();
                        writer.append(spacesString);
                        writer.append("- \"").append(elem).append("\"");
                    }
                }
                writer.newLine();
            } else if(value != null) {
                // String, int, double, boolean, etc
                writer.append(spacesString);
                if(value instanceof String) {
                    String valueAux = value.toString().replace("\"", "\\\"");
                    writer.append(key).append(": \"").append(valueAux).append("\"");
                } else {
                    writer.append(key).append(": ").append(value.toString());
                }
                writer.newLine();
            }
        }
    }
    
    private void updateConfigInternal(String resourcePath, File configFile, String... sectionsToIgnoreArray) throws IOException {
        List<String> sectionsToIgnore = Arrays.asList(sectionsToIgnoreArray);
        
        Config newConfig = new Config();
        newConfig.loadConfigFromResource(resourcePath);
                
        Set<String> configKeys1 = new HashSet<>(newConfig.yamlConfig.getKeys(true));
        Set<String> configKeys2 = new HashSet<>(this.yamlConfig.getKeys(true));
        for(String sectionToIgnore : sectionsToIgnore) {
            configKeys1.removeIf(section -> section.contains(sectionToIgnore));
            configKeys2.removeIf(section -> section.contains(sectionToIgnore));
            
            if(newConfig.yamlConfig.get(sectionToIgnore) != null) configKeys1.add(sectionToIgnore);
            if(this.yamlConfig.get(sectionToIgnore) != null) configKeys2.add(sectionToIgnore);
        }
        
        Set<String> intersection = new HashSet<String>(configKeys1);
        intersection.retainAll(configKeys2);
        
        if(intersection.size() == configKeys1.size() && intersection.size() == configKeys2.size()) {
            // Config is updated
            return;
        }
        
        // Copy sections from old config to new config
        for(String section : configKeys1) {
            if(this.yamlConfig.isConfigurationSection(section) && !sectionsToIgnore.contains(section)) {
                continue;
            }
            
            Object value = this.yamlConfig.get(section);  
            if(value != null) {
                newConfig.yamlConfig.set(section, value);
            }
        }
        
        // Save old config and new config
        File backupFile = new File(configFile.getPath() + BACKUP_EXTENSION);
        this.saveConfig(backupFile);
        newConfig.saveConfig(configFile);
        
        // Load new config
        this.loadConfig(configFile);
    }
    
    
    
    public boolean existsPath(String path) {
        return yamlConfig.get(path) != null;
    }
    private void assertPathExists(String path) {
        if(!existsPath(path)) {
            throw new IllegalArgumentException(String.format("Config path \"%s\" doesn't exist", path));
        }
    }
    
    public byte getByte(String path) {
        assertPathExists(path);
        return (byte) yamlConfig.getInt(path);
    }
    public short getShort(String path) {
        assertPathExists(path);
        return (short) yamlConfig.getInt(path);
    }
    public int getInt(String path) {
        assertPathExists(path);
        return yamlConfig.getInt(path);
    }
    public long getLong(String path) {
        assertPathExists(path);
        return yamlConfig.getLong(path);
    }
    public float getFloat(String path) {
        assertPathExists(path);
        return (float) yamlConfig.getDouble(path);
    }
    public double getDouble(String path) {
        assertPathExists(path);
        return yamlConfig.getDouble(path);
    }
    public boolean getBoolean(String path) {
        assertPathExists(path);
        return yamlConfig.getBoolean(path);
    }
    public String getString(String path) {
        assertPathExists(path);
        return yamlConfig.getString(path);
    }
    public List<String> getStringList(String path) {
        assertPathExists(path);
        return yamlConfig.getStringList(path);
    }
    public XMaterial getMaterial(String path) {
        String materialNameAndDurability = getString(path);
        return XMaterialUtils.parseXMaterial(materialNameAndDurability);
    }
    public Location getLocation(String path) {
        assertPathExists(path);
        return this.get(path, LocationSerializator.getInstance());
    }
    public ItemStackWrapper getItemStackWrapper(String path) {
        assertPathExists(path);
        return this.get(path, ItemStackWrapperSerializator.getInstance());
    }
    public <T extends Enum<T>> T getEnum(String path, Class<T> enumClass) {
        return T.valueOf(enumClass, getString(path));
    }
    public ConfigurationSection getConfigurationSection(String path) {
        assertPathExists(path);
        return yamlConfig.getConfigurationSection(path);
    }
    
    public byte getByte(String path, byte defaultValue) {
        return (byte) yamlConfig.getInt(path, defaultValue);
    }
    public short getShort(String path, short defaultValue) {
        return (short) yamlConfig.getInt(path, defaultValue);
    }
    public int getInt(String path, int defaultValue) {
        return yamlConfig.getInt(path, defaultValue);
    }
    public long getLong(String path, long defaultValue) {
        return yamlConfig.getLong(path, defaultValue);
    }
    public float getFloat(String path, float defaultValue) {
        return (float) yamlConfig.getDouble(path, defaultValue);
    }
    public double getDouble(String path, double defaultValue) {
        return yamlConfig.getDouble(path, defaultValue);
    }
    public boolean getBoolean(String path, boolean defaultValue) {
        return yamlConfig.getBoolean(path, defaultValue);
    }
    public String getString(String path, String defaultValue) {
        return yamlConfig.getString(path, defaultValue);
    }
    public List<String> getStringList(String path, List<String> defaultValue) {
        return existsPath(path) ? yamlConfig.getStringList(path) : defaultValue;
    }
    public XMaterial getMaterial(String path, XMaterial defaultValue) {
        return existsPath(path) ? this.getMaterial(path) : defaultValue;
    }
    public Location getLocation(String path, Location defaultValue) {
        return existsPath(path) ? this.getLocation(path) : defaultValue;
    }
    public ItemStackWrapper getItemStackWrapper(String path, ItemStackWrapper defaultValue) {
        return existsPath(path) ? this.getItemStackWrapper(path) : defaultValue;
    }
    public <T extends Enum<T>> T getEnum(String path, Class<T> enumClass, T defaultValue) {
        return existsPath(path) ? getEnum(path, enumClass) : defaultValue;
    }
    public ConfigurationSection getConfigurationSection(String path, ConfigurationSection defaultValue) {
        return existsPath(path) ? getConfigurationSection(path) : defaultValue;
    }
    public List<String> getComments(String path) {
        return comments.getOrDefault(path, new ArrayList<>());
    }
    
    public <T> T get(String path, ConfigDeserializer<T> serializator) {
        assertPathExists(path);
        return serializator.deserialize(this, path);
    }
    
    public void setComments(String path, List<String> comments) {
        this.comments.put(path, comments);
    }
    public void setComments(String path, String... comments) {
        this.setComments(path, Arrays.asList(comments));
    }
    
    public <T> void set(String path, T value, ConfigSerializer<T> serializer) {
        yamlConfig.set(path, null);
        if(value != null) {
            serializer.serialize(this, path, value);
        }
    }
    public void set(String path, Object value) {
        yamlConfig.set(path, null);
        if(value == null) {
            return;
        }
        
        if(value instanceof ConfigSerializer) {
            this.set(path, value, (ConfigSerializer) value);
        } else if(value instanceof Location) {
            this.set(path, (Location) value, LocationSerializator.getInstance());
        } else if(value instanceof ItemStackWrapper) {
            this.set(path, (ItemStackWrapper) value, ItemStackWrapperSerializator.getInstance());
        } else if(value instanceof Enum) {
            this.set(path, ((Enum) value).name());
        } else {
            yamlConfig.set(path, value);
        }
    }
    
    @Override
    public String toString() {
        return this.yamlConfig.saveToString();
    }
    
    
    
    private static byte[] inputStreamToByteArray(File file) throws IOException {
        FileInputStream input = new FileInputStream(file);
        return inputStreamToByteArray(input);
    }
    private static byte[] inputStreamToByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while((bytesRead = input.read(buffer)) > 0) {
            baos.write(buffer, 0, bytesRead);
        }
        baos.flush();
        try {
            input.close();
        } catch(IOException ex) { }
        
        return baos.toByteArray();
    }
    
    private static BufferedReader getReader(byte[] bytes) throws IOException {
        return new BufferedReader(new InputStreamReader(new ByteArrayInputStream(bytes), CONFIG_CHARSET));
    }
    private static BufferedWriter getWriter(File file) throws IOException {
        return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), CONFIG_CHARSET));
    }
    
    private static boolean isComment(@NonNull String line) {
        String trimmedLine = line.trim();
        return trimmedLine.length() == 0 || trimmedLine.charAt(0) == '#';
    }
    
    private static int getIdentationSpaces(@NonNull String line) {
        int spaces = 0;
        for(int i=0; i<line.length(); i++) {
            if(line.charAt(i) == ' ') {
                spaces++;
            } else {
                break;
            }
        }
        return spaces;
    }
    
    private static String getSpacesString(int spaces) {
        StringBuilder builder = new StringBuilder();
        for(int i=0; i<spaces; i++) {
            builder.append(' ');        
        }
        return builder.toString();
    }
}
