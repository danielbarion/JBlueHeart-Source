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
--------------------------------------| non checked
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
...
```
#### Active Commands by Default (build)
```
.exp
.expon
.expoff
.repair
.ping
```

# Bugs
```
- Tempo dos buffs contando (mostrando) os segundos ao invés de contar os minutos no game
- Ao encerrar o game server incorretamente, o char nasce sem buffs
```

# Dev Info
* Ao adicionar um novo item no item-mall, é necessário adicionar ele no `ProductName-e.dat` da system do jogo.

# Developer
`vert - [Daniel Barion]`