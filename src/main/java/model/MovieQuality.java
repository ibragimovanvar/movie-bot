package model;

public enum MovieQuality {
    SD_144("144p"),

    SD_240("240p"),

    SD_480("480p"),

    HD_720("720p"),

    FHD_1080("1080p"),

    QHD_1440("1440p"),

    UHD_2160("2K (2160p)"),

    FUHD_4320("4K (4320p)");

    public final String alias;

    MovieQuality(String alias) {
        this.alias = alias;
    }
}
