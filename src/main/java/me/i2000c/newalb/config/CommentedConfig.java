package me.i2000c.newalb.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.configuration.InvalidConfigurationException;

public class CommentedConfig extends YamlConfigurationUTF8{
    private static final Pattern COMMENT_PATTERN = Pattern.compile("[ \t\r]*(#.*)?");
    private static final Pattern CONFIG_KEY_PATTERN = Pattern.compile("( *)([A-Za-z0-9_\\-]+):.*");
    private final LinkedList<CommentKey> comments;
    
    private final Map<String, Integer> configLines;
    private final LinkedList<ConfigKey> configKeys;
    
    public CommentedConfig(){
        this.comments = new LinkedList<>();
        
        this.configLines = new HashMap<>();
        this.configKeys = new LinkedList<>();
    }
    
    @Override
    public void load(Reader reader) throws IOException, InvalidConfigurationException{
        //<editor-fold defaultstate="collapsed" desc="Code">
        try(BufferedReader bufferedReader = new BufferedReader(reader)){
            comments.clear();
            configLines.clear();
            configKeys.clear();
            StringBuilder configStringBuilder = new StringBuilder();
            int lineNumber = 0;
            String line;
            while((line = bufferedReader.readLine()) != null){
                Matcher matcher = COMMENT_PATTERN.matcher(line);
                if(matcher.matches()){
                    // The line is a comment
                    comments.add(new CommentKey(line, lineNumber));
                }else{
                    // The line is not a comment
                    configStringBuilder.append(line).append('\n');
                    processConfigLine(line, lineNumber);
                }
                lineNumber++;
            }
            super.loadFromString(configStringBuilder.toString());
        }
//</editor-fold>
    }
    
    @Override
    public String saveToString(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        StringBuilder configStringBuilder = new StringBuilder();
        
        String configString = super.saveToString();
        String[] splittedConfigLines = configString.split("\n");
        Iterator<String> configLinesIterator = Arrays.asList(splittedConfigLines).iterator();
        Iterator<CommentKey> commentLinesIterator = comments.iterator();
        
        int lineNumber = 0;
        CommentKey commentKey = null;
        while(configLinesIterator.hasNext() || commentLinesIterator.hasNext()){
            if(commentKey == null && commentLinesIterator.hasNext()){
                commentKey = commentLinesIterator.next();                
            }
            
            if(commentKey != null && commentKey.lineNumber == lineNumber){
                configStringBuilder.append(commentKey.comment);
                commentKey = null;
            }else if(configLinesIterator.hasNext()){
                String configLine = configLinesIterator.next();
                if(!configLine.contains("#")){
                    configStringBuilder.append(configLine);
                }
            }
            
            configStringBuilder.append('\n');
            ++lineNumber;
        }
        
        return configStringBuilder.toString();
//</editor-fold>
    }
    
    void updateCommentLines(String configPath, int offset){
        //<editor-fold defaultstate="collapsed" desc="Code">
        int lineNumber = configLines.get(configPath);
        comments.stream()
                .filter(commentKey -> commentKey.lineNumber > lineNumber)
                .forEach(commentKey -> commentKey.lineNumber += offset);
//</editor-fold>
    }
    
    private void processConfigLine(String line, int lineNumber){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Matcher matcher = CONFIG_KEY_PATTERN.matcher(line);
        if(matcher.matches()){
            int spaces = matcher.group(1).length();
            String key = matcher.group(2);
            ConfigKey configKey = new ConfigKey(spaces, key);
            
            while(!configKeys.isEmpty()){
                int lastKeySpaces = configKeys.getLast().spaces;
                if(lastKeySpaces >= spaces){
                    configKeys.removeLast();
                }else{
                    break;
                }
            }
            configKeys.addLast(configKey);
            
            StringBuilder builder = new StringBuilder();
            configKeys.forEach(confKey -> builder.append(confKey.key).append('.'));
            if(builder.length() > 0){
                // Remove last '.'
                builder.setLength(builder.length() - 1);
            }
            
            configLines.put(builder.toString(), lineNumber);
        }
//</editor-fold>
    }
    
    private static class ConfigKey{
        //<editor-fold defaultstate="collapsed" desc="Code">
        public int spaces;
        public String key;
        
        public ConfigKey(int spaces, String key){
            this.spaces = spaces;
            this.key = key;
        }
//</editor-fold>
    }
    
    private static class CommentKey{
        //<editor-fold defaultstate="collapsed" desc="Code">
        public String comment;
        public int lineNumber;
        
        public CommentKey(String comment, int lineNumber){
            this.comment = comment;
            this.lineNumber = lineNumber;
        }
//</editor-fold>
    }
}
