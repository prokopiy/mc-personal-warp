package com.github.prokopiy.mcpersonalwarp.data;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

import java.util.List;

public class PlayerData {
    protected String groupname;
    protected Integer grouplimit;

    public PlayerData(String groupname, Integer grouplimit) {
        this.groupname = groupname.toLowerCase();
        this.grouplimit = grouplimit;
    }


    public static class PlayerDataSerializer implements TypeSerializer<PlayerData> {
        @SuppressWarnings("serial")
        final public static TypeToken<List<PlayerData>> token = new TypeToken<List<PlayerData>>() {};

        @Override
        public PlayerData deserialize(TypeToken<?> token, ConfigurationNode node) throws ObjectMappingException {
            return new PlayerData(
                    node.getNode("groupname").getString(),
                    node.getNode("grouplimit").getInt());
        }

        @Override
        public void serialize(TypeToken<?> token, PlayerData groupdata, ConfigurationNode node) throws ObjectMappingException {
            node.getNode("groupname").setValue(groupdata.groupname);
            node.getNode("grouplimit").setValue(groupdata.grouplimit);
        }
    }



    public String getGroupName() {
        return groupname;
    }

    public Integer getGroupLimit() {
        return grouplimit;
    }


}
