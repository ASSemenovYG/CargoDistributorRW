package ru.liga.dcs.algorithm;

import ru.liga.dcs.cargo.CargoItem;
import ru.liga.dcs.cargo.CargoList;
import ru.liga.dcs.cargo.CargoVan;

import java.util.*;

/**
 * Класс алгоритма распределения грузов с максимальной оптимизацией погрузочного пространства грузовых фургонов.
 * В основу положено рекурсивное <a href="https://www.geeksforgeeks.org/perfect-sum-problem-print-subsets-given-sum/">решение</a>  задачи Perfect Sum Problem.
 * <br>
 * Решения задачи, чьё содержимое при суммировании всех решений приведет к тому,
 * что число элементов в сумме решений превысит число элементов в оригинальном списке, исключаются,
 * чтобы посылка, имеющая три экземпляра в исходном списке не оказалась в итоге погруженной в фургон более трех раз.
 */
public class MaximumCapacityDistribution extends DistributionAlgorithm {

    private LinkedList<CargoItem> cargoItems;
    private LinkedList<CargoItem> cargoItemsOriginal;
    private LinkedList<List<CargoItem>> summedCargoItemSubsets;
    private boolean[][] dp;

    public MaximumCapacityDistribution(CargoList cargoList) {
        super(cargoList);
    }

    @Override
    public List<CargoVan> distributeCargo(CargoList cargoList) {
        List<CargoVan> result = new ArrayList<>();
        this.summedCargoItemSubsets = new LinkedList<>();
        this.cargoItems = new LinkedList<>(cargoList.getCargo());
        this.cargoItemsOriginal = new LinkedList<>(cargoList.getCargo());
        cargoItems.sort(Comparator.comparing(CargoItem::getLength).reversed());

        int currMaxWidth = CargoVan.VAN_WIDTH;
        while (currMaxWidth > 0 && !cargoItems.isEmpty()) {
            getAllSubsets(cargoItems, cargoItems.size(), currMaxWidth);
            removeAddedCargoItems(summedCargoItemSubsets);
            currMaxWidth--;
        }
        while (!summedCargoItemSubsets.isEmpty()) {
            result.add(getFilledCargoVan());
        }
        return result;
    }

    private CargoVan getFilledCargoVan() {
        CargoVan result = new CargoVan();
        for (int i = 0; i < CargoVan.VAN_LENGTH; i++) {
            if (summedCargoItemSubsets.isEmpty()) {
                break;
            }
            CargoVan.CargoVanLine line = new CargoVan.CargoVanLine();
            List<CargoItem> subset = summedCargoItemSubsets.pollFirst();
            int index = 0;
            for (CargoItem cargoItem : subset) {
                line.addCargoItem(cargoItem, index);
                index += cargoItem.getLength();
            }
            result.addLine(line);
        }
        return result;
    }

    private void removeAddedCargoItems(List<List<CargoItem>> summedCargoItemSubsets) {
        for (List<CargoItem> subset : summedCargoItemSubsets) {
            for (CargoItem cargoItem : subset) {
                cargoItems.remove(cargoItem);
            }
        }
    }

    private boolean checkIfCargoItemsFromSubsetNotDuplicated(List<CargoItem> newSubset) {
        Map<String, Integer> cargoItems = new HashMap<>();
        for (CargoItem cargoItem : this.cargoItemsOriginal) {
            cargoItems.put(cargoItem.getName(), cargoItems.getOrDefault(cargoItem.getName(), 0) + 1);
        }

        Map<String, Integer> summedCargoItemSubsets = new HashMap<>();
        for (CargoItem cargoItem : this.summedCargoItemSubsets.stream()
                .flatMap(Collection::stream)
                .toList()) {
            summedCargoItemSubsets.put(cargoItem.getName(), summedCargoItemSubsets.getOrDefault(cargoItem.getName(), 0) + 1);
        }

        for (CargoItem cargoItem : newSubset) {
            if (summedCargoItemSubsets.containsKey(cargoItem.getName()) &&
                    cargoItems.get(cargoItem.getName()) - summedCargoItemSubsets.get(cargoItem.getName()) < newSubset.stream()
                            .filter(cargoItem1 -> Objects.equals(cargoItem1.getName(), cargoItem.getName()))
                            .count()
            ) {
                return false;
            }
        }
        return true;
    }

    private void getAllSubsets(List<CargoItem> cargoItems, int length, int sum) {
        if (length == 0 || sum < 0)
            return;

        dp = new boolean[length][sum + 1];
        for (int i = 0; i < length; ++i) {
            dp[i][0] = true;
        }

        if (cargoItems.get(0).getLength() <= sum) {
            dp[0][cargoItems.get(0).getLength()] = true;
        }

        for (int i = 1; i < length; ++i) {
            for (int j = 0; j < sum + 1; ++j) {
                dp[i][j] = (cargoItems.get(i).getLength() <= j) ? (dp[i - 1][j] || dp[i - 1][j - cargoItems.get(i).getLength()]) : dp[i - 1][j];
            }
        }

        if (!dp[length - 1][sum]) {
            return;
        }

        List<CargoItem> p = new ArrayList<>();
        findAllSubsets(cargoItems, length - 1, sum, p);
    }

    private void findAllSubsets(List<CargoItem> cargoItems, int i, int sum, List<CargoItem> summedLengthItems) {
        if (i == 0 && sum != 0 && dp[0][sum]) {
            summedLengthItems.add(cargoItems.get(i));
            if (checkIfCargoItemsFromSubsetNotDuplicated(summedLengthItems)) {
                summedCargoItemSubsets.add(new ArrayList<>(summedLengthItems));
            }
            summedLengthItems.clear();
            return;
        }

        if (i == 0 && sum == 0) {
            if (checkIfCargoItemsFromSubsetNotDuplicated(summedLengthItems)) {
                summedCargoItemSubsets.add(new ArrayList<>(summedLengthItems));
            }
            summedLengthItems.clear();
            return;
        }

        if (dp[i - 1][sum]) {
            List<CargoItem> b = new ArrayList<>(summedLengthItems);
            findAllSubsets(cargoItems, i - 1, sum, b);
        }

        if (sum >= cargoItems.get(i).getLength() && dp[i - 1][sum - cargoItems.get(i).getLength()]) {
            summedLengthItems.add(cargoItems.get(i));
            findAllSubsets(cargoItems, i - 1, sum - cargoItems.get(i).getLength(), summedLengthItems);
        }
    }
}
