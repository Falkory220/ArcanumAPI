package com.falkory.arcanumapi.client.gui.widgets;

import com.falkory.arcanumapi.book.BookMain;
import com.falkory.arcanumapi.book.BookTab;
import com.falkory.arcanumapi.client.gui.BookMainScreen;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 *  A LayeredWidgetHolder variant made specifically to work with {@link BookTabButton BookTabButtons}.
 * */
public class BookTabList extends LayeredWidgetHolder {
    BookMain book;
    Screen parentScreen;

    //Tabs are x24 textures // todo make dynamic at some point? consider divide by zoom times preferred scale
    protected final int buttonHeight = 24;
    protected final int buttonWidth = 24;
    // space each is allotted, in button pixels.
    protected final int xSpace = 18;
    protected final int ySpace = 26;

    public BookTabList(BookMainScreen parent){
        this.book = parent.getBook();
        parentScreen = parent;

        makeButtons();
        positionButtons();
    }

    private void makeButtons(){
        this.clearWidgets();
        for (BookTab tab : book.getTabs()){
            BookTabButton tabLink = new BookTabButton(book, tab.key(), 0, 0, buttonWidth, Component.translatable(tab.name()));
            addLayeredWidget(tabLink);
            //make our selected tab stay selected
            if(tab.key() == book.getTabKey()) select(tabLink);
        }
    }

    private void positionButtons(){
        //TODO temp but it's funny while it lasts, make it take a tabacceptor or smth as a parameter and push its stuff to that maybe
        setPos(0, BookMainScreen.minBookY);
        width = BookMainScreen.minBookX;
        height = BookMainScreen.bookHeight;

        int expectedColumns = (int) Math.ceil(((renderables.size()*ySpace) + buttonHeight)/(float)height);
        expectedColumns = (int) Math.ceil((((renderables.size()+expectedColumns-2)*ySpace) + buttonHeight)/(float)height);
        int neededSpace = -width +(expectedColumns * xSpace) + xSpace;
        if(neededSpace > 0){
            BookMainScreen.bookWidth -= neededSpace;
            BookMainScreen.minBookX += neededSpace;
            width = BookMainScreen.minBookX;
        }

        float tabOffset = 0;
        float left = 0;

        for(Deque<Widget> tabButtons = new ArrayDeque<>(renderables); !tabButtons.isEmpty();){
            BookTabButton tab = (BookTabButton)tabButtons.pop();
            if(tabOffset+ tab.getHeight() > height) {
                left += 1;
                tabOffset = (int)Math.ceil(ySpace*left/expectedColumns);
            }
            tab.setWidth(tab.getWidth() + (int)left*xSpace);
            tab.x = -tab.getWidth() + this.getX() + width;
            tab.y = this.getY() + (int)tabOffset;

            tabOffset += ySpace;
        }
    }

    /**@param widget the widget to select. If given null, selects the book's current tab instead.*/
    @Override public void select(@Nullable Widget widget) {
        if(widget == null) for(Widget w : renderables) if(((BookTabButton)w).getLink() == book.getTabKey()) {widget = w; break;} // my code is smooth, like my brain :3
        super.select(widget);
    }

    @Override protected void init() {
        super.init();
        positionButtons();
    }

    @Override public void updateNarration(@NotNull NarrationElementOutput narrationElementOutput) {
        narrationElementOutput.add(NarratedElementType.POSITION, Component.translatable("narrator.booktablist"));
        super.updateNarration(narrationElementOutput);
    }
}
