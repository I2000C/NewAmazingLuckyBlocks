package me.i2000c.newalb.utils.particles.data;

import xyz.xenondevs.particle.data.color.NoteColor;

public class NoteParticleData extends ColoredParticleData {
    
    public NoteParticleData(int note) {
        super(note, 0, 0);
    }
    
    public int getNote() {
        return super.getRed();
    }
    
    public void setNote(int note) {
        super.setRed(note);
    }

    @Override
    public xyz.xenondevs.particle.data.ParticleData convertToParticleLibData() {
        return new NoteColor(getNote());
    }

    @Override
    public <T> T convertToBukkitParticleData() {
        throw new IllegalArgumentException("This data cannot be converted to bukkit data");
    }
    
}
