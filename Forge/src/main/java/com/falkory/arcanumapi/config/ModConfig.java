package com.falkory.arcanumapi.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ModConfig {


    public static final ForgeConfigSpec GENERAL_SPEC;
    static {
        ForgeConfigSpec.Builder configBuilder = new ForgeConfigSpec.Builder();
        setupConfig(configBuilder);
        GENERAL_SPEC = configBuilder.build();
    }

    public static ForgeConfigSpec.IntValue exampleIntConfigEntry; //Macy it says required java doc is absent do we need that ;-; I know java docs are important

    private static void setupConfig(ForgeConfigSpec.Builder builder) {
        exampleIntConfigEntry = builder.defineInRange("example_int_config_entry", 5,2,50);
    }

}
