package com.minecolonies.api.util.constant;

import java.util.HashMap;
import java.util.Map;

public enum ToolType implements IToolType
{
    NONE       ("",        false),
    PICKAXE    ("pickaxe", true),
    SHOVEL     ("shovel",  true),
    AXE        ("axe",     true),
    HOE        ("hoe",     true),
    SWORD      ("weapon",  true),
    BOW        ("bow",     false),
    FISHINGROD ("rod",     false),
    SHEARS     ("shears",  false),
    SHIELD     ("shield",  false);

    static final private Map<String,IToolType> tools = new HashMap<>();
    private final String name;
    private final boolean variableMaterials;

    static
    {
        for(ToolType type : values())
        {
            tools.put(type.getName(), type);
        }
    }

    private ToolType(final String name, final boolean variableMaterials)
    {
        this.name = name;
        this.variableMaterials = variableMaterials;
    }

    public String getName()
    {
        return this.name;
    }

    public boolean hasVariableMaterials()
    {
        return variableMaterials;
    }

    public static IToolType getToolType(final String tool)
    {
        if (tools.containsKey(tool))
        {
            return tools.get(tool);
        }
        return NONE;
    }
}

