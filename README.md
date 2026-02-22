[![build](https://github.com/Mrbysco/RadialAggroIndicator/actions/workflows/build.yml/badge.svg)](https://github.com/Mrbysco/RadialAggroIndicator/actions/workflows/build.yml)
[![](http://cf.way2muchnoise.eu/versions/1469201.svg)](https://www.curseforge.com/minecraft/mc-mods/radial-aggro-indicator)

# Radial Aggro Indicator #

## About ##

The **Radial Aggro Indicator** mod displays an on-screen indicator showing the direction of mobs that have targeted the
player.
When targeted a red `^` will appear on the screen pointing in the direction of the mob. Multiple indicators can appear
at once if multiple mobs have targeted the player.

There are some entity tags available to customize the behavior of the indicator for specific mobs.  
The `radialaggro:unfading` tag can be added to mobs to make their indicators stay visible indefinitely instead of
disappearing after a certain duration.  
The `radialaggro:blacklist` tag can be added to mobs to prevent them from showing an indicator at all.  
If the `invertBlacklist` config option is enabled, then only mobs with the `radialaggro:blacklist` tag will show
indicators, effectively making it a whitelist instead.

The indicator can be customized through the config file, allowing you to change the color, size, symbol and more.
The following general config options are available:

- **initialAggro** - If true the indicator will be shown when a mob first targets the player (default: false)
- **indicatorDuration** - The duration in ticks that the aggro indicator will be shown for (default: 200, 10 seconds)
- **hideInView** - If true the indicator will be hidden when the entity is within the player's field of view (default: false)
- **invertBlacklist** - If true the blacklist will be inverted to a whitelist, meaning only entities in the list will show the indicator (default: false)

The following client config options are available:

- **fadeIn** - If true the indicator will fade in when it appears (default: true)
- **fadeOut** - If true the indicator will fade out when it disappears (default: true)
- **radiusScale** - The scale of the symbol used for the aggro indicator. Higher values will make the symbol larger (default: 2.0D)
- **symbol** - The symbol used for the aggro indicator. This can be any single character or a string of characters (default: "^")
- **symbolColorRed** - The red value of the symbol color rgb (default: 255)
- **symbolColorGreen** - The green value of the symbol color rgb (default: 0)
- **symbolColorBlue** - The blue value of the symbol color rgb (default: 0)
- **symbolRotationLock** - If true the symbol will not rotate to match the direction of the entity, it will always be upright (default: false)
- **symbolScale** - The scale of the symbol used for the aggro indicator. Higher values will make the symbol larger (default: 2.0D)

## License ##

* Radial Aggro Indicator is licensed under the MIT License
    - (c) 2026 Mrbysco
    - [![License](https://img.shields.io/badge/License-MIT-red.svg?style=flat)](http://opensource.org/licenses/MIT)

## Downloads ##

Downloads will be located on [CurseForge](https://www.curseforge.com/minecraft/mc-mods/radial-aggro-indicator)
