package com.github.neder_land.gamecenter.client.mod;

import com.github.neder_land.gamecenter.client.api.mod.IModInfo;
import com.google.gson.annotations.Expose;

import javax.annotation.ParametersAreNonnullByDefault;
import java.net.URL;
import java.util.Objects;
import java.util.jar.JarFile;

@ParametersAreNonnullByDefault
public class ModInfo implements IModInfo {
    public static final ModInfo INVALID = new ModInfo("INVALID", "INVALID", "INVALID", new String[]{}, "INVALID");
    public final String modid;
    public final String version;
    public final String name;
    public final String[] dependencies;
    private final String mainClass;
    @Expose(serialize = false, deserialize = false)
    private JarFile jar = null;
    @Expose
    private URL location = null;

    public ModInfo(String modid, String version, String name, String[] dependencies, String mainClass) {
        this.modid = modid;
        this.version = version;
        this.name = name;
        this.dependencies = dependencies;
        this.mainClass = mainClass;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String mi : dependencies) {
            sb.append(mi);
            sb.append(',');
        }
        sb.trimToSize();
        return String.format("Mod[modid=%s,version=%s,name=%s,dependencies={%s}", modid, version, name, sb.substring(0, sb.lastIndexOf(",")));
    }

    @Override
    public String modid() {
        return modid;
    }

    @Override
    public String version() {
        return version;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String[] dependencies() {
        return dependencies;
    }

    @Override
    public String mainClass() {
        return mainClass;
    }

    public JarFile getModJar() {
        return jar;
    }

    public URL getLocation() {
        return location;
    }

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     *
     * <p>The implementor must ensure <tt>sgn(x.compareTo(y)) ==
     * -sgn(y.compareTo(x))</tt> for all <tt>x</tt> and <tt>y</tt>.  (This
     * implies that <tt>x.compareTo(y)</tt> must throw an exception iff
     * <tt>y.compareTo(x)</tt> throws an exception.)
     *
     * <p>The implementor must also ensure that the relation is transitive:
     * <tt>(x.compareTo(y)&gt;0 &amp;&amp; y.compareTo(z)&gt;0)</tt> implies
     * <tt>x.compareTo(z)&gt;0</tt>.
     *
     * <p>Finally, the implementor must ensure that <tt>x.compareTo(y)==0</tt>
     * implies that <tt>sgn(x.compareTo(z)) == sgn(y.compareTo(z))</tt>, for
     * all <tt>z</tt>.
     *
     * <p>It is strongly recommended, but <i>not</i> strictly required that
     * <tt>(x.compareTo(y)==0) == (x.equals(y))</tt>.  Generally speaking, any
     * class that implements the <tt>Comparable</tt> interface and violates
     * this condition should clearly indicate this fact.  The recommended
     * language is "Note: this class has a natural ordering that is
     * inconsistent with equals."
     *
     * <p>In the foregoing description, the notation
     * <tt>sgn(</tt><i>expression</i><tt>)</tt> designates the mathematical
     * <i>signum</i> function, which is defined to return one of <tt>-1</tt>,
     * <tt>0</tt>, or <tt>1</tt> according to whether the value of
     * <i>expression</i> is negative, zero or positive.
     *
     * @param o the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     * is less than, equal to, or greater than the specified object.
     * @throws NullPointerException if the specified object is null
     * @throws ClassCastException   if the specified object's type prevents it
     *                              from being compared to this object.
     */
    @Override
    public int compareTo(IModInfo o) {
        return o.modid().compareTo(modid());
    }

    public void setModJar(JarFile jar, URL url) {
        Objects.requireNonNull(jar, "JarFile can't be null");
        Objects.requireNonNull(url, "URL can't be null");
        if (this.jar == null) this.jar = jar;
        if (this.location == null) location = url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModInfo modInfo = (ModInfo) o;
        return modid.equals(modInfo.modid) &&
                version.equals(modInfo.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(modid, version);
    }
}
