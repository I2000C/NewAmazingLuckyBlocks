package me.i2000c.newalb.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import me.i2000c.newalb.config.YamlConfigurationUTF8;
import org.bukkit.Color;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class CustomConfig<T extends Enum & CustomConfig.CustomConfigKey>{
    private static final String VERSION_FILENAME = "versions.properties";
    
    private final JavaPlugin plugin;
    private final File file;
    private final String resourceName;
    private FileConfiguration config;
    
    public CustomConfig(JavaPlugin plugin, String filename, String resourceName){
        this.plugin = plugin;
        this.file = new File(filename);
        this.resourceName = resourceName;
    }
    
    public CustomConfig(JavaPlugin plugin, String filename){
        this(plugin, filename, filename);
    }
    
    public File getFile(){
        return this.file;
    }
    
    public FileConfiguration getBukkitConfig(){
        return this.config;
    }
    
    private void copyConfigFile(){
        try(InputStream input = plugin.getResource(resourceName)){
            Files.copy(input, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }catch(IOException ex){
            System.err.printf("An error occurred while copying resource %s:\n", resourceName);
            ex.printStackTrace();
        }        
    }
    
    private void updateConfigFile(){
        config = new YamlConfigurationUTF8();
        FileConfiguration oldConfig = YamlConfigurationUTF8.loadConfiguration(file);
        
        for(String key : config.getKeys(true)){
            if(config.isConfigurationSection(key)){
                continue;
            }
            
            Object object = oldConfig.get(key);
            if(object == null || oldConfig.isConfigurationSection(key)){
                continue;
            }
            
            config.set(key, object);
        }
        
        File backupFile = new File(file.getParentFile(), file.getName() + ".bak");
        file.renameTo(backupFile);
        save();
    }
    
    private void a(String versionKey, int currentVersion){
        File propertiesFile = new File(plugin.getDataFolder(), VERSION_FILENAME);
        Properties properties = new Properties();
        if(propertiesFile.exists()){
            try(FileInputStream input = new FileInputStream(propertiesFile)){
                properties.load(input);
            }catch(IOException ex){
                System.err.printf("An error occurred while loading %s:\n", propertiesFile.getName());
                ex.printStackTrace();
            }
        }else{
            try{
                propertiesFile.createNewFile();
            }catch(IOException ex){
                System.err.printf("An error occurred while creating %s:\n", propertiesFile.getName());
                ex.printStackTrace();
            }            
        }
        
        int savedVersion = Integer.parseInt(properties.getProperty(versionKey, "-1"));
        if(savedVersion == -1){
            copyConfigFile();
        }else if(currentVersion > savedVersion){
            updateConfigFile();            
        }else{
            return;
        }
        
        properties.setProperty(versionKey, currentVersion + "");
        try(FileOutputStream output = new FileOutputStream(propertiesFile)){
            properties.store(output, null);
        }catch(IOException ex){
            System.err.printf("An error occurred while writing %s:\n", propertiesFile.getName());
            ex.printStackTrace();
        }
    }
    
    
    //<editor-fold defaultstate="collapsed" desc="Delegated methods">
    public void save(){
        try{
            config.save(file);
        }catch(IOException ex){
            System.err.printf("An error occurred while saving file %s:\n", file.getName());
            ex.printStackTrace();
        }        
    }
    
    public void load(){
        try{
            config = YamlConfigurationUTF8.loadConfiguration(file);
        }catch(Exception ex){
            System.err.printf("An error occurred while loading file %s:\n", file.getName());
            ex.printStackTrace();
        }
    }
    
    public ConfigurationSection getParent() {
        return config.getParent();
    }
    
    public Set<String> getKeys(boolean deep) {
        return config.getKeys(deep);
    }
    
    public Map<String, Object> getValues(boolean deep) {
        return config.getValues(deep);
    }
    
    public boolean contains(T path) {
        return config.contains(path.getValue());
    }
    public boolean contains(T path, boolean ignoreDefault) {
        return config.contains(path.getValue(), ignoreDefault);
    }
    
    public boolean isSet(T path) {
        return config.isSet(path.getValue());
    }
    
    public String getCurrentPath() {
        return config.getCurrentPath();
    }
    
    public String getName() {
        return config.getName();
    }
    
    public Configuration getRoot() {
        return config.getRoot();
    }
    
    public ConfigurationSection getDefaultSection() {
        return config.getDefaultSection();
    }
    
    public void set(T path, Object value) {
        config.set(path.getValue(), value);
    }
    
    public Object get(T path) {
        return config.get(path.getValue());
    }
    
    public Object get(T path, Object def) {
        return config.get(path.getValue(), def);
    }
    
    public ConfigurationSection createSection(T path) {
        return config.createSection(path.getValue());
    }
    
    public ConfigurationSection createSection(T path, Map<?, ?> map) {
        return config.createSection(path.getValue(), map);
    }
    
    public String getString(T path) {
        return config.getString(path.getValue());
    }
    
    public String getString(T path, String def) {
        return config.getString(path.getValue(), def);
    }
    
    public boolean isString(T path) {
        return config.isString(path.getValue());
    }
    
    public int getInt(T path) {
        return config.getInt(path.getValue());
    }
    
    public int getInt(T path, int def) {
        return config.getInt(path.getValue(), def);
    }
    
    public boolean isInt(T path) {
        return config.isInt(path.getValue());
    }
    
    public boolean getBoolean(T path) {
        return config.getBoolean(path.getValue());
    }
    
    public boolean getBoolean(T path, boolean def) {
        return config.getBoolean(path.getValue(), def);
    }
    
    public boolean isBoolean(T path) {
        return config.isBoolean(path.getValue());
    }
    
    public double getDouble(T path) {
        return config.getDouble(path.getValue());
    }
    
    public double getDouble(T path, double def) {
        return config.getDouble(path.getValue(), def);
    }
    
    public boolean isDouble(T path) {
        return config.isDouble(path.getValue());
    }
    
    public long getLong(T path) {
        return config.getLong(path.getValue());
    }
    
    public long getLong(T path, long def) {
        return config.getLong(path.getValue(), def);
    }
    
    public boolean isLong(T path) {
        return config.isLong(path.getValue());
    }
    
    public List<?> getList(T path) {
        return config.getList(path.getValue());
    }
    
    public List<?> getList(T path, List<?> def) {
        return config.getList(path.getValue(), def);
    }
    
    public boolean isList(T path) {
        return config.isList(path.getValue());
    }
    
    public List<String> getStringList(T path) {
        return config.getStringList(path.getValue());
    }
    
    public List<Integer> getIntegerList(T path) {
        return config.getIntegerList(path.getValue());
    }
    
    public List<Boolean> getBooleanList(T path) {
        return config.getBooleanList(path.getValue());
    }
    
    public List<Double> getDoubleList(T path) {
        return config.getDoubleList(path.getValue());
    }
    
    public List<Float> getFloatList(T path) {
        return config.getFloatList(path.getValue());
    }
    
    public List<Long> getLongList(T path) {
        return config.getLongList(path.getValue());
    }
    
    public List<Byte> getByteList(T path) {
        return config.getByteList(path.getValue());
    }
    
    public List<Character> getCharacterList(T path) {
        return config.getCharacterList(path.getValue());
    }
    
    public List<Short> getShortList(T path) {
        return config.getShortList(path.getValue());
    }
    
    public List<Map<?, ?>> getMapList(T path) {
        return config.getMapList(path.getValue());
    }
    
    public <U extends ConfigurationSerializable> U getSerializable(T path, Class<U> clazz) {
        return config.getSerializable(path.getValue(), clazz);
    }
    
    public <U extends ConfigurationSerializable> U getSerializable(T path, Class<U> clazz, U def) {
        return config.getSerializable(path.getValue(), clazz, def);
    }
    
    public Vector getVector(T path) {
        return config.getVector(path.getValue());
    }
    
    public Vector getVector(T path, Vector def) {
        return config.getVector(path.getValue(), def);
    }
    
    public boolean isVector(T path) {
        return config.isVector(path.getValue());
    }
    
    public OfflinePlayer getOfflinePlayer(T path) {
        return config.getOfflinePlayer(path.getValue());
    }
    
    public OfflinePlayer getOfflinePlayer(T path, OfflinePlayer def) {
        return config.getOfflinePlayer(path.getValue(), def);
    }
    
    public boolean isOfflinePlayer(T path) {
        return config.isOfflinePlayer(path.getValue());
    }
    
    public ItemStack getItemStack(T path) {
        return config.getItemStack(path.getValue());
    }
    
    public ItemStack getItemStack(T path, ItemStack def) {
        return config.getItemStack(path.getValue(), def);
    }
    
    public boolean isItemStack(T path) {
        return config.isItemStack(path.getValue());
    }
    
    public Color getColor(T path) {
        return config.getColor(path.getValue());
    }
    
    public Color getColor(T path, Color def) {
        return config.getColor(path.getValue(), def);
    }
    
    public boolean isColor(T path) {
        return config.isColor(path.getValue());
    }
    
    public ConfigurationSection getConfigurationSection(T path) {
        return config.getConfigurationSection(path.getValue());
    }
    
    public boolean isConfigurationSection(T path) {
        return config.isConfigurationSection(path.getValue());
    }
    
    public static String createPath(ConfigurationSection section, String key) {
        return MemorySection.createPath(section, key);
    }
    
    public static String createPath(ConfigurationSection section, String key, ConfigurationSection relativeTo) {
        return MemorySection.createPath(section, key, relativeTo);
    }
    
    @Override
    public String toString() {
        return config.toString();
    }
//</editor-fold>
        
    public static interface CustomConfigKey{
        public String getValue();
    }
}
