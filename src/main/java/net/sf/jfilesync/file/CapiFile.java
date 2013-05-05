package net.sf.jfilesync.file;

public interface CapiFile {

    public abstract void addChild(final CapiFile file);

    public abstract boolean hasChildren();

   
}