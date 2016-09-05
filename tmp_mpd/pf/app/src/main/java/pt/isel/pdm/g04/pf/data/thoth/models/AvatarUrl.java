package pt.isel.pdm.g04.pf.data.thoth.models;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.Map;


public class AvatarUrl extends BaseAvatarUrl {

    private String size24;
    private String size64;
    private String size128;

    /**
     * @return The size24
     */
    public String getSize24() {
        return size24;
    }

    /**
     * @param size24 The size24
     */
    public void setSize24(String size24) {
        this.size24 = size24;
    }

    /**
     * @return The size64
     */
    public String getSize64() {
        return size64;
    }

    /**
     * @param size64 The size64
     */
    public void setSize64(String size64) {
        this.size64 = size64;
    }

    /**
     * @return The size128
     */
    public String getSize128() {
        return size128;
    }

    /**
     * @param size128 The size128
     */
    public void setSize128(String size128) {
        this.size128 = size128;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(size24).append(size32).append(size64).append(size128).append(additionalProperties).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof AvatarUrl) == false) {
            return false;
        }
        AvatarUrl rhs = ((AvatarUrl) other);
        return new EqualsBuilder().append(size24, rhs.size24).append(size32, rhs.size32).append(size64, rhs.size64).append(size128, rhs.size128).append(additionalProperties, rhs.additionalProperties).isEquals();
    }

}
