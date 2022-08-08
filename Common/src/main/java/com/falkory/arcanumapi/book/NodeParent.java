package com.falkory.arcanumapi.book;

import net.minecraft.ResourceLocationException;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NodeParent {
    // this whole thing is one biiig snip (thank you Luna)
    //TODO rip and tear (separate node linking and unlock conditions)

    ResourceLocation entry;
    int stage = -1;
    boolean showArrowhead = true, showLine = true, reverseLine = false;

    public static Logger LOGGER = LogManager.getLogger();

    public NodeParent(ResourceLocation entry){
        this.entry = entry;
    }

    public static NodeParent parse(String text){
        NodeParent parent = new NodeParent(null);
        String original = text;
        // Check for prefixes
        // ~ for no line, & for no arrowheads, / for reversed
        // @ for stage
        if(text.startsWith("~")){
            text = text.substring(1);
            parent.showLine = false;
        }
        if(text.startsWith("&")){
            text = text.substring(1);
            parent.showArrowhead = false;
        }
        if(text.startsWith("/")){
            text = text.substring(1);
            parent.reverseLine = true;
        }
        if(text.contains("@")){
            String[] sections = text.split("@");
            text = sections[0];
            try{
                parent.stage = Integer.parseUnsignedInt(sections[1]);
            }catch(NumberFormatException exception){
                LOGGER.error("Invalid entry stage \"" + sections[1] + "\" found in parent \"" + original + "\"!");
            }
        }
        try{
            parent.entry = new ResourceLocation(text);
        }catch(ResourceLocationException exception){
            LOGGER.error("Invalid entry \"" + text + "\" found in parent \"" + original + "\"!");
        }
        return parent;
    }

    public String asString(){
        return (!showLine ? "~" : "") + (!showArrowhead ? "&" : "")+ (reverseLine ? "/" : "") + entry + (stage != -1 ? "@" + stage : "");
    }

    public ResourceLocation getEntry(){
        return entry;
    }

    public int getStage(){
        return stage;
    }

    public boolean shouldShowArrowhead(){
        return showArrowhead;
    }

    public boolean shouldShowLine(){
        return showLine;
    }

    public boolean shouldReverseLine(){
        return reverseLine;
    }

    /*
     * quickly commenting out the link to the research system proper here until... something
     *
    public boolean satisfiedBy(Researcher r){
        ResearchEntry entry = ResearchBooks.getEntry(getEntry());
        if(entry == null)
            return true;
        if(entry.meta().contains("locked"))
            return false;
        return stage == -1 ? r.entryStage(entry) >= entry.sections().size() : r.entryStage(entry) >= stage;
    }
    */
}
