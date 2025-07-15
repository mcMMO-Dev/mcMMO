package com.gmail.nossr50.util.nms;

public enum NMSVersion {
    //1.8
    NMS_1_8_8("1.8.8"),

    //1.12
    NMS_1_12_2("1.12.2"),

    //1.13
    NMS_1_13_2("1.13.2"),

    //1.14
    NMS_1_14_4("1.14.4"),

    //1.15
    NMS_1_15_2("1.15.2"),

    //1.16
    NMS_1_16_1("1.16.1"),
    NMS_1_16_2("1.16.2"),
    NMS_1_16_3("1.16.3"),
    NMS_1_16_4("1.16.4"),
    NMS_1_16_5("1.16.5"),
    NMS_1_17("1.17"),

    //Version not known to this build of mcMMO
    UNSUPPORTED("unsupported");

    private final String sanitizedVersionNumber;

    NMSVersion(String sanitizedVersionNumber) {
        this.sanitizedVersionNumber = sanitizedVersionNumber;
    }

    /**
     * The standardized major.minor.patch {@link String} for the current NMS mappings
     *
     * @return the standardized major.minor.patch version string, patch is omitted if it is not a
     * patch version
     */
    public String getSanitizedVersionNumber() {
        return sanitizedVersionNumber;
    }
}
