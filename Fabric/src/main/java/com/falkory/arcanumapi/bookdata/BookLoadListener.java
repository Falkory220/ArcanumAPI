package com.falkory.arcanumapi.bookdata;

import com.falkory.arcanumapi.book.BookLoader;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resources.ResourceLocation;

import static com.falkory.arcanumapi.ArcanumCommon.AmId;

/** Wraps the Common {@link BookLoader} as an IdentifiableResourceReloadListener for Fabric */
public class BookLoadListener extends BookLoader implements IdentifiableResourceReloadListener {

    @Override
    public ResourceLocation getFabricId() {
        return AmId("bookloader");
    }
}
