package com.minecolonies.sounds;

import com.minecolonies.util.SoundUtils;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Class containing the fisherman sounds.
 */
public class FishermanSounds
{
    /**
     * Random generator.
     */
    private static Random rand = new Random();

    /**
     * Number of different sounds in this class.
     */
    private static final int NUMBER_OF_SOUNDS = 2;

    /**
     * Chance to say a phrase.
     */
    private static final int PHRASE_CHANCE = 25;

    /**
     * Chance to play a basic sound.
     */
    private static final int BASIC_SOUND_CHANCE = 100;

    /**
     * Containing the female fisherman sounds.
     */
    public static class Female
    {
        public static SoundEvent generalPhrases;
        public static SoundEvent noises;

        public static SoundEvent iGotOne;
        public static SoundEvent needFishingRod;
        public static SoundEvent offToBed;
    }

    /**
     * Containing the male fisherman sounds.
     */
    public static class Male
    {

    }

    /**
     * Plays fisherman sounds.
     * @param worldIn the world to play the sound in.
     * @param position the position to play the sound at.
     * @param isFemale the gender.
     */
    public static void playFishermanSound(World worldIn, BlockPos position, boolean isFemale)
    {
        if(isFemale)
        {
            //Leaving it as switch-case we may add further random sound categories here (Whistling, singing, etc).
            switch(rand.nextInt(NUMBER_OF_SOUNDS+1))
            {
                case 1:
                    SoundUtils.playSoundAtCitizenWithChance(worldIn, position, Female.generalPhrases, PHRASE_CHANCE);
                    break;
                case 2:
                    SoundUtils.playSoundAtCitizenWithChance(worldIn, position, Female.noises, BASIC_SOUND_CHANCE);
                    break;
            }

            return;
        }

        //Following the male sounds as soon as they are uploaded.

    }
}
