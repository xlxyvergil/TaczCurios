// kubejs/server_scripts/boss_loot_fix.js
// 修复Boss在使用 sethealth 或某些武器/饰品效果导致死亡时无法正常掉落的问题

var bossTracker = {};
var processedBosses = {};
var tickCounter = 0;
var CHECK_INTERVAL = 100;

LevelEvents.tick(function(event) {
    tickCounter++;
    if (tickCounter < CHECK_INTERVAL) return;
    tickCounter = 0;

    try {
        var server = event.server;
        if (!server) return;

        var levels = [];
        try {
            var iter = server.getAllLevels().iterator();
            while (iter.hasNext()) levels.push(iter.next());
        } catch(e) { levels.push(server.overworld()); }

        var aliveIds = {};

        for (var li = 0; li < levels.length; li++) {
            var level = levels[li];
            var entities = level.getEntities();
            for (var i = 0; i < entities.size(); i++) {
                var entity = entities.get(i);
                var entityType = entity.getType();

                if (entityType === 'minecraft:player') continue;

                var id = entity.id;
                aliveIds[id] = true;

                if (processedBosses[id]) continue;

                var bp = entity.blockPosition();
                if (!bp) continue;

                var px = bp.getX();
                var py = bp.getY();
                var pz = bp.getZ();
                if (isNaN(px) || isNaN(py) || isNaN(pz)) continue;

                var h = entity.getHealth();
                var mh = entity.getMaxHealth();

                if (!bossTracker[id]) {
                    var lt = entity.getLootTable();
                    // level.getDimension() 是 KubeJS API，直接返回 ResourceLocation
                    var dim = level.getDimension().toString();
                    bossTracker[id] = {
                        type: entityType, x: px, y: py, z: pz,
                        health: h, maxHealth: mh,
                        lootTable: lt, dimension: dim
                    };
                    console.log("[Boss掉落修复] 新Boss: " + entityType + " ID:" + id + " 血量:" + Math.floor(h) + "/" + Math.floor(mh) + " 维度:" + dim);
                } else {
                    bossTracker[id].x = px;
                    bossTracker[id].y = py;
                    bossTracker[id].z = pz;
                    bossTracker[id].health = h;
                    bossTracker[id].maxHealth = mh;
                }
            }
        }

        for (var id in bossTracker) {
            if (processedBosses[id]) continue;
            if (!aliveIds[id]) {
                var d = bossTracker[id];
                var healthRatio = d.health / d.maxHealth;

                console.log("[Boss掉落修复] " + d.type + " ID:" + id + " 消失 血量:" + Math.floor(d.health) + "/" + Math.floor(d.maxHealth) + " (" + Math.floor(healthRatio * 100) + "%)");

                if (healthRatio > 0.5) {
                    console.log("[Boss掉落修复] 血量过高，跳过");
                    delete bossTracker[id];
                    continue;
                }

                // server.runCommandSilent = 服务器源头执行，不经过网络层，最安全
                var cmd = "execute in " + d.dimension + " run loot spawn " + d.x + " " + d.y + " " + d.z + " loot " + d.lootTable.toString();
                console.log("[Boss掉落修复] " + d.dimension + " " + d.x + "," + d.y + "," + d.z + " " + d.lootTable.toString());
                server.runCommandSilent(cmd);
                console.log("[Boss掉落修复] 完成");

                processedBosses[id] = true;
                delete bossTracker[id];
            }
        }
    } catch(ex) {}
});