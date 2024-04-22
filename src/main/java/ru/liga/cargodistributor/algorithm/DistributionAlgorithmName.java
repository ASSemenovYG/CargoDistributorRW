package ru.liga.cargodistributor.algorithm;

public enum DistributionAlgorithmName {
    ONE_VAN_ONE_ITEM("OneVanOneItemDistribution"),
    SINGLE_SORTED("SingleSortedCargoDistribution"),
    SIMPLE_FIT("SimpleFitDistribution");

    private final String title;

    DistributionAlgorithmName(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public static DistributionAlgorithmName fromString(String text) {
        for (DistributionAlgorithmName algorithmName : DistributionAlgorithmName.values()) {
            if (algorithmName.title.equalsIgnoreCase(text)) {
                return algorithmName;
            }
        }
        return null;
    }
}
