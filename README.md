<div align="center">
  <img src="src/main/resources/icon.png" alt="CBC: Security Breach Icon" width="180" />

  <h1>Create Big Cannons Security Breach</h1>

  <p>
    <a href="https://modrinth.com/mod/create-big-cannons-security-breach">
      <img src="https://img.shields.io/modrinth/dt/create-big-cannons-security-breach?logo=modrinth&label=Downloads&style=flat&color=242629&labelColor=5CA424&logoColor=fff" alt="Modrinth Downloads" />
    </a>
    <img src="https://img.shields.io/badge/Minecraft-1.21.1-brightgreen?logo=minecraft" alt="Minecraft Version" />
    <img src="https://img.shields.io/badge/Loader-NeoForge-orange" alt="NeoForge" />
  </p>

  <p>A bridge mod that lets Create Big Cannons projectiles damage Security Craft reinforced blocks..</p>
</div>

## Requirements

- Minecraft 1.21.1
- NeoForge 21.1.234 or newer
- Create Big Cannons 5.11.7 or newer
- Security Craft 1.10.1 or newer

## What it does

Security Craft blocks are normally immune to explosions. This mod gives them a hit pool, so a block needs several hits before it breaks:

- Each block has HP based on its material group.
- Every explosion or direct projectile hit adds damage. A block shows the vanilla break-progress animation (10 stages) as it takes damage.
- When damage reaches its HP, the block breaks and drops.
- Damage slowly decreases over time when the damage decay option is enabled, so partially damaged blocks can recover.
- Block HP and accumulated damage are stored in the level save data and survive restarts.

Blocks are matched by material group using the block's registry name. Blocks that match no group use the fallback HP and resistance values. Non-Security Craft blocks are not touched.

### Damage sources

- Cannon explosions (shells, impact shells, mortar stones) that reach a Security Craft block.
- Direct projectile hits (solid shot, grapeshot) against a Security Craft block. Direct hit damage scales with projectile speed.

### Material groups

Blocks are grouped by name:

- glass, powder (sand, gravel, dirt), wool, wood
- stone, copper, iron, gold, diamond, obsidian, steel, netherite

## Configuration

The config file is created at `config/create_big_cannons_security_breach-server.toml` on first run. It has four sections:

- `general` - enable the mod, explosion damage multiplier, minimum explosion power, direct hit multiplier.
- `damage_decay` - crack healing interval (in ticks) and whether it is enabled.
- `material_hp` - HP for each material group and the fallback value.
- `material_resistance` - explosion resistance for each material group and the fallback value.

## License

Apache 2.0.
