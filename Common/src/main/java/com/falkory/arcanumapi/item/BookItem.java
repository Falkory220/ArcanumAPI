package com.falkory.arcanumapi.item;

import com.falkory.arcanumapi.api.ArcanumAPI;
import com.falkory.arcanumapi.book.BookMain;
import com.falkory.arcanumapi.book.Books;
import com.falkory.arcanumapi.util.Identifiable;
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

import static com.falkory.arcanumapi.ArcanumCommon.AmId;

public class BookItem extends Item {
    ResourceLocation book;

    public BookItem(Properties pops, ResourceLocation book) {
        super(pops);
        this.book = book;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        //todo animation property for open
        if (!player.isDiscrete()) {
            ArcanumAPI.LOG.info("Item \"" + player.getItemInHand(hand).getDisplayName().getString() + "\" used, with target " + this.book);
        }
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, player.getItemInHand(hand));
    }

    @Override
    public Component getName(ItemStack $$0) {
        return super.getName($$0);
    }

    @Override
    public InteractionResult useOn(UseOnContext nya) {
        BlockEntity eepy = nya.getLevel().getBlockEntity(nya.getClickedPos());
        if(eepy != null && eepy.getType() == BlockEntityType.SIGN) {
            SignBlockEntity seepy = (SignBlockEntity) eepy;
            ArcanumAPI.LOG.info(seepy.getMessage(0, true).getString());
            //prints the book if we have it at <signfirstline>:<signsecondline>. this shouldn't be in a release ever uhhhh
            BookMain signybook = Books.BOOKS.getOrDefault(new ResourceLocation(seepy.getMessage(0, true).getString(), seepy.getMessage(1, true).getString()), new BookMain(AmId("notfound"), null));
            ArcanumAPI.LOG.info(signybook.key().toString());
            if(!"notfound".equals(signybook.key().getPath())){ // if we're in a real book
                signybook.getTabs().forEach(tab -> ArcanumAPI.LOG.info("  " + tab.key().toString()));
            }
        }
        return super.useOn(nya);
    }

    private static <E extends Identifiable> void nya(E nya){
        if(nya == null){ArcanumAPI.LOG.info("didn't find the nya"); return;}
        ArcanumAPI.LOG.info(nya.key().toString());
    }
}
