package com.minecolonies.coremod.entity.ai.mobs.util;

import com.minecolonies.api.configuration.Configurations;
import com.minecolonies.api.util.BlockPosUtil;
import com.minecolonies.api.util.LanguageHandler;
import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.coremod.colony.CitizenData;
import com.minecolonies.coremod.colony.Colony;
import com.minecolonies.coremod.colony.buildings.AbstractBuilding;
import com.minecolonies.coremod.colony.buildings.BuildingBarracks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Utils for Colony mob events
 */
public final class MobEventsUtils
{
    private static final ResourceLocation BARBARIAN = new ResourceLocation(Constants.MOD_ID, "Barbarian");
    private static final ResourceLocation ARCHER    = new ResourceLocation(Constants.MOD_ID, "ArcherBarbarian");
    private static final ResourceLocation CHIEF     = new ResourceLocation(Constants.MOD_ID, "ChiefBarbarian");

    private static final int    MAX_SIZE                     = Configurations.gameplay.maxBarbarianHordeSize;
    private static final double BARBARIANS_MULTIPLIER        = 0.5;
    private static final double ARCHER_BARBARIANS_MULTIPLIER = 0.25;
    private static final double CHIEF_BARBARIANS_MULTIPLIER  = 0.1;
    private static final int    PREFERRED_MAX_HORDE_SIZE     = 40;
    private static final int    PREFERRED_MAX_BARBARIANS     = 22;
    private static final int    PREFERRED_MAX_ARCHERS        = 16;
    private static final int    PREFERRED_MAX_CHIEFS         = 2;
    private static final int    MIN_CITIZENS_FOR_RAID        = 5;
    private static final int    NUMBER_OF_CITIZENS_NEEDED    = 5;
    private static       int    numberOfBarbarians           = 0;
    private static       int    numberOfArchers              = 0;
    private static       int    numberOfChiefs               = 0;

    /**
     * Spawn modifier to decrease the spawnrate.
     */
    private static final int SPAWN_MODIFIER = 3;

    /**
     * Private constructor to hide the implicit public one.
     */
    private MobEventsUtils()
    {
    }

    public static void barbarianEvent(final World world, final Colony colony)
    {
        if(world == null)
        {
            return;
        }

        numberOfSpawns(colony);

        final BlockPos targetSpawnPoint = calculateSpawnLocation(world, colony);

        if(targetSpawnPoint.equals(colony.getCenter()))
        {
            return;
        }

        if (Configurations.gameplay.enableInDevelopmentFeatures)
        {
            LanguageHandler.sendPlayersMessage(
              colony.getMessageEntityPlayers(),
              "Horde Spawn Point: " + targetSpawnPoint);
        }

        LanguageHandler.sendPlayersMessage(
          colony.getMessageEntityPlayers(),
          "event.minecolonies.raidMessage");

        BarbarianSpawnUtils.spawn(BARBARIAN, numberOfBarbarians, targetSpawnPoint, world);
        BarbarianSpawnUtils.spawn(ARCHER, numberOfArchers, targetSpawnPoint, world);
        BarbarianSpawnUtils.spawn(CHIEF, numberOfChiefs, targetSpawnPoint, world);
    }

    /**
     * Sets the number of spawns for each barbarian type
     *
     * @param colony The colony to get the RaidLevel from
     */
    private static void numberOfSpawns(final Colony colony)
    {
        if (colony.getCitizens().size() < MIN_CITIZENS_FOR_RAID)
        {
            return;
        }

        final int raidLevel = getColonyRaidLevel(colony);

        numberOfBarbarians = (int) (BARBARIANS_MULTIPLIER * raidLevel / SPAWN_MODIFIER);
        numberOfArchers = (int) (ARCHER_BARBARIANS_MULTIPLIER * raidLevel / SPAWN_MODIFIER);
        numberOfChiefs = (int) (CHIEF_BARBARIANS_MULTIPLIER * raidLevel / SPAWN_MODIFIER);

        int hordeTotal = numberOfBarbarians + numberOfArchers + numberOfChiefs;

        if (hordeTotal > PREFERRED_MAX_HORDE_SIZE && MAX_SIZE == PREFERRED_MAX_HORDE_SIZE)
        {
            //set the preferred horde style if the total spawns is greater that the config's max size
            numberOfBarbarians = PREFERRED_MAX_BARBARIANS;
            numberOfArchers = PREFERRED_MAX_ARCHERS;
            numberOfChiefs = PREFERRED_MAX_CHIEFS;
        }
        else if (hordeTotal > MAX_SIZE)
        {
            //Equalize the spawns so that there is less spawns than the config's max size
            numberOfBarbarians = equalizeBarbarianSpawns(hordeTotal, numberOfBarbarians);
            hordeTotal = numberOfArchers + numberOfBarbarians + numberOfChiefs;
            numberOfArchers = equalizeBarbarianSpawns(hordeTotal, numberOfArchers);
            hordeTotal = numberOfArchers + numberOfBarbarians + numberOfChiefs;
            numberOfChiefs = equalizeBarbarianSpawns(hordeTotal, numberOfChiefs);
        }
    }

