package me.i2000c.newalb.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.i2000c.newalb.config.YamlConfigurationUTF8;
import org.bukkit.configuration.InvalidConfigurationException;

public class CommentedConfig extends YamlConfigurationUTF8{
    private static final Pattern COMMENT_PATTERN = Pattern.compile("[ \t\r]*(#.*)?");
    private final Map<Integer, String> comments;
    
    public CommentedConfig(){
        this.comments = new HashMap<>();
    }
    
    @Override
    public void load(Reader reader) throws IOException, InvalidConfigurationException{
        try(BufferedReader bufferedReader = new BufferedReader(reader)){
            comments.clear();
            StringBuilder configStringBuilder = new StringBuilder();
            int lineNumber = 0;
            String line;
            while((line = bufferedReader.readLine()) != null){
                Matcher matcher = COMMENT_PATTERN.matcher(line);
                if(matcher.matches()){
                    // The line is a comment
                    comments.put(lineNumber, line);
                }else{
                    // The line is not a comment
                    configStringBuilder.append(line).append('\n');
                }
                lineNumber++;
            }
            super.loadFromString(configStringBuilder.toString());
        }
    }
    
    @Override
    public String saveToString(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        StringBuilder configStringBuilder = new StringBuilder();
        
        String configString = super.saveToString();
        String[] splittedConfigLines = configString.split("\n");
        Iterator<String> configLinesIterator = Arrays.asList(splittedConfigLines).iterator();
        int lines = splittedConfigLines.length + comments.size();            
        for(int lineNumber = 0; lineNumber < lines; lineNumber++){
            String commentLine = comments.get(lineNumber);
            if(commentLine != null){
                configStringBuilder.append(commentLine);
            }else if(configLinesIterator.hasNext()){
                String configLine = configLinesIterator.next();
                if(!configLine.contains("#")){
                    configStringBuilder.append(configLine);
                }                
            }
            configStringBuilder.append('\n');
        }
        
        return configStringBuilder.toString();
//</editor-fold>
    }
}
