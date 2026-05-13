package com.g2806.MysticRocket;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.Arrays;

public class MysticFireworkPlugin extends JavaPlugin implements Listener {

    // Vanilla-like boost values for Levels 1-3, enhanced for Level 4
    private static final double[] BOOST_VALUES = {0.5, 0.7, 1.0, 1.3}; // Level 1, 2, 3, 4
    private NamespacedKey[] recipeKeys;

    @Override
    public void onEnable() {
        // Inicializar las claves de las recetas
        recipeKeys = new NamespacedKey[] {
                new NamespacedKey(this, "mystic_rocket_1"),
                new NamespacedKey(this, "mystic_rocket_2"),
                new NamespacedKey(this, "mystic_rocket_3"),
                new NamespacedKey(this, "mystic_rocket_4")
        };

        // Registrar eventos y recetas
        getServer().getPluginManager().registerEvents(this, this);
        // registerRecipes();
        getLogger().info("MysticFireworkPlugin enabled!");
    }

    private void registerRecipes() {
        // Nivel 1: Iron Ingot
        ItemStack mysticRocket1 = createMysticRocket(1);
        ShapedRecipe recipe1 = new ShapedRecipe(recipeKeys[0], mysticRocket1);
        recipe1.shape(" T ", " F ", " I ");
        recipe1.setIngredient('T', Material.TOTEM_OF_UNDYING);
        recipe1.setIngredient('F', Material.FIREWORK_ROCKET);
        recipe1.setIngredient('I', Material.IRON_INGOT);
        getServer().addRecipe(recipe1);

        // Nivel 2: Diamond
        ItemStack mysticRocket2 = createMysticRocket(2);
        ShapedRecipe recipe2 = new ShapedRecipe(recipeKeys[1], mysticRocket2);
        recipe2.shape(" T ", " F ", " D ");
        recipe2.setIngredient('T', Material.TOTEM_OF_UNDYING);
        recipe2.setIngredient('F', Material.FIREWORK_ROCKET);
        recipe2.setIngredient('D', Material.DIAMOND);
        getServer().addRecipe(recipe2);

        // Nivel 3: Emerald
        ItemStack mysticRocket3 = createMysticRocket(3);
        ShapedRecipe recipe3 = new ShapedRecipe(recipeKeys[2], mysticRocket3);
        recipe3.shape(" T ", " F ", " E ");
        recipe3.setIngredient('T', Material.TOTEM_OF_UNDYING);
        recipe3.setIngredient('F', Material.FIREWORK_ROCKET);
        recipe3.setIngredient('E', Material.EMERALD);
        getServer().addRecipe(recipe3);

        // Nivel 4: Netherite Ingot
        ItemStack mysticRocket4 = createMysticRocket(4);
        ShapedRecipe recipe4 = new ShapedRecipe(recipeKeys[3], mysticRocket4);
        recipe4.shape(" T ", " F ", " N ");
        recipe4.setIngredient('T', Material.TOTEM_OF_UNDYING);
        recipe4.setIngredient('F', Material.FIREWORK_ROCKET);
        recipe4.setIngredient('N', Material.NETHERITE_INGOT);
        getServer().addRecipe(recipe4);

        // Log para verificar registro
        getLogger().info("Registered Mystic Rocket recipes: Level 1, 2, 3, 4");
    }

    private ItemStack createMysticRocket(int level) {
        ItemStack rocket = new ItemStack(Material.FIREWORK_ROCKET, 1);
        ItemMeta meta = rocket.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Mystic Rocket (Level " + level + ")");
        meta.setCustomModelData(1000 + level); // Identificador único
        meta.setLore(Arrays.asList(
                ChatColor.GRAY + "Infinite Elytra Boost!",
                ChatColor.GRAY + "Level: " + ChatColor.YELLOW + level // Resaltar nivel
        ));
        // Agregar efecto de encantamiento (brillo)
        meta.addEnchant(Enchantment.INFINITY, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS); // Ocultar detalles del encantamiento
        rocket.setItemMeta(meta);
        return rocket;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item != null && item.getType() == Material.FIREWORK_ROCKET && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta.hasCustomModelData() && meta.getCustomModelData() >= 1001 && meta.getCustomModelData() <= 1004) {
                event.setCancelled(true); // Prevenir consumo
                if (player.isGliding()) {
                    int level = meta.getCustomModelData() - 1001; // 0-based index for array
                    double boost = BOOST_VALUES[level];
                    // Aplicar impulso
                    Vector direction = player.getLocation().getDirection();
                    player.setVelocity(player.getVelocity().add(direction.multiply(boost)));
                    // Efecto de sonido
                    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1.0f, 1.0f);
                    // Efecto de partículas (imitar vainilla)
                    player.getWorld().spawnParticle(
                            org.bukkit.Particle.FIREWORK,
                            player.getLocation(),
                            20, // Cantidad
                            0.2, 0.2, 0.2, // Dispersión
                            0.1 // Velocidad
                    );
                }
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Desbloquear todas las recetas de Mystic Rocket para el jugador
        Player player = event.getPlayer();
        for (NamespacedKey key : recipeKeys) {
            player.discoverRecipe(key);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("mysticrocket") && sender instanceof Player) {
            Player player = (Player) sender;
            if (!player.hasPermission("mysticrocket.give")) {
                player.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
                return true;
            }
            if (args.length == 1 && args[0].matches("[1-4]")) {
                int level = Integer.parseInt(args[0]);
                ItemStack rocket = createMysticRocket(level);
                player.getInventory().addItem(rocket);
                player.sendMessage(ChatColor.GREEN + "Received Mystic Rocket Level " + level + "!");
                return true;
            }
            player.sendMessage(ChatColor.RED + "Usage: /mysticrocket <1|2|3|4>");
            return true;
        }
        return false;
    }
}
