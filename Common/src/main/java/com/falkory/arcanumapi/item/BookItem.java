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

    //if book is used on a sign in debug mode, get the book
    @Override public InteractionResult useOn(UseOnContext nya) {
        if(!ArcanumAPI.LOG.isDebugEnabled() || nya.getPlayer() == null) return super.useOn(nya);

        nya.getItemInHand();
        BlockEntity eepy = nya.getLevel().getBlockEntity(nya.getClickedPos());

        if (eepy != null && eepy.getType() == BlockEntityType.SIGN) {
            SignBlockEntity seepy = (SignBlockEntity) eepy;

            //opens the book if we have it at <signfirstline>:<signsecondline>.
            ResourceLocation signBookId = new ResourceLocation(seepy.getMessage(0, true).getString(), seepy.getMessage(1, true).getString());
            if(Books.BOOKS.containsKey(signBookId)){
                SplitUtils.openBookSafe(nya.getPlayer(), signBookId, nya.getItemInHand());
            }
        }
        return super.useOn(nya);
    }

}
