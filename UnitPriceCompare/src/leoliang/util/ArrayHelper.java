package leoliang.util;

public class ArrayHelper {

    /**
     * Get the index of the array item which is maximum.
     * 
     * @param <T>
     * @param array
     * @return index of the maximum
     */
    public static <T extends Comparable<T>> int getIndexOfMax(T[] array) {
        int indexOfMax = -1;
        T max = array[0];
        for (int i = 0; i < array.length; i++) {
            if (array[i].compareTo(max) > 0) {
                indexOfMax = i;
                max = array[i];
            }
        }
        return indexOfMax;
    }

    /**
     * Get the index of the array item which is maximum.
     * 
     * @param array
     * @return index of the maximum
     */
    public static int getIndexOfMax(int[] array) {
        if (array.length <= 1) {
            return 1;
        }
        int indexOfMax = 0;
        int max = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] > max) {
                indexOfMax = i;
                max = array[i];
            }
        }
        return indexOfMax;
    }

}
