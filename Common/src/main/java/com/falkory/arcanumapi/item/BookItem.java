package com.falkory.arcanumapi.item;

import com.falkory.arcanumapi.api.ArcanumAPI;
import com.falkory.arcanumapi.book.Books;
import com.falkory.arcanumapi.util.SplitUtils;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignBlockEntity;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BookItem extends Item {
    ResourceLocation book;

    public BookItem(Properties pops, ResourceLocation book) {
        super(pops);
        this.book = book;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if(player.isDiscrete() && ArcanumAPI.LOG.isDebugEnabled()) return super.use(level, player, hand);
        //todo animation property for open
        SplitUtils.openBookSafe(player, book, player.getItemInHand(hand));
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, player.getItemInHand(hand));
    }

    @Override
    public Component getName(ItemStack $$0) {
        return super.getName($$0);
    }

    @Override public InteractionResult useOn(UseOnContext ctx) {
        if(ArcanumAPI.LOG.isDebugEnabled()) debugGetBook(ctx);
        return super.useOn(ctx);
    }

    private void debugGetBook(UseOnContext ctx){
        if(ctx.getPlayer() == null) return;
        BlockEntity block = ctx.getLevel().getBlockEntity(ctx.getClickedPos());
        if (block == null || block.getType() != BlockEntityType.SIGN) return;
        SignBlockEntity signBlock = (SignBlockEntity) block;

        //collects the messages and makes sure they're a valid resource location
        StringBuilder messages = new StringBuilder();
        for (int i = 0; i < 4; i++) messages.append(signBlock.getMessage(i, true).getString());
        String messageConcat = messages.toString();
        if(!ResourceLocation.isValidResourceLocation(messageConcat)) return;

        //opens the book if we have it registered.
        ResourceLocation signBookId = new ResourceLocation(messageConcat);
        if(Books.BOOKS.containsKey(signBookId)){
            SplitUtils.openBookSafe(ctx.getPlayer(), signBookId, ctx.getItemInHand());
        } else {
            ArcanumAPI.LOG.info("No book found at location: "+ messageConcat);
        }
    }

}
