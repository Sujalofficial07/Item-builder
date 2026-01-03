package com.sujal.skyblockmaker.util; // Changed package

import net.minecraft.nbt.NbtCompound;

public interface IEntityDataSaver {
    NbtCompound getPersistentData();
}
