# Trade System
Trade items and coins with other players in a safe way!

## Server requirements (optional*):
- spigot 1.20.1
- java 1.17
- _Coin system*_
- _MySQl*_

## Installation (plug and play):
1. Put the .jar file into your plugins folder
2. Run your server -> **done**

## Coins interface:
```
interface:
  enabled: false
  type: 'mysql'
  mysql:
    url: 'localhost'
    port: 3306
    database: 'test'
    user: 'root'
    password: ''
    table:
      name: 'CoinSystem'
      columns:
        uuid: 'PlayerUUID'
        coins: 'Coins'
  config:
    path: 'plugins/CoinSystem'
    file: 'database.yml'
    item: 'Coins.%uuid%'
  essentials:
    path: 'plugins/Essentials/userdata'
    file: '%uuid%.yml'
    item: 'money'
    command:
      give: 'eco give %uuid% %coins%'
      take: 'eco take %uuid% %coins%'
```
You must already have a coin system to be able to use the coin trade function. The player's coins 
must be stored either in a MySQl database or in a YAML file. Then it is possible to access the 
coins with a customisable interface.

**MySQl** <br/>
- `table.name` The table name where the coins are stored
- `table.columns.uuid` The column name of player uuid
- `table.columns.coins` The column name of coins

**Config:** <br/>
- `path` The path to the coin database file
- `file` The name of the file
- `item` The YAML item path

**Essentials:** <br/>
Can also be used if other plugins use player specific configuration files. It requires a command to give and take coins.
- `path` The path to userdata
- `file` The name of player data file
- `item` The YAML item path
- `command.give` The give coins command
- `command.take` The take coins command

## Functions:
- Trade items with other players
- Trade coins with other players
- GUI trading

## Permission:
- tradesystem.use

## Download:
[https://www.spigotmc.org/resources/trade-system-1-20-x-spigot.115765/](https://www.spigotmc.org/resources/trade-system-1-20-x-spigot.115765/)
