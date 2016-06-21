package pt.isel.s1516v.ps.apiaccess.support;

public class MembersVw {

    @com.google.gson.annotations.SerializedName("userId")
    public String userId;
    @com.google.gson.annotations.SerializedName("userAlias")
    public String userAlias;
    @com.google.gson.annotations.SerializedName("memberId")
    public String memberId;
    @com.google.gson.annotations.SerializedName("tertuliaId")
    public String tertuliaId;
    @com.google.gson.annotations.SerializedName("tertuliaTitle")
    public String title;

    @Override
    public String toString() { return title; }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        MembersVw other = (MembersVw) obj;
        return obj instanceof MembersVw
                && other.userId == this.userId
                && other.memberId == this.memberId
                && other.tertuliaId == this.tertuliaId;
    }

}
