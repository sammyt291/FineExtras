# FineExtras

A powerful and lightweight utility plugin for Minecraft 1.21+ servers. FineExtras provides essential player management commands with full support for **Spigot**, **Paper**, and **Folia** servers.

## Features

- 🚀 **Cross-platform Support**: Works on Spigot, Paper, and Folia servers
- 🎮 **Essential Commands**: Teleportation, inventory management, gamemode switching, and more
- ⚡ **Smart Command Registration**: Only registers direct commands if they're not already taken by another plugin
- 📱 **Interactive UI**: Clickable Accept/Deny buttons for teleport requests
- 🔒 **Permission-based**: Granular permission control for all commands
- ⏱️ **Async-safe**: All timers and schedulers are abstracted for Folia compatibility

## Commands

All commands are available through the main `/fineextras` (or `/fe`) command, as well as direct shortcuts.

### Main Command
| Command | Description |
|---------|-------------|
| `/fineextras` or `/fe` | Main plugin command |
| `/fe help` | Show the help menu |

### Teleportation Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/tp2p <player1> <player2>` | Teleport player1 to player2 | `fineextras.tp2p` |
| `/tpa <player>` | Request to teleport to a player | `fineextras.tpa` |
| `/tpr <player>` | Request a player to teleport to you | `fineextras.tpr` |
| `/fe tpaccept` | Accept a pending teleport request | - |
| `/fe tpdeny` | Deny a pending teleport request | - |

**TPA/TPR Features:**
- Clickable **[Accept]** and **[Deny]** buttons in chat
- Requests expire after 60 seconds
- Prevents duplicate requests

### Inventory Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/ci [invonly] [player]` | Clear inventory | `fineextras.clearinventory` |
| `/clearinventory [invonly] [player]` | Alias for /ci | `fineextras.clearinventory` |

**Options:**
- `invonly` - Only clears the main inventory (preserves armor and hotbar)
- `[player]` - Target player (requires `fineextras.clearinventory.others`)

### Gamemode Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/gmc [player]` | Set gamemode to Creative | `fineextras.gamemode.creative` |
| `/gms [player]` | Set gamemode to Survival | `fineextras.gamemode.survival` |
| `/gma [player]` | Set gamemode to Adventure | `fineextras.gamemode.adventure` |
| `/gmsp [player]` | Set gamemode to Spectator | `fineextras.gamemode.spectator` |

Changing another player's gamemode requires `fineextras.gamemode.others` permission.

### Movement Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/speed <0-10\|reset> [player]` | Set movement speed | `fineextras.speed` |
| `/gravity <player> <value\|reset>` | Set player gravity | `fineextras.gravity` |

**Speed Command:**
- Automatically detects what type of speed to modify based on player state:
  - **Flying**: Sets fly speed
  - **Swimming**: Sets swim speed
  - **Walking**: Sets walk speed
- Use `reset` to restore default speeds

**Gravity Command:**
- Set gravity value to `0` to disable gravity (player floats)
- Any other value enables normal gravity
- Use `reset` to restore normal gravity

## Permissions

| Permission | Description | Default |
|------------|-------------|---------|
| `fineextras.*` | Access to all commands | OP |
| `fineextras.tp2p` | Teleport players to each other | OP |
| `fineextras.clearinventory` | Clear your own inventory | OP |
| `fineextras.clearinventory.others` | Clear others' inventories | OP |
| `fineextras.gamemode.creative` | Switch to Creative mode | OP |
| `fineextras.gamemode.survival` | Switch to Survival mode | OP |
| `fineextras.gamemode.adventure` | Switch to Adventure mode | OP |
| `fineextras.gamemode.spectator` | Switch to Spectator mode | OP |
| `fineextras.gamemode.others` | Change others' gamemode | OP |
| `fineextras.tpa` | Send teleport requests | true |
| `fineextras.tpr` | Request others to teleport to you | true |
| `fineextras.speed` | Change your own speed | OP |
| `fineextras.speed.others` | Change others' speed | OP |
| `fineextras.gravity` | Change player gravity | OP |

## Installation

1. Download the latest `FineExtras.jar` from the releases page
2. Place the JAR file in your server's `plugins` folder
3. Restart your server
4. (Optional) Configure permissions using your permissions plugin

## Building from Source

### Requirements
- Java 21 or higher
- Gradle 8.12+ (or use the included wrapper)

### Build Commands

```bash
# Clone the repository
git clone https://github.com/sammyt291/FineExtras.git
cd FineExtras

# Build the plugin using Gradle wrapper
./gradlew shadowJar

# Or on Windows
gradlew.bat shadowJar

# The JAR file will be in build/libs/FineExtras-1.0.0.jar
```

The project uses the Shadow plugin to create a shaded JAR with all dependencies properly packaged.

## Compatibility

- **Minecraft Version**: 1.21+
- **Server Software**: 
  - ✅ Spigot
  - ✅ Paper
  - ✅ Folia
  - ✅ Other Spigot/Paper forks

## Smart Command Registration

FineExtras intelligently handles command conflicts with other plugins:

- Direct commands (like `/tpa`, `/gmc`, etc.) are only registered if no other plugin has claimed them
- The `/fineextras` (or `/fe`) command is always available and provides access to ALL features as subcommands
- If another plugin registers `/tpa`, you can still use `/fe tpa`

This means FineExtras works alongside popular plugins like EssentialsX without conflicts!

## Technical Details

### Folia Support

FineExtras includes a custom scheduler abstraction that automatically detects and uses the appropriate scheduler:
- **Spigot/Paper**: Uses BukkitScheduler
- **Folia**: Uses RegionizedScheduler for global tasks and EntityScheduler for entity-specific operations

This ensures all timed operations (like teleport request expiration) work correctly regardless of server software.

### API Version

This plugin targets API version 1.21 and uses modern Java features (Java 21).

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Support

If you encounter any issues or have feature requests, please open an issue on GitHub.

---

Made with ❤️ for the Minecraft community
