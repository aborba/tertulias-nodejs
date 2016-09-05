package pt.isel.pdm.g04.pf.data.thoth.models;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.HashMap;
import java.util.Map;

import pt.isel.pdm.g04.pf.data.thoth.models.core.Attributes;

public class BaseAvatarUrl {

    protected String size32;
    @Attributes(notMapped = true)
    protected Map<String, Object> additionalProperties = new HashMap<>();

    /**
     * @return The size32
     */
    public String getSize32() {
        return size32;
    }

    /**
     * @param size32 The size32
     */
    public void setSize32(String size32) {
        this.size32 = size32;
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
        return new HashCodeBuilder().append(size32).append(additionalProperties).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof BaseAvatarUrl) == false) {
            return false;
        }
        BaseAvatarUrl rhs = ((BaseAvatarUrl) other);
        return new EqualsBuilder().append(size32, rhs.size32).append(additionalProperties, rhs.additionalProperties).isEquals();
    }

}
