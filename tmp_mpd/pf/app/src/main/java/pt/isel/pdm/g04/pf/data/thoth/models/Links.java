package pt.isel.pdm.g04.pf.data.thoth.models;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import pt.isel.pdm.g04.pf.data.thoth.models.core.Attributes;

public class Links extends BaseLinks {

    @Attributes(notMapped = true)
    private String root;


    /**
     * @return The root
     */
    public String getRoot() {
        return root;
    }

    /**
     * @param root The root
     */
    public void setRoot(String root) {
        this.root = root;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }


    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(self).append(root).append(additionalProperties).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Links) == false) {
            return false;
        }
        Links rhs = ((Links) other);
        return new EqualsBuilder().append(self, rhs.self).append(root, rhs.root).append(additionalProperties, rhs.additionalProperties).isEquals();
    }

}
