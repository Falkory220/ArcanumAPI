package com.falkory.arcanumapi.book.layers;

import com.falkory.arcanumapi.book.BookNode;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * An interface for making layer-held nodes accessible to parent {@link com.falkory.arcanumapi.book.BookTab BookTabs}
 * and the {@link com.falkory.arcanumapi.book.BookMain BookMain} as a whole. <br>
 * Please implement this interface in any layer types that hold nodes, or things may break!
 * */
public interface NodeLayer {
    /** Takes a {@link BookNode} and returns a contained node with matching ID*/
    BookNode node(BookNode node);

    /** Takes a {@link ResourceLocation} and returns a contained node with matching ID*/
    BookNode getNode(ResourceLocation key);

    /** @return A list of all nodes the layer contains. */
    List<BookNode> nodes();

    /** @return The map of the layer's nodes with IDs attached. */
    Map<ResourceLocation, BookNode> getNodes();

    default Stream<BookNode> streamNodes(){
        return nodes().stream();
    }




}
