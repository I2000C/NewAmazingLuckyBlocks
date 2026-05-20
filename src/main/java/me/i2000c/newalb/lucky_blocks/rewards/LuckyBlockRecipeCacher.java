package me.i2000c.newalb.lucky_blocks.rewards;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;

import com.cryptomorin.xseries.XMaterial;

import lombok.NonNull;
import me.i2000c.newalb.api.version.MinecraftVersion;
import me.i2000c.newalb.utils.reflection.ReflectionManager;

public class LuckyBlockRecipeCacher {
    
    private static final Map<String, ShapedRecipe> cachedRecipes = new HashMap<>();
    
    public static void removeAllRecipes() {
        List<ShapedRecipe> recipes = new ArrayList<>(cachedRecipes.values());
        removeRecipes(recipes);
        cachedRecipes.clear();
    }
    
    public static void refreshRecipes() {
        List<LuckyBlockType> types = TypeManager.getTypes();
        Map<String, ShapedRecipe> currentRecipes = new HashMap<>(cachedRecipes);
        cachedRecipes.clear();
        List<ShapedRecipe> oldRecipes = new ArrayList<>();
        List<ShapedRecipe> newRecipes = new ArrayList<>();
        
        types.forEach(type -> {
            String key = type.getNamespacedKey();
            ShapedRecipe recipe = type.getRecipe();
            if(currentRecipes.containsKey(key)) {
                ShapedRecipe currentRecipe = currentRecipes.get(key);
                if(!sameRecipes(currentRecipe, recipe)) {
                    oldRecipes.add(currentRecipe);
                    newRecipes.add(recipe);
                }
                currentRecipes.remove(key);
            } else {
                newRecipes.add(recipe);
            }
            cachedRecipes.put(key, recipe);
        });
        
        oldRecipes.addAll(currentRecipes.values());
        
        removeRecipes(oldRecipes);
        addRecipes(newRecipes);
    }
    
    private static void addRecipes(@NonNull List<ShapedRecipe> recipes) {
        if(recipes.isEmpty()) {
            return;
        }
        
        List<ShapedRecipe> recipesAlreadyAdded = new ArrayList<>();
        
        recipes.forEach(recipe -> {
            try {
                Bukkit.addRecipe(recipe);
            } catch(IllegalStateException ex) {
                recipesAlreadyAdded.add(recipe);
            }
        });
        
        if(!recipesAlreadyAdded.isEmpty()) {
            removeRecipes(recipesAlreadyAdded);
            recipesAlreadyAdded.forEach(Bukkit::addRecipe);
        }
    }
    
    private static void removeRecipes(@NonNull List<ShapedRecipe> recipes) {
        if(recipes.isEmpty()) {
            return;
        }
        
        if(MinecraftVersion.CURRENT_VERSION.isGreaterThanOrEqual(MinecraftVersion.v1_15)) {
            // In Minecraft 1.15 the method org.bukkit.Bukkit.removeRecipe(NamespacedKey key) was added.
            // Source: https://helpch.at/docs/1.15.2/org/bukkit/Bukkit.html
            recipes.forEach(recipe -> 
                ReflectionManager.callStaticMethod(Bukkit.class, "removeRecipe", recipe.getKey()));
        } else {
            Iterator<Recipe> iter = Bukkit.recipeIterator();
            if(MinecraftVersion.CURRENT_VERSION.isLessThan(MinecraftVersion.v1_12)) {
                Set<XMaterial> recipeResults = recipes.stream()
                                                      .map(ShapedRecipe::getResult)
                                                      .map(XMaterial::matchXMaterial)
                                                      .collect(Collectors.toSet());
                while(iter.hasNext()) {
                    Recipe recipe = iter.next();
                    if(recipe instanceof ShapedRecipe) {
                        ItemStack result = recipe.getResult();
                        XMaterial material = XMaterial.matchXMaterial(result);
                        if(recipeResults.contains(material)) {
                            iter.remove();
                            break;
                        }
                    }
                }
            } else {
                // Since Minecraft 1.12 recipe iterator is inmutable
                // https://www.spigotmc.org/threads/problem-remove-recipe.242988/#post-2461728
                Set<NamespacedKey> recipeKeys = recipes.stream()
                                                       .map(ShapedRecipe::getKey)
                                                       .collect(Collectors.toSet());
                List<Recipe> backup = new ArrayList<>();
                while(iter.hasNext()) {
                    Recipe recipe = iter.next();
                    if(recipe instanceof ShapedRecipe) {
                        ShapedRecipe shapedRecipe = (ShapedRecipe) recipe;
                        if(recipeKeys.contains(shapedRecipe.getKey())) {
                            break;
                        }
                    }
                    backup.add(recipe);
                }
                
                Bukkit.clearRecipes();
                backup.forEach(Bukkit::addRecipe);
            }
        }
    }
    
    private static boolean sameRecipes(ShapedRecipe recipe1, ShapedRecipe recipe2) {
        // 1. Compare shape
        String[] shape1 = recipe1.getShape();
        String[] shape2 = recipe2.getShape();
        
        if (shape1.length != shape2.length) {
            return false;
        }
        
        for (int i=0; i<shape1.length; i++) {
            if (!shape1[i].equals(shape2[i])) {
                return false;
            }
        }
        
        // 2. Compare ingredients
        Map<Character, ItemStack> ingredients1 = recipe1.getIngredientMap();
        Map<Character, ItemStack> ingredients2 = recipe2.getIngredientMap();
        if(ingredients1.size() != ingredients2.size()) {
            return false;
        }
        
        for(Character c : ingredients1.keySet()) {
            if(!ingredients2.containsKey(c)) {
                return false;
            }

            ItemStack ingredient1 = ingredients1.get(c);
            ItemStack ingredient2 = ingredients2.get(c);
            
            XMaterial material1 = ingredient1 != null ? XMaterial.matchXMaterial(ingredient1) : XMaterial.AIR;
            XMaterial material2 = ingredient2 != null ? XMaterial.matchXMaterial(ingredient2) : XMaterial.AIR;
            if(material1 != material2) {
                return false;
            }
        }
        
        return true;
    }
}
