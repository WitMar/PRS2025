package structuredConcurrency;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.StructuredTaskScope;

public class StructuredTournamentMax {

    public static void main(String[] args) throws InterruptedException {
        int[] numbers = {8, 3, 5, 1, 9, 4, 6, 2, 7};

        System.out.println("Tablica: " + Arrays.toString(numbers));

        int maximum = tournamentMax(numbers);

        System.out.println("Największy element: " + maximum);
    }

    public static int tournamentMax(int[] array) throws InterruptedException {
        List<Integer> currentRound = new ArrayList<>();
        for (int num : array) {
            currentRound.add(num);
        }
        return tournamentRound(currentRound);
    }

    private static int tournamentRound(List<Integer> elements) throws InterruptedException {
        if (elements.size() == 1) {
            return elements.get(0); // mamy zwycięzcę
        }

        List<Integer> winners = new ArrayList<>();

        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            List<StructuredTaskScope.Subtask<Integer>> tasks = new ArrayList<>();

            for (int i = 0; i < elements.size(); i += 2) {
                int first = elements.get(i);
                int second = (i + 1 < elements.size()) ? elements.get(i + 1) : Integer.MIN_VALUE;

                var task = scope.fork(() -> {
                    int winner = Math.max(first, second);
                    System.out.println("Porównanie: " + first + " vs " + second + " -> zwycięzca: " + winner + " w " + Thread.currentThread());
                    return winner;
                });
                tasks.add(task);
            }

            scope.join();
            scope.throwIfFailed();

            for (var task : tasks) {
                winners.add(task.get());
            }
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Runda zakończona. Zwycięzcy rundy: " + winners);

        // REKURENCYJNIE kolejna runda
        return tournamentRound(winners);
    }
}
