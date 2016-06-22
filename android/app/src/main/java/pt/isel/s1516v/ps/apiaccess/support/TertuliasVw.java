package pt.isel.s1516v.ps.apiaccess.support;

public class TertuliasVw {

    @com.google.gson.annotations.SerializedName("userId")
    public String userId;
    @com.google.gson.annotations.SerializedName("userAlias")
    public String userAlias;
    @com.google.gson.annotations.SerializedName("tertuliaId")
    public String tertuliaId;
    @com.google.gson.annotations.SerializedName("tertuliaTitle")
    public String title;
    @com.google.gson.annotations.SerializedName("tertuliaSubject")
    public String subject;
    @com.google.gson.annotations.SerializedName("tertuliaSchedule")
    public int schedule;
    @com.google.gson.annotations.SerializedName("tertuliaPrivate")
    public int isPrivate;
    @com.google.gson.annotations.SerializedName("tertuliaIdentity")
    public int identity;

    @Override
    public String toString() { return title; }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        TertuliasVw other = (TertuliasVw) obj;
        return obj instanceof TertuliasVw
                && other.userId == this.userId
                && other.tertuliaId == this.tertuliaId;
    }

}
