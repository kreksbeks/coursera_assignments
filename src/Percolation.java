public class Percolation {
   private static final int FULL_MASK = 1 << 1;
   private static final int CONNECTED_WITH_MASK = 1 << 2;
   private static final int OPEN_MASK = 1;
   private static final int TOP_ROW_INDEX = 1;

   private final int gridSize;
   private final int[] openSites;
   private final WeightedQuickUnionUF grid;
   private int virtualTopIndex = 0;

   public Percolation(int N) {
      gridSize = N;
      int numberOfSites = gridSize * gridSize + 2; // grid size + virtual top and bottom

      grid = new WeightedQuickUnionUF(numberOfSites);
      openSites = new int[numberOfSites];
      setOpenFlag(virtualTopIndex);
      openSites[virtualTopIndex] |= FULL_MASK;
   }

   public void open(int i, int j) {
      validateIndex(i, j);

      int idnex1D = to1DIndex(i, j);
      setOpenFlag(idnex1D);

      int rootIndex = grid.find(idnex1D);
      if (i == TOP_ROW_INDEX) {
         setFullFlag(rootIndex);
      }
      if (i == gridSize) {
         setBottomFlag(rootIndex);
      }

      connect(idnex1D, to1DIndex(i - 1, j)); //connect top

      if (i < gridSize) {
         connect(idnex1D, to1DIndex(i + 1, j)); //connect bottom
      }
      if (j > 1) {
         connect(idnex1D, to1DIndex(i, j - 1)); //connect left
      }
      if (j < gridSize) {
         connect(idnex1D, to1DIndex(i, j + 1)); //connect right
      }
   }

   private void connect(int i, int other) {
      if (!isOpenInner(openSites[other])) return;

      int iRoot = grid.find(i);
      int otherRootIndex = grid.find(other);

      boolean setFullFlag = isConnectedWithTop(iRoot)
            || isConnectedWithTop(otherRootIndex);
      boolean setBottomFlag = isConnectedWithBottom(iRoot)
            || isConnectedWithBottom(otherRootIndex);

      grid.union(i, other);

      int newRoot = grid.find(i);
      if (setFullFlag) {
         setFullFlag(newRoot);
      }
      if (setBottomFlag) {
         setBottomFlag(newRoot);
      }
   }

   public boolean isOpen(int i, int j) {
      validateIndex(i, j);

      return isOpenInner(openSites[to1DIndex(i, j)]);
   }

   private boolean isOpenInner(int state) {
      return (state & OPEN_MASK) == 1;
   }

   public boolean isFull(int i, int j) {
      validateIndex(i, j);

      int index = to1DIndex(i, j);
      int rootIndex = grid.find(index);
      return isConnectedWithTop(rootIndex);
   }

   public boolean percolates() {
      int topComponentRoot = grid.find(virtualTopIndex);

      return isConnectedWithTop(topComponentRoot)
               && isConnectedWithBottom(topComponentRoot);
   }

   private void validateIndex(int i, int j) {
      if (i < 1 || j < 1 || i > gridSize || j > gridSize) {
         throw new IndexOutOfBoundsException("Index should be within range [1, N]");
      }
   }

   private int to1DIndex(int i, int j) {
      if (i < virtualTopIndex || i > gridSize) {
         throw new IndexOutOfBoundsException("Invalid row index: " + i);
      }

      return (i == virtualTopIndex)
                     ? virtualTopIndex
                     : (i - 1) * gridSize + j;
   }

   private boolean isConnectedWithTop(int index1D) {
      return (openSites[index1D] & FULL_MASK) != 0;
   }

   private boolean isConnectedWithBottom(int index1D) {
      return (openSites[index1D] & CONNECTED_WITH_MASK) != 0;
   }

   private void setFullFlag(int index1D) {
      int previousState = openSites[index1D];
      openSites[index1D] = (previousState | FULL_MASK);
   }

   private void setBottomFlag(int index1D) {
      int previousState = openSites[index1D];
      openSites[index1D] = (previousState | CONNECTED_WITH_MASK);
   }

   private void setOpenFlag(int siteToOpen) {
      openSites[siteToOpen] |= OPEN_MASK;
   }
}