    /**
     * Calculate a random spawn point along the colony's border
     *
     * @param world  in the world.
     * @param colony the Colony to spawn the barbarians near.
     * @return Returns the random blockPos
     */
    private static BlockPos calculateSpawnLocation(final World world, @NotNull final Colony colony)
    {
        final Random random = new Random();
        final BlockPos pos = colony.getRandomOutsiderInDirection(
                random.nextInt(2) < 1 ? EnumFacing.EAST : EnumFacing.WEST,
                random.nextInt(2) < 1 ? EnumFacing.NORTH : EnumFacing.SOUTH);

        if(pos.equals(colony.getCenter()))
        {
            return colony.getCenter();
        }

        return BlockPosUtil.findLand(pos, world);
    }

    /**
     * Takes a colony and spits out that colony's RaidLevel.
     *
     * @param colony The colony to use
     * @return an int describing the raid level
     */
    public static int getColonyRaidLevel(final Colony colony)
    {
        int levels = 0;

        @NotNull final List<CitizenData> citizensList = new ArrayList<>();
        citizensList.addAll(colony.getCitizens().values());

        for (@NotNull final CitizenData citizen : citizensList)
        {
            if (citizen.getJob() != null && citizen.getWorkBuilding() != null)
            {
                final int buildingLevel = citizen.getWorkBuilding().getBuildingLevel();
                levels += buildingLevel;
            }
        }

        for(final AbstractBuilding building: colony.getBuildings().values())
        {
            if(building instanceof BuildingBarracks)
            {
                levels+= building.getBuildingLevel() * 2;
            }
        }

        if (colony.getTownHall() != null)
        {
            return levels + colony.getTownHall().getBuildingLevel() * 2;
        }
        else
        {
            return 0;
        }
    }

    /**
     * Reduces barbarian spawns to less than the maximum allowed (set via the config)
     *
     * @param total    The current horde size
     * @param numberOf The number of barbarians which we are reducing
     * @return the new number that the barbarians should be set to.
     */
    private static int equalizeBarbarianSpawns(final int total, final int numberOf)
    {
        int returnValue = numberOf;
        if (total > MAX_SIZE)
        {
            returnValue = total - MAX_SIZE;

            if (returnValue < 0)
            {
                return 0;
            }
            return returnValue;
        }
        return returnValue;
    }

    public static boolean isItTimeToRaid(final World world, final Colony colony)
    {
        if (colony.getCitizens().size() < NUMBER_OF_CITIZENS_NEEDED)
        {
            return false;
        }

        if (world.isDaytime())
        {
            if(!colony.hasWillRaidTonight())
            {
                final boolean raid = raidThisNight(world);
                if (Configurations.gameplay.enableInDevelopmentFeatures)
                {
                    LanguageHandler.sendPlayersMessage(
                            colony.getMessageEntityPlayers(),
                            "Will raid tonight: " + raid);
                }
                colony.setWillRaidTonight(raid);
            }
            return false;
        }
        else if (colony.hasWillRaidTonight())
        {
            colony.setWillRaidTonight(false);
            if (Configurations.gameplay.enableInDevelopmentFeatures)
            {
                LanguageHandler.sendPlayersMessage(
                        colony.getMessageEntityPlayers(),
                        "Night reached: raiding");
            }
            return true;
        }

        return false;
    }

    /**
     * Returns whether a raid should happen depending on the Config
     *
     * @param world The world in which the raid is possibly happening (Used to get a random number easily)
     * @return Boolean value on whether to act this night
     */
    private static boolean raidThisNight(final World world)
    {
        return world.rand.nextDouble() < 1.0 / Configurations.gameplay.averageNumberOfNightsBetweenRaids;
    }
}
