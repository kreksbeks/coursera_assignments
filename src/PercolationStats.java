public class PercolationStats {
   private final int T;
   private final int N;
   private double[] thresholds;
   private int experimentNo = -1;

   public PercolationStats(int N, int T) {
      if (N <= 0 || T <= 0) {
         throw new IllegalArgumentException(
               "Grid size (N) and number of experiments (T) should be > 0 ");
      }

      this.N = N;
      this.T = T;
      thresholds = new double[T];

      //run experiments
      for (int i = 0; i < T; i++) {
         performExperiment(new Percolation(N));
      }
   }

   public double mean() {
      if (experimentNo == -1) {
         return 0;
      }

      return StdStats.mean(thresholds, 0, experimentNo);
   }

   public double stddev() {
      if (experimentNo == -1) {
         return 0;
      }

      return StdStats.stddev(thresholds, 0, experimentNo);
   }

   public double confidenceLo() {
      return mean() - 1.96 * stddev() / Math.sqrt(T);
   }

   public double confidenceHi() {
      return mean() + 1.96 * stddev() / Math.sqrt(T);
   }

   public static void main(String[] args) {
      int N = StdIn.readInt();
      int T = StdIn.readInt();
      PercolationStats stats = new PercolationStats(N, T);

      StdOut.printf("%-24s%1s%-17f%n", "mean", " = ", stats.mean());
      StdOut.printf("%-24s%1s%-17f%n", "stddev", " = ", stats.stddev());
      StdOut.printf("%-24s%1s%-17f%-17f%n", "95% confidence interval", " = ",
            stats.confidenceLo(), stats.confidenceHi());

   }

   private void performExperiment(Percolation percolation) {
      validateNumberOfExperiments();

      experimentNo++;

      double openSiteCounter = 0;
      for (; !percolation.percolates(); ) {
         int i = randomIndex();
         int j = randomIndex();
         if (!percolation.isOpen(i, j)) {
            percolation.open(i, j);
            openSiteCounter++;
         }
      }

      thresholds[experimentNo] = (openSiteCounter / (N * N));
   }

   private int randomIndex() {
      return (1 + (int)(StdRandom.random() * (N - 1 + 1)));
   }

   private void validateNumberOfExperiments() {
      if (experimentNo >= T) {
         throw new IllegalStateException("Max number of experiments exceeded." +
               " T = " + T);
      }
   }
}
