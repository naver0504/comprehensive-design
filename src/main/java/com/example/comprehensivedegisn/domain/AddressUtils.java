package com.example.comprehensivedegisn.domain;

public final class AddressUtils {
    private static final String SEOUL = "서울특별시";

    public static String getJibunAddress(Gu gu, String dong, String jibun) {
        if(jibun == null) return null;
        return gu + " " + dong + " " + jibun;
    }

    public static String getRoadNameWithSeoulPrefixAndGu(Gu gu, String roadName, int roadNameBonbun, int roadNameBubun) {
        if(roadName == null) return null;
        return SEOUL + " " + getRoadNameWithGu(gu, roadName, roadNameBonbun, roadNameBubun);
    }

    public static String getRoadNameWithGu(Gu gu, String roadName, int roadNameBonbun, int roadNameBubun) {
        if(roadName == null) return null;
        return  gu + " " + concatRoadName(roadName, roadNameBonbun, roadNameBubun);
    }

    public static String getRoadName(String roadName, int roadNameBonbun, int roadNameBubun) {
        if(roadName == null) return null;
        return concatRoadName(roadName, roadNameBonbun, roadNameBubun);
    }

    private static String concatRoadName(String roadName, int roadNameBonbun, int roadNameBubun) {
        if(roadNameBonbun == 0) return roadName;
        return roadName + " " + roadNameBonbun  + getRoadNameBubun(roadNameBubun);
    }

    private static String getRoadNameBubun(int roadNameBubun) {
        return roadNameBubun == 0 ? "" : "-" + roadNameBubun;
    }
}
