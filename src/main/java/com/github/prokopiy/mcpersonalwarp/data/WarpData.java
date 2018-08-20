package com.github.prokopiy.mcpersonalwarp.data;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

import java.util.List;

public class WarpData {
    protected String name;
    protected String ownerName;
    protected String ownerUUID;


    public WarpData(String name, String ownerName, String ownerUUID) {
        this.name = name;
        this.ownerName = ownerName;
        this.ownerUUID = ownerUUID;
    }


    public static class WarpDataSerializer implements TypeSerializer<WarpData> {
        @SuppressWarnings("serial")
        final public static TypeToken<List<WarpData>> token = new TypeToken<List<WarpData>>() {};

        @Override
        public WarpData deserialize(TypeToken<?> token, ConfigurationNode node) throws ObjectMappingException {
            return new WarpData(
                    node.getNode("name").getString(),
                    node.getNode("ownerName").getString(),
                    node.getNode("ownerUUID").getString()
                    );
        }

        @Override
        public void serialize(TypeToken<?> token, WarpData warpdata, ConfigurationNode node) throws ObjectMappingException {
            node.getNode("name").setValue(warpdata.name);
            node.getNode("ownerName").setValue(warpdata.ownerName);
            node.getNode("ownerUUID").setValue(warpdata.ownerUUID);
        }
    }



    public String getWarpName() {
        return name;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getOwnerUUID() {
        return ownerUUID;
    }


}
