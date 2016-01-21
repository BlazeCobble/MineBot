/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot.pathfinding;

import java.util.Objects;
import minebot.MineBot;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.BlockPos;

/**
 *
 * @author leijurv
 */
public class ActionBridge extends ActionPlaceOrBreak {
    public ActionBridge(BlockPos from, BlockPos to) {
        super(from, to, new BlockPos[]{to, new BlockPos(to.getX(), to.getY() + 1, to.getZ())}, new BlockPos[]{new BlockPos(to.getX(), to.getY() - 1, to.getZ())});
    }
    @Override
    protected double calculateCost() {
        if (canWalkOn(blocksToPlace[0])) {//this is a walk, not a bridge
            if (canWalkThrough(blocksToBreak[0]) && canWalkThrough(blocksToBreak[1])) {
                return WALK_ONE_BLOCK_COST;
            }
            //double hardness1 = blocksToBreak[0].getBlockHardness(Minecraft.theMinecraft.theWorld, positionsToBreak[0]);
            //double hardness2 = blocksToBreak[1].getBlockHardness(Minecraft.theMinecraft.theWorld, positionsToBreak[1]);
            //System.out.println("Can't walk through " + blocksToBreak[0] + " (hardness" + hardness1 + ") or " + blocksToBreak[1] + " (hardness " + hardness2 + ")");
            return WALK_ONE_BLOCK_COST + getTotalHardnessOfBlocksToBreak() * HARDNESS_MULTIPLIER;
        } else {//this is a bridge, so we need to place a block
            //return 1000000;
            return WALK_ONE_BLOCK_COST + PLACE_ONE_BLOCK_COST + getTotalHardnessOfBlocksToBreak() * HARDNESS_MULTIPLIER;
            //System.out.println("Can't walk on " + Minecraft.theMinecraft.theWorld.getBlockState(positionsToPlace[0]).getBlock());
        }
    }
    @Override
    protected boolean tick0() {
        boolean isTheBridgeBlockThere = !canWalkThrough(Minecraft.theMinecraft.theWorld.getBlockState(positionsToPlace[0]).getBlock());
        System.out.println("is block there: " + isTheBridgeBlockThere + " block " + Minecraft.theMinecraft.theWorld.getBlockState(positionsToPlace[0]).getBlock());
        EntityPlayerSP thePlayer = Minecraft.theMinecraft.thePlayer;
        BlockPos whereAmI = new BlockPos(thePlayer.posX, thePlayer.posY, thePlayer.posZ);
        if (isTheBridgeBlockThere) {//either the bridge block was there the whole time or we just placed it
            MineBot.lookAtBlock(to, false);//look at where we are walking
            MineBot.sneak = false;
            MineBot.backward = false;
            MineBot.forward = true;//we are going forward
            if (whereAmI.equals(to)) {//if we are there
                System.out.println("Done walking to " + to);
                MineBot.forward = false;//stop walking forwards
                MineBot.backward = false;
                MineBot.sneak = false;
                return true;//and we are done
            }
            System.out.println("Trying to get to " + to + " currently at " + whereAmI);
            return false;//not there yet
        } else {
            MineBot.sneak = true;
            double faceX = (to.getX() + from.getX() + 1.0D) * 0.5D;
            double faceY = (to.getY() + from.getY() - 1.0D) * 0.5D;
            double faceZ = (to.getZ() + from.getZ() + 1.0D) * 0.5D;
            //double faceX = to.getX();
            //double faceY = to.getY();
            //double faceZ = to.getZ();
            BlockPos goalLook = new BlockPos(from.getX(), from.getY() - 1, from.getZ());
            if (whereAmI.equals(to)) {
                System.out.println(from + " " + to + " " + faceX + "," + faceY + "," + faceZ + " " + whereAmI);
                MineBot.lookAtCoords(faceX, faceY, faceZ, true);
                if (Objects.equals(MineBot.whatAreYouLookingAt(), goalLook)) {
                    Minecraft.theMinecraft.rightClickMouse();
                }
                MineBot.forward = false;
                MineBot.backward = true;
                System.out.println("Trying to look at " + goalLook + ", actually looking at" + MineBot.whatAreYouLookingAt());
                return false;
            } else {
                MineBot.forward = true;
                MineBot.lookAtBlock(to, false);//look at where we are walking
                return false;
            }
        }
    }
}
