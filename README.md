# L2jBlueHeart
This is a project based on `L2jSunrise - 10/2017`.

# Run in Intellij IDEA
1. Build files first and extract the data pack to the build of core.
1. Run

# Voice Commands
Need check all commands available.

#### Without Config
```
.ping
...
```
#### With Config &  non-checked commands
```
.ccp
.bank
.withdraw
.deposit
.exp
.expon
.expoff
.ccp
.nobuff
.changeexp
.enchantanime
.hidestores
.blockshotsanime
.shotsonenter
.tradeprot
.bank
.withdraw
.deposit
.giran
.dion
.aden
.goddard
.gludio
.rune
.heine
.schuttgart
.oren
.repair
.startrepair
.dressme
.undressme
.dressinfo
.showdress
.hidedress
.dressme-armor
.dress-armor
.dress-armorpage
.undressme-armor
.dressme-cloak
.dress-cloak
.dress-cloakpage
.undressme-cloak
.dressme-shield
.dress-shield
.dress-shieldpage
.undressme-shield
.dressme-weapon
.dress-weapon
.dress-weaponpage
.undressme-weapon
.dressme-hat
.dress-hat
.dress-hatpage
.undressme-hat
----------------| non checked
.aioitem
.antibot
.captcha
.farmcaptcha
.enchantcaptcha
.enchantbot
.opendoors
.closedoors
.ridewyvern
.changepassword
.banchat
.unbanchat
.debug
.evenly
.hellbound
.buffer
.lang
.online
.premium
.return
.enter
.zone
.set name
.set home
.set group
.divorce
.engage
.gotolove
```
#### Active Commands by Default (build)
```
.exp
.expon
.expoff
.ping
.repair
.startrepair 'charName'
```

#### BlueHeart 💙
Made with love.

# Bugs
```

```

# Todo
```
- Organizar as skills do aio ao serem carregadas
- Testar o sql da soure para checar a Lady e a Kiara (job shop)
```

# Dev Info
* Ao adicionar um novo item no item-mall, é necessário adicionar ele no `ProductName-e.dat` da system do jogo.

# Game Info
```
// Nevit Points For Level
// getActiveChar().getNevitSystem().addPoints(1950); // Default barion
// getActiveChar().getNevitSystem().addPoints(720); // Active after 10 levels
getActiveChar().getNevitSystem().addPoints(360); // Active after 20 levels
```

# Developer
`vert - [Daniel Barion]`