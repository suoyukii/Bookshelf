package net.darkhax.bookshelf.tileentity;

import net.darkhax.bookshelf.lib.Constants;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TileEntityBasic extends TileEntity implements ITickable {
    
    @Override
    public void readFromNBT (NBTTagCompound dataTag) {
        
        super.readFromNBT(dataTag);
        readNBT(dataTag);
    }
    
    @Override
    public void writeToNBT (NBTTagCompound dataTag) {
        
        super.writeToNBT(dataTag);
        writeNBT(dataTag);
    }
    
    @Override
    public Packet<?> getDescriptionPacket () {
        
        NBTTagCompound dataTag = new NBTTagCompound();
        writeNBT(dataTag);
        return new SPacketUpdateTileEntity(pos, -1337, dataTag);
    }
    
    @Override
    public void onDataPacket (NetworkManager net, SPacketUpdateTileEntity packet) {
        
        super.onDataPacket(net, packet);
        readNBT(packet.getNbtCompound());
    }
    
    @Override
    public boolean shouldRefresh (World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        
        return oldState.getBlock() != newState.getBlock();
    }
    
    @Override
    public final void update () {
        
        if (this.isInvalid() || !this.getWorld().isBlockLoaded(this.getPos()) || this.getWorld().isRemote)
            return;
            
        try {
            
            this.onEntityUpdate();
        }
        
        catch (Exception exception) {
            
            Constants.LOG.warn("A TileEntity at %s in world %s failed a client update tick!", this.getPos(), this.getWorld().getWorldInfo().getWorldName());
            exception.printStackTrace();
        }
    }
    
    /**
     * Handles the ability to write custom NBT values to a TileEntity.
     * 
     * @param dataTag: The NBTTagCompound for the TileEntity.
     */
    public void writeNBT (NBTTagCompound dataTag) {
    
    }
    
    /**
     * Handles the ability to read custom NBT values from the TileEntity's NBTTagCompound.
     * 
     * @param dataTag: The NBTTagCompound for the TileEntity.
     */
    public void readNBT (NBTTagCompound dataTag) {
    
    }
    
    /**
     * Handles the TileEntity update ticks. This method will only be called in a safe
     * environment.
     */
    public void onEntityUpdate () {
    
    }
}