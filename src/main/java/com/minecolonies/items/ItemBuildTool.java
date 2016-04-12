package com.minecolonies.items;

import com.minecolonies.MineColonies;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemBuildTool extends ItemMinecolonies
{
    public ItemBuildTool()
    {
        super();
        setMaxStackSize(1);
    }

    @Override
    public String getName()
    {
        return "scepterGold";
    }

    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int face, float par8, float par9, float par10)
    {
        if(!world.isRemote)
        {
            return false;
        }

        switch(face)
        {
        case 0:
            y--;
            break;
        case 1:
            y++;
            break;
        case 2:
            z--;
            break;
        case 3:
            z++;
            break;
        case 4:
            x--;
            break;
        case 5:
            x++;
            break;
        }

        MineColonies.proxy.openBuildToolWindow(x, y, z);
        return false;
    }
}