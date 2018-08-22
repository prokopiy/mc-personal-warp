package com.github.prokopiy.mcpersonalwarp.data;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

import java.util.List;

public class PlayerData {
    protected String name;
    protected String uuid;
    protected Integer warpLimit;
    protected List<String> warps;


    public PlayerData(String name, String uuid, Integer warpLimit) {
        this.name = name;
        this.uuid = uuid;
        this.warpLimit = warpLimit;
    }


    public static class PlayerDataSerializer implements TypeSerializer<PlayerData> {
        @SuppressWarnings("serial")
        final public static TypeToken<List<PlayerData>> token = new TypeToken<List<PlayerData>>() {};

        @Override
        public PlayerData deserialize(TypeToken<?> token, ConfigurationNode node) throws ObjectMappingException {
            return new PlayerData(
                    node.getNode("name").getString(),
                    node.getNode("uuid").getString(),
                    node.getNode("warpLimit").getInt()
                    );
        }

        @Override
        public void serialize(TypeToken<?> token, PlayerData playerdata, ConfigurationNode node) throws ObjectMappingException {
            node.getNode("name").setValue(playerdata.name);
            node.getNode("uuid").setValue(playerdata.uuid);
            node.getNode("warpLimit").setValue(playerdata.warpLimit);
        }
    }



    public String getPlayerName() {
        return name;
    }

    public String getPlayerUUID() {
        return uuid;
    }

    public Integer getWarpLimit() {
        return warpLimit;
    }

    public void setWarpLimit(Integer newlimit) {
        warpLimit = newlimit;
    }


}
