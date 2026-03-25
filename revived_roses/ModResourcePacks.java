package com.dalterdile.revived_roses;

import com.mojang.logging.LogUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.fml.ModList;
import org.slf4j.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class ModResourcePacks {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static void addPackFinders(AddPackFindersEvent event) {
        if (event.getPackType() != PackType.CLIENT_RESOURCES) return;

        // Programmer Art pack
        registerPack(
                event,
                "ProgrammerArtRevivedRoses",
                RevivedRoses.MODID + ":programmer_art",
                "Programmer Art Revived Roses"
        );

        // Faithful Style pack
        registerPack(
                event,
                "FaithfulStyleRevivedRoses",
                RevivedRoses.MODID + ":faithful",
                "Revived Roses – Faithful 32x32"
        );
    }

    private static void registerPack(
            AddPackFindersEvent event,
            String folderName,
            String packId,
            String title
    ) {
        event.addRepositorySource((consumer) -> {
            Path packPath = ModList.get()
                    .getModFileById(RevivedRoses.MODID)
                    .getFile()
                    .findResource("resourcepacks/" + folderName);

            if (!Files.exists(packPath) || !Files.exists(packPath.resolve("pack.mcmeta"))) {
                LOGGER.error("[{}] Pack no encontrado: {}", RevivedRoses.MODID, packPath);
                return;
            }

            PackLocationInfo info = new PackLocationInfo(
                    packId,
                    Component.literal(title),
                    PackSource.BUILT_IN,
                    Optional.empty()
            );

            PackSelectionConfig selection =
                    new PackSelectionConfig(false, Pack.Position.TOP, false);

            Pack.ResourcesSupplier supplier = new Pack.ResourcesSupplier() {
                @Override
                public net.minecraft.server.packs.PackResources openPrimary(PackLocationInfo info) {
                    return new PathPackResources(info, packPath);
                }

                @Override
                public net.minecraft.server.packs.PackResources openFull(PackLocationInfo info, Pack.Metadata metadata) {
                    return new PathPackResources(info, packPath);
                }
            };

            Pack pack = Pack.readMetaAndCreate(info, supplier, PackType.CLIENT_RESOURCES, selection);

            if (pack != null) {
                consumer.accept(pack);
                LOGGER.info("[{}] Pack registrado: {}", RevivedRoses.MODID, title);
            }
        });
    }
}